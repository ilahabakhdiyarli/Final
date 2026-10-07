package com.grad.api.epic2;

import com.grad.api.base.BaseApiTest;
import com.grad.api.util.Endpoints;
import io.qameta.allure.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

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

    private String blockBody(String reason) {
        return "{\"reason\":\"" + reason + "\"}";
    }

    /**
     * Spec #51 / AC-1: ACTIVE kart, TEMPORARY səbəbi ilə dondurulur.
     * 200; status FROZEN; blockReason TEMPORARY; blockedAt açarı mövcud.
     */
    @Test(description = "AC-1: ACTIVE kart + TEMPORARY -> 200 FROZEN, blockReason TEMPORARY, blockedAt mövcud")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-1")
    public void ac1_activeCardTemporaryReason_returns200Frozen() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        given(spec).header("Authorization", "Bearer " + token)
                .body(blockBody("TEMPORARY"))
                .when().post(Endpoints.cardBlock(cardId))
                .then().statusCode(200)
                .body("status", equalTo("FROZEN"))
                .body("blockReason", equalTo("TEMPORARY"))
                .body("blockedAt", notNullValue());
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

        given(spec).header("Authorization", "Bearer " + token)
                .body(blockBody("LOST"))
                .when().post(Endpoints.cardBlock(cardId))
                .then().statusCode(200)
                .body("status", equalTo("BLOCKED"));
    }

    /**
     * Spec #53 / AC-3: FROZEN kart, STOLEN səbəbi ilə BLOCKED-ə keçir (eskalasiya).
     * Əvvəlcə TEMPORARY ilə FROZEN edilir (200), sonra STOLEN ilə BLOCKED-ə keçir (200).
     */
    @Test(description = "AC-3: FROZEN kart + STOLEN -> 200 BLOCKED (eskalasiya)")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-3")
    public void ac3_frozenCardStolenReason_escalatesTo200Blocked() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        given(spec).header("Authorization", "Bearer " + token)
                .body(blockBody("TEMPORARY"))
                .when().post(Endpoints.cardBlock(cardId))
                .then().statusCode(200)
                .body("status", equalTo("FROZEN"));

        given(spec).header("Authorization", "Bearer " + token)
                .body(blockBody("STOLEN"))
                .when().post(Endpoints.cardBlock(cardId))
                .then().statusCode(200)
                .body("status", equalTo("BLOCKED"));
    }

    /**
     * Spec #54 / AC-4: FROZEN kart yenidən TEMPORARY ilə dondurulmaq istənir.
     * 409; code ALREADY_FROZEN.
     */
    @Test(description = "AC-4: FROZEN kart + TEMPORARY təkrar -> 409 ALREADY_FROZEN")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-4")
    public void ac4_frozenCardTemporaryAgain_returns409AlreadyFrozen() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        given(spec).header("Authorization", "Bearer " + token)
                .body(blockBody("TEMPORARY"))
                .when().post(Endpoints.cardBlock(cardId))
                .then().statusCode(200);

        given(spec).header("Authorization", "Bearer " + token)
                .body(blockBody("TEMPORARY"))
                .when().post(Endpoints.cardBlock(cardId))
                .then().statusCode(409)
                .body("code", equalTo("ALREADY_FROZEN"));
    }

    /**
     * Spec #55 / AC-5: BLOCKED kart yenidən bloklanmaq istənir.
     * Əvvəlcə LOST ilə BLOCKED edilir (200), sonra STOLEN ilə təkrar cəhd 409 CARD_BLOCKED.
     */
    @Test(description = "AC-5: BLOCKED kart + istənilən reason təkrar -> 409 CARD_BLOCKED")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-5")
    public void ac5_blockedCardBlockedAgain_returns409CardBlocked() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        given(spec).header("Authorization", "Bearer " + token)
                .body(blockBody("LOST"))
                .when().post(Endpoints.cardBlock(cardId))
                .then().statusCode(200)
                .body("status", equalTo("BLOCKED"));

        given(spec).header("Authorization", "Bearer " + token)
                .body(blockBody("STOLEN"))
                .when().post(Endpoints.cardBlock(cardId))
                .then().statusCode(409)
                .body("code", equalTo("CARD_BLOCKED"));
    }

    @DataProvider(name = "invalidReasonBVA")
    public Object[][] invalidReasonBVA() {
        return new Object[][]{
                {"{\"reason\":\"BROKEN\"}"}, // #56 Yanlış reason dəyəri
                {"{}"},                       // #57 reason göndərilmir
        };
    }

    /**
     * Spec #56 (Yanlış reason dəyəri) və #57 (reason göndərilmir) / AC-6.
     * Hər iki halda 400; code VALIDATION_ERROR.
     */
    @Test(dataProvider = "invalidReasonBVA",
            description = "AC-6: reason \"BROKEN\" və ya yoxdur -> 400 VALIDATION_ERROR")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-6")
    public void ac6_invalidOrMissingReason_returns400ValidationError(String requestBody) {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        given(spec).header("Authorization", "Bearer " + token)
                .body(requestBody)
                .when().post(Endpoints.cardBlock(cardId))
                .then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"));
    }

    /**
     * Spec #58: Token yoxdur/etibarsızdır.
     * Endpoint "/cards/vcd_notreal/block", Authorization header yoxdur -> 401.
     */
    @Test(description = "Spec #58: Token yoxdur -> 401")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Security")
    public void scenario58_noToken_returns401() {
        given(spec)
                .body(blockBody("TEMPORARY"))
                .when().post(Endpoints.cardBlock("vcd_notreal"))
                .then().statusCode(401);
    }

    /**
     * Spec #59: Mövcud olmayan kart id.
     * 404; code NOT_FOUND.
     */
    @Test(description = "Spec #59: Mövcud olmayan kart id -> 404 NOT_FOUND")
    @Severity(SeverityLevel.NORMAL)
    @Story("NOT_FOUND")
    public void scenario59_nonExistentCardId_returns404NotFound() {
        String token = loginAsUser1();

        given(spec).header("Authorization", "Bearer " + token)
                .body(blockBody("TEMPORARY"))
                .when().post(Endpoints.cardBlock("vcd_notreal"))
                .then().statusCode(404)
                .body("code", equalTo("NOT_FOUND"));
    }

    /**
     * YENİ (gap-closing) test — mövcud Gauge suite-də POST /cards/{id}/block üçün IDOR testi yox idi.
     * Model: gradGetCard.spec ## 48. Başqa istifadəçinin kartına GET cəhdi (IDOR) +
     * GRAD-AC-Reference.md ümumi qaydası ("Sahiblik olmayan resursa giriş → 404, 403 yox").
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

        given(spec).header("Authorization", "Bearer " + user2Token)
                .body(blockBody("TEMPORARY"))
                .when().post(Endpoints.cardBlock(cardId))
                .then().statusCode(404)
                .body("code", equalTo("NOT_FOUND"));
    }
}
