param(
    [string]$RootDir = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path,
    [string]$OpenSslPath = "",
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

function Ensure-Directory {
    param([string]$Path)

    if (-not (Test-Path $Path)) {
        New-Item -ItemType Directory -Path $Path | Out-Null
        Write-Log "INFO" "Created directory: $Path"
    }
}

function Assert-FileExists {
    param(
        [string]$Path,
        [string]$Description
    )

    if (-not (Test-Path $Path)) {
        throw "$Description not found: $Path"
    }
}

function Resolve-OpenSslPath {
    param([string]$ConfiguredPath)

    if (-not [string]::IsNullOrWhiteSpace($ConfiguredPath)) {
        Assert-FileExists $ConfiguredPath "openssl.exe"
        return (Resolve-Path $ConfiguredPath).Path
    }

    $command = Get-Command openssl -ErrorAction SilentlyContinue
    if ($command) {
        Write-Log "INFO" "Using openssl from PATH: $($command.Source)"
        return $command.Source
    }

    $candidatePaths = @(
        "C:\Program Files\Git\usr\bin\openssl.exe",
        "C:\Program Files\OpenSSL-Win64\bin\openssl.exe",
        "C:\Program Files\OpenSSL-Win32\bin\openssl.exe"
    )

    foreach ($candidatePath in $candidatePaths) {
        if (Test-Path $candidatePath) {
            Write-Log "INFO" "Using detected openssl: $candidatePath"
            return (Resolve-Path $candidatePath).Path
        }
    }

    throw "openssl.exe was not found. Pass -OpenSslPath 'path\to\openssl.exe'."
}

$RootDir = (Resolve-Path $RootDir).Path
$OpenSslPath = Resolve-OpenSslPath $OpenSslPath

$NginxCertDirectory = Join-Path $RootDir "docker\nginx\certs"
$NginxKeyPath = Join-Path $NginxCertDirectory "nginx.key"
$NginxCertPath = Join-Path $NginxCertDirectory "nginx.crt"

Write-Log "INFO" "Root directory: $RootDir"
Write-Log "INFO" "OpenSSL path: $OpenSslPath"

Ensure-Directory $NginxCertDirectory

if (((Test-Path $NginxKeyPath) -or (Test-Path $NginxCertPath)) -and (-not $Force)) {
    Write-Log "INFO" "Skipping nginx certificate because nginx.crt/nginx.key already exist in: $NginxCertDirectory"
    return
}

if ($Force) {
    if (Test-Path $NginxKeyPath) {
        Remove-Item $NginxKeyPath -Force
        Write-Log "INFO" "Removed existing nginx key: $NginxKeyPath"
    }

    if (Test-Path $NginxCertPath) {
        Remove-Item $NginxCertPath -Force
        Write-Log "INFO" "Removed existing nginx certificate: $NginxCertPath"
    }
}

Write-Log "INFO" "Creating nginx certificate and key in: $NginxCertDirectory"

& $OpenSslPath req -x509 -nodes -days $ValidityDays -newkey rsa:2048 `
    -keyout $NginxKeyPath `
    -out $NginxCertPath `
    -subj "/C=RS/ST=Serbia/L=Novi Sad/O=SEP/OU=SEP/CN=localhost" `
    -addext "subjectAltName=DNS:localhost,DNS:ws-nginx,IP:127.0.0.1"

if ($LASTEXITCODE -ne 0) {
    throw "Failed to create nginx certificate."
}

Write-Log "INFO" "Created nginx.crt and nginx.key."
