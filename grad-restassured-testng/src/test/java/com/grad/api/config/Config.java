package com.grad.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * config.properties-dən sandbox base URL-i və test istifadəçilərinin məlumatlarını oxuyur.
 * Mənbə: TestPlan.md bölmə 3.
 */
public final class Config {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties tapılmadı (src/test/resources altında olmalıdır)");
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("config.properties oxunarkən xəta", e);
        }
    }

    private Config() {
    }

    public static String baseUrl() {
        return PROPS.getProperty("base.url");
    }

    public static String user1Email() {
        return PROPS.getProperty("user1.email");
    }

    public static String user1Password() {
        return PROPS.getProperty("user1.password");
    }

    public static String user2Email() {
        return PROPS.getProperty("user2.email");
    }

    public static String user2Password() {
        return PROPS.getProperty("user2.password");
    }
}
