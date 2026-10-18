import os

root = '.'
excluded_dirs = {'.git', '.angular', 'node_modules', 'target', '.idea', 'dist'}

auditable_by_module = {}
excluded_files = []

for dirpath, dirnames, filenames in os.walk(root):
    dirnames[:] = [d for d in dirnames if d not in excluded_dirs]
    norm_dp = os.path.relpath(dirpath, root).replace('\\', '/')
    if norm_dp == '.':
        norm_dp = ''
    
    for f in sorted(filenames):
        p = f if not norm_dp else f'{norm_dp}/{f}'
        if p.startswith('ANALISIS/'):
            continue
        ext = os.path.splitext(f)[1].lower()
        if ext in ['.ico', '.docx', '.png', '.jpg', '.jpeg', '.gif', '.jar', '.class', '.woff', '.woff2', '.ttf']:
            excluded_files.append((p, f'Archivo binario / multimedia no textual ({ext})'))
            continue
        
        parts = p.split('/')
        if parts[0] == 'services':
            if len(parts) > 1 and os.path.isdir(f'services/{parts[1]}') and parts[1] != '.mvn':
                mod = f'services/{parts[1]}'
            else:
                mod = 'services (root/build)'
        elif parts[0] == 'frontend':
            mod = 'frontend/tienda-frontend'
        elif parts[0] == '.agents':
            mod = '.agents'
        elif parts[0] in ['docs', '.copilot', '.vscode', 'scripts', 'infra']:
            mod = parts[0]
        else:
            mod = 'root (archivos de configuración)'
        
        if mod not in auditable_by_module:
            auditable_by_module[mod] = []
        auditable_by_module[mod].append(p)

os.makedirs('ANALISIS', exist_ok=True)

with open('ANALISIS/00_inventario.md', 'w', encoding='utf-8') as out:
    out.write('# Inventario Completo y Checklist de Auditoría Técnica\n\n')
    out.write('## Resumen General del Inventario\n\n')
    total_auditable = sum(len(v) for v in auditable_by_module.values())
    out.write(f'- **Total de archivos auditables en el proyecto:** {total_auditable}\n')
    out.write(f'- **Total de archivos binarios/assets no textuales excluidos:** {len(excluded_files)}\n')
    out.write('- **Directorios de build/cache/dependencias excluidos:** 6 (`.git`, `.angular`, `node_modules`, `dist`, `target`, `.idea`)\n\n')
    
    out.write('### Desglose por Módulo / Directorio\n\n')
    out.write('| Módulo / Directorio | Cantidad de Archivos | Estado |\n')
    out.write('|---|---|---|\n')
    for mod in sorted(auditable_by_module.keys()):
        out.write(f'| `{mod}` | {len(auditable_by_module[mod])} | Pendiente |\n')
    out.write(f'| **TOTAL** | **{total_auditable}** | **Pendiente** |\n\n')

    out.write('## Archivos y Directorios Excluidos de Lectura Directa\n\n')
    out.write('Conforme a la **Regla Estricta 1**, se declara explícitamente qué carpetas y archivos no son leídos como código fuente y se justifica el motivo:\n\n')
    out.write('1. `.git/`: Metadatos internos y base de datos de objetos del sistema de control de versiones Git. No contienen lógica de negocio de la aplicación.\n')
    out.write('2. `frontend/tienda-frontend/.angular/`: Caché efímero interno del compilador y build tool de Angular CLI.\n')
    out.write('3. `frontend/tienda-frontend/node_modules/`: Dependencias de librerías de terceros gestionadas por npm. Se auditan a través de `package.json` y `package-lock.json`.\n')
    out.write('4. `frontend/tienda-frontend/dist/`: Salida compilada (build artifacts minificados/transpilados) de Angular generados a partir de `src/`. Se verifica el proceso de build en la fase correspondiente.\n')
    out.write('5. `**/target/`: Archivos binarios compilados Java (.class, .jar) producidos por Maven. Se audita el código fuente Java en `src/`.\n')
    out.write('6. `.idea/`: Archivos temporales de caché de IDE JetBrains/IntelliJ.\n\n')
    out.write('### Archivos binarios específicos excluidos:\n\n')
    for p, mot in sorted(excluded_files):
        out.write(f'- `{p}`: {mot}\n')
    out.write('\n---\n\n')

    out.write('## Checklist de Auditoría Archivo por Archivo\n\n')
    for mod in sorted(auditable_by_module.keys()):
        out.write(f'### Módulo: `{mod}` ({len(auditable_by_module[mod])} archivos)\n\n')
        for f in sorted(auditable_by_module[mod]):
            out.write(f'- [ ] `{f}`\n')
        out.write('\n')

print(f'Inventario generado: {total_auditable} archivos auditables.')
