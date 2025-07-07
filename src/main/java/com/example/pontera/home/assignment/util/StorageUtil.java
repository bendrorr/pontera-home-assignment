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
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StorageUtil {
    private static final String FILE_PATH = "src/test/resources/storageState.json";

    private final AuthenticationApi authenticationApi;
    private final AuthenticationDataProvider dataProvider;
    private final ObjectMapper objectMapper;

    public void createStorageIfExpired() throws IOException {
        if (!isStorageFileExpired()) {
            return;
        }

        Map<String, Object> cookie = createJSessionCookie();
        Map<String, Object> storage = createStorageMap(cookie);

        saveToFile(storage);
    }

    private Map<String, Object> createJSessionCookie() {
        return Map.of(
                "name", "JSESSIONID",
                "value", getSessionIdFromLogin(),
                "domain", "advisor-test.pontera.com",
                "path", "/",
                "httpOnly", true,
                "secure", true,
                "sameSite", "Lax",
                "expires", Instant.now().getEpochSecond() + 3600
        );
    }

    private Map<String, Object> createStorageMap(Map<String, Object> cookie) {
        return Map.of(
                "cookies", List.of(cookie),
                "origins", Collections.emptyList()
        );
    }

    private String getSessionIdFromLogin() {
        LoginRequest request = dataProvider.createLoginRequest();
        Response response = authenticationApi.sendLoginRequest(request);
        return response.getCookies().get("JSESSIONID");
    }

    private void saveToFile(Map<String, Object> data) throws IOException {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        objectMapper.writeValue(file, data);
    }

    private boolean isStorageFileExpired() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return true;
        }

        Instant lastModified = Instant.ofEpochMilli(file.lastModified());
        Instant threshold = Instant.now().minusSeconds(15 * 60);

        return lastModified.isBefore(threshold);
    }
}
