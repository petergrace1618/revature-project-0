package org.shivacorp.ui;

import org.apache.log4j.Logger;
import org.shivacorp.service.ShivacorpService;
import org.shivacorp.service.impl.ShivacorpServiceImpl;

abstract class Menu {
    protected String title;
    protected int selection;
    protected String[] menuItems;
    protected int numMenuItems;
    protected boolean showCancelOption = false;
    protected String menuPrompt = "Enter choice:";
    protected static final Logger log = Logger.getLogger(Menu.class);
    protected static final String MENU_ITEM_SEPARATOR = "..";
    protected static final String INVALID_MENU_CHOICE = "Invalid choice";
    protected static final ShivacorpService shivacorpService = new ShivacorpServiceImpl();

    public abstract Object getSelection();

    public void display() {
        if (title != null) log.info("\n"+title);
        if (showCancelOption) log.info("0" +MENU_ITEM_SEPARATOR+ "Cancel");
        int i = 1;
        for(String item: menuItems) {
            log.info(i++ +MENU_ITEM_SEPARATOR+ item);
        }
    }

    public void displayPrompt() {
        log.info(menuPrompt);
    }

    protected void displaySubmenu() {
        log.info("\n"+title+" > "+menuItems[selection - 1]);
    }

    protected void serviceUnavailable() { log.info("I'm sorry, this service is currently unavailable."); }
}
