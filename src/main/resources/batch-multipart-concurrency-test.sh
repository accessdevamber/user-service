#!/bin/bash

URL="http://localhost:9090/users/createBulkUsers/V4"

if [ "$#" -eq 0 ]; then
  echo "Usage: $0 <csv-file-1> <csv-file-2> ..."
  exit 1
fi

launched=0
skipped=0

i=1

echo "Starting $# batch import request(s)..."

for file in "$@"
do
  if [ ! -f "$file" ]; then
    echo "File not found: $file"
    skipped=$((skipped + 1))
    continue
  fi

  echo "Uploading: $file"

#  curl -X POST "$URL" \
#    -F "file=@$file" &

# curl -sS \
#    -o "/tmp/batch-import-$i.json" \
#    -w "Request $i -> HTTP %{http_code}\n" \
#    -X POST "$URL" \
#    -F "file=@$file" &

  request_no=$i

  curl -sS \
    -w "\nRequest $request_no -> HTTP %{http_code}\n" \
    -X POST "$URL" \
    -F "file=@$file" &

  ((i++))

  launched=$((launched + 1))

done

wait

echo
echo "Batch request summary:"
echo "Requested : $#"
echo "launched : $launched"
echo "Skipped   : $skipped"