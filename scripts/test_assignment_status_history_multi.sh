#!/bin/bash

API_URL="http://localhost:8080/api/evaluation-sessions"
GROUP_API_URL="http://localhost:8080/api/evaluation-groups"
LOGIN_URL="http://localhost:8080/api/auth/login"
TASK_API_URL="http://localhost:8080/api/tasks"
ASSIGNMENT_API_URL="http://localhost:8080/api/assignments"
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

# 2. Lấy danh sách task
echo "[2] Lấy danh sách task..."
TASKS=$(curl -s -H "$AUTH_HEADER" "$TASK_API_URL")
echo "$TASKS" | jq

# 3. Lấy assignment từ task đầu tiên (nếu có)
echo "[3] Lấy assignment từ task đầu tiên..."
TASK_ID=$(echo "$TASKS" | jq -r '.data[0].id // .data[0].taskId')
ASSIGNMENT_ID=$(echo "$TASKS" | jq -r '.data[0].assignments[0].assignmentId // .data[0].assignments[0].id')

if [ "$ASSIGNMENT_ID" == "null" ] || [ -z "$ASSIGNMENT_ID" ]; then
  echo "[3.1] Không tìm thấy assignment, tiến hành tạo assignment mới để test..."
  if [ "$TASK_ID" == "null" ] || [ -z "$TASK_ID" ]; then
    echo "[LỖI] Không tìm thấy task để tạo assignment."
    exit 1
  fi
  CREATE_ASSIGNMENT_RESPONSE=$(curl -s -X POST "$ASSIGNMENT_API_URL" \
    -H "$AUTH_HEADER" \
    -H "Content-Type: application/json" \
    -d '{"taskId":'$TASK_ID', "recipientType":"user", "recipientId":1, "status":"ASSIGNED"}')
  ASSIGNMENT_ID=$(echo "$CREATE_ASSIGNMENT_RESPONSE" | jq -r '.data.assignmentId // .data.id')
  echo "[3.2] Đã tạo assignment mới với ID: $ASSIGNMENT_ID"
else
  echo "[3.3] Đã tìm thấy assignment có sẵn với ID: $ASSIGNMENT_ID"
fi

# 4. Đổi status 3 lần liên tiếp
echo "[4] Đổi status sang SUBMITTED..."
curl -s -X POST "$ASSIGNMENT_API_URL/$ASSIGNMENT_ID/status" \
  -H "$AUTH_HEADER" \
  -d "status=SUBMITTED&comment=Submit lần 1&fileUrl=https://mock.link/file1.pdf&userId=1" | jq
sleep 1

echo "[5] Đổi status sang REVIEWING..."
curl -s -X POST "$ASSIGNMENT_API_URL/$ASSIGNMENT_ID/status" \
  -H "$AUTH_HEADER" \
  -d "status=REVIEWING&comment=Review lần 2&fileUrl=https://mock.link/file2.pdf&userId=1" | jq
sleep 1

echo "[6] Đổi status sang COMPLETED..."
curl -s -X POST "$ASSIGNMENT_API_URL/$ASSIGNMENT_ID/status" \
  -H "$AUTH_HEADER" \
  -d "status=COMPLETED&comment=Hoàn thành lần 3&fileUrl=https://mock.link/file3.pdf&userId=1" | jq
sleep 1

# 7. Lấy lịch sử thay đổi trạng thái assignment
echo "[7] Lấy lịch sử thay đổi trạng thái assignment $ASSIGNMENT_ID..."
curl -s -H "$AUTH_HEADER" "$ASSIGNMENT_API_URL/$ASSIGNMENT_ID/status-history" | jq
