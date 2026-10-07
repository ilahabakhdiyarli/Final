package com.grad.api.epic1;

import com.grad.api.base.BaseApiTest;
import com.grad.api.util.Endpoints;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * GRAD-103 — DELETE /beneficiaries/{id} (Alıcını silmək).
 * Mənbə: GRAD-AC-Reference.md (GRAD-103, AC-1..AC-4) + specs/api-testing/gradDeleteBeneficiary.spec (ssenari #1-6).
 */
@Epic("EPIC-1: Saxlanmış alıcılar")
@Feature("GRAD-103: Alıcı silmə")
public class BeneficiaryDeleteTest extends BaseApiTest {

    private String body(String fullName, String iban, String currency, String nickname) {
        String nick = nickname == null ? "" : ",\"nickname\":\"" + nickname + "\"";
        return "{\"fullName\":\"" + fullName + "\",\"iban\":\"" + iban + "\",\"currency\":\"" + currency + "\"" + nick + "}";
    }

    @Test(description = "AC-1: Mövcud alıcı silinir -> 204, body boşdur")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-1")
    public void ac1_deleteExistingBeneficiary_returns204EmptyBody() {
        String token = loginAsUser1();
        String benId = createBeneficiary(token, body("Sil Meni", "AZ81NABZ00000000137010009881", "AZN", "SilTest1"));

        given(spec).header("Authorization", "Bearer " + token)
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

        given(spec).header("Authorization", "Bearer " + token)
                .when().delete(Endpoints.beneficiaryById(deleteId))
                .then().statusCode(204);

        given(spec).header("Authorization", "Bearer " + token)
                .when().get(Endpoints.BENEFICIARIES)
                .then().statusCode(200)
                .body("total", equalTo(1))
                .body("items.size()", equalTo(1))
                .body("items[0].id", equalTo(keepId));
    }

    @Test(description = "AC-3: Eyni id ikinci dəfə silinir -> 404")
    @Severity(SeverityLevel.NORMAL)
    @Story("AC-3")
    public void ac3_sameIdDeletedTwice_secondReturns404() {
        String token = loginAsUser1();
        String benId = createBeneficiary(token, body("Iki Defe Sil", "AZ84NABZ00000000137010009884", "AZN", "IkiDefeTest"));

        given(spec).header("Authorization", "Bearer " + token)
                .when().delete(Endpoints.beneficiaryById(benId))
                .then().statusCode(204);

        given(spec).header("Authorization", "Bearer " + token)
                .when().delete(Endpoints.beneficiaryById(benId))
                .then().statusCode(404);
    }

    @Test(description = "AC-4: user2 user1-in alıcı id-si ilə silməyə çalışır (IDOR) -> 404 NOT_FOUND (403 yox)")
    @Severity(SeverityLevel.CRITICAL)
    @Story("AC-4")
    public void ac4_idorAnotherUsersBeneficiary_returns404NotFound() {
        String token1 = loginAsUser1();
        String user1BenId = createBeneficiary(token1, body("Birinci Istifadeci Alicisi", "AZ85NABZ00000000137010009885", "AZN", "User1Test"));

        String token2 = loginAsUser2();

        given(spec).header("Authorization", "Bearer " + token2)
                .when().delete(Endpoints.beneficiaryById(user1BenId))
                .then().statusCode(404)
                .body("code", equalTo("NOT_FOUND"));
    }

    @Test(description = "Əlavə: Token olmadan DELETE sorğusu -> 401")
    @Severity(SeverityLevel.NORMAL)
    @Story("Security")
    public void extra_noToken_returns401() {
        given(spec)
                .when().delete(Endpoints.beneficiaryById("ben_placeholder123"))
                .then().statusCode(401);
    }

    @Test(description = "Əlavə: Mövcud olmayan id ilə DELETE -> 404")
    @Severity(SeverityLevel.MINOR)
    @Story("Validation")
    public void extra_nonExistentId_returns404() {
        String token = loginAsUser1();
        given(spec).header("Authorization", "Bearer " + token)
                .when().delete(Endpoints.beneficiaryById("xyz_notreal"))
                .then().statusCode(404);
    }
}
