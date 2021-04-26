package org.shivacorp.ui;

import org.apache.log4j.Logger;
import java.util.Scanner;

public class Stdin {
    private static final Scanner scanner;
    private static final Logger log = Logger.getLogger(Stdin.class);

    private Stdin() {}

    static { scanner = new Scanner(System.in); }

    public static void close() { scanner.close(); }

    public static Integer nextInt() {
        int n = 0;
        boolean valid = false;
        String curr = "";
        do {
            try {
                curr = scanner.nextLine();
                n = Integer.parseInt(curr);
                valid = true;
            } catch (NumberFormatException e) {
                log.info("Invalid choice: '"+curr+"'");
            }
        } while(!valid);
        return n;
    }

    public static String nextString() {
        return scanner.nextLine();
    }

    public static String getPassword() {
        char[] password = null;
        if (System.console() != null) {
            password = System.console().readPassword();
        }
        return (password == null) ? nextString() : String.valueOf(password);
    }
}
