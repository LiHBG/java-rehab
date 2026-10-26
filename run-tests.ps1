$ErrorActionPreference = 'Stop'
$env:JAVA_HOME = 'D:\develop\JDK\jdk-21'
Set-Location $PSScriptRoot
& 'D:\develop\Maven\apache-maven-3.9.9\bin\mvn.cmd' -B -ntp @args
exit $LASTEXITCODE
