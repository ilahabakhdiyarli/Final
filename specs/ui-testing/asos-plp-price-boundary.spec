# ASOS — PLP qiymət filtri sərhədləri (PLP)

Test mühiti: https://www.asos.com
Qeyd: sərhəd dəyəri sabit yazılmır (kataloqun qiymət aralığı dəyişir: 12.10-da max 625 → 650). Slider-in ilkin (sərhəd) dəyəri yadda saxlanılır və ox düyməsindən sonra dəyişmədiyi yoxlanılır.

## Qiymət filterinin minimum sərhəd dəyəri — TC-PLP-11

* "chrome" brauzeri aç ve keçid et "https://www.asos.com/women/dresses/cat/?cid=8799"
* "Filter Button" görsensin deye maksimum "10" saniye gözle
* "Filter Button" elementine klik et
* "Price Range Filter Button" görsensin deye maksimum "10" saniye gözle
* "Price Range Filter Button" elementine klik et
* "Price Min Slider" görsensin deye maksimum "10" saniye gözle
* "Price Min Slider" elementine klik et
* "Price Min Slider" elementinin "aria-valuenow" atributunu "minPrice" olaraq yadda saxla
* "Price Min Slider" elementinde "ARROW_LEFT" düymesine bas
* "Price Min Slider" elementinin "aria-valuenow" atributu "${minPrice}" olmalıdır
* Brauzeri bağla

## Qiymət filterinin maksimum sərhəd dəyəri — TC-PLP-12

* "chrome" brauzeri aç ve keçid et "https://www.asos.com/women/dresses/cat/?cid=8799"
* "Filter Button" görsensin deye maksimum "10" saniye gözle
* "Filter Button" elementine klik et
* "Price Range Filter Button" görsensin deye maksimum "10" saniye gözle
* "Price Range Filter Button" elementine klik et
* "Price Max Slider" görsensin deye maksimum "10" saniye gözle
* "Price Max Slider" elementine klik et
* "Price Max Slider" elementinin "aria-valuenow" atributunu "maxPrice" olaraq yadda saxla
* "Price Max Slider" elementinde "ARROW_RIGHT" düymesine bas
* "Price Max Slider" elementinin "aria-valuenow" atributu "${maxPrice}" olmalıdır
* Brauzeri bağla