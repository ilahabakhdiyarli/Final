# GRAD API Test Automation — RestAssured + TestNG + Allure

Bu modul `api.anarabbas.com` sandbox API-si üçün RestAssured + TestNG + Allure ilə yazılmış
ayrıca avtomasiya layihəsidir (Gauge `.spec` suitindən asılı deyil, paralel/əlavə bir test qatıdır).

## Strukturu

```
src/test/java/com/grad/api/
  config/Config.java            — config.properties oxuyur (base URL, user1/user2 credentials)
  util/Endpoints.java           — endpoint path konstantları
  util/Schemas.java             — JSON Schema fayllarının classpath yolları
  base/BaseApiTest.java         — RequestSpecification (base URI, JSON, Allure filtri),
                                  ResponseSpecification-lar (error / validationError / unauthorized),
                                  login və kart/alıcı yaratma helper-ləri
  models/request/               — sorğu POJO-ları: LoginRequest, BeneficiaryRequest, CardRequest,
                                  BlockCardRequest, BillPaymentRequest
  models/response/              — cavab POJO-ları: LoginResponse, Beneficiary, BeneficiaryList, Card,
                                  CardStatus, BillerList, BillPayment, BillPaymentList, ErrorResponse
  auth/
    AuthTokenTest.java          (token lifecycle — GAP-02)
    RateLimitTest.java          (rate limit — GAP-01, default suite-də deyil)
  epic1/
    BeneficiaryCreateTest.java  (GRAD-101)
    BeneficiaryListTest.java    (GRAD-102)
    BeneficiaryDeleteTest.java  (GRAD-103)
  epic2/
    CardCreateTest.java         (GRAD-201)
    CardGetTest.java            (GRAD-202)
    CardBlockTest.java          (GRAD-203 — state-transition cədvəlinin 9 keçidi daxil)
    CardUnblockTest.java        (GRAD-204)
  epic3/
    BillersListTest.java        (GRAD-301)
    BillPaymentTest.java        (GRAD-302)
    PaymentHistoryTest.java     (GRAD-303)
src/test/resources/
  config.properties             — base URL və test istifadəçiləri
  allure.properties             — Allure nəticələri target/allure-results-ə yazılır
  schemas/*.json                — 11 JSON Schema (draft-04)
testng.xml                      — default suite (Auth + EPIC-1/2/3)
testng-ratelimit.xml            — yalnız rate limit testi
pom.xml
```

## Test dizaynı

- **POJO-lar:** bütün etibarlı sorğu body-ləri POJO ilə (Jackson) qurulur. Xam JSON yalnız bilərəkdən
  səhv tipli/formatlı sorğularda qalıb (məs. `"initialAmount":"50"`, `"amount":"10"`, `{fullName: Ali`),
  çünki POJO-nun tipli sahələri belə sorğu qurmağa imkan vermir. Cavablar `.as(Card.class)` kimi
  POJO-ya çevrilib pul hesablamaları `BigDecimal` ilə yoxlanılır (məs. `total = amount + fee`,
  `balanceAfter = əvvəlki balans – total`, GET /cards POST cavabı ilə sahə-sahə eynidir).
- **JSON Schema:** hər uğurlu cavab və hər xəta cavabı schema ilə yoxlanılır. Schema-lar real API
  cavablarından çıxarılıb və AC-Reference-dəki formatlarla (`ben_`/`vcd_`/`bpm_` + 8 simvol,
  `maskedPan`, `receiptNo`, status/currency enum-ları) məhdudlaşdırılıb. Yazılarkən Allure-da
  saxlanmış 1200 real cavabın hamısı həm schema-lardan, həm POJO deserializasiyasından keçib.
- **Xəta assert-ləri koda görədir, mesaja yox:** `error(422, "INSUFFICIENT_FUNDS")` spesifikasiyası
  status + `schemas/error.json` + `code` yoxlayır; `validationError("amount")` əlavə olaraq
  `details[].field`-i yoxlayır; `unauthorized()` 401 cavabını yoxlayır.
- **Texnikalar:** BVA (`@DataProvider`: fullName 1/2/50/51, initialAmount 0.99/1.00/1000.00/1000.01,
  Azercell 0.49/0.50/200.00/200.01, CityNet fee 5/20/21/150, limit 0/1/50/51), equivalence
  partitioning (valyuta/kateqoriya/reason sinifləri), state transition (`CardBlockTest` — ACTIVE/FROZEN/
  BLOCKED × TEMPORARY/LOST/STOLEN cədvəlinin 9 keçidi), decision table (GRAD-302 yoxlama ardıcıllığının
  hər qaydası ayrıca testlə). Cədvəllər və test metodlarına bağlantı: `../docs/Test-Design.md`.

## Token lifecycle

- Token heç vaxt hardcode edilmir — hər test `POST /auth/login` ilə runtime-da alır.
- Hər login təcrid olunmuş yeni sandbox yaratdığı üçün hər test öz token-ini alır; testlər bir-birindən
  asılı deyil. Bir test daxilində eyni token bütün sorğularda təkrar istifadə olunur.
- `AuthTokenTest`: token-in bir neçə endpoint-də təkrar istifadəsi, yeni login-in təcrid olunmuş sandbox
  verməsi, köhnə token-in etibarlı qalması, dəyişdirilmiş token → 401.
- 24 saatlıq müddətin bitməsi avtomatlaşdırılmayıb (24 saat gözləmə tələb edir) — `docs/TestPlan.md`-də
  manual yoxlama kimi qeyd olunub.

## Necə işə salmaq olar

```bash
cd grad-restassured-testng
mvn clean test                                  # default suite
mvn allure:report                               # target/site/allure-maven-plugin/index.html
mvn allure:serve                                # hesabatı brauzerdə açır
mvn test -DsuiteXml=testng-ratelimit.xml        # yalnız rate limit testi (~650 sorğu)
```

## İcra tarixçəsi

- 2026-10-06 — ilk icra: 6 uğursuz nəticə. Hamısı test kodu defekti idi (API bug-ı yox), düzəldildi —
  təfərrüat: `bug-report/GRAD-BugReport-and-Gaps.xlsx`, "Test Kodu Defektləri" vərəqi.
- 2026-10-07 — `testng-results.xml`: 129 / 129 passed (POJO/schema refaktorundan əvvəlki versiya).
- POJO/JSON Schema refaktoru və yeni testlər (AuthTokenTest, state-transition cədvəli, GAP-03, GAP-04)
  `mvn test-compile` ilə compile olunub, lakin API-yə qarşı hələ icra olunmayıb — növbəti `mvn clean test`
  nəticəsi bura əlavə olunmalıdır.

Sandbox hər login-də təzə/təcrid olunmuş data yaratdığı üçün testlər hər işə salınmada fərqli
ID-lər görə bilər — bu, gözlənilən davranışdır, bug deyil.
