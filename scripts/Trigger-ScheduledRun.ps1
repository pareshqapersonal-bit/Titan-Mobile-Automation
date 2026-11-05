# Starts the "Titan Eyewear Mobile Automation - Scheduled Run" GitHub workflow on time.
# Windows Task Scheduler runs this daily at 1:30 PM IST while the PC is on. When the PC is
# off, GitHub's own (late) cron runs instead; it skips itself if this already ran today.
#
# Needs a GitHub fine-grained token with "Actions: Read and write" on this repo, saved in the
# user environment variable GITHUB_DISPATCH_TOKEN.

$ErrorActionPreference = 'Stop'
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

$repo     = 'pareshqapersonal-bit/Titan-Mobile-Automation'
$workflow = 'titan-eyewear-scheduled.yml'
$branch   = 'main'

$logDir  = Join-Path $env:LOCALAPPDATA 'TitanAutomation'
$logFile = Join-Path $logDir 'trigger.log'
New-Item -ItemType Directory -Force $logDir | Out-Null
function Log($msg) {
    $line = "$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')  $msg"
    Add-Content -Path $logFile -Value $line -Encoding utf8
    Write-Output $line
}

$token = [Environment]::GetEnvironmentVariable('GITHUB_DISPATCH_TOKEN', 'User')
if (-not $token) {
    Log 'ERROR: GITHUB_DISPATCH_TOKEN user environment variable is not set.'
    exit 1
}
$headers = @{
    Authorization          = "Bearer $token"
    Accept                 = 'application/vnd.github+json'
    'X-GitHub-Api-Version' = '2022-11-28'
    'User-Agent'           = 'titan-automation-trigger'
}
$base = "https://api.github.com/repos/$repo/actions/workflows/$workflow"

# Start of today in IST, as UTC, so the check matches the workflow's own once-per-day rule
$ist      = [TimeZoneInfo]::FindSystemTimeZoneById('India Standard Time')
$istToday = [TimeZoneInfo]::ConvertTimeFromUtc([DateTime]::UtcNow, $ist).Date
$dayStart = [TimeZoneInfo]::ConvertTimeToUtc($istToday, $ist).ToString("yyyy-MM-ddTHH:mm:ssZ")

# The PC may have just woken up; give the network a few tries
for ($attempt = 1; $attempt -le 5; $attempt++) {
    try {
        $runs = Invoke-RestMethod -Uri "$base/runs?created=%3E%3D$dayStart&per_page=100" -Headers $headers
        $earlier = @($runs.workflow_runs | Where-Object { $_.conclusion -ne 'cancelled' }).Count
        if ($earlier -gt 0) {
            Log "Already ran today ($earlier run(s) since $dayStart) - not triggering."
            exit 0
        }
        Invoke-RestMethod -Method Post -Uri "$base/dispatches" -Headers $headers `
            -ContentType 'application/json' -Body (@{ ref = $branch } | ConvertTo-Json) | Out-Null
        Log "Triggered $workflow on $branch."
        exit 0
    } catch {
        Log "Attempt $attempt failed: $($_.Exception.Message)"
        if ($attempt -lt 5) { Start-Sleep -Seconds 60 }
    }
}
Log 'ERROR: could not trigger the workflow after 5 attempts. GitHub cron will run it later.'
exit 1
