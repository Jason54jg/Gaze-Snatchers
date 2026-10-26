# Build Gaze Snatchers for 1.21.11 and optionally Minecraft 26.x
# Usage:
#   .\scripts\build-all-versions.ps1
#   $env:BUILD_26='true'; $env:JAVA_HOME='C:\Program Files\Java\jdk-25.0.3'; .\scripts\build-all-versions.ps1

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $Root

$Versions = @("1.21.11")
if ($env:BUILD_26 -eq "true") {
    $Versions += @("26.1", "26.1.1", "26.1.2", "26.2", "26.3")
}

function Merge-Properties([string]$mc) {
    $common = [System.IO.File]::ReadAllText("$Root\gradle\versions\common.properties")
    $v = [System.IO.File]::ReadAllText("$Root\gradle\versions\$mc.properties")
    $utf8 = New-Object System.Text.UTF8Encoding $false
    [System.IO.File]::WriteAllText("$Root\gradle.properties", ($common.TrimEnd() + "`n" + $v.TrimEnd() + "`n"), $utf8)
}

function Test-IsJdk25 {
    if (-not $env:JAVA_HOME) { return $false }
    $java = Join-Path $env:JAVA_HOME "bin\java.exe"
    if (-not (Test-Path $java)) { return $false }
    $prev = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    $ver = cmd /c "`"$java`" -version 2>&1"
    $ErrorActionPreference = $prev
    return ($ver | Out-String) -match 'version "25'
}

New-Item -ItemType Directory -Force -Path "$Root\release" | Out-Null

foreach ($mc in $Versions) {
    Write-Host "========== Minecraft $mc =========="
    Merge-Properties $mc
    $props = Get-Content "$Root\gradle.properties" -Raw
    $unobfuscated = $props -match '(?m)^unobfuscated=true'
    $skipNeo = $props -match '(?m)^skip_neoforge=true'

    if ($unobfuscated -and -not (Test-IsJdk25)) {
        Write-Host "Skip $mc : JDK 25 required (set JAVA_HOME). See docs/MINECRAFT-26.md"
        continue
    }

    # gradlew.bat writes JVM warnings to stderr; do not treat that as a terminating error
    $prevEap = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    if ($skipNeo) {
        cmd /c "gradlew.bat --no-daemon clean :fabric:remapJar"
    } else {
        cmd /c "gradlew.bat --no-daemon clean :fabric:remapJar :neoforge:remapJar"
    }
    $gradleExit = $LASTEXITCODE
    $ErrorActionPreference = $prevEap

    $fabricJar = Get-ChildItem "fabric\build\libs\gazesnatchers*.jar" -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -notmatch "sources|dev-shadow|shadow" }
    if (-not $fabricJar) {
        throw "Build failed for $mc (gradle exit=$gradleExit)"
    }

    $out = Join-Path $Root "release\$mc"
    New-Item -ItemType Directory -Force -Path $out | Out-Null
    Get-ChildItem $out -Filter "*.jar" -ErrorAction SilentlyContinue | Remove-Item -Force

    # Exact match: *-26.1+fabric.jar must not also pick *-26.1.1+fabric.jar
    Get-ChildItem "fabric\build\libs" -Filter "gazesnatchers-*$mc+fabric.jar" -EA SilentlyContinue |
        ForEach-Object { Copy-Item $_.FullName (Join-Path $out $_.Name) -Force }

    if (-not $skipNeo) {
        Get-ChildItem "neoforge\build\libs" -Filter "gazesnatchers-*$mc+neoforge.jar" -EA SilentlyContinue |
            ForEach-Object { Copy-Item $_.FullName (Join-Path $out $_.Name) -Force }
    }

    Get-ChildItem $out | Format-Table Name, Length
}

if ($env:BUILD_26 -eq "true") {
    Merge-Properties "26.3"
} else {
    Merge-Properties "1.21.11"
}
Write-Host "Done. Artifacts under release/<version>/"
