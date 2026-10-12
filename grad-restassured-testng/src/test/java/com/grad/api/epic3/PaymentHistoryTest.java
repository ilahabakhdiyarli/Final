package com.grad.api.epic3;

import com.grad.api.base.BaseApiTest;
import com.grad.api.models.request.BillPaymentRequest;
import com.grad.api.models.response.BillPaymentList;
import com.grad.api.util.Endpoints;
import com.grad.api.util.Schemas;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;
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

    /** Tarix filtrləri Bakı vaxtı ilə işləyir (GRAD-AC-Reference.md, GRAD-303). */
    private static final ZoneId BAKU = ZoneId.of("Asia/Baku");

    private Response postBillPayment(String token, String idempotencyKey, String cardId,
                                     String billerId, String subscriberNo, Number amount) {
        return authed(token)
                .header("Idempotency-Key", idempotencyKey)
                .body(new BillPaymentRequest(cardId, billerId, subscriberNo, new BigDecimal(String.valueOf(amount))))
                .when().post(Endpoints.BILL_PAYMENTS);
    }

    private BillPaymentList history(String token, String query) {
        return authed(token)
                .when().get(Endpoints.BILL_PAYMENTS + query)
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath(Schemas.BILL_PAYMENT_LIST))
                .extract().as(BillPaymentList.class);
    }

    @Test(description = "1. AC-1: Parametrsiz sorğu — default page 1, limit 10")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-1")
    public void scenario1_noParams_defaultPageLimit() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);
        postBillPayment(token, "idem-key-p01-default-aaa", cardId, "azerisiq", "1234567890", 10)
                .then().statusCode(201);

        BillPaymentList list = history(token, "");
        assertEquals(list.getPage(), 1);
        assertEquals(list.getLimit(), 10);
        assertEquals(list.getTotal(), 1);
        assertEquals(list.getItems().size(), 1);
    }

    @Test(description = "2. AC-2: Pagination — 18 ödəniş, limit 10, page=2 -> 8 element, totalPages 2; ən yenidən köhnəyə")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-2")
    public void scenario2_pagination_18Payments_page2() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 50);
        for (int i = 1; i <= 18; i++) {
            String idemKey = String.format("idem-key-p02-%03d-aaa", i);
            postBillPayment(token, idemKey, cardId, "azerisiq", "1234567890", 1)
                    .then().statusCode(201);
        }

        BillPaymentList page2 = history(token, "?page=2&limit=10");
        assertEquals(page2.getPage(), 2);
        assertEquals(page2.getLimit(), 10);
        assertEquals(page2.getTotal(), 18);
        assertEquals(page2.getTotalPages(), 2);
        assertEquals(page2.getItems().size(), 8);

        List<BillPaymentList.Item> items = page2.getItems();
        for (int i = 1; i < items.size(); i++) {
            assertTrue(items.get(i - 1).getCreatedAt().compareTo(items.get(i).getCreatedAt()) >= 0,
                    "items.createdAt siyahısı azalan sırada olmalıdır (ən yeni birinci)");
        }
    }

    @Test(description = "3. AC-3: Mövcud olmayan səhifə (page=3) — 200, items []")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-3")
    public void scenario3_nonExistentPage_emptyResult() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);
        postBillPayment(token, "idem-key-p03-001-aaa", cardId, "azerisiq", "1234567890", 10)
                .then().statusCode(201);
        postBillPayment(token, "idem-key-p03-002-aaa", cardId, "azerisiq", "1234567890", 10)
                .then().statusCode(201);

        BillPaymentList page3 = history(token, "?page=3&limit=10");
        assertEquals(page3.getPage(), 3);
        assertEquals(page3.getTotal(), 2);
        assertEquals(page3.getTotalPages(), 1);
        assertTrue(page3.getItems().isEmpty(), "mövcud olmayan səhifə boş siyahı qaytarmalıdır");
    }

    @Test(description = "4. AC-4: limit=0 — qəbul olunmur -> 400 VALIDATION_ERROR")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-4")
    public void scenario4_limitZero_rejected() {
        String token = loginAsUser1();
        authed(token)
                .when().get(Endpoints.BILL_PAYMENTS + "?limit=0")
                .then().spec(validationError("limit"));
    }

    @Test(description = "5. AC-4: limit=51 — qəbul olunmur -> 400 VALIDATION_ERROR")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-4")
    public void scenario5_limitFiftyOne_rejected() {
        String token = loginAsUser1();
        authed(token)
                .when().get(Endpoints.BILL_PAYMENTS + "?limit=51")
                .then().spec(validationError("limit"));
    }

    @Test(description = "6. AC-4: page=0 — qəbul olunmur -> 400 VALIDATION_ERROR")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-4")
    public void scenario6_pageZero_rejected() {
        String token = loginAsUser1();
        authed(token)
                .when().get(Endpoints.BILL_PAYMENTS + "?page=0")
                .then().spec(validationError("page"));
    }

    @Test(description = "7. AC-4: page=-1 — qəbul olunmur -> 400 VALIDATION_ERROR")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-4")
    public void scenario7_pageNegativeOne_rejected() {
        String token = loginAsUser1();
        authed(token)
                .when().get(Endpoints.BILL_PAYMENTS + "?page=-1")
                .then().spec(validationError("page"));
    }

    @Test(description = "8. AC-4: page=abc — rəqəm olmayan dəyər -> 400 VALIDATION_ERROR")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-4")
    public void scenario8_pageAbc_rejected() {
        String token = loginAsUser1();
        authed(token)
                .when().get(Endpoints.BILL_PAYMENTS + "?page=abc")
                .then().spec(validationError("page"));
    }

    @Test(description = "9. AC-5: Mövcud olmayan tarix (from=2026-13-01) -> 400 VALIDATION_ERROR")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-5")
    public void scenario9_nonExistentDate_rejected() {
        String token = loginAsUser1();
        authed(token)
                .when().get(Endpoints.BILL_PAYMENTS + "?from=2026-13-01")
                .then().spec(validationError("from"));
    }

    @Test(description = "10. AC-6: from > to -> 400 VALIDATION_ERROR")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-6")
    public void scenario10_fromGreaterThanTo_rejected() {
        String token = loginAsUser1();
        authed(token)
                .when().get(Endpoints.BILL_PAYMENTS + "?from=2026-09-30&to=2026-09-01")
                .then().spec(validationError("from"));
    }

    @Test(description = "11. AC-7: from = to = bu gün (Bakı vaxtı) -> bu günün bütün ödənişləri")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-7")
    public void scenario11_fromEqualsToToday() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);
        postBillPayment(token, "idem-key-p11-today-aaa", cardId, "azerisiq", "1234567890", 10)
                .then().statusCode(201);

        // DÜZƏLİŞ: Əvvəlcə hardcoded literal tarix ("2026-10-05") istifadə olunurdu — bu, yalnız spec
        // sənədindəki nümunə tarixdir, real "bu gün" deyil. Tarix filtrləri Bakı vaxtı ilə işlədiyi
        // üçün (GRAD-303), "bu gün" kompüterin saat qurşağından asılı olmayaraq Asia/Baku ilə hesablanır.
        String today = LocalDate.now(BAKU).format(DateTimeFormatter.ISO_LOCAL_DATE);
        BillPaymentList list = history(token, "?from=" + today + "&to=" + today);
        assertEquals(list.getTotal(), 1);
    }

    @Test(description = "12. AC-8: billerId=citynet filtri — yalnız citynet ödənişləri; total uyğun")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-8")
    public void scenario12_billerIdCitynetFilter() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);
        postBillPayment(token, "idem-key-p12-citynet-aaa", cardId, "citynet", "CN123456", 10)
                .then().statusCode(201);
        postBillPayment(token, "idem-key-p12-azerisiq-bbb", cardId, "azerisiq", "1234567890", 10)
                .then().statusCode(201);

        authed(token)
                .when().get(Endpoints.BILL_PAYMENTS + "?billerId=citynet")
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath(Schemas.BILL_PAYMENT_LIST))
                .body("total", equalTo(1))
                .body("items.billerId", everyItem(equalTo("citynet")));
    }

    @Test(description = "13. Token yoxdur -> 401")
    @Severity(SeverityLevel.NORMAL)
    @Story("Security")
    public void scenario13_noToken_returns401() {
        given(spec)
                .when().get(Endpoints.BILL_PAYMENTS)
                .then().spec(unauthorized());
    }

    /**
     * YENİ (gap-closing) test — limit qəbul sərhədi mövcud suite-də test edilmirdi (GRAD-303 AC-4).
     * limit=1 — ən aşağı qəbul edilən sərhəd: 200 qaytarılmalı və items massivinin ölçüsü 1-dən çox olmamalıdır.
     */
    @Test(description = "YENİ: AC-4 limit=1 — qəbul edilən aşağı sərhəd")
    @Severity(SeverityLevel.MINOR)
    @Story("AC-4")
    public void newGap_limitOne_acceptedLowerBoundary() {
        String token = loginAsUser1();
        String cardId = createCard(token, "acc_002", 100);
        postBillPayment(token, "idem-key-gap-limit1-aaa", cardId, "azerisiq", "1234567890", 10)
                .then().statusCode(201);

        BillPaymentList list = history(token, "?limit=1");
        assertEquals(list.getLimit(), 1);
        assertTrue(list.getItems().size() <= 1, "limit=1 olduqda ən çox 1 element qaytarılmalıdır");
    }

    /**
     * YENİ (gap-closing) test — limit qəbul sərhədi mövcud suite-də test edilmirdi (GRAD-303 AC-4).
     * limit=50 — yuxarı qəbul edilən sərhəd (limit=51-in rədd edildiyinin əksinə): 200 qaytarılmalıdır.
     */
    @Test(description = "YENİ: AC-4 limit=50 — qəbul edilən yuxarı sərhəd")
    @Severity(SeverityLevel.MINOR)
    @Story("AC-4")
    public void newGap_limitFifty_acceptedUpperBoundary() {
        String token = loginAsUser1();
        BillPaymentList list = history(token, "?limit=50");
        assertEquals(list.getLimit(), 50);
    }
}
