package test.sandbox;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Sandbox {
    public static void main(String[] args) {
        new Dummy().dummyMethod();
        List<Integer> li = new ArrayList<>();
        li.add(null);
    }
}

class Dummy {
    void dummyMethod() {
        String c = this.getClass().getName();
        c = c.substring(c.lastIndexOf('.')+1);
        System.out.println(c);
    }
}