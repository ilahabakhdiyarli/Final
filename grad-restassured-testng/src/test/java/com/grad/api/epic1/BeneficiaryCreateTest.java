package com.grad.api.epic1;

import com.grad.api.base.BaseApiTest;
import com.grad.api.models.request.BeneficiaryRequest;
import com.grad.api.models.response.Beneficiary;
import com.grad.api.models.response.ErrorResponse;
import com.grad.api.util.Endpoints;
import com.grad.api.util.Schemas;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * GRAD-101 — POST /beneficiaries (Saxlanmış alıcı yaratmaq).
 * Mənbə: GRAD-AC-Reference.md (AC-1..AC-10) + specs/api-testing/gradBeneficiaryTesting.spec (ssenari #1-22).
 */
@Epic("EPIC-1: Saxlanmış alıcılar")
@Feature("GRAD-101: Alıcı əlavə etmə")
public class BeneficiaryCreateTest extends BaseApiTest {

    private static final String VALID_IBAN = "AZ77AIIB40060019440123456789";

    private BeneficiaryRequest body(String fullName, String iban, String currency, String nickname) {
        return new BeneficiaryRequest(fullName, iban, currency, nickname);
    }

    @Test(description = "AC-1: Etibarlı sorğu -> 201; id/iban/ibanMasked/currency/nickname/createdAt + Location header")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-1")
    public void ac1_validRequest_returns201WithAllFields() {
        String token = loginAsUser1();
        Response response = authed(token)
                .body(body("Nigar Əliyeva", VALID_IBAN, "AZN", "Anam"))
                .when().post(Endpoints.BENEFICIARIES)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.BENEFICIARY))
                .extract().response();

        Beneficiary beneficiary = response.as(Beneficiary.class);
        assertTrue(beneficiary.getId().matches("^ben_[a-z0-9]{8}$"), "id formatı: ben_ + 8 simvol");
        assertEquals(beneficiary.getIban(), VALID_IBAN);
        assertTrue(beneficiary.getIbanMasked().matches("^AZ[0-9]{2} [*]{4} [*]{4} [*]{4} [*]{4} [0-9]{4}$"),
                "ibanMasked formatı");
        assertEquals(beneficiary.getCurrency(), "AZN");
        assertEquals(beneficiary.getNickname(), "Anam");
        assertNotNull(beneficiary.getCreatedAt());
        assertEquals(response.header("Location"), "/beneficiaries/" + beneficiary.getId(),
                "Location header /beneficiaries/{id} göstərməlidir");
    }

    @Test(description = "AC-2: IBAN normallaşdırma (boşluq + kiçik hərf -> təmiz + böyük hərf)")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-2")
    public void ac2_ibanNormalization_spacesAndLowercase() {
        String token = loginAsUser1();
        authed(token)
                .body(body("Elvin Məmmədov", "az77 aiib 4006 0019 4401 2345 6789", "AZN", "Dost"))
                .when().post(Endpoints.BENEFICIARIES)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.BENEFICIARY))
                .body("iban", equalTo(VALID_IBAN));
    }

    @Test(description = "AC-3: nickname göndərilmədikdə null qaytarılır")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-3")
    public void ac3_nicknameNotSent_returnsNull() {
        String token = loginAsUser1();
        Beneficiary beneficiary = authed(token)
                .body(body("Kamran Hüseynov", VALID_IBAN, "AZN", null))
                .when().post(Endpoints.BENEFICIARIES)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.BENEFICIARY))
                .body("$", hasKey("nickname"))
                .extract().as(Beneficiary.class);
        assertNull(beneficiary.getNickname(), "nickname göndərilmədikdə cavabda null olmalıdır");
    }

    @Test(description = "AC-4: Bir neçə sahə eyni anda səhv -> 400 VALIDATION_ERROR, details[]-də hər səhv sahə")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-4")
    public void ac4_multipleInvalidFields_returns400WithTwoDetails() {
        String token = loginAsUser1();
        ErrorResponse error = authed(token)
                .body(body("A", "123", "AZN", null))
                .when().post(Endpoints.BENEFICIARIES)
                .then().spec(validationError("fullName"))
                .body("details.size()", equalTo(2))
                .extract().as(ErrorResponse.class);

        assertTrue(error.getDetailFields().containsAll(List.of("fullName", "iban")),
                "details həm fullName, həm iban üçün { field, message } daşımalıdır: " + error.getDetailFields());
        error.getDetails().forEach(d -> assertNotNull(d.getMessage(), "details[].message boş olmamalıdır"));
    }

    @DataProvider(name = "fullNameLengthBoundary")
    public Object[][] fullNameLengthBoundary() {
        return new Object[][]{
                {"V", 400},                                                   // 1 simvol -> rədd
                {"Və", 201},                                                   // 2 simvol -> qəbul
                {"VəliQızıAnarHəsənovaYusifTağıyevVəliQızıAnarHəsəno", 201},  // 50 simvol -> qəbul
                {"VəliQızıAnarHəsənovaYusifTağıyevVəliQızıAnarHəsənov", 400}, // 51 simvol -> rədd
        };
    }

    @Test(dataProvider = "fullNameLengthBoundary",
            description = "AC-5: fullName uzunluq sərhədi — 1/51 rədd, 2/50 qəbul (BVA)")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-5")
    public void ac5_fullNameLengthBoundary(String fullName, int expectedStatus) {
        String token = loginAsUser1();
        var then = authed(token)
                .body(body(fullName, VALID_IBAN, "AZN", null))
                .when().post(Endpoints.BENEFICIARIES)
                .then();
        if (expectedStatus == 400) {
            then.spec(validationError("fullName"));
        } else {
            then.statusCode(201).body(matchesJsonSchemaInClasspath(Schemas.BENEFICIARY))
                    .body("fullName", equalTo(fullName));
        }
    }

    @DataProvider(name = "fullNameFormat")
    public Object[][] fullNameFormat() {
        return new Object[][]{
                {"---"},   // hərflə başlamır
                {"Ali2"},  // rəqəm var
        };
    }

    @Test(dataProvider = "fullNameFormat",
            description = "AC-6: fullName format — hərflə başlamır / rəqəm var -> 400 VALIDATION_ERROR")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-6")
    public void ac6_fullNameInvalidFormat_returns400(String fullName) {
        String token = loginAsUser1();
        authed(token)
                .body(body(fullName, VALID_IBAN, "AZN", null))
                .when().post(Endpoints.BENEFICIARIES)
                .then().spec(validationError("fullName"));
    }

    @Test(description = "AC-7: Eyni IBAN təkrar -> 409 BENEFICIARY_EXISTS")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-7")
    public void ac7_duplicateIban_returns409() {
        String token = loginAsUser1();
        createBeneficiary(token, body("Rəşad Quliyev", VALID_IBAN, "AZN", null));

        authed(token)
                .body(body("Başqa Ad", VALID_IBAN, "AZN", null))
                .when().post(Endpoints.BENEFICIARIES)
                .then().spec(error(409, "BENEFICIARY_EXISTS"));
    }

    @Test(description = "AC-8: nickname unikallığı (case-insensitive) -> 409 NICKNAME_TAKEN")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-8")
    public void ac8_nicknameUniqueness_caseInsensitive_returns409() {
        String token = loginAsUser1();
        createBeneficiary(token, body("Anar Vəliyev", VALID_IBAN, "AZN", "anam"));

        authed(token)
                .body(body("Fərid Əhmədov", "AZ77AIIB40060019440123456700", "AZN", "ANAM"))
                .when().post(Endpoints.BENEFICIARIES)
                .then().spec(error(409, "NICKNAME_TAKEN"));
    }

    @Test(description = "AC-9: Öz hesabının IBAN-ı -> 422 OWN_ACCOUNT")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-9")
    public void ac9_ownAccountIban_returns422() {
        String token = loginAsUser1();
        authed(token)
                .body(body("Özüm", "AZ29NABZ00000000137010002855", "AZN", null))
                .when().post(Endpoints.BENEFICIARIES)
                .then().spec(error(422, "OWN_ACCOUNT"));
    }

    @Test(description = "AC-10: 11-ci alıcı -> 422 LIMIT_REACHED, limit:10")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-10")
    public void ac10_eleventhBeneficiary_returns422LimitReached() {
        String token = loginAsUser1();
        // DÜZƏLİŞ: Əvvəlcə fullName = "Alıcı " + i idi (yəni "Alıcı 0", "Alıcı 1" və s. — rəqəm daxil
        // edirdi). AC-6 artıq sübut edir ki, API fullName-də rəqəm olduqda 400 VALIDATION_ERROR
        // qaytarır ("Ali2" nümunəsi). Ona görə loop-un BİRİNCİ iterasiyası (i=0, "Alıcı 0") elə ilk
        // addımda 400 alırdı (201 gözlənilirdi) — API bug deyil, test kodu bug-ı.
        // Hərflə fərqləndirmə (A, B, C, ...) rəqəm problemi olmadan 10 unikal IBAN yaratmağa imkan verir.
        for (int i = 0; i < 10; i++) {
            String iban = "AZ77AIIB4006001944012345670" + i;
            char suffix = (char) ('A' + i);
            createBeneficiary(token, body("Alıcı " + suffix, iban, "AZN", null));
        }
        ErrorResponse error = authed(token)
                .body(body("On Birinci", "AZ77AIIB40060019440123456711", "AZN", null))
                .when().post(Endpoints.BENEFICIARIES)
                .then().spec(error(422, "LIMIT_REACHED"))
                .extract().as(ErrorResponse.class);
        assertEquals(((Number) error.get("limit")).intValue(), 10, "AC-10: \"limit\": 10");
    }

    @Test(description = "Əlavə: Token olmadan POST -> 401")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Security")
    public void extra_noToken_returns401() {
        given(spec)
                .body(body("Token Yox", VALID_IBAN, "AZN", null))
                .when().post(Endpoints.BENEFICIARIES)
                .then().spec(unauthorized());
    }

    @Test(description = "Əlavə: Saxta token -> 401")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Security")
    public void extra_fakeToken_returns401() {
        authed("saxta.token.deyeri")
                .body(body("Saxta Token", VALID_IBAN, "AZN", null))
                .when().post(Endpoints.BENEFICIARIES)
                .then().spec(unauthorized());
    }

    @Test(description = "Əlavə: Body etibarlı JSON deyil -> 400 INVALID_JSON")
    @Severity(SeverityLevel.NORMAL)
    @Story("Validation")
    public void extra_invalidJsonBody_returns400InvalidJson() {
        String token = loginAsUser1();
        // Bilərəkdən xam (səhv) JSON göndərilir — POJO ilə bu ssenarini qurmaq mümkün deyil.
        authed(token)
                .body("{fullName: Ali")
                .when().post(Endpoints.BENEFICIARIES)
                .then().spec(error(400, "INVALID_JSON"));
    }

    @Test(description = "Əlavə (security): SQL-injection payload — fullName -> 400 VALIDATION_ERROR")
    @Severity(SeverityLevel.NORMAL)
    @Story("Security")
    public void extra_sqlInjectionInFullName_returns400() {
        String token = loginAsUser1();
        authed(token)
                .body(body("Ali' OR '1'='1", VALID_IBAN, "AZN", null))
                .when().post(Endpoints.BENEFICIARIES)
                .then().spec(validationError("fullName"));
    }

    @Test(description = "Əlavə (security): XSS payload — nickname -> 400 VALIDATION_ERROR")
    @Severity(SeverityLevel.NORMAL)
    @Story("Security")
    public void extra_xssInNickname_returns400() {
        String token = loginAsUser1();
        authed(token)
                .body(body("Elvin Elvinov", VALID_IBAN, "AZN", "<script>alert(1)</script>"))
                .when().post(Endpoints.BENEFICIARIES)
                .then().spec(validationError("nickname"));
    }

    @Test(description = "Əlavə (BVA): Azərbaycan hərfləri + defis/apostrof qəbulu -> 201")
    @Severity(SeverityLevel.MINOR)
    @Story("BVA")
    public void extra_azerbaijaniLettersHyphenApostrophe_accepted() {
        String token = loginAsUser1();
        authed(token)
                .body(body("Vəli-Qızı O'Brien", VALID_IBAN, "AZN", null))
                .when().post(Endpoints.BENEFICIARIES)
                .then().statusCode(201)
                .body(matchesJsonSchemaInClasspath(Schemas.BENEFICIARY));
    }

    @DataProvider(name = "ibanLengthBoundary")
    public Object[][] ibanLengthBoundary() {
        return new Object[][]{
                {"AZ77AIIB4006001944012345678"},    // 27 simvol
                {"AZ77AIIB400600194401234567899"},  // 29 simvol
        };
    }

    @Test(dataProvider = "ibanLengthBoundary",
            description = "Əlavə (BVA): IBAN uzunluq sərhədi 27/29 simvol -> 400 VALIDATION_ERROR, field=iban")
    @Severity(SeverityLevel.MINOR)
    @Story("BVA")
    public void extra_ibanLengthBoundary_returns400(String iban) {
        String token = loginAsUser1();
        authed(token)
                .body(body("Yanlış Uzunluq", iban, "AZN", null))
                .when().post(Endpoints.BENEFICIARIES)
                .then().spec(validationError("iban"))
                .body("details[0].field", equalTo("iban"));
    }
}
