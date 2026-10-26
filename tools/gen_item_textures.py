"""Generate Minecraft-style 16x16 item textures for Gaze Snatchers."""
from PIL import Image
import os

OUT = os.path.join(
    os.path.dirname(__file__),
    "..",
    "common",
    "src",
    "main",
    "resources",
    "assets",
    "gazesnatchers",
    "textures",
    "item",
)
OUT = os.path.normpath(OUT)
os.makedirs(OUT, exist_ok=True)


def save(name, img):
    path = os.path.join(OUT, name)
    img.save(path, "PNG")
    print(name, os.path.getsize(path))


VOID = (18, 12, 28, 255)
INK = (28, 18, 42, 255)
PURPLE = (132, 62, 198, 255)
LILAC = (186, 120, 255, 255)
CYAN = (90, 220, 255, 255)
TEAL = (40, 160, 200, 255)
GOLD = (220, 180, 70, 255)
GOLD_D = (160, 110, 30, 255)
CREAM = (255, 245, 220, 255)
WHITE = (255, 255, 255, 255)
GLASS = (180, 210, 230, 180)
GLASS_E = (120, 150, 180, 220)
LIQUID = (120, 70, 220, 255)
LIQUID_H = (200, 160, 255, 255)
EGG_S = (48, 28, 72, 255)
EGG_M = (72, 40, 110, 255)
EGG_L = (110, 70, 160, 255)
SPOT = (40, 200, 230, 255)

# --- gaze_shard ---
im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
for y, xs in [
    (2, [7, 8]),
    (3, [6, 7, 8, 9]),
    (4, [5, 6, 7, 8, 9]),
    (5, [4, 5, 6, 7, 8, 9, 10]),
    (6, [4, 5, 6, 7, 8, 9, 10]),
    (7, [5, 6, 7, 8, 9, 10]),
    (8, [5, 6, 7, 8, 9]),
    (9, [6, 7, 8, 9]),
    (10, [6, 7, 8]),
    (11, [7, 8]),
    (12, [7]),
]:
    for x in xs:
        im.putpixel((x, y), PURPLE)
for x, y in [
    (8, 3),
    (9, 4),
    (9, 5),
    (10, 5),
    (10, 6),
    (9, 6),
    (9, 7),
    (8, 7),
    (8, 8),
    (7, 9),
    (7, 10),
    (7, 11),
]:
    im.putpixel((x, y), LILAC)
for x, y in [(7, 5), (7, 6), (8, 5), (8, 6), (7, 7)]:
    im.putpixel((x, y), CYAN)
im.putpixel((7, 6), WHITE)
for x, y in [
    (6, 3),
    (5, 4),
    (4, 5),
    (4, 6),
    (5, 8),
    (6, 9),
    (6, 10),
    (7, 12),
    (8, 11),
    (9, 9),
    (10, 7),
    (9, 3),
]:
    im.putpixel((x, y), INK)
im.putpixel((8, 2), CYAN)
im.putpixel((7, 4), TEAL)
save("gaze_shard.png", im)

# --- gaze_ward_amulet ---
im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
for x, y in [(7, 1), (8, 1), (7, 2), (8, 2), (7, 3), (8, 3)]:
    im.putpixel((x, y), GOLD_D)
im.putpixel((7, 1), GOLD)
im.putpixel((8, 2), GOLD)
body = [
    (5, 5),
    (6, 5),
    (7, 5),
    (8, 5),
    (9, 5),
    (10, 5),
    (4, 6),
    (5, 6),
    (6, 6),
    (7, 6),
    (8, 6),
    (9, 6),
    (10, 6),
    (11, 6),
    (4, 7),
    (5, 7),
    (6, 7),
    (7, 7),
    (8, 7),
    (9, 7),
    (10, 7),
    (11, 7),
    (4, 8),
    (5, 8),
    (6, 8),
    (7, 8),
    (8, 8),
    (9, 8),
    (10, 8),
    (11, 8),
    (4, 9),
    (5, 9),
    (6, 9),
    (7, 9),
    (8, 9),
    (9, 9),
    (10, 9),
    (11, 9),
    (5, 10),
    (6, 10),
    (7, 10),
    (8, 10),
    (9, 10),
    (10, 10),
    (6, 11),
    (7, 11),
    (8, 11),
    (9, 11),
    (7, 12),
    (8, 12),
]
for x, y in body:
    im.putpixel((x, y), GOLD)
for x, y in [
    (5, 5),
    (10, 5),
    (4, 6),
    (11, 6),
    (4, 9),
    (11, 9),
    (5, 10),
    (10, 10),
    (6, 11),
    (9, 11),
    (7, 12),
    (8, 12),
]:
    im.putpixel((x, y), GOLD_D)
for x, y in [(6, 7), (7, 7), (8, 7), (9, 7), (6, 8), (7, 8), (8, 8), (9, 8)]:
    im.putpixel((x, y), CREAM)
im.putpixel((7, 7), CYAN)
im.putpixel((8, 7), CYAN)
im.putpixel((7, 8), TEAL)
im.putpixel((8, 8), PURPLE)
im.putpixel((7, 7), WHITE)
im.putpixel((7, 4), GOLD)
im.putpixel((8, 4), GOLD_D)
save("gaze_ward_amulet.png", im)

# --- gaze_draught ---
im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
for x, y in [(7, 1), (8, 1), (7, 2), (8, 2)]:
    im.putpixel((x, y), GOLD_D)
im.putpixel((7, 1), GOLD)
for x, y in [(7, 3), (8, 3), (7, 4), (8, 4)]:
    im.putpixel((x, y), GLASS_E)
outline = [
    (5, 5),
    (6, 5),
    (7, 5),
    (8, 5),
    (9, 5),
    (10, 5),
    (4, 6),
    (11, 6),
    (4, 7),
    (11, 7),
    (4, 8),
    (11, 8),
    (4, 9),
    (11, 9),
    (4, 10),
    (11, 10),
    (5, 11),
    (10, 11),
    (6, 12),
    (7, 12),
    (8, 12),
    (9, 12),
]
for x, y in outline:
    im.putpixel((x, y), GLASS_E)
for y in range(6, 12):
    for x in range(5, 11):
        if im.getpixel((x, y))[3] == 0:
            im.putpixel((x, y), GLASS)
for y in range(8, 12):
    for x in range(5, 11):
        im.putpixel((x, y), LIQUID)
for x in range(6, 10):
    im.putpixel((x, 8), LIQUID_H)
im.putpixel((6, 9), CYAN)
im.putpixel((8, 10), LILAC)
im.putpixel((5, 7), WHITE)
im.putpixel((5, 8), (255, 255, 255, 160))
save("gaze_draught.png", im)


def spawn_egg(spots_color, name):
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    rows = {
        2: range(5, 11),
        3: range(4, 12),
        4: range(3, 13),
        5: range(3, 13),
        6: range(2, 14),
        7: range(2, 14),
        8: range(2, 14),
        9: range(2, 14),
        10: range(3, 13),
        11: range(3, 13),
        12: range(4, 12),
        13: range(5, 11),
    }
    for y, xs in rows.items():
        for x in xs:
            if x + y > 18:
                c = EGG_S
            elif x < 6 or y > 10:
                c = EGG_M
            else:
                c = EGG_L
            im.putpixel((x, y), c)
    edge = [
        (5, 2),
        (6, 2),
        (7, 2),
        (8, 2),
        (9, 2),
        (10, 2),
        (4, 3),
        (11, 3),
        (3, 4),
        (12, 4),
        (3, 5),
        (12, 5),
        (2, 6),
        (13, 6),
        (2, 7),
        (13, 7),
        (2, 8),
        (13, 8),
        (2, 9),
        (13, 9),
        (3, 10),
        (12, 10),
        (3, 11),
        (12, 11),
        (4, 12),
        (11, 12),
        (5, 13),
        (10, 13),
        (6, 13),
        (7, 13),
        (8, 13),
        (9, 13),
    ]
    for x, y in edge:
        im.putpixel((x, y), VOID)
    for x, y in [
        (5, 5),
        (6, 5),
        (9, 6),
        (10, 6),
        (4, 8),
        (5, 8),
        (8, 9),
        (9, 9),
        (6, 11),
        (7, 7),
    ]:
        im.putpixel((x, y), spots_color)
    im.putpixel((7, 7), CREAM)
    im.putpixel((8, 7), CREAM)
    im.putpixel((7, 8), spots_color)
    im.putpixel((8, 8), VOID)
    im.putpixel((7, 7), WHITE)
    save(name, im)


spawn_egg(SPOT, "gaze_snatcher_spawn_egg.png")
spawn_egg(LILAC, "baby_gaze_snatcher_spawn_egg.png")
print("done ->", OUT)
