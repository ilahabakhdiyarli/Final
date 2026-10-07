package com.grad.api.epic2;

import com.grad.api.base.BaseApiTest;
import com.grad.api.util.Endpoints;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * GRAD-204 — POST /cards/{id}/unblock (Dondurulmuş kartı açmaq).
 * Mənbə:
 *  - specs/api-testing/gradUnblockCard.spec (ssenarilər #60-64: "FROZEN kart unblock edilir",
 *    "ACTIVE kart unblock edilir (hələ dondurulmayıb)", "BLOCKED kart unblock edilir",
 *    "Token yoxdur/etibarsızdır", "Mövcud olmayan kart id").
 *  - GRAD-AC-Reference.md, bölmə "GRAD-204 — Dondurulmuş kartı açmaq (POST /cards/{id}/unblock)"
 *    (AC-1..AC-4) və "Ümumi texniki qaydalar" (error format: {"error","code"} — koda görə assert;
 *    sahiblik olmayan resursa giriş -> 404, 403 yox).
 *  - specs/api-testing/gradBlockCard.spec (block sorğusunun body sahəsi "reason", dəyərlər
 *    TEMPORARY/LOST/STOLEN; ssenari 51 TEMPORARY -> FROZEN, ssenari 52 LOST -> BLOCKED).
 *  - specs/api-testing/gradGetCard.spec, ssenari 48 "Başqa istifadəçinin kartına GET cəhdi (IDOR)"
 *    (IDOR nümunəsi: user2-nin tokeni ilə user1-in resursuna giriş -> 404 NOT_FOUND).
 *  - specs/api-testing/gradBillPayments.spec, ssenari 70 "Etibarlı ödəniş — CityNet (faiz əsaslı
 *    komissiya)" (POST /bill-payments body forması: cardId/billerId/subscriberNo/amount,
 *    Idempotency-Key header tələbi, gözlənilən 201/SUCCESS cavabı).
 *
 * Heç bir sahə adı/status/error code uydurulmayıb — hamısı yuxarıdaki mənbələrdən verbatim götürülüb.
 */
@Epic("EPIC-2: Virtual kartlar")
@Feature("GRAD-204: Dondurulmuş kartı açma")
public class CardUnblockTest extends BaseApiTest {

    @Test(description = "60. FROZEN kart unblock edilir -> 200; status ACTIVE; blockReason/blockedAt null")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-1")
    public void frozenCard_unblock_returns200ActiveStatus() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        given(spec).header("Authorization", "Bearer " + token)
                .body("{\"reason\":\"TEMPORARY\"}")
                .when().post(Endpoints.cardBlock(cardId))
                .then().statusCode(200)
                .body("status", equalTo("FROZEN"));

        given(spec).header("Authorization", "Bearer " + token)
                .when().post(Endpoints.cardUnblock(cardId))
                .then().statusCode(200)
                .body("status", equalTo("ACTIVE"))
                .body("blockReason", nullValue())
                .body("blockedAt", nullValue());
    }

    @Test(description = "61. ACTIVE kart unblock edilir (hələ dondurulmayıb) -> 409 NOT_FROZEN")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-2")
    public void activeCard_unblock_returns409NotFrozen() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        given(spec).header("Authorization", "Bearer " + token)
                .when().post(Endpoints.cardUnblock(cardId))
                .then().statusCode(409)
                .body("code", equalTo("NOT_FROZEN"));
    }

    @Test(description = "62. BLOCKED kart unblock edilir -> 409 PERMANENTLY_BLOCKED")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-3")
    public void blockedCard_unblock_returns409PermanentlyBlocked() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        given(spec).header("Authorization", "Bearer " + token)
                .body("{\"reason\":\"LOST\"}")
                .when().post(Endpoints.cardBlock(cardId))
                .then().statusCode(200)
                .body("status", equalTo("BLOCKED"));

        given(spec).header("Authorization", "Bearer " + token)
                .when().post(Endpoints.cardUnblock(cardId))
                .then().statusCode(409)
                .body("code", equalTo("PERMANENTLY_BLOCKED"));
    }

    @Test(description = "63. Token yoxdur/etibarsızdır -> 401")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Security")
    public void noToken_returns401() {
        given(spec)
                .when().post(Endpoints.cardUnblock("vcd_notreal"))
                .then().statusCode(401);
    }

    @Test(description = "64. Mövcud olmayan kart id -> 404 NOT_FOUND")
    @Severity(SeverityLevel.NORMAL)
    @Story("Validation")
    public void nonExistentCardId_returns404NotFound() {
        String token = loginAsUser1();

        given(spec).header("Authorization", "Bearer " + token)
                .when().post(Endpoints.cardUnblock("vcd_notreal"))
                .then().statusCode(404)
                .body("code", equalTo("NOT_FOUND"));
    }

    /**
     * YENİ (gap-closing) test — AC-4/IDOR mövcud suite-də yox idi.
     * Mənbə sətiri: "- **AC-4** Given Açılmış kart When Ödəniş edilir Then Ödəniş uğurla keçir (201)"
     * (GRAD-AC-Reference.md, GRAD-204 bölməsi). Kart əvvəlcə FROZEN edilir (block body forması
     * specs/api-testing/gradBlockCard.spec, ssenari 51: {"reason":"TEMPORARY"}), sonra
     * Endpoints.cardUnblock(id) ilə açılır (specs/api-testing/gradUnblockCard.spec, ssenari 60:
     * status ACTIVE). Ödəniş body forması və Idempotency-Key header tələbi
     * specs/api-testing/gradBillPayments.spec, ssenari 70-dən götürülüb:
     * {"cardId":"...","billerId":"citynet","subscriberNo":"CN123456","amount":50} -> 201, status SUCCESS.
     */
    @Test(description = "AC-4 (YENİ): Unblock edilmiş kartla ödəniş yenidən uğurla keçir -> 201 SUCCESS")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-4")
    public void ac4_unblockedCard_paymentSucceedsAgain() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        given(spec).header("Authorization", "Bearer " + token)
                .body("{\"reason\":\"TEMPORARY\"}")
                .when().post(Endpoints.cardBlock(cardId))
                .then().statusCode(200)
                .body("status", equalTo("FROZEN"));

        given(spec).header("Authorization", "Bearer " + token)
                .when().post(Endpoints.cardUnblock(cardId))
                .then().statusCode(200)
                .body("status", equalTo("ACTIVE"));

        given(spec).header("Authorization", "Bearer " + token)
                .header("Idempotency-Key", "idem-key-ac4-unblock-pay-" + UUID.randomUUID())
                .body("{\"cardId\":\"" + cardId + "\",\"billerId\":\"citynet\",\"subscriberNo\":\"CN123456\",\"amount\":50}")
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().statusCode(201)
                .body("status", equalTo("SUCCESS"));
    }

    /**
     * YENİ (gap-closing) test — AC-4/IDOR mövcud suite-də yox idi.
     * IDOR nümunəsi specs/api-testing/gradGetCard.spec, ssenari 48-dən modelləşdirilib
     * ("Başqa istifadəçinin kartına GET cəhdi (IDOR)" -> user2-nin tokeni ilə user1-in
     * kartına giriş -> 404 NOT_FOUND). "Ümumi texniki qaydalar" bölməsi: "Sahiblik olmayan
     * resursa giriş -> 404 (403 yox), resurs varlığını gizlətmək üçün" (GRAD-AC-Reference.md).
     * Burada eyni qayda POST /cards/{id}/unblock üçün yoxlanılır.
     */
    @Test(description = "IDOR (YENİ): user2-nin tokeni ilə user1-in kartını unblock cəhdi -> 404 NOT_FOUND")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Security")
    public void idor_anotherUsersCardUnblockAttempt_returns404NotFound() {
        String user1Token = loginAsUser1();
        String cardId = createCard(user1Token, "acc_002", 100);

        given(spec).header("Authorization", "Bearer " + user1Token)
                .body("{\"reason\":\"TEMPORARY\"}")
                .when().post(Endpoints.cardBlock(cardId))
                .then().statusCode(200)
                .body("status", equalTo("FROZEN"));

        String user2Token = loginAsUser2();
        given(spec).header("Authorization", "Bearer " + user2Token)
                .when().post(Endpoints.cardUnblock(cardId))
                .then().statusCode(404)
                .body("code", equalTo("NOT_FOUND"));
    }
}
