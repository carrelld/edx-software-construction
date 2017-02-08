package warmup;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class Quadratic {

    /**
     * Find the integer roots of a quadratic equation, ax^2 + bx + c = 0.
     * @param a coefficient of x^2
     * @param b coefficient of x
     * @param c constant term.  Requires that a, b, and c are not ALL zero.
     * @return all integers x such that ax^2 + bx + c = 0.
     */
    public static Set<Integer> roots(int a, int b, int c) {
        Set<Integer> roots = new HashSet<Integer>();
        
        BigInteger big4 = BigInteger.valueOf(4);
        BigInteger bigA = BigInteger.valueOf(a);
        BigInteger bigB = BigInteger.valueOf(b);
        BigInteger bigC = BigInteger.valueOf(c);
        
        BigInteger bigCalculationGroup1 = bigB.pow(2).subtract(big4.multiply(bigA).multiply(bigC)); // b*b - 4*a*c
        BigInteger bigCalculationGroup2 = bigA.multiply(BigInteger.valueOf(2)); // 2*a
        
        long calculationGroup1 = bigCalculationGroup1.longValue();
        long calculationGroup2 = bigCalculationGroup2.longValue();
        
        // can't divide by 0;
        if (calculationGroup2 == 0) {
            roots.add(-c / b);
        } else if (calculationGroup1 >= 0) { // can't sqrt(negative)
            int r1 = (int) ((-b + Math.sqrt(calculationGroup1)) / calculationGroup2);
            int r2 = (int) ((-b - Math.sqrt(calculationGroup1)) / calculationGroup2);
            // verify calculation
            if (isValidRoot(a, b, c, r1)) { roots.add(r1); }
            if (isValidRoot(a, b, c, r2)) { roots.add(r2); }

        }
        
        return roots;
        
    }

    private static boolean isValidRoot(int a, int b, int c, int x) {
        BigInteger bigA = BigInteger.valueOf(a);
        BigInteger bigB = BigInteger.valueOf(b);
        BigInteger bigC = BigInteger.valueOf(c);
        BigInteger bigX = BigInteger.valueOf(x);
        
        BigInteger result = bigA.multiply(bigX.pow(2)).add(bigB.multiply(bigX)).add(bigC); // ax^2 + bx + c
        return result.compareTo(BigInteger.ZERO) == 0;
    }

    /**
     * Main function of program.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("For the equation x^2 - 4x + 3 = 0, the possible solutions are:");
        Set<Integer> result = roots(1, -4, 3);
        System.out.println(result);
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
