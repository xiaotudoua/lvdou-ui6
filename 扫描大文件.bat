@echo off
echo 正在扫描大于 5MB 的文件，请稍候...
echo ---------------------------------------------------
powershell -Command "Get-ChildItem -Path . -Recurse -File -ErrorAction SilentlyContinue | Where-Object { $_.Length -gt 5MB } | Sort-Object Length -Descending | Select-Object @{Name='大小(MB)';Expression={[math]::Round($_.Length / 1MB, 2)}}, FullName | Format-Table -AutoSize"
echo ---------------------------------------------------
pause