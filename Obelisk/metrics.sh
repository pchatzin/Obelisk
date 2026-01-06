#!/bin/bash

echo "===================="
echo " Git Metrics Report"
echo "===================="
printf "%-30s | %-8s | %-12s | %-13s\n" "User" "Commits" "Lines Added" "Lines Deleted"
echo "-------------------------------------------------------------------------------"

# Get all unique authors across all branches, handle spaces properly
git log --all --format='%aN' | sort -u | while IFS= read -r author; do
    # Count commits
    commits=$(git log --all --author="$author" --pretty=oneline | wc -l)

    # Sum lines added/deleted
    stats=$(git log --all --author="$author" --pretty=tformat: --numstat \
        | awk '{added+=$1; deleted+=$2} END {print added, deleted}')

    added=$(echo $stats | cut -d' ' -f1)
    deleted=$(echo $stats | cut -d' ' -f2)

    # Default to 0 if empty
    [ -z "$added" ] && added=0
    [ -z "$deleted" ] && deleted=0

    printf "%-30s | %-8s | %-12s | %-13s\n" "$author" "$commits" "$added" "$deleted"
done

echo "===================="
echo " End of Report"
echo "===================="
