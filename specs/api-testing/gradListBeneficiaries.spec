# GRAD-102 — GET /beneficiaries

## Token olmadan GET sorğusu
* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Get request and display respons
* Status kodunun "401" olmalıdır

## Mövcud olmayan adla axtarış - heç ne tapılmır
* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/auth/login"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"email\":\"user1@test.com\",\"password\":\"User1234\"}"
* Post request and display respons
* Status kodunun "200" olmalıdır
* Save value of "token" as "token"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Query parametri elave et "q" = "Zzzznonexistent"
* Get request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "total" deyeri integer "0" beraberdir
* Json cavabında "items" massivinin ölçüsü "0" olmalıdır

## q parametri minimum uzunluqdan qısadır
* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/auth/login"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"email\":\"user1@test.com\",\"password\":\"User1234\"}"
* Post request and display respons
* Status kodunun "200" olmalıdır
* Save value of "token" as "token"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Query parametri elave et "q" = "a"
* Get request and display respons
* Status kodunun "400" olmalıdır
* Json cavabında "code" deyeri "VALIDATION_ERROR" beraberdir
* Json cavabında "details[0].field" deyeri "q" beraberdir

## Etibarsız sort deyeri ile sorğu
* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/auth/login"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"email\":\"user1@test.com\",\"password\":\"User1234\"}"
* Post request and display respons
* Status kodunun "200" olmalıdır
* Save value of "token" as "token"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Query parametri elave et "sort" = "invalidfield"
* Get request and display respons
* Status kodunun "400" olmalıdır
* Json cavabında "code" deyeri "VALIDATION_ERROR" beraberdir

## Valyuta filtri - yalnız USD alıcılar
* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/auth/login"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"email\":\"user1@test.com\",\"password\":\"User1234\"}"
* Post request and display respons
* Status kodunun "200" olmalıdır
* Save value of "token" as "token"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Kamran Huseynov\",\"iban\":\"AZ11NABZ00000000137010009111\",\"nickname\":\"USDTest1\",\"currency\":\"USD\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Elvin Qasimov\",\"iban\":\"AZ22NABZ00000000137010009222\",\"nickname\":\"AZNTest1\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"

## Parametrsiz siyahı - defolt sıralama yeni-ucdan
* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/auth/login"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"email\":\"user1@test.com\",\"password\":\"User1234\"}"
* Post request and display respons
* Status kodunun "200" olmalıdır
* Save value of "token" as "token"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Tahir Aliyev\",\"iban\":\"AZ41NABZ00000000137010009441\",\"nickname\":\"Birinci\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Samir Nasirov\",\"iban\":\"AZ42NABZ00000000137010009442\",\"nickname\":\"Ikinci\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "total" deyeri integer "2" beraberdir
* Json cavabında "items" massivinin ölçüsü "2" olmalıdır
* Json cavabında "items[0].nickname" deyeri "Ikinci" beraberdir

## Adla sıralama - əlifba sırası ilə (asc)
* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/auth/login"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"email\":\"user1@test.com\",\"password\":\"User1234\"}"
* Post request and display respons
* Status kodunun "200" olmalıdır
* Save value of "token" as "token"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Cavid Aliyev\",\"iban\":\"AZ51NABZ00000000137010009551\",\"nickname\":\"SortTest1\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Araz Huseynov\",\"iban\":\"AZ52NABZ00000000137010009552\",\"nickname\":\"SortTest2\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Baba Mammadov\",\"iban\":\"AZ53NABZ00000000137010009553\",\"nickname\":\"SortTest3\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Query parametri elave et "sort" = "fullName"
* Query parametri elave et "order" = "asc"
* Get request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "items.fullName" siyahısı artan sırada olmalıdır

## Case-folding axtarış - İ/I herf problemi
* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/auth/login"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"email\":\"user1@test.com\",\"password\":\"User1234\"}"
* Post request and display respons
* Status kodunun "200" olmalıdır
* Save value of "token" as "token"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Nigar Əliyeva\",\"iban\":\"AZ61NABZ00000000137010009661\",\"nickname\":\"NigarTest\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Query parametri elave et "q" = "NİGAR"
* Get request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "items" massivinin ölçüsü en az "1" olmalıdır
* Json cavabında "items[0].fullName" deyerinde "Nigar" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Query parametri elave et "q" = "NIGAR"
* Get request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "items" massivinin ölçüsü en az "1" olmalıdır
* Json cavabında "items[0].fullName" deyerinde "Nigar" olmalıdır

## SQL-injection payload - q parametrinde
* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/auth/login"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"email\":\"user1@test.com\",\"password\":\"User1234\"}"
* Post request and display respons
* Status kodunun "200" olmalıdır
* Save value of "token" as "token"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Query parametri elave et "q" = "' OR 1=1--"
* Get request and display respons
* Status kodu "200" ile "400" arasında olmalıdır

## Kombinə edilmiş filtrlər - q+currency+sort+order
* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/auth/login"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"email\":\"user1@test.com\",\"password\":\"User1234\"}"
* Post request and display respons
* Status kodunun "200" olmalıdır
* Save value of "token" as "token"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Nigar Mammadova\",\"iban\":\"AZ71NABZ00000000137010009771\",\"nickname\":\"ComboMatch\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Nigar Huseynova\",\"iban\":\"AZ72NABZ00000000137010009772\",\"nickname\":\"ComboNoMatch\",\"currency\":\"USD\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Query parametri elave et "q" = "nig"
* Query parametri elave et "currency" = "AZN"
* Query parametri elave et "sort" = "fullName"
* Query parametri elave et "order" = "asc"
* Get request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "total" deyeri integer "1" beraberdir
* Json cavabında "items[0].fullName" deyerinde "Nigar" olmalıdır
* Json cavabında "items[0].currency" deyeri "AZN" beraberdir

## Ləqəbin bir hissəsi ile axtarış
* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/auth/login"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"email\":\"user1@test.com\",\"password\":\"User1234\"}"
* Post request and display respons
* Status kodunun "200" olmalıdır
* Save value of "token" as "token"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Anar Mammadov\",\"iban\":\"AZ33NABZ00000000137010009333\",\"nickname\":\"Anam\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Query parametri elave et "q" = "Ana"
* Get request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "items" massivinin ölçüsü en az "1" olmalıdır
* Json cavabında "items[0].nickname" deyerinde "Ana" olmalıdır