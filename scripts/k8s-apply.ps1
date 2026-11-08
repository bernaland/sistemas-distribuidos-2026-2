param(
    [string]$Namespace = 'tienda-generica'
)

$ErrorActionPreference = 'Stop'
Set-Location (Split-Path -Parent $PSScriptRoot)

kubectl apply -f .\infra\k8s\namespace.yaml
kubectl apply -k .\infra\k8s
kubectl -n $Namespace get deployments,statefulsets,services,ingress
