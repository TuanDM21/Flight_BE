#!/bin/bash

# 🧪 AUTOMATED API TEST SCRIPT - Activity Management System
# Run comprehensive tests for all refactored APIs

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
BASE_URL="http://localhost:8080"
AUTH_EMAIL="NamNT@vdh.com"
AUTH_PASSWORD="123456"
TOKEN=""
CREATED_ACTIVITY_ID=""

# Test counters
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# Helper functions
log_test() {
    echo -e "${BLUE}[TEST]${NC} $1"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
}

log_success() {
    echo -e "${GREEN}[PASS]${NC} $1"
    PASSED_TESTS=$((PASSED_TESTS + 1))
}

log_failure() {
    echo -e "${RED}[FAIL]${NC} $1"
    FAILED_TESTS=$((FAILED_TESTS + 1))
}

log_info() {
    echo -e "${YELLOW}[INFO]${NC} $1"
}

# Function to make authenticated requests
api_call() {
    local method=$1
    local endpoint=$2
    local data=$3
    local expected_status=$4
    
    if [ -z "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method \
                   -H "Authorization: Bearer $TOKEN" \
                   -H "Content-Type: application/json" \
                   "$BASE_URL$endpoint")
    else
        response=$(curl -s -w "\n%{http_code}" -X $method \
                   -H "Authorization: Bearer $TOKEN" \
                   -H "Content-Type: application/json" \
                   -d "$data" \
                   "$BASE_URL$endpoint")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    response_body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" -eq "$expected_status" ]; then
        return 0
    else
        echo "Expected: $expected_status, Got: $http_code"
        echo "Response: $response_body"
        return 1
    fi
}

# Start testing
echo -e "${BLUE}🧪 STARTING COMPREHENSIVE API TESTS${NC}"
echo "======================================"

# 1. Authentication
log_test "Authentication - Login"
login_response=$(curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"$AUTH_EMAIL\",\"password\":\"$AUTH_PASSWORD\"}")

if echo "$login_response" | grep -q "accessToken"; then
    TOKEN=$(echo "$login_response" | grep -o '"accessToken":"[^"]*' | cut -d'"' -f4)
    log_success "Authentication successful, token obtained"
else
    log_failure "Authentication failed"
    echo "$login_response"
    exit 1
fi

# 2. Calendar View Tests
log_test "Calendar View - Empty State"
if api_call "GET" "/api/activities/calendar" "" 200; then
    log_success "Calendar empty state works"
else
    log_failure "Calendar empty state failed"
fi

log_test "Calendar View - With Date Range"
if api_call "GET" "/api/activities/calendar?startDate=2025-10-01&endDate=2025-10-31" "" 200; then
    log_success "Calendar with date range works"
else
    log_failure "Calendar with date range failed"
fi

log_test "Calendar View - Personal View"
if api_call "GET" "/api/activities/calendar?type=my&startDate=2025-10-01&endDate=2025-10-31" "" 200; then
    log_success "Personal calendar view works"
else
    log_failure "Personal calendar view failed"
fi

log_test "Calendar View - Invalid Type"
if api_call "GET" "/api/activities/calendar?type=invalid" "" 400; then
    log_success "Invalid calendar type properly rejected"
else
    log_failure "Invalid calendar type validation failed"
fi

# 3. Strict Validation Tests
log_test "Validation - Empty Participants Array"
empty_participants='{
    "name": "VALIDATION TEST - Empty Participants",
    "location": "Test Room",
    "startTime": "2025-11-20T10:00:00",
    "endTime": "2025-11-20T12:00:00",
    "participants": []
}'
if api_call "POST" "/api/activities" "$empty_participants" 400; then
    log_success "Empty participants properly rejected"
else
    log_failure "Empty participants validation failed"
fi

log_test "Validation - Empty ParticipantIds Array"
empty_participant_ids='{
    "name": "VALIDATION TEST - Empty ParticipantIds",
    "location": "Test Room",
    "startTime": "2025-11-20T10:00:00",
    "endTime": "2025-11-20T12:00:00",
    "participants": [
        {
            "participantType": "USER",
            "participantIds": []
        }
    ]
}'
if api_call "POST" "/api/activities" "$empty_participant_ids" 400; then
    log_success "Empty participantIds properly rejected"
else
    log_failure "Empty participantIds validation failed"
fi

# 4. CREATE Activity Test
log_test "Activity Creation - Valid Multi-type Participants"
create_activity='{
    "name": "AUTOMATED TEST - Create Activity",
    "location": "Test Conference Room",
    "startTime": "2025-11-15T09:00:00",
    "endTime": "2025-11-15T11:00:00",
    "notes": "Testing complete array-based participant functionality",
    "participants": [
        {
            "participantType": "USER",
            "participantIds": [1, 2, 3]
        },
        {
            "participantType": "TEAM", 
            "participantIds": [1, 2]
        }
    ],
    "pinned": false
}'

create_response=$(curl -s -X POST "$BASE_URL/api/activities" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "$create_activity")

if echo "$create_response" | grep -q '"statusCode":201'; then
    CREATED_ACTIVITY_ID=$(echo "$create_response" | grep -o '"id":[0-9]*' | cut -d':' -f2)
    log_success "Activity created successfully (ID: $CREATED_ACTIVITY_ID)"
else
    log_failure "Activity creation failed"
    echo "$create_response"
fi

# 5. READ Activity Test
if [ -n "$CREATED_ACTIVITY_ID" ]; then
    log_test "Activity Read - Get Created Activity"
    if api_call "GET" "/api/activities/$CREATED_ACTIVITY_ID" "" 200; then
        log_success "Activity read successful"
    else
        log_failure "Activity read failed"
    fi
fi

# 6. UPDATE Activity Test
if [ -n "$CREATED_ACTIVITY_ID" ]; then
    log_test "Activity Update - Array-based Participants"
    update_activity='{
        "name": "AUTOMATED TEST - UPDATED Activity",
        "location": "Updated Test Room",
        "startTime": "2025-11-15T10:00:00",
        "endTime": "2025-11-15T12:30:00",
        "notes": "Updated with new array-based participants",
        "participants": [
            {
                "participantType": "USER",
                "participantIds": [4, 5]
            }
        ],
        "pinned": true
    }'
    
    if api_call "PUT" "/api/activities/$CREATED_ACTIVITY_ID" "$update_activity" 200; then
        log_success "Activity update successful"
    else
        log_failure "Activity update failed"
    fi
fi

# 7. ADD Participants Test
if [ -n "$CREATED_ACTIVITY_ID" ]; then
    log_test "Participant Management - Add Participants"
    add_participants='[
        {
            "participantType": "UNIT",
            "participantIds": [1, 2]
        }
    ]'
    
    if api_call "POST" "/api/activities/$CREATED_ACTIVITY_ID/participants" "$add_participants" 200; then
        log_success "Add participants successful"
    else
        log_failure "Add participants failed"
    fi
fi

# 8. Search Tests
log_test "Search - By Month/Year"
if api_call "GET" "/api/activities/search?month=11&year=2025" "" 200; then
    log_success "Search by month/year works"
else
    log_failure "Search by month/year failed"
fi

log_test "Search - By Date"
if api_call "GET" "/api/activities/search-by-date?date=2025-11-15" "" 200; then
    log_success "Search by date works"
else
    log_failure "Search by date failed"
fi

# 9. Error Handling Tests
log_test "Error Handling - Unauthorized Access"
unauth_response=$(curl -s -w "\n%{http_code}" -X GET \
    -H "Authorization: Bearer INVALID_TOKEN" \
    "$BASE_URL/api/activities")
unauth_code=$(echo "$unauth_response" | tail -n1)

if [ "$unauth_code" -eq 401 ]; then
    log_success "Unauthorized access properly handled"
else
    log_failure "Unauthorized access handling failed"
fi

log_test "Error Handling - Non-existent Activity"
if api_call "GET" "/api/activities/99999" "" 404; then
    log_success "Non-existent activity properly handled"
else
    log_failure "Non-existent activity handling failed"
fi

# 10. Cleanup - Delete Test Activity
if [ -n "$CREATED_ACTIVITY_ID" ]; then
    log_test "Cleanup - Delete Test Activity"
    if api_call "DELETE" "/api/activities/batch" "[$CREATED_ACTIVITY_ID]" 200; then
        log_success "Test activity cleanup successful"
    else
        log_failure "Test activity cleanup failed"
    fi
fi

# Final Summary
echo ""
echo "======================================"
echo -e "${BLUE}🏆 TEST EXECUTION SUMMARY${NC}"
echo "======================================"
echo -e "Total Tests: ${YELLOW}$TOTAL_TESTS${NC}"
echo -e "Passed: ${GREEN}$PASSED_TESTS${NC}"
echo -e "Failed: ${RED}$FAILED_TESTS${NC}"

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}🎉 ALL TESTS PASSED! System ready for production.${NC}"
    exit 0
else
    echo -e "${RED}❌ Some tests failed. Please review and fix issues.${NC}"
    exit 1
fi
