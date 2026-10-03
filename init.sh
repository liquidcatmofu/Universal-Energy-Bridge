#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 3 ]]; then
    echo 'Usage: ./init.sh <mod_id> "<Mod Name>" <java.package>' >&2
    echo 'Example: ./init.sh example_tools "Example Tools" dev.liquidcatmofu.exampletools' >&2
    exit 2
fi

command -v python3 >/dev/null 2>&1 || {
    echo "python3 is required." >&2
    exit 1
}

python3 - "$1" "$2" "$3" <<'PY'
from __future__ import annotations

import re
import shutil
import sys
from pathlib import Path

mod_id, mod_name, package = sys.argv[1:4]

java_reserved = {
    "_", "abstract", "assert", "boolean", "break", "byte", "case", "catch",
    "char", "class", "const", "continue", "default", "do", "double", "else",
    "enum", "extends", "false", "final", "finally", "float", "for", "goto",
    "if", "implements", "import", "instanceof", "int", "interface", "long",
    "native", "new", "null", "package", "private", "protected", "public",
    "return", "short", "static", "strictfp", "super", "switch", "synchronized",
    "this", "throw", "throws", "transient", "true", "try", "void", "volatile",
    "while", "record", "sealed", "permits", "var", "yield",
}

if not re.fullmatch(r"[a-z][a-z0-9_]{0,63}", mod_id):
    raise SystemExit("mod_id must match [a-z][a-z0-9_]{0,63}")

if not re.fullmatch(r"[a-z_][a-z0-9_]*(?:\.[a-z_][a-z0-9_]*)+", package):
    raise SystemExit("java.package must be a lowercase dotted Java package")

segments = package.split(".")
reserved_segments = [segment for segment in segments if segment in java_reserved]
if reserved_segments:
    raise SystemExit(
        "java.package contains reserved Java identifier(s): "
        + ", ".join(reserved_segments)
    )

if "\r" in mod_name or "\n" in mod_name or "\\" in mod_name:
    raise SystemExit("Mod name must not contain line breaks or backslashes")

if not any(character.isalnum() for character in mod_name):
    raise SystemExit("Mod name must contain at least one alphanumeric character")

def class_part(word: str) -> str:
    return word if word.isupper() else word[:1].upper() + word[1:]

words = re.findall(r"[A-Za-z0-9]+", mod_name)
if words:
    main_class = "".join(class_part(word) for word in words)
else:
    # Display names may be entirely non-ASCII. Java identifiers remain
    # predictable by falling back to the already-validated mod_id.
    id_words = [word for word in mod_id.split("_") if word]
    main_class = "".join(class_part(word) for word in id_words)
if main_class[0].isdigit():
    main_class = "Mod" + main_class
if main_class.lower() in java_reserved:
    main_class += "Mod"

root = Path.cwd()
old_package = "dev.liquidcatmofu.examplemod"
old_package_path = Path(*old_package.split("."))
new_package_path = Path(*package.split("."))

old_java_dir = root / "src/main/java" / old_package_path
new_java_dir = root / "src/main/java" / new_package_path
old_main = old_java_dir / "ExampleMod.java"
prospective_main = new_java_dir / f"{main_class}.java"

old_mixin = root / "src/main/resources/examplemod.mixins.json"
new_mixin = root / f"src/main/resources/{mod_id}.mixins.json"

scaffold = root / ".template"
template_ci = root / ".github/workflows/template-ci.yml"

required = [
    root / "gradle.properties",
    root / "build.gradle",
    root / "src/main/resources/META-INF/mods.toml",
    old_main,
    old_mixin,
    scaffold / "README.md",
    scaffold / "README_ja.md",
    template_ci,
]
missing = [str(path.relative_to(root)) for path in required if not path.is_file()]
if missing:
    raise SystemExit(
        "Template is incomplete; missing required file(s): " + ", ".join(missing)
    )

if old_java_dir != new_java_dir and new_java_dir.exists():
    raise SystemExit(f"Destination package already exists: {new_java_dir}")

if prospective_main != old_main and prospective_main.exists():
    raise SystemExit(f"Destination main class already exists: {prospective_main}")

if new_mixin != old_mixin and new_mixin.exists():
    raise SystemExit(f"Destination Mixin config already exists: {new_mixin}")

replacements = (
    ("Example Mod", mod_name),
    ("ExampleMod", main_class),
    ("examplemod", mod_id),
)

skip_dirs = {".git", ".gradle", "build", "run"}
skip_paths = {
    Path("init.sh"),
    Path("gradle/wrapper/gradle-wrapper.jar"),
}

planned_updates: list[tuple[Path, str, str]] = []
original_text: dict[Path, str] = {}

for path in root.rglob("*"):
    if not path.is_file():
        continue

    relative = path.relative_to(root)
    if any(part in skip_dirs for part in relative.parts) or relative in skip_paths:
        continue

    try:
        content = path.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        continue

    updated = content.replace(old_package, "__TEMPLATE_JAVA_PACKAGE__")
    for old, new in replacements:
        updated = updated.replace(old, new)
    updated = updated.replace("__TEMPLATE_JAVA_PACKAGE__", package)

    if updated != content:
        planned_updates.append((path, content, updated))
        original_text[path] = content

# README files are overwritten from .template after replacement. Preserve their
# original contents as part of the rollback set even if generic replacement
# would not otherwise touch them.
for path in (root / "README.md", root / "README_ja.md"):
    if path.is_file() and path not in original_text:
        original_text[path] = path.read_text(encoding="utf-8")

package_moved = False
main_renamed = False
mixin_renamed = False

try:
    # No repository mutation happens before all validation, collision checks,
    # and text transformations have been planned successfully.
    for path, _, updated in planned_updates:
        path.write_text(updated, encoding="utf-8")

    if old_java_dir != new_java_dir:
        new_java_dir.parent.mkdir(parents=True, exist_ok=True)
        shutil.move(str(old_java_dir), str(new_java_dir))
        package_moved = True

    current_main = new_java_dir / "ExampleMod.java"
    final_main = new_java_dir / f"{main_class}.java"
    if current_main.exists() and current_main != final_main:
        current_main.rename(final_main)
        main_renamed = True

    if old_mixin.exists() and old_mixin != new_mixin:
        old_mixin.rename(new_mixin)
        mixin_renamed = True

    for name in ("README.md", "README_ja.md"):
        source = scaffold / name
        shutil.copy2(source, root / name)

except Exception:
    if mixin_renamed and new_mixin.exists():
        new_mixin.rename(old_mixin)

    current_main = new_java_dir / f"{main_class}.java"
    if main_renamed and current_main.exists():
        current_main.rename(new_java_dir / "ExampleMod.java")

    if package_moved and new_java_dir.exists():
        old_java_dir.parent.mkdir(parents=True, exist_ok=True)
        shutil.move(str(new_java_dir), str(old_java_dir))

    for path, content in original_text.items():
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(content, encoding="utf-8")
    raise

# Template-only files are removed only after the generated project has been
# written successfully.
shutil.rmtree(scaffold)
template_ci.unlink()
Path("init.sh").unlink()

print(f"Initialized {mod_name}")
print(f"  mod_id:        {mod_id}")
print(f"  package:       {package}")
print(f"  main class:    {main_class}")
print(f"  archives_name: {main_class}")
print("\nNext: ./gradlew clean build")
PY
