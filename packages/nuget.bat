:: downloads dependencies used


:: for .NET 3.5
nuget\nuget.exe install CodeContracts.Unofficial
nuget\nuget.exe install TaskParallelLibrary

:: for tests

nuget\nuget.exe install Unity -Version 2.1.505.2
nuget\nuget.exe install Unity.Interception -Version 2.1.505.2
nuget\nuget.exe install NSubstitute
nuget\nuget.exe install NUnit
nuget\nuget.exe install MeasureIt

:: for deployment
nuget\nuget.exe install ILRepack





