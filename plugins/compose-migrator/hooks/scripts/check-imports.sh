#!/usr/bin/env bash
# PostToolUse hook: warn when a common Compose/M3 symbol is used in an edited Kotlin
# file without a matching import. A heuristic safety net for projects with no local
# build — it only WARNS (exit 0), never blocks.
set -euo pipefail

payload="$(cat 2>/dev/null || true)"

file=""
if command -v jq >/dev/null 2>&1; then
  file="$(printf '%s' "$payload" | jq -r '.tool_input.file_path // empty' 2>/dev/null || true)"
fi
if [ -z "$file" ]; then
  file="$(printf '%s' "$payload" | grep -oE '"file_path"[[:space:]]*:[[:space:]]*"[^"]+"' | head -1 | sed -E 's/.*"file_path"[[:space:]]*:[[:space:]]*"([^"]+)".*/\1/' || true)"
fi

case "$file" in
  *.kt) ;;
  *) exit 0 ;;
esac
[ -f "$file" ] || exit 0

# symbol -> a substring that should appear in some import line if the symbol is used.
symbols="Modifier|Composable|LazyColumn|LazyRow|stickyHeader|collectAsStateWithLifecycle|observeAsState|painterResource|stringResource|hiltViewModel|remember|mutableStateOf|Arrangement|Alignment"

missing=""
while IFS='|' read -r sym; do
  [ -z "$sym" ] && continue
  if grep -qE "\\b$sym\\b" "$file" 2>/dev/null; then
    if ! grep -qE "^import .*\\b$sym\$|^import .*\\.$sym\$|^import .*\\.\\*" "$file" 2>/dev/null; then
      missing="$missing $sym"
    fi
  fi
done <<EOF
$(printf '%s' "$symbols" | tr '|' '\n')
EOF

if [ -n "$missing" ]; then
  echo "compose-migrator: possible missing import(s) in $(basename "$file"):$missing" >&2
fi

exit 0
