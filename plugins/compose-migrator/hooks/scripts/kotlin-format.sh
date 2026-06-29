#!/usr/bin/env bash
# PostToolUse hook: format the edited Kotlin file if a formatter is on PATH.
# No-ops (exit 0) when no formatter is available, so it never breaks a project.
set -euo pipefail

# Claude Code passes the tool payload as JSON on stdin.
payload="$(cat 2>/dev/null || true)"

# Extract the edited file path (prefer jq; fall back to a grep).
file=""
if command -v jq >/dev/null 2>&1; then
  file="$(printf '%s' "$payload" | jq -r '.tool_input.file_path // empty' 2>/dev/null || true)"
fi
if [ -z "$file" ]; then
  file="$(printf '%s' "$payload" | grep -oE '"file_path"[[:space:]]*:[[:space:]]*"[^"]+"' | head -1 | sed -E 's/.*"file_path"[[:space:]]*:[[:space:]]*"([^"]+)".*/\1/' || true)"
fi

# Only act on Kotlin files that exist.
case "$file" in
  *.kt|*.kts) ;;
  *) exit 0 ;;
esac
[ -f "$file" ] || exit 0

if command -v ktlint >/dev/null 2>&1; then
  ktlint -F "$file" >/dev/null 2>&1 || true
elif command -v ktfmt >/dev/null 2>&1; then
  ktfmt "$file" >/dev/null 2>&1 || true
fi

exit 0
