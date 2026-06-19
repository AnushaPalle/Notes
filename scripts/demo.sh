#!/usr/bin/env bash
# End-to-end demo through Kong. Requires: docker compose up --build -d, curl, openssl.
set -euo pipefail
KONG=${KONG:-http://localhost:8000}

b64url() { openssl base64 -A | tr '+/' '-_' | tr -d '='; }

jwt() { # $1 = issuer key, $2 = secret
  local header payload sig
  header=$(printf '{"alg":"HS256","typ":"JWT"}' | b64url)
  payload=$(printf '{"iss":"%s","exp":%d}' "$1" "$(( $(date +%s) + 3600 ))" | b64url)
  sig=$(printf '%s.%s' "$header" "$payload" | openssl dgst -sha256 -hmac "$2" -binary | b64url)
  printf '%s.%s.%s' "$header" "$payload" "$sig"
}

ADMIN=$(jwt admin-ravi-key demo-secret-admin-change-me-0123456789)
SHOPPER=$(jwt shopper-asha-key demo-secret-shopper-change-me-0123456789)
json='Content-Type: application/json'

echo "1) no token            -> 401"
curl -s -o /dev/null -w "%{http_code}\n" "$KONG/api/products"

echo "2) shopper creates product (ACL) -> 403"
curl -s -o /dev/null -w "%{http_code}\n" -H "Authorization: Bearer $SHOPPER" -H "$json" \
  -d '{"sku":"BK-1","name":"Book","price":12.5}' "$KONG/api/products"

echo "3) admin creates product -> 201"
curl -s -w " (%{http_code})\n" -H "Authorization: Bearer $ADMIN" -H "$json" \
  -d '{"sku":"BK-1","name":"Book","price":12.5}' "$KONG/api/products"

echo "4) shopper registers, orders -> PENDING then PAID"
USER_ID=$(curl -s -H "Authorization: Bearer $SHOPPER" -H "$json" \
  -d "{\"name\":\"Asha\",\"email\":\"asha$RANDOM@example.com\"}" "$KONG/api/users" | sed 's/.*"id":\([0-9]*\).*/\1/')
ORDER=$(curl -s -H "Authorization: Bearer $SHOPPER" -H "$json" \
  -d "{\"userId\":$USER_ID,\"sku\":\"BK-1\",\"quantity\":2}" "$KONG/api/orders")
echo "$ORDER"
ORDER_ID=$(echo "$ORDER" | sed 's/.*"id":\([0-9]*\).*/\1/')
sleep 2
curl -s -H "Authorization: Bearer $SHOPPER" "$KONG/api/orders/$ORDER_ID"; echo
