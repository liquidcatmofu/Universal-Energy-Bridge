#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$repo_root"

version="${1:-}"
output="${2:-}"
if [[ -z "$version" || -z "$output" ]]; then
    echo "Usage: scripts/extract-release-notes.sh <SemVer> <output-file>" >&2
    exit 2
fi

python3 scripts/changelog-entry.py extract "$version" "$output"
