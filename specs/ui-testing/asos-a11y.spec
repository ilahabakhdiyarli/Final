# ASOS — Accessibility (A11Y) avtomat yoxlamaları

Test mühiti: https://www.asos.com
Qeyd: yalnız DOM atributları yoxlanılır (aria-label, svg title + aria-labelledby, label[for], alt) — mətn dəyəri təxmin edilmir, yalnız mövcudluğu və boş olmaması.

## Yalnız ikonlu düymələrin əlçatan adı (WCAG 4.1.2) — TC-A11Y-07

* "chrome" brauzeri aç ve keçid et "https://www.asos.com"
* "My Account Icon" görsensin deye maksimum "10" saniye gözle
* "Saved Items Icon With Accessible Name" elementlerinin sayı en az "1" olmalıdır
* "My Account Icon With Accessible Name" elementlerinin sayı en az "1" olmalıdır
* "Bag Icon With Accessible Name" elementlerinin sayı en az "1" olmalıdır
* "Search Button Icon Labelled By Title" elementlerinin sayı en az "1" olmalıdır
* "Search Button Icon Title" elementlerinin sayı en az "1" olmalıdır
* Brauzeri bağla

## Login formunda email sahəsinin label əlaqəsi (WCAG 1.3.1 / 4.1.2) — TC-A11Y-05

* "chrome" brauzeri aç ve keçid et "https://www.asos.com"
* "My Account Icon" görsensin deye maksimum "10" saniye gözle
* "My Account Icon" elementine klik et
* "Sign In Link" görsensin deye maksimum "10" saniye gözle
* "Sign In Link" elementine klik et
* "ASOS Email Input" görsensin deye maksimum "10" saniye gözle
* "Email Input Label" elementlerinin sayı en az "1" olmalıdır
* Brauzeri bağla

## Məhsul şəkillərində alt atributu (WCAG 1.1.1) — TC-A11Y-02

* "chrome" brauzeri aç ve keçid et "https://www.asos.com/women/dresses/cat/?cid=8799&refine=base_colour:4"
* "Product Cards" görsensin deye maksimum "10" saniye gözle
* "Product Card Image With Alt" elementlerinin sayı en az "1" olmalıdır
* "Product Card Image Without Alt" sehifede mövcud olmamalıdır
* Brauzeri bağla
