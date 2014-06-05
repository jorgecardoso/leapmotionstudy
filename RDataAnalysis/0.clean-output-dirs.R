# Cleans the output directories
unlink("charts/*.pdf", recursive=TRUE)
unlink("charts/*/*.pdf", recursive=TRUE)

unlink("tables/*.csv", recursive=TRUE)