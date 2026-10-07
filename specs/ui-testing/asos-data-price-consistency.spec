# ASOS — Data integrity: PLP/PDP qiymət uyğunluğu (DATA)

Test mühiti: https://www.asos.com

## PLP və PDP qiymətinin uyğunluğu — TC-DATA-01

* "chrome" brauzeri aç ve keçid et "https://www.asos.com/women/dresses/cat/?cid=8799&refine=base_colour:4"
* "PLP Product Price" görsensin deye maksimum "10" saniye gözle
* "PLP Product Price" elementinin metnini "plpPrice" olaraq yadda saxla
* "Product Card Link" elementine klik et
* "Product Price" görsensin deye maksimum "10" saniye gözle
* "Product Price" elementinin metni "${plpPrice}" olmalıdır
* Brauzeri bağla