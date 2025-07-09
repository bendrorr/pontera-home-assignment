package com.example.pontera.home.assignment.ui;

import com.example.pontera.home.assignment.config.ComponentScanConfig;
import com.example.pontera.home.assignment.pages.impl.AddNewClientPage;
import com.example.pontera.home.assignment.pages.impl.ClientsPage;
import com.example.pontera.home.assignment.util.RetriesUtil;
import com.example.pontera.home.assignment.util.StorageUtil;
import com.microsoft.playwright.Browser;
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

import java.io.IOException;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

@SpringBootTest
@ContextConfiguration(classes = ComponentScanConfig.class)
public class PonteraWebsiteWithStorageTests {
    @Value("${auth.email}")
    private String advisorEmail;
    @Value("${auth.password}")
    private String advisorPassword;
    @Autowired
    private Browser browser;
    @Autowired
    private StorageUtil storageUtil;

    private BrowserContext context;
    private Page page;
    private ClientsPage clientsPage;
    private AddNewClientPage addNewClientPage;
    private static final String STORAGE_PATH = "src/test/resources/storageState.json";


    @BeforeEach
    void setUp() throws IOException {
        storageUtil.createStorageIfExpired();
        context = browser.newContext(new Browser.NewContextOptions().setStorageStatePath(Paths.get(STORAGE_PATH)));
        page = context.newPage();

        clientsPage = new ClientsPage(page);
        addNewClientPage = new AddNewClientPage(page);

    }

    @AfterEach
    void tearDown() {
        if (page != null) page.close();
        if (context != null) context.close();
    }


    @ParameterizedTest
    @ValueSource(ints = {2})
    void whenAdvisorClicksAddNewClient_thenAddNewClientPageIsDisplayed(Integer maxRetries) {
        RetriesUtil.runWithRetries(() -> {
            clientsPage.navigateToClientsPage();

            assumeThat(clientsPage.isPageLoaded())
                    .isTrue();

            clientsPage.clickOnAddNewClient();

            assertThat(addNewClientPage.isPageLoaded())
                    .isTrue();
        }, maxRetries);

    }

    @ParameterizedTest
    @ValueSource(ints = {2})
    void whenNavigatingToAddNewClientPage_thenPageShouldBeDisplayed(Integer maxRetries) {
        RetriesUtil.runWithRetries(() -> {
            addNewClientPage.navigateToAddNewClientPage();

            assertThat(addNewClientPage.isPageLoaded())
                    .isTrue();
        }, maxRetries);
    }

}
