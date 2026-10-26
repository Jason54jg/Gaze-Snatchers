"""Textures: checker/stone dark body + glowing eyes (white adult / purple baby)."""
from __future__ import annotations

import os
import random
from PIL import Image, ImageDraw

OUT = os.path.normpath(os.path.join(
    os.path.dirname(__file__), "..", "common", "src", "main", "resources",
    "assets", "gazesnatchers", "textures", "entity"))
rng = random.Random(42)


def save(name, img):
    p = os.path.join(OUT, name)
    img.save(p, "PNG")
    print(name, img.size, os.path.getsize(p))


def checker_fill(img, x, y, w, h, c0, c1, cell=2):
    for py in range(y, y + h):
        for px in range(x, x + w):
            use = ((px // cell) + (py // cell)) % 2 == 0
            base = c0 if use else c1
            d = rng.randint(-6, 6)
            img.putpixel((px, py), (
                max(0, min(255, base[0] + d)),
                max(0, min(255, base[1] + d)),
                max(0, min(255, base[2] + d)),
                255))


def paint_cube(img, u, v, w, h, d, c0, c1, cell=2):
    # top, bottom, west, north, east, south
    checker_fill(img, u + d, v, w, d, c0, c1, cell)
    checker_fill(img, u + d + w, v, w, d, c1, c0, cell)
    checker_fill(img, u, v + d, d, h, c0, c1, cell)
    checker_fill(img, u + d, v + d, w, h, c0, c1, cell)
    checker_fill(img, u + d + w, v + d, d, h, c0, c1, cell)
    checker_fill(img, u + d + w + d, v + d, w, h, c1, c0, cell)


C0 = (28, 28, 32)
C1 = (12, 12, 16)
C0b = (40, 38, 44)
C1b = (18, 16, 22)
WHITE = (255, 255, 255, 255)
PURPLE = (200, 100, 255, 255)
PURPLE_D = (140, 50, 220, 255)

# ---- Adult 128 ----
# Head 10³ @0,0 ; spine 2x22x2 @40,0 ; pelvis @48,0 ; ribs @48,8
# Arm 2x28x2 @64,0 ; Leg 2x22x2 @72,0 ; hand/toes @80,*
a = Image.new("RGBA", (128, 128), (0, 0, 0, 0))
paint_cube(a, 0, 0, 10, 10, 10, C0, C1, 2)
fx, fy = 10, 10  # front of head
# big white rectangular eyes
for y in range(3, 7):
    for x in range(1, 4):
        a.putpixel((fx + x, fy + y), WHITE)
    for x in range(6, 9):
        a.putpixel((fx + x, fy + y), WHITE)

paint_cube(a, 40, 0, 2, 22, 2, C0b, C1b, 1)
paint_cube(a, 48, 0, 6, 3, 3, C0, C1, 2)
# rib strips region
checker_fill(a, 48, 8, 20, 12, C0b, C1b, 1)
paint_cube(a, 64, 0, 2, 28, 2, C0, C1, 1)
paint_cube(a, 72, 0, 2, 22, 2, C0, C1, 1)
checker_fill(a, 80, 0, 16, 8, C0b, C1b, 1)
checker_fill(a, 80, 6, 12, 8, C0, C1, 1)
checker_fill(a, 80, 14, 14, 8, C0b, C1b, 1)
checker_fill(a, 80, 20, 10, 6, C0, C1, 1)
save("gaze_snatcher.png", a)

ae = Image.new("RGBA", (128, 128), (0, 0, 0, 0))
for y in range(3, 7):
    for x in range(1, 4):
        ae.putpixel((fx + x, fy + y), WHITE)
    for x in range(6, 9):
        ae.putpixel((fx + x, fy + y), WHITE)
save("gaze_snatcher_emissive.png", ae)

# ---- Baby 64 ----
# Head 12³ @0,0 ; Body 10x7x12 @0,24 ; legs 4x7x4 @48,0 / 48,12
b = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
paint_cube(b, 0, 0, 12, 12, 12, C0b, C1b, 2)
bfx, bfy = 12, 12
# purple square eyes with white center (2x2 bloom look)
for y in range(4, 8):
    for x in range(2, 5):
        b.putpixel((bfx + x, bfy + y), PURPLE if y < 7 else PURPLE_D)
    for x in range(7, 10):
        b.putpixel((bfx + x, bfy + y), PURPLE if y < 7 else PURPLE_D)
b.putpixel((bfx + 3, bfy + 5), WHITE)
b.putpixel((bfx + 8, bfy + 5), WHITE)

paint_cube(b, 0, 24, 10, 7, 12, C0, C1, 2)
paint_cube(b, 48, 0, 4, 7, 4, C0b, C1b, 2)
paint_cube(b, 48, 12, 4, 7, 4, C0b, C1b, 2)
save("baby_gaze_snatcher.png", b)

be = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
for y in range(4, 8):
    for x in range(2, 5):
        be.putpixel((bfx + x, bfy + y), PURPLE)
    for x in range(7, 10):
        be.putpixel((bfx + x, bfy + y), PURPLE)
be.putpixel((bfx + 3, bfy + 5), WHITE)
be.putpixel((bfx + 8, bfy + 5), WHITE)
save("baby_gaze_snatcher_emissive.png", be)
print("done")
