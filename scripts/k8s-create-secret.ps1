param(
    [string]$Namespace = 'tienda-generica',
    [string]$EnvFile = '.env'
)

$ErrorActionPreference = 'Stop'
Set-Location (Split-Path -Parent $PSScriptRoot)

if (-not (Test-Path $EnvFile)) {
    throw "No se encontro el archivo $EnvFile"
}

$required = @('JWT_SECRET', 'DB_PASSWORD', 'POSTGRES_PASSWORD', 'CONFIG_PASSWORD', 'ADMIN_INITIAL_PASSWORD')
$values = @{}

Get-Content -Path $EnvFile | ForEach-Object {
    $line = $_.Trim()
    if ([string]::IsNullOrWhiteSpace($line)) { return }
    if ($line.StartsWith('#')) { return }
    $parts = $line -split '=', 2
    if ($parts.Length -eq 2) {
        $values[$parts[0].Trim()] = $parts[1].Trim()
    }
}

foreach ($key in $required) {
    if (-not $values.ContainsKey($key)) {
        throw "Falta la variable $key en $EnvFile"
    }
}

kubectl apply -f .\infra\k8s\namespace.yaml

$secretArgs = @('create', 'secret', 'generic', 'tienda-generica-secrets', '-n', $Namespace, '--dry-run=client', '-o', 'yaml')
foreach ($key in $required) {
    $secretArgs += "--from-literal=$key=$($values[$key])"
}

$secretYaml = & kubectl @secretArgs
$secretYaml | kubectl apply -f -
