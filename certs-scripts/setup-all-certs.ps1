param(
    [string]$RootDir = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path,
    [string]$KeytoolPath = "",
    [string]$OpenSslPath = "",
    [string]$StorePassword = "changeit",
    [int]$ValidityDays = 3650,
    [switch]$Force
)

$ErrorActionPreference = "Stop"

function Write-Log {
    param(
        [string]$Level,
        [string]$Message
    )

    Write-Host "$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss') | $($Level.PadRight(5)) | HTTPS_SETUP | $Message"
}

function Invoke-StepScript {
    param(
        [string]$ScriptName,
        [scriptblock]$Command
    )

    Write-Log "INFO" "Starting: $ScriptName"
    & $Command
    Write-Log "INFO" "Finished: $ScriptName"
}

$RootDir = (Resolve-Path $RootDir).Path
$forceArgs = @()
if ($Force) {
    $forceArgs += "-Force"
}

Write-Log "INFO" "Starting complete HTTPS certificate setup."
Write-Log "INFO" "Root directory: $RootDir"

Invoke-StepScript "create-keystores.ps1" {
    & (Join-Path $PSScriptRoot "create-keystores.ps1") `
        -RootDir $RootDir `
        -KeytoolPath $KeytoolPath `
        -StorePassword $StorePassword `
        -ValidityDays $ValidityDays `
        @forceArgs
}

Invoke-StepScript "create-ws-truststore.ps1" {
    & (Join-Path $PSScriptRoot "create-ws-truststore.ps1") `
        -RootDir $RootDir `
        -KeytoolPath $KeytoolPath `
        -StorePassword $StorePassword `
        @forceArgs
}

Invoke-StepScript "create-psp-truststore.ps1" {
    & (Join-Path $PSScriptRoot "create-psp-truststore.ps1") `
        -RootDir $RootDir `
        -KeytoolPath $KeytoolPath `
        -StorePassword $StorePassword `
        @forceArgs
}

Invoke-StepScript "create-plugin-truststore.ps1" {
    & (Join-Path $PSScriptRoot "create-plugin-truststore.ps1") `
        -RootDir $RootDir `
        -KeytoolPath $KeytoolPath `
        -StorePassword $StorePassword `
        @forceArgs
}

Invoke-StepScript "create-bank-truststore.ps1" {
    & (Join-Path $PSScriptRoot "create-bank-truststore.ps1") `
        -RootDir $RootDir `
        -KeytoolPath $KeytoolPath `
        -StorePassword $StorePassword `
        @forceArgs
}

Invoke-StepScript "create-nginx-cert.ps1" {
    & (Join-Path $PSScriptRoot "create-nginx-cert.ps1") `
        -RootDir $RootDir `
        -OpenSslPath $OpenSslPath `
        -ValidityDays $ValidityDays `
        @forceArgs
}

Write-Log "INFO" "Complete HTTPS certificate setup finished successfully."
Write-Log "INFO" "Use truststore.p12 in VM options for ws-back, psp-back, bank-payment-plugin and bank-back."
