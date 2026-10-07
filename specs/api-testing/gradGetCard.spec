# GRAD-202 — GET /cards/{id} (Kart məlumatının alınması)

## 46. Yeni buraxılmış kartın məlumatı
tags: get, cards, positive

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
* Add Endpoint "/cards"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"accountId\":\"acc_002\",\"initialAmount\":100,\"label\":\"Test Card\"}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Save value of "id" as "cardId"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/cards/${cardId}"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Cavabı çap et
* Status kodunun "200" olmalıdır
* Json cavabında "accountId" deyeri "acc_002" beraberdir
* Json cavabında "status" deyeri "ACTIVE" beraberdir
* Json cavabında "availableBalance" deyeri integer "100" beraberdir
* Json cavabında "limits.dailyOnline" deyeri integer "500" beraberdir
* Json cavabında "limits.perTransaction" deyeri integer "300" beraberdir
* Json cavabında "spentToday" deyeri integer "0" beraberdir
* Json cavabında "blockReason" deyeri null olmalıdır
* Json cavabında "id" açarı mövcud olmalıdır

## 47. Ödənişdən sonra balansın düzgün əks olunması
tags: get, cards, positive

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
* Add Endpoint "/cards"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"accountId\":\"acc_002\",\"initialAmount\":100}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Save value of "id" as "cardId"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-047-balance-zzz"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"citynet\",\"subscriberNo\":\"CN123456\",\"amount\":50}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/cards/${cardId}"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Cavabı çap et
* Status kodunun "200" olmalıdır
* Json cavabında "availableBalance" deyeri "49.5" beraberdir

## 48. Başqa istifadəçinin kartına GET cəhdi (IDOR)
tags: get, cards, negative, security

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
* Add Endpoint "/cards"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"accountId\":\"acc_002\",\"initialAmount\":100}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Save value of "id" as "cardId"

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
* Add Endpoint "/cards/${cardId}"
* Add as a header "Authorization" = "Bearer ${token2}"
* Get request and display respons
* Cavabı çap et
* Status kodunun "404" olmalıdır
* Json cavabında "code" deyeri "NOT_FOUND" beraberdir

## 49. Token yoxdur/etibarsızdır
tags: get, cards, negative, security

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/cards/vcd_notreal"
* Get request and display respons
* Status kodunun "401" olmalıdır

## 50. Mövcud olmayan kart id
tags: get, cards, negative

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
* Add Endpoint "/cards/vcd_notreal"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Cavabı çap et
* Status kodunun "404" olmalıdır