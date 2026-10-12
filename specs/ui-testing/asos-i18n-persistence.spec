# ASOS — Lokalizasiya: seçimin saxlanması (I18N)

Test mühiti: https://www.asos.com

Qeyd: Bag səhifəsi bot-detection səbəbindən daxil edilməyib — yoxlama PLP → PDP keçidində aparılır.

## Ölkə/valyuta seçiminin səhifələr arası saxlanması — TC-I18N-02

* "chrome" brauzeri aç ve keçid et "https://www.asos.com/women/dresses/cat/?cid=8799&refine=base_colour:4"
* "Country Selector Button" görsensin deye maksimum "10" saniye gözle
* "Country Selector Button" elementine klik et
* "Shop In Country Select" görsensin deye maksimum "10" saniye gözle
* "Shop In Country Select" dropdown-undan "United Kingdom" metnini seç
* "Update Preferences Button" elementine klik et
* Sehifenin tam yüklenmesini gözle
* "https://www.asos.com/women/dresses/cat/?cid=8799&refine=base_colour:4" adresine keçid et
* "PLP Product Price" görsensin deye maksimum "10" saniye gözle
* "PLP Product Price" elementin içinde "£" yazısı var
* "Product Card Link" elementine klik et
* "Product Price" görsensin deye maksimum "10" saniye gözle
* "Product Price" elementin içinde "£" yazısı var
* Brauzeri bağla
