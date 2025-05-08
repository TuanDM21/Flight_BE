#!/bin/bash
# Script to test EvaluationGroup API endpoints with login
# =========================
# CHÚ Ý:
# 1. Đảm bảo server Spring Boot đã chạy và DB đã migrate đầy đủ các bảng liên quan.
# 2. Đảm bảo đã cài jq (brew install jq) để parse JSON.
# 3. Nếu login thất bại, script sẽ dừng lại.
# 4. Nếu lỗi "not-null property" hoặc "table doesn't exist", kiểm tra lại entity và migration DB.
# 5. Nếu có lỗi xác thực (401/403), kiểm tra lại token hoặc quyền truy cập.
# =========================

API_URL="http://localhost:8080/api/evaluation-groups"
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

# 2. Tạo mới EvaluationGroup
echo "\n[2] Tạo mới EvaluationGroup..."
CREATE_RESPONSE=$(curl -s -X POST "$API_URL" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{"groupName": "Đoàn kiểm tra Sở Công thương", "description": "Đoàn kiểm tra chuyên ngành"}')
echo "Response: $CREATE_RESPONSE"
NEW_ID=$(echo $CREATE_RESPONSE | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
# NOTE: Nếu lỗi not-null property hoặc table doesn't exist, kiểm tra lại entity và migration DB.

# 3. Lấy danh sách EvaluationGroup
echo "\n[3] Lấy danh sách EvaluationGroup..."
curl -s "$API_URL" -H "$AUTH_HEADER" | jq
# NOTE: Nếu lỗi SQL, kiểm tra lại migration DB.

# 4. Lấy EvaluationGroup theo ID
echo "\n[4] Lấy EvaluationGroup theo ID: $NEW_ID"
curl -s "$API_URL/$NEW_ID" -H "$AUTH_HEADER" | jq
# NOTE: Nếu ID không tồn tại hoặc bảng chưa có dữ liệu, sẽ trả về lỗi hoặc empty.

# 5. Cập nhật EvaluationGroup
echo "\n[5] Cập nhật EvaluationGroup..."
curl -s -X PUT "$API_URL/$NEW_ID" \
  -H "Content-Type: application/json" \
  -H "$AUTH_HEADER" \
  -d '{"groupName": "Đoàn kiểm tra Sở Công thương (Cập nhật)", "description": "Đã cập nhật mô tả"}' | jq
# NOTE: Nếu ID không tồn tại hoặc thiếu trường bắt buộc, sẽ bị lỗi validate.

# 6. Xóa EvaluationGroup
echo "\n[6] Xóa EvaluationGroup..."
curl -s -X DELETE "$API_URL/$NEW_ID" -H "$AUTH_HEADER"
# NOTE: Nếu có ràng buộc khóa ngoại, có thể bị lỗi khi xóa.

echo "\n[Hoàn thành script test EvaluationGroup]"
