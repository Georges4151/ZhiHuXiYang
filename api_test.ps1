# 养老项目API测试脚本
$baseUrl = "http://localhost:8080/api"
$testResults = @()

function Test-API {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Endpoint,
        [string]$Body = "",
        [hashtable]$Headers = @{},
        [string]$Token = ""
    )
    
    $headers = @{"Content-Type"="application/json"}
    if ($Token) {
        $headers["Authorization"] = "Bearer $Token"
    }
    
    try {
        if ($Method -eq "GET") {
            $response = Invoke-WebRequest -Uri "$baseUrl$Endpoint" -Method $Method -Headers $headers -TimeoutSec 10
        } else {
            $response = Invoke-WebRequest -Uri "$baseUrl$Endpoint" -Method $Method -Headers $headers -Body $Body -TimeoutSec 10
        }
        
        $content = $response.Content | ConvertFrom-Json
        $success = ($response.StatusCode -eq 200) -and ($content.code -eq 200)
        
        return @{
            Name = $Name
            Status = if ($success) { "PASS" } else { "FAIL" }
            StatusCode = $response.StatusCode
            ResponseCode = $content.code
            Message = $content.message
        }
    }
    catch {
        return @{
            Name = $Name
            Status = "ERROR"
            StatusCode = $_.Exception.Response.StatusCode.value__
            ResponseCode = "N/A"
            Message = $_.Exception.Message
        }
    }
}

Write-Host "========== 养老项目API测试开始 ==========" -ForegroundColor Cyan
Write-Host ""

# 1. 测试用户认证
Write-Host "--- 1. 用户认证测试 ---" -ForegroundColor Yellow
$testResults += Test-API -Name "用户登录" -Method "POST" -Endpoint "/auth/login" -Body '{"phone":"13800138001","password":"123456"}'
$testResults += Test-API -Name "用户登录(错误密码)" -Method "POST" -Endpoint "/auth/login" -Body '{"phone":"13800138001","password":"wrong"}'

# 2. 测试用户管理
Write-Host "--- 2. 用户管理测试 ---" -ForegroundColor Yellow
$testResults += Test-API -Name "获取用户列表" -Method "GET" -Endpoint "/user/list"
$testResults += Test-API -Name "获取用户详情" -Method "GET" -Endpoint "/user/1"

# 3. 测试健康数据
Write-Host "--- 3. 健康数据测试 ---" -ForegroundColor Yellow
$testResults += Test-API -Name "获取健康数据列表" -Method "GET" -Endpoint "/health/list"
$testResults += Test-API -Name "获取健康数据详情" -Method "GET" -Endpoint "/health/1"

# 4. 测试活动管理
Write-Host "--- 4. 活动管理测试 ---" -ForegroundColor Yellow
$testResults += Test-API -Name "获取活动列表" -Method "GET" -Endpoint "/activity/list"
$testResults += Test-API -Name "获取活动详情" -Method "GET" -Endpoint "/activity/1"

# 5. 测试服务管理
Write-Host "--- 5. 服务管理测试 ---" -ForegroundColor Yellow
$testResults += Test-API -Name "获取服务列表" -Method "GET" -Endpoint "/service/list"
$testResults += Test-API -Name "获取服务详情" -Method "GET" -Endpoint "/service/1"

# 6. 测试订单管理
Write-Host "--- 6. 订单管理测试 ---" -ForegroundColor Yellow
$testResults += Test-API -Name "获取订单列表" -Method "GET" -Endpoint "/order/list"
$testResults += Test-API -Name "获取订单详情" -Method "GET" -Endpoint "/order/1"

# 7. 测试新闻资讯
Write-Host "--- 7. 新闻资讯测试 ---" -ForegroundColor Yellow
$testResults += Test-API -Name "获取新闻列表" -Method "GET" -Endpoint "/news/list"
$testResults += Test-API -Name "获取新闻详情" -Method "GET" -Endpoint "/news/1"

# 8. 测试通知消息
Write-Host "--- 8. 通知消息测试 ---" -ForegroundColor Yellow
$testResults += Test-API -Name "获取通知列表" -Method "GET" -Endpoint "/notification/list"
$testResults += Test-API -Name "获取通知详情" -Method "GET" -Endpoint "/notification/1"

# 9. 测试用药提醒
Write-Host "--- 9. 用药提醒测试 ---" -ForegroundColor Yellow
$testResults += Test-API -Name "获取用药提醒列表" -Method "GET" -Endpoint "/medication/list"
$testResults += Test-API -Name "获取用药提醒详情" -Method "GET" -Endpoint "/medication/1"

# 10. 测试紧急呼叫
Write-Host "--- 10. 紧急呼叫测试 ---" -ForegroundColor Yellow
$testResults += Test-API -Name "获取紧急呼叫列表" -Method "GET" -Endpoint "/emergency/list"

# 11. 测试反馈
Write-Host "--- 11. 反馈测试 ---" -ForegroundColor Yellow
$testResults += Test-API -Name "获取反馈列表" -Method "GET" -Endpoint "/feedback/list"

# 12. 测试社交功能
Write-Host "--- 12. 社交功能测试 ---" -ForegroundColor Yellow
$testResults += Test-API -Name "获取动态列表" -Method "GET" -Endpoint "/social/posts"
$testResults += Test-API -Name "获取动态详情" -Method "GET" -Endpoint "/social/post/1"

# 13. 测试家庭功能
Write-Host "--- 13. 家庭功能测试 ---" -ForegroundColor Yellow
$testResults += Test-API -Name "获取家庭列表" -Method "GET" -Endpoint "/family/list"

# 14. 测试群组功能
Write-Host "--- 14. 群组功能测试 ---" -ForegroundColor Yellow
$testResults += Test-API -Name "获取群组列表" -Method "GET" -Endpoint "/group/list"
$testResults += Test-API -Name "获取群组详情" -Method "GET" -Endpoint "/group/1"

# 15. 测试地址管理
Write-Host "--- 15. 地址管理测试 ---" -ForegroundColor Yellow
$testResults += Test-API -Name "获取地址列表" -Method "GET" -Endpoint "/address/list"

# 16. 测试天气
Write-Host "--- 16. 天气功能测试 ---" -ForegroundColor Yellow
$testResults += Test-API -Name "获取天气" -Method "GET" -Endpoint "/weather/current?city=北京"

# 17. 测试统计
Write-Host "--- 17. 统计功能测试 ---" -ForegroundColor Yellow
$testResults += Test-API -Name "获取统计数据" -Method "GET" -Endpoint "/stats/overview"

# 18. 测试咨询
Write-Host "--- 18. 咨询功能测试 ---" -ForegroundColor Yellow
$testResults += Test-API -Name "获取咨询列表" -Method "GET" -Endpoint "/consultation/list"

Write-Host ""
Write-Host "========== 测试结果汇总 ==========" -ForegroundColor Cyan

$passed = ($testResults | Where-Object { $_.Status -eq "PASS" }).Count
$failed = ($testResults | Where-Object { $_.Status -eq "FAIL" }).Count
$errors = ($testResults | Where-Object { $_.Status -eq "ERROR" }).Count

foreach ($result in $testResults) {
    $color = switch ($result.Status) {
        "PASS" { "Green" }
        "FAIL" { "Red" }
        "ERROR" { "Red" }
        default { "White" }
    }
    Write-Host "[$($result.Status)] $($result.Name) - HTTP: $($result.StatusCode), Code: $($result.ResponseCode), Msg: $($result.Message)" -ForegroundColor $color
}

Write-Host ""
Write-Host "总计: $($testResults.Count) 个测试" -ForegroundColor Cyan
Write-Host "通过: $passed" -ForegroundColor Green
Write-Host "失败: $failed" -ForegroundColor Red
Write-Host "错误: $errors" -ForegroundColor Red

# 保存测试结果
$testResults | ConvertTo-Json | Out-File -FilePath "d:\养老\test_results.json" -Encoding UTF8
Write-Host ""
Write-Host "测试结果已保存到: d:\养老\test_results.json" -ForegroundColor Cyan
