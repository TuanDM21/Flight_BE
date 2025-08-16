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

# 5. Tạo pre-signed URL để upload file
log "\n==> [Tạo pre-signed URL để upload file]"
PRESIGNED_RESPONSE=$(curl -s -X POST "$API_URL/attachments/generate-upload-urls" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "files": [
      {
        "fileName": "test.pdf",
        "fileSize": 123456,
        "contentType": "application/pdf"
      }
    ]
  }')
echo "$PRESIGNED_RESPONSE"

# Lấy attachment ID và pre-signed URL từ response
ATT_ID=$(echo $PRESIGNED_RESPONSE | jq -r '.data.files[0].attachmentId // empty')
UPLOAD_URL=$(echo $PRESIGNED_RESPONSE | jq -r '.data.files[0].uploadUrl // empty')

if [ -z "$ATT_ID" ] || [ "$ATT_ID" = "null" ]; then
  log "Không lấy được attachment id! Response: $PRESIGNED_RESPONSE"
  exit 1
fi

log "Attachment ID: $ATT_ID"
log "Upload URL được tạo thành công"

# 6. Giả lập upload file (trong thực tế sẽ upload file thật lên Azure)
log "\n==> [Giả lập upload file thành công]"
log "Trong thực tế, bạn sẽ upload file lên: $UPLOAD_URL"

# 7. Xác nhận upload thành công
log "\n==> [Xác nhận upload thành công]"
CONFIRM_RESPONSE=$(curl -s -X POST "$API_URL/attachments/confirm-upload" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "attachmentIds": ['$ATT_ID']
  }')
echo "$CONFIRM_RESPONSE"

# 8. Gán attachment vào document
log "\n==> [Gán attachment vào document]"
ASSIGN_RESPONSE=$(curl -s -X POST "$API_URL/documents/$DOC_ID/attachments/assign" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "attachmentIds": ['$ATT_ID']
  }')
echo "$ASSIGN_RESPONSE"

# 9. Cập nhật tên file đính kèm
log "\n==> [Cập nhật tên file đính kèm]"
UPDATE_ATT_RESPONSE=$(curl -s -X PUT "$API_URL/attachments/$ATT_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "fileName": "test_updated.pdf"
  }')
echo "$UPDATE_ATT_RESPONSE"

# 10. Xem chi tiết văn bản sau khi gán file
log "\n==> [Xem chi tiết văn bản sau khi gán file]"
curl -s -X GET "$API_URL/documents/$DOC_ID" \
  -H "Authorization: Bearer $TOKEN" | jq

# 11. Xem tất cả attachments
log "\n==> [Xem tất cả file đính kèm]"
curl -s -X GET "$API_URL/attachments" \
  -H "Authorization: Bearer $TOKEN" | jq

# 12. Xem chi tiết một attachment
log "\n==> [Xem chi tiết attachment]"
curl -s -X GET "$API_URL/attachments/$ATT_ID" \
  -H "Authorization: Bearer $TOKEN" | jq

# 13. Tạo download URL
log "\n==> [Tạo download URL]"
DOWNLOAD_RESPONSE=$(curl -s -X GET "$API_URL/attachments/download-url/$ATT_ID" \
  -H "Authorization: Bearer $TOKEN")
echo "$DOWNLOAD_RESPONSE"

# 14. Gỡ attachment khỏi document (tùy chọn)
# log "\n==> [Gỡ attachment khỏi document]"
# curl -s -X PATCH "$API_URL/documents/$DOC_ID/attachments/remove" \
#   -H "Content-Type: application/json" \
#   -H "Authorization: Bearer $TOKEN" \
#   -d '{
#     "attachmentIds": ['$ATT_ID']
#   }'

# 15. Xoá file đính kèm (tùy chọn)
# log "\n==> [Xoá file đính kèm]"
# curl -s -X DELETE "$API_URL/attachments/$ATT_ID" \
#   -H "Authorization: Bearer $TOKEN"

# 16. Xoá văn bản (tùy chọn)
# log "\n==> [Xoá văn bản]"
# curl -s -X DELETE "$API_URL/documents/$DOC_ID" \
#   -H "Authorization: Bearer $TOKEN"

log "\n==> ✅ Đã test xong toàn bộ workflow Document & Attachment!"
