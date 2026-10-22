import os
import re
import json

services_dir = 'services'

entities = []
controllers = []
http_clients = []

for dp, dn, fn in os.walk(services_dir):
    for f in fn:
        if not f.endswith('.java'):
            continue
        filepath = os.path.join(dp, f).replace('\\', '/')
        with open(filepath, 'r', encoding='utf-8') as jf:
            content = jf.read()
        
        # Check Entity
        if '@Entity' in content:
            table_m = re.search(r'@Table\s*\(\s*name\s*=\s*"([^"]+)"', content)
            table_name = table_m.group(1) if table_m else f.replace('.java', '').lower()
            
            # find fields
            fields = []
            for line in content.split('\n'):
                line_str = line.strip()
                if line_str.startswith('private ') and ';' in line_str and '(' not in line_str:
                    clean_field = line_str.replace('private ', '').replace(';', '').strip()
                    fields.append(clean_field)
            
            entities.append({
                'file': filepath,
                'class': f.replace('.java', ''),
                'table': table_name,
                'fields': fields
            })

        # Check Controller
        if '@RestController' in content or '@Controller' in content:
            base_mapping_m = re.search(r'@RequestMapping\s*\(\s*(?:value\s*=\s*)?"([^"]+)"', content)
            base_mapping = base_mapping_m.group(1) if base_mapping_m else ''
            
            endpoints = []
            method_matches = re.finditer(r'@(GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping)(?:\s*\(\s*(?:value\s*=\s*)?(?:"([^"]*)")?.*?\))?', content)
            for m in method_matches:
                verb = m.group(1).replace('Mapping', '').upper()
                subpath = m.group(2) if m.group(2) is not None else ''
                full_path = (base_mapping + subpath).replace('//', '/')
                endpoints.append(f'{verb} {full_path}')
            
            controllers.append({
                'file': filepath,
                'class': f.replace('.java', ''),
                'base': base_mapping,
                'endpoints': endpoints
            })

        # Check Outbound HTTP Calls
        if 'RestTemplate' in content or 'WebClient' in content or 'HttpURLConnection' in content:
            # find target service URLs
            urls = re.findall(r'(?:SERVICES_[A-Z]+|USER_SERVICE_URL|CATALOG_SERVICE_URL|http://[^\s"]+)', content)
            http_clients.append({
                'file': filepath,
                'class': f.replace('.java', ''),
                'urls': list(set(urls))
            })

with open('ANALISIS/arch_data.json', 'w', encoding='utf-8') as out:
    json.dump({
        'entities': entities,
        'controllers': controllers,
        'http_clients': http_clients
    }, out, indent=2)

print(f'Extracted {len(entities)} entities, {len(controllers)} controllers, {len(http_clients)} HTTP clients.')
