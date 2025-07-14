package com.example.pontera.home.assignment.ui;

import com.example.pontera.home.assignment.config.ComponentScanConfig;
import com.example.pontera.home.assignment.pages.impl.AddNewClientPage;
import com.example.pontera.home.assignment.pages.impl.ClientsPage;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

@SpringBootTest
@ContextConfiguration(classes = ComponentScanConfig.class)
public class PonteraWebsiteWithStorageTests {
    @Autowired
    private TestContextManager contextManager;
    private BrowserContext context;
    private Page page;
    private ClientsPage clientsPage;
    private AddNewClientPage addNewClientPage;


    @BeforeEach
    void setUp() throws IOException {
        context = contextManager.createContextWithStorage();
        page = contextManager.createPage(context);
        clientsPage = new ClientsPage(page);
        addNewClientPage = new AddNewClientPage(page);
    }

    @AfterEach
    void tearDown() {
        contextManager.close(page, context);
    }


    @ParameterizedTest
    @ValueSource(ints = {2})
    void whenAdvisorClicksAddNewClient_thenAddNewClientPageIsDisplayed(Integer maxRetries) {
        clientsPage.navigateToClientsPage();

        assumeThat(clientsPage.isLoaded())
                .isTrue();

        clientsPage.clickOnAddNewClient();

        assertThat(addNewClientPage.isLoaded())
                .isTrue();

    }

    @ParameterizedTest
    @ValueSource(ints = {2})
    void whenNavigatingToAddNewClientPage_thenPageShouldBeDisplayed(Integer maxRetries) {
        addNewClientPage.navigateToAddNewClientPage();

        assertThat(addNewClientPage.isLoaded())
                .isTrue();
    }

}
