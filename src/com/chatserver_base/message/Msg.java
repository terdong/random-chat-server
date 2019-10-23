package com.chatserver_base.message;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Msg {
    private static final String BUNDLE_NAME = "messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle(BUNDLE_NAME);

    protected Msg() {
    }
    
    public static void printKey() {
        System.out.println(RESOURCE_BUNDLE.keySet());
    }

    public static String getStr(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
    public static String getStr(String key1, String key2) {
        try {
            return RESOURCE_BUNDLE.getString(key1)+RESOURCE_BUNDLE.getString(key2);
        } catch (MissingResourceException e) {
            return '!' + key1 + '!' + '!' + key2 + '!';
        }
    }
    public static String getStr(String key1, String key2, String key3) {
        try {
            return RESOURCE_BUNDLE.getString(key1)+RESOURCE_BUNDLE.getString(key2)+RESOURCE_BUNDLE.getString(key3);
        } catch (MissingResourceException e) {
            return '!' + key1 + '!' + '!' + key2 + '!'+ '!' + key3 + '!';
        }
    }
    public static String getStr(String key1, String key2, String key3, String key4) {
        try {
            return RESOURCE_BUNDLE.getString(key1)+RESOURCE_BUNDLE.getString(key2)+RESOURCE_BUNDLE.getString(key3)+RESOURCE_BUNDLE.getString(key4);
        } catch (MissingResourceException e) {
            return '!' + key1 + '!' + '!' + key2 + '!'+ '!' + key3 + '!' + '!' + key4 + '!';
        }
    }
}//class











