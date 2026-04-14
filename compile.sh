#!/usr/bin/env bash
set -euo pipefail

OUT_DIR="out"
SRC_DIR="src"
SOURCES_FILE="sources.txt"

find_sources() {
    echo "Scanning ${SRC_DIR} for .java files ..."
    find "$SRC_DIR" -name "*.java" | sort > "$SOURCES_FILE"
}

compile() {
    mkdir -p "$OUT_DIR"
    echo "Compiling to ${OUT_DIR} ..."
    javac -d "$OUT_DIR" "@$SOURCES_FILE"
}

clean() {
    echo "Cleaning ${OUT_DIR} ..."
    rm -rf "$OUT_DIR"
    rm -f "$SOURCES_FILE"
}

case "${1:-}" in
    clean)
        clean
        ;;
    compile|"")
        if [ ! -f "$SOURCES_FILE" ]; then
            find_sources
        fi
        compile
        ;;
    *)
        echo "Usage: $0 [clean|compile]"
        exit 1
        ;;
esac
