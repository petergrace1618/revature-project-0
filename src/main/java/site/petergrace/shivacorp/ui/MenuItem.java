package site.petergrace.shivacorp.ui;

public class MenuItem {
    String text;
    Runnable action;

    public MenuItem(String text, Runnable action) {
        this.text = text;
        this.action = action;
    }

    public String getText() {
        return text;
    }

    public Runnable getAction() {
        return action;
    }

}
