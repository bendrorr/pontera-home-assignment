package com.example.pontera.home.assignment.util;

import com.example.pontera.home.assignment.api.AuthenticationApi;
import com.example.pontera.home.assignment.api.testdata.AuthenticationDataProvider;
import com.example.pontera.home.assignment.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
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
public class storageUtil {
    private static final String FILE_PATH = "src/test/resources/storageState.json";
    private static final String DOMAIN = "advisor-test.pontera.com";

    private final AuthenticationApi authenticationApi;
    private final AuthenticationDataProvider dataProvider;
    private final ObjectMapper objectMapper;

    public void createStorageIfExpired() throws IOException {
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
        return cookies;
    }

    private void addCookie(List<Map<String, Object>> cookies, String name, String value, boolean httpOnly) {
        long expires = Instant.now().getEpochSecond() + 3600;
        addCookie(cookies, name, value, expires, httpOnly);
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
        Instant threshold = Instant.now().minusSeconds(15 * 60);

        return lastModified.isBefore(threshold);
    }

}
