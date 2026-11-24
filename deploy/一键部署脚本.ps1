# 智慧养老平台一键部署脚本
# 使用方法：在PowerShell中执行 .\deploy\一键部署脚本.ps1

param(
    [string]$ServerIP = "",
    [string]$ServerUser = "root",
    [string]$Domain = "liyirou.top"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  智慧养老平台一键部署脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查参数
if ([string]::IsNullOrEmpty($ServerIP)) {
    $ServerIP = Read-Host "请输入服务器IP地址"
}

Write-Host "部署配置：" -ForegroundColor Yellow
Write-Host "  服务器IP: $ServerIP" -ForegroundColor White
Write-Host "  用户名: $ServerUser" -ForegroundColor White
Write-Host "  域名: $Domain" -ForegroundColor White
Write-Host ""

$confirm = Read-Host "确认开始部署? (y/n)"
if ($confirm -ne "y") {
    Write-Host "已取消部署" -ForegroundColor Red
    exit
}

# 设置路径
$ProjectRoot = "d:\养老"
$BackendPath = "$ProjectRoot\后端\target\elderly-care-1.0.0.jar"
$FrontendPath = "$ProjectRoot\管理平台前端\dist"
$SqlPath = "$ProjectRoot\后端\sql"

Write-Host ""
Write-Host "步骤 1/6: 检查本地文件..." -ForegroundColor Green

# 检查文件是否存在
if (-not (Test-Path $BackendPath)) {
    Write-Host "