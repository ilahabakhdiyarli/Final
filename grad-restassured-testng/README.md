# GRAD API Test Automation — RestAssured + TestNG + Allure

Bu modul `api.anarabbas.com` sandbox API-si üçün RestAssured + TestNG + Allure ilə yazılmış
ayrıca avtomasiya layihəsidir (Gauge `.spec` suitindən asılı deyil, paralel/əlavə bir test qatıdır).

## Strukturu

```
src/test/java/com/grad/api/
  config/Config.java          — config.properties oxuyur (base URL, user1/user2 credentials)
  util/Endpoints.java         — endpoint path konstantları
  base/BaseApiTest.java       — login, kart/alıcı yaratma helper-ləri, RequestSpecification
  epic1/
    BeneficiaryCreateTest.java  (GRAD-101)
    BeneficiaryListTest.java    (GRAD-102)
    BeneficiaryDeleteTest.java  (GRAD-103)
  epic2/
    CardCreateTest.java         (GRAD-201)
    CardGetTest.java            (GRAD-202)
    CardBlockTest.java          (GRAD-203)
    CardUnblockTest.java        (GRAD-204)
  epic3/
    BillersListTest.java        (GRAD-301)
    BillPaymentTest.java        (GRAD-302)
    PaymentHistoryTest.java     (GRAD-303)
src/test/resources/config.properties
testng.xml
pom.xml
```

Hər test sinfi müvafiq Gauge `.spec` faylındakı ssenarilərin (AC-Reference-ə uyğun) RestAssured
qarşılığıdır, əlavə olaraq traceability matrix/gap siyahısında aşkarlanmış bəzi test gap-larını
bağlayan yeni test metodları ilə (məs. IDOR-404 yoxlamaları, spentToday dəqiqliyi, receiptNo formatı,
sərhəd dəyərləri və s. — hər yeni metodun adı `ac...` yox, `new...`/`newGap...` prefiksi ilə
başlayır ki, hansı testlərin orijinal Gauge suitindən fərqli/əlavə olduğu aydın olsun).

Hər test metodu özü login edir və öz sandbox data-sını yaradır (TestPlan.md bölmə 3/5-ə uyğun —
heç bir state testlər arasında paylaşılmır).

## Necə işə salmaq olar

```bash
cd grad-restassured-testng
mvn clean test
mvn allure:serve     # Allure hesabatını brauzerdə açır (allure CLI quraşdırılmış olmalıdır)
# və ya:
mvn allure:report     # target/site/allure-maven-plugin altında statik hesabat yaradır
```

`testng.xml` 3 `<test>` blokuna bölünüb (EPIC-1/2/3), bütün 10 sinif daxildir.

## Qeyd — bu layihə bu mühitdə compile/run edilməyib

Bu fayllar Claude tərəfindən yazılıb, lakin **cari sandbox mühitində Maven quraşdırılmayıb və
Maven Central-a şəbəkə girişi yoxdur** (`mvn -v` → "command not found", Maven Central-a sorğu → 403
"blocked-by-allowlist"). Ona görə bu kod burada heç bir dəfə compile və ya işə salınmayıb.

Bütün fayllar üzərində manual statik yoxlama aparılıb (brace balansı, package/class bəyannaməsi,
`Endpoints`/`BaseApiTest` metod istinadlarının düzgünlüyü, import uyğunluğu) — amma bu, `mvn test-compile`-ın
əvəzi deyil. İlk `mvn clean test` zamanı kiçik compile xətaları (məs. tipos, import çatışmazlığı)
çıxma ehtimalı var; belə olarsa mənə nəticəni göstər, birgə düzəldək.

Sandbox hər login-də təzə/təcrid olunmuş data yaratdığı üçün, testlər hər işə salınmada fərqli
nəticələr/ID-lər görə bilər — bu, gözlənilən davranışdır, bug deyil.
