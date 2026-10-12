# ASOS — PLP filterlərin təmizlənməsi (PLP)

Test mühiti: https://www.asos.com
Qeyd: təmizləmədən sonrakı URL-də kataloqun cari qiymət aralığı olur (12.10-da 5-625 → 5-650 dəyişdi) — ona görə aralıq sabit yazılmır, testin əvvəlində slider-dən oxunur, sonra səhifə yenidən açılır (Price bölməsi açıq qalanda Size bölməsi görünmür).

## Bütün filterlərin təmizlənməsi — TC-PLP-03

* "chrome" brauzeri aç ve keçid et "https://www.asos.com/women/dresses/cat/?cid=8799"
* "Filter Button" görsensin deye maksimum "10" saniye gözle
* "Filter Button" elementine klik et
* "Price Range Filter Button" görsensin deye maksimum "10" saniye gözle
* "Price Range Filter Button" elementine klik et
* "Price Min Slider" görsensin deye maksimum "10" saniye gözle
* "Price Min Slider" elementinin "aria-valuenow" atributunu "minPrice" olaraq yadda saxla
* "Price Max Slider" elementinin "aria-valuenow" atributunu "maxPrice" olaraq yadda saxla
* "https://www.asos.com/women/dresses/cat/?cid=8799" adresine keçid et
* "Filter Button" görsensin deye maksimum "10" saniye gözle
* "Filter Button" elementine klik et
* "Size Filter Button" görsensin deye maksimum "10" saniye gözle
* "Size Filter Button" elementine klik et
* "Size Option EU 36" görsensin deye maksimum "10" saniye gözle
* "Size Option EU 36" elementine klik et
* Sehifenin tam yüklenmesini gözle
* URL "size:23" içermelidir
* "Clear Filters Button" görsensin deye maksimum "10" saniye gözle
* "Clear Filters Button" elementine klik et
* Sehifenin tam yüklenmesini gözle
* URL "https://www.asos.com/women/dresses/cat/?cid=8799&currentpricerange=${minPrice}-${maxPrice}" olmalıdır
* Brauzeri bağla