#!/usr/bin/env bash
set -euo pipefail

case "${1:-}" in
    clean)
        echo "Cleaning ..."
        mvn clean
        ;;
    compile|"")
        echo "Compiling ..."
        mvn compile
        ;;
    *)
        echo "Usage: $0 [clean|compile]"
        exit 1
        ;;
esac
