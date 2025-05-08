#!/bin/bash
# Script to test EvaluationIssue API endpoints (CRUD + update status + document)
# Yêu cầu: server Spring Boot đã chạy, đã cài jq, đã có user test để login lấy token

API_URL="http://localhost:8080/api/evaluation-issues"
SESSION_API_URL="http://localhost:8080/api/evaluation-sessions"
DOCUMENT_API_URL="http://localhost:8080/api/documents"
LOGIN_URL="http://localhost:8080/api/auth/login"
EMAIL="domtuan22@gmail.com"
PASSWORD="123456"

# 1. Đăng nhập lấy token
echo "[1] Đăng nhập lấy token..."
LOGIN_RESPONSE=$(curl -s -X POST "$LOGIN_URL" \
  -H "Content-Type: application/json" \
  -d '{"email": "'$EMAIL'", "password": "'$PASSWORD'"}')
TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.data.token // .data.accessToken // .token')

echo "Token: $TOKEN"
if [ "$TOKEN" == "null" ] || [ -z "$TOKEN" ]; then
  echo "[LỖI] Đăng nhập thất bại. Kiểm tra lại email/password hoặc API login."
  exit 1
fi

AUTH_HEADER="Authorization: Bearer $TOKEN"

# 2. Tạo EvaluationSession nếu chưa có
echo "\n[2] Kiểm tra/tạo EvaluationSession mẫu..."
SESSION_ID=$(curl -s "$SESSION_API_URL" -H "$AUTH_HEADER" | jq -r '.data[0].id // empty')
if [ -z "$SESSION_ID" ]; then
  CREATE_SESSION=$(curl -s -X POST "$SESSION_API_URL" \
    -H "Content-Type: application/json" \
    -H "$AUTH_HEADER" \
    -d '{
      "evaluationGroupId": 1,
      "startDate": "2025-05-10",
      "endDate": "2025-05-12",
      "notes": "Đợt kiểm tra tự động",
      "assignments": [ { "targetType": "team", "targetId": 1 } ]
    }')
  SESSION_ID=$(echo $CREATE_SESSION | jq -r '.data.id // .id // empty')
  echo "Tạo mới EvaluationSession, id: $SESSION_ID"
else
  echo "Dùng EvaluationSession có sẵn, id: $SESSION_ID"
fi

if [ -z "$SESSION_ID" ]; then
  echo "[LỖI] Không tạo được EvaluationSession. Thoát."
  exit 1
fi

# 3. Tạo Document mẫu nếu chưa có
echo "\n[3] Kiểm tra/tạo Document mẫu..."
DOC_ID=$(curl -s "$DOCUMENT_API_URL" -H "$AUTH_HEADER" | jq -r '.data[0].id // empty')
if [ -z "$DOC_ID" ]; then
  CREATE_DOC=$(curl -s -X POST "$DOCUMENT_API_URL" \
    -H "Content-Type: application/json" \
    -H "$AUTH_HEADER" \
    -d '{
      "documentType": "MINUTES",
      "content": "Nội dung biên bản kiểm tra mẫu",
      "notes": "Tài liệu test tự động"
    }')
  DOC_ID=$(echo $CREATE_DOC | jq -r '.data.id // .id // empty')
  echo "Tạo mới Document, id: $DOC_ID"
else
  echo "Dùng Document có sẵn, id: $DOC_ID"
fi

if [ -z "$DOC_ID" ]; then
  echo "[LỖI] Không tạo được Document. Thoát."
  exit 1
fi

# 4. Tạo mới EvaluationIssue
echo "\n[4] Tạo mới EvaluationIssue..."
CREATE_RESPONSE=$(curl -s -X POST "$API_URL" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{
    "evaluationSessionId": '$SESSION_ID',
    "targetType": "team",
    "targetId": 1,
    "issueContent": "Chưa có quy trình kiểm tra thiết bị",
    "requestedResolutionDate": "2025-05-20",
    "isResolved": false,
    "notes": "Cần bổ sung quy trình",
    "documentIds": ['$DOC_ID']
  }')
echo "Response: $CREATE_RESPONSE"
NEW_ID=$(echo $CREATE_RESPONSE | grep -o '"id":[0-9]*' | grep -o '[0-9]*')

# 5. Lấy danh sách EvaluationIssue
echo "\n[5] Lấy danh sách EvaluationIssue..."
curl -s "$API_URL" -H "$AUTH_HEADER" | jq

# 6. Lấy EvaluationIssue theo ID
echo "\n[6] Lấy EvaluationIssue theo ID: $NEW_ID"
curl -s "$API_URL/$NEW_ID" -H "$AUTH_HEADER" | jq

# 7. Cập nhật EvaluationIssue
echo "\n[7] Cập nhật EvaluationIssue..."
curl -s -X PUT "$API_URL/$NEW_ID" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{
    "evaluationSessionId": '$SESSION_ID',
    "targetType": "team",
    "targetId": 1,
    "issueContent": "Đã cập nhật nội dung tồn tại",
    "requestedResolutionDate": "2025-05-25",
    "isResolved": false,
    "notes": "Đã cập nhật ghi chú",
    "documentIds": ['$DOC_ID']
  }' | jq

# 8. Khắc phục tồn tại (update status)
echo "\n[8] Khắc phục tồn tại (update status)..."
curl -s -X PUT "$API_URL/$NEW_ID/status" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{
    "isResolved": true,
    "resolutionDate": "2025-05-21"
  }' | jq

# 9. Xóa EvaluationIssue
# echo "\n[9] Xóa EvaluationIssue..."
# curl -s -X DELETE "$API_URL/$NEW_ID" -H "$AUTH_HEADER"

# echo "\n[Hoàn thành script test EvaluationIssue]"
