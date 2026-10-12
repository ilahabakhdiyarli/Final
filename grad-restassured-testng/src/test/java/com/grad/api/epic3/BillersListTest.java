package com.grad.api.epic3;

import com.grad.api.base.BaseApiTest;
import com.grad.api.models.response.BillerList;
import com.grad.api.util.Endpoints;
import com.grad.api.util.Schemas;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * GRAD-301 — GET /billers (Xidmət təchizatçıları kataloqu).
 * Mənbə: GRAD-AC-Reference.md (AC-1..AC-3) + specs/api-testing/gradBillersList.spec (ssenari #65-69,
 * Gauge suite-dəki orijinal nömrələnmə). Kataloq dəyərləri real GET /billers cavabı ilə təsdiqlənib.
 */
@Epic("EPIC-3: Kommunal ödənişlər")
@Feature("GRAD-301: Xidmət təchizatçıları kataloqu")
public class BillersListTest extends BaseApiTest {

    @Test(description = "65. Parametrsiz sorğu — bütün billerlər qaytarılır")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-1")
    public void scenario65_noParams_returnsAllBillers() {
        String token = loginAsUser1();

        BillerList billers = authed(token)
                .when().get(Endpoints.BILLERS)
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath(Schemas.BILLERS_LIST))
                .body("total", equalTo(6))
                .body("items.find { it.id == 'azerisiq' }.category", equalTo("UTILITY"))
                .body("items.find { it.id == 'azerisiq' }.minAmount", equalTo(1))
                .body("items.find { it.id == 'azerisiq' }.maxAmount", equalTo(500))
                .body("items.find { it.id == 'azerisiq' }.fee.type", equalTo("NONE"))
                .body("items.find { it.id == 'azerisiq' }.subscriberPattern", equalTo("^\\d{10}$"))
                .body("items.find { it.id == 'azersu' }.category", equalTo("UTILITY"))
                .body("items.find { it.id == 'azersu' }.minAmount", equalTo(1))
                .body("items.find { it.id == 'azersu' }.maxAmount", equalTo(300))
                .body("items.find { it.id == 'azersu' }.fee.type", equalTo("NONE"))
                .body("items.find { it.id == 'azersu' }.subscriberPattern", equalTo("^\\d{8}$"))
                .body("items.find { it.id == 'azercell' }.category", equalTo("MOBILE"))
                .body("items.find { it.id == 'azercell' }.minAmount", equalTo(0.5f))
                .body("items.find { it.id == 'azercell' }.maxAmount", equalTo(200))
                .body("items.find { it.id == 'azercell' }.fee.type", equalTo("NONE"))
                .body("items.find { it.id == 'azercell' }.subscriberPattern", equalTo("^(50|51|10)\\d{7}$"))
                .body("items.find { it.id == 'bakcell' }.category", equalTo("MOBILE"))
                .body("items.find { it.id == 'bakcell' }.minAmount", equalTo(0.5f))
                .body("items.find { it.id == 'bakcell' }.maxAmount", equalTo(200))
                .body("items.find { it.id == 'bakcell' }.fee.type", equalTo("NONE"))
                .body("items.find { it.id == 'bakcell' }.subscriberPattern", equalTo("^(55|99)\\d{7}$"))
                .body("items.find { it.id == 'citynet' }.category", equalTo("INTERNET"))
                .body("items.find { it.id == 'citynet' }.minAmount", equalTo(5))
                .body("items.find { it.id == 'citynet' }.maxAmount", equalTo(150))
                .body("items.find { it.id == 'citynet' }.fee.type", equalTo("PERCENT"))
                .body("items.find { it.id == 'citynet' }.fee.value", equalTo(1))
                .body("items.find { it.id == 'citynet' }.fee.min", equalTo(0.2f))
                .body("items.find { it.id == 'citynet' }.fee.max", equalTo(2))
                .body("items.find { it.id == 'citynet' }.subscriberPattern", equalTo("^CN\\d{6}$"))
                .body("items.find { it.id == 'aztelekom' }.category", equalTo("INTERNET"))
                .body("items.find { it.id == 'aztelekom' }.minAmount", equalTo(2))
                .body("items.find { it.id == 'aztelekom' }.maxAmount", equalTo(100))
                .body("items.find { it.id == 'aztelekom' }.fee.type", equalTo("FIXED"))
                .body("items.find { it.id == 'aztelekom' }.fee.value", equalTo(0.5f))
                .body("items.find { it.id == 'aztelekom' }.subscriberPattern", equalTo("^\\d{7}$"))
                .extract().as(BillerList.class);

        // AC-1: total 6; hər element subscriberPattern, minAmount, maxAmount, fee daşıyır
        assertEquals(billers.getItems().size(), billers.getTotal());
        billers.getItems().forEach(b -> {
            assertNotNull(b.getSubscriberPattern(), b.getId() + ".subscriberPattern");
            assertNotNull(b.getMinAmount(), b.getId() + ".minAmount");
            assertNotNull(b.getMaxAmount(), b.getId() + ".maxAmount");
            assertNotNull(b.getFee(), b.getId() + ".fee");
        });
    }

    @Test(description = "66. category=MOBILE filtri")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-2")
    public void scenario66_categoryMobile_filtersCorrectly() {
        String token = loginAsUser1();

        authed(token)
                .queryParam("category", "MOBILE")
                .when().get(Endpoints.BILLERS)
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath(Schemas.BILLERS_LIST))
                .body("total", equalTo(2))
                .body("items.id", containsInAnyOrder("azercell", "bakcell"))
                .body("items.category", everyItem(equalTo("MOBILE")));
    }

    @Test(description = "67. category=INTERNET filtri")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-2")
    public void scenario67_categoryInternet_filtersCorrectly() {
        String token = loginAsUser1();

        authed(token)
                .queryParam("category", "INTERNET")
                .when().get(Endpoints.BILLERS)
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath(Schemas.BILLERS_LIST))
                .body("total", equalTo(2))
                .body("items.id", containsInAnyOrder("citynet", "aztelekom"))
                .body("items.category", everyItem(equalTo("INTERNET")));
    }

    @Test(description = "68. Yanlış category dəyəri -> 400 VALIDATION_ERROR")
    @Severity(SeverityLevel.MINOR)
    @Story("AC-3")
    public void scenario68_invalidCategoryValue_returns400() {
        String token = loginAsUser1();

        authed(token)
                .queryParam("category", "GAS")
                .when().get(Endpoints.BILLERS)
                .then().spec(validationError("category"));
    }

    @Test(description = "69. Token yoxdur/etibarsızdır -> 401")
    @Severity(SeverityLevel.MINOR)
    @Story("Security")
    public void scenario69_noAuthHeader_returns401() {
        given(spec)
                .when().get(Endpoints.BILLERS)
                .then().spec(unauthorized());
    }
}
