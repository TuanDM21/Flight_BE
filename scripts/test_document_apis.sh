#!/bin/bash
# Script test API Document & Attachment: tạo, cập nhật, xoá, xem danh sách, xem chi tiết văn bản và file đính kèm

API_URL="http://localhost:8080/api"
USERNAME="domtuan22@gmail.com"
PASSWORD="123456"

log() {
  echo -e "\033[1;34m$1\033[0m"
}

# Đăng nhập lấy token
log "==> Đăng nhập lấy access token..."
LOGIN_RESPONSE=$(curl -s -X POST "$API_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"'$USERNAME'","password":"'$PASSWORD'"}')
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"accessToken":"[^"]*' | grep -o '[^\"]*$')

if [ -z "$TOKEN" ]; then
  log "Không lấy được token! Response: $LOGIN_RESPONSE"
  exit 1
fi

log "Token: $TOKEN"

# 1. Tạo văn bản
log "\n==> [Tạo văn bản]"
CREATE_DOC_RESPONSE=$(curl -s -X POST "$API_URL/documents" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "documentType": "report",
    "content": "Nội dung văn bản test",
    "notes": "Ghi chú test"
  }')
echo "$CREATE_DOC_RESPONSE"
DOC_ID=$(echo $CREATE_DOC_RESPONSE | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')

if [ -z "$DOC_ID" ]; then
  log "Không lấy được document id!"
  exit 1
fi

# 2. Cập nhật văn bản
log "\n==> [Cập nhật văn bản]"
UPDATE_DOC_RESPONSE=$(curl -s -X PUT "$API_URL/documents/$DOC_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "documentType": "report",
    "content": "Nội dung văn bản đã cập nhật",
    "notes": "Ghi chú đã cập nhật"
  }')
echo "$UPDATE_DOC_RESPONSE"

# 3. Xem danh sách văn bản
log "\n==> [Xem danh sách văn bản]"
curl -s -X GET "$API_URL/documents" \
  -H "Authorization: Bearer $TOKEN" | jq

# 4. Xem chi tiết văn bản
log "\n==> [Xem chi tiết văn bản]"
curl -s -X GET "$API_URL/documents/$DOC_ID" \
  -H "Authorization: Bearer $TOKEN" | jq

# 5. Gắn file vào văn bản
log "\n==> [Gắn file vào văn bản]"
CREATE_ATT_RESPONSE=$(curl -s -X POST "$API_URL/attachments/document/$DOC_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "filePath": "/files/test.pdf",
    "fileName": "test.pdf",
    "fileSize": 123456
  }')
echo "$CREATE_ATT_RESPONSE"
ATT_ID=$(echo $CREATE_ATT_RESPONSE | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')

if [ -z "$ATT_ID" ]; then
  log "Không lấy được attachment id!"
  exit 1
fi

# 6. Cập nhật file trong văn bản
log "\n==> [Cập nhật file trong văn bản]"
UPDATE_ATT_RESPONSE=$(curl -s -X PUT "$API_URL/attachments/$ATT_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "filePath": "/files/test_update.pdf",
    "fileName": "test_update.pdf",
    "fileSize": 654321
  }')
echo "$UPDATE_ATT_RESPONSE"

# 7. Xem danh sách file đính kèm của văn bản
log "\n==> [Xem danh sách file đính kèm của văn bản]"
curl -s -X GET "$API_URL/attachments/document/$DOC_ID" \
  -H "Authorization: Bearer $TOKEN" | jq

# 8. Xoá file trong văn bản
# log "\n==> [Xoá file trong văn bản]"
# curl -s -X DELETE "$API_URL/attachments/$ATT_ID" \
#   -H "Authorization: Bearer $TOKEN"
# log "\n==> Đã test xong các chức năng Document & Attachment!"

# 9. Xoá văn bản
# log "\n==> [Xoá văn bản]"
# curl -s -X DELETE "$API_URL/documents/$DOC_ID" \
#   -H "Authorization: Bearer $TOKEN"
# log "\n==> Đã test xong toàn bộ chức năng!"
