param(
    [string]$Profile = 'minikube',
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

$status = & minikube -p $Profile status --output=json | ConvertFrom-Json
if ($status.Host -ne 'Running') {
    throw "Minikube profile '$Profile' no esta corriendo. Inicia Minikube antes de construir imagenes."
}

$serviceBuildMap = @{
    'config-server' = @{ Dockerfile = 'services\config-server\Dockerfile'; Image = 'tienda-generica-config-server:latest' }
    'user-service' = @{ Dockerfile = 'services\user-service\Dockerfile'; Image = 'tienda-generica-user-service:latest' }
    'catalog-service' = @{ Dockerfile = 'services\catalog-service\Dockerfile'; Image = 'tienda-generica-catalog-service:latest' }
    'inventory-service' = @{ Dockerfile = 'services\inventory-service\Dockerfile'; Image = 'tienda-generica-inventory-service:latest' }
    'cart-service' = @{ Dockerfile = 'services\cart-service\Dockerfile'; Image = 'tienda-generica-cart-service:latest' }
    'order-service' = @{ Dockerfile = 'services\order-service\Dockerfile'; Image = 'tienda-generica-order-service:latest' }
    'payment-service' = @{ Dockerfile = 'services\payment-service\Dockerfile'; Image = 'tienda-generica-payment-service:latest' }
    'notification-service' = @{ Dockerfile = 'services\notification-service\Dockerfile'; Image = 'tienda-generica-notification-service:latest' }
    'admin-service' = @{ Dockerfile = 'services\admin-service\Dockerfile'; Image = 'tienda-generica-admin-service:latest' }
    'customer-service' = @{ Dockerfile = 'services\customer-service\Dockerfile'; Image = 'tienda-generica-customer-service:latest' }
    'supplier-service' = @{ Dockerfile = 'services\supplier-service\Dockerfile'; Image = 'tienda-generica-supplier-service:latest' }
    'gateway' = @{ Dockerfile = 'services\gateway\Dockerfile'; Image = 'tienda-generica-gateway:latest' }
    'tienda-frontend' = @{ Dockerfile = 'frontend\tienda-frontend\Dockerfile'; Image = 'tienda-generica-frontend:latest' }
}

foreach ($service in $Services) {
    if (-not $serviceBuildMap.ContainsKey($service)) {
        throw "Servicio no soportado para build en Minikube: $service"
    }
}

Write-Host "Construyendo imagenes dentro del runtime de Minikube ($Profile)..."
foreach ($service in $Services) {
    $cfg = $serviceBuildMap[$service]
    Write-Host "Building $service -> $($cfg.Image)"
    & minikube -p $Profile image build . -f $cfg.Dockerfile -t $cfg.Image
    if ($LASTEXITCODE -ne 0) {
        throw "Fallo construccion de imagen para $service (exit code $LASTEXITCODE)"
    }
    $present = & minikube -p $Profile image ls | Select-String -SimpleMatch $cfg.Image
    if (-not $present) {
        throw "Minikube no registro la imagen $($cfg.Image) para $service"
    }
}
