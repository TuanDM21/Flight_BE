#!/bin/bash
# Script test_task_document_apis.sh: Test các API quản lý document của task có xác thực và tự động lấy ID

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

# Lấy TASK_ID thực tế
log "==> Lấy TASK_ID thực tế"
TASK_ID=$(curl -s -X GET "$API_URL/tasks" -H "Authorization: Bearer $TOKEN" | jq -r '.data[0].id')
if [ "$TASK_ID" == "null" ] || [ -z "$TASK_ID" ]; then
  log "Không tìm thấy Task nào để test!"
  exit 1
fi
log "TASK_ID: $TASK_ID"

# Lấy DOCUMENT_ID thực tế
log "==> Lấy DOCUMENT_ID thực tế"
DOCUMENT_ID=$(curl -s -X GET "$API_URL/documents" -H "Authorization: Bearer $TOKEN" | jq -r '.data[0].id')
if [ "$DOCUMENT_ID" == "null" ] || [ -z "$DOCUMENT_ID" ]; then
  log "Không tìm thấy Document nào để test!"
  exit 1
fi
log "DOCUMENT_ID: $DOCUMENT_ID"

# 1. Lấy danh sách document của task
log "==> Lấy danh sách document của task $TASK_ID"
curl -s -X GET "$API_URL/task-documents?taskId=$TASK_ID" -H "Authorization: Bearer $TOKEN" | jq

echo "---"
# 2. Gắn document vào task
log "==> Gắn document $DOCUMENT_ID vào task $TASK_ID"
curl -s -X POST "$API_URL/task-documents/attach?taskId=$TASK_ID&documentId=$DOCUMENT_ID" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json"
echo "---"
# 3. Xóa document khỏi task
log "==> Xóa document $DOCUMENT_ID khỏi task $TASK_ID"
curl -s -X DELETE "$API_URL/task-documents/remove?taskId=$TASK_ID&documentId=$DOCUMENT_ID" -H "Authorization: Bearer $TOKEN"
echo "---"
# 4. Lấy lại danh sách document của task sau khi xóa
log "==> Lấy lại danh sách document của task $TASK_ID sau khi xóa"
curl -s -X GET "$API_URL/task-documents?taskId=$TASK_ID" -H "Authorization: Bearer $TOKEN" | jq
echo "---"
