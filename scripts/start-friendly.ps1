param(
    [switch]$Build,
    [ValidateSet(
        'all',
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
    )]
    [string]$Service = 'all',
    [ValidateRange(1, 5)]
    [int]$Replicas = 2
)

$ErrorActionPreference = 'Stop'
Set-Location (Split-Path -Parent $PSScriptRoot)

$serviceOrder = @(
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

$lbByService = @{
    'config-server' = 'config-server-lb'
    'user-service' = 'user-service-lb'
    'catalog-service' = 'catalog-service-lb'
    'inventory-service' = 'inventory-service-lb'
    'cart-service' = 'cart-service-lb'
    'order-service' = 'order-service-lb'
    'payment-service' = 'payment-service-lb'
    'notification-service' = 'notification-service-lb'
    'admin-service' = 'admin-service-lb'
    'customer-service' = 'customer-service-lb'
    'supplier-service' = 'supplier-service-lb'
    'gateway' = 'gateway-lb'
    'tienda-frontend' = 'tienda-frontend-lb'
}

$composeArgs = @('up', '-d')
if ($Build) { $composeArgs += '--build' }

if ($Service -eq 'all') {
    foreach ($svc in $serviceOrder) {
        $composeArgs += '--scale'
        $composeArgs += "$svc=$Replicas"
    }
    $targets = @('postgres')
    foreach ($svc in $serviceOrder) {
        $targets += $svc
        $targets += $lbByService[$svc]
    }
}
else {
    $composeArgs += '--scale'
    $composeArgs += "$Service=$Replicas"
    $targets = @($Service, $lbByService[$Service])
}

$composeArgs += $targets

Write-Host "Running: docker compose $($composeArgs -join ' ')"
& docker compose @composeArgs
if (-not $?) {
    throw "Docker Compose command failed."
}

docker compose ps
