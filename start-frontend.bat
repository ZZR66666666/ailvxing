@echo off
echo ================================
echo  爱出行 - 启动前端
echo ================================
echo.
echo 请先确保已安装 Node.js (https://nodejs.org)
echo.

cd /d "%~dp0frontend"

echo 正在安装 serve（仅首次需要）...
call npm install -g serve 2>nul

echo 启动前端服务器...
echo 访问地址: http://localhost:5173
echo 按 Ctrl+C 停止
echo.
call serve -s . -l 5173
pause
