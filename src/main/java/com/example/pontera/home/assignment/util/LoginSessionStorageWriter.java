package com.example.pontera.home.assignment.util;

import com.example.pontera.home.assignment.api.AuthenticationApi;
import com.example.pontera.home.assignment.api.testdata.AuthenticationDataProvider;
import com.example.pontera.home.assignment.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Component
@RequiredArgsConstructor
public class LoginSessionStorageWriter {
    private static final String FILE_PATH = "src/test/resources/storageState.json";
    private static final String DOMAIN = "advisor-test.pontera.com";
    private static final long TEN_YEARS_SECONDS = 60L * 60 * 24 * 365 * 10;
    private static final String FEE_X_USER_VALUE = "userId%3D453631918%26autoLogin%3Dfalse%26r%3Dmem9ir1cvmltsotbqona17ddtj%26c%3Dxxencrypted2xx...";
    private static final String CC_VALUE = "tuacce215pgie8v2efom5f7v7p";
    private static final String USER_ID = "453631918";

    @Value("${auth.email}")
    private String advisorEmail;

    private final AuthenticationApi authenticationApi;
    private final AuthenticationDataProvider dataProvider;

    public void createStorageFromApi() throws IOException {
        if (!isStorageFileExpired()) {
            return;
        }

        List<Map<String, Object>> cookies = new ArrayList<>();
        Map<String, String> apiCookies = fetchApiCookies();

        addApiSessionCookies(cookies, apiCookies);
        addPersistentCookies(cookies);

        Map<String, Object> storage = Map.of(
                "cookies", cookies,
                "origins", Collections.emptyList()
        );

        writeJsonToFile(storage);
    }

    private Map<String, String> fetchApiCookies() {
        LoginRequest request = dataProvider.createLoginRequest();
        Response response = authenticationApi.sendLoginRequest(request);
        return response.getCookies();
    }

    private void addApiSessionCookies(List<Map<String, Object>> cookies, Map<String, String> apiCookies) {
        addCookie(cookies, "JSESSIONID", apiCookies.get("JSESSIONID"), null, true);
        addCookie(cookies, "AWSALB", apiCookies.get("AWSALB"), null, false);
        addCookie(cookies, "AWSALBCORS", apiCookies.get("AWSALBCORS"), null, false);
    }

    private void addPersistentCookies(List<Map<String, Object>> cookies) {
        String expiryISO = Instant.now().plusSeconds(TEN_YEARS_SECONDS).toString();

        addCookie(cookies, "feex-user", FEE_X_USER_VALUE, expiryISO, false);
        addCookie(cookies, "user-id", USER_ID, null, false);
        addCookie(cookies, "user-email", advisorEmail, null, false);
        addCookie(cookies, "login-time", String.valueOf(System.currentTimeMillis()), null, false);
        addCookie(cookies, "CC", CC_VALUE, null, false);
    }

    private void addCookie(List<Map<String, Object>> cookies, String name, String value, String expiryISO, boolean httpOnly) {
        if (value == null) return;

        long expires = expiryISO != null
                ? Instant.parse(expiryISO).getEpochSecond()
                : Instant.now().getEpochSecond() + 3600;

        Map<String, Object> cookie = new HashMap<>();
        cookie.put("name", name);
        cookie.put("value", value);
        cookie.put("domain", DOMAIN);
        cookie.put("path", "/");
        cookie.put("httpOnly", httpOnly);
        cookie.put("secure", true);
        cookie.put("sameSite", "Lax");
        cookie.put("expires", expires);

        cookies.add(cookie);
    }

    private void writeJsonToFile(Map<String, Object> data) throws IOException {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        new ObjectMapper().writeValue(file, data);
    }

    public boolean isStorageFileExpired() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            throw new IllegalArgumentException("Storage file not found at path: " + FILE_PATH);
        }

        Instant lastModified = Instant.ofEpochMilli(file.lastModified());
        Instant threshold = Instant.now().minusSeconds(15 * 60);

        return lastModified.isBefore(threshold);
    }
}
