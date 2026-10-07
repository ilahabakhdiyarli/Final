package com.grad.api.epic2;

import com.grad.api.base.BaseApiTest;
import com.grad.api.util.Endpoints;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * GRAD-202 — GET /cards/{id} (Kart məlumatının alınması).
 * Mənbə:
 *  - specs/api-testing/gradGetCard.spec (ssenarilər #46-#50: "Yeni buraxılmış kartın məlumatı",
 *    "Ödənişdən sonra balansın düzgün əks olunması", "Başqa istifadəçinin kartına GET cəhdi (IDOR)",
 *    "Token yoxdur/etibarsızdır", "Mövcud olmayan kart id").
 *  - GRAD-AC-Reference.md, bölmə "GRAD-202 — Kart məlumatına baxmaq (GET /cards/{id})" (AC-1..AC-3)
 *    və "Ümumi texniki qaydalar" (error format: {"error","code"} — koda görə assert; sahiblik olmayan
 *    resursa giriş → 404, 403 yox).
 *
 * Heç bir sahə adı/status/error code uydurulmayıb — hamısı yuxarıdaki mənbələrdən verbatim götürülüb.
 */
@Epic("EPIC-2: Virtual kartlar")
@Feature("GRAD-202: Kart məlumatı alma (GET)")
public class CardGetTest extends BaseApiTest {

    @Test(description = "46. Yeni buraxılmış kartın məlumatı -> 200; POST cavabı ilə eyni dəyərlər")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-1")
    public void newlyIssuedCard_returnsMatchingDetails() {
        String token = loginAsUser1();
        String cardId = given(spec).header("Authorization", "Bearer " + token)
                .body("{\"accountId\":\"acc_002\",\"initialAmount\":100,\"label\":\"Test Card\"}")
                .when().post(Endpoints.CARDS)
                .then().statusCode(201)
                .extract().jsonPath().getString("id");

        given(spec).header("Authorization", "Bearer " + token)
                .when().get(Endpoints.cardById(cardId))
                .then().statusCode(200)
                .body("accountId", equalTo("acc_002"))
                .body("status", equalTo("ACTIVE"))
                .body("availableBalance", equalTo(100))
                .body("limits.dailyOnline", equalTo(500))
                .body("limits.perTransaction", equalTo(300))
                .body("spentToday", equalTo(0))
                .body("blockReason", nullValue())
                .body("$", hasKey("id"));
    }

    @Test(description = "47. Ödənişdən sonra balansın düzgün əks olunması -> availableBalance 49.5")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-2")
    public void afterPayment_balanceReflectsCorrectly() {
        String token = loginAsUser1();
        String cardId = given(spec).header("Authorization", "Bearer " + token)
                .body("{\"accountId\":\"acc_002\",\"initialAmount\":100}")
                .when().post(Endpoints.CARDS)
                .then().statusCode(201)
                .extract().jsonPath().getString("id");

        given(spec).header("Authorization", "Bearer " + token)
                .header("Idempotency-Key", "idem-key-047-balance-zzz")
                .body("{\"cardId\":\"" + cardId + "\",\"billerId\":\"citynet\",\"subscriberNo\":\"CN123456\",\"amount\":50}")
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().statusCode(201);

        given(spec).header("Authorization", "Bearer " + token)
                .when().get(Endpoints.cardById(cardId))
                .then().statusCode(200)
                .body("availableBalance", equalTo(49.5f))
                // YENİ (gap-closing): spentToday heç vaxt assert edilmirdi — GRAD-202 AC-2 / GRAD-302 AC-2 gap-ı
                .body("spentToday", equalTo(50.5f));
    }

    @Test(description = "48. Başqa istifadəçinin kartına GET cəhdi (IDOR) -> 404 NOT_FOUND")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-3")
    public void anotherUsersCard_idor_returns404NotFound() {
        String token = loginAsUser1();
        String cardId = given(spec).header("Authorization", "Bearer " + token)
                .body("{\"accountId\":\"acc_002\",\"initialAmount\":100}")
                .when().post(Endpoints.CARDS)
                .then().statusCode(201)
                .extract().jsonPath().getString("id");

        String token2 = loginAsUser2();

        given(spec).header("Authorization", "Bearer " + token2)
                .when().get(Endpoints.cardById(cardId))
                .then().statusCode(404)
                .body("code", equalTo("NOT_FOUND"));
    }

    @Test(description = "49. Token yoxdur/etibarsızdır -> 401")
    @Severity(SeverityLevel.NORMAL)
    @Story("Security")
    public void noToken_returns401() {
        given(spec)
                .when().get(Endpoints.cardById("vcd_notreal"))
                .then().statusCode(401);
    }

    @Test(description = "50. Mövcud olmayan kart id -> 404")
    @Severity(SeverityLevel.NORMAL)
    @Story("Validation")
    public void nonExistentCardId_returns404() {
        String token = loginAsUser1();
        given(spec).header("Authorization", "Bearer " + token)
                .when().get(Endpoints.cardById("vcd_notreal"))
                .then().statusCode(404);
    }
}
