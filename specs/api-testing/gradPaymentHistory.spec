# GRAD-303 — GET /bill-payments (Ödəniş tarixçəsi — filtr və səhifələmə)

## 1. Parametrsiz sorğu — default page/limit
tags: get, bill-payments, positive

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
* Add as a header "Idempotency-Key" = "idem-key-p01-default-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":10}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Cavabı çap et
* Status kodunun "200" olmalıdır
* Json cavabında "page" deyeri integer "1" beraberdir
* Json cavabında "limit" deyeri integer "10" beraberdir
* Json cavabında "total" deyeri integer "1" beraberdir
* Json cavabında "items" massivinin ölçüsü "1" olmalıdır

## 2. Pagination — 18 ödəniş, page=2
tags: get, bill-payments, positive, pagination

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
* Add body as text "{\"accountId\":\"acc_002\",\"initialAmount\":50}"
* Post request and display respons
* Status kodunun "201" olmalıdır
* Save value of "id" as "cardId"

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-p02-001-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":1}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-p02-002-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":1}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-p02-003-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":1}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-p02-004-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":1}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-p02-005-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":1}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-p02-006-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":1}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-p02-007-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":1}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-p02-008-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":1}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-p02-009-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":1}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-p02-010-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":1}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-p02-011-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":1}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-p02-012-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":1}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-p02-013-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":1}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-p02-014-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":1}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-p02-015-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":1}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-p02-016-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":1}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-p02-017-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":1}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-p02-018-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":1}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments?page=2&limit=10"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Cavabı çap et
* Status kodunun "200" olmalıdır
* Json cavabında "page" deyeri integer "2" beraberdir
* Json cavabında "limit" deyeri integer "10" beraberdir
* Json cavabında "total" deyeri integer "18" beraberdir
* Json cavabında "totalPages" deyeri integer "2" beraberdir
* Json cavabında "items" massivinin ölçüsü "8" olmalıdır
* Json cavabında "items.createdAt" siyahısı azalan sırada olmalıdır

## 3. Mövcud olmayan səhifə — boş nəticə
tags: get, bill-payments, positive, pagination

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
* Add as a header "Idempotency-Key" = "idem-key-p03-001-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":10}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-p03-002-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":10}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments?page=3&limit=10"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Cavabı çap et
* Status kodunun "200" olmalıdır
* Json cavabında "page" deyeri integer "3" beraberdir
* Json cavabında "total" deyeri integer "2" beraberdir
* Json cavabında "totalPages" deyeri integer "1" beraberdir
* Json cavabında "items" massivinin ölçüsü "0" olmalıdır

## 4. limit=0 — qəbul olunmur
tags: get, bill-payments, negative, validation

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
* Add Endpoint "/bill-payments?limit=0"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Cavabı çap et
* Status kodunun "400" olmalıdır
* Json cavabında "code" deyeri "VALIDATION_ERROR" beraberdir

## 5. limit=51 — qəbul olunmur
tags: get, bill-payments, negative, validation

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
* Add Endpoint "/bill-payments?limit=51"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Cavabı çap et
* Status kodunun "400" olmalıdır
* Json cavabında "code" deyeri "VALIDATION_ERROR" beraberdir

## 6. page=0 — qəbul olunmur
tags: get, bill-payments, negative, validation

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
* Add Endpoint "/bill-payments?page=0"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Cavabı çap et
* Status kodunun "400" olmalıdır
* Json cavabında "code" deyeri "VALIDATION_ERROR" beraberdir

## 7. page=-1 — qəbul olunmur
tags: get, bill-payments, negative, validation

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
* Add Endpoint "/bill-payments?page=-1"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Cavabı çap et
* Status kodunun "400" olmalıdır
* Json cavabında "code" deyeri "VALIDATION_ERROR" beraberdir

## 8. page=abc — rəqəm olmayan dəyər
tags: get, bill-payments, negative, validation

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
* Add Endpoint "/bill-payments?page=abc"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Cavabı çap et
* Status kodunun "400" olmalıdır
* Json cavabında "code" deyeri "VALIDATION_ERROR" beraberdir

## 9. Mövcud olmayan tarix (from=2026-13-01)
tags: get, bill-payments, negative, validation

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
* Add Endpoint "/bill-payments?from=2026-13-01"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Cavabı çap et
* Status kodunun "400" olmalıdır
* Json cavabında "code" deyeri "VALIDATION_ERROR" beraberdir

## 10. from > to
tags: get, bill-payments, negative, validation

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
* Add Endpoint "/bill-payments?from=2026-09-30&to=2026-09-01"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Cavabı çap et
* Status kodunun "400" olmalıdır
* Json cavabında "code" deyeri "VALIDATION_ERROR" beraberdir

## 11. from = to = bu gün
tags: get, bill-payments, positive, filter

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
* Add as a header "Idempotency-Key" = "idem-key-p11-today-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":10}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments?from=${today}&to=${today}"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Cavabı çap et
* Status kodunun "200" olmalıdır
* Json cavabında "total" deyeri integer "1" beraberdir

## 12. billerId=citynet filtri
tags: get, bill-payments, positive, filter

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
* Add as a header "Idempotency-Key" = "idem-key-p12-citynet-aaa"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"citynet\",\"subscriberNo\":\"CN123456\",\"amount\":10}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Add as a header "Authorization" = "Bearer ${token}"
* Add as a header "Content-Type" = "application/json"
* Add as a header "Idempotency-Key" = "idem-key-p12-azerisiq-bbb"
* Add body as text "{\"cardId\":\"${cardId}\",\"billerId\":\"azerisiq\",\"subscriberNo\":\"1234567890\",\"amount\":10}"
* Post request and display respons
* Status kodunun "201" olmalıdır

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments?billerId=citynet"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Cavabı çap et
* Status kodunun "200" olmalıdır
* Json cavabında "total" deyeri integer "1" beraberdir
* Json cavabında "items.billerId" siyahısındakı bütün deyerler "citynet" olmalıdır

## 13. Token yoxdur
tags: get, bill-payments, negative, security

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/bill-payments"
* Get request and display respons
* Cavabı çap et
* Status kodunun "401" olmalıdır