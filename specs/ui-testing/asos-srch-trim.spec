# ASOS — Axtarış: əvvəl/sonra boşluqlu input (SRCH)

Test mühiti: https://www.asos.com

## Əvvəl/sonra boşluqlu axtarış — TC-SRCH-07

* "chrome" brauzeri aç ve keçid et "https://www.asos.com"
* "Search Input" görsensin deye maksimum "10" saniye gözle
* "Search Input" elementine "dress" yaz ve Enter düymesine bas
* Sehifenin tam yüklenmesini gözle
* "Result Count Text" görsensin deye maksimum "10" saniye gözle
* "Result Count Text" elementinin metnini "plainCount" olaraq yadda saxla
* "Search Input" elementini temizle
* "Search Input" elementine "  dress  " yaz ve Enter düymesine bas
* Sehifenin tam yüklenmesini gözle
* "Result Count Text" görsensin deye maksimum "10" saniye gözle
* "Result Count Text" elementinin metni "${plainCount}" olmalıdır
* Brauzeri bağla
