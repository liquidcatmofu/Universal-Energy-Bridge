# Minecraft 1.20.1 Forge Mod Template

Minimal repository template for small Minecraft Forge 1.20.1 mods.

## Baseline

- Minecraft 1.20.1
- Forge 47.4.10
- ForgeGradle 6.0.54
- Gradle Wrapper 8.14
- Gradle runtime JDK 21
- Java target 17
- Official Mojang mappings
- MixinGradle 0.7.38 and SpongePowered Mixin enabled by default
- Keep a Changelog 1.1.0 + SemVer 2.0.0 release conventions
- CI and tag-driven GitHub Release / Modrinth / CurseForge publishing

Forge 1.20.1 documents JDK 17 as its development prerequisite. This template intentionally runs Gradle on JDK 21 while compiling with `--release 17`; release validation rejects class files newer than Java 17. This keeps the build runtime separate from the Minecraft runtime target.

## Create a mod

Create a repository from this GitHub template, then run:

```bash
./init.sh <mod_id> "<Mod Name>" <java.package>
```

Examples:

```bash
./init.sh example_tools "Example Tools" dev.liquidcatmofu.exampletools
./init.sh benri_tools "便利ツール" dev.liquidcatmofu.benritools
```

The Java main class is derived from the display name when it contains ASCII alphanumerics. For an entirely non-ASCII display name, the initializer falls back to the `mod_id`, so `benri_tools` becomes `BenriTools`.

The initializer validates the destination before changing files, updates the Gradle/mod metadata, Java package, main class, Mixin config, documentation scaffold, and artifact name, then removes template-only files including the template CI workflow and itself.

After initialization:

```bash
./gradlew clean build
```

With the example above, the public artifact naming scheme is:

```text
ExampleTools-Forge-1.20.1-0.1.0.jar
```

Release tags use `MinecraftVersion-SemVer`, for example `1.20.1-0.1.0`. See [Releasing](docs/releasing.md).

## Before the first release

Set the actual `mod_description`, `mod_license`, and `publish_environment` in `gradle.properties`, update the generated README files and CHANGELOG, and configure the publishing variables/secrets described in `docs/releasing.md`.
