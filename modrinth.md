# LightMod ⚡

**Performance optimizer for Minecraft on weak hardware.**  
Works on **26.1.2** and **26.2**. No Vulkan required. Fully compatible with Sodium + Iris.

---

## Why LightMod?

Minecraft 26.2 added an experimental Vulkan renderer — but on older GPUs and integrated graphics it crashes or simply won't start. **LightMod works with plain OpenGL** and optimizes the CPU side of the game instead: what to render, when to render it, and how to adapt in real time to your hardware.

---

## Features

### ⚙️ Dynamic FPS Optimizer
Monitors your average FPS every 2 seconds and automatically nudges render distance up or down to keep you near your target. Configurable target FPS, min, and max render distance.

### 👁️ Entity Culling
Skips rendering entities that are solidly behind the camera. On a busy server this can save 20–35% of entity render time. **Auto-disabled when Sodium is present** — Sodium's occlusion culling is more sophisticated.

### 📦 Chunk Load Throttle
Caps chunk mesh uploads per frame. Vanilla can burst 10+ uploads in one tick, causing frame drops when entering new areas. We limit this to 4 by default. **Auto-disabled when Sodium is present.**

### ✨ Dynamic Particles
Probabilistically skips particle spawns when FPS drops. At 60 FPS target: 0% skip. At 30 FPS: ~50% skip. At 15 FPS: ~80% skip. With Iris shaders active, the threshold becomes stricter since shaders make particles significantly heavier.

### ☁️ Auto Clouds
Automatically switches off cloud rendering when FPS is consistently low, and restores it when FPS recovers. Safe with Iris shaders.

---

## Settings Screen

Press **[L]** in-game (rebindable) or use the **Configure** button in [Mod Menu](https://modrinth.com/mod/modmenu).

![Settings screen with three tabs: General, Rendering, Advanced](https://raw.githubusercontent.com/yourname/lightmod/main/docs/settings-preview.png)

Three categories:
- **General** — target FPS, render distance range, optimizer toggle
- **Rendering** — entity culling, clouds, particles
- **Advanced** — chunk throttle, status panel showing if Sodium/Iris are detected

---

## Compatibility

| Mod | Status |
|---|---|
| **Sodium** | ✅ Fully compatible — overlapping features auto-disabled |
| **Iris Shaders** | ✅ Fully compatible — particle system becomes shader-aware |
| **Mod Menu** | ✅ Adds "Configure" button |
| **Lithium** | ✅ No conflicts |
| **FerriteCore** | ✅ No conflicts |
| **OptiFabric** | ❌ Incompatible (avoid on 26.x anyway) |

---

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for your MC version
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. *(Recommended)* Install [Iris Shaders](https://modrinth.com/mod/iris) — it bundles Sodium
4. Drop **lightmod-2.0.0.jar** into `.minecraft/mods/`

---

## Required Dependencies

- [Cloth Config API](https://modrinth.com/mod/cloth-config) — powers the settings screen (auto-installed by most launchers)

---

## Source

[GitHub](https://github.com/yourname/lightmod) · MIT License
