package com.tckb.sandbox;

import com.tckb.util.Utility;
import java.util.concurrent.ForkJoinPool;

public class ForkJoinWorker {

    public static void main(String[] args) {

        // Check the number of available processors
        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println("No of processors: " + processors);

        int n = 50;

        FibonacciProblem bigProblem = new FibonacciProblem(n);

        FibonacciTask task = new FibonacciTask(bigProblem);
       
        ForkJoinPool pool = new ForkJoinPool(processors);

        long t = Utility.tic();


        pool.invoke(task);
        
        
        
        double time = Utility.toc(t);

        long result = task.result;
        System.out.println("Computed Result: " + result);
        System.out.println(time);
        System.out.println("Elapsed Time: " + Utility.toFormatedTimeString((int)(time * 1000)));

    }
}
