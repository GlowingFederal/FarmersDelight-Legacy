param(
 [Parameter(Mandatory=$true)][int]$Port,
 [switch]$Client,
 [string]$JdkHome=$env:JAVA21_HOME,
 [string]$Output=(Join-Path ([IO.Path]::GetTempPath()) 'fd-recipe-checks.json')
)
$ErrorActionPreference='Stop'
$classes=Join-Path ([IO.Path]::GetTempPath()) ('fd-recipe-probe-'+[Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $classes | Out-Null
try {
 & (Join-Path $JdkHome 'bin\javac.exe') --add-modules jdk.jdi -d $classes (Join-Path $PSScriptRoot 'RecipeProbe.java')
 if($LASTEXITCODE -ne 0){throw 'Cannot compile debugger runner'}
 $script=if($Client){'viewers.js'}else{'checks.js'}
 $arguments=@('--add-modules','jdk.jdi','-cp',$classes,'RecipeProbe',(Join-Path $PSScriptRoot $script),$Port,'vectorwing.farmersdelight.legacy.Foundation')
 if($Client){$arguments+='client'}
 & (Join-Path $JdkHome 'bin\java.exe') @arguments | Set-Content -LiteralPath $Output -Encoding UTF8
 if($LASTEXITCODE -ne 0){throw 'Runtime recipe check failed'}
 $report=Get-Content -LiteralPath $Output -Raw | ConvertFrom-Json
 $inventoryFailures=if($Client){0}else{$report.inventory.validationFailures}
 Write-Output ('Checks: '+$report.checks+'; failures: '+$report.failures.Count+'; inventory failures: '+$inventoryFailures+'; report: '+$Output)
 if($report.failures.Count -gt 0 -or $inventoryFailures -gt 0){exit 1}
} finally {
 # Only remove the unique directory created by this invocation.
 $resolved=[IO.Path]::GetFullPath($classes)
 $tempRoot=[IO.Path]::GetFullPath([IO.Path]::GetTempPath())
 if($resolved.StartsWith($tempRoot,[StringComparison]::OrdinalIgnoreCase) -and (Split-Path $resolved -Leaf).StartsWith('fd-recipe-probe-')) { Remove-Item -LiteralPath $resolved -Recurse -Force }
}
