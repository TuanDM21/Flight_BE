#!/bin/bash
# Script to test EvaluationSession API endpoints (tự động tạo EvaluationGroup nếu chưa có)
# Yêu cầu: server Spring Boot đã chạy, đã cài jq, đã có user test để login lấy token

API_URL="http://localhost:8080/api/evaluation-sessions"
GROUP_API_URL="http://localhost:8080/api/evaluation-groups"
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

# 2. Tạo EvaluationGroup nếu chưa có
echo "\n[2] Kiểm tra/tạo EvaluationGroup mẫu..."
GROUP_ID=$(curl -s "$GROUP_API_URL" -H "$AUTH_HEADER" | jq -r '.data[0].id // empty')
if [ -z "$GROUP_ID" ]; then
  CREATE_GROUP=$(curl -s -X POST "$GROUP_API_URL" \
    -H "Content-Type: application/json" \
    -H "$AUTH_HEADER" \
    -d '{"groupName": "Đoàn kiểm tra tự động", "description": "Tạo tự động cho test session"}')
  GROUP_ID=$(echo $CREATE_GROUP | jq -r '.id // .data.id // empty')
  echo "Tạo mới EvaluationGroup, id: $GROUP_ID"
else
  echo "Dùng EvaluationGroup có sẵn, id: $GROUP_ID"
fi

if [ -z "$GROUP_ID" ]; then
  echo "[LỖI] Không tạo được EvaluationGroup. Thoát."
  exit 1
fi

# 3. Tạo mới EvaluationSession
echo "\n[3] Tạo mới EvaluationSession..."
CREATE_RESPONSE=$(curl -s -X POST "$API_URL" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{
    "evaluationGroupId": '$GROUP_ID',
    "startDate": "2025-05-10",
    "endDate": "2025-05-12",
    "notes": "Đợt kiểm tra định kỳ",
    "assignments": [
      { "targetType": "team", "targetId": 1 },
      { "targetType": "unit", "targetId": 2 }
    ]
  }')
echo "Response: $CREATE_RESPONSE"
NEW_ID=$(echo $CREATE_RESPONSE | grep -o '"id":[0-9]*' | grep -o '[0-9]*')

# 4. Lấy danh sách EvaluationSession
echo "\n[4] Lấy danh sách EvaluationSession..."
curl -s "$API_URL" -H "$AUTH_HEADER" | jq

# 5. Lấy EvaluationSession theo ID
echo "\n[5] Lấy EvaluationSession theo ID: $NEW_ID"
curl -s "$API_URL/$NEW_ID" -H "$AUTH_HEADER" | jq

# 6. Cập nhật EvaluationSession
echo "\n[6] Cập nhật EvaluationSession..."
curl -s -X PUT "$API_URL/$NEW_ID" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{
    "evaluationGroupId": '$GROUP_ID',
    "startDate": "2025-05-11",
    "endDate": "2025-05-13",
    "notes": "Đợt kiểm tra cập nhật",
    "assignments": [
      { "targetType": "team", "targetId": 1 }
    ]
  }' | jq

# 7. Xóa EvaluationSession
# echo "\n[7] Xóa EvaluationSession..."
# curl -s -X DELETE "$API_URL/$NEW_ID" -H "$AUTH_HEADER"

# echo "\n[Hoàn thành script test EvaluationSession]"
