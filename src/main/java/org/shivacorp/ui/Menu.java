package org.shivacorp.ui;

import org.apache.log4j.Logger;

abstract class Menu {
    protected Logger log;
    protected String title;
    protected String[] menuItems;
    protected int menuChoice;
    protected static final String MENU_ITEM_SEPERATOR = "..";
    protected static final String MENU_PROMPT = "Enter choice";
    protected static final String INVALID_MENU_CHOICE = "Invalid choice";

    public void displayMenu() {
        log.info(title);
        for(int i = 1; i <= menuItems.length; i++) {
            log.info(i + MENU_ITEM_SEPERATOR + menuItems[i-1]);
        }
    }

    protected void displaySubmenu() {
        log.info(title+" > "+menuItems[menuChoice - 1]);
    }

    public abstract Menu processInput();
}
