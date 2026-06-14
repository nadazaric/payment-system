param(
    [string]$RootDir = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path,
    [string]$KeytoolPath = "",
    [string]$StorePassword = "changeit",
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

function Remove-AliasIfExists {
    param(
        [string]$TruststorePath,
        [string]$Alias,
        [string]$Keytool
    )

    if (-not (Test-Path $TruststorePath)) {
        return
    }

    & $Keytool -list `
        -keystore $TruststorePath `
        -storepass $StorePassword `
        -storetype PKCS12 `
        -alias $Alias *> $null

    if ($LASTEXITCODE -eq 0) {
        & $Keytool -delete `
            -alias $Alias `
            -keystore $TruststorePath `
            -storepass $StorePassword `
            -storetype PKCS12

        if ($LASTEXITCODE -ne 0) {
            throw "Failed to remove existing alias=$Alias from truststore=$TruststorePath."
        }

        Write-Log "INFO" "Removed existing alias=$Alias from truststore: $TruststorePath"
    }
}

function Import-CertificateToTruststore {
    param(
        [string]$SourceKeystorePath,
        [string]$SourceAlias,
        [string]$CertificateFile,
        [string]$TruststorePath,
        [string]$TrustAlias,
        [string]$Keytool
    )

    Assert-FileExists $SourceKeystorePath "source keystore"
    Ensure-Directory (Split-Path $CertificateFile -Parent)
    Ensure-Directory (Split-Path $TruststorePath -Parent)

    Write-Log "INFO" "Exporting certificate alias=$SourceAlias from $SourceKeystorePath"
    & $Keytool -exportcert `
        -alias $SourceAlias `
        -keystore $SourceKeystorePath `
        -storepass $StorePassword `
        -storetype PKCS12 `
        -file $CertificateFile

    if ($LASTEXITCODE -ne 0) {
        throw "Failed to export certificate alias=$SourceAlias from $SourceKeystorePath."
    }

    Remove-AliasIfExists -TruststorePath $TruststorePath -Alias $TrustAlias -Keytool $Keytool

    Write-Log "INFO" "Importing certificate alias=$TrustAlias into $TruststorePath"
    & $Keytool -importcert `
        -alias $TrustAlias `
        -file $CertificateFile `
        -keystore $TruststorePath `
        -storepass $StorePassword `
        -storetype PKCS12 `
        -noprompt

    if ($LASTEXITCODE -ne 0) {
        throw "Failed to import certificate alias=$TrustAlias into $TruststorePath."
    }

    Remove-Item $CertificateFile -Force
    Write-Log "INFO" "Imported alias=$TrustAlias into truststore: $TruststorePath"
}

$RootDir = (Resolve-Path $RootDir).Path
$KeytoolPath = Resolve-KeytoolPath $KeytoolPath
$TempDir = Join-Path $PSScriptRoot ".tmp"
Ensure-Directory $TempDir

Write-Log "INFO" "Root directory: $RootDir"
Write-Log "INFO" "Keytool path: $KeytoolPath"

$pluginKeystore = Join-Path $RootDir "psp\plugin\bank-payment-plugin\src\main\resources\certs\keystore.p12"
$bankTruststore = Join-Path $RootDir "bank\back\src\main\resources\certs\truststore.p12"
$pluginCert = Join-Path $TempDir "bank-payment-plugin-cert.cer"

if ((Test-Path $bankTruststore) -and $Force) {
    Remove-Item $bankTruststore -Force
    Write-Log "INFO" "Removed existing bank-back truststore: $bankTruststore"
}

Import-CertificateToTruststore `
    -SourceKeystorePath $pluginKeystore `
    -SourceAlias "bankpaymentplugin" `
    -CertificateFile $pluginCert `
    -TruststorePath $bankTruststore `
    -TrustAlias "bankpaymentplugin" `
    -Keytool $KeytoolPath

Write-Log "INFO" "bank-back truststore is ready. It trusts bank-payment-plugin."
