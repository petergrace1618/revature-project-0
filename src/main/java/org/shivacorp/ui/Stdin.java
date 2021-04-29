package org.shivacorp.ui;

import org.apache.log4j.Logger;
import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Stdin {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Logger log = Logger.getLogger(Stdin.class);
    private static final String INVALID_INPUT = "Invalid input: ";

    private Stdin() {}

    public static void close() { scanner.close(); }

    public static Integer getInt(int min, int max) {
        int n = 0;
        boolean valid = false;
        String curr = "";
        do {
            try {
                curr = scanner.nextLine();
                n = Integer.parseInt(curr);
                if (n >= min && n <= max) {
                    valid = true;
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                log.info("Please enter a number between "+min+" and "+max);
            }
        } while(!valid);
        return n;
    }

    public static Integer getInt(int max) {
        return getInt(0, max);
    }

    public static BigDecimal getBigDecimal() {
        double d = 0.0;
        boolean valid = false;
        String curr = "";
        do {
            try {
                curr = scanner.nextLine();
                d = Double.parseDouble(curr);
                valid = true;
            } catch (NumberFormatException e) {
                log.info(INVALID_INPUT+"'"+curr+"'");
            }
        } while(!valid);
        return BigDecimal.valueOf(d);
    }

    public static double getDouble() {
        double d = 0.0;
        boolean valid = false;
        String curr = "";
        do {
            try {
                curr = scanner.nextLine();
                d = Double.parseDouble(curr);
                if (d < 0)
                    throw new NumberFormatException();
                if (d >= 0)
                    valid = true;
            } catch (NumberFormatException e) {
                log.info("Please enter a non-negative decimal number");
            }
        } while(!valid);
        return d;
    }

    public static String getString(boolean nonEmptyConstraint) {
        String s = scanner.nextLine();
        if (nonEmptyConstraint)
            while (s.isEmpty()) {
                log.info("String cannot be empty");
                s = scanner.nextLine();
            }
        return s;
    }

    public static String getString() {
        return getString(true);
    }

    public static String getPassword() {
        char[] password = null;
        if (System.console() != null) {
            password = System.console().readPassword();
        }
        return (password == null) ? getString() : String.valueOf(password);
    }
}
