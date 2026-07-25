@echo off
echo 正在构建 UltiminePlugin...
echo.

REM 检查 Maven 是否安装
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误：未找到 Maven，请先安装 Maven
    echo 下载地址：https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

REM 执行构建
mvn clean package

if %errorlevel% equ 0 (
    echo.
    echo 构建成功！
    echo JAR 文件位于：target\UltiminePlugin-1.0-SNAPSHOT.jar
) else (
    echo.
    echo 构建失败，请检查错误信息
)

pause