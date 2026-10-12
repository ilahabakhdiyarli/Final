package com.grad.api.epic2;

import com.grad.api.base.BaseApiTest;
import com.grad.api.models.request.BlockCardRequest;
import com.grad.api.models.response.CardStatus;
import com.grad.api.util.Endpoints;
import com.grad.api.util.Schemas;
import io.qameta.allure.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * GRAD-203 — POST /cards/{id}/block (Kartı bloklamaq / dondurmaq).
 * Mənbə: specs/api-testing/gradBlockCard.spec (ssenari #51-59) +
 * GRAD-AC-Reference.md GRAD-203 bölməsi (AC-1..AC-7, state-transition cədvəli).
 * Yeni IDOR testi, GRAD-AC-Reference.md-in ümumi qaydasına ("Sahiblik olmayan
 * resursa giriş → 404, 403 yox") əsaslanaraq, gradGetCard.spec-dəki
 * ## 48. Başqa istifadəçinin kartına GET cəhdi (IDOR) ssenarisinin modeli üzərində qurulub.
 */
@Epic("EPIC-2: Virtual kartlar")
@Feature("GRAD-203: Kartı bloklama/dondurma")
public class CardBlockTest extends BaseApiTest {

    private BlockCardRequest blockBody(String reason) {
        return new BlockCardRequest(reason);
    }

    /** Kartı verilmiş başlanğıc statusuna gətirir: ACTIVE (heç nə), FROZEN (TEMPORARY), BLOCKED (LOST). */
    private void bringToState(String token, String cardId, String state) {
        if ("FROZEN".equals(state)) {
            blockCard(token, cardId, "TEMPORARY");
        } else if ("BLOCKED".equals(state)) {
            blockCard(token, cardId, "LOST");
        }
    }

    /**
     * Spec #51 / AC-1: ACTIVE kart, TEMPORARY səbəbi ilə dondurulur.
     * 200; status FROZEN; blockReason TEMPORARY; blockedAt dolu.
     */
    @Test(description = "AC-1: ACTIVE kart + TEMPORARY -> 200 FROZEN, blockReason TEMPORARY, blockedAt dolu")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-1")
    public void ac1_activeCardTemporaryReason_returns200Frozen() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        CardStatus status = authed(token)
                .body(blockBody("TEMPORARY"))
                .when().post(Endpoints.cardBlock(cardId))
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath(Schemas.CARD_STATUS))
                .extract().as(CardStatus.class);

        assertEquals(status.getId(), cardId);
        assertEquals(status.getStatus(), "FROZEN");
        assertEquals(status.getBlockReason(), "TEMPORARY");
        assertNotNull(status.getBlockedAt(), "blockedAt dolu olmalıdır");
    }

    /**
     * Spec #52 / AC-2: ACTIVE kart, LOST səbəbi ilə həmişəlik bloklanır.
     * 200; status BLOCKED.
     */
    @Test(description = "AC-2: ACTIVE kart + LOST -> 200 BLOCKED")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-2")
    public void ac2_activeCardLostReason_returns200Blocked() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        authed(token)
                .body(blockBody("LOST"))
                .when().post(Endpoints.cardBlock(cardId))
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath(Schemas.CARD_STATUS))
                .body("status", equalTo("BLOCKED"))
                .body("blockReason", equalTo("LOST"));
    }

    /**
     * Spec #53 / AC-3: FROZEN kart, STOLEN səbəbi ilə BLOCKED-ə keçir (eskalasiya).
     */
    @Test(description = "AC-3: FROZEN kart + STOLEN -> 200 BLOCKED (eskalasiya)")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-3")
    public void ac3_frozenCardStolenReason_escalatesTo200Blocked() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);
        blockCard(token, cardId, "TEMPORARY");

        authed(token)
                .body(blockBody("STOLEN"))
                .when().post(Endpoints.cardBlock(cardId))
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath(Schemas.CARD_STATUS))
                .body("status", equalTo("BLOCKED"));
    }

    /**
     * Spec #54 / AC-4: FROZEN kart yenidən TEMPORARY ilə dondurulmaq istənir -> 409 ALREADY_FROZEN.
     */
    @Test(description = "AC-4: FROZEN kart + TEMPORARY təkrar -> 409 ALREADY_FROZEN")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-4")
    public void ac4_frozenCardTemporaryAgain_returns409AlreadyFrozen() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);
        blockCard(token, cardId, "TEMPORARY");

        authed(token)
                .body(blockBody("TEMPORARY"))
                .when().post(Endpoints.cardBlock(cardId))
                .then().spec(error(409, "ALREADY_FROZEN"));
    }

    /**
     * Spec #55 / AC-5: BLOCKED kart yenidən bloklanmaq istənir -> 409 CARD_BLOCKED.
     * (Bütün reason-lar üçün tam yoxlama aşağıdakı state-transition testindədir — GAP-08.)
     */
    @Test(description = "AC-5: BLOCKED kart + STOLEN təkrar -> 409 CARD_BLOCKED")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-5")
    public void ac5_blockedCardBlockedAgain_returns409CardBlocked() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);
        blockCard(token, cardId, "LOST");

        authed(token)
                .body(blockBody("STOLEN"))
                .when().post(Endpoints.cardBlock(cardId))
                .then().spec(error(409, "CARD_BLOCKED"));
    }

    @DataProvider(name = "invalidReason")
    public Object[][] invalidReason() {
        return new Object[][]{
                {"BROKEN"}, // #56 Yanlış reason dəyəri
                {null},     // #57 reason göndərilmir -> body "{}"
        };
    }

    /**
     * Spec #56 (Yanlış reason dəyəri) və #57 (reason göndərilmir) / AC-6.
     * Hər iki halda 400; code VALIDATION_ERROR; details[].field = reason.
     */
    @Test(dataProvider = "invalidReason",
            description = "AC-6: reason \"BROKEN\" və ya yoxdur -> 400 VALIDATION_ERROR")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-6")
    public void ac6_invalidOrMissingReason_returns400ValidationError(String reason) {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        authed(token)
                .body(blockBody(reason))
                .when().post(Endpoints.cardBlock(cardId))
                .then().spec(validationError("reason"));
    }

    /**
     * State-transition cədvəli (GRAD-AC-Reference.md, GRAD-203) — 3 başlanğıc status × 3 reason = 9 keçid:
     *   ACTIVE:  TEMPORARY→200 FROZEN,          LOST/STOLEN→200 BLOCKED
     *   FROZEN:  TEMPORARY→409 ALREADY_FROZEN,  LOST/STOLEN→200 BLOCKED
     *   BLOCKED: TEMPORARY→409 CARD_BLOCKED,    LOST/STOLEN→409 CARD_BLOCKED
     * Sütunlar: başlanğıc status, reason, gözlənilən HTTP status, gözlənilən yeni status (200 üçün) / code (409 üçün).
     */
    @DataProvider(name = "stateTransitionTable")
    public Object[][] stateTransitionTable() {
        return new Object[][]{
                {"ACTIVE", "TEMPORARY", 200, "FROZEN"},
                {"ACTIVE", "LOST", 200, "BLOCKED"},
                {"ACTIVE", "STOLEN", 200, "BLOCKED"},
                {"FROZEN", "TEMPORARY", 409, "ALREADY_FROZEN"},
                {"FROZEN", "LOST", 200, "BLOCKED"},
                {"FROZEN", "STOLEN", 200, "BLOCKED"},
                {"BLOCKED", "TEMPORARY", 409, "CARD_BLOCKED"},
                {"BLOCKED", "LOST", 409, "CARD_BLOCKED"},
                {"BLOCKED", "STOLEN", 409, "CARD_BLOCKED"},
        };
    }

    @Test(dataProvider = "stateTransitionTable",
            description = "State transition (AC-1..AC-5, GAP-08): status × reason cədvəlinin bütün 9 keçidi")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-5")
    public void stateTransitionTable_allNineTransitions(String fromState, String reason,
                                                        int expectedHttp, String expectedStateOrCode) {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);
        bringToState(token, cardId, fromState);

        var then = authed(token)
                .body(blockBody(reason))
                .when().post(Endpoints.cardBlock(cardId))
                .then();
        if (expectedHttp == 200) {
            then.statusCode(200)
                    .body(matchesJsonSchemaInClasspath(Schemas.CARD_STATUS))
                    .body("status", equalTo(expectedStateOrCode))
                    .body("blockReason", equalTo(reason));
        } else {
            then.spec(error(expectedHttp, expectedStateOrCode));
        }
    }

    /**
     * Spec #58: Token yoxdur/etibarsızdır -> 401.
     */
    @Test(description = "Spec #58: Token yoxdur -> 401")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Security")
    public void scenario58_noToken_returns401() {
        given(spec)
                .body(blockBody("TEMPORARY"))
                .when().post(Endpoints.cardBlock("vcd_notreal"))
                .then().spec(unauthorized());
    }

    /**
     * Spec #59: Mövcud olmayan kart id -> 404 NOT_FOUND.
     */
    @Test(description = "Spec #59: Mövcud olmayan kart id -> 404 NOT_FOUND")
    @Severity(SeverityLevel.NORMAL)
    @Story("NOT_FOUND")
    public void scenario59_nonExistentCardId_returns404NotFound() {
        String token = loginAsUser1();

        authed(token)
                .body(blockBody("TEMPORARY"))
                .when().post(Endpoints.cardBlock("vcd_notreal"))
                .then().spec(error(404, "NOT_FOUND"));
    }

    /**
     * YENİ (gap-closing) test — mövcud Gauge suite-də POST /cards/{id}/block üçün IDOR testi yox idi.
     * user1 kart yaradır; user2 öz token-i ilə user1-in kartını bloklamağa çalışır ->
     * 404; code NOT_FOUND (403 yox).
     */
    @Test(description = "YENİ (gap-closing): Başqa istifadəçinin kartına POST /block cəhdi (IDOR) -> 404 NOT_FOUND")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Security")
    public void newGapClosing_blockAnotherUsersCard_returns404NotFound() {
        String user1Token = loginAsUser1();
        String cardId = createCard(user1Token, "acc_002", 100);

        String user2Token = loginAsUser2();

        authed(user2Token)
                .body(blockBody("TEMPORARY"))
                .when().post(Endpoints.cardBlock(cardId))
                .then().spec(error(404, "NOT_FOUND"));
    }
}
