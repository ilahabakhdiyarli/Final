# ASOS — PDP: stokda olmayan ölçü (PDP)

Test mühiti: https://www.asos.com

Qeyd: test konkret məhsuldan istifadə edir (adidas Samba OG — 08.10 DevTools ilə bir neçə ölçünün "Out of stock" olduğu təsdiqləndi). Stok dəyişərsə test datası yenilənməlidir — buna görə regression dəstinə daxil edilmir.

## Stokda olmayan ölçünün göstərilməsi — TC-PDP-04

* "chrome" brauzeri aç ve keçid et "https://www.asos.com/adidas-originals/adidas-originals-samba-og-trainers-in-maroon-croc-print/prd/209137410"
* "Size Selector" görsensin deye maksimum "10" saniye gözle
* "Out Of Stock Size Option" elementlerinin sayı en az "1" olmalıdır
* Brauzeri bağla
