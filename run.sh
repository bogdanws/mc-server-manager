#!/usr/bin/env bash
set -euo pipefail

OUT_DIR="out"
MAIN_CLASS="ws.bogdan.mcserver.Main"

if [ ! -d "$OUT_DIR" ] || [ -z "$(find "$OUT_DIR" -name '*.class' -print -quit 2>/dev/null)" ]; then
    echo "ERROR: No compiled classes found. Run ./compile.sh first."
    exit 1
fi

java -cp "$OUT_DIR" "$MAIN_CLASS" "$@"
