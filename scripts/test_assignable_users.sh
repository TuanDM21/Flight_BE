#!/bin/bash
# Script test API lấy danh sách user có thể giao việc (theo phân quyền)
# Hiển thị số lượng user, tên, role, team, unit, và các role/team/unit có thể giao

API_URL="http://localhost:8080/api"
USERNAME="domtuan22@gmail.com"
PASSWORD="123456"

# Đăng nhập lấy token
echo "==> Đăng nhập lấy access token..."
LOGIN_RESPONSE=$(curl -s -X POST "$API_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"'$USERNAME'","password":"'$PASSWORD'"}')
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"accessToken":"[^"]*' | grep -o '[^"]*$')

if [ -z "$TOKEN" ]; then
  echo "Không lấy được token! Response: $LOGIN_RESPONSE"
  exit 1
fi

echo "Token: $TOKEN"

# Lấy thông tin user hiện tại
CURRENT_USER=$(curl -s -X GET "$API_URL/users/me" -H "Authorization: Bearer $TOKEN")
ROLE_NAME=$(echo "$CURRENT_USER" | jq -r '.data.roleName')
USER_NAME=$(echo "$CURRENT_USER" | jq -r '.data.name')
echo -e "\n==> Đăng nhập với user: $USER_NAME (role: $ROLE_NAME)"

echo "==> Lấy danh sách user có thể giao việc (theo phân quyền)..."
ASSIGNABLE_USERS=$(curl -s -X GET "$API_URL/users/assignable" \
  -H "Authorization: Bearer $TOKEN")

COUNT=$(echo "$ASSIGNABLE_USERS" | jq '.data | length')
echo "Số lượng user có thể giao việc: $COUNT"
echo "Danh sách chi tiết:"
echo "$ASSIGNABLE_USERS" | jq -r '.data[] | "- [\u001b[1;34m"+.roleName+"\u001b[0m] "+.name+" (Team: "+(.teamName // "-")+", Unit: "+(.unitName // "-")+")"'

# Nếu là VICE_TEAM_LEAD thì log chi tiết từng user có thể giao
if [ "$ROLE_NAME" = "VICE_TEAM_LEAD" ]; then
  echo -e "\n[DEBUG] VICE_TEAM_LEAD: Danh sách user cùng team (trừ TEAM_LEAD):"
  echo "$ASSIGNABLE_USERS" | jq -r '.data[] | select(.roleName != "TEAM_LEAD") | "[DEBUG] VICE_TEAM_LEAD có thể giao cho: " + .name + " (role=" + .roleName + ", team=" + (.teamId|tostring) + ", unit=" + (.unitId|tostring) + ")"'
  echo "[DEBUG] VICE_TEAM_LEAD tổng số user có thể giao: $COUNT"
fi

echo -e "\nCác role có thể giao việc:"
echo "$ASSIGNABLE_USERS" | jq -r '.data[].roleName' | sort | uniq | awk '{print "- "$0}'

echo -e "\nCác team có thể giao việc:"
echo "$ASSIGNABLE_USERS" | jq -r '.data[].teamName' | sort | uniq | grep -v null | awk '{print "- "$0}'

echo -e "\nCác unit có thể giao việc:"
echo "$ASSIGNABLE_USERS" | jq -r '.data[].unitName' | sort | uniq | grep -v null | awk '{print "- "$0}'
