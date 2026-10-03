#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_root"

tag="${1:-${GITHUB_REF_NAME:-}}"
if [[ -z "$tag" ]]; then
    echo "Usage: scripts/release-metadata.sh <MinecraftVersion-SemVer>" >&2
    exit 2
fi

read_property() {
    sed -n "s/^$1 *= *//p" gradle.properties | tr -d '\r'
}

archives_name="$(read_property archives_name)"
minecraft_version="$(read_property minecraft_version)"
mod_version="$(read_property mod_version)"
publish_environment="$(read_property publish_environment)"
release_version="${minecraft_version}-${mod_version}"

for pair in \
    "archives_name:$archives_name" \
    "minecraft_version:$minecraft_version" \
    "mod_version:$mod_version" \
    "publish_environment:$publish_environment"
do
    name="${pair%%:*}"
    value="${pair#*:}"
    if [[ -z "$value" ]]; then
        echo "Missing required gradle.properties value: $name" >&2
        exit 1
    fi
done

validate_semver() {
    local version="$1"
    local prerelease
    local identifier

    if [[ ! "$version" =~ ^(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)(-([0-9A-Za-z-]+(\.[0-9A-Za-z-]+)*))?(\+([0-9A-Za-z-]+(\.[0-9A-Za-z-]+)*))?$ ]]; then
        echo "mod_version must be valid SemVer 2.0.0: $version" >&2
        return 1
    fi

    prerelease="${BASH_REMATCH[5]:-}"
    if [[ -n "$prerelease" ]]; then
        IFS='.' read -r -a identifiers <<< "$prerelease"
        for identifier in "${identifiers[@]}"; do
            if [[ "$identifier" =~ ^0[0-9]+$ ]]; then
                echo "Numeric prerelease identifiers must not contain leading zeroes: $identifier" >&2
                return 1
            fi
        done
    fi
}

validate_semver "$mod_version"

version_without_build="${mod_version%%+*}"
if [[ "$version_without_build" == *-* ]]; then
    prerelease="${version_without_build#*-}"
    channel="${prerelease%%.*}"
    case "$channel" in
        alpha)
            release_type="alpha"
            ;;
        beta|rc)
            release_type="beta"
            ;;
        *)
            echo "Unsupported SemVer prerelease channel: $channel" >&2
            echo "Use alpha, beta, or rc; stable versions must not have a prerelease suffix." >&2
            exit 1
            ;;
    esac
    github_prerelease="true"
else
    release_type="release"
    github_prerelease="false"
fi

if [[ "$tag" != "$release_version" ]]; then
    echo "Tag must be $release_version, but was $tag" >&2
    exit 1
fi

release_date="$(python3 scripts/changelog-entry.py date "$mod_version")"

emit() {
    local name="$1"
    local value="$2"
    printf '%s=%s\n' "$name" "$value"
    if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
        printf '%s=%s\n' "$name" "$value" >> "$GITHUB_OUTPUT"
    fi
}

emit archives_name "$archives_name"
emit minecraft_version "$minecraft_version"
emit mod_version "$mod_version"
emit publish_environment "$publish_environment"
emit release_version "$release_version"
emit release_date "$release_date"
emit release_type "$release_type"
emit github_prerelease "$github_prerelease"
