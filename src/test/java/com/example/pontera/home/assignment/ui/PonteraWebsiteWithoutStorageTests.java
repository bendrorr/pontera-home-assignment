package com.example.pontera.home.assignment.ui;

import com.example.pontera.home.assignment.config.ComponentScanConfig;
import com.example.pontera.home.assignment.pages.impl.AddNewClientPage;
import com.example.pontera.home.assignment.pages.impl.ClientsPage;
import com.example.pontera.home.assignment.pages.impl.LoginPage;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assumptions.assumeThat;

@SpringBootTest
@ContextConfiguration(classes = ComponentScanConfig.class)
class PonteraWebsiteWithoutStorageTests {
    @Value("${auth.email}")
    private String advisorEmail;
    @Value("${auth.password}")
    private String advisorPassword;
    @Autowired
    private Browser browser;

    private BrowserContext context;
    private Page page;
    private LoginPage loginPage;
    private ClientsPage clientsPage;
    private AddNewClientPage addNewClientPage;

    @BeforeEach
    void setUp() {
        context = browser.newContext();
        page = context.newPage();

        loginPage = new LoginPage(page);
        clientsPage = new ClientsPage(page);
        addNewClientPage = new AddNewClientPage(page);

    }

    @AfterEach
    void tearDown() {
        if (page != null) page.close();
        if (context != null) context.close();
    }


    @Test
    void whenAdvisorLogsInAndClicksAddNewClient_thenAddNewClientPageIsDisplayed() {
        loginPage.navigateToLoginPage();
        assumeThat(loginPage.isPageLoaded()).as("Login page should be loaded").isTrue();

        loginPage.login(advisorEmail, advisorPassword);
        assumeThat(clientsPage.isPageLoaded()).as("Clients page should be loaded").isTrue();

        clientsPage.clickOnAddNewClient();

        Assertions.assertThat(addNewClientPage.isPageLoaded())
                .as("Add New Client page should be loaded")
                .isTrue();
    }
}