package com.example.pontera.home.assignment.pages.impl;

import com.example.pontera.home.assignment.pages.PageLoadable;
import com.example.pontera.home.assignment.util.CommonActions;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class ClientsPage implements PageLoadable {
    private static final String CLIENTS_PAGE_URL = "https://advisor-test.pontera.com/qaa5/advisor/clients";
    private static final Integer PAGE_LOAD_TIMEOUT = 8000;

    private final Page page;

    private final Locator advisorsComboBox;
    private final Locator addNewClientButton;

    public ClientsPage(Page page) {
        this.page = page;
        this.advisorsComboBox = page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Select advisors All"));
        this.addNewClientButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("+ Add new client"));
    }

    public void navigateToClientsPage() {
        page.navigate(CLIENTS_PAGE_URL);
    }

    public void clickOnAddNewClient() {
        addNewClientButton.click();
    }

    @Override
    public boolean isPageLoaded() {
        return CommonActions.isOnPage(PAGE_LOAD_TIMEOUT, advisorsComboBox, addNewClientButton);
    }
}
