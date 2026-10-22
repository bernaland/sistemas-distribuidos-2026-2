import sys

def mark_checked(files_to_check):
    inv_path = 'ANALISIS/00_inventario.md'
    with open(inv_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    updated_lines = []
    checked_set = set(files_to_check)
    count = 0
    
    for line in lines:
        matched = False
        for f in checked_set:
            if f'- [ ] `{f}`' in line:
                updated_lines.append(line.replace('- [ ]', '- [x]'))
                matched = True
                count += 1
                break
        if not matched:
            updated_lines.append(line)
            
    with open(inv_path, 'w', encoding='utf-8') as f:
        f.writelines(updated_lines)
    
    print(f'Marcados como revisados en 00_inventario.md: {count} archivos.')

if __name__ == '__main__':
    files = [
        '.dockerignore',
        '.env.example',
        '.gitignore',
        'AGENTS.md',
        'README.md',
        'REQUERIMIENTOS_MICROSERVICIOS.md',
        'compose.yml',
        'skills-lock.json',
        'infra/init-databases.sh',
        'scripts/check-java-functions.py',
        'scripts/setup-env.ps1',
        '.vscode/settings.json',
        'docs/BACKEND.md',
        'docs/REQUISITOS_BACKEND.md',
        'docs/VALIDACION_BACKEND.md',
        'docs/superpowers/plans/2026-09-06-microservicios.md',
        '.copilot/instructions/angular.instructions.md',
        '.copilot/instructions/java.instructions.md',
        '.copilot/instructions/postgreSql.instructions.md'
    ]
    mark_checked(files)
