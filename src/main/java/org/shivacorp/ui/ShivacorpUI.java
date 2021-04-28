package org.shivacorp.ui;

import org.apache.log4j.Logger;

public class ShivacorpUI {
    Logger log = Logger.getLogger(ShivacorpUI.class);

    public void start() {
        log.info("\n### Welcome to ShivaCorp Banking App ###");

        Menu menu = new MainMenu();
        do {
            menu = (Menu) menu.getSelection();
        } while (menu != null);

        Stdin.close();
    }
}
