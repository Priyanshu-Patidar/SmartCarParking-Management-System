# Load Test Simulation for SmartPark Platform
# Simulate multiple users searching and booking

$baseUrl = "http://localhost:8080/api"
$userCount = 10
$iterations = 5

Write-Host "Starting Load Test Simulation ($userCount users, $iterations iterations)..." -Cyan

$jobs = @()

for ($i = 1; $i -le $userCount; $i++) {
    $email = "loaduser_$i@test.com"
    $password = "Password@123"
    
    # Register/Login user in a script block
    $scriptBlock = {
        param($baseUrl, $email, $password, $iter)
        try {
            # Register
            $regBody = @{ fullName="Load User"; email=$email; password=$password } | ConvertTo-Json
            try { Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -ContentType "application/json" -Body $regBody } catch {}
            
            # Login
            $login = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -ContentType "application/json" -Body (@{email=$email; password=$password} | ConvertTo-Json)
            $token = $login.accessToken
            
            for ($j = 1; $j -le $iter; $j++) {
                # Search
                $res = Invoke-RestMethod -Uri "$baseUrl/parking/search?location=Mumbai" -Method Get -Headers @{Authorization="Bearer $token"}
                Start-Sleep -Milliseconds (Get-Random -Minimum 100 -Maximum 500)
            }
            return "SUCCESS: $email completed"
        } catch {
            return "ERROR: $email - $($_.Exception.Message)"
        }
    }
    
    $jobs += Start-Job -ScriptBlock $scriptBlock -ArgumentList $baseUrl, $email, $password, $iterations
}

Write-Host "Waiting for simulations to complete..."
$results = $jobs | Wait-Job | Receive-Job
$jobs | Remove-Job

$results | Group-Object | Select-Object Name, Count | Format-Table -AutoSize
