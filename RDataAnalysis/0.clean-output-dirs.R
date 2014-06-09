# Cleans the output directories for charts and tables
# Does not delete the transformed.txt and measures.txt data files

unlink("charts/*.pdf", recursive=TRUE)
unlink("charts/*/*.pdf", recursive=TRUE)

unlink("tables/*.csv", recursive=TRUE)