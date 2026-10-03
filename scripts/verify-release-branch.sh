#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 2 ]]; then
    echo "Usage: scripts/verify-release-branch.sh <commit> <default-branch-ref>" >&2
    exit 2
fi

commit="$1"
default_ref="$2"

git rev-parse --verify "${commit}^{commit}" >/dev/null
git rev-parse --verify "${default_ref}^{commit}" >/dev/null

if ! git merge-base --is-ancestor "$commit" "$default_ref"; then
    echo "Release commit $commit is not contained in $default_ref" >&2
    exit 1
fi

echo "Release commit $commit is contained in $default_ref"
