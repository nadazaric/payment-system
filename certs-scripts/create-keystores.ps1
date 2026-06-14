param(
    [string]$RootDir = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path,
    [string]$KeytoolPath = "",
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

function Resolve-KeytoolPath {
    param([string]$ConfiguredPath)

    if (-not [string]::IsNullOrWhiteSpace($ConfiguredPath)) {
        Assert-FileExists $ConfiguredPath "keytool.exe"
        return (Resolve-Path $ConfiguredPath).Path
    }

    $command = Get-Command keytool -ErrorAction SilentlyContinue
    if ($command) {
        Write-Log "INFO" "Using keytool from PATH: $($command.Source)"
        return $command.Source
    }

    if (-not [string]::IsNullOrWhiteSpace($env:JAVA_HOME)) {
        $javaHomeCandidate = Join-Path $env:JAVA_HOME "bin\keytool.exe"
        if (Test-Path $javaHomeCandidate) {
            Write-Log "INFO" "Using keytool from JAVA_HOME: $javaHomeCandidate"
            return (Resolve-Path $javaHomeCandidate).Path
        }
    }

    $candidateRoots = @(
        "C:\Program Files\Java",
        "C:\Program Files\Eclipse Adoptium",
        "C:\Program Files\JetBrains"
    )

    foreach ($candidateRoot in $candidateRoots) {
        if (Test-Path $candidateRoot) {
            $candidate = Get-ChildItem $candidateRoot -Recurse -Filter keytool.exe -ErrorAction SilentlyContinue |
                    Sort-Object LastWriteTime -Descending |
                    Select-Object -First 1

            if ($candidate) {
                Write-Log "INFO" "Using detected keytool: $($candidate.FullName)"
                return $candidate.FullName
            }
        }
    }

    throw "keytool.exe was not found. Pass -KeytoolPath 'path\to\keytool.exe'."
}

function New-ServiceKeystore {
    param(
        [string]$ServiceName,
        [string]$KeystorePath,
        [string]$Alias,
        [string]$DockerDnsName,
        [string]$Keytool
    )

    $certDirectory = Split-Path $KeystorePath -Parent
    Ensure-Directory $certDirectory

    if ((Test-Path $KeystorePath) -and (-not $Force)) {
        Write-Log "INFO" "Skipping $ServiceName keystore because it already exists: $KeystorePath"
        return
    }

    if ((Test-Path $KeystorePath) -and $Force) {
        Remove-Item $KeystorePath -Force
        Write-Log "INFO" "Removed existing $ServiceName keystore: $KeystorePath"
    }

    Write-Log "INFO" "Creating $ServiceName keystore: $KeystorePath"

    & $Keytool -genkeypair `
        -alias $Alias `
        -keyalg RSA `
        -keysize 2048 `
        -storetype PKCS12 `
        -keystore $KeystorePath `
        -validity $ValidityDays `
        -storepass $StorePassword `
        -keypass $StorePassword `
        -dname "CN=localhost, OU=SEP, O=SEP, L=Novi Sad, ST=Serbia, C=RS" `
        -ext "SAN=dns:localhost,ip:127.0.0.1,dns:$DockerDnsName"

    if ($LASTEXITCODE -ne 0) {
        throw "Failed to create $ServiceName keystore."
    }

    Write-Log "INFO" "Created $ServiceName keystore with alias=$Alias and SAN=localhost,127.0.0.1,$DockerDnsName"
}

$RootDir = (Resolve-Path $RootDir).Path
$KeytoolPath = Resolve-KeytoolPath $KeytoolPath

Write-Log "INFO" "Root directory: $RootDir"
Write-Log "INFO" "Keytool path: $KeytoolPath"

$keystores = @(
    @{
        ServiceName = "ws-back"
        KeystorePath = Join-Path $RootDir "web-shop\back\src\main\resources\certs\keystore.p12"
        Alias = "wsbackend"
        DockerDnsName = "ws-back"
    },
    @{
        ServiceName = "psp-back"
        KeystorePath = Join-Path $RootDir "psp\back\src\main\resources\certs\keystore.p12"
        Alias = "pspbackend"
        DockerDnsName = "psp-back"
    },
    @{
        ServiceName = "bank-payment-plugin"
        KeystorePath = Join-Path $RootDir "psp\plugin\bank-payment-plugin\src\main\resources\certs\keystore.p12"
        Alias = "bankpaymentplugin"
        DockerDnsName = "bank-payment-plugin"
    },
    @{
        ServiceName = "bank-back"
        KeystorePath = Join-Path $RootDir "bank\back\src\main\resources\certs\keystore.p12"
        Alias = "bankbackend"
        DockerDnsName = "bank-back"
    }
)

foreach ($keystore in $keystores) {
    New-ServiceKeystore `
        -ServiceName $keystore.ServiceName `
        -KeystorePath $keystore.KeystorePath `
        -Alias $keystore.Alias `
        -DockerDnsName $keystore.DockerDnsName `
        -Keytool $KeytoolPath
}

Write-Log "INFO" "All service keystores are ready."
