package org.shivacorp.ui;

import org.apache.log4j.Logger;

public class ShivacorpApp {
    private static Logger log = Logger.getLogger(ShivacorpApp.class);
    private String title = "ShivaCorp Banking App";

//    public ShivacorpApp() { }

    public void main() {
        if (!title.isEmpty())
            log.info(title);

        Menu currentMenu = new MainMenu();
        do {
            currentMenu.displayMenu();
            currentMenu = currentMenu.processInput();
        } while (currentMenu != null);

        Stdin.close();
    }
}
