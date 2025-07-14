package com.example.pontera.home.assignment.config;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class PlaywrightConfig {
    @Value("${playwright.headless:false}")
    private boolean headless;

    @Value("${playwright.slowMo:0}")
    private int slowMo;

    @Lazy
    @Bean(destroyMethod = "close")
    public Playwright playwright() {
        return Playwright.create();
    }

    @Lazy
    @Bean(destroyMethod = "close")
    public Browser browser(Playwright playwright) {
        return playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(slowMo));
    }

}
