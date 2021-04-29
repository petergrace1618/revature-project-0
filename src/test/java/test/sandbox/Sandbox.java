package test.sandbox;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Sandbox {
    public static void main(String[] args) {
        System.out.println(Double.parseDouble(null));
//        new Dummy().dummyMethod();
//        List<Integer> li = new ArrayList<>();
//        li.add(null);
//        System.out.print("Enter: ");
//        String s = getString(true);
//        System.out.println(s);
    }
}

class Dummy {
    void dummyMethod() {
        String c = this.getClass().getName();
        c = c.substring(c.lastIndexOf('.')+1);
        System.out.println(c);
    }
}