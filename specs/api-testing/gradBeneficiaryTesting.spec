# GRAD-101 — POST /beneficiaries (Saxlanmış alıcı yaratmaq)

## 1. Etibarlı sorğu ilə alıcı yaratmaq
tags: post, beneficiaries, positive

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
* Add body as text "{\"fullName\":\"Nigar Əliyeva\",\"iban\":\"AZ77AIIB40060019440123456789\",\"currency\":\"AZN\",\"nickname\":\"Anam\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Header "Location" movcud olmalıdır
* Json cavabında "id" deyeri "^ben_[a-z0-9]{8}$" regex-ine uyğun olmalıdır
* Json cavabında "iban" deyeri "AZ77AIIB40060019440123456789" beraberdir
* Json cavabında "ibanMasked" deyeri "^AZ[0-9]{2} [*]{4} [*]{4} [*]{4} [*]{4} [0-9]{4}$" regex-ine uyğun olmalıdır
* Json cavabında "currency" deyeri "AZN" beraberdir
* Json cavabında "nickname" deyeri "Anam" beraberdir
* Json cavabında "createdAt" deyeri boş olmamalıdır

## 2. IBAN normallaşdırma (boşluq + kiçik hərf)
tags: post, beneficiaries, positive

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
* Add body as text "{\"fullName\":\"Elvin Məmmədov\",\"iban\":\"az77 aiib 4006 0019 4401 2345 6789\",\"currency\":\"AZN\",\"nickname\":\"Dost\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Json cavabında "iban" deyeri "AZ77AIIB40060019440123456789" beraberdir

## 3. nickname göndərilmədikdə null qaytarılır
tags: post, beneficiaries, positive

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
* Add body as text "{\"fullName\":\"Kamran Hüseynov\",\"iban\":\"AZ77AIIB40060019440123456789\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Json cavabında "nickname" deyeri null olmalıdır

## 4. Bir neçə sahə eyni anda səhv
tags: post, beneficiaries, negative

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
* Add body as text "{\"fullName\":\"A\",\"iban\":\"123\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "400" olmalıdır
* Json cavabında "code" deyeri "VALIDATION_ERROR" beraberdir
* Json cavabında "details" massivinin ölçüsü "2" olmalıdır

## 5. fullName uzunluq sərhədi — 1 simvol (rədd)
tags: post, beneficiaries, edge, bva

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
* Add body as text "{\"fullName\":\"V\",\"iban\":\"AZ77AIIB40060019440123456789\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "400" olmalıdır

## 6. fullName uzunluq sərhədi — 2 simvol (qəbul)
tags: post, beneficiaries, edge, bva

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
* Add body as text "{\"fullName\":\"Və\",\"iban\":\"AZ77AIIB40060019440123456789\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

## 7. fullName uzunluq sərhədi — 50 simvol (qəbul)
tags: post, beneficiaries, edge, bva

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
* Add body as text "{\"fullName\":\"VəliQızıAnarHəsənovaYusifTağıyevVəliQızıAnarHəsəno\",\"iban\":\"AZ77AIIB40060019440123456789\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

## 8. fullName uzunluq sərhədi — 51 simvol (rədd)
tags: post, beneficiaries, edge, bva

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
* Add body as text "{\"fullName\":\"VəliQızıAnarHəsənovaYusifTağıyevVəliQızıAnarHəsənov\",\"iban\":\"AZ77AIIB40060019440123456789\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "400" olmalıdır

## 9. fullName format — hərflə başlamır ("---")
tags: post, beneficiaries, negative

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
* Add body as text "{\"fullName\":\"---\",\"iban\":\"AZ77AIIB40060019440123456789\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "400" olmalıdır
* Json cavabında "code" deyeri "VALIDATION_ERROR" beraberdir

## 10. fullName format — rəqəm var ("Ali2")
tags: post, beneficiaries, negative

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
* Add body as text "{\"fullName\":\"Ali2\",\"iban\":\"AZ77AIIB40060019440123456789\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "400" olmalıdır
* Json cavabında "code" deyeri "VALIDATION_ERROR" beraberdir

## 11. Eyni IBAN təkrar əlavə edilir
tags: post, beneficiaries, negative

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
* Add body as text "{\"fullName\":\"Rəşad Quliyev\",\"iban\":\"AZ77AIIB40060019440123456789\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Başqa Ad\",\"iban\":\"AZ77AIIB40060019440123456789\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "409" olmalıdır
* Json cavabında "code" deyeri "BENEFICIARY_EXISTS" beraberdir

## 12. nickname unikallığı (case-insensitive)
tags: post, beneficiaries, negative

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
* Add body as text "{\"fullName\":\"Anar Vəliyev\",\"iban\":\"AZ77AIIB40060019440123456789\",\"currency\":\"AZN\",\"nickname\":\"anam\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Fərid Əhmədov\",\"iban\":\"AZ77AIIB40060019440123456700\",\"currency\":\"AZN\",\"nickname\":\"ANAM\"}"
* Post request and display respons
* Status kodunun "409" olmalıdır
* Json cavabında "code" deyeri "NICKNAME_TAKEN" beraberdir

## 13. Öz hesabının IBAN-ı əlavə edilir
tags: post, beneficiaries, negative

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
* Add body as text "{\"fullName\":\"Özüm\",\"iban\":\"AZ29NABZ00000000137010002855\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "422" olmalıdır
* Json cavabında "code" deyeri "OWN_ACCOUNT" beraberdir

## 14. Alıcı limiti (10) aşılır
tags: post, beneficiaries, edge, bva

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
* Add body as text "{\"fullName\":\"Alıcı Bir\",\"iban\":\"AZ77AIIB40060019440123456700\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Alıcı İki\",\"iban\":\"AZ77AIIB40060019440123456701\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Alıcı Üç\",\"iban\":\"AZ77AIIB40060019440123456702\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Alıcı Dörd\",\"iban\":\"AZ77AIIB40060019440123456703\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Alıcı Beş\",\"iban\":\"AZ77AIIB40060019440123456704\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Alıcı Altı\",\"iban\":\"AZ77AIIB40060019440123456705\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Alıcı Yeddi\",\"iban\":\"AZ77AIIB40060019440123456706\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Alıcı Səkkiz\",\"iban\":\"AZ77AIIB40060019440123456707\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Alıcı Doqquz\",\"iban\":\"AZ77AIIB40060019440123456708\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Alıcı On\",\"iban\":\"AZ77AIIB40060019440123456709\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"On Birinci\",\"iban\":\"AZ77AIIB40060019440123456711\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "422" olmalıdır
* Json cavabında "code" deyeri "LIMIT_REACHED" beraberdir
* Json cavabında verilen "limit" deyeri ededdir

## 15. Token yoxdur
tags: post, beneficiaries, negative, security

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Token Yox\",\"iban\":\"AZ77AIIB40060019440123456789\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "401" olmalıdır

## 16. Saxta token
tags: post, beneficiaries, negative, security

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/beneficiaries"
* Add as a header "Authorization" = "Bearer saxta.token.deyeri"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"fullName\":\"Saxta Token\",\"iban\":\"AZ77AIIB40060019440123456789\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "401" olmalıdır

## 17. Body etibarlı JSON deyil
tags: post, beneficiaries, negative

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
* Add body as text "{fullName: Ali"
* Post request and display respons
* Status kodunun "400" olmalıdır
* Json cavabında "code" deyeri "INVALID_JSON" beraberdir

## 18. SQL-injection payload — fullName
tags: post, beneficiaries, security

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
* Add body as text "{\"fullName\":\"Ali' OR '1'='1\",\"iban\":\"AZ77AIIB40060019440123456789\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "400" olmalıdır
* Json cavabında "code" deyeri "VALIDATION_ERROR" beraberdir

## 19. XSS payload — nickname
tags: post, beneficiaries, security

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
* Add body as text "{\"fullName\":\"Elvin Elvinov\",\"iban\":\"AZ77AIIB40060019440123456789\",\"currency\":\"AZN\",\"nickname\":\"<script>alert(1)</script>\"}"
* Post request and display respons
* Status kodunun "400" olmalıdır

## 20. Azərbaycan hərfləri + defis/apostrof qəbulu
tags: post, beneficiaries, edge, bva

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
* Add body as text "{\"fullName\":\"Vəli-Qızı O'Brien\",\"iban\":\"AZ77AIIB40060019440123456789\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır

## 21. IBAN uzunluq sərhədi — 27 simvol
tags: post, beneficiaries, negative

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
* Add body as text "{\"fullName\":\"Qısa Iban\",\"iban\":\"AZ77AIIB4006001944012345678\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "400" olmalıdır
* Json cavabında "details[0].field" deyeri "iban" beraberdir

## 22. IBAN uzunluq sərhədi — 29 simvol
tags: post, beneficiaries, negative

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
* Add body as text "{\"fullName\":\"Uzun Iban\",\"iban\":\"AZ77AIIB400600194401234567899\",\"currency\":\"AZN\"}"
* Post request and display respons
* Status kodunun "400" olmalıdır
* Json cavabında "details[0].field" deyeri "iban" beraberdir