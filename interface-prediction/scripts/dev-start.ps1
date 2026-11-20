# Dev startup script — runs Vite dev server and Eureka registration concurrently
Write-Host "Starting Vite dev server..." -ForegroundColor Cyan
$viteTask = Start-Process -FilePath "npm" -ArgumentList "run", "dev" -NoNewWindow -PassThru

Write-Host "Starting Eureka registration..." -ForegroundColor Yellow
$eurekaTask = Start-Process -FilePath "node" -ArgumentList "src/api/eureka-config.js" -NoNewWindow -PassThru

Write-Host "Press Ctrl+C to stop." -ForegroundColor Green
Wait-Process -Id $viteTask.Id -ErrorAction SilentlyContinue
Wait-Process -Id $eurekaTask.Id -ErrorAction SilentlyContinue
