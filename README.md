# LightMod v2.0

Performance optimizer for Minecraft 26.1.2 and 26.2 (Fabric).
No Vulkan required. Settings screen included. Compatible with Sodium + Iris.

## Quick start

1. Install Fabric Loader + Fabric API
2. Drop `lightmod-2.0.0.jar` into `.minecraft/mods/`
3. Press **[L]** in-game to open settings

## Building

```bash
./gradlew build
# Output: build/libs/lightmod-2.0.0.jar
```

Switch versions: edit commented blocks in `gradle.properties`.

## Publishing to Modrinth (step by step)

1. Go to modrinth.com → Create new project → type: Mod
2. Fill title "LightMod", paste content from `modrinth.md` as description
3. Copy your **Project ID** from the right sidebar
4. Go to modrinth.com/settings/pats → Create token with `VERSION_CREATE` scope
5. GitHub repo → Settings → Secrets → New secret → name: `MODRINTH_TOKEN`, value: your token
6. In `.github/workflows/publish.yml` replace `PASTE_YOUR_PROJECT_ID_HERE` with your Project ID
7. Push a tag:
   ```bash
   git add .
   git commit -m "Initial release"
   git tag v2.0.0
   git push origin main --tags
   ```
   GitHub Actions builds and uploads to Modrinth automatically.

## File structure

```
src/main/java/dev/lightmod/
├── LightMod.java                   entrypoint, keybind [L], mod detection
├── DynamicOptimizer.java           FPS-aware render distance adjuster
├── config/LightModConfig.java      .properties config file
├── screen/
│   ├── LightModScreenFactory.java  Cloth Config settings UI (3 tabs)
│   └── ModMenuIntegration.java     "Configure" button in Mod Menu
└── mixin/
    ├── EntityCullingMixin.java      skip entities behind camera
    ├── ChunkThrottleMixin.java      limit chunk uploads/frame
    ├── ParticleReducerMixin.java    reduce particles on low FPS
    └── AutoCloudsMixin.java         auto-disable clouds on low FPS
```
