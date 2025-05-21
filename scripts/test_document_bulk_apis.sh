#!/bin/bash

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
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"accessToken":"[^"]*' | grep -o '[^"]*$')

if [ -z "$TOKEN" ]; then
  log "Không lấy được token! Response: $LOGIN_RESPONSE"
  exit 1
fi

log "Token: $TOKEN"

# Test bulk insert documents
log "==> Test bulk insert documents..."
BULK_INSERT_RESPONSE=$(curl -s -X POST "$API_URL/documents/bulk" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '[
    {"documentType":"TYPE_A","content":"Nội dung 1","notes":"Ghi chú 1"},
    {"documentType":"TYPE_B","content":"Nội dung 2","notes":"Ghi chú 2"}
  ]')
log "Bulk Insert Response: $BULK_INSERT_RESPONSE"

# Lấy danh sách id vừa insert để test bulk delete
IDS=$(echo $BULK_INSERT_RESPONSE | grep -o '"id":[0-9]*' | grep -o '[0-9]*' | paste -sd, -)
if [ -z "$IDS" ]; then
  log "Không lấy được id từ response bulk insert!"
  exit 1
fi

# Test bulk delete documents
log "==> Test bulk delete documents..."
BULK_DELETE_RESPONSE=$(curl -s -X DELETE "$API_URL/documents/bulk" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "[$IDS]")
log "Bulk Delete Response: $BULK_DELETE_RESPONSE"
