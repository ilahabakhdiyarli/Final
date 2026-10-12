# Test dizaynı — texnikalar və test metodları

Bütün dəyərlər [GRAD-AC-Reference.md](GRAD-AC-Reference.md)-dəndir. "Test" sütunu `grad-restassured-testng/src/test/java/com/grad/api/` altındakı sinif və metodu göstərir.

## 1. Equivalence partitioning (EP)

| Parametr | Etibarlı siniflər | Etibarsız siniflər | Test |
|---|---|---|---|
| `currency` (GRAD-101/102) | AZN, USD, EUR | digər | `BeneficiaryListTest.ac3_currencyFilter_usdOnly` |
| `category` (GRAD-301) | UTILITY, MOBILE, INTERNET, yoxdur | GAS → 400 | `BillersListTest.scenario65..68` |
| `reason` (GRAD-203) | TEMPORARY / LOST / STOLEN | BROKEN, yoxdur → 400 | `CardBlockTest.ac6_invalidOrMissingReason…` |
| `fullName` formatı (GRAD-101) | hərflə başlayır, AZ hərfləri, `-` `'` | `---`, `Ali2`, SQLi → 400 | `BeneficiaryCreateTest.ac6…`, `extra_azerbaijaniLetters…`, `extra_sqlInjection…` |
| Pul sahəsinin tipi | number, ≤ 2 onluq | `"50"` mətn, 10.555 → 400 | `CardCreateTest.initialAmountFormatErrors…`, `BillPaymentTest.scenario79…`, `gap03_invalidMoneyAmount…` |
| Resurs sahibi | öz resursu | başqasının resursu → 404 | `CardGetTest.anotherUsersCard_idor…`, `CardBlockTest.newGapClosing_block…`, `CardUnblockTest.idor…`, `BeneficiaryDeleteTest.ac4…` |
| Token | etibarlı | yoxdur / saxta / dəyişdirilmiş → 401 | hər sinifdə `noToken_…`, `AuthTokenTest.tamperedToken_returns401` |

## 2. Boundary value analysis (BVA)

| Sərhəd | Rədd | Qəbul | Test (`@DataProvider`) |
|---|---|---|---|
| fullName uzunluğu 2–50 | 1, 51 | 2, 50 | `BeneficiaryCreateTest.fullNameLengthBoundary` |
| IBAN uzunluğu 28 | 27, 29 | 28 | `BeneficiaryCreateTest.ibanLengthBoundary` |
| initialAmount 1.00–1000.00 | 0.99, 1000.01 | 1.00, 1000.00 | `CardCreateTest.initialAmountBoundary` |
| Azercell 0.50–200.00 | 0.49, 200.01 | 0.50, 200.00 | `BillPaymentTest.azercellAmountBoundaryExact` |
| CityNet 5–150 | 4.99, 150.01 | — | `BillPaymentTest.cityNetAmountOutOfRange` |
| CityNet fee 1% (min 0.20, max 2.00) | — | 5→0.20, 20→0.20, 21→0.21, 150→1.50 | `BillPaymentTest.cityNetFeeBoundary` |
| Tək ödəniş limiti 300 (total) | 300.01 | 300.00 | `BillPaymentTest.scenario87`, `scenario88` |
| Gündəlik limit 500 | 500 + 1 | — | `BillPaymentTest.scenario89` (`remaining: 0`) |
| Balans 49.50 | 49.51 | 49.50 (`balanceAfter` 0) | `BillPaymentTest.scenario90`, `scenario91` |
| limit 1–50 | 0, 51 | 1, 50 | `PaymentHistoryTest.scenario4/5`, `newGap_limitOne…`, `newGap_limitFifty…` |
| page ≥ 1 | 0, -1, abc | 1, 2, 3 (boş) | `PaymentHistoryTest.scenario1..3, 6..8` |
| Kart sayı ≤ 3 (ACTIVE/FROZEN) | 4-cü | BLOCKED sayılmır | `CardCreateTest.cardLimitExceeded…`, `ac8_blockedCard…` |
| Alıcı sayı ≤ 10 | 11-ci (`limit: 10`) | 10 | `BeneficiaryCreateTest.ac10…` |

## 3. Decision table — GRAD-302 POST /bill-payments yoxlama ardıcıllığı

Qaydalar bu ardıcıllıqla yoxlanılır; ilk pozulan qayda cavabı müəyyən edir. Hər sütunda yalnız bir şərt pozulur.

| # | Pozulan şərt | Gözlənilən | Test |
|---|---|---|---|
| R1 | Token yoxdur | 401 | `scenario78_noToken_returns401` |
| R2 | Idempotency-Key yoxdur / qısadır | 400 `IDEMPOTENCY_KEY_REQUIRED` / `IDEMPOTENCY_KEY_INVALID` | `scenario72`, `scenario73` |
| R3 | Sahə validasiyası (amount mətn, 3 onluq, ≤ 0) | 400 `VALIDATION_ERROR` | `scenario79`, `gap03_invalidMoneyAmount…` |
| R4 | Eyni key, fərqli body | 409 `IDEMPOTENCY_CONFLICT` | `scenario75` |
| R5 | Eyni key, eyni body | 200 replay, `Idempotent-Replay: true`, balans 2-ci dəfə azalmır | `scenario74` |
| R6 | Biller yoxdur | 404 `BILLER_NOT_FOUND` | `scenario76` |
| R7 | Abonent formatı | 422 `INVALID_SUBSCRIBER` | `scenario80`, `ac7_azercellGivenBakcellNumber…` |
| R8 | Məbləğ biller aralığından kənar | 422 `AMOUNT_OUT_OF_RANGE` | `scenarios85to86`, `ac8_azercellExactAmountBoundary` |
| R9 | Kart yoxdur | 404 `CARD_NOT_FOUND` | `scenario77` |
| R10 | Kart FROZEN / BLOCKED | 422 `CARD_NOT_ACTIVE` | `scenario92`, `scenario93` |
| R11 | USD kart | 422 `CURRENCY_NOT_SUPPORTED` | `scenario94` |
| R12 | total > 300 | 422 `PER_TRANSACTION_LIMIT` | `scenario87` |
| R13 | Gündəlik 500 aşılır | 422 `DAILY_LIMIT_EXCEEDED` | `scenario89` |
| R14 | Balans çatmır | 422 `INSUFFICIENT_FUNDS` | `scenario90` |
| — | Heç biri pozulmur | 201 `SUCCESS`, `total = amount + fee` | `scenario70`, `scenario71`, `scenarios81to84` |

## 4. State transition — GRAD-203 / GRAD-204 kart statusu

| Başlanğıc \ hadisə | block TEMPORARY | block LOST | block STOLEN | unblock |
|---|---|---|---|---|
| **ACTIVE** | 200 → FROZEN | 200 → BLOCKED | 200 → BLOCKED | 409 `NOT_FROZEN` |
| **FROZEN** | 409 `ALREADY_FROZEN` | 200 → BLOCKED | 200 → BLOCKED | 200 → ACTIVE |
| **BLOCKED** | 409 `CARD_BLOCKED` | 409 `CARD_BLOCKED` | 409 `CARD_BLOCKED` | 409 `PERMANENTLY_BLOCKED` |

- Block sütunlarının 9 xanası: `CardBlockTest.stateTransitionTable_allNineTransitions` (`@DataProvider stateTransitionTable`).
- Unblock sütunu: `CardUnblockTest.frozenCard_unblock…`, `activeCard_unblock…`, `blockedCard_unblock…`.
- Statusun ödənişə təsiri: FROZEN/BLOCKED → 422 `CARD_NOT_ACTIVE` (`BillPaymentTest.scenario92/93`); unblock-dan sonra ödəniş 201 (`CardUnblockTest.ac4…`).
