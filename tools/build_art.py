#!/usr/bin/env python3
"""Génère les modèles Blockbench, les textures, les sons et les rendus Modrinth.

Les fichiers .bbmodel sont la source. Ce script les écrit, peint les UV en
box-UV Minecraft, importe la hiérarchie dans les classes Java, puis encode
les sons finaux et les vignettes de la fiche.
"""

from __future__ import annotations

import base64
import json
import math
import struct
import subprocess
import uuid
import zlib
from pathlib import Path

import numpy as np

ROOT = Path(__file__).resolve().parents[1]
TEX = ROOT / "common/src/main/resources/assets/gazesnatchers/textures/entity"
SND = ROOT / "common/src/main/resources/assets/gazesnatchers/sounds"
JAVA = ROOT / "common/src/main/java/dev/gazesnatchers/client/model"
BB = ROOT / "blockbench"
GALLERY = ROOT / "docs/modrinth"
NS = uuid.UUID("6f0c1a3e-9b24-4c1a-8f55-0a6e2d4b7c91")
# 4 texels par pixel modèle. Le box-UV vanilla compte 1 texel par unité ;
# addBox(..., texScale) compresse ce pas pour remplir la texture haute.
TEX_DENSITY = 4


def uid(name: str) -> str:
    return str(uuid.uuid5(NS, name))


def box_uv_rects(u: int, v: int, w: int, h: int, d: int) -> dict[str, tuple[int, int, int, int]]:
    """Dépliage box-UV Mojang : (u, v, largeur, hauteur) par face."""
    return {
        "top": (u + d, v, w, d),
        "bottom": (u + d + w, v, w, d),
        "right": (u, v + d, d, h),
        "front": (u + d, v + d, w, h),
        "left": (u + d + w, v + d, d, h),
        "back": (u + d + w + d, v + d, w, h),
    }


def write_png(path: Path, rgba: np.ndarray) -> None:
    if rgba.shape[2] == 3:
        alpha = np.full(rgba.shape[:2] + (1,), 255, dtype=np.uint8)
        rgba = np.concatenate([rgba, alpha], axis=2)
    height, width, _ = rgba.shape
    raw = b"".join(b"\x00" + rgba[y].tobytes() for y in range(height))
    compressed = zlib.compress(raw, 9)

    def chunk(tag: bytes, data: bytes) -> bytes:
        return struct.pack(">I", len(data)) + tag + data + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF)

    ihdr = struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0)
    png = b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", ihdr) + chunk(b"IDAT", compressed) + chunk(b"IEND", b"")
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(png)


FACE_SHADES = {
    "flesh": {
        "front": (58, 54, 68),
        "back": (34, 32, 40),
        "left": (42, 40, 50),
        "right": (40, 38, 48),
        "top": (72, 68, 82),
        "bottom": (24, 22, 28),
    },
    "brow": {
        "front": (32, 28, 38),
        "back": (22, 18, 26),
        "left": (28, 24, 34),
        "right": (28, 24, 34),
        "top": (48, 42, 56),
        "bottom": (16, 14, 20),
    },
    "horn": {
        "front": (186, 168, 136),
        "back": (112, 98, 76),
        "left": (156, 140, 112),
        "right": (148, 132, 104),
        "top": (224, 208, 170),
        "bottom": (86, 74, 58),
    },
    "spike": {
        "front": (168, 152, 122),
        "back": (96, 84, 66),
        "left": (140, 124, 98),
        "right": (132, 116, 92),
        "top": (206, 188, 150),
        "bottom": (72, 62, 48),
    },
    "tooth": {
        "front": (236, 230, 214),
        "back": (196, 188, 170),
        "left": (220, 212, 196),
        "right": (214, 206, 190),
        "top": (248, 244, 232),
        "bottom": (170, 162, 146),
    },
    "rib": {
        "front": (196, 184, 162),
        "back": (120, 110, 94),
        "left": (168, 156, 136),
        "right": (160, 148, 128),
        "top": (224, 210, 186),
        "bottom": (96, 86, 72),
    },
    "finger": {
        "front": (46, 40, 52),
        "back": (24, 20, 28),
        "left": (36, 32, 42),
        "right": (34, 30, 40),
        "top": (64, 56, 72),
        "bottom": (18, 14, 20),
    },
    "palm": {
        "front": (40, 34, 46),
        "back": (22, 18, 26),
        "left": (32, 28, 38),
        "right": (30, 26, 36),
        "top": (56, 48, 64),
        "bottom": (16, 12, 18),
    },
    "foot": {
        "front": (44, 40, 50),
        "back": (26, 24, 32),
        "left": (36, 32, 42),
        "right": (34, 30, 40),
        "top": (62, 56, 70),
        "bottom": (18, 16, 22),
    },
    "collar": {
        "front": (22, 18, 28),
        "back": (12, 10, 16),
        "left": (18, 14, 22),
        "right": (16, 12, 20),
        "top": (36, 30, 44),
        "bottom": (8, 6, 12),
    },
    "tail": {
        "front": (48, 44, 58),
        "back": (28, 26, 34),
        "left": (38, 34, 46),
        "right": (36, 32, 44),
        "top": (64, 58, 74),
        "bottom": (18, 16, 22),
    },
}


def upsample_noise(rng: np.random.Generator, size: int, cells: int) -> np.ndarray:
    """Bruit bilinéaire : les texels voisins varient ensemble, pas un à un."""
    cells = max(2, cells)
    coarse = rng.random((cells, cells)).astype(np.float32)
    gy = np.linspace(0, cells - 1, size)
    gx = np.linspace(0, cells - 1, size)
    y0 = np.floor(gy).astype(np.int32)
    x0 = np.floor(gx).astype(np.int32)
    y1 = np.minimum(y0 + 1, cells - 1)
    x1 = np.minimum(x0 + 1, cells - 1)
    fy = (gy - y0).astype(np.float32)[:, None]
    fx = (gx - x0).astype(np.float32)[None, :]
    n00 = coarse[y0][:, x0]
    n10 = coarse[y0][:, x1]
    n01 = coarse[y1][:, x0]
    n11 = coarse[y1][:, x1]
    return n00 * (1 - fx) * (1 - fy) + n10 * fx * (1 - fy) + n01 * (1 - fx) * fy + n11 * fx * fy


def scale_rect(rect: tuple[int, int, int, int]) -> tuple[int, int, int, int]:
    u, v, w, h = rect
    return u * TEX_DENSITY, v * TEX_DENSITY, w * TEX_DENSITY, h * TEX_DENSITY


def paint_slit(img, glow, u, v, w, h, light, dark) -> None:
    if w < 2 or h < 2:
        return
    xs = np.arange(w, dtype=np.float32)[None, :]
    center = (w - 1) / 2
    slit = np.clip(1.0 - np.abs(xs - center) / max(w * 0.16, 1.5), 0, 1)
    slit = slit * slit
    base = img[v : v + h, u : u + w, :3].astype(np.float32)
    img[v : v + h, u : u + w, :3] = np.clip(base * (1 - slit[..., None]) + np.array(dark) * slit[..., None], 0, 255).astype(np.uint8)
    if glow is not None:
        glow[v : v + h, u : u + w, 0] = light[0]
        glow[v : v + h, u : u + w, 1] = light[1]
        glow[v : v + h, u : u + w, 2] = light[2]
        glow[v : v + h, u : u + w, 3] = np.clip(255 * (1 - slit), 0, 255).astype(np.uint8)


def paint_texture(size: int, cubes: list[dict], eyes: list[dict], seed: int) -> tuple[np.ndarray, np.ndarray]:
    rng = np.random.default_rng(seed)
    img = np.zeros((size, size, 4), dtype=np.uint8)
    glow = np.zeros((size, size, 4), dtype=np.uint8)
    broad = upsample_noise(rng, size, max(6, size // 48))
    fine = upsample_noise(rng, size, max(12, size // 14))
    noise = np.clip(0.82 * broad + 0.18 * fine, 0, 1)
    vein = upsample_noise(rng, size, max(8, size // 18))

    def fill(rect, shade, vein_strength=0.35):
        u, v, w, h = scale_rect(rect)
        if w <= 0 or h <= 0:
            return
        patch = noise[v : v + h, u : u + w]
        veins = vein[v : v + h, u : u + w]
        vein_mask = np.clip((veins - 0.64) * 5.0, 0, 1) ** 2
        base = np.array(shade, dtype=np.float32)
        color = base * (0.9 + 0.16 * patch)[..., None]
        color *= 1.0 - vein_strength * 0.5 * vein_mask[..., None]
        color = np.clip(color, 0, 255).astype(np.uint8)
        img[v : v + h, u : u + w, :3] = color
        img[v : v + h, u : u + w, 3] = 255

    for cube in cubes:
        style = cube.get("style", "flesh")
        rects = box_uv_rects(cube["uv"][0], cube["uv"][1], cube["w"], cube["h"], cube["d"])
        if style == "glow":
            for face, rect in rects.items():
                u, v, w, h = scale_rect(rect)
                img[v : v + h, u : u + w, :3] = (236, 244, 255)
                img[v : v + h, u : u + w, 3] = 255
                glow[v : v + h, u : u + w] = (255, 255, 255, 255)
                if face == "front":
                    paint_slit(img, glow, u, v, w, h, (255, 255, 255), (16, 10, 22))
            continue
        shades = FACE_SHADES.get(style, FACE_SHADES["flesh"])
        if style in ("tooth", "horn", "spike", "rib"):
            vein_strength = 0.12
        elif style in ("finger", "palm", "foot", "tail") or cube.get("limb"):
            vein_strength = 0.35
        elif style == "collar":
            vein_strength = 0.5
        else:
            vein_strength = 0.28
        for face, rect in rects.items():
            fill(rect, shades[face], vein_strength)

    for eye in eyes:
        u, v, w, h = scale_rect(eye["rect"])
        face = img[v : v + h, u : u + w, :3].astype(np.float32) * 0.78
        img[v : v + h, u : u + w, :3] = np.clip(face, 0, 255).astype(np.uint8)
        for socket in eye.get("sockets", []):
            su, sv, sw, sh = scale_rect(socket)
            img[sv : sv + sh, su : su + sw, :3] = (8, 6, 12)
            img[sv : sv + sh, su : su + sw, 3] = 255
        for ex, ey, ew, eh in eye.get("eyes", []):
            eu, ev, ew, eh = scale_rect((ex, ey, ew, eh))
            img[ev : ev + eh, eu : eu + ew, :3] = (244, 248, 255)
            glow[ev : ev + eh, eu : eu + ew] = (255, 255, 255, 255)
            paint_slit(img, glow, eu, ev, ew, eh, (255, 255, 255), (20, 16, 28))
        mx, my, mw, mh = scale_rect(eye["mouth"])
        img[my : my + mh, mx : mx + mw, :3] = (8, 6, 10)
    return img, glow


def mc_offset(pivot):
    # Blockbench Y-up, pieds à y=0 → espace modèle Mojang (y positif vers le bas, pieds à 24).
    return (pivot[0], 24.0 - pivot[1], pivot[2])


def mc_box(pivot, frm, to):
    size = [to[i] - frm[i] for i in range(3)]
    x = frm[0] - pivot[0]
    y = pivot[1] - to[1]
    z = frm[2] - pivot[2]
    return x, y, z, size[0], size[1], size[2]


def fnum(value: float) -> str:
    text = f"{value:.4f}".rstrip("0").rstrip(".")
    if "." not in text:
        text += ".0"
    return text + "F"


def cube_java(var: str, name: str, pivot, frm, to, uv) -> str:
    x, y, z, w, h, d = mc_box(pivot, frm, to)
    ox, oy, oz = mc_offset(pivot)
    return (
        f'        PartDefinition {var} = parent.addOrReplaceChild("{name}", '
        f"CubeListBuilder.create().texOffs({uv[0]}, {uv[1]})"
        f".addBox({fnum(x)}, {fnum(y)}, {fnum(z)}, {fnum(w)}, {fnum(h)}, {fnum(d)}, "
        f"new CubeDeformation(0.0F), {fnum(1 / TEX_DENSITY)}, {fnum(1 / TEX_DENSITY)}), "
        f"PartPose.offset({fnum(ox)}, {fnum(oy)}, {fnum(oz)}));"
    )


def child_java(var: str, parent_var: str, name: str, parent_pivot, pivot, frm, to, uv) -> str:
    x, y, z, w, h, d = mc_box(pivot, frm, to)
    ox, oy, oz = mc_offset(pivot)
    px, py, pz = mc_offset(parent_pivot)
    return (
        f'        PartDefinition {var} = {parent_var}.addOrReplaceChild("{name}", '
        f"CubeListBuilder.create().texOffs({uv[0]}, {uv[1]})"
        f".addBox({fnum(x)}, {fnum(y)}, {fnum(z)}, {fnum(w)}, {fnum(h)}, {fnum(d)}, "
        f"new CubeDeformation(0.0F), {fnum(1 / TEX_DENSITY)}, {fnum(1 / TEX_DENSITY)}), "
        f"PartPose.offset({fnum(ox - px)}, {fnum(oy - py)}, {fnum(oz - pz)}));"
    )


def java_name(bone: str) -> str:
    return bone[0].lower() + bone[1:]


def append_children(lines: list[str], spec: dict, parent_vars: dict, pivots: dict) -> None:
    seen = set(parent_vars)
    for child in spec["children"]:
        if child["bone"] in seen:
            raise SystemExit(f"os en double : {child['bone']}")
        seen.add(child["bone"])
        var = java_name(child["bone"])
        lines.append(
            child_java(
                var,
                parent_vars[child["parent"]],
                child["bone"],
                pivots[child["parent"]],
                child["pivot"],
                child["from"],
                child["to"],
                child["uv"],
            )
        )
        parent_vars[child["bone"]] = var
        pivots[child["bone"]] = child["pivot"]


ADULT = {
    "name": "gaze_snatcher",
    "tex": 128,
    "cubes": [
        {"bone": "Head", "pivot": (0, 34, 0), "from": (-6, 34, -6), "to": (6, 46, 6), "uv": (0, 0)},
        {"bone": "Body", "pivot": (0, 34, 0), "from": (-3, 14, -2), "to": (3, 34, 2), "uv": (0, 28)},
        {"bone": "LeftArm", "pivot": (4.5, 32, 0), "from": (3, 6, -1.5), "to": (6, 32, 1.5), "uv": (24, 28), "limb": True},
        {"bone": "RightArm", "pivot": (-4.5, 32, 0), "from": (-6, 6, -1.5), "to": (-3, 32, 1.5), "uv": (40, 28), "limb": True},
        {"bone": "LeftLeg", "pivot": (2, 22, 0), "from": (0.5, 0, -1.5), "to": (3.5, 22, 1.5), "uv": (56, 28), "limb": True},
        {"bone": "RightLeg", "pivot": (-2, 22, 0), "from": (-3.5, 0, -1.5), "to": (-0.5, 22, 1.5), "uv": (72, 28), "limb": True},
    ],
    "children": [
        {"bone": "Jaw", "parent": "Head", "pivot": (0, 37, -6), "from": (-4, 34, -7), "to": (4, 37, -5), "uv": (0, 64)},
        {"bone": "ToothL", "parent": "Jaw", "pivot": (0, 37, -6), "from": (-4, 31, -8), "to": (-2, 34, -6), "uv": (48, 70), "style": "tooth"},
        {"bone": "ToothR", "parent": "Jaw", "pivot": (0, 37, -6), "from": (2, 31, -8), "to": (4, 34, -6), "uv": (58, 70), "style": "tooth"},
        {"bone": "ToothM", "parent": "Jaw", "pivot": (0, 37, -6), "from": (-1, 30, -8), "to": (1, 34, -6), "uv": (68, 70), "style": "tooth"},
        {"bone": "Brow", "parent": "Head", "pivot": (0, 34, 0), "from": (-5, 42, -9), "to": (5, 44, -5), "uv": (0, 70), "style": "brow"},
        {"bone": "HornL", "parent": "Head", "pivot": (0, 34, 0), "from": (2, 46, -2), "to": (4, 49, 0), "uv": (28, 70), "style": "horn"},
        {"bone": "HornR", "parent": "Head", "pivot": (0, 34, 0), "from": (-4, 46, -2), "to": (-2, 49, 0), "uv": (38, 70), "style": "horn"},
        {"bone": "HornLTip", "parent": "Head", "pivot": (0, 34, 0), "from": (2, 49, -1), "to": (3, 53, 0), "uv": (28, 76), "style": "horn"},
        {"bone": "HornRTip", "parent": "Head", "pivot": (0, 34, 0), "from": (-3, 49, -1), "to": (-2, 53, 0), "uv": (38, 76), "style": "horn"},
        {"bone": "EyeL", "parent": "Head", "pivot": (0, 34, 0), "from": (1, 39, -10), "to": (4, 43, -6), "uv": (88, 0), "style": "glow"},
        {"bone": "EyeR", "parent": "Head", "pivot": (0, 34, 0), "from": (-4, 39, -10), "to": (-1, 43, -6), "uv": (104, 0), "style": "glow"},
        {"bone": "Collar", "parent": "Body", "pivot": (0, 34, 0), "from": (-4, 33, -3), "to": (4, 35, 2), "uv": (22, 90), "style": "collar"},
        {"bone": "Rib1", "parent": "Body", "pivot": (0, 34, 0), "from": (-4, 27, -4), "to": (4, 29, -2), "uv": (0, 82), "style": "rib"},
        {"bone": "Rib2", "parent": "Body", "pivot": (0, 34, 0), "from": (-4, 23, -4), "to": (4, 25, -2), "uv": (0, 88), "style": "rib"},
        {"bone": "Rib3", "parent": "Body", "pivot": (0, 34, 0), "from": (-4, 19, -4), "to": (4, 21, -2), "uv": (0, 94), "style": "rib"},
        {"bone": "Spike1", "parent": "Body", "pivot": (0, 34, 0), "from": (-1, 30, 2), "to": (1, 34, 4), "uv": (70, 78), "style": "spike"},
        {"bone": "Spike2", "parent": "Body", "pivot": (0, 34, 0), "from": (-1, 24, 2), "to": (1, 28, 4), "uv": (80, 78), "style": "spike"},
        {"bone": "Spike3", "parent": "Body", "pivot": (0, 34, 0), "from": (-1, 18, 2), "to": (1, 22, 4), "uv": (90, 78), "style": "spike"},
        {"bone": "PalmL", "parent": "LeftArm", "pivot": (4.5, 32, 0), "from": (2, 4, -3), "to": (7, 7, -1), "uv": (88, 28), "style": "palm"},
        {"bone": "PalmR", "parent": "RightArm", "pivot": (-4.5, 32, 0), "from": (-7, 4, -3), "to": (-2, 7, -1), "uv": (104, 28), "style": "palm"},
        {"bone": "FingerL1", "parent": "LeftArm", "pivot": (4.5, 32, 0), "from": (2, 1, -5), "to": (3, 6, -2), "uv": (0, 100), "style": "finger"},
        {"bone": "FingerL2", "parent": "LeftArm", "pivot": (4.5, 32, 0), "from": (4, 0, -7), "to": (5, 5, -4), "uv": (10, 100), "style": "finger"},
        {"bone": "FingerL3", "parent": "LeftArm", "pivot": (4.5, 32, 0), "from": (6, 1, -5), "to": (7, 6, -2), "uv": (20, 100), "style": "finger"},
        {"bone": "FingerR1", "parent": "RightArm", "pivot": (-4.5, 32, 0), "from": (-3, 1, -5), "to": (-2, 6, -2), "uv": (30, 100), "style": "finger"},
        {"bone": "FingerR2", "parent": "RightArm", "pivot": (-4.5, 32, 0), "from": (-5, 0, -7), "to": (-4, 5, -4), "uv": (40, 100), "style": "finger"},
        {"bone": "FingerR3", "parent": "RightArm", "pivot": (-4.5, 32, 0), "from": (-7, 1, -5), "to": (-6, 6, -2), "uv": (50, 100), "style": "finger"},
        {"bone": "ClawL1", "parent": "LeftArm", "pivot": (4.5, 32, 0), "from": (2, 0, -7), "to": (3, 2, -5), "uv": (0, 112), "style": "tooth"},
        {"bone": "ClawL2", "parent": "LeftArm", "pivot": (4.5, 32, 0), "from": (4, 0, -9), "to": (5, 2, -7), "uv": (8, 112), "style": "tooth"},
        {"bone": "ClawL3", "parent": "LeftArm", "pivot": (4.5, 32, 0), "from": (6, 0, -7), "to": (7, 2, -5), "uv": (16, 112), "style": "tooth"},
        {"bone": "ClawR1", "parent": "RightArm", "pivot": (-4.5, 32, 0), "from": (-3, 0, -7), "to": (-2, 2, -5), "uv": (24, 112), "style": "tooth"},
        {"bone": "ClawR2", "parent": "RightArm", "pivot": (-4.5, 32, 0), "from": (-5, 0, -9), "to": (-4, 2, -7), "uv": (32, 112), "style": "tooth"},
        {"bone": "ClawR3", "parent": "RightArm", "pivot": (-4.5, 32, 0), "from": (-7, 0, -7), "to": (-6, 2, -5), "uv": (40, 112), "style": "tooth"},
        {"bone": "FootL", "parent": "LeftLeg", "pivot": (2, 22, 0), "from": (0, 0, -4), "to": (4, 2, -1), "uv": (56, 64), "style": "foot"},
        {"bone": "FootR", "parent": "RightLeg", "pivot": (-2, 22, 0), "from": (-4, 0, -4), "to": (0, 2, -1), "uv": (72, 64), "style": "foot"},
    ],
}

BABY = {
    "name": "baby_gaze_snatcher",
    "tex": 64,
    "cubes": [
        {"bone": "Head", "pivot": (0, 5, -1), "from": (-3, 4, -5), "to": (3, 10, 1), "uv": (0, 0)},
        {"bone": "Body", "pivot": (0, 5, 0), "from": (-2.5, 2, -3), "to": (2.5, 5, 3), "uv": (0, 16)},
        {"bone": "Left_Foreleg", "pivot": (2, 3, -2), "from": (1, 0, -3), "to": (3, 3, -1), "uv": (28, 0), "limb": True},
        {"bone": "Right_Foreleg", "pivot": (-2, 3, -2), "from": (-3, 0, -3), "to": (-1, 3, -1), "uv": (36, 0), "limb": True},
        {"bone": "Left_Hindleg", "pivot": (2, 3, 2), "from": (1, 0, 1), "to": (3, 3, 3), "uv": (44, 0), "limb": True},
        {"bone": "Right_Hindleg", "pivot": (-2, 3, 2), "from": (-3, 0, 1), "to": (-1, 3, 3), "uv": (52, 0), "limb": True},
    ],
    "children": [
        {"bone": "Jaw", "parent": "Head", "pivot": (0, 6, -5), "from": (-2, 4, -6), "to": (2, 6, -4), "uv": (0, 40)},
        {"bone": "ToothL", "parent": "Jaw", "pivot": (0, 6, -5), "from": (-2, 3, -6), "to": (-1, 4, -5), "uv": (16, 40), "style": "tooth"},
        {"bone": "ToothR", "parent": "Jaw", "pivot": (0, 6, -5), "from": (1, 3, -6), "to": (2, 4, -5), "uv": (22, 40), "style": "tooth"},
        {"bone": "EyeL", "parent": "Head", "pivot": (0, 5, -1), "from": (0, 7, -8), "to": (2, 9, -5), "uv": (40, 18), "style": "glow"},
        {"bone": "EyeR", "parent": "Head", "pivot": (0, 5, -1), "from": (-2, 7, -8), "to": (0, 9, -5), "uv": (52, 18), "style": "glow"},
        {"bone": "HornL", "parent": "Head", "pivot": (0, 5, -1), "from": (1, 10, -3), "to": (2, 14, -2), "uv": (32, 28), "style": "horn"},
        {"bone": "HornR", "parent": "Head", "pivot": (0, 5, -1), "from": (-2, 10, -3), "to": (-1, 14, -2), "uv": (38, 28), "style": "horn"},
        {"bone": "Tail", "parent": "Body", "pivot": (0, 3, 3), "from": (-1, 2, 3), "to": (1, 4, 9), "uv": (0, 28), "style": "tail"},
        {"bone": "TailTip", "parent": "Tail", "pivot": (0, 4, 9), "from": (-1, 3, 9), "to": (1, 6, 12), "uv": (18, 28), "style": "tail"},
        {"bone": "NubL", "parent": "Body", "pivot": (0, 5, 0), "from": (-2, 4, 3), "to": (-1, 6, 4), "uv": (44, 28), "style": "spike"},
        {"bone": "NubR", "parent": "Body", "pivot": (0, 5, 0), "from": (1, 4, 3), "to": (2, 6, 4), "uv": (50, 28), "style": "spike"},
    ],
}


def cube_dims(cube):
    frm, to = cube["from"], cube["to"]
    return {
        **cube,
        "w": int(round(to[0] - frm[0])),
        "h": int(round(to[1] - frm[1])),
        "d": int(round(to[2] - frm[2])),
    }


def adult_eyes(cubes):
    head = next(c for c in cubes if c["bone"] == "Head")
    rect = box_uv_rects(head["uv"][0], head["uv"][1], head["w"], head["h"], head["d"])["front"]
    u, v, w, h = rect
    return [{
        "rect": rect,
        "sockets": [(u + 2, v + 3, 3, 4), (u + 7, v + 3, 3, 4)],
        "mouth": (u + 3, v + 8, 6, 2),
    }]


def baby_eyes(cubes):
    head = next(c for c in cubes if c["bone"] == "Head")
    rect = box_uv_rects(head["uv"][0], head["uv"][1], head["w"], head["h"], head["d"])["front"]
    u, v, w, h = rect
    return [{
        "rect": rect,
        "sockets": [(u + 1, v + 1, 1, 2), (u + 4, v + 1, 1, 2)],
        "mouth": (u + 1, v + 4, 4, 1),
    }]


BB_FACE = {
    "front": "north",
    "back": "south",
    "left": "east",
    "right": "west",
    "top": "up",
    "bottom": "down",
}


def bb_faces(cube) -> dict:
    rects = box_uv_rects(cube["uv"][0], cube["uv"][1], cube["w"], cube["h"], cube["d"])
    faces = {}
    for name, rect in rects.items():
        u, v, w, h = scale_rect(rect)
        faces[BB_FACE[name]] = {"uv": [u, v, u + w, v + h], "texture": 0}
    return faces


def bb_element(cube, texture_uuid: str) -> dict:
    return {
        "name": cube["bone"],
        "box_uv": False,
        "rescale": False,
        "locked": False,
        "from": list(cube["from"]),
        "to": list(cube["to"]),
        "autouv": 0,
        "color": 0,
        "origin": list(cube["pivot"]),
        "uv_offset": list(cube["uv"]),
        "inflate": 0,
        "rotation": [0, 0, 0],
        "faces": bb_faces(cube),
        "type": "cube",
        "uuid": uid(cube.get("id", cube["bone"])),
    }


def build_bbmodel(spec: dict, texture_name: str, png_bytes: bytes) -> dict:
    elements = []
    bones: dict[str, dict] = {}
    order = []
    for cube in spec["cubes"]:
        element = bb_element(cube, "")
        elements.append(element)
        bone = {
            "name": cube["bone"],
            "origin": list(cube["pivot"]),
            "rotation": [0, 0, 0],
            "uuid": uid("bone-" + cube["bone"] + spec["name"]),
            "export": True,
            "children": [element["uuid"]],
        }
        bones[cube["bone"]] = bone
        order.append(cube["bone"])
    for child in spec["children"]:
        tagged = {**child, "id": child["parent"] + "-" + child["bone"]}
        element = bb_element(tagged, "")
        element["name"] = child["bone"]
        elements.append(element)
        node = {
            "name": child["bone"],
            "origin": list(child["pivot"]),
            "rotation": [0, 0, 0],
            "uuid": uid("bone-" + tagged["id"]),
            "export": True,
            "children": [element["uuid"]],
        }
        if child["parent"] not in bones:
            raise SystemExit(f"os parent inconnu : {child['parent']}")
        bones[child["parent"]]["children"].append(node)
        bones[child["bone"]] = node

    root = {
        "name": "Root",
        "origin": [0, 0, 0],
        "rotation": [0, 0, 0],
        "uuid": uid("root-" + spec["name"]),
        "export": True,
        "children": [bones[name] for name in order],
    }
    head = uid("bone-Head" + spec["name"])
    body = uid("bone-Body" + spec["name"])
    animations = [
        {
            "uuid": uid("anim-idle-" + spec["name"]),
            "name": "idle",
            "loop": "loop",
            "override": False,
            "length": 4,
            "snapping": 24,
            "selected": False,
            "anim_time_update": "",
            "blend_weight": "",
            "start_delay": "",
            "loop_delay": "",
            "animators": {
                body: {
                    "name": "Body",
                    "type": "bone",
                    "keyframes": [
                        {"channel": "rotation", "data_points": [{"x": "12", "y": "0", "z": "0"}], "time": 0, "interpolation": "catmullrom"},
                        {"channel": "rotation", "data_points": [{"x": "16", "y": "0", "z": "0"}], "time": 2, "interpolation": "catmullrom"},
                        {"channel": "rotation", "data_points": [{"x": "12", "y": "0", "z": "0"}], "time": 4, "interpolation": "catmullrom"},
                    ],
                }
            },
        },
        {
            "uuid": uid("anim-walk-" + spec["name"]),
            "name": "walk",
            "loop": "loop",
            "override": False,
            "length": 1,
            "snapping": 24,
            "selected": False,
            "anim_time_update": "",
            "blend_weight": "",
            "start_delay": "",
            "loop_delay": "",
            "animators": {
                head: {
                    "name": "Head",
                    "type": "bone",
                    "keyframes": [
                        {"channel": "rotation", "data_points": [{"x": "0", "y": "0", "z": "0"}], "time": 0, "interpolation": "linear"},
                    ],
                }
            },
        },
    ]
    return {
        "meta": {
            "format_version": "4.10",
            "model_format": "modded_entity",
            "box_uv": False,
        },
        "name": spec["name"],
        "model_identifier": spec["name"],
        "visible_box": [1, 1, 0],
        "variable_placeholders": "",
        "resolution": {"width": spec["tex"] * TEX_DENSITY, "height": spec["tex"] * TEX_DENSITY},
        "elements": elements,
        "outliner": [root],
        "textures": [
            {
                "path": f"../common/src/main/resources/assets/gazesnatchers/textures/entity/{texture_name}",
                "name": texture_name,
                "folder": "",
                "namespace": "",
                "id": "0",
                "width": spec["tex"] * TEX_DENSITY,
                "height": spec["tex"] * TEX_DENSITY,
                "uv_width": spec["tex"] * TEX_DENSITY,
                "uv_height": spec["tex"] * TEX_DENSITY,
                "particle": False,
                "use_as_default": True,
                "render_mode": "default",
                "visible": True,
                "internal": True,
                "saved": True,
                "uuid": uid("tex-" + spec["name"]),
                "source": "data:image/png;base64," + base64.b64encode(png_bytes).decode("ascii"),
            }
        ],
        "animations": animations,
    }


def write_java(class_name: str, layer: str, tex: int, body: str, fields: str, ctor: str, anim: str) -> None:
    source = f'''package dev.gazesnatchers.client.model;

import dev.gazesnatchers.GazeSnatchers;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * Import Blockbench ({layer}.bbmodel). texScale 0.25 : 4 texels par pixel modèle.
 * idle et walk reprennent les pistes du fichier.
 */
public class {class_name} extends EntityModel<LivingEntityRenderState> {{
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            GazeSnatchers.id("{layer}"), "main");

{fields}
    public {class_name}(ModelPart root) {{
        super(root);
{ctor}
    }}

    public static LayerDefinition createBodyLayer() {{
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parent = mesh.getRoot();
{body}
        return LayerDefinition.create(mesh, {tex}, {tex});
    }}

    @Override
    public void setupAnim(LivingEntityRenderState state) {{
{anim}
    }}
}}
'''
    (JAVA / f"{class_name}.java").write_text(source)


def adult_java(spec: dict) -> None:
    lines = []
    variables = {
        "Head": "head",
        "Body": "body",
        "LeftArm": "leftArm",
        "RightArm": "rightArm",
        "LeftLeg": "leftLeg",
        "RightLeg": "rightLeg",
    }
    pivots = {}
    for cube in spec["cubes"]:
        lines.append(cube_java(variables[cube["bone"]], cube["bone"], cube["pivot"], cube["from"], cube["to"], cube["uv"]))
        pivots[cube["bone"]] = cube["pivot"]
    append_children(lines, spec, variables, pivots)
    fields = """    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart body;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
"""
    ctor = """        this.head = root.getChild("Head");
        this.jaw = this.head.getChild("Jaw");
        this.body = root.getChild("Body");
        this.leftArm = root.getChild("LeftArm");
        this.rightArm = root.getChild("RightArm");
        this.leftLeg = root.getChild("LeftLeg");
        this.rightLeg = root.getChild("RightLeg");
"""
    anim = """        float lookX = state.xRot * Mth.DEG_TO_RAD;
        float lookY = state.yRot * Mth.DEG_TO_RAD;
        float idle = state.ageInTicks;
        float limb = state.walkAnimationPos;
        float swing = state.walkAnimationSpeed;

        this.head.xRot = lookX * 0.65F + Mth.sin(idle * 0.06F) * 0.04F;
        this.head.yRot = lookY;
        this.head.zRot = Mth.sin(idle * 0.04F) * 0.03F;
        this.jaw.xRot = 0.12F + Mth.sin(idle * 0.11F) * 0.07F;
        this.jaw.yRot = 0.0F;
        this.jaw.zRot = 0.0F;

        this.body.xRot = 0.22F + Mth.sin(idle * 0.07F) * 0.04F;
        this.body.yRot = lookY * 0.12F;
        this.body.zRot = 0.0F;

        float arm = Mth.cos(limb * 0.6662F) * 1.05F * swing;
        this.leftArm.xRot = 0.16F + arm;
        this.rightArm.xRot = 0.16F - arm;
        this.leftArm.yRot = 0.0F;
        this.rightArm.yRot = 0.0F;
        this.leftArm.zRot = 0.06F;
        this.rightArm.zRot = -0.06F;

        float leg = Mth.cos(limb * 0.6662F) * 0.85F * swing;
        this.leftLeg.xRot = -leg;
        this.rightLeg.xRot = leg;
        this.leftLeg.yRot = 0.0F;
        this.rightLeg.yRot = 0.0F;
        this.leftLeg.zRot = 0.0F;
        this.rightLeg.zRot = 0.0F;
"""
    write_java("GazeSnatcherModel", "gaze_snatcher", spec["tex"] * TEX_DENSITY, "\n".join(lines), fields, ctor, anim)


def baby_java(spec: dict) -> None:
    lines = []
    variables = {
        "Head": "head",
        "Body": "body",
        "Left_Foreleg": "leftFore",
        "Right_Foreleg": "rightFore",
        "Left_Hindleg": "leftHind",
        "Right_Hindleg": "rightHind",
    }
    pivots = {}
    for cube in spec["cubes"]:
        lines.append(cube_java(variables[cube["bone"]], cube["bone"], cube["pivot"], cube["from"], cube["to"], cube["uv"]))
        pivots[cube["bone"]] = cube["pivot"]
    append_children(lines, spec, variables, pivots)
    fields = """    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart tailTip;
    private final ModelPart leftFore;
    private final ModelPart rightFore;
    private final ModelPart leftHind;
    private final ModelPart rightHind;
"""
    ctor = """        this.head = root.getChild("Head");
        this.jaw = this.head.getChild("Jaw");
        this.body = root.getChild("Body");
        this.tail = this.body.getChild("Tail");
        this.tailTip = this.tail.getChild("TailTip");
        this.leftFore = root.getChild("Left_Foreleg");
        this.rightFore = root.getChild("Right_Foreleg");
        this.leftHind = root.getChild("Left_Hindleg");
        this.rightHind = root.getChild("Right_Hindleg");
"""
    anim = """        float lookX = state.xRot * Mth.DEG_TO_RAD;
        float lookY = state.yRot * Mth.DEG_TO_RAD;
        float idle = state.ageInTicks;
        float limb = state.walkAnimationPos;
        float swing = state.walkAnimationSpeed;

        this.head.xRot = lookX * 0.8F + Mth.sin(idle * 0.2F) * 0.08F;
        this.head.yRot = lookY;
        this.head.zRot = 0.0F;
        this.jaw.xRot = 0.18F + Mth.sin(idle * 0.23F) * 0.1F;
        this.jaw.yRot = 0.0F;
        this.jaw.zRot = 0.0F;
        this.body.xRot = 0.08F;
        this.body.yRot = lookY * 0.2F;
        this.body.zRot = 0.0F;
        this.tail.xRot = -0.35F + Mth.sin(idle * 0.18F) * 0.12F;
        this.tail.yRot = Mth.sin(idle * 0.15F) * 0.35F;
        this.tail.zRot = 0.0F;
        this.tailTip.xRot = -0.45F;
        this.tailTip.yRot = Mth.sin(idle * 0.15F + 0.6F) * 0.25F;
        this.tailTip.zRot = 0.0F;

        float fore = Mth.cos(limb * 0.9F) * 1.3F * swing;
        float hind = Mth.cos(limb * 0.9F + (float) Math.PI) * 1.3F * swing;
        this.leftFore.xRot = fore;
        this.rightFore.xRot = -fore;
        this.leftHind.xRot = hind;
        this.rightHind.xRot = -hind;
        this.leftFore.yRot = this.rightFore.yRot = this.leftHind.yRot = this.rightHind.yRot = 0.0F;
        this.leftFore.zRot = this.rightFore.zRot = this.leftHind.zRot = this.rightHind.zRot = 0.0F;
"""
    write_java("BabyGazeSnatcherModel", "baby_gaze_snatcher", spec["tex"] * TEX_DENSITY, "\n".join(lines), fields, ctor, anim)


def lowpass(samples: np.ndarray, alpha: float) -> np.ndarray:
    out = np.empty_like(samples)
    acc = 0.0
    for i, sample in enumerate(samples):
        acc += alpha * (sample - acc)
        out[i] = acc
    return out


def envelope_fade(n: int, attack: int, release: int) -> np.ndarray:
    env = np.ones(n, dtype=np.float32)
    if attack > 0:
        env[:attack] = np.linspace(0, 1, attack, dtype=np.float32)
    if release > 0:
        env[-release:] *= np.linspace(1, 0, release, dtype=np.float32)
    return env


def synth_sounds() -> None:
    sr = 44100
    rng = np.random.default_rng(20261026)

    def breath(seconds, rate, seed):
        n = int(sr * seconds)
        local = np.random.default_rng(seed)
        noise = lowpass(local.uniform(-1, 1, n).astype(np.float32), 0.08)
        t = np.arange(n) / sr
        amp = (0.5 + 0.5 * np.sin(2 * math.pi * rate * t - 1.2)) ** 2
        return noise * amp.astype(np.float32)

    def cracks(n, count, seed):
        local = np.random.default_rng(seed)
        out = np.zeros(n, dtype=np.float32)
        for _ in range(count):
            start = int(local.integers(0, max(1, n - sr // 20)))
            length = int(local.integers(sr // 80, sr // 25))
            burst = local.uniform(-1, 1, length).astype(np.float32)
            burst *= np.exp(-np.linspace(0, 6, length)).astype(np.float32)
            out[start : start + length] += burst * 0.55
        return out

    def tone(n, freq, slide=1.0):
        t = np.arange(n) / sr
        phase = 2 * math.pi * freq * t * (1 + (slide - 1) * t / max(t[-1], 1e-6))
        return np.sin(phase).astype(np.float32)

    clips = {}
    n = int(sr * 5.0)
    ambient = breath(5.0, 0.22, 1) * 0.55
    ambient += lowpass(rng.uniform(-1, 1, n).astype(np.float32), 0.02) * 0.08
    ambient += tone(n, 92) * (0.04 + 0.03 * np.sin(np.arange(n) / sr * 3).astype(np.float32))
    ambient += cracks(n, 7, 11) * 0.35
    clips["gaze_snatcher_ambient"] = ambient * envelope_fade(n, sr // 10, sr // 10)

    n = int(sr * 1.0)
    pop = np.zeros(n, dtype=np.float32)
    burst_n = sr // 12
    pop[:burst_n] = rng.uniform(-1, 1, burst_n).astype(np.float32) * np.exp(-np.linspace(0, 5, burst_n))
    pop += tone(n, 70, 0.6) * np.exp(-np.linspace(0, 4, n)) * 0.4
    inhale = breath(1.0, 0.5, 3)
    inhale[: sr // 5] *= np.linspace(0, 1, sr // 5)
    clips["gaze_snatcher_spawn"] = np.clip(pop * 0.8 + inhale * 0.45, -1, 1) * envelope_fade(n, 10, sr // 8)

    n = int(sr * 0.6)
    t = np.arange(n) / sr
    pulses = (np.sin(2 * math.pi * 8 * t) > 0.2).astype(np.float32)
    laugh = lowpass(rng.uniform(-1, 1, n).astype(np.float32), 0.2) * pulses
    suck = tone(n, 240, 0.35) * np.linspace(0.2, 0.0, n).astype(np.float32)
    clips["gaze_snatcher_steal"] = (laugh * 0.55 + suck * 0.25) * envelope_fade(n, sr // 40, sr // 12)

    n = int(sr * 1.6)
    scream_n = int(sr * 0.85)
    scream = tone(scream_n, 360, 0.28)
    scream += lowpass(rng.uniform(-1, 1, scream_n).astype(np.float32), 0.15) * 0.35
    scream *= np.linspace(1, 0.2, scream_n).astype(np.float32)
    death = np.zeros(n, dtype=np.float32)
    death[:scream_n] += scream
    for i, freq in enumerate((980, 760, 1120, 640)):
        start = scream_n + i * (sr // 10)
        chirp_n = sr // 14
        if start + chirp_n < n:
            chirp = tone(chirp_n, freq, 0.5) * np.exp(-np.linspace(0, 4, chirp_n)).astype(np.float32)
            death[start : start + chirp_n] += chirp * 0.45
    clips["gaze_snatcher_death"] = death * envelope_fade(n, sr // 50, sr // 10)

    n = int(sr * 2.2)
    t = np.arange(n) / sr
    whimper = lowpass(rng.uniform(-1, 1, n).astype(np.float32), 0.35)
    whimper *= (0.45 + 0.55 * np.sin(2 * math.pi * 3.2 * t)).astype(np.float32)
    whimper += tone(n, 540, 1.08) * 0.08 * (0.5 + 0.5 * np.sin(2 * math.pi * 6 * t)).astype(np.float32)
    clips["baby_gaze_snatcher_ambient"] = whimper * 0.7 * envelope_fade(n, sr // 12, sr // 12)

    n = int(sr * 0.45)
    hurt = tone(n, 880, 0.55) * np.exp(-np.linspace(0, 3.2, n)).astype(np.float32)
    hurt += lowpass(rng.uniform(-1, 1, n).astype(np.float32), 0.4) * np.exp(-np.linspace(0, 4, n)) * 0.4
    clips["baby_gaze_snatcher_hurt"] = hurt * envelope_fade(n, sr // 80, sr // 10)

    n = int(sr * 0.9)
    baby_death = tone(n, 720, 0.3)
    baby_death += tone(n, 980, 0.4) * 0.3
    baby_death *= np.exp(-np.linspace(0, 3.5, n)).astype(np.float32)
    clips["baby_gaze_snatcher_death"] = baby_death * 0.8 * envelope_fade(n, sr // 60, sr // 8)

    SND.mkdir(parents=True, exist_ok=True)
    for name, audio in clips.items():
        peak = float(np.max(np.abs(audio))) or 1.0
        audio = (audio / peak * 0.89).astype(np.float32)
        path = SND / f"{name}.ogg"
        proc = subprocess.run(
            [
                "ffmpeg", "-y", "-hide_banner", "-loglevel", "error",
                "-f", "f32le", "-ar", str(sr), "-ac", "1", "-i", "pipe:0",
                "-c:a", "libvorbis", "-q:a", "5", str(path),
            ],
            input=audio.tobytes(),
            check=True,
        )
        del proc


def render_gallery(models: list[tuple[str, dict, np.ndarray, np.ndarray]]) -> None:
    GALLERY.mkdir(parents=True, exist_ok=True)

    def faces_of(cube, texture):
        frm = np.array(cube["from"], dtype=np.float64)
        to = np.array(cube["to"], dtype=np.float64)
        rects = box_uv_rects(cube["uv"][0], cube["uv"][1], cube["w"], cube["h"], cube["d"])
        # coins : x y z, UV de la face (u0,v0)-(u1,v1) en pixels
        specs = {
            "front": ([0, 1, 2, 3], (0, 0, -1)),
            "back": ([4, 5, 6, 7], (0, 0, 1)),
            "top": ([3, 2, 6, 7], (0, 1, 0)),
            "bottom": ([0, 1, 5, 4], (0, -1, 0)),
            "left": ([0, 3, 7, 4], (-1, 0, 0)),
            "right": ([1, 2, 6, 5], (1, 0, 0)),
        }
        corners = np.array([
            [frm[0], frm[1], frm[2]],
            [to[0], frm[1], frm[2]],
            [to[0], to[1], frm[2]],
            [frm[0], to[1], frm[2]],
            [frm[0], frm[1], to[2]],
            [to[0], frm[1], to[2]],
            [to[0], to[1], to[2]],
            [frm[0], to[1], to[2]],
        ])
        quads = []
        for face, (idx, normal) in specs.items():
            u, v, w, h = scale_rect(rects[face])
            quads.append((corners[list(idx)], (u, v, u + w, v + h), np.array(normal, dtype=np.float64), texture))
        return quads

    def project(models_posed, eye, target, width, height, fov_px):
        forward = np.array(target) - np.array(eye)
        forward = forward / np.linalg.norm(forward)
        right = np.cross(forward, np.array([0.0, 1.0, 0.0]))
        right = right / np.linalg.norm(right)
        up = np.cross(right, forward)
        color = np.zeros((height, width, 3), dtype=np.float32)
        depth = np.full((height, width), np.inf, dtype=np.float32)
        yy, xx = np.mgrid[0:height, 0:width]
        for quads in models_posed:
            for corners, uv, normal, texture in quads:
                cam = []
                for point in corners:
                    rel = point - np.array(eye)
                    cam.append((float(np.dot(rel, right)), float(np.dot(rel, up)), float(np.dot(rel, forward))))
                cam = np.array(cam)
                if np.any(cam[:, 2] <= 0.2):
                    continue
                facing = np.dot(normal, forward)
                if facing >= 0.05:
                    continue
                sx = fov_px * cam[:, 0] / cam[:, 2] + width / 2
                sy = -fov_px * cam[:, 1] / cam[:, 2] + height / 2
                minx, maxx = int(max(0, np.floor(sx.min()))), int(min(width - 1, np.ceil(sx.max())))
                miny, maxy = int(max(0, np.floor(sy.min()))), int(min(height - 1, np.ceil(sy.max())))
                if minx >= maxx or miny >= maxy:
                    continue
                # triangle 0-1-2 et 0-2-3
                light = 0.38 + 0.62 * max(0.0, -float(np.dot(normal, np.array([0.25, 0.85, 0.45]))))
                for tri in ((0, 1, 2), (0, 2, 3)):
                    p = np.stack([sx[list(tri)], sy[list(tri)]], axis=1)
                    z = cam[list(tri), 2]
                    uvs = np.array([
                        [uv[0], uv[1]],
                        [uv[2], uv[1]],
                        [uv[2], uv[3]],
                        [uv[0], uv[3]],
                    ], dtype=np.float64)[list(tri)]
                    area = (p[1, 0] - p[0, 0]) * (p[2, 1] - p[0, 1]) - (p[2, 0] - p[0, 0]) * (p[1, 1] - p[0, 1])
                    if abs(area) < 1e-6:
                        continue
                    xs = xx[miny : maxy + 1, minx : maxx + 1]
                    ys = yy[miny : maxy + 1, minx : maxx + 1]
                    w0 = ((p[1, 0] - xs) * (p[2, 1] - ys) - (p[2, 0] - xs) * (p[1, 1] - ys)) / area
                    w1 = ((p[2, 0] - xs) * (p[0, 1] - ys) - (p[0, 0] - xs) * (p[2, 1] - ys)) / area
                    w2 = 1 - w0 - w1
                    mask = (w0 >= 0) & (w1 >= 0) & (w2 >= 0)
                    if not np.any(mask):
                        continue
                    zz = w0 * z[0] + w1 * z[1] + w2 * z[2]
                    nearer = mask & (zz < depth[miny : maxy + 1, minx : maxx + 1])
                    if not np.any(nearer):
                        continue
                    uu = w0 * uvs[0, 0] + w1 * uvs[1, 0] + w2 * uvs[2, 0]
                    vv = w0 * uvs[0, 1] + w1 * uvs[1, 1] + w2 * uvs[2, 1]
                    tex_h, tex_w, _ = texture.shape
                    uu = np.clip(uu, 0, tex_w - 1.001)
                    vv = np.clip(vv, 0, tex_h - 1.001)
                    u0 = np.floor(uu).astype(np.int32)
                    v0 = np.floor(vv).astype(np.int32)
                    u1 = np.minimum(u0 + 1, tex_w - 1)
                    v1 = np.minimum(v0 + 1, tex_h - 1)
                    fu = (uu - u0)[..., None]
                    fv = (vv - v0)[..., None]
                    c00 = texture[v0, u0].astype(np.float32)
                    c10 = texture[v0, u1].astype(np.float32)
                    c01 = texture[v1, u0].astype(np.float32)
                    c11 = texture[v1, u1].astype(np.float32)
                    sampled = c00 * (1 - fu) * (1 - fv) + c10 * fu * (1 - fv) + c01 * (1 - fu) * fv + c11 * fu * fv
                    shade = sampled[:, :, :3] * light
                    nearer &= sampled[:, :, 3] > 16
                    if not np.any(nearer):
                        continue
                    view = depth[miny : maxy + 1, minx : maxx + 1]
                    view[nearer] = zz[nearer]
                    patch = color[miny : maxy + 1, minx : maxx + 1]
                    patch[nearer] = shade[nearer] / 255.0
        sky = np.linspace(0.07, 0.11, height, dtype=np.float32)[:, None, None]
        sky = np.broadcast_to(sky, (height, width, 3)).copy()
        sky[:, :, 0] *= 0.7
        sky[:, :, 2] *= 1.15
        covered = np.isfinite(depth)
        sky[covered] = np.clip(color[covered], 0, 1)
        return (np.clip(sky, 0, 1) * 255).astype(np.uint8)

    def quads_for(spec, texture, translate):
        quads = []
        for cube in list(spec["cubes"]) + list(spec["children"]):
            moved = {
                **cube,
                "from": tuple(a + b for a, b in zip(cube["from"], translate)),
                "to": tuple(a + b for a, b in zip(cube["to"], translate)),
            }
            quads.extend(faces_of(moved, texture))
        return quads

    adult_spec, adult_tex, adult_glow = models[0]
    baby_spec, baby_tex, baby_glow = models[1]
    shots = [
        ("gaze-snatcher.png", [quads_for(adult_spec, adult_tex, (0, 0, 0))], (32, 34, -46), (0, 24, 0), 600),
        ("baby-gaze-snatcher.png", [quads_for(baby_spec, baby_tex, (0, 0, 0))], (16, 11, -20), (1, 5, 1), 760),
        (
            "gaze-snatchers.png",
            [
                quads_for(adult_spec, adult_tex, (0, 0, 0)),
                quads_for(baby_spec, baby_tex, (12, 0, 4)),
            ],
            (40, 32, -48),
            (6, 18, 2),
            520,
        ),
    ]
    for filename, posed, eye, target, fov in shots:
        image = project(posed, eye, target, 1280, 720, fov)
        write_png(GALLERY / filename, image)
    eye = (10, 44, -22)
    target = (0, 41, 0)
    portrait_quads = quads_for(adult_spec, adult_tex, (0, 0, 0))
    glow_quads = quads_for(adult_spec, adult_glow, (0, 0, 0))
    base = project([portrait_quads], eye, target, 960, 960, 620)
    glow = project([glow_quads], eye, target, 960, 960, 620)
    added = np.clip(base.astype(np.int16) + (glow.astype(np.int16) // 3), 0, 255).astype(np.uint8)
    write_png(GALLERY / "gaze-snatcher-eyes.png", added)


def assert_atlas(size: int, cubes: list[dict]) -> None:
    seen: list[tuple[tuple[int, int, int, int], str]] = []
    for cube in cubes:
        rects = box_uv_rects(cube["uv"][0], cube["uv"][1], cube["w"], cube["h"], cube["d"])
        for rect in rects.values():
            u, v, w, h = rect
            if w <= 0 or h <= 0 or u < 0 or v < 0 or u + w > size or v + h > size:
                raise SystemExit(f"UV hors atlas pour {cube['bone']} : {rect} (texture {size})")
            for prev, name in seen:
                pu, pv, pw, ph = prev
                if u < pu + pw and pu < u + w and v < pv + ph and pv < v + h:
                    raise SystemExit(f"UV en chevauchement : {cube['bone']} {rect} et {name} {prev}")
            seen.append((rect, cube["bone"]))


def prepare(spec: dict, eyes_fn, seed: int):
    cubes = [cube_dims(c) for c in spec["cubes"]]
    children = [cube_dims(c) for c in spec["children"]]
    spec = {**spec, "cubes": cubes, "children": children}
    paint_cubes = cubes + children
    assert_atlas(spec["tex"], paint_cubes)
    eyes = eyes_fn(cubes)
    image, glow = paint_texture(spec["tex"] * TEX_DENSITY, paint_cubes, eyes, seed)
    return spec, image, glow


def main() -> None:
    adult, adult_img, adult_glow = prepare(ADULT, adult_eyes, 7)
    baby, baby_img, baby_glow = prepare(BABY, baby_eyes, 11)
    write_png(TEX / "gaze_snatcher.png", adult_img)
    write_png(TEX / "gaze_snatcher_emissive.png", adult_glow)
    write_png(TEX / "baby_gaze_snatcher.png", baby_img)
    write_png(TEX / "baby_gaze_snatcher_emissive.png", baby_glow)
    BB.mkdir(parents=True, exist_ok=True)
    for spec, image, filename in (
        (adult, adult_img, "gaze_snatcher.png"),
        (baby, baby_img, "baby_gaze_snatcher.png"),
    ):
        raw = (TEX / filename).read_bytes()
        model = build_bbmodel(spec, filename, raw)
        (BB / f"{spec['name']}.bbmodel").write_text(json.dumps(model, indent=2) + "\n")
    adult_java(adult)
    baby_java(baby)
    sound_names = (
        "gaze_snatcher_ambient",
        "gaze_snatcher_spawn",
        "gaze_snatcher_steal",
        "gaze_snatcher_death",
        "baby_gaze_snatcher_ambient",
        "baby_gaze_snatcher_hurt",
        "baby_gaze_snatcher_death",
    )
    if all((SND / f"{name}.ogg").exists() for name in sound_names):
        print("sons conservés")
    else:
        synth_sounds()
    render_gallery([(adult, adult_img, adult_glow), (baby, baby_img, baby_glow)])
    print("art prêt")


if __name__ == "__main__":
    main()
