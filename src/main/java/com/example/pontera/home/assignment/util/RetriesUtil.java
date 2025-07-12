package com.example.pontera.home.assignment.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RetriesUtil {

    public static void runWithRetries(Runnable action, int maxRetries) {
        int attempts = 0;
        while (attempts < maxRetries) {
            try {
                action.run();
                return;
            } catch (Throwable throwable) {
                attempts++;
                if (attempts >= maxRetries) {
                    throw throwable;
                }

            }
        }
    }
}
