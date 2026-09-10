$ErrorActionPreference = 'Stop'
$repoPath = Split-Path -Parent $PSScriptRoot
$envPath = Join-Path $repoPath '.env'
if (Test-Path -LiteralPath $envPath) { Write-Output '.env existente conservado'; exit 0 }
function New-Secret {
    $bytes = New-Object byte[] 32
    $generator = [System.Security.Cryptography.RandomNumberGenerator]::Create()
    $generator.GetBytes($bytes)
    $generator.Dispose()
    return ([BitConverter]::ToString($bytes)).Replace('-', '').ToLowerInvariant()
}
$entries = @('JWT_SECRET', 'DB_PASSWORD', 'POSTGRES_PASSWORD', 'CONFIG_PASSWORD') | ForEach-Object { "$_=$(New-Secret)" }
[System.IO.File]::WriteAllLines($envPath, $entries)
Write-Output '.env creado con claves aleatorias. No lo publiques.'
