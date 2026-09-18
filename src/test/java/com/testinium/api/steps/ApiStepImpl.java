package com.testinium.api.steps;

import com.testinium.api.client.BaseApiClient;
import com.testinium.api.support.ApiSession;
import com.testinium.api.support.JwtPayload;
import com.testinium.api.support.SessionKeys;
import com.thoughtworks.gauge.Step;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;


public class ApiStepImpl extends BaseApiClient {

    @Step("API clear session environments")
    public void clearApiSession() {
        ApiSession.clear();
    }

    @Step("API OTP giris istegi gonder <telefon>")
    public void requestOtpLogin(String telefon) {
        String normalized = normalizePhone(telefon);
        ApiSession.put(SessionKeys.LOGIN_PHONE, normalized);
        given()
                .spec(baseSpec())
                .body(Map.of("isOtpLogin", true, "phoneNumber", normalized))
                .post("/INSTANCE/EnduserAuthorizer")
                .then()
                .statusCode(200);
    }

    @Step("API OTP dogrula <otp>")
    public void validateOtp(String otp) {
        String phone = ApiSession.get(SessionKeys.LOGIN_PHONE);
        if (phone == null || phone.isBlank()) {
            throw new IllegalStateException("Önce OTP giriş isteği adımını çalıştırın.");
        }
        validateOtpCall(phone, otp);
    }

    @Step("API <telefon> telefonu icin OTP dogrula <otp>")
    public void validateOtpExplicitPhone(String telefon, String otp) {
        validateOtpCall(normalizePhone(telefon), otp);
    }

    private void validateOtpCall(String phone, String otp) {
        Response response = given()
                .spec(baseSpec())
                .body(Map.of("otp", otp.trim()))
                .post("/CALL/EnduserAuthorizer/validateOtp/" + phone);
        response.then().statusCode(200);

        Boolean signupRequired = response.jsonPath().getBoolean("signupRequired");
        if (Boolean.TRUE.equals(signupRequired)) {
            String msg = response.jsonPath().getString("message");
            String signupToken = response.jsonPath().getString("signupToken");
            ApiSession.put(SessionKeys.SIGNUP_REQUIRED, "true");
            if (signupToken != null && !signupToken.isBlank()) {
                ApiSession.put(SessionKeys.SIGNUP_TOKEN, signupToken);
            }
            System.out.println("Kullanıcı kayıtlı değil, devam ediliyor..." + (msg == null ? "" : " (" + msg + ")"));
            return;
        }

        ApiSession.put(SessionKeys.SIGNUP_REQUIRED, "false");
        String tempToken = response.jsonPath().getString("_token");
        if (tempToken == null || tempToken.isBlank()) {
            throw new AssertionError("validateOtp yanıtında _token yok. Body: " + response.asPrettyString());
        }
        ApiSession.put(SessionKeys.TEMP_TOKEN, tempToken);
    }

    @Step("Temp token ile kimlik dogrulamayi tamamla")
    public void enduserCompleteTokenAuth() {
        String signupRequired = ApiSession.get(SessionKeys.SIGNUP_REQUIRED);
        if ("true".equalsIgnoreCase(signupRequired)) {
            // Kayıtlı değil -> auth yok, senaryo devam eder.
            return;
        }

        String customToken = ApiSession.get(SessionKeys.TEMP_TOKEN);
        if (customToken == null || customToken.isBlank()) {
            throw new IllegalStateException("temp_token yok; önce OTP doğrulama isteğini çalıştırın.");
        }
        Response response = given()
                .spec(baseSpec())
                .body(Map.of("customToken", customToken))
                .post("/TOKEN/auth");
        response.then().statusCode(200);
        String accessToken = response.jsonPath().getString("accessToken");
        if (accessToken == null || accessToken.isBlank()) {
            throw new AssertionError("TOKEN/auth yanıtında accessToken yok. Body: " + response.asPrettyString());
        }
        ApiSession.put(SessionKeys.ACCESS_TOKEN, accessToken);
        ApiSession.put(SessionKeys.USER_ID, JwtPayload.userIdFromAccessToken(accessToken));
        ApiSession.setBearerToken(accessToken);
    }

    @Step("API ile kullanıcı kayıt ol. Email: <email>")
    public void apiRegisterUserWithEmail(String email) {
        String signupRequired = ApiSession.get(SessionKeys.SIGNUP_REQUIRED);
        if (!"true".equalsIgnoreCase(signupRequired)) {
            System.out.println("Kullanıcı zaten kayıtlı");
            return;
        }

        String phone = ApiSession.get(SessionKeys.LOGIN_PHONE);
        if (phone == null || phone.isBlank()) {
            throw new IllegalStateException("loginPhone yok; önce OTP giriş isteği adımını çalıştırın.");
        }

        String signupToken = ApiSession.get(SessionKeys.SIGNUP_TOKEN);
        if (signupToken == null || signupToken.isBlank()) {
            throw new IllegalStateException("signupToken yok; validateOtp response'undan alınamadı.");
        }

        String mail = email == null ? "" : email.trim();
        if (mail.isBlank()) {
            throw new IllegalArgumentException("Email boş olamaz.");
        }

        Map<String, Object> body = Map.ofEntries(
                Map.entry("birthDay", "2000-01-01"),
                Map.entry("email", mail),
                Map.entry("gender", "MALE"),
                Map.entry("iysAgreeClarification", true),
                Map.entry("iysAgreeDataTransfer", true),
                Map.entry("iysAgreeMembership", true),
                Map.entry("name", "Murat Can"),
                Map.entry("phoneNumber", phone.trim()),
                Map.entry("receiveCall", false),
                Map.entry("receiveEmail", false),
                Map.entry("receiveSms", false),
                Map.entry("signupToken", signupToken),
                Map.entry("surname", "Dere")
        );

        Response response = given()
                .spec(baseSpec())
                .body(body)
                .post("/CALL/EnduserAuthorizer/register/" + phone.trim());

        int sc = response.getStatusCode();
        if (sc == 200) {
            System.out.println("Kullanıcı kayıt işlemi başarılı");
            return;
        }

        // Zaten kayıtlı ise hata vermeyelim
        String message = null;
        try {
            message = response.jsonPath().getString("message");
        } catch (Exception ignored) {
        }
        String bodyText = response.asString();
        if ((message != null && message.toLowerCase().contains("zaten"))
                || (bodyText != null && bodyText.toLowerCase().contains("zaten"))) {
            System.out.println("Kullanıcı zaten kayıtlı");
            return;
        }

        throw new AssertionError("Register başarısız. status=" + sc + " body=" + response.asPrettyString());
    }

    @Step("API ile tüm favori listesini sil")
    public void apiDeleteAllWishListsExceptDefaultFavorites() {
        String signupRequired = ApiSession.get(SessionKeys.SIGNUP_REQUIRED);
        if ("true".equalsIgnoreCase(signupRequired)) {
            throw new IllegalStateException("Kullanıcı kayıtlı değil; favori listeleri silinemez.");
        }

        String userId = ApiSession.get(SessionKeys.USER_ID);
        if (userId == null || userId.isBlank()) {
            throw new IllegalStateException("userId yok; favori listeleri silinemez. Önce auth tamamlanmalı.");
        }
        String uid = userId.trim();

        Response listResponse = given()
                .spec(baseSpec())
                .post("/CALL/User/getWishLists/" + uid);
        listResponse.then().statusCode(200);

        List<Map<String, Object>> lists = listResponse.jsonPath().getList("wishLists");
        if (lists == null || lists.isEmpty()) {
            return;
        }

        for (Map<String, Object> l : lists) {
            if (l == null) {
                continue;
            }
            String listName = l.get("listName") == null ? null : String.valueOf(l.get("listName"));
            if ("Favorilerim".equalsIgnoreCase(listName)) {
                continue;
            }
            String listId = l.get("listId") == null ? null : String.valueOf(l.get("listId"));
            if (listId == null || listId.isBlank()) {
                continue;
            }

            given()
                    .spec(baseSpec())
                    .body(Map.of("listId", listId.trim()))
                    .post("/CALL/User/deleteList/" + uid)
                    .then()
                    .statusCode(200);
        }
    }

    @Step("API ile <listeAdi> adında favori listesi varsa sil")
    public void apiDeleteWishlistIfExistsByName(String listeAdi) {
        String signupRequired = ApiSession.get(SessionKeys.SIGNUP_REQUIRED);
        if ("true".equalsIgnoreCase(signupRequired)) {
            throw new IllegalStateException("Kullanıcı kayıtlı değil; favori listeleri silinemez.");
        }

        String userId = ApiSession.get(SessionKeys.USER_ID);
        if (userId == null || userId.isBlank()) {
            throw new IllegalStateException("userId yok; favori listesi silinemez. Önce auth tamamlanmalı.");
        }

        String name = listeAdi == null ? "" : listeAdi.trim();
        if (name.isBlank()) {
            throw new IllegalArgumentException("Liste Adı boş olamaz.");
        }

        String uid = userId.trim();
        Response listResponse = given()
                .spec(baseSpec())
                .post("/CALL/User/getWishLists/" + uid);
        listResponse.then().statusCode(200);

        List<Map<String, Object>> lists = listResponse.jsonPath().getList("wishLists");
        if (lists == null || lists.isEmpty()) {
            return;
        }

        for (Map<String, Object> l : lists) {
            if (l == null) {
                continue;
            }
            String listName = l.get("listName") == null ? null : String.valueOf(l.get("listName"));
            if (listName == null || listName.isBlank()) {
                continue;
            }
            if (!name.equalsIgnoreCase(listName.trim())) {
                continue;
            }

            String listId = l.get("listId") == null ? null : String.valueOf(l.get("listId"));
            if (listId == null || listId.isBlank()) {
                continue;
            }

            given()
                    .spec(baseSpec())
                    .body(Map.of("listId", listId.trim()))
                    .post("/CALL/User/deleteList/" + uid)
                    .then()
                    .statusCode(200);
        }
    }

    @Step("API ile ürün favorilere eklenmişse çıkar. ProductId: <productId>")
    public void apiRemoveProductFromWishListsIfExists(String productId) {
        String signupRequired = ApiSession.get(SessionKeys.SIGNUP_REQUIRED);
        if ("true".equalsIgnoreCase(signupRequired)) {
            throw new IllegalStateException("Kullanıcı kayıtlı değil; favorilerden ürün çıkarılamaz.");
        }

        String userId = ApiSession.get(SessionKeys.USER_ID);
        if (userId == null || userId.isBlank()) {
            throw new IllegalStateException("userId yok; favorilerden ürün çıkarılamaz. Önce auth tamamlanmalı.");
        }

        String sku = productId == null ? "" : productId.trim();
        if (sku.isBlank()) {
            throw new IllegalArgumentException("ProductId boş olamaz.");
        }

        String uid = userId.trim();
        Response listResponse = given()
                .spec(baseSpec())
                .post("/CALL/User/getWishLists/" + uid);
        listResponse.then().statusCode(200);

        List<Map<String, Object>> lists = listResponse.jsonPath().getList("wishLists");
        if (lists == null || lists.isEmpty()) {
            return;
        }

        for (Map<String, Object> l : lists) {
            if (l == null) continue;

            String listId = l.get("listId") == null ? null : String.valueOf(l.get("listId"));
            if (listId == null || listId.isBlank()) continue;

            // getWishLists response: "products": ["10791503", "10037582", ...]
            List<String> products = listResponse.jsonPath().getList("wishLists.find { it.listId == '" + listId.trim() + "' }.products");
            if (products == null || products.isEmpty()) continue;

            boolean skuInThisList = products.stream()
                    .filter(p -> p != null && !p.isBlank())
                    .anyMatch(p -> sku.equalsIgnoreCase(p.trim()));

            if (!skuInThisList) {
                continue;
            }

            given()
                    .spec(baseSpec())
                    .body(Map.of(
                            "listId", listId.trim(),
                            "sku", sku
                    ))
                    .post("/CALL/User/deleteProductFromList/" + uid)
                    .then()
                    .statusCode(200);
        }
    }

    @Step("API ile favori listesi oluştur. Liste Adı: <listeAdi>")
    public void apiCreateWishlist(String listeAdi) {
        String signupRequired = ApiSession.get(SessionKeys.SIGNUP_REQUIRED);
        if ("true".equalsIgnoreCase(signupRequired)) {
            throw new IllegalStateException("Kullanıcı kayıtlı değil; favori listesi oluşturulamaz.");
        }

        String userId = ApiSession.get(SessionKeys.USER_ID);
        if (userId == null || userId.isBlank()) {
            throw new IllegalStateException("userId yok; favori listesi oluşturulamaz. Önce auth tamamlanmalı.");
        }

        String name = listeAdi == null ? "" : listeAdi.trim();
        if (name.isBlank()) {
            throw new IllegalArgumentException("Liste Adı boş olamaz.");
        }

        given()
                .spec(baseSpec())
                .body(Map.of("listName", name))
                .post("/CALL/User/upsertList/" + userId.trim())
                .then()
                .statusCode(200);
    }

    @Step("API ile kullanıcının hesabını sil")
    public void apiDeleteUser() {
        String signupRequired = ApiSession.get(SessionKeys.SIGNUP_REQUIRED);
        if ("true".equalsIgnoreCase(signupRequired)) {
            System.out.println("Hesap zaten kayıtlı değil");
            return;
        }

        String userId = ApiSession.get(SessionKeys.USER_ID);
        if (userId == null || userId.isBlank()) {
            System.out.println("Hesap zaten kayıtlı değil");
            return;
        }

        Response response = given()
                .spec(baseSpec())
                .header("x-rio-sdk-client", "android")
                .post("/CALL/User/deleteUser/" + userId.trim());
        response.then().statusCode(200);

        String message = response.jsonPath().getString("message");
        if (message != null && message.contains("Kullanıcı başarıyla silindi")) {
            System.out.println("Kulanıcı başarılı bir şekilde silindi");
        }
    }

    @Step("API ile sepete urun ekle. ProductId: <urun_id> Quantity: <adet>")
    public void apiCartUpdate(String urun_id, String adet) {
        String userId = ApiSession.get(SessionKeys.USER_ID);
        if (userId == null || userId.isBlank()) {
            throw new IllegalStateException("userId yok; önce kimlik doğrulama adımlarını tamamlayın.");
        }
        String productId = urun_id.trim();
        if (productId.isBlank()) {
            throw new IllegalArgumentException("urun_id boş olamaz.");
        }
        int qty = Integer.parseInt(adet.trim());
        if (qty < 1) {
            throw new IllegalArgumentException("adet en az 1 olmalı.");
        }
        Map<String, Object> body = Map.of(
                "advanced", false,
                "items", List.of(Map.of("id", productId, "qty", qty)));
        given()
                .spec(baseSpec())
                .body(body)
                .post("/CALL/Cart/update/" + userId.trim())
                .then()
                .statusCode(200);
    }

    @Step("API ile sepeti temizle")
    public void apiCartClear() {
        String userId = ApiSession.get(SessionKeys.USER_ID);
        if (userId == null || userId.isBlank()) {
            throw new IllegalStateException("userId yok; önce API ile giriş yapın.");
        }
        given()
                .spec(baseSpec())
                .post("/CALL/Cart/clear/" + userId.trim())
                .then()
                .statusCode(200);
    }

    @Step("API ile kayitli adresler varsa silinir")
    public void apiDeleteAllAddressesIfAny() {
        String userId = ApiSession.get(SessionKeys.USER_ID);
        if (userId == null || userId.isBlank()) {
            throw new IllegalStateException("userId yok; önce API ile giriş yapın.");
        }
        String uid = userId.trim();
        Response listResponse = given()
                .spec(baseSpec())
                .post("/CALL/User/getAddresses/" + uid);
        listResponse.then().statusCode(200);

        List<String> ids = listResponse.jsonPath().getList("addresses.addressId");
        if (ids == null || ids.isEmpty()) {
            return;
        }
        List<String> toDelete = new ArrayList<>();
        for (String id : ids) {
            if (id != null && !id.isBlank()) {
                toDelete.add(id.trim());
            }
        }
        if (toDelete.isEmpty()) {
            return;
        }
        for (String addressId : toDelete) {
            given()
                    .spec(baseSpec())
                    .body(Map.of("addressId", addressId))
                    .post("/CALL/User/deleteAddress/" + uid)
                    .then()
                    .statusCode(200);
        }
    }

    @Step("API ile bireysel adres ekle. Il: <String> Ilce: <String> Mahalle: <String> AdresAdi: <String>")
    public void apiUpsertIndividualAddress(String city, String district, String neighborhood, String addressName) {
        String userId = ApiSession.get(SessionKeys.USER_ID);
        if (userId == null || userId.isBlank()) {
            throw new IllegalStateException("userId yok; önce API ile giriş yapın.");
        }
        Map<String, Object> body = Map.ofEntries(
                Map.entry("addressName", addressName),
                Map.entry("addressText", "deneme"),
                Map.entry("addressType", "individual"),
                Map.entry("city", city),
                Map.entry("country", "Türkiye"),
                Map.entry("district", district),
                Map.entry("firstName", "deneme"),
                Map.entry("lastName", "test"),
                Map.entry("neighborhood", neighborhood),
                Map.entry("phoneNumber", "05995565656"),
                Map.entry("postalCode", ""));
        given()
                .spec(baseSpec())
                .body(body)
                .post("/CALL/User/upsertAddress/" + userId.trim())
                .then()
                .statusCode(200);
    }

    private static String normalizePhone(String phoneNumber) {
        if (phoneNumber == null) {
            throw new IllegalArgumentException("phoneNumber null");
        }
        return phoneNumber.replaceAll("\\s+", "").trim();
    }
}
