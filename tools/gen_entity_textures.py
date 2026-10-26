"""Paint Gaze Snatcher entity atlases with correct Minecraft cube UV layout."""
from __future__ import annotations

from PIL import Image, ImageDraw
import os

OUT = os.path.normpath(
    os.path.join(
        os.path.dirname(__file__),
        "..",
        "common",
        "src",
        "main",
        "resources",
        "assets",
        "gazesnatchers",
        "textures",
        "entity",
    )
)


def save(name: str, img: Image.Image) -> None:
    path = os.path.join(OUT, name)
    img.save(path, "PNG")
    print(name, img.size, os.path.getsize(path))


def fill_rect(img: Image.Image, x: int, y: int, w: int, h: int, color) -> None:
    if w <= 0 or h <= 0:
        return
    ImageDraw.Draw(img).rectangle([x, y, x + w - 1, y + h - 1], fill=color)


def paint_cube(img, u, v, w, h, d, faces):
    fill_rect(img, u + d, v, w, d, faces["top"])
    fill_rect(img, u + d + w, v, w, d, faces["bottom"])
    fill_rect(img, u, v + d, d, h, faces["west"])
    fill_rect(img, u + d, v + d, w, h, faces["north"])
    fill_rect(img, u + d + w, v + d, d, h, faces["east"])
    fill_rect(img, u + d + w + d, v + d, w, h, faces["south"])


def shade(base, factor):
    r, g, b, a = base
    return (max(0, min(255, int(r * factor))), max(0, min(255, int(g * factor))), max(0, min(255, int(b * factor))), a)


def paint_front(img, u, v, w, h, d, painter):
    fx, fy = u + d, v + d
    for ly in range(h):
        for lx in range(w):
            painter(img, fx + lx, fy + ly, lx, ly, w, h)


VOID = (8, 6, 14, 255)
DARK = (16, 12, 26, 255)
MID = (28, 20, 42, 255)
EDGE = (42, 30, 62, 255)
CYAN = (70, 230, 255, 255)
CYAN_D = (30, 140, 180, 255)
WHITE = (255, 255, 255, 255)
PURPLE = (120, 60, 200, 255)

# --- Adult 64x64 (same UV as GazeSnatcherModel) ---
atlas = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
paint_cube(atlas, 0, 0, 10, 10, 10, {
    "top": shade(DARK, 0.85), "bottom": VOID,
    "west": shade(MID, 0.9), "east": shade(MID, 0.9),
    "north": DARK, "south": shade(DARK, 0.95),
})


def adult_face(img, px, py, lx, ly, fw, fh):
    if (lx + ly) % 5 == 0:
        img.putpixel((px, py), EDGE)
    if 3 <= ly <= 5 and (2 <= lx <= 3 or 6 <= lx <= 7):
        img.putpixel((px, py), CYAN if ly != 5 else CYAN_D)
        if ly == 3 and (lx == 2 or lx == 6):
            img.putpixel((px, py), WHITE)
    if ly == 8 and 3 <= lx <= 6:
        img.putpixel((px, py), VOID)


paint_front(atlas, 0, 0, 10, 10, 10, adult_face)
paint_cube(atlas, 0, 24, 5, 16, 3, {
    "top": MID, "bottom": VOID, "west": shade(DARK, 1.05), "east": shade(DARK, 1.05),
    "north": MID, "south": DARK,
})
paint_cube(atlas, 40, 0, 2, 20, 2, {
    "top": MID, "bottom": VOID, "west": DARK, "east": DARK, "north": MID, "south": DARK,
})
fill_rect(atlas, 42, 19, 2, 3, EDGE)
paint_cube(atlas, 40, 24, 2, 16, 2, {
    "top": MID, "bottom": VOID, "west": DARK, "east": DARK, "north": MID, "south": DARK,
})
save("gaze_snatcher.png", atlas)

emissive = Image.new("RGBA", (64, 64), (0, 0, 0, 0))


def eyes(img, px, py, lx, ly, fw, fh):
    if 3 <= ly <= 5 and (2 <= lx <= 3 or 6 <= lx <= 7):
        img.putpixel((px, py), CYAN if ly < 5 else CYAN_D)
        if ly == 3 and (lx == 2 or lx == 6):
            img.putpixel((px, py), WHITE)


paint_front(emissive, 0, 0, 10, 10, 10, eyes)
save("gaze_snatcher_emissive.png", emissive)

# --- Baby 64x64: Head 8x8x8 @0,0 ; Body 6x5x8 @0,20 ; Legs @32,0 / 32,8 ---
baby = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
paint_cube(baby, 0, 0, 8, 8, 8, {
    "top": shade(PURPLE, 0.4), "bottom": VOID,
    "west": shade(MID, 1.1), "east": shade(MID, 1.1),
    "north": MID, "south": DARK,
})


def baby_face(img, px, py, lx, ly, fw, fh):
    if 2 <= ly <= 4 and (1 <= lx <= 2 or 5 <= lx <= 6):
        img.putpixel((px, py), CYAN)
        if ly == 2:
            img.putpixel((px, py), WHITE)
    if ly == 6 and 2 <= lx <= 5:
        img.putpixel((px, py), VOID)


paint_front(baby, 0, 0, 8, 8, 8, baby_face)
paint_cube(baby, 0, 20, 6, 5, 8, {
    "top": MID, "bottom": VOID, "west": DARK, "east": DARK, "north": EDGE, "south": DARK,
})
paint_cube(baby, 32, 0, 2, 4, 2, {
    "top": MID, "bottom": VOID, "west": DARK, "east": DARK, "north": MID, "south": DARK,
})
paint_cube(baby, 32, 8, 2, 4, 2, {
    "top": MID, "bottom": VOID, "west": DARK, "east": DARK, "north": MID, "south": DARK,
})
save("baby_gaze_snatcher.png", baby)

baby_e = Image.new("RGBA", (64, 64), (0, 0, 0, 0))


def baby_eyes(img, px, py, lx, ly, fw, fh):
    if 2 <= ly <= 4 and (1 <= lx <= 2 or 5 <= lx <= 6):
        img.putpixel((px, py), CYAN)
        if ly == 2:
            img.putpixel((px, py), WHITE)


paint_front(baby_e, 0, 0, 8, 8, 8, baby_eyes)
save("baby_gaze_snatcher_emissive.png", baby_e)
print("done", OUT)
