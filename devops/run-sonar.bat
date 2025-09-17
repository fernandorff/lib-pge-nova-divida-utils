@echo off
echo PGE NOVA DIVIDA SONAR - DIRETORIO ATIVO: %cd%
:: obtém a versão do projeto ::
for /f "tokens=3 delims=<>" %%i in ('findstr /r "<revision>.*</revision>" ./pom.xml') do set version=%%i
:: verifica se a branch ativa contém / no nome, se não, pega o nome da branch ::
for /f "tokens=2 delims=/" %%i in ('git branch --show-current') do set branch=%%i
if  "%branch%" == ""  for /f "tokens=1 delims=" %%i in ('git branch --show-current') do set branch=%%i
echo PGE NOVA DIVIDA SONAR - BRANCH: %branch%  VERSION: %version%
@echo on
call mvn -s ./devops/settings-pge.xml clean package dependency:copy-dependencies jacoco:report
call sonar-scanner -D project.settings=./devops/sonar-project.properties -D sonar.projectVersion=%version% -D sonar.branch.name=%branch%
