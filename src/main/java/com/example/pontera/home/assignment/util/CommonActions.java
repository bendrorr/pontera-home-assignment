package com.example.pontera.home.assignment.util;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonActions {

    public static boolean isOnPage(int timeoutMillis, Locator... locators) {
        for (Locator locator : locators) {
            if (!isLocatorVisible(locator, timeoutMillis)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isLocatorVisible(Locator locator, int timeoutMillis) {
        try {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeoutMillis));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
