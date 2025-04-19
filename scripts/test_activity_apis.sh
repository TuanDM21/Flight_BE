#!/bin/bash

# Đăng nhập để lấy token
LOGIN_RESPONSE=$(curl -s -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"domtuan22@gmail.com","password":"123456"}')

# Lấy accessToken từ response
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"accessToken":"[^"]*"' | cut -d':' -f2 | tr -d '"')

echo "Token: $TOKEN"

echo "\n==================="
echo "API 1: Tạo activity mới với participants ban đầu"
echo "==================="
CREATE_RESULT=$(curl -s -X POST "http://localhost:8080/api/activities" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Test Activity",
    "location":"Test Location",
    "startTime":"2025-04-18T10:00:00",
    "endTime":"2025-04-18T12:00:00",
    "notes":"Test notes",
    "participants":[{"participantType":"USER","participantId":2}]
  }')
echo "$CREATE_RESULT"
ACTIVITY_ID=$(echo $CREATE_RESULT | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)

# # Thêm kiểm tra nếu không lấy được ACTIVITY_ID
# if [ -z "$ACTIVITY_ID" ]; then
#   echo "Không lấy được ACTIVITY_ID, dừng script."
#   exit 1
# fi

# echo -e "\n---"

# echo "\n==================="
# echo "API 2: Cập nhật activity (id=$ACTIVITY_ID)"
# echo "==================="
# UPDATE_RESULT=$(curl -s -X PUT "http://localhost:8080/api/activities/$ACTIVITY_ID" \
#   -H "Authorization: Bearer $TOKEN" \
#   -H "Content-Type: application/json" \
#   -d '{
#     "name":"Updated Activity",
#     "location":"Updated Location",
#     "startTime":"2025-04-18T13:00:00",
#     "endTime":"2025-04-18T15:00:00",
#     "notes":"Updated notes",
#     "participants":[{"participantType":"USER","participantId":2},{"participantType":"USER","participantId":3}]
#   }')
# echo "$UPDATE_RESULT"
# echo -e "\n---"

# echo "\n==================="
# echo "API 3: Lấy chi tiết activity (id=$ACTIVITY_ID)"
# echo "==================="
# DETAIL_RESULT=$(curl -s -X GET "http://localhost:8080/api/activities/$ACTIVITY_ID" \
#   -H "Authorization: Bearer $TOKEN")
# echo "$DETAIL_RESULT"
# echo -e "\n---"

# echo "\n==================="
# echo "API 4: Thêm participants vào activity (id=$ACTIVITY_ID)"
# echo "==================="
# ADD_PARTICIPANT_RESULT=$(curl -s -X POST "http://localhost:8080/api/activities/$ACTIVITY_ID/participants" \
#   -H "Authorization: Bearer $TOKEN" \
#   -H "Content-Type: application/json" \
#   -d '[{"participantType":"USER","participantId":4}]')
# echo "$ADD_PARTICIPANT_RESULT"
# echo -e "\n---"

# echo "\n==================="
# echo "API 5: Lấy chi tiết activity (id=$ACTIVITY_ID) sau khi thêm participant"
# echo "==================="
# DETAIL_RESULT2=$(curl -s -X GET "http://localhost:8080/api/activities/$ACTIVITY_ID" \
#   -H "Authorization: Bearer $TOKEN")
# echo "$DETAIL_RESULT2"
# echo -e "\n---"

# echo "\n==================="
# echo "API 6: Xóa participant khỏi activity (id=$ACTIVITY_ID, participantType=USER, participantId=2)"
# echo "==================="
# REMOVE_PARTICIPANT_RESULT=$(curl -s -X DELETE "http://localhost:8080/api/activities/$ACTIVITY_ID/participants?participantType=USER&participantId=2" \
#   -H "Authorization: Bearer $TOKEN")
# echo "$REMOVE_PARTICIPANT_RESULT"
# echo -e "\n---"

# echo "\n==================="
# echo "API 7: Lấy chi tiết activity (id=$ACTIVITY_ID) sau khi xóa participant"
# echo "==================="
# DETAIL_RESULT3=$(curl -s -X GET "http://localhost:8080/api/activities/$ACTIVITY_ID" \
#   -H "Authorization: Bearer $TOKEN")
# echo "$DETAIL_RESULT3"
# echo -e "\n---"

# echo "\n==================="
# echo "API 8: Xóa activity (id=$ACTIVITY_ID)"
# echo "==================="
# DELETE_RESULT=$(curl -s -X DELETE "http://localhost:8080/api/activities/$ACTIVITY_ID" \
#   -H "Authorization: Bearer $TOKEN")
# echo "$DELETE_RESULT"
# echo -e "\n---"

# echo "\n==================="
# echo "API 9: Lấy tất cả activity"
# echo "==================="
# ALL_RESULT=$(curl -s -X GET "http://localhost:8080/api/activities" \
#   -H "Authorization: Bearer $TOKEN")
# echo "$ALL_RESULT"
# echo -e "\n---"
