package com.grad.api.epic2;

import com.grad.api.base.BaseApiTest;
import com.grad.api.util.Endpoints;
import io.qameta.allure.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * GRAD-201 — POST /cards (Virtual kart buraxmaq).
 * Mənbə:
 *  - specs/api-testing/gradCreateCard.spec (ssenarilər: "Etibarlı kart buraxılışı",
 *    "maskedPan/expiry formatı", "initialAmount sərhədi (BVA)", "initialAmount format xətaları",
 *    "Başqasının hesabı ilə kart açmaq", "Kart limiti (3) aşılır", "Mövcud olmayan accountId",
 *    "Token yoxdur", "Saxta token", "XSS payload - label", "label göndərilmədikdə null").
 *  - GRAD-AC-Reference.md, bölmə "GRAD-201 — Virtual kart buraxmaq (POST /cards)" (AC-1..AC-8)
 *    və "Ümumi texniki qaydalar" (error format: {"error","code"} — koda görə assert).
 *  - TestPlan.md bölmə 3 (user1 -> acc_002 AZN, user2 -> acc_003 USD) və bölmə 5 (risklər:
 *    pul/limit ssenariləri ən yüksək prioritetdə).
 *  - specs/api-testing/gradBlockCard.spec (block body sahəsi: "reason", dəyərlər TEMPORARY/LOST/STOLEN).
 *
 * Heç bir sahə adı/status/error code uydurulmayıb — hamısı yuxarıdaki mənbələrdən verbatim götürülüb.
 */
@Epic("EPIC-2: Virtual kartlar")
@Feature("GRAD-201: Kart yaratma")
public class CardCreateTest extends BaseApiTest {

    /** Spec-dəki literal dəyərləri dəyişmədən JSON body qurur (amountJsonLiteral artıq tırnaqlı/tırnaqsız formada verilir). */
    private String body(String accountId, String amountJsonLiteral) {
        return "{\"accountId\":\"" + accountId + "\",\"initialAmount\":" + amountJsonLiteral + "}";
    }

    private String bodyWithLabel(String accountId, String amountJsonLiteral, String label) {
        return "{\"accountId\":\"" + accountId + "\",\"initialAmount\":" + amountJsonLiteral + ",\"label\":\"" + label + "\"}";
    }

    @Test(description = "Etibarlı kart buraxılışı -> 201; status/availableBalance/limits/spentToday/blockReason/id")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-1")
    public void validCardCreation_returns201WithAllFields() {
        String token = loginAsUser1();
        given(spec).header("Authorization", "Bearer " + token)
                .body(bodyWithLabel("acc_002", "100", "Test Card"))
                .when().post(Endpoints.CARDS)
                .then().statusCode(201)
                .body("accountId", equalTo("acc_002"))
                .body("status", equalTo("ACTIVE"))
                .body("availableBalance", equalTo(100))
                .body("limits.dailyOnline", equalTo(500))
                .body("limits.perTransaction", equalTo(300))
                .body("spentToday", equalTo(0))
                .body("blockReason", nullValue())
                .body("id", notNullValue());
    }

    @Test(description = "maskedPan/expiry formatı regex-ə uyğundur")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-2")
    public void maskedPanAndExpiry_matchExpectedFormat() {
        String token = loginAsUser1();
        given(spec).header("Authorization", "Bearer " + token)
                .body(body("acc_002", "50"))
                .when().post(Endpoints.CARDS)
                .then().statusCode(201)
                .body("maskedPan", matchesPattern("^4169 73[*]{2} [*]{4} [0-9]{4}$"))
                .body("expiry", matchesPattern("^[0-9]{2}/[0-9]{2}$"));
    }

    @DataProvider(name = "initialAmountBoundary")
    public Object[][] initialAmountBoundary() {
        return new Object[][]{
                {"0.99", 400},
                {"1.00", 201},
                {"1000.01", 400},
                {"1000.00", 201},
        };
    }

    @Test(dataProvider = "initialAmountBoundary",
            description = "AC-3: initialAmount sərhədi (BVA) — 0.99/1000.01 rədd, 1.00/1000.00 qəbul")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-3")
    public void initialAmountBoundary_rejectsOrAccepts(String amountLiteral, int expectedStatus) {
        String token = loginAsUser1();
        given(spec).header("Authorization", "Bearer " + token)
                .body(body("acc_002", amountLiteral))
                .when().post(Endpoints.CARDS)
                .then().statusCode(expectedStatus);
    }

    @DataProvider(name = "initialAmountFormatErrors")
    public Object[][] initialAmountFormatErrors() {
        return new Object[][]{
                {"\"50\""},  // mətn kimi göndərilir
                {"10.555"},  // həddindən çox onluq
        };
    }

    @Test(dataProvider = "initialAmountFormatErrors",
            description = "AC-4: initialAmount format xətaları ('50' mətn / 10.555) -> 400")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-4")
    public void initialAmountFormatErrors_return400(String amountLiteral) {
        String token = loginAsUser1();
        given(spec).header("Authorization", "Bearer " + token)
                .body(body("acc_002", amountLiteral))
                .when().post(Endpoints.CARDS)
                .then().statusCode(400);
    }

    @Test(description = "AC-5: Başqasının hesabı ilə kart açmaq (user1 -> acc_003) -> 403")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-5")
    public void anotherUsersAccount_returns403() {
        String token = loginAsUser1();
        given(spec).header("Authorization", "Bearer " + token)
                .body(body("acc_003", "50"))
                .when().post(Endpoints.CARDS)
                .then().statusCode(403);
    }

    @Test(description = "AC-7: Kart limiti (3) aşılır — 4-cü kart 422 CARD_LIMIT_REACHED")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-7")
    public void cardLimitExceeded_fourthCardReturns422() {
        String token = loginAsUser1();
        for (int i = 0; i < 3; i++) {
            given(spec).header("Authorization", "Bearer " + token)
                    .body(body("acc_002", "50"))
                    .when().post(Endpoints.CARDS)
                    .then().statusCode(201);
        }
        given(spec).header("Authorization", "Bearer " + token)
                .body(body("acc_002", "50"))
                .when().post(Endpoints.CARDS)
                .then().statusCode(422)
                .body("code", equalTo("CARD_LIMIT_REACHED"));
    }

    @Test(description = "Mövcud olmayan accountId -> 404")
    @Severity(SeverityLevel.NORMAL)
    @Story("Validation")
    public void nonExistentAccountId_returns404() {
        String token = loginAsUser1();
        given(spec).header("Authorization", "Bearer " + token)
                .body(body("acc_999", "50"))
                .when().post(Endpoints.CARDS)
                .then().statusCode(404);
    }

    @Test(description = "Token yoxdur -> 401")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Security")
    public void noToken_returns401() {
        given(spec)
                .body(body("acc_002", "50"))
                .when().post(Endpoints.CARDS)
                .then().statusCode(401);
    }

    @Test(description = "Saxta token -> 401")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Security")
    public void fakeToken_returns401() {
        given(spec).header("Authorization", "Bearer saxta.token.deyeri")
                .body(body("acc_002", "50"))
                .when().post(Endpoints.CARDS)
                .then().statusCode(401);
    }

    @Test(description = "XSS payload - label: 201, label dəyəri olduğu kimi (escape edilmədən) qaytarılır")
    @Severity(SeverityLevel.NORMAL)
    @Story("Security")
    public void xssPayloadInLabel_returns201WithLabelEchoedVerbatim() {
        String token = loginAsUser1();
        given(spec).header("Authorization", "Bearer " + token)
                .body(bodyWithLabel("acc_002", "50", "<script>alert(1)</script>"))
                .when().post(Endpoints.CARDS)
                .then().statusCode(201)
                .body("label", equalTo("<script>alert(1)</script>"));
    }

    @Test(description = "label göndərilmədikdə null -> 201, label: null")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-1")
    public void labelNotSent_returnsNullLabel() {
        String token = loginAsUser1();
        given(spec).header("Authorization", "Bearer " + token)
                .body(body("acc_002", "50"))
                .when().post(Endpoints.CARDS)
                .then().statusCode(201)
                .body("label", nullValue());
    }

    /**
     * YENİ (gap-closing) test — mövcud Gauge suite-də yox idi, GRAD-AC-Reference.md AC-6 əsasında yazılıb.
     * Mənbə sətiri: "- **AC-6** Given user2 891 yükləmək istəyir (balans 890.50) When Sorğu rədd edilir
     * Then 422 INSUFFICIENT_FUNDS" (GRAD-AC-Reference.md, GRAD-201 bölməsi).
     * Error formatı "Ümumi texniki qaydalar"-a əsasən koda görə assert edilir: { "error", "code" }.
     */
    @Test(description = "AC-6 (YENİ): user2, acc_003 balansından (890.50) çox initialAmount (891) -> 422 INSUFFICIENT_FUNDS")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-6")
    public void ac6_initialAmountExceedsAvailableBalance_returns422InsufficientFunds() {
        String token = loginAsUser2();
        given(spec).header("Authorization", "Bearer " + token)
                .body(body("acc_003", "891"))
                .when().post(Endpoints.CARDS)
                .then().statusCode(422)
                .body("code", equalTo("INSUFFICIENT_FUNDS"));
    }

    /**
     * YENİ (gap-closing) test — mövcud Gauge suite-də yox idi, GRAD-AC-Reference.md AC-8 əsasında yazılıb.
     * Mənbə sətiri: "- **AC-8** Given Kartlardan biri BLOCKED edilib When Yeni kart açılır Then 201 —
     * BLOCKED kart limitə sayılmır" (GRAD-AC-Reference.md, GRAD-201 bölməsi).
     * Block sorğusunun body sahəsi ("reason") specs/api-testing/gradBlockCard.spec-dən götürülüb —
     * reason "LOST" ACTIVE kartı birbaşa BLOCKED statusuna keçirir (gradBlockCard.spec, ssenari 52).
     */
    @Test(description = "AC-8 (YENİ): 3 kart var, biri BLOCKED edilir -> limitə sayılmadığı üçün 4-cü kart 201 ilə yaranır")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-8")
    public void ac8_blockedCardDoesNotCountTowardActiveCardLimit() {
        String token = loginAsUser1();
        String firstCardId = createCard(token, "acc_002", 50);
        given(spec).header("Authorization", "Bearer " + token)
                .body(body("acc_002", "50"))
                .when().post(Endpoints.CARDS)
                .then().statusCode(201);
        given(spec).header("Authorization", "Bearer " + token)
                .body(body("acc_002", "50"))
                .when().post(Endpoints.CARDS)
                .then().statusCode(201);

        given(spec).header("Authorization", "Bearer " + token)
                .body("{\"reason\":\"LOST\"}")
                .when().post(Endpoints.cardBlock(firstCardId))
                .then().statusCode(200)
                .body("status", equalTo("BLOCKED"));

        given(spec).header("Authorization", "Bearer " + token)
                .body(body("acc_002", "50"))
                .when().post(Endpoints.CARDS)
                .then().statusCode(201);
    }
}
