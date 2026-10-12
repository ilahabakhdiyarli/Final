package com.grad.api.auth;

import com.grad.api.base.BaseApiTest;
import com.grad.api.config.Config;
import com.grad.api.util.Endpoints;
import com.grad.api.util.Schemas;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertNotNull;

/**
 * Rate limit (GAP-01) — "600 sorğu / 10 saniyə (token/sessiya üzrə); aşıldıqda 429, "code": "RATE_LIMITED"".
 *
 * Bu test default suite-ə (testng.xml) daxil DEYİL, çünki qısa müddətdə ~650 sorğu göndərir.
 * Ayrıca işə salınır:  mvn test -DsuiteXml=testng-ratelimit.xml
 * Sorğular Allure filtri olmadan göndərilir (yüzlərlə attachment yaranmasın); yalnız 429 cavabı yoxlanılır.
 */
@Epic("Ümumi qaydalar")
@Feature("Rate limit")
public class RateLimitTest extends BaseApiTest {

    private static final int LIMIT = 600;
    private static final int TOTAL_REQUESTS = LIMIT + 50;
    private static final int THREADS = 25;

    @Test(groups = "rate-limit",
            description = "601+ sorğu / 10 saniyə eyni token ilə -> 429 RATE_LIMITED")
    @Severity(SeverityLevel.NORMAL)
    @Story("Qayda-3")
    public void exceedingRateLimit_returns429RateLimited() throws Exception {
        String token = loginAsUser1();

        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        List<Future<Response>> futures = new ArrayList<>();
        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            futures.add(pool.submit(() -> given()
                    .baseUri(Config.baseUrl())
                    .header("Authorization", "Bearer " + token)
                    .when().get(Endpoints.BILLERS)
                    .andReturn()));
        }

        Response limited = null;
        for (Future<Response> f : futures) {
            Response r = f.get();
            if (r.statusCode() == 429 && limited == null) {
                limited = r;
            }
        }
        pool.shutdown();

        assertNotNull(limited, TOTAL_REQUESTS + " sorğudan heç biri 429 qaytarmadı");
        assertThat(limited.jsonPath().getString("code"), equalTo("RATE_LIMITED"));
        limited.then().body(matchesJsonSchemaInClasspath(Schemas.ERROR));
    }
}
