# Blockbench sources (GeckoLib only)

Canonical models for the mod. Both files use `"model_format": "geckolib_model"`.

| File | Entity |
|------|--------|
| `gaze_snatcher.bbmodel` | Adult |
| `baby_gaze_snatcher.bbmodel` | Baby |

Runtime assets are generated into `common/.../assets/gazesnatchers/geckolib/` via:

```bash
python tools/bbmodel_to_geckolib.py
```

Do not drop `modded_entity` `.bbmodel` files here — that format was for the old hand-written Java models and is incompatible with GeckoLib.
