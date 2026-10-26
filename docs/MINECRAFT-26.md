# Minecraft 26.x (26.1 → 26.3)

Minecraft **26.1+** is **unobfuscated** (Mojang names). It is **not** the same as the Architectury / 1.21.11 profile.

## Build

- **Gradle** 9.5.1
- **JDK 25** for Minecraft 26.x
- **JDK 21** for Minecraft 1.21.11
- Loom **no-remap** (`unobfuscated=true`) on 26.x
- Profiles: `gradle/versions/26.1.properties` … `26.3.properties`

Dedicated sources (no Architectury API on 26.x):

- `common/src/main/javaMojmap26/`
- `fabric/src/main/javaMojmap26/` + `fabric/src/unobfuscated/resources/`
- `neoforge/src/main/javaMojmap26/` + `neoforge/src/unobfuscated/resources/`

## Commands

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.3"
$env:BUILD_26 = "true"
.\scripts\build-all-versions.ps1
```

```bash
export JAVA_HOME=/path/to/jdk-25
BUILD_26=true ./scripts/build-all-versions.sh
```

Without `BUILD_26=true`, only **1.21.11** is built (JDK 21).

**26.1** / **26.1.1** : `skip_neoforge=true` (Fabric only).

## Single-profile Gradle

```powershell
# merge common + version, then build
Get-Content gradle/versions/common.properties, gradle/versions/26.3.properties |
  Set-Content gradle.properties  # prefer UTF-8 without BOM
.\gradlew.bat :fabric:remapJar :neoforge:remapJar
```

## Français

- JDK **25**, Loom no-remap (`unobfuscated=true`)
- Sources sous `javaMojmap26/` ; métadonnées dans `*/unobfuscated/resources/`
- Rebuild : `BUILD_26=true` + script `scripts/build-all-versions`
- Sans ce flag : build **1.21.11** uniquement
