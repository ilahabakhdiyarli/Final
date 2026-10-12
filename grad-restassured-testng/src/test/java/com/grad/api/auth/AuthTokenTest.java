package com.grad.api.auth;

import com.grad.api.base.BaseApiTest;
import com.grad.api.config.Config;
import com.grad.api.models.request.BeneficiaryRequest;
import com.grad.api.models.response.BeneficiaryList;
import com.grad.api.models.response.LoginResponse;
import com.grad.api.util.Endpoints;
import com.grad.api.util.Schemas;
import io.qameta.allure.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

/**
 * Token lifecycle (GAP-02) — POST /auth/login və Bearer token davranışı.
 * Mənbə (layihə səhifəsi / GRAD-AC-Reference.md "Ümumi texniki qaydalar"):
 *  - Token POST /auth/login ilə alınır və "Authorization: Bearer {token}" ilə göndərilir.
 *  - "Token yoxdur, saxtadır və ya müddəti bitib → 401".
 *  - Sandbox data token üzrə təcrid olunur; token 24 saat etibarlıdır.
 *
 * 24 saatlıq müddətin bitməsi avtomatlaşdırılmır (testin 24 saat gözləməsi lazımdır) —
 * TestPlan.md-də manual yoxlama kimi qeyd olunub.
 */
@Epic("Ümumi qaydalar")
@Feature("Auth: token lifecycle")
public class AuthTokenTest extends BaseApiTest {

    private static final String IBAN = "AZ77AIIB40060019440123456789";

    @DataProvider(name = "users")
    public Object[][] users() {
        return new Object[][]{
                {Config.user1Email(), Config.user1Password()},
                {Config.user2Email(), Config.user2Password()},
        };
    }

    @Test(dataProvider = "users",
            description = "Login: etibarlı məlumatlar -> 200; token + user (schema); user.email sorğudakı ilə eyni")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Token alınması")
    public void login_validCredentials_returnsTokenAndUser(String email, String password) {
        LoginResponse response = login(email, password);   // status 200 + schemas/login-response.json
        assertFalse(response.getToken().isBlank(), "token boş olmamalıdır");
        assertEquals(response.getUser().getEmail(), email);
    }

    @Test(description = "Eyni token bir neçə müxtəlif endpoint-də təkrar istifadə olunur (hər sorğuda yeni login lazım deyil)")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Token təkrar istifadəsi")
    public void sameToken_reusableAcrossMultipleRequests() {
        String token = loginAsUser1();

        String cardId = createCard(token, "acc_002", 100);
        authed(token).when().get(Endpoints.cardById(cardId))
                .then().statusCode(200).body(matchesJsonSchemaInClasspath(Schemas.CARD));
        authed(token).when().get(Endpoints.BILLERS)
                .then().statusCode(200).body(matchesJsonSchemaInClasspath(Schemas.BILLERS_LIST));
        authed(token).when().get(Endpoints.BENEFICIARIES)
                .then().statusCode(200).body(matchesJsonSchemaInClasspath(Schemas.BENEFICIARY_LIST));
        authed(token).when().get(Endpoints.BILL_PAYMENTS)
                .then().statusCode(200).body(matchesJsonSchemaInClasspath(Schemas.BILL_PAYMENT_LIST));
    }

    @Test(description = "Yeni login yeni token və təcrid olunmuş sandbox verir: token A-da yaradılan alıcı token B-də görünmür")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Sandbox izolyasiyası")
    public void newLogin_returnsNewTokenWithIsolatedSandbox() {
        String tokenA = loginAsUser1();
        String beneficiaryId = createBeneficiary(tokenA,
                new BeneficiaryRequest("Izolyasiya Testi", IBAN, "AZN", null));

        String tokenB = loginAsUser1();
        assertNotEquals(tokenB, tokenA, "hər login yeni token qaytarmalıdır");

        BeneficiaryList seenByB = authed(tokenB)
                .when().get(Endpoints.BENEFICIARIES)
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath(Schemas.BENEFICIARY_LIST))
                .extract().as(BeneficiaryList.class);
        assertTrue(seenByB.getItems().stream().noneMatch(b -> b.getId().equals(beneficiaryId)),
                "token A-nın sandbox datası token B-də görünməməlidir");
    }

    @Test(description = "İkinci login-dən sonra əvvəlki token etibarlı qalır və öz sandbox datasını görür")
    @Severity(SeverityLevel.NORMAL)
    @Story("Sandbox izolyasiyası")
    public void firstToken_staysValidWithOwnData_afterSecondLogin() {
        String tokenA = loginAsUser1();
        String beneficiaryId = createBeneficiary(tokenA,
                new BeneficiaryRequest("Kohne Token Testi", IBAN, "AZN", null));

        loginAsUser1(); // eyni istifadəçi üçün yeni sessiya

        BeneficiaryList seenByA = authed(tokenA)
                .when().get(Endpoints.BENEFICIARIES)
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath(Schemas.BENEFICIARY_LIST))
                .extract().as(BeneficiaryList.class);
        assertTrue(seenByA.getItems().stream().anyMatch(b -> b.getId().equals(beneficiaryId)),
                "token A 24 saat ərzində etibarlıdır və öz datasını görməlidir");
    }

    @Test(description = "Dəyişdirilmiş (saxta) token -> 401")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Saxta token")
    public void tamperedToken_returns401() {
        String token = loginAsUser1();
        // Token-in son seqmentinin (imzanın) ortasındakı bir simvolu dəyişirik
        int sigStart = token.lastIndexOf('.') + 1;
        int pos = sigStart + (token.length() - sigStart) / 2;
        char replacement = token.charAt(pos) == 'x' ? 'y' : 'x';
        String tampered = token.substring(0, pos) + replacement + token.substring(pos + 1);

        authed(tampered)
                .when().get(Endpoints.BENEFICIARIES)
                .then().spec(unauthorized());
    }
}
