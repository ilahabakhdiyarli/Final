package com.grad.api.epic1;

import com.grad.api.base.BaseApiTest;
import com.grad.api.util.Endpoints;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * GRAD-102 — GET /beneficiaries (Alıcılar siyahısı — axtarış, filtr, sıralama).
 * Mənbə: GRAD-AC-Reference.md (AC-1..AC-7) + specs/api-testing/gradListBeneficiaries.spec (ssenari #1-11).
 */
@Epic("EPIC-1: Saxlanmış alıcılar")
@Feature("GRAD-102: Alıcıları siyahılama/axtarış")
public class BeneficiaryListTest extends BaseApiTest {

    private String body(String fullName, String iban, String nickname, String currency) {
        return "{\"fullName\":\"" + fullName + "\",\"iban\":\"" + iban + "\",\"nickname\":\"" + nickname + "\",\"currency\":\"" + currency + "\"}";
    }

    @Test(description = "AC-1: Parametrsiz sorğu -> 200; total = items.length; ən yeni əlavə edilən birinci gəlir")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-1")
    public void ac1_noParams_defaultSortNewestFirst() {
        String token = loginAsUser1();
        createBeneficiary(token, body("Tahir Aliyev", "AZ41NABZ00000000137010009441", "Birinci", "AZN"));
        createBeneficiary(token, body("Samir Nasirov", "AZ42NABZ00000000137010009442", "Ikinci", "AZN"));

        given(spec).header("Authorization", "Bearer " + token)
                .when().get(Endpoints.BENEFICIARIES)
                .then().statusCode(200)
                .body("total", equalTo(2))
                .body("items.size()", equalTo(2))
                .body("items[0].nickname", equalTo("Ikinci"));
    }

    @Test(description = "AC-2: sort=fullName&order=asc -> adlar əlifba sırası ilə düzülür")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-2")
    public void ac2_sortByFullNameAsc() {
        String token = loginAsUser1();
        createBeneficiary(token, body("Cavid Aliyev", "AZ51NABZ00000000137010009551", "SortTest1", "AZN"));
        createBeneficiary(token, body("Araz Huseynov", "AZ52NABZ00000000137010009552", "SortTest2", "AZN"));
        createBeneficiary(token, body("Baba Mammadov", "AZ53NABZ00000000137010009553", "SortTest3", "AZN"));

        given(spec).header("Authorization", "Bearer " + token)
                .queryParam("sort", "fullName")
                .queryParam("order", "asc")
                .when().get(Endpoints.BENEFICIARIES)
                .then().statusCode(200)
                .body("items.fullName", contains("Araz Huseynov", "Baba Mammadov", "Cavid Aliyev"));
    }

    @Test(description = "AC-3: currency=USD -> yalnız USD alıcılar, total uyğun")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-3")
    public void ac3_currencyFilter_usdOnly() {
        String token = loginAsUser1();
        createBeneficiary(token, body("Kamran Huseynov", "AZ11NABZ00000000137010009111", "USDTest1", "USD"));
        createBeneficiary(token, body("Elvin Qasimov", "AZ22NABZ00000000137010009222", "AZNTest1", "AZN"));

        given(spec).header("Authorization", "Bearer " + token)
                .queryParam("currency", "USD")
                .when().get(Endpoints.BENEFICIARIES)
                .then().statusCode(200)
                .body("total", equalTo(1))
                .body("items.size()", equalTo(1))
                .body("items.currency", everyItem(equalTo("USD")));
    }

    @Test(description = "AC-4: Case-folding axtarış - q=NİGAR və q=NIGAR hər ikisi 'Nigar Əliyeva'nı tapır")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-4")
    public void ac4_caseFoldingSearch_turkishIProblem() {
        String token = loginAsUser1();
        createBeneficiary(token, body("Nigar Əliyeva", "AZ61NABZ00000000137010009661", "NigarTest", "AZN"));

        given(spec).header("Authorization", "Bearer " + token)
                .queryParam("q", "NİGAR")
                .when().get(Endpoints.BENEFICIARIES)
                .then().statusCode(200)
                .body("items.size()", greaterThanOrEqualTo(1))
                .body("items[0].fullName", containsString("Nigar"));

        given(spec).header("Authorization", "Bearer " + token)
                .queryParam("q", "NIGAR")
                .when().get(Endpoints.BENEFICIARIES)
                .then().statusCode(200)
                .body("items.size()", greaterThanOrEqualTo(1))
                .body("items[0].fullName", containsString("Nigar"));
    }

    @Test(description = "AC-5: q ləqəbin bir hissəsidir -> ləqəbə görə də tapılır")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-5")
    public void ac5_nicknamePartialSearch() {
        String token = loginAsUser1();
        createBeneficiary(token, body("Anar Mammadov", "AZ33NABZ00000000137010009333", "Anam", "AZN"));

        given(spec).header("Authorization", "Bearer " + token)
                .queryParam("q", "Ana")
                .when().get(Endpoints.BENEFICIARIES)
                .then().statusCode(200)
                .body("items.size()", greaterThanOrEqualTo(1))
                .body("items[0].nickname", containsString("Ana"));
    }

    @Test(description = "AC-6: q=a (1 simvol) -> 400 VALIDATION_ERROR, details[0].field=q")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-6")
    public void ac6_qTooShort_returns400ValidationError() {
        String token = loginAsUser1();

        given(spec).header("Authorization", "Bearer " + token)
                .queryParam("q", "a")
                .when().get(Endpoints.BENEFICIARIES)
                .then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("details[0].field", equalTo("q"));
    }

    @Test(description = "AC-7: Heç nə tapılmır -> 200, total:0, items:[]")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-7")
    public void ac7_noResults_returns200EmptyList() {
        String token = loginAsUser1();

        given(spec).header("Authorization", "Bearer " + token)
                .queryParam("q", "Zzzznonexistent")
                .when().get(Endpoints.BENEFICIARIES)
                .then().statusCode(200)
                .body("total", equalTo(0))
                .body("items.size()", equalTo(0));
    }

    @Test(description = "Əlavə: Token olmadan GET -> 401")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Security")
    public void extra_noToken_returns401() {
        given(spec)
                .when().get(Endpoints.BENEFICIARIES)
                .then().statusCode(401);
    }

    @Test(description = "Əlavə: Etibarsız sort deyeri -> 400 VALIDATION_ERROR")
    @Severity(SeverityLevel.NORMAL)
    @Story("Validation")
    public void extra_invalidSortValue_returns400() {
        String token = loginAsUser1();

        given(spec).header("Authorization", "Bearer " + token)
                .queryParam("sort", "invalidfield")
                .when().get(Endpoints.BENEFICIARIES)
                .then().statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"));
    }

    @Test(description = "Əlavə (security): SQL-injection payload q parametrində -> 200 ilə 400 arasında")
    @Severity(SeverityLevel.NORMAL)
    @Story("Security")
    public void extra_sqlInjectionInQ_returns200to400() {
        String token = loginAsUser1();

        given(spec).header("Authorization", "Bearer " + token)
                .queryParam("q", "' OR 1=1--")
                .when().get(Endpoints.BENEFICIARIES)
                .then().statusCode(allOf(greaterThanOrEqualTo(200), lessThanOrEqualTo(400)));
    }

    @Test(description = "Əlavə: Kombinə edilmiş filtrlər - q+currency+sort+order (AND məntiqi)")
    @Severity(SeverityLevel.MINOR)
    @Story("Extra")
    public void extra_combinedFilters_qCurrencySortOrder() {
        String token = loginAsUser1();
        createBeneficiary(token, body("Nigar Mammadova", "AZ71NABZ00000000137010009771", "ComboMatch", "AZN"));
        createBeneficiary(token, body("Nigar Huseynova", "AZ72NABZ00000000137010009772", "ComboNoMatch", "USD"));

        given(spec).header("Authorization", "Bearer " + token)
                .queryParam("q", "nig")
                .queryParam("currency", "AZN")
                .queryParam("sort", "fullName")
                .queryParam("order", "asc")
                .when().get(Endpoints.BENEFICIARIES)
                .then().statusCode(200)
                .body("total", equalTo(1))
                .body("items[0].fullName", containsString("Nigar"))
                .body("items[0].currency", equalTo("AZN"));
    }
}
