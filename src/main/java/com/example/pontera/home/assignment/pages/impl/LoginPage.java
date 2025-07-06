package com.example.pontera.home.assignment.pages.impl;

import com.example.pontera.home.assignment.pages.PageLoadable;
import com.example.pontera.home.assignment.util.CommonActions;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class LoginPage implements PageLoadable {
    private static final String LOGIN_PAGE_URL = "https://advisor-test.pontera.com/business/auth/signin";
    private static final Integer PAGE_LOAD_TIMEOUT = 5000;

    private final Page page;

    private final Locator emailField;
    private final Locator passwordField;
    private final Locator loginButton;

    public LoginPage(Page page) {
        this.page = page;
        this.emailField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email"));
        this.passwordField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password"));
        this.loginButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Log in"));
    }

    public void navigateToLoginPage() {
        page.navigate(LOGIN_PAGE_URL);
    }

    public void login(String email, String password) {
        emailField.fill(email);
        passwordField.fill(password);
        emailField.click();
        loginButton.click();
    }

    @Override
    public boolean isPageLoaded() {
        return CommonActions.isOnPage(PAGE_LOAD_TIMEOUT, emailField, passwordField);
    }
}
