#!/bin/bash
# Test API live-tracking-group cho Flight (có login)

API_URL="http://localhost:8080/api"
USER_EMAIL="domtuan22@gmail.com"
USER_PASSWORD="123456"

# Đăng nhập lấy token
echo "[LOGIN] Đăng nhập lấy token..."
TOKEN=$(curl -s -X POST "$API_URL/auth/login" -H "Content-Type: application/json" -d "{\"email\":\"$USER_EMAIL\",\"password\":\"$USER_PASSWORD\"}" | jq -r '.data.accessToken')
echo "[LOGIN] Token: $TOKEN"

API_LIVE_TRACK="$API_URL/flights/live-tracking-group"

echo "[TEST] Gọi API live-tracking-group..."
response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -H "Authorization: Bearer $TOKEN" "$API_LIVE_TRACK")

body=$(echo "$response" | sed -e '/HTTP_STATUS:/d')
status=$(echo "$response" | grep 'HTTP_STATUS' | awk -F: '{print $2}')

echo "Status: $status"
echo "Response body:"
echo "$body"

if [ "$status" = "200" ]; then
  echo "✅ API /api/flights/live-tracking-group OK"
else
  echo "❌ API /api/flights/live-tracking-group FAIL"
  exit 1
fi
