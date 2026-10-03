#!/usr/bin/env python3
from __future__ import annotations

import re
import sys
from datetime import date
from pathlib import Path

CHANGELOG = Path("CHANGELOG.md")


def load_entry(version: str) -> tuple[list[str], int, str]:
    lines = CHANGELOG.read_text(encoding="utf-8").splitlines()
    prefix = f"## [{version}]"
    candidates = [(index, line) for index, line in enumerate(lines) if line.startswith(prefix)]

    if len(candidates) != 1:
        raise SystemExit(
            f"CHANGELOG.md must contain exactly one heading for {version}; "
            f"found {len(candidates)}"
        )

    index, heading = candidates[0]
    pattern = re.compile(
        rf"^## \[{re.escape(version)}\] - (?P<release_date>\d{{4}}-\d{{2}}-\d{{2}})$"
    )
    match = pattern.fullmatch(heading)
    if match is None:
        raise SystemExit(
            f"CHANGELOG.md heading must be exactly: "
            f"## [{version}] - YYYY-MM-DD"
        )

    release_date = match.group("release_date")
    try:
        date.fromisoformat(release_date)
    except ValueError as exc:
        raise SystemExit(
            f"CHANGELOG.md has an invalid ISO release date for {version}: "
            f"{release_date}"
        ) from exc

    return lines, index, release_date


def extract_notes(version: str, output: Path) -> None:
    lines, index, _ = load_entry(version)

    end = len(lines)
    reference_definition = re.compile(r"^\[[^\]]+\]:\s+\S")
    for current in range(index + 1, len(lines)):
        line = lines[current]
        if line.startswith("## ") or reference_definition.match(line):
            end = current
            break

    notes = lines[index + 1 : end]
    while notes and not notes[0].strip():
        notes.pop(0)
    while notes and not notes[-1].strip():
        notes.pop()

    if not any(line.strip() for line in notes):
        raise SystemExit(f"Release notes for {version} are missing or empty")

    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text("\n".join(notes) + "\n", encoding="utf-8")


def main() -> None:
    if len(sys.argv) < 3:
        raise SystemExit(
            "Usage: scripts/changelog-entry.py "
            "<date|extract> <SemVer> [output-file]"
        )

    command = sys.argv[1]
    version = sys.argv[2]

    if command == "date":
        if len(sys.argv) != 3:
            raise SystemExit(
                "Usage: scripts/changelog-entry.py date <SemVer>"
            )
        _, _, release_date = load_entry(version)
        print(release_date)
        return

    if command == "extract":
        if len(sys.argv) != 4:
            raise SystemExit(
                "Usage: scripts/changelog-entry.py "
                "extract <SemVer> <output-file>"
            )
        extract_notes(version, Path(sys.argv[3]))
        return

    raise SystemExit(f"Unknown command: {command}")


if __name__ == "__main__":
    main()
