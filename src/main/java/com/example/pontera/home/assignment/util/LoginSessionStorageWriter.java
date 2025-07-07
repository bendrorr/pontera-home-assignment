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
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoginSessionStorageWriter {
    private static final String FILE_PATH = "src/test/resources/storageState.json";
    private static final String DOMAIN = "advisor-test.pontera.com";
    private static final long SESSION_EXPIRY_MINUTES = 15;
    private static final long COOKIE_EXPIRY_YEARS = 10;

    @Value("${auth.email}")
    private String advisorEmail;

    private final AuthenticationApi authenticationApi;
    private final AuthenticationDataProvider dataProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void createStorageFromApi() throws IOException {
        if (!isStorageFileExpired()) {
            return;
        }

        Map<String, String> apiCookies = getApiCookies();
        List<Map<String, Object>> allCookies = buildAllCookies(apiCookies);

        Map<String, Object> storage = Map.of(
                "cookies", allCookies,
                "origins", Collections.emptyList()
        );

        saveToFile(storage);
    }

    private Map<String, String> getApiCookies() {
        LoginRequest request = dataProvider.createLoginRequest();
        Response response = authenticationApi.sendLoginRequest(request);
        return response.getCookies();
    }

    private List<Map<String, Object>> buildAllCookies(Map<String, String> apiCookies) {
        List<Map<String, Object>> cookies = new ArrayList<>();

        addCookie(cookies, "JSESSIONID", apiCookies.get("JSESSIONID"), true);
        addCookie(cookies, "AWSALB", apiCookies.get("AWSALB"), false);
        addCookie(cookies, "AWSALBCORS", apiCookies.get("AWSALBCORS"), false);

        long tenYearsFromNow = Instant.now().plusSeconds(60L * 60 * 24 * 365 * COOKIE_EXPIRY_YEARS).getEpochSecond();
        addCookie(cookies, "feex-user", "userId%3D453631918%26autoLogin%3Dfalse%26r%3Dmem9ir1cvmltsotbqona17ddtj%26c%3Dxxencrypted2xx...", tenYearsFromNow, false);
        addCookie(cookies, "user-id", "453631918", false);
        addCookie(cookies, "user-email", advisorEmail, false);
        addCookie(cookies, "login-time", String.valueOf(System.currentTimeMillis()), false);
        addCookie(cookies, "CC", "tuacce215pgie8v2efom5f7v7p", false);

        return cookies;
    }

    private void addCookie(List<Map<String, Object>> cookies, String name, String value, boolean httpOnly) {
        addCookie(cookies, name, value, Instant.now().getEpochSecond() + 3600, httpOnly);
    }

    private void addCookie(List<Map<String, Object>> cookies, String name, String value, long expires, boolean httpOnly) {
        if (value == null) return;

        cookies.add(Map.of(
                "name", name,
                "value", value,
                "domain", DOMAIN,
                "path", "/",
                "httpOnly", httpOnly,
                "secure", true,
                "sameSite", "Lax",
                "expires", expires
        ));
    }

    private void saveToFile(Map<String, Object> data) throws IOException {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        objectMapper.writeValue(file, data);
    }

    public boolean isStorageFileExpired() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return true;
        }

        Instant lastModified = Instant.ofEpochMilli(file.lastModified());
        Instant threshold = Instant.now().minusSeconds(SESSION_EXPIRY_MINUTES * 60);

        return lastModified.isBefore(threshold);
    }
}