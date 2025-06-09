package com.example.abe.lsss;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class LSSSShareCalculator {

    public static List<BigInteger> computeLambdas(BigInteger[][] M, BigInteger[] v) {
        List<BigInteger> lambdas = new ArrayList<>();
        for (int i = 0; i < M.length; i++) {
            BigInteger lambda = BigInteger.ZERO;
            for (int j = 0; j < M[i].length; j++) {
                lambda = lambda.add(M[i][j].multiply(v[j]));
            }
            lambdas.add(lambda);
        }
        return lambdas;
    }

    public static void main(String[] args) {
        BigInteger[][] M = {
            {BigInteger.ONE, BigInteger.ZERO, BigInteger.ZERO},
            {BigInteger.ONE, BigInteger.ONE, BigInteger.ZERO},
            {BigInteger.ONE, BigInteger.ZERO, BigInteger.ONE}
        };

        BigInteger s = new BigInteger("123456789");
        BigInteger r1 = new BigInteger("987654321");
        BigInteger r2 = new BigInteger("111111111");
        BigInteger[] v = {s, r1, r2};

        List<BigInteger> lambdas = computeLambdas(M, v);
        for (int i = 0; i < lambdas.size(); i++) {
            System.out.println("λ[" + i + "] = " + lambdas.get(i));
        }
    }
}
