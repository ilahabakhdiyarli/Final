package com.grad.api.base;

import com.grad.api.config.Config;
import com.grad.api.models.request.BeneficiaryRequest;
import com.grad.api.models.request.BlockCardRequest;
import com.grad.api.models.request.CardRequest;
import com.grad.api.models.request.LoginRequest;
import com.grad.api.models.response.Card;
import com.grad.api.models.response.LoginResponse;
import com.grad.api.util.Endpoints;
import com.grad.api.util.Schemas;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

/**
 * Bütün test siniflərinin əsası: request/response spesifikasiyaları, login və data helper-ləri.
 *
 * Token lifecycle:
 *  - Token heç vaxt hardcode edilmir — həmişə POST /auth/login ilə runtime-da alınır.
 *  - Hər login sandbox-da TƏCRİD OLUNMUŞ yeni data yaradır (TestPlan.md bölmə 3 və 5), ona görə
 *    hər test metodu öz token-ini özü alır; token testlər arasında paylaşılmır və testlər bir-birindən
 *    asılı deyil (istənilən ardıcıllıqla və ya tək-tək işə salına bilər).
 *  - Bir test daxilində eyni token bütün sorğularda təkrar istifadə olunur.
 *  - Token-in davranışı (təkrar istifadə, sandbox izolyasiyası, saxta/dəyişdirilmiş token → 401)
 *    ayrıca com.grad.api.auth.AuthTokenTest-də yoxlanılır.
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

    // ==================== Auth ====================

    /** POST /auth/login edir, cavabı schema ilə yoxlayır və POJO kimi qaytarır. */
    protected LoginResponse login(String email, String password) {
        return given(spec)
                .body(new LoginRequest(email, password))
                .when().post(Endpoints.LOGIN)
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath(Schemas.LOGIN))
                .extract().as(LoginResponse.class);
    }

    /** POST /auth/login edir və token-i qaytarır. Hər çağırış yeni, təcrid olunmuş sandbox data yaradır. */
    protected String loginAndGetToken(String email, String password) {
        return login(email, password).getToken();
    }

    protected String loginAsUser1() {
        return loginAndGetToken(Config.user1Email(), Config.user1Password());
    }

    protected String loginAsUser2() {
        return loginAndGetToken(Config.user2Email(), Config.user2Password());
    }

    protected RequestSpecification authed(String token) {
        return given(spec).header("Authorization", "Bearer " + token);
    }

    // ==================== Data helper-ləri ====================

    /** POST /cards edir (accountId + initialAmount) və yaranan kartı POJO kimi qaytarır. */
    protected Card createCardEntity(String token, String accountId, Number initialAmount) {
        return authed(token)
                .body(new CardRequest(accountId, new BigDecimal(String.valueOf(initialAmount))))
                .when().post(Endpoints.CARDS)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.CARD))
                .extract().as(Card.class);
    }

    /** POST /cards edir və yaranan kartın id-sini qaytarır. */
    protected String createCard(String token, String accountId, Number initialAmount) {
        return createCardEntity(token, accountId, initialAmount).getId();
    }

    /** POST /cards/{id}/block edir (reason: TEMPORARY → FROZEN, LOST/STOLEN → BLOCKED). */
    protected void blockCard(String token, String cardId, String reason) {
        authed(token)
                .body(new BlockCardRequest(reason))
                .when().post(Endpoints.cardBlock(cardId))
                .then().statusCode(200);
    }

    /** POST /beneficiaries edir və yaranan alıcının id-sini qaytarır. */
    protected String createBeneficiary(String token, BeneficiaryRequest request) {
        return authed(token)
                .body(request)
                .when().post(Endpoints.BENEFICIARIES)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.BENEFICIARY))
                .extract().jsonPath().getString("id");
    }

    // ==================== Response spesifikasiyaları ====================

    /**
     * Biznes xətası: status + schemas/error.json + code. Layihə qaydasına uyğun olaraq assert
     * mesaja (error) yox, koda (code) yazılır.
     */
    protected static ResponseSpecification error(int status, String code) {
        return new ResponseSpecBuilder()
                .expectStatusCode(status)
                .expectBody(matchesJsonSchemaInClasspath(Schemas.ERROR))
                .expectBody("code", equalTo(code))
                .build();
    }

    /** 400 VALIDATION_ERROR: schemas/validation-error.json + details[].field səhv sahəni ehtiva edir. */
    protected static ResponseSpecification validationError(String field) {
        return new ResponseSpecBuilder()
                .expectStatusCode(400)
                .expectBody(matchesJsonSchemaInClasspath(Schemas.VALIDATION_ERROR))
                .expectBody("code", equalTo("VALIDATION_ERROR"))
                .expectBody("details.field", hasItem(field))
                .build();
    }

    /** 401: token yoxdur, saxtadır və ya müddəti bitib (cavab yalnız { "error" } daşıyır). */
    protected static ResponseSpecification unauthorized() {
        return new ResponseSpecBuilder()
                .expectStatusCode(401)
                .expectBody(matchesJsonSchemaInClasspath(Schemas.UNAUTHORIZED))
                .build();
    }
}
