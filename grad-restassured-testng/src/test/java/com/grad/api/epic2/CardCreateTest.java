package com.grad.api.epic2;

import com.grad.api.base.BaseApiTest;
import com.grad.api.models.request.BlockCardRequest;
import com.grad.api.models.request.CardRequest;
import com.grad.api.models.response.Card;
import com.grad.api.util.Endpoints;
import com.grad.api.util.Schemas;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

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
 * Heç bir sahə adı/status/error code uydurulmayıb — hamısı yuxarıdaki mənbələrdən və real API
 * cavablarından (Allure qeydləri) götürülüb.
 */
@Epic("EPIC-2: Virtual kartlar")
@Feature("GRAD-201: Kart yaratma")
public class CardCreateTest extends BaseApiTest {

    private CardRequest body(String accountId, String amount) {
        return new CardRequest(accountId, new BigDecimal(amount));
    }

    /** Yalnız tip/format xətası ssenariləri üçün — amountJsonLiteral JSON-a olduğu kimi yazılır. */
    private String rawBody(String accountId, String amountJsonLiteral) {
        return "{\"accountId\":\"" + accountId + "\",\"initialAmount\":" + amountJsonLiteral + "}";
    }

    @Test(description = "AC-1: Etibarlı kart buraxılışı -> 201; status ACTIVE; availableBalance = initialAmount; limitlər 500/300; spentToday 0")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-1")
    public void validCardCreation_returns201WithAllFields() {
        String token = loginAsUser1();
        Response response = authed(token)
                .body(new CardRequest("acc_002", new BigDecimal("100"), "Test Card"))
                .when().post(Endpoints.CARDS)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.CARD))
                .extract().response();

        Card card = response.as(Card.class);
        assertEquals(card.getAccountId(), "acc_002");
        assertEquals(card.getStatus(), "ACTIVE");
        assertEquals(card.getAvailableBalance().compareTo(new BigDecimal("100")), 0, "availableBalance = initialAmount");
        assertEquals(card.getLimits().getDailyOnline().compareTo(new BigDecimal("500")), 0, "limits.dailyOnline = 500");
        assertEquals(card.getLimits().getPerTransaction().compareTo(new BigDecimal("300")), 0, "limits.perTransaction = 300");
        assertEquals(card.getSpentToday().compareTo(BigDecimal.ZERO), 0, "spentToday = 0");
        assertEquals(card.getLabel(), "Test Card");
        assertNull(card.getBlockReason());
        assertEquals(response.header("Location"), "/cards/" + card.getId());
    }

    /**
     * AC-2: maskedPan "4169 73** **** NNNN"; expiry MM/YY — bu gündən 3 il sonra.
     * GAP-04 bağlanır: əvvəllər yalnız MM/YY regex-i yoxlanırdı, indi expiry kartın createdAt
     * tarixinə (UTC) +3 il ilə dəqiq müqayisə olunur.
     */
    @Test(description = "AC-2: maskedPan formatı; expiry MM/YY = buraxılış tarixi + 3 il")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-2")
    public void maskedPanAndExpiry_matchExpectedFormat() {
        String token = loginAsUser1();
        Card card = authed(token)
                .body(body("acc_002", "50"))
                .when().post(Endpoints.CARDS)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.CARD))
                .body("maskedPan", matchesPattern("^4169 73[*]{2} [*]{4} [0-9]{4}$"))
                .body("expiry", matchesPattern("^[0-9]{2}/[0-9]{2}$"))
                .extract().as(Card.class);

        ZonedDateTime issuedAt = Instant.parse(card.getCreatedAt()).atZone(ZoneOffset.UTC);
        String expectedExpiry = issuedAt.plusYears(3).format(DateTimeFormatter.ofPattern("MM/yy"));
        assertEquals(card.getExpiry(), expectedExpiry, "expiry buraxılış tarixindən 3 il sonra olmalıdır");
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
    public void initialAmountBoundary_rejectsOrAccepts(String amount, int expectedStatus) {
        String token = loginAsUser1();
        var then = authed(token)
                .body(body("acc_002", amount))
                .when().post(Endpoints.CARDS)
                .then();
        if (expectedStatus == 400) {
            then.spec(validationError("initialAmount"));
        } else {
            Card card = then.statusCode(201).body(matchesJsonSchemaInClasspath(Schemas.CARD))
                    .extract().as(Card.class);
            assertEquals(card.getAvailableBalance().compareTo(new BigDecimal(amount)), 0,
                    "availableBalance = initialAmount (" + amount + ")");
        }
    }

    @DataProvider(name = "initialAmountFormatErrors")
    public Object[][] initialAmountFormatErrors() {
        return new Object[][]{
                {"\"50\""},  // mətn kimi göndərilir
                {"10.555"},  // həddindən çox onluq
        };
    }

    @Test(dataProvider = "initialAmountFormatErrors",
            description = "AC-4: initialAmount format xətaları ('50' mətn / 10.555) -> 400 VALIDATION_ERROR")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-4")
    public void initialAmountFormatErrors_return400(String amountLiteral) {
        String token = loginAsUser1();
        // Bilərəkdən xam JSON — POJO-nun BigDecimal sahəsi mətn tipini göndərməyə imkan vermir.
        authed(token)
                .body(rawBody("acc_002", amountLiteral))
                .when().post(Endpoints.CARDS)
                .then().spec(validationError("initialAmount"));
    }

    @Test(description = "AC-5: Başqasının hesabı ilə kart açmaq (user1 -> acc_003) -> 403 FORBIDDEN")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-5")
    public void anotherUsersAccount_returns403() {
        String token = loginAsUser1();
        authed(token)
                .body(body("acc_003", "50"))
                .when().post(Endpoints.CARDS)
                .then().spec(error(403, "FORBIDDEN"));
    }

    @Test(description = "AC-7: Kart limiti (3) aşılır — 4-cü kart 422 CARD_LIMIT_REACHED")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-7")
    public void cardLimitExceeded_fourthCardReturns422() {
        String token = loginAsUser1();
        for (int i = 0; i < 3; i++) {
            createCard(token, "acc_002", 50);
        }
        authed(token)
                .body(body("acc_002", "50"))
                .when().post(Endpoints.CARDS)
                .then().spec(error(422, "CARD_LIMIT_REACHED"));
    }

    @Test(description = "Mövcud olmayan accountId -> 404 ACCOUNT_NOT_FOUND")
    @Severity(SeverityLevel.NORMAL)
    @Story("Validation")
    public void nonExistentAccountId_returns404() {
        String token = loginAsUser1();
        authed(token)
                .body(body("acc_999", "50"))
                .when().post(Endpoints.CARDS)
                .then().spec(error(404, "ACCOUNT_NOT_FOUND"));
    }

    @Test(description = "Token yoxdur -> 401")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Security")
    public void noToken_returns401() {
        given(spec)
                .body(body("acc_002", "50"))
                .when().post(Endpoints.CARDS)
                .then().spec(unauthorized());
    }

    @Test(description = "Saxta token -> 401")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Security")
    public void fakeToken_returns401() {
        authed("saxta.token.deyeri")
                .body(body("acc_002", "50"))
                .when().post(Endpoints.CARDS)
                .then().spec(unauthorized());
    }

    @Test(description = "XSS payload - label: 201, label dəyəri olduğu kimi (escape edilmədən) qaytarılır")
    @Severity(SeverityLevel.NORMAL)
    @Story("Security")
    public void xssPayloadInLabel_returns201WithLabelEchoedVerbatim() {
        String token = loginAsUser1();
        authed(token)
                .body(new CardRequest("acc_002", new BigDecimal("50"), "<script>alert(1)</script>"))
                .when().post(Endpoints.CARDS)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.CARD))
                .body("label", equalTo("<script>alert(1)</script>"));
    }

    @Test(description = "label göndərilmədikdə null -> 201, label: null")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-1")
    public void labelNotSent_returnsNullLabel() {
        String token = loginAsUser1();
        Card card = createCardEntity(token, "acc_002", 50);
        assertNull(card.getLabel(), "label göndərilmədikdə null olmalıdır");
    }

    /**
     * YENİ (gap-closing) test — mövcud Gauge suite-də yox idi, GRAD-AC-Reference.md AC-6 əsasında yazılıb.
     * Mənbə sətiri: "- **AC-6** Given user2 891 yükləmək istəyir (balans 890.50) When Sorğu rədd edilir
     * Then 422 INSUFFICIENT_FUNDS" (GRAD-AC-Reference.md, GRAD-201 bölməsi).
     */
    @Test(description = "AC-6 (YENİ): user2, acc_003 balansından (890.50) çox initialAmount (891) -> 422 INSUFFICIENT_FUNDS")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-6")
    public void ac6_initialAmountExceedsAvailableBalance_returns422InsufficientFunds() {
        String token = loginAsUser2();
        authed(token)
                .body(body("acc_003", "891"))
                .when().post(Endpoints.CARDS)
                .then().spec(error(422, "INSUFFICIENT_FUNDS"));
    }

    /**
     * YENİ (gap-closing) test — mövcud Gauge suite-də yox idi, GRAD-AC-Reference.md AC-8 əsasında yazılıb.
     * Mənbə sətiri: "- **AC-8** Given Kartlardan biri BLOCKED edilib When Yeni kart açılır Then 201 —
     * BLOCKED kart limitə sayılmır" (GRAD-AC-Reference.md, GRAD-201 bölməsi).
     * reason "LOST" ACTIVE kartı birbaşa BLOCKED statusuna keçirir (gradBlockCard.spec, ssenari 52).
     */
    @Test(description = "AC-8 (YENİ): 3 kart var, biri BLOCKED edilir -> limitə sayılmadığı üçün 4-cü kart 201 ilə yaranır")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-8")
    public void ac8_blockedCardDoesNotCountTowardActiveCardLimit() {
        String token = loginAsUser1();
        String firstCardId = createCard(token, "acc_002", 50);
        createCard(token, "acc_002", 50);
        createCard(token, "acc_002", 50);

        authed(token)
                .body(new BlockCardRequest("LOST"))
                .when().post(Endpoints.cardBlock(firstCardId))
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath(Schemas.CARD_STATUS))
                .body("status", equalTo("BLOCKED"));

        Card fourth = createCardEntity(token, "acc_002", 50);
        assertEquals(fourth.getStatus(), "ACTIVE", "BLOCKED kart limitə sayılmadığı üçün 4-cü kart ACTIVE yaranmalıdır");
    }
}
