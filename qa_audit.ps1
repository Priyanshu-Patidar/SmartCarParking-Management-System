# Automated QA Test Suite for SmartPark Platform

$baseUrl = "http://localhost:8080/api"
$report = @()

function Log-Test($module, $testName, $status, $details) {
    $obj = [PSCustomObject]@{
        Module   = $module
        Test     = $testName
        Status   = $status
        Details  = $details
    }
    $global:report += $obj
    if ($status -eq "FAIL") {
        Write-Host "[-] FAIL: [$module] $testName - $details" -ForegroundColor Red
    } else {
        Write-Host "[+] PASS: [$module] $testName" -ForegroundColor Green
    }
}

# --- Module 1: Authentication & User Management ---
Write-Host "`n--- Testing Authentication ---" -Cyan
try {
    # 1. Registration
    $regBody = @{
        fullName = "QA Tester"
        email = "qa_$(Get-Date -Format 'HHmmss')@test.com"
        password = "Password@123"
        phone = "1234567890"
    } | ConvertTo-Json
    $reg = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -ContentType "application/json" -Body $regBody
    Log-Test "Auth" "User Registration" "PASS" "User created with ID $($reg.userId)"

    # 2. Login
    $loginBody = @{
        email = "admin@smartparking.com"
        password = "Admin@123"
    } | ConvertTo-Json
    $login = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -ContentType "application/json" -Body $loginBody
    $adminToken = $login.accessToken
    Log-Test "Auth" "Admin Login" "PASS" "Token received"

    # 3. Role-Based Access (Admin)
    try {
        $users = Invoke-RestMethod -Uri "$baseUrl/admin/users" -Method Get -Headers @{Authorization="Bearer $adminToken"}
        Log-Test "Auth" "RBAC (Admin Access)" "PASS" "Admin can access user list"
    } catch {
        Log-Test "Auth" "RBAC (Admin Access)" "FAIL" $_.Exception.Message
    }

    # 4. Role-Based Access (User restriction)
    $userLoginBody = @{
        email = "user@smartparking.com"
        password = "User@123"
    } | ConvertTo-Json
    $userLogin = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -ContentType "application/json" -Body $userLoginBody
    $userToken = $userLogin.accessToken
    try {
        Invoke-RestMethod -Uri "$baseUrl/admin/users" -Method Get -Headers @{Authorization="Bearer $userToken"}
        Log-Test "Auth" "RBAC (User Restriction)" "FAIL" "User should NOT access admin endpoints"
    } catch {
        if ($_.Exception.Message -contains "403") {
            Log-Test "Auth" "RBAC (User Restriction)" "PASS" "User forbidden from admin endpoints"
        } else {
            Log-Test "Auth" "RBAC (User Restriction)" "FAIL" "Unexpected error: $($_.Exception.Message)"
        }
    }
} catch {
    Log-Test "Auth" "Auth Workflow" "FAIL" $_.Exception.Message
}

# --- Module 2: Parking Operations ---
Write-Host "`n--- Testing Parking Operations ---" -Cyan
try {
    # 1. Search
    $search = Invoke-RestMethod -Uri "$baseUrl/parking/search?location=Mumbai" -Method Get
    Log-Test "Parking" "Search Facility" "PASS" "Found $($search.Count) locations"
    $locId = $search[0].id

    # 2. Slot Availability
    $slots = Invoke-RestMethod -Uri "$baseUrl/parking/$locId/slots?vehicleType=CAR&startTime=2026-06-14T15:00:00&endTime=2026-06-14T17:00:00" -Method Get
    Log-Test "Parking" "Check Slot Availability" "PASS" "Found $($slots.Count) slots"
    $slotId = $slots[0].id

    # 3. Dynamic Pricing Estimate
    $estimate = Invoke-RestMethod -Uri "$baseUrl/parking/estimate-breakdown?locationId=$locId&vehicleType=CAR&startTime=2026-06-14T15:00:00&durationHours=2" -Method Get
    Log-Test "Parking" "Dynamic Pricing Breakdown" "PASS" "Total amount: ₹$($estimate.totalAmount)"

    # 4. Booking Conflict Test (Same slot, same time)
    $bookBody = @{
        locationId = $locId
        slotId = $slotId
        vehicleType = "CAR"
        startTime = "2026-06-14T15:00:00"
        durationHours = 2
        vehicleNumber = "QA-123"
        payment = @{ paymentMethod = "UPI"; upiId = "qa@upi" }
    } | ConvertTo-Json

    $booking1 = Invoke-RestMethod -Uri "$baseUrl/parking/prebook" -Method Post -ContentType "application/json" -Body $bookBody -Headers @{Authorization="Bearer $adminToken"}
    Log-Test "Parking" "Place Initial Booking" "PASS" "Booking Code: $($booking1.bookingCode)"

    try {
        Invoke-RestMethod -Uri "$baseUrl/parking/prebook" -Method Post -ContentType "application/json" -Body $bookBody -Headers @{Authorization="Bearer $userToken"}
        Log-Test "Parking" "Booking Conflict Detection" "FAIL" "System allowed double booking of same slot"
    } catch {
        if ($_.Exception.Message -contains "400") {
            Log-Test "Parking" "Booking Conflict Detection" "PASS" "Double booking prevented"
        } else {
            Log-Test "Parking" "Booking Conflict Detection" "FAIL" "Unexpected error: $($_.Exception.Message)"
        }
    }
} catch {
    Log-Test "Parking" "Operations Workflow" "FAIL" $_.Exception.Message
}

# --- Module 3: Admin & System Health ---
Write-Host "`n--- Testing Admin Suite ---" -Cyan
try {
    $health = Invoke-RestMethod -Uri "$baseUrl/admin/health" -Method Get -Headers @{Authorization="Bearer $adminToken"}
    Log-Test "Admin" "System Health Monitoring" "PASS" "Status: $($health.status), DB: $($health.database)"

    $analytics = Invoke-RestMethod -Uri "$baseUrl/dashboard/analytics" -Method Get -Headers @{Authorization="Bearer $adminToken"}
    Log-Test "Admin" "Advanced Analytics" "PASS" "Revenue Trends: $($analytics.revenueTrends.Count) points"
} catch {
    Log-Test "Admin" "Admin Suite Workflow" "FAIL" $_.Exception.Message
}

# --- Final Report ---
Write-Host "`n--- QA SUMMARY ---" -Yellow
$report | Format-Table -AutoSize
