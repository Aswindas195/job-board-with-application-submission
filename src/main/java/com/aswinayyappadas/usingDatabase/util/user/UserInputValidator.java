package com.aswinayyappadas.usingDatabase.util.user;

public class UserInputValidator {
    public boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        // Allowing space-separated full names
        String[] names = name.split("\\s+");
        for (String individualName : names) {
            if (individualName.trim().isEmpty() || !Character.isUpperCase(individualName.charAt(0))
                    || !individualName.substring(1).toLowerCase().equals(individualName.substring(1))) {
                return false; // Reject if any part is empty, doesn't start with an uppercase letter,
                // or contains uppercase letters after the first character
            }
        }

        return true;
    }
    public boolean isValidEmail(String email) {
        return email != null && email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }

    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public boolean isValidUserType(int userType) {
        return userType == 1 || userType == 2;
    }

}
