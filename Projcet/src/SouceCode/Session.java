package SouceCode;

import entities.Staff;

public class Session {
    public static boolean isLoggedIn = false;
    public static String username = null; 
    
    private Session() {}

    public static Staff currentStaff = null;

    public static boolean isLoggedIn() {
        return currentStaff != null;
    }

    public static void logout() {
        currentStaff = null;
    }
}
