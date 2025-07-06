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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoginSessionStorageWriter {
    @Value("${auth.email}")
    private String advisorEmail;
    private static final String FILE_PATH = "src/test/resources/storageState.json";
    private static final String DOMAIN = "advisor-test.pontera.com";
    private static final long TEN_YEARS_SECONDS = 60L * 60 * 24 * 365 * 10;

    private final AuthenticationApi authenticationApi;
    private final AuthenticationDataProvider dataProvider;

    public void createStorageFromApi() throws IOException {
        if (!isStorageFileExpired()) {
            return;
        }
        List<Map<String, Object>> cookies = new ArrayList<>();

        Map<String, String> apiCookies = getApiCookies();
        addBasicCookies(cookies, apiCookies);
        addStaticCookies(cookies);

        Map<String, Object> storage = Map.of(
                "cookies", cookies,
                "origins", List.of()
        );

        writeJsonToFile(storage);
    }

    private Map<String, String> getApiCookies() {
        LoginRequest request = dataProvider.createLoginRequest();
        Response response = authenticationApi.sendLoginRequest(request);
        return response.getCookies();
    }

    private void addBasicCookies(List<Map<String, Object>> cookies, Map<String, String> apiCookies) {
        addCookie(cookies, "JSESSIONID", apiCookies.get("JSESSIONID"), null, true);
        addCookie(cookies, "AWSALB", apiCookies.get("AWSALB"), null, false);
        addCookie(cookies, "AWSALBCORS", apiCookies.get("AWSALBCORS"), null, false);
    }

    private void addStaticCookies(List<Map<String, Object>> cookies) {
        String longExpiry = Instant.now().plusSeconds(TEN_YEARS_SECONDS).toString();

        addCookie(cookies, "feex-user", "userId%3D453631918%26autoLogin%3Dfalse%26r%3Dmem9ir1cvmltsotbqona17ddtj%26c%3Dxxencrypted2xx...",
                longExpiry, false);

        addCookie(cookies, "user-id", "453631918", null, false);
        addCookie(cookies, "user-email", advisorEmail, null, false);
        addCookie(cookies, "login-time", String.valueOf(System.currentTimeMillis()), null, false);
        addCookie(cookies, "CC", "tuacce215pgie8v2efom5f7v7p", null, false);
    }

    private void addCookie(List<Map<String, Object>> cookies, String name, String value, String expiryISO, boolean httpOnly) {
        if (value == null) {
            return;
        }

        Long expires = expiryISO != null ? Instant.parse(expiryISO).getEpochSecond() : Instant.now().getEpochSecond() + 3600;

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
        File file = new File(LoginSessionStorageWriter.FILE_PATH);
        file.getParentFile().mkdirs();
        new ObjectMapper().writeValue(file, data);
    }

    public boolean isStorageFileExpired() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            throw new IllegalArgumentException("Storage file not found at path: " + FILE_PATH);
        }

        Instant lastModifiedTime = Instant.ofEpochMilli(file.lastModified());
        Instant expiryThreshold = Instant.now().minusSeconds(15 * 60);

        return lastModifiedTime.isBefore(expiryThreshold);
    }
}
