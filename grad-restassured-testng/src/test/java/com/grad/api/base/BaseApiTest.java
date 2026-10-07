package com.grad.api.base;

import com.grad.api.config.Config;
import com.grad.api.util.Endpoints;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;

import static io.restassured.RestAssured.given;

/**
 * Bütün test siniflərinin əsası.
 * Hər login sandbox-da TƏCRİD OLUNMUŞ yeni data yaradır (TestPlan.md bölmə 3 və 5) —
 * buna görə hər test metodu öz login/data-sını özü yaradır, heç bir state paylaşılmır.
 */
public abstract class BaseApiTest {

    protected RequestSpecification spec;

    @BeforeClass(alwaysRun = true)
    public void setUpBaseSpec() {
        RestAssured.baseURI = Config.baseUrl();
        spec = new RequestSpecBuilder()
                .setBaseUri(Config.baseUrl())
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .build();
    }

    /** POST /auth/login edir və token-i qaytarır. Hər çağırış yeni, təcrid olunmuş sandbox data yaradır. */
    protected String loginAndGetToken(String email, String password) {
        Response resp = given(spec)
                .body("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
                .when().post(Endpoints.LOGIN)
                .then().statusCode(200)
                .extract().response();
        return resp.jsonPath().getString("token");
    }

    protected String loginAsUser1() {
        return loginAndGetToken(Config.user1Email(), Config.user1Password());
    }

    protected String loginAsUser2() {
        return loginAndGetToken(Config.user2Email(), Config.user2Password());
    }

    /** POST /cards edir (minimal body: accountId + initialAmount) və yaranan kartın id-sini qaytarır. */
    protected String createCard(String token, String accountId, Object initialAmount) {
        return given(spec)
                .header("Authorization", "Bearer " + token)
                .body("{\"accountId\":\"" + accountId + "\",\"initialAmount\":" + initialAmount + "}")
                .when().post(Endpoints.CARDS)
                .then().statusCode(201)
                .extract().jsonPath().getString("id");
    }

    /** POST /beneficiaries edir və yaranan alıcının id-sini qaytarır. */
    protected String createBeneficiary(String token, String bodyJson) {
        return given(spec)
                .header("Authorization", "Bearer " + token)
                .body(bodyJson)
                .when().post(Endpoints.BENEFICIARIES)
                .then().statusCode(201)
                .extract().jsonPath().getString("id");
    }

    protected RequestSpecification authed(String token) {
        return given(spec).header("Authorization", "Bearer " + token);
    }
}
