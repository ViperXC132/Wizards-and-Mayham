# Wizards and Mayham

Minecraft Java Edition 1.21.11 Fabric magic/adventure RPG.

## Toolchain

- Minecraft 1.21.11
- Fabric Loader 0.18.4+
- Fabric API 0.141.3+1.21.11
- Fabric Loom 1.15-SNAPSHOT
- Java 21

## Architecture

The project is intentionally starting with a small, server-authoritative core. Gameplay systems will be added in staged modules for magic, wands, mana/energy, progression, enchantments, economy, artifacts, ruins, bosses, world generation, configuration, and networking.

VulkanMod is an optional client renderer. Wizards and Mayham does not depend on VulkanMod, Sodium, or Iris.

## Build

Use Java 21 and Gradle 8.14.3 or newer. The GitHub Actions build uses Gradle directly so the repository can be bootstrapped before a Gradle wrapper is committed.

```text
gradle build
```

## Build order

See the master build specification for the full feature roadmap. Each major stage should compile and be tested before the next system is introduced.
