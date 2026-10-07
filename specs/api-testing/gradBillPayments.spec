# GRAD-302 — POST /bill-payments (Kommunal ödəniş etmək) — Faza 1

## 70. Etibarlı ödəniş — CityNet (faiz əsaslı komissiya)
tags: post, bill-payments, positive

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
* Add as a header "Idempotency-Key" = "idem-key-070-citynet-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"citynet\",\"subscriberNo\":\"CN123456\",\"amount\":50}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "201" olmalıdır
* Json cavabında "status" deyeri "SUCCESS" beraberdir
* Json cavabında "biller.id" deyeri "citynet" beraberdir
* Json cavabında "subscriberNo" deyeri "CN123456" beraberdir
* Json cavabında "amount" deyeri integer "50" beraberdir
* Json cavabında "fee" deyeri "0.5" beraberdir
* Json cavabında "total" deyeri "50.5" beraberdir
* Json cavabında "currency" deyeri "AZN" beraberdir
* Json cavabında "cardId" deyeri "${cardId}" beraberdir
* Json cavabında "balanceAfter" deyeri "49.5" beraberdir

## 71. Aztelekom — sabit komissiya (0.50)
tags: post, bill-payments, positive

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
* Add as a header "Idempotency-Key" = "idem-key-071-aztelekom-bbb"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"aztelekom\",\"subscriberNo\":\"1234567\",\"amount\":10}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "201" olmalıdır
* Json cavabında "fee" deyeri "0.5" beraberdir
* Json cavabında "total" deyeri "10.5" beraberdir

## 72. Idempotency-Key header yoxdur
tags: post, bill-payments, negative

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
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"cardId\":\"vcd_fake\",\"billerId\":\"citynet\",\"subscriberNo\":\"CN123456\",\"amount\":50}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "400" olmalıdır
* Json cavabında "code" deyeri "IDEMPOTENCY_KEY_REQUIRED" beraberdir

## 73. Idempotency-Key çox qısa
tags: post, bill-payments, negative

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
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "short"
* Add body as text "{\"cardId\":\"vcd_fake\",\"billerId\":\"citynet\",\"subscriberNo\":\"CN123456\",\"amount\":50}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "400" olmalıdır
* Json cavabında "code" deyeri "IDEMPOTENCY_KEY_INVALID" beraberdir

## 74. Eyni Idempotency-Key + eyni body — təkrar göndərmə
tags: post, bill-payments, positive, idempotency

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
* Header "Location" movcud olmalıdır
* Save value of "id" as "cardId"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-074-replay-ccc"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"citynet\",\"subscriberNo\":\"CN123456\",\"amount\":50}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Save value of "id" as "paymentId1"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-074-replay-ccc"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"citynet\",\"subscriberNo\":\"CN123456\",\"amount\":50}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "200" olmalıdır
* Json cavabında "id" deyeri "paymentId1" ile eynidir
* Header "Idempotent-Replay" deyerinde "true" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/cards/${cardId}"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Cavabı çap et
* Json cavabında "availableBalance" deyeri "49.5" beraberdir

## 75. Eyni Idempotency-Key, fərqli body
tags: post, bill-payments, negative, idempotency

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
* Add as a header "Idempotency-Key" = "idem-key-075-conflict-ddd"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"citynet\",\"subscriberNo\":\"CN123456\",\"amount\":50}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-075-conflict-ddd"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"citynet\",\"subscriberNo\":\"CN123456\",\"amount\":60}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "409" olmalıdır
* Json cavabında "code" deyeri "IDEMPOTENCY_CONFLICT" beraberdir

## 76. Mövcud olmayan billerId
tags: post, bill-payments, negative

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
* Add as a header "Idempotency-Key" = "idem-key-076-nobiller-eee"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"faketech\",\"subscriberNo\":\"CN123456\",\"amount\":50}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "404" olmalıdır
* Json cavabında "code" deyeri "BILLER_NOT_FOUND" beraberdir

## 77. Mövcud olmayan cardId
tags: post, bill-payments, negative

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
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-077-nocard-fff"
* Add body as text "{\"cardId\":\"vcd_notreal\",\"billerId\":\"citynet\",\"subscriberNo\":\"CN123456\",\"amount\":50}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "404" olmalıdır
* Json cavabında "code" deyeri "CARD_NOT_FOUND" beraberdir

## 78. Token yoxdur/etibarsızdır
tags: post, bill-payments, negative, security

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-078-notoken-ggg"
* Add body as text "{\"cardId\":\"vcd_fake\",\"billerId\":\"citynet\",\"subscriberNo\":\"CN123456\",\"amount\":50}"
* Post request and display respons
* Status kodunun "401" olmalıdır

## 79. amount mətn kimi göndərilir
tags: post, bill-payments, negative

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
* Add as a header "Idempotency-Key" = "idem-key-079-textamount-hhh"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"citynet\",\"subscriberNo\":\"CN123456\",\"amount\":\"10\"}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "400" olmalıdır
* Json cavabında "code" deyeri "VALIDATION_ERROR" beraberdir

## 80. Subscriber format uyğunsuzluğu — Azərişıq
tags: post, bill-payments, negative

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
* Add as a header "Idempotency-Key" = "idem-key-080-badsub-iii"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"123\",\"amount\":50}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "422" olmalıdır
* Json cavabında "code" deyeri "INVALID_SUBSCRIBER" beraberdir

## 81. CityNet fee sərhədi — amount=5 (minimuma clamp)
tags: post, bill-payments, positive, fee

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
* Add as a header "Idempotency-Key" = "idem-key-081-fee5-jjj"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"citynet\",\"subscriberNo\":\"CN123456\",\"amount\":5}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "201" olmalıdır
* Json cavabında "fee" deyeri "0.2" beraberdir
* Json cavabında "total" deyeri "5.2" beraberdir

## 82. CityNet fee sərhədi — amount=20 (minimum dəqiq)
tags: post, bill-payments, positive, fee

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
* Add as a header "Idempotency-Key" = "idem-key-082-fee20-kkk"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"citynet\",\"subscriberNo\":\"CN123456\",\"amount\":20}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "201" olmalıdır
* Json cavabında "fee" deyeri "0.2" beraberdir
* Json cavabında "total" deyeri "20.2" beraberdir

## 83. CityNet fee sərhədi — amount=21 (clamp yoxdur)
tags: post, bill-payments, positive, fee

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
* Add as a header "Idempotency-Key" = "idem-key-083-fee21-lll"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"citynet\",\"subscriberNo\":\"CN123456\",\"amount\":21}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "201" olmalıdır
* Json cavabında "fee" deyeri "0.21" beraberdir
* Json cavabında "total" deyeri "21.21" beraberdir

## 84. CityNet fee sərhədi — amount=150 (maksimum)
tags: post, bill-payments, positive, fee

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
* Add body as text "{\"accountId\":\"acc_002\",\"initialAmount\":200}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Save value of "id" as "cardId"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-084-fee150-mmm"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"citynet\",\"subscriberNo\":\"CN123456\",\"amount\":150}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "201" olmalıdır
* Json cavabında "fee" deyeri "1.5" beraberdir
* Json cavabında "total" deyeri "151.5" beraberdir

## 85. Amount minimumdan aşağı (CityNet, 4.99)
tags: post, bill-payments, negative

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
* Add as a header "Idempotency-Key" = "idem-key-085-toolow-nnn"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"citynet\",\"subscriberNo\":\"CN123456\",\"amount\":4.99}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "422" olmalıdır
* Json cavabında "code" deyeri "AMOUNT_OUT_OF_RANGE" beraberdir

## 86. Amount maksimumdan yuxarı (CityNet, 150.01)
tags: post, bill-payments, negative

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
* Add body as text "{\"accountId\":\"acc_002\",\"initialAmount\":200}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Save value of "id" as "cardId"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-086-toohigh-ooo"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"citynet\",\"subscriberNo\":\"CN123456\",\"amount\":150.01}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "422" olmalıdır
* Json cavabında "code" deyeri "AMOUNT_OUT_OF_RANGE" beraberdir

## 87. Per-transaction limit (300) aşılır
tags: post, bill-payments, negative, limit

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
* Add body as text "{\"accountId\":\"acc_002\",\"initialAmount\":1000}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Save value of "id" as "cardId"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-087-perlimit-ppp"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":300.01}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "422" olmalıdır
* Json cavabında "code" deyeri "PER_TRANSACTION_LIMIT" beraberdir

## 88. Per-transaction limit sərhədi — 300 dəqiq (keçir)
tags: post, bill-payments, positive, limit

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
* Add body as text "{\"accountId\":\"acc_002\",\"initialAmount\":1000}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Save value of "id" as "cardId"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-088-perlimit300-qqq"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":300}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "201" olmalıdır
* Json cavabında "total" deyeri "300" beraberdir

## 89. Daily limit (500) aşılır — zəncirvari ödənişlər
tags: post, bill-payments, negative, limit

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
* Add body as text "{\"accountId\":\"acc_002\",\"initialAmount\":1000}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Save value of "id" as "cardId"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-089-daily1-rrr"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":300}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-089-daily2-sss"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":200}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-089-daily3-ttt"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":1}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "422" olmalıdır
* Json cavabında "code" deyeri "DAILY_LIMIT_EXCEEDED" beraberdir

## 90. Insufficient funds — balansdan böyük ödəniş
tags: post, bill-payments, negative

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
* Add body as text "{\"accountId\":\"acc_002\",\"initialAmount\":49.50}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Save value of "id" as "cardId"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-090-insuff-uuu"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":49.51}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "422" olmalıdır
* Json cavabında "code" deyeri "INSUFFICIENT_FUNDS" beraberdir

## 91. Insufficient funds sərhədi — balansın tam məbləği (keçir)
tags: post, bill-payments, positive

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
* Add body as text "{\"accountId\":\"acc_002\",\"initialAmount\":49.50}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Save value of "id" as "cardId"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-091-exact-vvv"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":49.50}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "201" olmalıdır
* Json cavabında "balanceAfter" deyeri "0" beraberdir

## 92. FROZEN kartla ödəniş cəhdi
tags: post, bill-payments, negative

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
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-092-frozen-www"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":50}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "422" olmalıdır
* Json cavabında "code" deyeri "CARD_NOT_ACTIVE" beraberdir

## 93. BLOCKED kartla ödəniş cəhdi
tags: post, bill-payments, negative

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

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-093-blocked-xxx"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":50}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "422" olmalıdır
* Json cavabında "code" deyeri "CARD_NOT_ACTIVE" beraberdir

## 94. USD kartla ödəniş — CURRENCY_NOT_SUPPORTED (AC-14)
tags: post, bill-payments, negative, currency

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/auth/login"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"email\":\"user2@test.com\",\"password\":\"User1234\"}"
* Post request and display respons
* Status kodunun "200" olmalıdır
* Save value of "token" as "token"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/cards"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add body as text "{\"accountId\":\"acc_003\",\"initialAmount\":100}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Save value of "id" as "cardId"
* Json cavabında "currency" deyeri "USD" beraberdir

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-094-usdcurrency-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":50}"
* Post request and display respons
* Cavabı çap et
* Status kodunun "422" olmalıdır
* Json cavabında "code" deyeri "CURRENCY_NOT_SUPPORTED" beraberdir