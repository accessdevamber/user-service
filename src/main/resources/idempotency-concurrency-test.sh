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

wait

echo
echo "Responses:"
for i in {1..5}
do
  echo "----- Request $i -----"
  cat "/tmp/idem-s1-$i.json"
  echo
done


echo
echo "========================================"
echo "SCENARIO 2"
echo "Same key + DIFFERENT request bodies"
echo "========================================"

KEY="concurrent-different-001"

for i in {1..5}
do
  (
    BODY="{
      \"firstName\": \"User$i\",
      \"lastName\": \"Different\",
      \"email\": \"concurrent.diff.$i@test.com\",
      \"phone\": \"+91987654321$i\",
      \"password\": \"Password@123\"
    }"

    echo "Request $i starting..."

    curl -sS \
      -o "/tmp/idem-s2-$i.json" \
      -w "Request $i -> HTTP %{http_code}\n" \
      -X POST "$BASE_URL" \
      -H "Content-Type: application/json" \
      -H "Idempotency-Key: $KEY" \
      -d "$BODY"
  ) &
done

wait

echo
echo "Responses:"
for i in {1..5}
do
  echo "----- Request $i -----"
  cat "/tmp/idem-s2-$i.json"
  echo
done


echo
echo "========================================"
echo "SCENARIO 3"
echo "Different keys + different requests"
echo "========================================"

for i in {1..5}
do
  (
    KEY="concurrent-normal-$i"

    BODY="{
      \"firstName\": \"Normal$i\",
      \"lastName\": \"User\",
      \"email\": \"concurrent.normal.$i@test.com\",
      \"phone\": \"+91887654321$i\",
      \"password\": \"Password@123\"
    }"

    echo "Request $i starting..."

    curl -sS \
      -o "/tmp/idem-s3-$i.json" \
      -w "Request $i -> HTTP %{http_code}\n" \
      -X POST "$BASE_URL" \
      -H "Content-Type: application/json" \
      -H "Idempotency-Key: $KEY" \
      -d "$BODY"
  ) &
done

wait

echo
echo "Responses:"
for i in {1..5}
do
  echo "----- Request $i -----"
  cat "/tmp/idem-s3-$i.json"
  echo
done

echo
echo "========================================"
echo "All tests finished"
echo "========================================"

