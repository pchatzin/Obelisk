#!/usr/bin/env bash
set -euo pipefail

echo "===================="
echo " Git Metrics Report "
echo "===================="

# Βεβαιώσου ότι είμαστε σε git repo
git rev-parse --is-inside-work-tree >/dev/null

########################################
#  Συλλογή authors (emails)
########################################
mapfile -t emails < <(git log --all --format='%ae' | sort -u)

# Associative arrays ανά email
declare -A name_by_email commits added deleted

########################################
#  Όνομα ανά email
########################################
for email in "${emails[@]}"; do
  name=$(
    git log --all --author="$email" --format='%an' \
      | sort | uniq -c | sort -rn \
      | awk '{print substr($0, index($0,$2))}' \
      | head -1 || true
  )
  [[ -z "$name" ]] && name="$email"

  # Ομογενοποίηση ονόματος για την Ελισάβετ
  if [[ "$name" == "Elisavet" ]]; then
    name="Elisavet Roumeli"
  fi

  name_by_email["$email"]="$name"
done

########################################
#  Commits ανά email
########################################
for email in "${emails[@]}"; do
  commits["$email"]=$(
    git log --all --no-merges --author="$email" --pretty=oneline \
      | wc -l | tr -d ' '
  )
done

########################################
#  Γραμμές added / deleted ανά email
########################################
for email in "${emails[@]}"; do
  read add del < <(
    git log --all --no-merges --author="$email" --numstat --pretty=tformat: \
      | awk '
          { a=$1; d=$2;
            if (a=="-") a=0;
            if (d=="-") d=0;
            add+=a; del+=d
          }
          END { print add+0, del+0 }'
  )
  added["$email"]="${add:-0}"
  deleted["$email"]="${del:-0}"
done

########################################
#  Άθροιση ανά ΟΝΟΜΑ (όχι email)
########################################

declare -A total_commits total_added total_deleted
declare -A seen_name
names=()

for email in "${emails[@]}"; do
  name="${name_by_email[$email]:-$email}"

  # (ξανα-ομογενοποίηση για σιγουριά)
  if [[ "$name" == "Elisavet" ]]; then
    name="Elisavet Roumeli"
  fi

  if [[ -z "${seen_name[$name]+x}" ]]; then
    names+=("$name")
    seen_name["$name"]=1
  fi

  total_commits["$name"]=$(( ${total_commits["$name"]:-0} + ${commits["$email"]:-0} ))
  total_added["$name"]=$(( ${total_added["$name"]:-0} + ${added["$email"]:-0} ))
  total_deleted["$name"]=$(( ${total_deleted["$name"]:-0} + ${deleted["$email"]:-0} ))
done

########################################
#  Τελικό Report
########################################

printf "\n%-25s | %7s | %10s | %12s\n" \
  "User" "Commits" "Lines Added" "Lines Deleted"
echo "--------------------------------------------------------------"

for name in "${names[@]}"; do
  printf "%-25s | %7s | %10s | %12s\n" \
    "$name" \
    "${total_commits["$name"]:-0}" \
    "${total_added["$name"]:-0}" \
    "${total_deleted["$name"]:-0}"
done

echo "===================="
echo " End of Report "
echo "===================="


