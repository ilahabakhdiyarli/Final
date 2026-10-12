# ASOS — Axtarış: SQL-injection formatlı input (SRCH)

Test mühiti: https://www.asos.com

Qeyd (08.10 tapıntısı): production asos.com-da Akamai WAF SQL formatlı axtarış sorğusunu həm adi brauzerdə, həm avtomatlaşdırılmış brauzerdə "Access Denied" səhifəsi ilə bloklayır. Server xətası və ya stack-trace göstərilmir.

## SQL-injection formatlı axtarış inputu — TC-SRCH-09

* "chrome" brauzeri aç ve keçid et "https://www.asos.com"
* "Search Input" görsensin deye maksimum "10" saniye gözle
* "Search Input" elementine "' OR '1'='1" yaz ve Enter düymesine bas
* Sehifenin tam yüklenmesini gözle
* Sehifede "Access Denied" yazısı olmalıdır
* Brauzeri bağla
