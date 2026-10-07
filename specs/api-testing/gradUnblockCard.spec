# GRAD-204 — POST /cards/{id}/unblock (Dondurulmuş kartı açmaq)


## 60. FROZEN kart unblock edilir
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
* Add Endpoint "/cards/${cardId}/unblock"
* Add as a header "Authorization" = "Bearer ${token}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "200" olmalıdır
* Json cavabında "status" deyeri "ACTIVE" beraberdir
* Json cavabında "blockReason" deyeri null olmalıdır
* Json cavabında "blockedAt" deyeri null olmalıdır

## 61. ACTIVE kart unblock edilir (hələ dondurulmayıb)
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
* Add Endpoint "/cards/${cardId}/unblock"
* Add as a header "Authorization" = "Bearer ${token}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "409" olmalıdır
* Json cavabında "code" deyeri "NOT_FROZEN" beraberdir

## 62. BLOCKED kart unblock edilir
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
* Add Endpoint "/cards/${cardId}/unblock"
* Add as a header "Authorization" = "Bearer ${token}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "409" olmalıdır
* Json cavabında "code" deyeri "PERMANENTLY_BLOCKED" beraberdir

## 63. Token yoxdur/etibarsızdır
tags: post, cards, negative, security

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/cards/vcd_notreal/unblock"
* Post request and display respons
* Status kodunun "401" olmalıdır

## 64. Mövcud olmayan kart id
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
* Add Endpoint "/cards/vcd_notreal/unblock"
* Add as a header "Authorization" = "Bearer ${token}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "404" olmalıdır
* Json cavabında "code" deyeri "NOT_FOUND" beraberdir