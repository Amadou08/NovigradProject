package com.example.novigradproject.utils;

public class EmailUtils {
    public static boolean isValidUserName(String str) {
        return isSingleWord(str) && isAlphaNumeric(str);
    }

    public static String getDomainSuffixedEmailAddress(String userName) {
        return userName.trim() + Constants.EMAIL_ADDRESS_SUFFIX;
    }

    private static boolean isSingleWord(String str) {
        return str.matches("\\w+");
    }

    // Helper method to check if a string contains only English alphabets and numbers
    private static boolean isAlphaNumeric(String str) {
        return str.matches("[a-zA-Z0-9]+");
    }
}
