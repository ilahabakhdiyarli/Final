# GRAD-101..303 — Tam Acceptance Criteria (ticket skrinşotlarından, verbatim)

Mənbə: layihə ticket board-u — https://anarabbas.com/labs/final-project-7k3q9x (GRAD-101..303). AC-lər ticket-lərdən hərfi köçürülüb, parafraz edilməyib.

## GRAD-101 — Yeni alıcı əlavə etmək (POST /beneficiaries)
*Epic-1 · Saxlanmış alıcılar · High · 5 SP*
**Parameters:** fullName (2–50 simvol), iban, currency (AZN|USD|EUR), nickname (opsional, 1–20 simvol, göndərilməsə null)

- **AC-1** Given Etibarlı sorğu göndərilir When Alıcı yaradılır Then 201 qaytarılır; cavabda id (ben_ + 8 simvol), iban, ibanMasked, currency, nickname, createdAt var; Location header `/beneficiaries/{id}` göstərir
- **AC-2** Given IBAN boşluqlu və kiçik hərflə göndərilir When Yadda saxlanılır Then Cavabdakı iban boşluqsuz və böyük hərflə olur
- **AC-3** Given nickname göndərilmir When Alıcı yaradılır Then Cavabda "nickname": null
- **AC-4** Given Bir neçə sahə eyni anda səhvdir When Sorğu rədd edilir Then 400 VALIDATION_ERROR; details-də hər səhv sahə üçün { field, message } var
- **AC-5** Given fullName 1 və ya 51 simvoldur When Sorğu rədd edilir Then 400; 2 və 50 simvol isə qəbul olunur
- **AC-6** Given fullName "---" və ya "Ali2" When Sorğu rədd edilir Then 400 VALIDATION_ERROR (hərflə başlamır / rəqəm var)
- **AC-7** Given Eyni IBAN ikinci dəfə göndərilir When Sorğu rədd edilir Then 409 BENEFICIARY_EXISTS
- **AC-8** Given "anam" ləqəbi var, "ANAM" göndərilir When Sorğu rədd edilir Then 409 NICKNAME_TAKEN
- **AC-9** Given user1 öz hesabının IBAN-ını (AZ29NABZ00000000137010002855) göndərir When Sorğu rədd edilir Then 422 OWN_ACCOUNT
- **AC-10** Given Siyahıda 10 alıcı var When 11-ci əlavə edilir Then 422 LIMIT_REACHED, "limit": 10

Yoxlama ardıcıllığı: auth → JSON → sahə validasiyası → OWN_ACCOUNT → BENEFICIARY_EXISTS → NICKNAME_TAKEN → LIMIT_REACHED.

## GRAD-102 — Alıcılar siyahısı — axtarış, filtr, sıralama (GET /beneficiaries)
*Medium · 3 SP* · **Parameters:** q, currency, sort, order

- **AC-1** Given Parametrsiz sorğu When Siyahı qaytarılır Then 200; total = items.length; ən yeni əlavə edilən birinci gəlir
- **AC-2** Given sort=fullName&order=asc When Siyahı qaytarılır Then Adlar əlifba sırası ilə (Azərbaycan əlifbası) düzülür
- **AC-3** Given currency=USD When Siyahı qaytarılır Then Yalnız USD alıcılar, total uyğun
- **AC-4** Given q=NİGAR və ya q=NIGAR (nöqtəsiz I) When Axtarış edilir Then Hər iki halda "Nigar Əliyeva" tapılır
- **AC-5** Given q ləqəbin bir hissəsidir When Axtarış edilir Then Ləqəbə görə də tapılır
- **AC-6** Given q=a (1 simvol) When Sorğu rədd edilir Then 400 VALIDATION_ERROR, field: "q"
- **AC-7** Given Heç nə tapılmır When Axtarış edilir Then 200, "total": 0, "items": []

Parametrlər birləşdirilə bilər (AND məntiqi ilə).

## GRAD-103 — Alıcını silmək (DELETE /beneficiaries/{id})
*Medium · 2 SP*

- **AC-1** Given Mövcud alıcı silinir When Sorğu icra olunur Then 204, body boşdur
- **AC-2** Given Silinmiş alıcı When GET /beneficiaries çağırılır Then Siyahıda yoxdur, total 1 azalıb
- **AC-3** Given Eyni id ikinci dəfə silinir When Sorğu icra olunur Then 404 NOT_FOUND
- **AC-4** Given user2 user1-in alıcı id-si ilə silməyə çalışır When Sorğu icra olunur Then 404 NOT_FOUND (403 yox)

## GRAD-201 — Virtual kart buraxmaq (POST /cards)
*Epic-2 · Virtual kartlar · High · 5 SP* · **Parameters:** accountId, initialAmount, label

- **AC-1** Given Etibarlı sorğu When Kart buraxılır Then 201; status ACTIVE; availableBalance = initialAmount; limitlər 500 / 300; spentToday 0
- **AC-2** Given Kart buraxılır When Cavab yoxlanır Then maskedPan "4169 73** **** NNNN" formatındadır; expiry MM/YY — bu gündən 3 il sonra
- **AC-3** Given initialAmount 0.99 və ya 1000.01 When Sorğu rədd edilir Then 400; 1.00 və 1000.00 qəbul olunur
- **AC-4** Given initialAmount "50" (mətn) və ya 10.555 When Sorğu rədd edilir Then 400 VALIDATION_ERROR
- **AC-5** Given user1 acc_003 ilə kart açmağa çalışır When Sorğu rədd edilir Then 403 FORBIDDEN
- **AC-6** Given user2 891 yükləmək istəyir (balans 890.50) When Sorğu rədd edilir Then 422 INSUFFICIENT_FUNDS
- **AC-7** Given 3 ACTIVE/FROZEN kart var When 4-cü kart açılır Then 422 CARD_LIMIT_REACHED
- **AC-8** Given Kartlardan biri BLOCKED edilib When Yeni kart açılır Then 201 — BLOCKED kart limitə sayılmır

Karta yükləmə sandbox daxilindədir — əsas hesabın balansı (GET /me/accounts) DƏYİŞMİR. user2-nin hesabı USD-dir → onun kartı kommunal ödəniş edə bilməz (GRAD-302).

## GRAD-202 — Kart məlumatına baxmaq (GET /cards/{id})
*Medium · 2 SP*

- **AC-1** Given Kart yeni buraxılıb When GET edilir Then 200; POST cavabı ilə eyni dəyərlər
- **AC-2** Given Kartla 50.50 AZN ödəniş edilib When GET edilir Then availableBalance 50.50 azalıb, spentToday 50.50 artıb
- **AC-3** Given Başqa istifadəçinin kart id-si When GET edilir Then 404 NOT_FOUND

## GRAD-203 — Kartı bloklamaq / dondurmaq (POST /cards/{id}/block)
**Parameters:** reason (string, məcburi) — TEMPORARY → FROZEN · LOST / STOLEN → BLOCKED

- **AC-1** Given ACTIVE kart, reason TEMPORARY When Sorğu icra olunur Then 200; status FROZEN; blockReason TEMPORARY; blockedAt dolu
- **AC-2** Given ACTIVE kart, reason LOST When Sorğu icra olunur Then 200; status BLOCKED
- **AC-3** Given FROZEN kart, reason STOLEN When Sorğu icra olunur Then 200; status BLOCKED (eskalasiya)
- **AC-4** Given FROZEN kart, reason TEMPORARY When Sorğu icra olunur Then 409 ALREADY_FROZEN
- **AC-5** Given BLOCKED kart, istənilən reason When Sorğu icra olunur Then 409 CARD_BLOCKED
- **AC-6** Given reason "BROKEN" və ya yoxdur When Sorğu icra olunur Then 400 VALIDATION_ERROR
- **AC-7** Given Kart FROZEN / BLOCKED When Ödəniş edilir (GRAD-302) Then 422 CARD_NOT_ACTIVE

Vəziyyət keçid cədvəli (state transition): ACTIVE: TEMPORARY→200 FROZEN, LOST/STOLEN→200 BLOCKED; FROZEN: TEMPORARY→409 ALREADY_FROZEN, LOST/STOLEN→200 BLOCKED; BLOCKED: TEMPORARY→409 CARD_BLOCKED, LOST/STOLEN→409 CARD_BLOCKED.
Yoxlama ardıcıllığı: auth → JSON → reason validasiyası → NOT_FOUND → status keçidi.

## GRAD-204 — Dondurulmuş kartı açmaq (POST /cards/{id}/unblock)
*Medium · 2 SP* — Yalnız FROZEN kart ACTIVE-ə qaytarılır. LOST/STOLEN səbəbi ilə bloklanmış kart açılmır. Body göndərilmir.

- **AC-1** Given FROZEN kart When Unblock edilir Then 200; status ACTIVE; blockReason və blockedAt null
- **AC-2** Given ACTIVE kart When Unblock edilir Then 409 NOT_FROZEN
- **AC-3** Given BLOCKED kart When Unblock edilir Then 409 PERMANENTLY_BLOCKED
- **AC-4** Given Açılmış kart When Ödəniş edilir Then Ödəniş uğurla keçir (201)

## GRAD-301 — Xidmət təchizatçıları kataloqu (GET /billers)
*Epic-3 · Kommunal ödənişlər · Low · 2 SP* · **Parameters:** category (opsional) — UTILITY | MOBILE | INTERNET

- **AC-1** Given Parametrsiz When Sorğu edilir Then 200; total 6; hər element subscriberPattern, minAmount, maxAmount, fee daşıyır
- **AC-2** Given category=MOBILE When Sorğu edilir Then Yalnız Azercell və Bakcell
- **AC-3** Given category=GAS When Sorğu edilir Then 400 VALIDATION_ERROR

## GRAD-302 — Kommunal ödəniş etmək (POST /bill-payments)
*Highest · 8 SP* · **Parameters:** cardId, billerId, subscriberNo, amount (>0, max 2 onluq, biller aralığında). Header: Idempotency-Key (8–64 simvol, UUID tövsiyə olunur).

- **AC-1** Given Etibarlı ödəniş When Sorğu icra olunur Then 201; status SUCCESS; total = amount + fee; balanceAfter = əvvəlki balans – total; Location header var
- **AC-2** Given Ödəniş uğurludur When GET /cards/{id} çağırılır Then availableBalance total qədər azalıb, spentToday total qədər artıb
- **AC-3** Given Sandbox-da ilk ödəniş When Cavab yoxlanır Then receiptNo RCP-YYYYMMDD-000001; hər növbəti ödəniş +1
- **AC-4** Given Eyni Idempotency-Key + eyni body təkrar göndərilir When Sorğu icra olunur Then 200 (201 yox); eyni id; header Idempotent-Replay: true; balans İKİNCİ DƏFƏ azalmır
- **AC-5** Given Eyni Idempotency-Key, fərqli amount When Sorğu icra olunur Then 409 IDEMPOTENCY_CONFLICT
- **AC-6** Given Idempotency-Key yoxdur When Sorğu icra olunur Then 400 IDEMPOTENCY_KEY_REQUIRED
- **AC-7** Given Azercell-ə 551234567 (Bakcell nömrəsi) When Sorğu icra olunur Then 422 INVALID_SUBSCRIBER
- **AC-8** Given Azercell: 0.49 və 200.01 When Sorğu icra olunur Then 422 AMOUNT_OUT_OF_RANGE; 0.50 və 200.00 qəbul olunur
- **AC-9** Given CityNet: 5, 20, 21, 150 AZN When Komissiya hesablanır Then fee: 0.20, 0.20, 0.21, 1.50 (1%, min 0.20, max 2.00)
- **AC-10** Given Aztelekom: istənilən məbləğ When Komissiya hesablanır Then fee: 0.50 (sabit)
- **AC-11** Given total 300.01 When Sorğu icra olunur Then 422 PER_TRANSACTION_LIMIT; total 300.00 keçir
- **AC-12** Given Bu gün 500 xərclənib When 1 AZN də ödənir Then 422 DAILY_LIMIT_EXCEEDED, "remaining": 0
- **AC-13** Given Balans 49.50 When 49.51 ödənir / 49.50 ödənir Then 422 INSUFFICIENT_FUNDS / 201 və balanceAfter 0
- **AC-14** Given FROZEN kart / USD kart When Sorğu icra olunur Then 422 CARD_NOT_ACTIVE / 422 CURRENCY_NOT_SUPPORTED
- **AC-15** Given amount "10" (mətn) When Sorğu icra olunur Then 400 VALIDATION_ERROR

Yoxlama ardıcıllığı: auth → Idempotency-Key → JSON → sahə validasiyası → idempotency replay/conflict → BILLER_NOT_FOUND → INVALID_SUBSCRIBER → AMOUNT_OUT_OF_RANGE → CARD_NOT_FOUND → CARD_NOT_ACTIVE → CURRENCY_NOT_SUPPORTED → PER_TRANSACTION_LIMIT → DAILY_LIMIT_EXCEEDED → INSUFFICIENT_FUNDS. Limitlər total (amount+fee) üzrə. Uğursuz sorğu Idempotency-Key-i "istifadə etmir". Komissiya 2 onluğa yuvarlaqlaşdırılır.

## GRAD-303 — Ödəniş tarixçəsi — filtr və səhifələmə (GET /bill-payments)
*Medium · 5 SP* — Ödənişlər ən yenidən köhnəyə sıralanır. Tarix filtrləri Bakı vaxtı, inclusive. Mövcud olmayan səhifə → boş siyahı.
**Parameters:** page (≥1, default 1), limit (1–50, default 10), billerId, cardId, from (YYYY-MM-DD, inclusive), to (YYYY-MM-DD, inclusive, from-dan kiçik ola bilməz)

- **AC-1** Given Parametrsiz When Sorğu edilir Then 200; page 1, limit 10; ən yeni ödəniş birinci
- **AC-2** Given 18 ödəniş var, limit 10 When page=2 istənilir Then 8 element; totalPages 2
- **AC-3** Given page=3 (mövcud deyil) When Sorğu edilir Then 200, items []
- **AC-4** Given limit=0 / 51, page=0 / -1 / abc When Sorğu edilir Then 400; limit 1 və 50 qəbul olunur
- **AC-5** Given from=2026-13-01 When Sorğu edilir Then 400 (mövcud olmayan tarix)
- **AC-6** Given from > to When Sorğu edilir Then 400
- **AC-7** Given from = to = bu gün When Sorğu edilir Then Bu günün bütün ödənişləri
- **AC-8** Given billerId=citynet When Sorğu edilir Then Yalnız citynet ödənişləri; total uyğun

total filtrdən sonrakı ümumi saydır, səhifədəki say deyil.

---

## Ümumi texniki qaydalar (layihə səhifəsindən, 2026-10-06 təsdiqləndi)
- Error format: `{ "error": "...", "code": "..." }` — koda görə assert, mesaja yox
- Sahiblik olmayan resursa giriş → 404 (403 yox), resurs varlığını gizlətmək üçün
- Rate limit: 600 sorğu / 10 saniyə (token/sessiya üzrə), aşıldıqda 429 `RATE_LIMITED` — avtomat test: `RateLimitTest` (default suite-də deyil)
- Token yoxdur, saxtadır və ya müddəti bitib → 401. Token 24 saat etibarlıdır, sandbox data 24 saatdan sonra sıfırlanır — token təkrar istifadəsi/izolyasiyası/saxta token: `AuthTokenTest`; 24 saatlıq müddət: manual yoxlama (TestPlan bölmə 5)
- Pul sahələri number, maksimum 2 onluq; "50" kimi mətn rədd edilir — GRAD-201 AC-4 və GRAD-302 `gap03_invalidMoneyAmount_returns400ValidationError` (10.555 / 0 / -5)
