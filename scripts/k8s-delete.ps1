param(
    [string]$Namespace = 'tienda-generica'
)

$ErrorActionPreference = 'Stop'
Set-Location (Split-Path -Parent $PSScriptRoot)

kubectl delete -k .\infra\k8s --ignore-not-found=true
kubectl delete namespace $Namespace --ignore-not-found=true
