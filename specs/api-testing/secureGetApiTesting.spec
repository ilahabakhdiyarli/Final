# Secure GET Testleri — api.anarabbas.com (Bearer token tələb edir)

Bu spec Accounts, Transactions, Me və Loans qruplarının GET endpoint-lərini əhatə edir. Hər ssenaridən əvvəl `Login Test User` konsepti icra olunur.

* Login Test User

## GET /accounts — bütün hesablar
tags: get, accounts, token

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/accounts"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "[0].id" deyeri boş olmamalıdır
* Json cavabında "[0].currency" deyeri boş olmamalıdır

## GET /accounts — token olmadan 401
tags: get, accounts, error

* API e GET request gönder "https://api.anarabbas.com/accounts"
* Status kodunun "401" olmalıdır

## GET /accounts/:id — tək hesab detalı
tags: get, accounts, token

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/accounts/acc_001"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "id" deyeri "acc_001" beraberdir
* Json cavabında verilen "balance" deyeri ededdir

## GET /accounts/:id/balance-history — balans tarixçəsi
tags: get, accounts, history

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/accounts/acc_001/balance-history"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "[0].date" deyeri boş olmamalıdır
* Json cavabında verilen "[0].balance" deyeri ededdir

## GET /accounts/:id/cards — hesaba bağlı kartlar
tags: get, accounts, cards

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/accounts/acc_002/cards"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "[0].brand" deyeri boş olmamalıdır
* Json cavabında "[0].last4" deyeri boş olmamalıdır

## GET /transactions — filter ilə əməliyyat siyahısı
tags: get, transactions

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/transactions?status=completed&type=transfer"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "[0].id" deyeri boş olmamalıdır
* Json cavabında "[0].status" deyeri "completed" beraberdir

## GET /transactions/:id — tək əməliyyat
tags: get, transactions

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/transactions/txn_001"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "id" deyeri "txn_001" beraberdir

## GET /transactions/:id — mövcud olmayan 404
tags: get, transactions, error

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/transactions/txn_nonexistent"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Status kodunun "404" olmalıdır

## GET /me/accounts — daxil olmuş userin hesabları
tags: get, me, accounts

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/me/accounts"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında verilen "total" deyeri ededdir
* Json cavabında "data[0].id" deyeri boş olmamalıdır

## GET /me/accounts/:id — konkret hesab
tags: get, me, accounts

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/me/accounts/acc_001"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "id" deyeri "acc_001" beraberdir

## GET /me/accounts/:id — başqa userin hesabı 403
tags: get, me, accounts, forbidden

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/me/accounts/acc_003"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Status kodunun "403" olmalıdır

## GET /me/transfers — transfer tarixçəsi
tags: get, me, transfers

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/me/transfers"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında verilen "total" deyeri ededdir

## GET /transfers/:id — transfer receipt
tags: get, transfers

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/transfers/txn_001"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "receipt.id" deyeri "txn_001" beraberdir
* Json cavabında "receipt.status" deyeri "completed" beraberdir

## GET /loans — kredit siyahısı
tags: get, loans

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/loans"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında verilen "total" deyeri ededdir

## GET /loans/:id — tək kredit tam nested
tags: get, loans

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/loans/loan_002"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Status kodunun "200" olmalıdır
* Json cavabında "loan.id" deyeri "loan_002" beraberdir
* Json cavabında verilen "loan.monthlyPayment" deyeri ededdir

## GET /loans/:id — mövcud olmayan kredit 404
tags: get, loans, error

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/loans/loan_nonexistent"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Status kodunun "404" olmalıdır