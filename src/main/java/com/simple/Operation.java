package com.simple;

import java.util.Random;

public class Operation {
    private static final int MAGIC_NUMBER = 100;
    private static Random random = new Random(0);

    public static boolean ferma(long number) {
        if (number == 2) {
            return true;
        }
        if (number % 2 == 0) {
            return false;
        }

        for (int i = 0; i < MAGIC_NUMBER; ++i) {
            long randomNumber = (Math.abs(random.nextLong()) % (number - 2)) + 2;

            if (gcd(randomNumber, number) != 1 || power(randomNumber, number - 1, number) != 1) {
                return false;
            }
        }
        return true;
    }

    private static long power(long a, long pow, long mod) {
        long res = 1;
        for (int i = 0; i < pow; ++i) {
            res = (res * a) % mod;
        }

        return res;
    }

    private static long gcd(long a, long b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }
}
