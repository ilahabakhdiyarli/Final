package com.grad.api.epic3;

import com.grad.api.base.BaseApiTest;
import com.grad.api.util.Endpoints;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertTrue;

/**
 * GRAD-303 — GET /bill-payments (Ödəniş tarixçəsi — filtr və səhifələmə).
 * Mənbə: specs/api-testing/gradPaymentHistory.spec (ssenari #1-13) +
 * GRAD-AC-Reference.md (GRAD-303 bölməsi, AC-1..AC-8) +
 * specs/api-testing/gradBillPayments.spec (POST /bill-payments request formatı).
 *
 * Qeyd: AC-4-ün tam mətni — "Given limit=0 / 51, page=0 / -1 / abc When Sorğu edilir
 * Then 400; limit 1 və 50 qəbul olunur" — mövcud suite-də yalnız rədd (limit=0/51,
 * page=0/-1/abc) ssenariləri var idi, qəbul sərhədləri (limit=1, limit=50) test
 * edilmirdi. Bu boşluğu bağlamaq üçün aşağıda iki YENİ test əlavə olunub.
 */
@Epic("EPIC-3: Kommunal ödənişlər")
@Feature("GRAD-303: Ödəniş tarixçəsi")
public class PaymentHistoryTest extends BaseApiTest {

    private Response postBillPayment(String token, String idempotencyKey, String cardId,
                                       String billerId, String subscriberNo, Object amount) {
        return given(spec)
                .header("Authorization", "Bearer " + token)
                .header("Idempotency-Key", idempotencyKey)
                .body("{\"cardId\":\"" + cardId + "\",\"billerId\":\"" + billerId
                        + "\",\"subscriberNo\":\"" + subscriberNo + "\",\"amount\":" + amount + "}")
                .when().post(Endpoints.BILL_PAYMENTS);
    }

    @Test(description = "1. Parametrsiz sorğu — default page/limit")
    @Severity(SeverityLevel.NORMAL)
    @Story("1")
    public void scenario1_noParams_defaultPageLimit() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);
        postBillPayment(token, "idem-key-p01-default-aaa", cardId, "azerisiq", "1234567890", 10)
                .then().statusCode(201);

        given(spec).header("Authorization", "Bearer " + token)
                .when().get(Endpoints.BILL_PAYMENTS)
                .then().statusCode(200)
                .body("page", equalTo(1))
                .body("limit", equalTo(10))
                .body("total", equalTo(1))
                .body("items.size()", equalTo(1));
    }

    @Test(description = "2. Pagination — 18 ödəniş, page=2")
    @Severity(SeverityLevel.NORMAL)
    @Story("2")
    public void scenario2_pagination_18Payments_page2() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 50);
        for (int i = 1; i <= 18; i++) {
            String idemKey = String.format("idem-key-p02-%03d-aaa", i);
            postBillPayment(token, idemKey, cardId, "azerisiq", "1234567890", 1)
                    .then().statusCode(201);
        }

        Response resp = given(spec).header("Authorization", "Bearer " + token)
                .when().get(Endpoints.BILL_PAYMENTS + "?page=2&limit=10")
                .then().statusCode(200)
                .body("page", equalTo(2))
                .body("limit", equalTo(10))
                .body("total", equalTo(18))
                .body("totalPages", equalTo(2))
                .body("items.size()", equalTo(8))
                .extract().response();

        List<String> createdAtList = resp.jsonPath().getList("items.createdAt", String.class);
        for (int i = 1; i < createdAtList.size(); i++) {
            assertTrue(createdAtList.get(i - 1).compareTo(createdAtList.get(i)) >= 0,
                    "items.createdAt siyahısı azalan sırada olmalıdır");
        }
    }

    @Test(description = "3. Mövcud olmayan səhifə — boş nəticə")
    @Severity(SeverityLevel.NORMAL)
    @Story("3")
    public void scenario3_nonExistentPage_emptyResult() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);
        postBillPayment(token, "idem-key-p03-001-aaa", cardId, "azerisiq", "1234567890", 10)
                .then().statusCode(201);
        postBillPayment(token, "idem-key-p03-002-aaa", cardId, "azerisiq", "1234567890", 10)
                .then().statusCode(201);

        given(spec).header("Authorization", "Bearer " + token)
                .when().get(Endpoints.BILL_PAYMENTS + "?page=3&limit=10")
                .then().statusCode(200)
                .body("page", equalTo(3))
                .body("total", equalTo(2))
                .body("totalPages", equalTo(1))
                .body("items.size()", equalTo(0));
    }

    @Test(description = "4. limit=0 — qəbul olunmur")
    @Severity(SeverityLevel.NORMAL)
    @Story("4")
    public void scenario4_limitZero_rejected() {
        String token = loginAsUser1();
        given(spec).header("Authorization", "Bearer " + token)
                .when().get(Endpoints.BILL_PAYMENTS + "?limit=0")
                .then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"));
    }

    @Test(description = "5. limit=51 — qəbul olunmur")
    @Severity(SeverityLevel.NORMAL)
    @Story("5")
    public void scenario5_limitFiftyOne_rejected() {
        String token = loginAsUser1();
        given(spec).header("Authorization", "Bearer " + token)
                .when().get(Endpoints.BILL_PAYMENTS + "?limit=51")
                .then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"));
    }

    @Test(description = "6. page=0 — qəbul olunmur")
    @Severity(SeverityLevel.NORMAL)
    @Story("6")
    public void scenario6_pageZero_rejected() {
        String token = loginAsUser1();
        given(spec).header("Authorization", "Bearer " + token)
                .when().get(Endpoints.BILL_PAYMENTS + "?page=0")
                .then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"));
    }

    @Test(description = "7. page=-1 — qəbul olunmur")
    @Severity(SeverityLevel.NORMAL)
    @Story("7")
    public void scenario7_pageNegativeOne_rejected() {
        String token = loginAsUser1();
        given(spec).header("Authorization", "Bearer " + token)
                .when().get(Endpoints.BILL_PAYMENTS + "?page=-1")
                .then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"));
    }

    @Test(description = "8. page=abc — rəqəm olmayan dəyər")
    @Severity(SeverityLevel.NORMAL)
    @Story("8")
    public void scenario8_pageAbc_rejected() {
        String token = loginAsUser1();
        given(spec).header("Authorization", "Bearer " + token)
                .when().get(Endpoints.BILL_PAYMENTS + "?page=abc")
                .then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"));
    }

    @Test(description = "9. Mövcud olmayan tarix (from=2026-13-01)")
    @Severity(SeverityLevel.NORMAL)
    @Story("9")
    public void scenario9_nonExistentDate_rejected() {
        String token = loginAsUser1();
        given(spec).header("Authorization", "Bearer " + token)
                .when().get(Endpoints.BILL_PAYMENTS + "?from=2026-13-01")
                .then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"));
    }

    @Test(description = "10. from > to")
    @Severity(SeverityLevel.NORMAL)
    @Story("10")
    public void scenario10_fromGreaterThanTo_rejected() {
        String token = loginAsUser1();
        given(spec).header("Authorization", "Bearer " + token)
                .when().get(Endpoints.BILL_PAYMENTS + "?from=2026-09-30&to=2026-09-01")
                .then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"));
    }

    @Test(description = "11. from = to = bu gün")
    @Severity(SeverityLevel.NORMAL)
    @Story("11")
    public void scenario11_fromEqualsToToday() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);
        postBillPayment(token, "idem-key-p11-today-aaa", cardId, "azerisiq", "1234567890", 10)
                .then().statusCode(201);

        // DÜZƏLİŞ: Əvvəlcə hardcoded literal tarix ("2026-10-05") istifadə olunurdu —
        // bu, yalnız spec sənədindəki nümunə tarixdir, real "bu gün" deyil. Test faktiki
        // icra tarixindən asılı olmalıdır, ona görə LocalDate.now() istifadə olunur (bug deyil,
        // test kodu düzəlişi).
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        given(spec).header("Authorization", "Bearer " + token)
                .when().get(Endpoints.BILL_PAYMENTS + "?from=" + today + "&to=" + today)
                .then().statusCode(200)
                .body("total", equalTo(1));
    }

    @Test(description = "12. billerId=citynet filtri")
    @Severity(SeverityLevel.NORMAL)
    @Story("12")
    public void scenario12_billerIdCitynetFilter() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);
        postBillPayment(token, "idem-key-p12-citynet-aaa", cardId, "citynet", "CN123456", 10)
                .then().statusCode(201);
        postBillPayment(token, "idem-key-p12-azerisiq-bbb", cardId, "azerisiq", "1234567890", 10)
                .then().statusCode(201);

        given(spec).header("Authorization", "Bearer " + token)
                .when().get(Endpoints.BILL_PAYMENTS + "?billerId=citynet")
                .then().statusCode(200)
                .body("total", equalTo(1))
                .body("items.billerId", everyItem(equalTo("citynet")));
    }

    @Test(description = "13. Token yoxdur")
    @Severity(SeverityLevel.NORMAL)
    @Story("13")
    public void scenario13_noToken_returns401() {
        given(spec)
                .when().get(Endpoints.BILL_PAYMENTS)
                .then().statusCode(401);
    }

    /**
     * YENİ (gap-closing) test — limit qəbul sərhədi mövcud suite-də test edilmirdi (GRAD-303 AC-4).
     * AC-4: "Given limit=0 / 51, page=0 / -1 / abc When Sorğu edilir Then 400; limit 1 və 50 qəbul olunur."
     * limit=1 — ən aşağı qəbul edilən sərhəd: 200 qaytarılmalı və items massivinin ölçüsü 1-dən çox olmamalıdır.
     */
    @Test(description = "YENİ: limit=1 — qəbul edilən aşağı sərhəd (GRAD-303 AC-4)")
    @Severity(SeverityLevel.MINOR)
    @Story("AC-4 (gap-closing)")
    public void newGap_limitOne_acceptedLowerBoundary() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);
        postBillPayment(token, "idem-key-gap-limit1-aaa", cardId, "azerisiq", "1234567890", 10)
                .then().statusCode(201);

        given(spec).header("Authorization", "Bearer " + token)
                .when().get(Endpoints.BILL_PAYMENTS + "?limit=1")
                .then().statusCode(200)
                .body("limit", equalTo(1))
                .body("items.size()", lessThanOrEqualTo(1));
    }

    /**
     * YENİ (gap-closing) test — limit qəbul sərhədi mövcud suite-də test edilmirdi (GRAD-303 AC-4).
     * AC-4: "Given limit=0 / 51, page=0 / -1 / abc When Sorğu edilir Then 400; limit 1 və 50 qəbul olunur."
     * limit=50 — yuxarı qəbul edilən sərhəd (limit=51-in rədd edildiyinin əksinə): 200 qaytarılmalıdır.
     */
    @Test(description = "YENİ: limit=50 — qəbul edilən yuxarı sərhəd (GRAD-303 AC-4)")
    @Severity(SeverityLevel.MINOR)
    @Story("AC-4 (gap-closing)")
    public void newGap_limitFifty_acceptedUpperBoundary() {
        String token = loginAsUser1();
        given(spec).header("Authorization", "Bearer " + token)
                .when().get(Endpoints.BILL_PAYMENTS + "?limit=50")
                .then().statusCode(200)
                .body("limit", equalTo(50));
    }
}
