# Changelog

## v2.0.0 — Initial Release

### Features
- **Dynamic FPS Optimizer** — automatically adjusts render distance to maintain your target FPS
- **Entity Culling** — skips rendering entities behind the camera (~30% entity render savings)
- **Chunk Load Throttle** — limits chunk mesh uploads per frame, eliminates stutters
- **Dynamic Particles** — reduces particle spawn rate when FPS drops
- **Auto Clouds** — disables cloud rendering automatically when FPS is low

### Settings Screen
- Full settings UI accessible via **[L]** key or Mod Menu
- Three categories: General / Rendering / Advanced
- Live Sodium + Iris detection status shown in Advanced tab

### Compatibility
- Minecraft 26.1.2 and 26.2 (Fabric)
- Sodium: entity culling and chunk throttle auto-disabled to avoid conflicts
- Iris: particle reduction becomes more aggressive when shaders are active
- Mod Menu: "Configure" button shown in mod list
