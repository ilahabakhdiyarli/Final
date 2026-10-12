package com.grad.api.epic1;

import com.grad.api.base.BaseApiTest;
import com.grad.api.models.request.BeneficiaryRequest;
import com.grad.api.models.response.BeneficiaryList;
import com.grad.api.util.Endpoints;
import com.grad.api.util.Schemas;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * GRAD-103 — DELETE /beneficiaries/{id} (Alıcını silmək).
 * Mənbə: GRAD-AC-Reference.md (GRAD-103, AC-1..AC-4) + specs/api-testing/gradDeleteBeneficiary.spec (ssenari #1-6).
 */
@Epic("EPIC-1: Saxlanmış alıcılar")
@Feature("GRAD-103: Alıcı silmə")
public class BeneficiaryDeleteTest extends BaseApiTest {

    private BeneficiaryRequest body(String fullName, String iban, String currency, String nickname) {
        return new BeneficiaryRequest(fullName, iban, currency, nickname);
    }

    private BeneficiaryList list(String token) {
        return authed(token)
                .when().get(Endpoints.BENEFICIARIES)
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath(Schemas.BENEFICIARY_LIST))
                .extract().as(BeneficiaryList.class);
    }

    @Test(description = "AC-1: Mövcud alıcı silinir -> 204, body boşdur")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-1")
    public void ac1_deleteExistingBeneficiary_returns204EmptyBody() {
        String token = loginAsUser1();
        String benId = createBeneficiary(token, body("Sil Meni", "AZ81NABZ00000000137010009881", "AZN", "SilTest1"));

        authed(token)
                .when().delete(Endpoints.beneficiaryById(benId))
                .then().statusCode(204)
                .body(emptyString());
    }

    @Test(description = "AC-2: Silinmiş alıcı siyahıda görünmür, total 1 azalıb")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-2")
    public void ac2_deletedBeneficiaryNotInList_totalDecreasesByOne() {
        String token = loginAsUser1();
        String keepId = createBeneficiary(token, body("Qalacaq Alici", "AZ82NABZ00000000137010009882", "AZN", "QalacaqTest"));
        String deleteId = createBeneficiary(token, body("Silinecek Alici", "AZ83NABZ00000000137010009883", "AZN", "SilinecekTest"));
        int totalBefore = list(token).getTotal();

        authed(token)
                .when().delete(Endpoints.beneficiaryById(deleteId))
                .then().statusCode(204);

        BeneficiaryList after = list(token);
        assertEquals(after.getTotal(), totalBefore - 1, "total 1 azalmalıdır");
        assertTrue(after.getItems().stream().noneMatch(b -> b.getId().equals(deleteId)),
                "silinmiş alıcı siyahıda olmamalıdır");
        assertEquals(after.getItems().get(0).getId(), keepId);
    }

    @Test(description = "AC-3: Eyni id ikinci dəfə silinir -> 404 NOT_FOUND")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-3")
    public void ac3_sameIdDeletedTwice_secondReturns404() {
        String token = loginAsUser1();
        String benId = createBeneficiary(token, body("Iki Defe Sil", "AZ84NABZ00000000137010009884", "AZN", "IkiDefeTest"));

        authed(token)
                .when().delete(Endpoints.beneficiaryById(benId))
                .then().statusCode(204);

        authed(token)
                .when().delete(Endpoints.beneficiaryById(benId))
                .then().spec(error(404, "NOT_FOUND"));
    }

    @Test(description = "AC-4: user2 user1-in alıcı id-si ilə silməyə çalışır (IDOR) -> 404 NOT_FOUND (403 yox)")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-4")
    public void ac4_idorAnotherUsersBeneficiary_returns404NotFound() {
        String token1 = loginAsUser1();
        String user1BenId = createBeneficiary(token1, body("Birinci Istifadeci Alicisi", "AZ85NABZ00000000137010009885", "AZN", "User1Test"));

        String token2 = loginAsUser2();

        authed(token2)
                .when().delete(Endpoints.beneficiaryById(user1BenId))
                .then().spec(error(404, "NOT_FOUND"));

        // user1-in alıcısı silinməməlidir
        assertTrue(list(token1).getItems().stream().anyMatch(b -> b.getId().equals(user1BenId)),
                "IDOR cəhdindən sonra user1-in alıcısı yerində qalmalıdır");
    }

    @Test(description = "Əlavə: Token olmadan DELETE sorğusu -> 401")
    @Severity(SeverityLevel.NORMAL)
    @Story("Security")
    public void extra_noToken_returns401() {
        given(spec)
                .when().delete(Endpoints.beneficiaryById("ben_placeholder123"))
                .then().spec(unauthorized());
    }

    @Test(description = "Əlavə: Mövcud olmayan id ilə DELETE -> 404 NOT_FOUND")
    @Severity(SeverityLevel.MINOR)
    @Story("Validation")
    public void extra_nonExistentId_returns404() {
        String token = loginAsUser1();
        authed(token)
                .when().delete(Endpoints.beneficiaryById("xyz_notreal"))
                .then().spec(error(404, "NOT_FOUND"));
    }
}
