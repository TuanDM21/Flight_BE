#!/bin/bash
# Script test API Task: login lấy token, test tạo/lấy task detail/lấy danh sách task, giao việc cho user 2 và user 3

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

# Test tạo task với assignment cho user 2 và user 3
echo "==> Test tạo task giao việc cho user 2 và user 3..."
CREATE_TASK_RESPONSE=$(curl -s -X POST "$API_URL/tasks" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "content": "Test task from script",
    "instructions": "Test instructions",
    "notes": "Test notes",
    "createdBy": 1,
    "assignments": [
      {"recipientId": 2, "recipientType": "user", "assignedBy": 1, "note": "Giao cho user 2"},
      {"recipientId": 3, "recipientType": "user", "assignedBy": 1, "note": "Giao cho user 3"}
    ]
  }')
echo "Response tạo task: $CREATE_TASK_RESPONSE"
TASK_ID=$(echo $CREATE_TASK_RESPONSE | grep -o '"id":[0-9]*' | grep -o '[0-9]*')

if [ -z "$TASK_ID" ]; then
  echo "Không lấy được task id!"
  exit 1
fi

# Test lấy chi tiết task
echo "==> Test lấy chi tiết task id=$TASK_ID..."
curl -s -X GET "$API_URL/tasks/$TASK_ID" \
  -H "Authorization: Bearer $TOKEN" | jq

# Test lấy danh sách task
echo "==> Test lấy danh sách task..."
curl -s -X GET "$API_URL/tasks" \
  -H "Authorization: Bearer $TOKEN" | jq

echo "==> Đã test xong các API Task!"
