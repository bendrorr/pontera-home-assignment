package com.example.pontera.home.assignment.pages.impl;

import com.example.pontera.home.assignment.pages.PageLoadable;
import com.example.pontera.home.assignment.util.CommonActions;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class AddNewClientPage implements PageLoadable {
    private static final String ADD_NEW_CLIENT_URL = "https://advisor-test.pontera.com/qaa5/advisor/clients/-1/edit";
    private static final Integer PAGE_LOAD_TIMEOUT = 8000;
    private final Locator firstNameField;
    private final Locator lastNameField;
    private final Locator SSNField;
    private final Locator emailField;
    private final Locator mobileField;
    private final Page page;

    public AddNewClientPage(Page page) {
        this.page = page;
        this.firstNameField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First name"));
        this.lastNameField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last name"));
        this.SSNField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("SSN / ITIN"));
        this.emailField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email"));
        this.mobileField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Mobile phone"));
    }

    public void navigateToAddNewClientPage() {
        page.navigate(ADD_NEW_CLIENT_URL);
    }

    @Override
    public boolean isLoaded() {
        return CommonActions.isOnPage(PAGE_LOAD_TIMEOUT,
                firstNameField,
                lastNameField,
                SSNField,
                emailField,
                mobileField
        );
    }
}
