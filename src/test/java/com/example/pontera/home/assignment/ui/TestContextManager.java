package com.example.pontera.home.assignment.ui;

import com.example.pontera.home.assignment.util.StorageUtil;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class TestContextManager {
    private final Browser browser;
    private final StorageUtil storageUtil;
    private static final String STORAGE_PATH = "src/test/resources/storageState.json";

    public BrowserContext createContextWithStorage() throws IOException {
        storageUtil.createStorageIfExpired();
        return browser.newContext(new Browser.NewContextOptions()
                .setStorageStatePath(Paths.get(STORAGE_PATH)));
    }

    public BrowserContext createContext() {
        return browser.newContext();
    }

    public Page createPage(BrowserContext context) {
        return context.newPage();
    }

    public void close(Page page, BrowserContext context) {
        if (page != null) page.close();
        if (context != null) context.close();
    }
}
