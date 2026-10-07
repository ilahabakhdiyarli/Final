# GRAD-103 — DELETE /beneficiaries/{id}

## Movcud alicini silmek
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
* Add body as text "{\"fullName\":\"Sil Meni\",\"iban\":\"AZ81NABZ00000000137010009881\",\"nickname\":\"SilTest1\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Save value of "id" as "benId"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries/${benId}"
* Add as a header "Authorization" = "Bearer ${token}"
* Delete request and display response
* Status kodunun "204" olmalıdır

## Silinmiş alıcı siyahıda görünmür
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
* Add body as text "{\"fullName\":\"Qalacaq Alici\",\"iban\":\"AZ82NABZ00000000137010009882\",\"nickname\":\"QalacaqTest\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Save value of "id" as "keepId"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Silinecek Alici\",\"iban\":\"AZ83NABZ00000000137010009883\",\"nickname\":\"SilinecekTest\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Save value of "id" as "deleteId"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries/${deleteId}"
* Add as a header "Authorization" = "Bearer ${token}"
* Delete request and display response
* Status kodunun "204" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "total" deyeri integer "1" beraberdir
* Json cavabında "items" massivinin ölçüsü "1" olmalıdır
* Json cavabında "items[0].id" deyeri "keepId" ile eynidir

## Eyni id ikinci defe silinir
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
* Add body as text "{\"fullName\":\"Iki Defe Sil\",\"iban\":\"AZ84NABZ00000000137010009884\",\"nickname\":\"IkiDefeTest\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Save value of "id" as "benId"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries/${benId}"
* Add as a header "Authorization" = "Bearer ${token}"
* Delete request and display response
* Status kodunun "204" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries/${benId}"
* Add as a header "Authorization" = "Bearer ${token}"
* Delete request and display response
* Status kodunun "404" olmalıdır

## Token olmadan DELETE sorğusu
* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries/ben_placeholder123"
* Delete request and display response
* Status kodunun "401" olmalıdır

## Movcud olmayan id ile DELETE
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
* Add Endpoint "/beneficiaries/xyz_notreal"
* Add as a header "Authorization" = "Bearer ${token}"
* Delete request and display response
* Status kodunun "404" olmalıdır

## Başqa istifadəçinin alıcısını silmək (IDOR)
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
* Add body as text "{\"fullName\":\"Birinci Istifadeci Alicisi\",\"iban\":\"AZ85NABZ00000000137010009885\",\"nickname\":\"User1Test\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Save value of "id" as "user1BenId"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/auth/login"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"email\":\"user2@test.com\",\"password\":\"User1234\"}"
* Post request and display respons
* Status kodunun "200" olmalıdır
* Save value of "token" as "token2"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries/${user1BenId}"
* Add as a header "Authorization" = "Bearer ${token2}"
* Delete request and display response
* Cavabı çap et
* Status kodunun "404" olmalıdır
* Json cavabında "code" deyeri "NOT_FOUND" beraberdir