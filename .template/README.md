# Example Mod

[日本語](README_ja.md) · [Changelog](CHANGELOG.md)

Example Mod is a Forge mod for Minecraft Java Edition 1.20.1.

## Supported environments

- Minecraft 1.20.1
- Forge 47.4.10
- Java 17 target
- Build JDK 21

Forge 1.20.1 documents JDK 17 as its development prerequisite. This project intentionally runs Gradle on JDK 21 while compiling with `--release 17`; release validation rejects class files newer than Java 17.

The default publication environment is `client | server`. Change `publish_environment` in `gradle.properties` if the mod is client-only, server-only, or optional on either side.

## Building

Use JDK 21 to run Gradle:

```bash
./gradlew clean build
```

On Windows:

```powershell
.\gradlew.bat clean build
```

The production JAR is generated as:

```text
build/libs/ExampleMod-Forge-1.20.1-0.1.0.jar
```

Release automation and required repository settings are documented in [Releasing](docs/releasing.md).

## License and author

Author: LiquidCatMofu

License metadata is defined by `mod_license` in `gradle.properties`. The template defaults to `All Rights Reserved`; add a standard `LICENSE` file when choosing an open-source license.
