"""Check TypeScript function/method body length limit (<= 20 lines) per AGENTS.md."""
from pathlib import Path
import re

root = Path(__file__).resolve().parents[1]
ts_dir = root / 'frontend' / 'tienda-frontend' / 'src'

func_header = re.compile(
    r'(?:(?:export\s+)?(?:default\s+)?(?:async\s+)?function(?:\s+[\w$]+)?\s*\([^)]*\)\s*(?::\s*[^\{]+)?\s*\{|'
    r'(?:(?:public|private|protected|readonly|static|async|get|set)\s+)+(?!(?:if|for|while|switch|catch)\b)[\w$]+\s*\([^)]*\)\s*(?::\s*[^\{]+)?\s*\{|'
    r'(?<![@\w$])(?!(?:if|for|while|switch|catch)\b)[\w$]+\s*\([^)]*\)\s*(?::\s*[^\{]+)?\s*\{|'
    r'constructor\s*\([^)]*\)\s*(?::\s*[^\{]+)?\s*\{)',
    re.M
)

def mask_templates(text: str) -> str:
    def replace_backtick(m):
        return '`' + ('\n' * m.group(0).count('\n')) + '`'
    return re.sub(r'`(?:\\.|[^`\\])*`', replace_backtick, text, flags=re.DOTALL)

violations = []

for path in ts_dir.glob('**/*.ts'):
    if 'node_modules' in path.parts:
        continue
    raw_text = path.read_text(encoding='utf-8')
    text = mask_templates(raw_text)
    for match in func_header.finditer(text):
        brace_idx = text.find('{', match.start())
        if brace_idx == -1 or brace_idx > match.end():
            continue
        depth, end = 1, brace_idx + 1
        while depth > 0 and end < len(text):
            char = text[end]
            if char == '{':
                depth += 1
            elif char == '}':
                depth -= 1
            end += 1
        
        lines = text[brace_idx:end].count('\n') + 1
        if lines > 20:
            line_no = text[:brace_idx].count('\n') + 1
            fn_sig = match.group(0).strip().replace('\n', ' ')
            rel_path = path.relative_to(root)
            violations.append(f"{rel_path}:{line_no}: {lines} lines -> {fn_sig}")

if violations:
    print(f"FAILED: Found {len(violations)} function(s) exceeding 20 lines:")
    for v in violations:
        print(f"  {v}")
    raise SystemExit(1)
else:
    print("TypeScript function length check: OK (all functions <= 20 lines)")
    raise SystemExit(0)
