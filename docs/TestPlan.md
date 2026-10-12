# Test Plan — api.anarabbas.com (Banking Sandbox API)

**Layihə:** ABB Tech Academy QA — final layihə (API hissəsi) · **Tərtib edən:** Ilaha · **Tarix:** 06.10.2026 (yeniləndi 12.10.2026) · **Son tarix:** 11.10.2026

## 1. Əhatə dairəsi
3 epic, 10 ticket, 37 SP, 69 AC ([GRAD-AC-Reference.md](GRAD-AC-Reference.md)):
EPIC-1 Alıcılar — GRAD-101/102/103 · EPIC-2 Virtual kartlar — GRAD-201/202/203/204 · EPIC-3 Kommunal ödənişlər — GRAD-301/302/303.
Ümumi qaydalar da daxildir: auth/401, sahiblik → 404 (IDOR), xəta formatı `{error, code}`, pul sahələri (max 2 onluq), token müddəti, rate limit.
**Əhatə xaricində:** UI (asos.com) — ayrıca test case-lər `docs/Test-Cases-UI-API.xlsx`; yük/performans testi.

## 2. Yanaşma
- **Manual (Callman):** hər ticket əvvəl Callman-da icra olunur (happy path, neqativ, auth); token/id environment dəyişənləri ilə zəncirlənir.
- **Avtomat (regression):** Gauge `.spec` (verilmiş arxitektura) + ayrıca RestAssured + TestNG + Allure layihəsi (POJO, JSON Schema, `@DataProvider`).
- **Assert qaydası:** status + cavab məzmunu (sahələr, hesablamalar, schema); xətalarda `code` yoxlanılır, mesaj yox.
- **Texnikalar:** ekvivalent siniflər, BVA, decision table (GRAD-302 yoxlama ardıcıllığı), state transition (GRAD-203/204) — [Test-Design.md](Test-Design.md).
- **Prioritet:** pul/limit və status keçidləri (GRAD-302, 201, 203, 204) ən yüksək; kataloq/siyahı (GRAD-102, 301, 303) aşağı.

## 3. Mühit və data
Base URL `https://api.anarabbas.com`; Bearer token (`POST /auth/login`, 24 saat). user1 (acc_002, AZN), user2 (acc_003, USD). Hər login təcrid olunmuş yeni sandbox verir → hər test öz datasını yaradır, testlər bir-birindən asılı deyil.

## 4. Giriş / çıxış meyarları
**Giriş:** sandbox əlçatandır, test istifadəçiləri login olur. **Çıxış:** hər AC ən azı bir test case ilə örtülüb, avtomat suite yaşıldır və ya hər uğursuzluq bug kimi qeyd olunub, Allure hesabatı var.

## 5. Risklər
- Rate limit (600 sorğu / 10 s / token) — default suite-ə salınmayıb (sandbox-u yükləyir); manual curl ilə yoxlanılıb.
- Token 24 saat — manual (TC-API-GEN-02, Passed); sandbox-ın 24 saatda sıfırlanması API ilə müşahidə edilmir (TC-API-GEN-03 Blocked).
- Bakı gecəyarısı sərhədi (`spentToday`, tarix filtri) — Callman-da vaxtı seçilmiş manual icra.

## 6. Nəticələr (11.10.2026)
- **RestAssured:** 7 tam icra (1029 test icrası) — 1026 passed, 1 failed (BUG-01), 2 broken (şəbəkə). Son tam icra: **147/147 passed**.
- **Test case-lər (123):** 121 Passed · 1 Failed (GEN-01) · 1 Blocked (GEN-03). Token 24 saatdan sonra etibarsızdır (GEN-02, 11.10: 401). **69/69 AC** örtülüb, hamısı Passed.
- **Bug-lar:** BUG-01 — Idempotency-Key olmadan arabir 500 (GRAD-302 AC-6, Medium); BUG-03 — rate limit 600 əvəzinə 620–837 sorğudan sonra işə düşür, təkrarlandı 2/2 (Low).
- **Deliverable-lar:** `docs/Test-Cases-UI-API.xlsx`, `traceability/`, `postman/` (Callman export), `bug-report/`, `grad-restassured-testng/` (Allure: `mvn allure:serve`).
