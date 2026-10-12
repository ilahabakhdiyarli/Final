package com.grad.api.epic2;

import com.grad.api.base.BaseApiTest;
import com.grad.api.models.request.BillPaymentRequest;
import com.grad.api.models.request.CardRequest;
import com.grad.api.models.response.BillPayment;
import com.grad.api.models.response.Card;
import com.grad.api.util.Endpoints;
import com.grad.api.util.Schemas;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.assertEquals;

/**
 * GRAD-202 — GET /cards/{id} (Kart məlumatının alınması).
 * Mənbə:
 *  - specs/api-testing/gradGetCard.spec (ssenarilər #46-#50: "Yeni buraxılmış kartın məlumatı",
 *    "Ödənişdən sonra balansın düzgün əks olunması", "Başqa istifadəçinin kartına GET cəhdi (IDOR)",
 *    "Token yoxdur/etibarsızdır", "Mövcud olmayan kart id").
 *  - GRAD-AC-Reference.md, bölmə "GRAD-202 — Kart məlumatına baxmaq (GET /cards/{id})" (AC-1..AC-3)
 *    və "Ümumi texniki qaydalar" (error format: {"error","code"} — koda görə assert; sahiblik olmayan
 *    resursa giriş → 404, 403 yox).
 */
@Epic("EPIC-2: Virtual kartlar")
@Feature("GRAD-202: Kart məlumatı alma (GET)")
public class CardGetTest extends BaseApiTest {

    private Card getCard(String token, String cardId) {
        return authed(token)
                .when().get(Endpoints.cardById(cardId))
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath(Schemas.CARD))
                .extract().as(Card.class);
    }

    @Test(description = "46. AC-1: Yeni buraxılmış kartın məlumatı -> 200; POST cavabı ilə eyni dəyərlər")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-1")
    public void newlyIssuedCard_returnsMatchingDetails() {
        String token = loginAsUser1();
        Card created = authed(token)
                .body(new CardRequest("acc_002", new BigDecimal("100"), "Test Card"))
                .when().post(Endpoints.CARDS)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.CARD))
                .extract().as(Card.class);

        Card fetched = getCard(token, created.getId());

        // AC-1: GET cavabı POST cavabı ilə sahə-sahə eynidir (Card.equals bütün sahələri müqayisə edir)
        assertEquals(fetched, created, "GET /cards/{id} POST /cards cavabı ilə eyni olmalıdır");
    }

    @Test(description = "47. AC-2: 50 AZN ödənişdən sonra availableBalance total qədər azalıb, spentToday total qədər artıb")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-2")
    public void afterPayment_balanceReflectsCorrectly() {
        String token = loginAsUser1();
        Card before = createCardEntity(token, "acc_002", 100);

        BillPayment payment = authed(token)
                .header("Idempotency-Key", "idem-key-047-balance-" + UUID.randomUUID())
                .body(new BillPaymentRequest(before.getId(), "citynet", "CN123456", new BigDecimal("50")))
                .when().post(Endpoints.BILL_PAYMENTS)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.BILL_PAYMENT))
                .extract().as(BillPayment.class);

        Card after = getCard(token, before.getId());

        // CityNet: 50 + 1% fee (0.50) = 50.50 → balans 49.50, spentToday 50.50
        assertEquals(after.getAvailableBalance().compareTo(before.getAvailableBalance().subtract(payment.getTotal())), 0,
                "availableBalance total qədər azalmalıdır");
        assertEquals(after.getAvailableBalance().compareTo(new BigDecimal("49.5")), 0);
        // YENİ (gap-closing): spentToday heç vaxt assert edilmirdi — GRAD-202 AC-2 / GRAD-302 AC-2 gap-ı
        assertEquals(after.getSpentToday().compareTo(before.getSpentToday().add(payment.getTotal())), 0,
                "spentToday total qədər artmalıdır");
        assertEquals(after.getSpentToday().compareTo(new BigDecimal("50.5")), 0);
    }

    @Test(description = "48. AC-3: Başqa istifadəçinin kartına GET cəhdi (IDOR) -> 404 NOT_FOUND")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-3")
    public void anotherUsersCard_idor_returns404NotFound() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);

        String token2 = loginAsUser2();

        authed(token2)
                .when().get(Endpoints.cardById(cardId))
                .then().spec(error(404, "NOT_FOUND"));
    }

    @Test(description = "49. Token yoxdur/etibarsızdır -> 401")
    @Severity(SeverityLevel.NORMAL)
    @Story("Security")
    public void noToken_returns401() {
        given(spec)
                .when().get(Endpoints.cardById("vcd_notreal"))
                .then().spec(unauthorized());
    }

    @Test(description = "50. Mövcud olmayan kart id -> 404 NOT_FOUND")
    @Severity(SeverityLevel.NORMAL)
    @Story("Validation")
    public void nonExistentCardId_returns404() {
        String token = loginAsUser1();
        authed(token)
                .when().get(Endpoints.cardById("vcd_notreal"))
                .then().spec(error(404, "NOT_FOUND"));
    }
}
