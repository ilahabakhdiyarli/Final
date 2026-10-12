package com.grad.api.epic3;

import com.grad.api.base.BaseApiTest;
import com.grad.api.models.request.BillPaymentRequest;
import com.grad.api.models.response.BillPayment;
import com.grad.api.models.response.Card;
import com.grad.api.util.Endpoints;
import com.grad.api.util.Schemas;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

/**
 * GRAD-302 — POST /bill-payments (Kommunal ödəniş etmək) — Highest/8 SP, maliyyə riski ən yüksək bilet.
 * Mənbə:
 *  - specs/api-testing/gradBillPayments.spec (ssenarilər #70-94, layihənin ən böyük spec faylı — tam oxunub).
 *  - GRAD-AC-Reference.md, bölmə "GRAD-302 — Kommunal ödəniş etmək (POST /bill-payments)" (AC-1..AC-15,
 *    layihənin ən uzun AC siyahısı) — @Story("AC-n") etiketləri və gap-closing testlərin dəqiq mətni
 *    bu bölmədən hərfi götürülüb.
 *  - specs/api-testing/gradBillersList.spec (billerId/category/minAmount/maxAmount/fee/subscriberPattern
 *    kataloqu: azerisiq, azersu, azercell, bakcell, citynet, aztelekom).
 *  - AC-7/AC-8 gap testlərinin literal dəyərləri: "551234567" AC-7-nin mətnindən; receiptNo regex-i
 *    (/^RCP-[0-9]{8}-[0-9]{6}$/) AC-3-dəki "RCP-YYYYMMDD-000001" formatından; Azercell nömrəsi
 *    "501234567" real GET /billers cavabındakı subscriberPattern ^(50|51|10)\d{7}$ və
 *    subscriberFormat nümunəsindən ("məs: 501234567").
 *  - Stil/konvensiya: com.grad.api.epic1.BeneficiaryCreateTest.java (DataProvider ilə BVA, Allure annotasiya
 *    stili) və com.grad.api.epic2.CardCreateTest.java (gap-closing test şərh konvensiyası).
 *
 * Heç bir sahə adı/status/error code uydurulmayıb — mövcud ssenarilər üçün bütün dəyərlər yuxarıdaki
 * mənbələrdən hərfi (verbatim) götürülüb. Yeni (gap-closing) testlər isə yalnız mövcud mənbələrdə artıq
 * var olan adlar/formatlar üzərində qurulub.
 */
@Epic("EPIC-3: Kommunal ödənişlər")
@Feature("GRAD-302: Kommunal ödəniş")
public class BillPaymentTest extends BaseApiTest {

    private static final String RECEIPT_NO_PATTERN = "^RCP-[0-9]{8}-[0-9]{6}$";

    private BillPaymentRequest payBody(String cardId, String billerId, String subscriberNo, String amount) {
        return new BillPaymentRequest(cardId, billerId, subscriberNo, new BigDecimal(amount));
    }

    /** Yalnız tip/format xətası ssenariləri üçün — amountJsonLiteral JSON-a olduğu kimi yazılır (məs. "\"10\""). */
    private String rawPayBody(String cardId, String billerId, String subscriberNo, String amountJsonLiteral) {
        return "{\"cardId\":\"" + cardId + "\",\"billerId\":\"" + billerId + "\",\"subscriberNo\":\""
                + subscriberNo + "\",\"amount\":" + amountJsonLiteral + "}";
    }

    // ==================== 70-94: gradBillPayments.spec ssenariləri (verbatim) ====================

    @Test(description = "70. Etibarlı ödəniş — CityNet (faiz əsaslı komissiya)")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-1")
    public void scenario70_validCityNetPayment_returns201WithAllFields() {
        String token = loginAsUser1();
        BigDecimal initialBalance = new BigDecimal("100");
        String cardId = createCard(token, "acc_002", initialBalance);

        Response response = authed(token)
                .header("Idempotency-Key", "idem-key-070-citynet-aaa")
                .body(payBody(cardId, "citynet", "CN123456", "50"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.BILL_PAYMENT))
                .body("status", equalTo("SUCCESS"))
                .body("biller.id", equalTo("citynet"))
                .body("subscriberNo", equalTo("CN123456"))
                .body("currency", equalTo("AZN"))
                .body("cardId", equalTo(cardId))
                // YENİ (gap-closing): receiptNo formatı heç vaxt test edilmirdi
                .body("receiptNo", matchesPattern(RECEIPT_NO_PATTERN))
                .extract().response();

        BillPayment payment = response.as(BillPayment.class);
        // AC-1: total = amount + fee; balanceAfter = əvvəlki balans – total (CityNet: 50 + 1% = 50.50)
        Assert.assertEquals(payment.getAmount().compareTo(new BigDecimal("50")), 0, "amount");
        Assert.assertEquals(payment.getFee().compareTo(new BigDecimal("0.5")), 0, "fee = 1% (0.50)");
        Assert.assertEquals(payment.getTotal().compareTo(payment.getAmount().add(payment.getFee())), 0,
                "total = amount + fee");
        Assert.assertEquals(payment.getBalanceAfter().compareTo(initialBalance.subtract(payment.getTotal())), 0,
                "balanceAfter = əvvəlki balans – total");
        // YENİ (gap-closing): Location header POST /bill-payments cavabında heç vaxt assert edilmirdi
        Assert.assertEquals(response.header("Location"), "/bill-payments/" + payment.getId(), "Location header");

        // YENİ (gap-closing): spentToday heç vaxt assert edilmirdi (GRAD-302 AC-2)
        authed(token)
                .when().get(Endpoints.cardById(cardId))
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath(Schemas.CARD))
                .body("availableBalance", equalTo(49.5f))
                .body("spentToday", equalTo(50.5f));
    }

    @Test(description = "71. Aztelekom — sabit komissiya (0.50)")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-10")
    public void scenario71_aztelekomFixedFee_returns201() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        authed(token)
                .header("Idempotency-Key", "idem-key-071-aztelekom-bbb")
                .body(payBody(cardId, "aztelekom", "1234567", "10"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.BILL_PAYMENT))
                .body("fee", equalTo(0.5f))
                .body("total", equalTo(10.5f));
    }

    @Test(description = "72. Idempotency-Key header yoxdur")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-6")
    public void scenario72_noIdempotencyKeyHeader_returns400() {
        String token = loginAsUser1();

        authed(token)
                .body(payBody("vcd_fake", "citynet", "CN123456", "50"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().spec(error(400, "IDEMPOTENCY_KEY_REQUIRED"));
    }

    @Test(description = "73. Idempotency-Key çox qısa")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Idempotency-Key")
    public void scenario73_idempotencyKeyTooShort_returns400() {
        String token = loginAsUser1();

        authed(token)
                .header("Idempotency-Key", "short")
                .body(payBody("vcd_fake", "citynet", "CN123456", "50"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().spec(error(400, "IDEMPOTENCY_KEY_INVALID"));
    }

    @Test(description = "74. Eyni Idempotency-Key + eyni body — təkrar göndərmə")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-4")
    public void scenario74_sameIdempotencyKeySameBody_replaysWithoutDoubleCharging() {
        String token = loginAsUser1();

        String cardId = createCard(token, "acc_002", 100);

        String paymentId1 = authed(token)
                .header("Idempotency-Key", "idem-key-074-replay-ccc")
                .body(payBody(cardId, "citynet", "CN123456", "50"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.BILL_PAYMENT))
                .extract().jsonPath().getString("id");

        authed(token)
                .header("Idempotency-Key", "idem-key-074-replay-ccc")
                .body(payBody(cardId, "citynet", "CN123456", "50"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath(Schemas.BILL_PAYMENT))
                .body("id", equalTo(paymentId1))
                .header("Idempotent-Replay", equalTo("true"));

        authed(token)
                .when().get(Endpoints.cardById(cardId))
                .then().statusCode(200)
                .body("availableBalance", equalTo(49.5f));
    }

    @Test(description = "75. Eyni Idempotency-Key, fərqli body")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-5")
    public void scenario75_sameIdempotencyKeyDifferentBody_returns409Conflict() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        authed(token)
                .header("Idempotency-Key", "idem-key-075-conflict-ddd")
                .body(payBody(cardId, "citynet", "CN123456", "50"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.BILL_PAYMENT));

        authed(token)
                .header("Idempotency-Key", "idem-key-075-conflict-ddd")
                .body(payBody(cardId, "citynet", "CN123456", "60"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().spec(error(409, "IDEMPOTENCY_CONFLICT"));
    }

    @Test(description = "76. Mövcud olmayan billerId")
    @Severity(SeverityLevel.NORMAL)
    @Story("Validation")
    public void scenario76_nonExistentBillerId_returns404() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        authed(token)
                .header("Idempotency-Key", "idem-key-076-nobiller-eee")
                .body(payBody(cardId, "faketech", "CN123456", "50"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().spec(error(404, "BILLER_NOT_FOUND"));
    }

    @Test(description = "77. Mövcud olmayan cardId")
    @Severity(SeverityLevel.NORMAL)
    @Story("Validation")
    public void scenario77_nonExistentCardId_returns404() {
        String token = loginAsUser1();

        authed(token)
                .header("Idempotency-Key", "idem-key-077-nocard-fff")
                .body(payBody("vcd_notreal", "citynet", "CN123456", "50"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().spec(error(404, "CARD_NOT_FOUND"));
    }

    @Test(description = "78. Token yoxdur/etibarsızdır")
    @Severity(SeverityLevel.NORMAL)
    @Story("Security")
    public void scenario78_noToken_returns401() {
        given(spec)
                .header("Idempotency-Key", "idem-key-078-notoken-ggg")
                .body(payBody("vcd_fake", "citynet", "CN123456", "50"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().spec(unauthorized());
    }

    @Test(description = "79. amount mətn kimi göndərilir")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-15")
    public void scenario79_amountAsText_returns400ValidationError() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        authed(token)
                .header("Idempotency-Key", "idem-key-079-textamount-hhh")
                .body(rawPayBody(cardId, "citynet", "CN123456", "\"10\""))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().spec(validationError("amount"));
    }

    @Test(description = "80. Subscriber format uyğunsuzluğu — Azərişıq")
    @Severity(SeverityLevel.NORMAL)
    @Story("Validation")
    public void scenario80_azerisiqInvalidSubscriberFormat_returns422() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        authed(token)
                .header("Idempotency-Key", "idem-key-080-badsub-iii")
                .body(payBody(cardId, "azerisiq", "123", "50"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().spec(error(422, "INVALID_SUBSCRIBER"));
    }

    @DataProvider(name = "cityNetFeeBoundary")
    public Object[][] cityNetFeeBoundary() {
        return new Object[][]{
                {"idem-key-081-fee5-jjj", 100, "5", 0.2f, 5.2f},      // 81. amount=5 (minimuma clamp)
                {"idem-key-082-fee20-kkk", 100, "20", 0.2f, 20.2f},   // 82. amount=20 (minimum dəqiq)
                {"idem-key-083-fee21-lll", 100, "21", 0.21f, 21.21f}, // 83. amount=21 (clamp yoxdur)
                {"idem-key-084-fee150-mmm", 200, "150", 1.5f, 151.5f}, // 84. amount=150 (maksimum)
        };
    }

    @Test(dataProvider = "cityNetFeeBoundary",
            description = "81-84. CityNet fee sərhədləri (BVA): amount 5/20/21/150 -> fee 0.2/0.2/0.21/1.5")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-9")
    public void scenarios81to84_cityNetFeeBoundary(String idemKey, int cardInitialAmount,
                                                    String amountLiteral, float expectedFee, float expectedTotal) {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", cardInitialAmount);

        authed(token)
                .header("Idempotency-Key", idemKey)
                .body(payBody(cardId, "citynet", "CN123456", amountLiteral))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.BILL_PAYMENT))
                .body("fee", equalTo(expectedFee))
                .body("total", equalTo(expectedTotal));
    }

    @DataProvider(name = "cityNetAmountOutOfRange")
    public Object[][] cityNetAmountOutOfRange() {
        return new Object[][]{
                {"idem-key-085-toolow-nnn", 100, "4.99"},    // 85. minimumdan aşağı
                {"idem-key-086-toohigh-ooo", 200, "150.01"}, // 86. maksimumdan yuxarı
        };
    }

    @Test(dataProvider = "cityNetAmountOutOfRange",
            description = "85-86. Amount CityNet sərhədindən kənar (4.99 / 150.01) -> 422 AMOUNT_OUT_OF_RANGE")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-8")
    public void scenarios85to86_cityNetAmountOutOfRange(String idemKey, int cardInitialAmount, String amountLiteral) {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", cardInitialAmount);

        authed(token)
                .header("Idempotency-Key", idemKey)
                .body(payBody(cardId, "citynet", "CN123456", amountLiteral))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().spec(error(422, "AMOUNT_OUT_OF_RANGE"));
    }

    @Test(description = "87. Per-transaction limit (300) aşılır")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-11")
    public void scenario87_perTransactionLimitExceeded_returns422() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 1000);

        authed(token)
                .header("Idempotency-Key", "idem-key-087-perlimit-ppp")
                .body(payBody(cardId, "azerisiq", "1234567890", "300.01"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().spec(error(422, "PER_TRANSACTION_LIMIT"));
    }

    @Test(description = "88. Per-transaction limit sərhədi — 300 dəqiq (keçir)")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-11")
    public void scenario88_perTransactionLimitExact300_returns201() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 1000);

        authed(token)
                .header("Idempotency-Key", "idem-key-088-perlimit300-qqq")
                .body(payBody(cardId, "azerisiq", "1234567890", "300"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.BILL_PAYMENT))
                .body("total", equalTo(300));
    }

    @Test(description = "89. Daily limit (500) aşılır — zəncirvari ödənişlər")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-12")
    public void scenario89_dailyLimitExceeded_returns422() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 1000);

        authed(token)
                .header("Idempotency-Key", "idem-key-089-daily1-rrr")
                .body(payBody(cardId, "azerisiq", "1234567890", "300"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.BILL_PAYMENT));

        authed(token)
                .header("Idempotency-Key", "idem-key-089-daily2-sss")
                .body(payBody(cardId, "azerisiq", "1234567890", "200"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.BILL_PAYMENT));

        authed(token)
                .header("Idempotency-Key", "idem-key-089-daily3-ttt")
                .body(payBody(cardId, "azerisiq", "1234567890", "1"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().spec(error(422, "DAILY_LIMIT_EXCEEDED"))
                // YENİ (gap-closing): remaining sahəsi heç vaxt assert edilmirdi
                .body("remaining", equalTo(0));
    }

    @Test(description = "90. Insufficient funds — balansdan böyük ödəniş")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-13")
    public void scenario90_insufficientFunds_returns422() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 49.50);

        authed(token)
                .header("Idempotency-Key", "idem-key-090-insuff-uuu")
                .body(payBody(cardId, "azerisiq", "1234567890", "49.51"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().spec(error(422, "INSUFFICIENT_FUNDS"));
    }

    @Test(description = "91. Insufficient funds sərhədi — balansın tam məbləği (keçir)")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-13")
    public void scenario91_exactBalanceAmount_returns201WithZeroBalanceAfter() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 49.50);

        authed(token)
                .header("Idempotency-Key", "idem-key-091-exact-vvv")
                .body(payBody(cardId, "azerisiq", "1234567890", "49.50"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.BILL_PAYMENT))
                .body("balanceAfter", equalTo(0));
    }

    @Test(description = "92. FROZEN kartla ödəniş cəhdi")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-14")
    public void scenario92_frozenCardPaymentAttempt_returns422CardNotActive() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        blockCard(token, cardId, "TEMPORARY");

        authed(token)
                .header("Idempotency-Key", "idem-key-092-frozen-www")
                .body(payBody(cardId, "azerisiq", "1234567890", "50"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().spec(error(422, "CARD_NOT_ACTIVE"));
    }

    @Test(description = "93. BLOCKED kartla ödəniş cəhdi")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-14")
    public void scenario93_blockedCardPaymentAttempt_returns422CardNotActive() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        blockCard(token, cardId, "LOST");

        authed(token)
                .header("Idempotency-Key", "idem-key-093-blocked-xxx")
                .body(payBody(cardId, "azerisiq", "1234567890", "50"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().spec(error(422, "CARD_NOT_ACTIVE"));
    }

    @Test(description = "94. USD kartla ödəniş — CURRENCY_NOT_SUPPORTED (AC-14)")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-14")
    public void scenario94_usdCardPayment_returns422CurrencyNotSupported() {
        String token = loginAsUser2();

        Card usdCard = createCardEntity(token, "acc_003", 100);
        Assert.assertEquals(usdCard.getCurrency(), "USD", "user2-nin acc_003 hesabı USD-dir");
        String cardId = usdCard.getId();

        authed(token)
                .header("Idempotency-Key", "idem-key-094-usdcurrency-aaa")
                .body(payBody(cardId, "azerisiq", "1234567890", "50"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().spec(error(422, "CURRENCY_NOT_SUPPORTED"));
    }

    // ==================== YENİ (gap-closing) testlər — GRAD-AC-Reference.md əsasında ====================

    /**
     * YENİ (gap-closing) test — mövcud Gauge suite-də receiptNo-nun ardıcıllığı heç vaxt yoxlanılmayıb.
     * Mənbə sətiri: "- **AC-3** Given Sandbox-da ilk ödəniş When Cavab yoxlanır Then receiptNo
     * RCP-YYYYMMDD-000001; hər növbəti ödəniş +1" (GRAD-AC-Reference.md, GRAD-302 bölməsi).
     * Regex /^RCP-[0-9]{8}-[0-9]{6}$/ AC-3-dəki "RCP-YYYYMMDD-000001" formatından qurulub.
     */
    @Test(description = "YENİ (gap-closing): receiptNo ardıcıl ödənişlərdə +1 artır (AC-3)")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-3")
    public void acGap_receiptNoIncrementsByOneAcrossConsecutivePayments() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        String receiptNo1 = authed(token)
                .header("Idempotency-Key", "idem-key-gap-receipt1-" + System.currentTimeMillis())
                .body(payBody(cardId, "citynet", "CN123456", "5"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.BILL_PAYMENT))
                .body("receiptNo", matchesPattern(RECEIPT_NO_PATTERN))
                .extract().jsonPath().getString("receiptNo");

        String receiptNo2 = authed(token)
                .header("Idempotency-Key", "idem-key-gap-receipt2-" + System.currentTimeMillis())
                .body(payBody(cardId, "citynet", "CN123456", "5"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.BILL_PAYMENT))
                .body("receiptNo", matchesPattern(RECEIPT_NO_PATTERN))
                .extract().jsonPath().getString("receiptNo");

        long seq1 = Long.parseLong(receiptNo1.substring(receiptNo1.lastIndexOf('-') + 1));
        long seq2 = Long.parseLong(receiptNo2.substring(receiptNo2.lastIndexOf('-') + 1));
        Assert.assertEquals(seq2, seq1 + 1, "receiptNo ardıcıl ödənişlərdə +1 artmalıdır (AC-3)");
    }

    /**
     * YENİ: AC-7-nin dəqiq mətni ilə (mövcud Gauge testi fərqli kombinasiya işlədir).
     * Mənbə sətiri: "- **AC-7** Given Azercell-ə 551234567 (Bakcell nömrəsi) When Sorğu icra olunur
     * Then 422 INVALID_SUBSCRIBER" (GRAD-AC-Reference.md, GRAD-302 bölməsi). Mövcud gradBillPayments.spec
     * ssenari #80 isə Azərişıq+"123" kombinasiyasını işlədir — AC-7-nin dəqiq mətni ilə üst-üstə düşmür.
     * subscriberNo="551234567" AC-7-nin mətnindəki literal dəyərdir.
     */
    @Test(description = "YENİ: AC-7-nin dəqiq mətni ilə (mövcud Gauge testi fərqli kombinasiya işlədir)")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-7")
    public void ac7_azercellGivenBakcellNumber_returns422InvalidSubscriber() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        authed(token)
                .header("Idempotency-Key", "idem-key-ac7-azercell-bakcellno-" + System.currentTimeMillis())
                .body(payBody(cardId, "azercell", "551234567", "10"))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().spec(error(422, "INVALID_SUBSCRIBER"));
    }

    @DataProvider(name = "azercellAmountBoundaryExact")
    public Object[][] azercellAmountBoundaryExact() {
        return new Object[][]{
                {"0.49", 422, "AMOUNT_OUT_OF_RANGE"},
                {"200.01", 422, "AMOUNT_OUT_OF_RANGE"},
                {"0.50", 201, null},
                {"200.00", 201, null},
        };
    }

    /**
     * YENİ: AC-8-in dəqiq Azercell sərhəd dəyərləri ilə (mövcud Gauge testi CityNet işlədir).
     * Mənbə sətiri: "- **AC-8** Given Azercell: 0.49 və 200.01 When Sorğu icra olunur Then 422
     * AMOUNT_OUT_OF_RANGE; 0.50 və 200.00 qəbul olunur" (GRAD-AC-Reference.md, GRAD-302 bölməsi).
     * Mövcud gradBillPayments.spec ssenari #85/#86 bu sərhədi CityNet (4.99/150.01) dəyərləri ilə
     * yoxlayır — AC-8-in hərfi mətni Azercell üçündür.
     * DÜZƏLİŞ (TD-03): subscriberNo əvvəlcə "0501234567" idi — bu dəyər Azercell-in təsdiqlənmiş formatına
     * (^(50|51|10)\d{7}, yəni 9 rəqəm, prefiks 50/51/10) uyğun deyil (10 rəqəm, "05" ilə başlayır),
     * ona görə API AMOUNT_OUT_OF_RANGE əvəzinə INVALID_SUBSCRIBER qaytarırdı (test kodu bug-ı, API bug-ı deyil).
     * "501234567" (9 rəqəm, prefiks "50") formata uyğun real Azercell nömrəsidir.
     */
    @Test(dataProvider = "azercellAmountBoundaryExact",
            description = "YENİ: AC-8-in dəqiq Azercell sərhəd dəyərləri ilə (mövcud Gauge testi CityNet işlədir)")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-8")
    public void ac8_azercellExactAmountBoundary(String amountLiteral, int expectedStatus, String expectedCode) {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 1000);

        var then = authed(token)
                .header("Idempotency-Key", "idem-key-ac8-azercell-" + amountLiteral.replace(".", "")
                        + "-" + System.currentTimeMillis())
                .body(payBody(cardId, "azercell", "501234567", amountLiteral))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then();

        if (expectedCode != null) {
            then.spec(error(expectedStatus, expectedCode));
        } else {
            then.statusCode(expectedStatus).body(matchesJsonSchemaInClasspath(Schemas.BILL_PAYMENT));
        }
    }

    @DataProvider(name = "invalidMoneyAmount")
    public Object[][] invalidMoneyAmount() {
        return new Object[][]{
                {"10.555"}, // 3 onluq — "Pul sahələri number, maksimum 2 onluq"
                {"0"},      // amount > 0 olmalıdır
                {"-5"},     // mənfi məbləğ
        };
    }

    /**
     * YENİ (GAP-03): pul sahəsinin formatı — amount (>0, max 2 onluq) GRAD-302 parametr qaydasıdır və
     * yoxlama ardıcıllığında "sahə validasiyası" mərhələsinə düşür (AMOUNT_OUT_OF_RANGE-dən əvvəl),
     * ona görə 400 VALIDATION_ERROR (field: amount) gözlənilir. Mənbə: GRAD-AC-Reference.md, GRAD-302
     * "Parameters" və "Yoxlama ardıcıllığı" + "Ümumi texniki qaydalar" (pul sahələri maksimum 2 onluq).
     * Dəyərlər CityNet aralığı (5–150) daxilindədir ki, nəticəyə yalnız format qaydası təsir etsin
     * (0 və -5 istisna — onlar həm də aralıqdan kənardır, lakin sahə validasiyası daha əvvəl icra olunur).
     */
    @Test(dataProvider = "invalidMoneyAmount",
            description = "YENİ (GAP-03): amount 10.555 / 0 / -5 -> 400 VALIDATION_ERROR (field: amount)")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-15")
    public void gap03_invalidMoneyAmount_returns400ValidationError(String amount) {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        authed(token)
                .header("Idempotency-Key", "idem-key-gap03-" + amount.replace(".", "").replace("-", "m")
                        + "-" + System.currentTimeMillis())
                .body(payBody(cardId, "citynet", "CN123456", amount))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().spec(validationError("amount"));
    }
}
