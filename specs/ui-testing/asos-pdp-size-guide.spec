# ASOS — PDP Size Guide (PDP)

Test mühiti: https://www.asos.com
Qeyd (12.10.2026): PLP-dəki ilk məhsulun PDP-sində artıq "Fit Assistant" linki yox, "Size Guide" düyməsi var (data-testid="size-guide-button"); modal ayrıca iframe-dədir (data-testid="size-guide-iframe"), başlığı <h1 id="modalTitle">Size Guide</h1> — hər ikisi DevTools ilə təsdiqləndi.

## Size Guide linkinin açılması — TC-PDP-07

* "chrome" brauzeri aç ve keçid et "https://www.asos.com/women/dresses/cat/?cid=8799&refine=base_colour:4"
* "Product Card Link" görsensin deye maksimum "10" saniye gözle
* "Product Card Link" elementine klik et
* "Size Guide Link" görsensin deye maksimum "10" saniye gözle
* "Size Guide Link" elementine klik et
* "Size Guide Iframe" iframe-ine keç
* "Size Guide Modal Title" görsensin deye maksimum "10" saniye gözle
* "Size Guide Modal Title" elementinin metni "Size Guide" olmalıdır
* Iframe-den esas sehifeye qayıt
* Brauzeri bağla