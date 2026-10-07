# GRAD-301 — GET /billers (Xidmət təchizatçıları kataloqu)


## 65. Parametrsiz sorğu — bütün billerlər qaytarılır
tags: get, billers, positive

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
* Add Endpoint "/billers"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Cavabı çap et
* Status kodunun "200" olmalıdır
* Json cavabında "total" deyeri integer "6" beraberdir
* Json cavabında "items.find{it.id=='azerisiq'}.category" deyeri "UTILITY" beraberdir
* Json cavabında "items.find{it.id=='azerisiq'}.minAmount" deyeri integer "1" beraberdir
* Json cavabında "items.find{it.id=='azerisiq'}.maxAmount" deyeri integer "500" beraberdir
* Json cavabında "items.find{it.id=='azerisiq'}.fee.type" deyeri "NONE" beraberdir
* Json cavabında "items.find{it.id=='azerisiq'}.subscriberPattern" deyeri "^\\d{10}$" beraberdir
* Json cavabında "items.find{it.id=='azersu'}.category" deyeri "UTILITY" beraberdir
* Json cavabında "items.find{it.id=='azersu'}.minAmount" deyeri integer "1" beraberdir
* Json cavabında "items.find{it.id=='azersu'}.maxAmount" deyeri integer "300" beraberdir
* Json cavabında "items.find{it.id=='azersu'}.fee.type" deyeri "NONE" beraberdir
* Json cavabında "items.find{it.id=='azersu'}.subscriberPattern" deyeri "^\\d{8}$" beraberdir
* Json cavabında "items.find{it.id=='azercell'}.category" deyeri "MOBILE" beraberdir
* Json cavabında "items.find{it.id=='azercell'}.minAmount" deyeri "0.5" beraberdir
* Json cavabında "items.find{it.id=='azercell'}.maxAmount" deyeri integer "200" beraberdir
* Json cavabında "items.find{it.id=='azercell'}.fee.type" deyeri "NONE" beraberdir
* Json cavabında "items.find{it.id=='azercell'}.subscriberPattern" deyeri "^(50|51|10)\\d{7}$" beraberdir
* Json cavabında "items.find{it.id=='bakcell'}.category" deyeri "MOBILE" beraberdir
* Json cavabında "items.find{it.id=='bakcell'}.minAmount" deyeri "0.5" beraberdir
* Json cavabında "items.find{it.id=='bakcell'}.maxAmount" deyeri integer "200" beraberdir
* Json cavabında "items.find{it.id=='bakcell'}.fee.type" deyeri "NONE" beraberdir
* Json cavabında "items.find{it.id=='bakcell'}.subscriberPattern" deyeri "^(55|99)\\d{7}$" beraberdir
* Json cavabında "items.find{it.id=='citynet'}.category" deyeri "INTERNET" beraberdir
* Json cavabında "items.find{it.id=='citynet'}.minAmount" deyeri integer "5" beraberdir
* Json cavabında "items.find{it.id=='citynet'}.maxAmount" deyeri integer "150" beraberdir
* Json cavabında "items.find{it.id=='citynet'}.fee.type" deyeri "PERCENT" beraberdir
* Json cavabında "items.find{it.id=='citynet'}.fee.value" deyeri integer "1" beraberdir
* Json cavabında "items.find{it.id=='citynet'}.fee.min" deyeri "0.2" beraberdir
* Json cavabında "items.find{it.id=='citynet'}.fee.max" deyeri integer "2" beraberdir
* Json cavabında "items.find{it.id=='citynet'}.subscriberPattern" deyeri "^CN\\d{6}$" beraberdir
* Json cavabında "items.find{it.id=='aztelekom'}.category" deyeri "INTERNET" beraberdir
* Json cavabında "items.find{it.id=='aztelekom'}.minAmount" deyeri integer "2" beraberdir
* Json cavabında "items.find{it.id=='aztelekom'}.maxAmount" deyeri integer "100" beraberdir
* Json cavabında "items.find{it.id=='aztelekom'}.fee.type" deyeri "FIXED" beraberdir
* Json cavabında "items.find{it.id=='aztelekom'}.fee.value" deyeri "0.5" beraberdir
* Json cavabında "items.find{it.id=='aztelekom'}.subscriberPattern" deyeri "^\\d{7}$" beraberdir

## 66. category=MOBILE filtri
tags: get, billers, positive

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
* Add Endpoint "/billers?category=MOBILE"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Cavabı çap et
* Status kodunun "200" olmalıdır
* Json cavabında "total" deyeri integer "2" beraberdir
* Json cavabında "items.category" siyahısındakı bütün deyerler "MOBILE" olmalıdır

## 67. category=INTERNET filtri
tags: get, billers, positive

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
* Add Endpoint "/billers?category=INTERNET"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Cavabı çap et
* Status kodunun "200" olmalıdır
* Json cavabında "total" deyeri integer "2" beraberdir
* Json cavabında "items.category" siyahısındakı bütün deyerler "INTERNET" olmalıdır

## 68. Yanlış category dəyəri
tags: get, billers, negative

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
* Add Endpoint "/billers?category=GAS"
* Add as a header "Authorization" = "Bearer ${token}"
* Get request and display respons
* Cavabı çap et
* Status kodunun "400" olmalıdır
* Json cavabında "code" deyeri "VALIDATION_ERROR" beraberdir

## 69. Token yoxdur/etibarsızdır
tags: get, billers, negative, security

* Initalize request specification
* Set base Url to "https://api.anarabbas.com"
* Add Endpoint "/billers"
* Get request and display respons
* Status kodunun "401" olmalıdır