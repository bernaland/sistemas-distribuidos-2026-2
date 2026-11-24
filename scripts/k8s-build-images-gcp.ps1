param(
    [string]$Registry = 'us-east4-docker.pkg.dev/project-f32623f8-beb2-4c0a-9c5/sistemas-distribuidos',
    [string[]]$Services = @(
        'config-server',
        'user-service',
        'catalog-service',
        'inventory-service',
        'cart-service',
        'order-service',
        'payment-service',
        'notification-service',
        'admin-service',
        'customer-service',
        'supplier-service',
        'gateway',
        'tienda-frontend'
    )
)

$ErrorActionPreference = 'Stop'
Set-Location (Split-Path -Parent $PSScriptRoot)

$serviceBuildMap = @{
    'config-server' = @{ Dockerfile = 'services\config-server\Dockerfile'; Image = 'config-server' }
    'user-service' = @{ Dockerfile = 'services\user-service\Dockerfile'; Image = 'user-service' }
    'catalog-service' = @{ Dockerfile = 'services\catalog-service\Dockerfile'; Image = 'catalog-service' }
    'inventory-service' = @{ Dockerfile = 'services\inventory-service\Dockerfile'; Image = 'inventory-service' }
    'cart-service' = @{ Dockerfile = 'services\cart-service\Dockerfile'; Image = 'cart-service' }
    'order-service' = @{ Dockerfile = 'services\order-service\Dockerfile'; Image = 'order-service' }
    'payment-service' = @{ Dockerfile = 'services\payment-service\Dockerfile'; Image = 'payment-service' }
    'notification-service' = @{ Dockerfile = 'services\notification-service\Dockerfile'; Image = 'notification-service' }
    'admin-service' = @{ Dockerfile = 'services\admin-service\Dockerfile'; Image = 'admin-service' }
    'customer-service' = @{ Dockerfile = 'services\customer-service\Dockerfile'; Image = 'customer-service' }
    'supplier-service' = @{ Dockerfile = 'services\supplier-service\Dockerfile'; Image = 'supplier-service' }
    'gateway' = @{ Dockerfile = 'services\gateway\Dockerfile'; Image = 'gateway' }
    'tienda-frontend' = @{ Dockerfile = 'frontend\tienda-frontend\Dockerfile'; Image = 'frontend' }
}

foreach ($service in $Services) {
    if (-not $serviceBuildMap.ContainsKey($service)) {
        throw "Servicio no soportado para build en Artifact Registry: $service"
    }
}

Write-Host "Construyendo y publicando imagenes en $Registry..."
foreach ($service in $Services) {
    $cfg = $serviceBuildMap[$service]
    $image = "$Registry/$($cfg.Image):latest"
    Write-Host "Building and pushing $service -> $image"
    & docker build -f $cfg.Dockerfile -t $image .
    if ($LASTEXITCODE -ne 0) {
        throw "Fallo construccion de imagen para $service (exit code $LASTEXITCODE)"
    }
    & docker push $image
    if ($LASTEXITCODE -ne 0) {
        throw "Fallo publicacion de imagen para $service (exit code $LASTEXITCODE)"
    }
}
