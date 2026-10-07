# GRAD-203 — POST /cards/{id}/block (Kartı bloklamaq / dondurmaq)

## 51. ACTIVE kart, TEMPORARY səbəbi ilə dondurulur
tags: post, cards, positive

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
* Add Endpoint "/cards/${cardId}/block"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"reason\":\"TEMPORARY\"}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "200" olmalıdır
* Json cavabında "status" deyeri "FROZEN" beraberdir
* Json cavabında "blockReason" deyeri "TEMPORARY" beraberdir
* Json cavabında "blockedAt" açarı mövcud olmalıdır

## 52. ACTIVE kart, LOST səbəbi ilə həmişəlik bloklanır
tags: post, cards, positive

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
* Add Endpoint "/cards/${cardId}/block"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"reason\":\"LOST\"}"
* Post request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "status" deyeri "BLOCKED" beraberdir

## 53. FROZEN kart, STOLEN səbəbi ilə BLOCKED-ə keçir (eskalasiya)
tags: post, cards, positive

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
* Add Endpoint "/cards/${cardId}/block"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"reason\":\"TEMPORARY\"}"
* Post request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "status" deyeri "FROZEN" beraberdir

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/cards/${cardId}/block"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"reason\":\"STOLEN\"}"
* Post request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "status" deyeri "BLOCKED" beraberdir

## 54. FROZEN kart yenidən TEMPORARY ilə dondurulmaq istənir
tags: post, cards, negative

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
* Add Endpoint "/cards/${cardId}/block"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"reason\":\"TEMPORARY\"}"
* Post request and display respons
* Status kodunun "200" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/cards/${cardId}/block"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"reason\":\"TEMPORARY\"}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "409" olmalıdır
* Json cavabında "code" deyeri "ALREADY_FROZEN" beraberdir

## 55. BLOCKED kart yenidən bloklanmaq istənir
tags: post, cards, negative

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
* Add Endpoint "/cards/${cardId}/block"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"reason\":\"LOST\"}"
* Post request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "status" deyeri "BLOCKED" beraberdir

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/cards/${cardId}/block"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"reason\":\"STOLEN\"}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "409" olmalıdır
* Json cavabında "code" deyeri "CARD_BLOCKED" beraberdir

## 56. Yanlış reason dəyəri
tags: post, cards, negative

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
* Add Endpoint "/cards/${cardId}/block"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"reason\":\"BROKEN\"}"
* Post request and display respons
* Status kodunun "400" olmalıdır
* Json cavabında "code" deyeri "VALIDATION_ERROR" beraberdir

## 57. reason göndərilmir
tags: post, cards, negative

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
* Add Endpoint "/cards/${cardId}/block"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{}"
* Post request and display respons
* Status kodunun "400" olmalıdır
* Json cavabında "code" deyeri "VALIDATION_ERROR" beraberdir

## 58. Token yoxdur/etibarsızdır
tags: post, cards, negative, security

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/cards/vcd_notreal/block"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"reason\":\"TEMPORARY\"}"
* Post request and display respons
* Status kodunun "401" olmalıdır

## 59. Mövcud olmayan kart id
tags: post, cards, negative

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
* Add Endpoint "/cards/vcd_notreal/block"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"reason\":\"TEMPORARY\"}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "404" olmalıdır
* Json cavabında "code" deyeri "NOT_FOUND" beraberdir