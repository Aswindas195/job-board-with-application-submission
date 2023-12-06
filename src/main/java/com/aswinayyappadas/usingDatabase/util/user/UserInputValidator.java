package com.aswinayyappadas.usingDatabase.util.user;

public class UserInputValidator {

    public enum UserType {
        JOB_SEEKER,
        EMPLOYER
    }

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

    public boolean isValidUserType(String userType) {
        try {
            UserType.valueOf(userType.toUpperCase());  // Try to convert the input to uppercase and check if it's a valid enum value
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        UserInputValidator v = new UserInputValidator();
        System.out.println(v.isValidName("Aswin"));               // Should print true
        System.out.println(v.isValidName("John doe"));            // Should print true
        System.out.println(v.isValidName("Alice  Bob"));          // Should print true
        System.out.println(v.isValidName("   "));                 // Should print false
        System.out.println(v.isValidName("john Doe"));            // Should print false
        System.out.println(v.isValidName("Alice  Bob Joe"));      // Should print false
        System.out.println(v.isValidName("Alice  bob"));          // Should print false
        System.out.println(v.isValidEmail("aswin@mail.com"));      // Should print true
        System.out.println(v.isValidPassword("Das@123232"));      // Should print true
        System.out.println(v.isValidUserType("job_seeker"));       // Should print true
        System.out.println(v.isValidUserType("employer"));         // Should print true
        System.out.println(v.isValidUserType("manager"));          // Should print false
    }
}
