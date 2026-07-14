@echo off
echo ================================
echo  爱出行 - 启���所有服务
echo ================================

echo.
echo [0/5] 请确保 Nacos 已启动!
echo http://localhost:8848/nacos
pause

echo [1/5] 启动网关...
start "Gateway-8080" java -jar jars/ailvxing-gateway-1.0-SNAPSHOT.jar
timeout /t 10 /nobreak >nul

echo [2/5] 启动用户服务...
start "User-8001" java -jar jars/ailvxing-user-1.0-SNAPSHOT.jar
timeout /t 10 /nobreak >nul

echo [3/5] 启动产品服务...
start "Product-8002" java -jar jars/ailvxing-product-1.0-SNAPSHOT.jar
timeout /t 10 /nobreak >nul

echo [4/5] 启动规划服务...
start "Plan-8003" java -jar jars/ailvxing-plan-1.0-SNAPSHOT.jar
timeout /t 10 /nobreak >nul

echo [5/5] 启动订单服务...
start "Order-8004" java -jar jars/ailvxing-order-1.0-SNAPSHOT.jar

echo.
echo ================================
echo  全部启动完成!
echo  前端主页: http://localhost:5173
echo  接口文档: http://localhost:8001/swagger-ui/index.html
echo ================================
pause
