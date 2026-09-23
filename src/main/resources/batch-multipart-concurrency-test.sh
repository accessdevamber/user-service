#!/bin/bash

URL="http://localhost:9090/users/createBulkUsers/V4"

if [ "$#" -eq 0 ]; then
  echo "Usage: $0 <csv-file-1> <csv-file-2> ..."
  exit 1
fi

echo "Starting $# batch import request(s)..."

for file in "$@"
do
  if [ ! -f "$file" ]; then
    echo "File not found: $file"
    continue
  fi

  echo "Uploading: $file"

  curl -X POST "$URL" \
    -F "file=@$file" &

done

wait

echo
echo "All HTTP requests completed."