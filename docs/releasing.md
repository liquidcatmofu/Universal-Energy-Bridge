# Releasing

Releases are created when a matching Git tag is pushed.

## Version scheme

- Mod metadata: strict `SemVer 2.0.0`
- Git tag and GitHub Release: `MinecraftVersion-SemVer`
- Public JAR: `ModName-Forge-MinecraftVersion-SemVer.jar`
- Modrinth/CurseForge version: `Forge-MinecraftVersion-SemVer`

For the default template values, version `0.1.0` produces:

```text
Tag: 1.20.1-0.1.0
JAR: ExampleMod-Forge-1.20.1-0.1.0.jar
Provider version: Forge-1.20.1-0.1.0
```

## Release channels

| `mod_version` | Modrinth/CurseForge | GitHub Release |
| --- | --- | --- |
| `1.2.0` | Release | Normal release |
| `1.2.0-alpha.1` | Alpha | Prerelease |
| `1.2.0-beta.1` | Beta | Prerelease |
| `1.2.0-rc.1` | Beta | Prerelease |

Versions must satisfy SemVer 2.0.0, including identifier rules such as no empty prerelease/build identifiers and no leading zeroes in numeric prerelease identifiers. The first prerelease identifier must be `alpha`, `beta`, or `rc`; other prerelease channels are rejected. GitHub prereleases are excluded from `Latest`.

## Source of truth

Keep `minecraft_version`, `mod_version`, `archives_name`, and `publish_environment` in `gradle.properties`. Compatibility declarations are separate:

- `minecraft_version_range` controls the Minecraft range advertised in `mods.toml`.
- `forge_version_range` controls the Forge range advertised in `mods.toml`.
- `loader_version_range` controls the JavaFML loader range.

The workflow validates committed versions but does not increment or commit them.

## CHANGELOG format

`CHANGELOG.md` follows [Keep a Changelog 1.1.0](https://keepachangelog.com/en/1.1.0/).

During development, record user-visible changes under `## [Unreleased]`, grouped under Keep a Changelog categories such as `Added`, `Changed`, `Deprecated`, `Removed`, `Fixed`, and `Security` as appropriate.

When releasing, move the relevant entries into an exact dated heading:

```markdown
## [0.1.0] - 2026-10-02

### Added

- Initial release.
```

Release headings must use `## [SemVer] - YYYY-MM-DD`. The date must be a real ISO calendar date. Duplicate headings for the same version and suffix text on a release heading are rejected.

Comparison links in the style used by Keep a Changelog are optional; the release automation does not require them.

## Release branch policy

The release workflow accepts a tag only when the tagged commit is contained in the repository's current default branch. It fetches the default branch and verifies ancestry before building or publishing.

The release workflow rebuilds the exact tagged commit. Branch protection should still require the normal CI workflow before changes are merged to the default branch.

## Java build runtime

Forge 1.20.1 documents JDK 17 as its prerequisite. This template intentionally uses JDK 21 to run Gradle while compiling with Java 17 as the target. The release workflow verifies that class files do not exceed Java 17 class major version 61.

## Optional publishing providers

GitHub Releases require no additional project variables or secrets.

Modrinth publishing is enabled only when the repository variable `MODRINTH_PROJECT_ID` is set. When enabled, the repository secret `MODRINTH_TOKEN` is required.

CurseForge publishing is enabled only when the repository variable `CURSEFORGE_PROJECT_ID` is set. When enabled, the repository secret `CURSEFORGE_TOKEN` is required.

This allows a generated repository to use GitHub Releases only, GitHub + one provider, or all providers without editing the workflow.

## Local validation

The release checks are also runnable locally:

```bash
scripts/release-metadata.sh 1.20.1-0.1.0
scripts/extract-release-notes.sh 0.1.0 /tmp/release-notes.md
scripts/validate-release-jar.py build/libs/ExampleMod-Forge-1.20.1-0.1.0.jar 0.1.0
```

## Release procedure

1. Update `mod_version` in `gradle.properties`.
2. Move the release changes from `[Unreleased]` into a non-empty `## [SemVer] - YYYY-MM-DD` section.
3. Confirm `publish_environment`, compatibility ranges, dependencies, and project metadata are accurate.
4. Merge the release commit into the default branch and require CI success for that exact commit.
5. Confirm there is no conflicting tag or provider version.
6. Create and push `MinecraftVersion-SemVer`, for example `1.20.1-0.1.0`.
7. Monitor GitHub Release and any configured external provider jobs.
8. Verify the JAR filename, SHA-256, loader, Minecraft version, Java 17 target, environment, and dependencies.
