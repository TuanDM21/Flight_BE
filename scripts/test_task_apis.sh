#!/bin/bash
# Script test API Task: login lấy token, test tạo/lấy task detail/lấy danh sách task, giao việc cho user 2 và user 3

API_URL="http://localhost:8080/api"
USERNAME="domtuan22@gmail.com"
PASSWORD="123456"

log() {
  echo -e "\033[1;34m$1\033[0m"
}

echo "==> Đăng nhập lấy access token..."
LOGIN_RESPONSE=$(curl -s -X POST "$API_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"'$USERNAME'","password":"'$PASSWORD'"}')
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"accessToken":"[^"]*' | grep -o '[^"]*$')

if [ -z "$TOKEN" ]; then
  log "Không lấy được token! Response: $LOGIN_RESPONSE"
  exit 1
fi

log "Token: $TOKEN"

# Lấy danh sách documentId thực tế
log "==> Lấy danh sách documentId thực tế..."
DOC_LIST=$(curl -s -X GET "$API_URL/documents" -H "Authorization: Bearer $TOKEN")
DOC_ID1=$(echo $DOC_LIST | jq '.data[0].id')
DOC_ID2=$(echo $DOC_LIST | jq '.data[1].id')
if [ "$DOC_ID1" = "null" ]; then DOC_ID1=; fi
if [ "$DOC_ID2" = "null" ]; then DOC_ID2=; fi

echo "==> Test tạo task giao việc cho user 2 và user 3..."
CREATE_TASK_RESPONSE=$(curl -s -X POST "$API_URL/tasks" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "content": "Test task from script",
    "instructions": "Test instructions",
    "notes": "Test notes",
    "assignments": [
      {"recipientId": 1, "recipientType": "user", "dueAt": "2025-05-20T10:00:00Z", "note": "Giao cho user 2"},
      {"recipientId": 3, "recipientType": "user", "dueAt": "2025-05-21T10:00:00Z", "note": "Giao cho user 3"}
    ],
    "documentIds": ['${DOC_ID1:-0}','${DOC_ID2:-0}']
  }')
echo "Response tạo task: $CREATE_TASK_RESPONSE"
TASK_ID=$(echo $CREATE_TASK_RESPONSE | grep -o '"id":[0-9]*' | grep -o '[0-9]*')

if [ -z "$TASK_ID" ]; then
  log "Không lấy được task id!"
  exit 1
fi

echo "==> Test lấy chi tiết task id=$TASK_ID..."
curl -s -X GET "$API_URL/tasks/$TASK_ID" \
  -H "Authorization: Bearer $TOKEN" | jq

echo "==> Test lấy danh sách task..."
curl -s -X GET "$API_URL/tasks" \
  -H "Authorization: Bearer $TOKEN" | jq

echo "==> Test xoá mềm task id=$TASK_ID..."
DELETE_TASK_RESPONSE=$(curl -s -X DELETE "$API_URL/tasks/$TASK_ID" \
  -H "Authorization: Bearer $TOKEN")
echo "Response xoá task: $DELETE_TASK_RESPONSE"

echo "==> Test lấy chi tiết task id=$TASK_ID sau khi xoá..."
curl -s -X GET "$API_URL/tasks/$TASK_ID" \
  -H "Authorization: Bearer $TOKEN" | jq

echo "==> Test lấy danh sách task sau khi xoá..."
curl -s -X GET "$API_URL/tasks" \
  -H "Authorization: Bearer $TOKEN" | jq

echo "==> Đã test xong các API Task!"
