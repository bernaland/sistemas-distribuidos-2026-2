"""Report Java method bodies longer than the AGENTS.md limit."""
from pathlib import Path
import re

root = Path(__file__).resolve().parents[1]
pattern = re.compile(r'^\s*(?:public|private|protected)\s+(?:(?:static|final|synchronized)\s+)*(?:[\w<>?,\[\].]+\s+)?\w+\s*\([^;]*?\)\s*(?:throws[^\{]+)?\{', re.M)
violations = []
for path in (root / 'services').glob('*/src/main/java/**/*.java'):
    text = path.read_text(encoding='utf-8')
    for match in pattern.finditer(text):
        start = match.end() - 1
        depth, end = 1, start + 1
        while depth and end < len(text):
            depth += (text[end] == '{') - (text[end] == '}')
            end += 1
        size = text[start:end].count('\n') + 1
        if size > 20:
            line = text[:match.start()].count('\n') + 1
            violations.append(f'{path.relative_to(root)}:{line}: {size} lines')
print('\n'.join(violations) if violations else 'Java method body length check: OK')
raise SystemExit(bool(violations))
