

import java.io.IOException;
import java.util.Scanner;


// Class representing a user with username and password
class User {
    private String username;
    private String password;

    // Constructor
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Method to check credentials
    public boolean validate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
}

public class LoginSystemJava {

    // Reads password from console and displays '*' for each character
    public static String readPasswordWithMasking() {
        StringBuilder password = new StringBuilder();
        try {
            while (true) {
                int ch = System.in.read();
                if (ch == '\n' || ch == '\r') {
                    System.out.println(); // Move to next line after Enter
                    break;
                }
                if (ch == 127 || ch == 8) { // Handle Backspace
                    if (password.length() > 0) {
                        password.deleteCharAt(password.length() - 1);
                        System.out.print("\b \b");
                    }
                } else {
                    password.append((char) ch);
                    System.out.print("*");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return password.toString();
    }

    public static void main(String[] args) {
        User user = new User("testuser", "secret123");
        Scanner scanner = new Scanner(System.in);

        int attempts = 0;
        final int MAX_ATTEMPTS = 3;
        boolean loggedIn = false;

        while (attempts < MAX_ATTEMPTS && !loggedIn) {
            System.out.print("Enter Username: ");
            String inputUsername = scanner.nextLine();

            System.out.print("Enter Password: ");
            String inputPassword = readPasswordWithMasking();

            if (user.validate(inputUsername, inputPassword)) {
                System.out.println("Login successful! Welcome, " + inputUsername + ".");
                loggedIn = true;
            } else {
                attempts++;
                if (attempts < MAX_ATTEMPTS) {
                    System.out.println(" Incorrect username or password. Try again (" + (MAX_ATTEMPTS - attempts) + " attempts left).");
                } else {
                    System.out.println(" Too many failed attempts. Your account is locked.");
                }
            }
        }
        scanner.close();
    }
}