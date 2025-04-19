#!/bin/bash

API_URL="http://localhost:8080/api"
USER_EMAIL="domtuan22@gmail.com"
USER_PASSWORD="123456"
EXPO_PUSH_TOKEN="ExponentPushToken[xxxxxxx]"

# API 1: Đăng nhập lấy token
echo "[API 1] Đăng nhập..."
TOKEN=$(curl -s -X POST "$API_URL/auth/login" -H "Content-Type: application/json" -d "{\"email\":\"$USER_EMAIL\",\"password\":\"$USER_PASSWORD\"}" | jq -r '.data.accessToken')
echo "[API 1] Token: $TOKEN"

# API 2: Lưu expoPushToken
echo "[API 2] Lưu expoPushToken..."
curl -s -X POST "$API_URL/users/expo-push-token" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d "\"$EXPO_PUSH_TOKEN\""
echo "[API 2] Đã gửi expoPushToken."

# API 3: Tạo activity với participants là user, team, unit
echo "[API 3] Tạo activity mới..."
curl -s -X POST "$API_URL/activities" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "name": "Test Activity",
  "location": "Test Room",
  "startTime": "2025-04-20T09:00:00",
  "endTime": "2025-04-20T10:00:00",
  "notes": "Test notes",
  "participants": [
    { "participantType": "TEAM", "participantId": 1 }
  ]
}'
echo "[API 3] Đã tạo activity."

# API 4: Lấy notification
echo "[API 4] Lấy notification..."
curl -s -X GET "$API_URL/notifications" -H "Authorization: Bearer $TOKEN" | jq

# API 5: Đánh dấu notification đầu tiên đã đọc
NOTI_ID=$(curl -s -X GET "$API_URL/notifications" -H "Authorization: Bearer $TOKEN" | jq '.[0].id')
echo "[API 5] Đánh dấu notification $NOTI_ID đã đọc..."
curl -s -X POST "$API_URL/notifications/read/$NOTI_ID" -H "Authorization: Bearer $TOKEN"
echo "[API 5] Đã đánh dấu notification đã đọc."

# API 6: Đếm số notification chưa đọc
echo "[API 6] Số notification chưa đọc:"
curl -s -X GET "$API_URL/notifications/unread-count" -H "Authorization: Bearer $TOKEN"

# API 7: Phân công user vào chuyến bay (giả sử flightId=5)
echo "[API 7] Phân công user vào chuyến bay..."
curl -s -X POST "$API_URL/user-flight-shifts/apply" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{
  "flightId": 5,
  "userIds": [1, 2]
}'
echo "[API 7] Đã phân công user vào chuyến bay."

# API 8: Kiểm tra notification sau khi phân công chuyến bay
echo "[API 8] Lấy notification sau khi phân công chuyến bay..."
curl -s -X GET "$API_URL/notifications" -H "Authorization: Bearer $TOKEN" | jq

echo "Test hoàn tất."
