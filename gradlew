#!/bin/sh
DIR="$(cd "$(dirname "$0")" && pwd)"
exec gradle --project-dir "$DIR" "$@"
