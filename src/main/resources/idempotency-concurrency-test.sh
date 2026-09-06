#!/bin/bash

BASE_URL="http://localhost:9090/users/createUser/V2"

echo "========================================"
echo "SCENARIO 1"
echo "Same key + same request body"
echo "========================================"

KEY="concurrent-same-001"

BODY='{
  "firstName": "Concurrent",
  "lastName": "Same",
  "email": "concurrent.same.001@test.com",
  "phone": "+919876543211",
  "password": "Password@123"
}'

for i in {1..5}
do
  (
    echo "Request $i starting..."

    curl -sS \
      -o "/tmp/idem-s1-$i.json" \
      -w "Request $i -> HTTP %{http_code}\n" \
      -X POST "$BASE_URL" \
      -H "Content-Type: application/json" \
      -H "Idempotency-Key: $KEY" \
      -d "$BODY"
  ) &
done
