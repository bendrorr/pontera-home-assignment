package com.example.pontera.home.assignment.ui;

import com.example.pontera.home.assignment.config.ComponentScanConfig;
import com.example.pontera.home.assignment.pages.impl.AddNewClientPage;
import com.example.pontera.home.assignment.pages.impl.ClientsPage;
import com.example.pontera.home.assignment.pages.impl.LoginPage;
import com.example.pontera.home.assignment.util.RetriesUtil;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

@SpringBootTest
@ContextConfiguration(classes = ComponentScanConfig.class)
class PonteraWebsiteWithoutStorageTests {
    @Value("${auth.email}")
    private String advisorEmail;
    @Value("${auth.password}")
    private String advisorPassword;
    @Autowired
    private TestContextManager contextManager;
    private BrowserContext context;
    private Page page;
    private LoginPage loginPage;
    private ClientsPage clientsPage;
    private AddNewClientPage addNewClientPage;

    @BeforeEach
    void setUp() {
        context = contextManager.createFreshContext();
        page = contextManager.createPage(context);
        loginPage = new LoginPage(page);
        clientsPage = new ClientsPage(page);
        addNewClientPage = new AddNewClientPage(page);
    }

    @AfterEach
    void tearDown() {
        contextManager.close(page, context);
    }


    @ParameterizedTest
    @ValueSource(ints = {2})
    void whenAdvisorLogsInAndClicksAddNewClient_thenAddNewClientPageIsDisplayed(Integer maxRetries) {
        RetriesUtil.runWithRetries(() -> {
            loginPage.navigateToLoginPage();
            assumeThat(loginPage.isPageLoaded()).as("Login page should be loaded").isTrue();

            loginPage.login(advisorEmail, advisorPassword);
            assumeThat(clientsPage.isPageLoaded()).as("Clients page should be loaded").isTrue();

            clientsPage.clickOnAddNewClient();

            assertThat(addNewClientPage.isPageLoaded())
                    .as("Add New Client page should be loaded")
                    .isTrue();
        }, maxRetries);
    }

}