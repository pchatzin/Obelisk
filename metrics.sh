#!/bin/bash
set -euo pipefail

echo "===================="
echo " Git Metrics Report "
echo "===================="

# Collect authors (emails for stable keys)
mapfile -t emails < <(git log --all --format='%ae' | sort -u)

# Map email -> display name
declare -A name_by_email
for email in "${emails[@]}"; do
  name=$(git log --all --author="$email" --format='%an' | sort | uniq -c | sort -rn | awk '{print substr($0, index($0,$2))}' | head -1 || true)
  [[ -z "$name" ]] && name="$email"
  name_by_email["$email"]="$name"
done

# Commits per email
declare -A commits
for email in "${emails[@]}"; do
  commits["$email"]=$(git log --all --no-merges --author="$email" --pretty=oneline | wc -l | tr -d ' ')
done

# Lines added/deleted per email
declare -A added deleted
for email in "${emails[@]}"; do
  read add del < <(git log --all --no-merges --author="$email" --numstat --pretty=tformat: \
    | awk '{a=$1; d=$2; if(a=="-") a=0; if(d=="-") d=0; add+=a; del+=d} END {print add+0, del+0}')
  added["$email"]="${add:-0}"
  deleted["$email"]="${del:-0}"
done

# Classes & methods per email
declare -A classes methods

while read -r type email count; do
  if [[ "$type" == "C" ]]; then
    classes["$email"]=$(( ${classes["$email"]:-0} + count ))
  else
    methods["$email"]=$(( ${methods["$email"]:-0} + count ))
  fi
done < <(
  for file in $(git ls-files '*.java'); do
    git blame --line-porcelain "$file" | \
      awk -F': ' '
        # Ποιος έγραψε τη γραμμή
        /^author-mail/ {
          gsub(/[<>]/,"",$2);
          email=$2;
          next;
        }

        # Αγνοούμε μόνο-σχόλιο γραμμές
        /^[[:space:]]*\/\// { next }

        # Κλάσεις: οποιαδήποτε γραμμή που περιέχει "class Όνομα"
        /\bclass[[:space:]]+[A-Za-z_][A-Za-z0-9_]*/ {
          if (email != "") {
            classes[email]++
          }
        }

        # Μέθοδοι: γραμμές με ( ) και { που ΔΕΝ είναι if/for/while/switch/catch
        /\(/ && /\)/ && /\{/ &&
        !/\b(if|for|while|switch|catch)\b/ &&
        !/\bclass\b/ {
          if (email != "") {
            methods[email]++
          }
        }

        END {
          for (e in classes) print "C", e, classes[e]
          for (e in methods) print "M", e, methods[e]
        }'
  done
)

# Header
printf "\n%-25s | %7s | %10s | %12s | %7s | %7s\n" "User" "Commits" "Lines Added" "Lines Deleted" "Classes" "Methods"
echo "-----------------------------------------------------------------------------------------------"

# Print rows
for email in "${emails[@]}"; do
  name="${name_by_email[$email]:-$email}"
  printf "%-25s | %7s | %10s | %12s | %7s | %7s\n" \
    "$name" \
    "${commits[$email]:-0}" \
    "${added[$email]:-0}" \
    "${deleted[$email]:-0}" \
    "${classes[$email]:-0}" \
    "${methods[$email]:-0}"
done

echo "===================="
echo " End of Report "
echo "===================="
