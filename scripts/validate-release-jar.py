#!/usr/bin/env python3
from __future__ import annotations

import hashlib
import struct
import sys
import zipfile
from pathlib import Path

if len(sys.argv) != 3:
    raise SystemExit("Usage: scripts/validate-release-jar.py <jar> <mod-version>")

jar = Path(sys.argv[1])
mod_version = sys.argv[2]

if not jar.is_file():
    raise SystemExit(f"Release JAR does not exist: {jar}")

with zipfile.ZipFile(jar) as archive:
    try:
        mods_toml = archive.read("META-INF/mods.toml").decode("utf-8")
    except KeyError as exc:
        raise SystemExit("Release JAR has no META-INF/mods.toml") from exc

    expected_version = f'version="{mod_version}"'
    if expected_version not in mods_toml:
        raise SystemExit(
            f"mods.toml does not contain expected version metadata: {expected_version}"
        )

    classes = [name for name in archive.namelist() if name.endswith(".class")]
    if not classes:
        raise SystemExit("No class files found in release JAR")

    bad: list[tuple[str, int]] = []
    for name in classes:
        data = archive.read(name)
        if data[:4] != b"\xca\xfe\xba\xbe":
            continue
        major = struct.unpack(">H", data[6:8])[0]
        if major > 61:
            bad.append((name, major))

    if bad:
        raise SystemExit(f"Classes exceed Java 17 (major 61): {bad[:10]}")

digest = hashlib.sha256(jar.read_bytes()).hexdigest()
print(f"{digest}  {jar}")
