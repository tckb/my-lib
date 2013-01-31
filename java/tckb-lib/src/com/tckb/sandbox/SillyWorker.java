package com.tckb.sandbox;

import com.tckb.util.Utility;

public class SillyWorker {

    public static void main(String[] args) throws Exception {

        int n = 45;
        FibonacciProblem bigProblem = new FibonacciProblem(n);
        long t = Utility.tic();

        long result = bigProblem.solve();
        double time = Utility.toc(t);

        System.out.println("Computing Fib number: " + n);
        System.out.println("Computed Result: " + result);
        System.out.println("Elapsed Time: " + Utility.toFormatedTimeString((int) (time * 1000)) + " sec");

    }
}
