#!/bin/bash
# Script test API Assignment: login lấy token, test tạo/cập nhật/xoá/lấy assignment

API_URL="http://localhost:8080/api"
USERNAME="domtuan22@gmail.com"
PASSWORD="123456"

# Đăng nhập lấy token
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

# Test tạo assignment (giao công việc)
echo "==> Test tạo assignment..."
CREATE_ASSIGNMENT_RESPONSE=$(curl -s -X POST "$API_URL/assignments" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "taskId": 1,
    "recipientId": 1,
    "recipientType": "team",
    "status": 0,
    "note": "Giao cho user 4 qua script"
  }')
echo "Response tạo assignment: $CREATE_ASSIGNMENT_RESPONSE"
ASSIGNMENT_ID=$(echo $CREATE_ASSIGNMENT_RESPONSE | grep -o '"assignmentId":[0-9]*' | grep -o '[0-9]*')

if [ -z "$ASSIGNMENT_ID" ]; then
  echo "Không lấy được assignment id!"
  exit 1
fi

# Test cập nhật assignment
echo "==> Test cập nhật assignment id=$ASSIGNMENT_ID..."
UPDATE_ASSIGNMENT_RESPONSE=$(curl -s -X PUT "$API_URL/assignments/$ASSIGNMENT_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "taskId": 1,
    "recipientId": 4,
    "recipientType": "user",
    "assignedBy": 1,
    "status": 1,
    "note": "Đã cập nhật assignment cho user 4 qua script"
  }')
echo "Response cập nhật assignment: $UPDATE_ASSIGNMENT_RESPONSE"

# Test lấy chi tiết assignment
echo "==> Test lấy chi tiết assignment id=$ASSIGNMENT_ID..."
curl -s -X GET "$API_URL/assignments/$ASSIGNMENT_ID" \
  -H "Authorization: Bearer $TOKEN" | jq

# Test lấy danh sách assignment theo task
echo "==> Test lấy danh sách assignment theo taskId=1..."
curl -s -X GET "$API_URL/assignments/task/1" \
  -H "Authorization: Bearer $TOKEN" | jq

# Test xoá assignment
echo "==> Test xoá assignment id=$ASSIGNMENT_ID..."
curl -s -X DELETE "$API_URL/assignments/$ASSIGNMENT_ID" \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n==> Đã test xong các API Assignment!"
