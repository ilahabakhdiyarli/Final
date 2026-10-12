package com.grad.api.epic2;

import com.grad.api.base.BaseApiTest;
import com.grad.api.models.request.BillPaymentRequest;
import com.grad.api.models.response.CardStatus;
import com.grad.api.util.Endpoints;
import com.grad.api.util.Schemas;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * GRAD-204 — POST /cards/{id}/unblock (Dondurulmuş kartı açmaq).
 * Mənbə:
 *  - specs/api-testing/gradUnblockCard.spec (ssenarilər #60-64: "FROZEN kart unblock edilir",
 *    "ACTIVE kart unblock edilir (hələ dondurulmayıb)", "BLOCKED kart unblock edilir",
 *    "Token yoxdur/etibarsızdır", "Mövcud olmayan kart id").
 *  - GRAD-AC-Reference.md, bölmə "GRAD-204 — Dondurulmuş kartı açmaq (POST /cards/{id}/unblock)"
 *    (AC-1..AC-4) və "Ümumi texniki qaydalar" (error format: {"error","code"} — koda görə assert;
 *    sahiblik olmayan resursa giriş -> 404, 403 yox).
 *  - specs/api-testing/gradBlockCard.spec (TEMPORARY -> FROZEN, LOST -> BLOCKED).
 *  - specs/api-testing/gradBillPayments.spec, ssenari 70 (POST /bill-payments body forması və
 *    Idempotency-Key header tələbi).
 */
@Epic("EPIC-2: Virtual kartlar")
@Feature("GRAD-204: Dondurulmuş kartı açma")
public class CardUnblockTest extends BaseApiTest {

    @Test(description = "60. AC-1: FROZEN kart unblock edilir -> 200; status ACTIVE; blockReason/blockedAt null")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-1")
    public void frozenCard_unblock_returns200ActiveStatus() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);
        blockCard(token, cardId, "TEMPORARY");

        CardStatus status = authed(token)
                .when().post(Endpoints.cardUnblock(cardId))
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath(Schemas.CARD_STATUS))
                .extract().as(CardStatus.class);

        assertEquals(status.getStatus(), "ACTIVE");
        assertNull(status.getBlockReason(), "blockReason null olmalıdır");
        assertNull(status.getBlockedAt(), "blockedAt null olmalıdır");
    }

    @Test(description = "61. AC-2: ACTIVE kart unblock edilir (hələ dondurulmayıb) -> 409 NOT_FROZEN")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-2")
    public void activeCard_unblock_returns409NotFrozen() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        authed(token)
                .when().post(Endpoints.cardUnblock(cardId))
                .then().spec(error(409, "NOT_FROZEN"));
    }

    @Test(description = "62. AC-3: BLOCKED kart unblock edilir -> 409 PERMANENTLY_BLOCKED")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-3")
    public void blockedCard_unblock_returns409PermanentlyBlocked() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);
        blockCard(token, cardId, "LOST");

        authed(token)
                .when().post(Endpoints.cardUnblock(cardId))
                .then().spec(error(409, "PERMANENTLY_BLOCKED"));
    }

    @Test(description = "63. Token yoxdur/etibarsızdır -> 401")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Security")
    public void noToken_returns401() {
        given(spec)
                .when().post(Endpoints.cardUnblock("vcd_notreal"))
                .then().spec(unauthorized());
    }

    @Test(description = "64. Mövcud olmayan kart id -> 404 NOT_FOUND")
    @Severity(SeverityLevel.NORMAL)
    @Story("Validation")
    public void nonExistentCardId_returns404NotFound() {
        String token = loginAsUser1();

        authed(token)
                .when().post(Endpoints.cardUnblock("vcd_notreal"))
                .then().spec(error(404, "NOT_FOUND"));
    }

    /**
     * YENİ (gap-closing) test — Mənbə sətiri: "- **AC-4** Given Açılmış kart When Ödəniş edilir
     * Then Ödəniş uğurla keçir (201)" (GRAD-AC-Reference.md, GRAD-204 bölməsi).
     */
    @Test(description = "AC-4 (YENİ): Unblock edilmiş kartla ödəniş yenidən uğurla keçir -> 201 SUCCESS")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-4")
    public void ac4_unblockedCard_paymentSucceedsAgain() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);
        blockCard(token, cardId, "TEMPORARY");

        authed(token)
                .when().post(Endpoints.cardUnblock(cardId))
                .then().statusCode(200)
                .body("status", equalTo("ACTIVE"));

        authed(token)
                .header("Idempotency-Key", "idem-key-ac4-unblock-pay-" + UUID.randomUUID())
                .body(new BillPaymentRequest(cardId, "citynet", "CN123456", new BigDecimal("50")))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.BILL_PAYMENT))
                .body("status", equalTo("SUCCESS"));
    }

    /**
     * YENİ (gap-closing) test — "Sahiblik olmayan resursa giriş -> 404 (403 yox)" qaydası
     * POST /cards/{id}/unblock üçün (GRAD-AC-Reference.md, Ümumi texniki qaydalar).
     */
    @Test(description = "IDOR (YENİ): user2-nin tokeni ilə user1-in kartını unblock cəhdi -> 404 NOT_FOUND")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Security")
    public void idor_anotherUsersCardUnblockAttempt_returns404NotFound() {
        String user1Token = loginAsUser1();
        String cardId = createCard(user1Token, "acc_002", 100);
        blockCard(user1Token, cardId, "TEMPORARY");

        String user2Token = loginAsUser2();
        authed(user2Token)
                .when().post(Endpoints.cardUnblock(cardId))
                .then().spec(error(404, "NOT_FOUND"));
    }
}
