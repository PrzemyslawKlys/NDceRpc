:: Builds,  copies, merges, versions and packages release libs

:: TODO: call build solution in release mode
:: TODO: call updated Nuget according version of Version Info
call copyfiles.bat
call merge.bat
call pack.bat