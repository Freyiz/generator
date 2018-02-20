@echo off
color a
for /f "skip=5 tokens=1-2 delims==" %%i in (config.txt) ^
do (if %%i==username (set username=%%j) ^
else if %%i==password (set password=%%j & goto break))
:break
for %%f in (sql/*.sql) do ^
sqlplus %username%/%password% @sql/%%f
java -jar lib/generator.jar
echo.
echo 执行完成！按任意键退出...
pause>nul
