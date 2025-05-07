#!/bin/bash
# Script test các API gắn, xóa, cập nhật văn bản cho công việc (TaskDocument)
# Yêu cầu: Đã chạy backend trên http://localhost:8080
API_URL="http://localhost:8080/api"
USERNAME="domtuan22@gmail.com"
PASSWORD="123456"

echo "==> Đăng nhập lấy access token..."
LOGIN_RESPONSE=$(curl -s -X POST "$API_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"'$USERNAME'","password":"'$PASSWORD'"}')
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"accessToken":"[^"]*' | grep -o '[^\"]*$')

if [ -z "$TOKEN" ]; then
  echo "Không lấy được token! Response: $LOGIN_RESPONSE"
  exit 1
fi

echo "Token: $TOKEN"

API_URL="http://localhost:8080/api/task-documents"
AUTH_HEADER="Authorization: Bearer $TOKEN"

# 1. Gắn văn bản mới vào công việc (taskId=1)
echo "\n--- Attach NEW document to task 1 ---"
curl -X POST "$API_URL/attach?taskId=1" \
    -H "Content-Type: application/json" \
    -H "$AUTH_HEADER" \
    -d '{
        "documentType": "TEST_TYPE",
        "content": "Test document content",
        "notes": "Test note"
    }'

echo "\n--- Attach EXISTING document (id=2) to task 1 ---"
curl -X POST "$API_URL/attach?taskId=1" \
    -H "Content-Type: application/json" \
    -H "$AUTH_HEADER" \
    -d '{
        "id": 2
    }'

# 2. Cập nhật văn bản trong công việc
echo "\n--- Update document (id=2) in task 1 ---"
curl -X PUT "$API_URL/update?taskId=1&documentId=2" \
    -H "Content-Type: application/json" \
    -H "$AUTH_HEADER" \
    -d '{
        "documentType": "UPDATED_TYPE",
        "content": "Updated content",
        "notes": "Updated note"
    }'

# 3. Xóa văn bản khỏi công việc
echo "\n--- Remove document (id=2) from task 1 ---"
curl -X DELETE "$API_URL/remove?taskId=1&documentId=2" \
    -H "$AUTH_HEADER"
