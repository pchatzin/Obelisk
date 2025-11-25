#!/bin/bash
set -euo pipefail

echo "===================="
echo " Git Metrics Report "
echo "===================="

# Collect authors (name + email)
authors=$(git log --format='%an <%ae>' | sort -u)

# Header
printf "\n%-25s | %7s | %10s | %12s | %7s | %7s\n" "User" "Commits" "Lines Added" "Lines Deleted" "Classes" "Methods"
echo "-----------------------------------------------------------------------------------------------"

for author in $authors; do
  commits=$(git log --author="$author" --oneline | wc -l)
  read added deleted < <(git log --author="$author" --numstat --pretty=tformat: \
    | awk '{a=$1; d=$2; if(a=="-") a=0; if(d=="-") d=0; add+=a; del+=d} END {print add+0, del+0}')

  # Classes: count class declarations in files touched by this author
  classes=$(git log --author="$author" --name-only --pretty=tformat: \
    | sort -u | grep '\.java$' | xargs grep -h "class " 2>/dev/null | wc -l)

  # Methods: count public method signatures in files touched by this author
  methods=$(git log --author="$author" --name-only --pretty=tformat: \
    | sort -u | grep '\.java$' | xargs grep -h "public " 2>/dev/null | grep "(" | wc -l)

  printf "%-25s | %7s | %10s | %12s | %7s | %7s\n" \
    "$author" "$commits" "$added" "$deleted" "$classes" "$methods"
done

echo "===================="
echo " End of Report "
echo "===================="
