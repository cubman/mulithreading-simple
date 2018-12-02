package com.simple;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ThreadExecutor {
    private static final int MAX_THREAD_COUNT = 5;

    int maxValue;

    public ThreadExecutor(int maxValue) {
        this.maxValue = maxValue;
    }

    List<Integer> run() {
        final List<Integer> result = new ArrayList<>();
        try {
            cleverDivide(maxValue).stream().forEach(listFuture -> {
                try {
                    result.addAll(listFuture.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e);
        }

        return result;
    }

    private List<Future<List<Integer>>> cleverDivide(int maxValue) {
        int threadCount = maxValue / 30 == 0 ? 1 : Math.min(MAX_THREAD_COUNT, maxValue / 30);
        ExecutorService service = Executors.newFixedThreadPool(threadCount);

        List<Future<List<Integer>>> futures = new ArrayList<>();
        int div = maxValue / threadCount;

        for (int i = 0; i <= threadCount; ++i) {
            futures.add(service.submit(new Executor(Math.max(2, div * i + 1), Math.min(maxValue, div * (i + 1)))));
        }

        service.shutdown();

        return futures;
    }

    private class Executor implements Callable<List<Integer>> {
        int from, to;

        public Executor(int from, int to) {
            this.from = from;
            this.to = to;
        }

        public List<Integer> call() throws Exception {
            List<Integer> result = new ArrayList<>();

            for (int i = from; i < to; ++i) {
                if (Operation.ferma(i)) {
                    result.add(i);
                }
            }
            return result;
        }
    }

    List<Integer> primes() {
        int i = 0;
        int num = 0;
        String primeNumbers = "";

        List<Integer> list = new ArrayList<>();

        //from here
        for (i = 1; i <= maxValue; i++) {
            int counter = 0;
            for (num = i; num >= 1; num--) {
                if (i % num == 0) {
                    counter++;
                }
            }

            if (counter == 2) {
                list.add(i);
            }
        }

        return list;
    }

    public static void main(String[] args) {

        ThreadExecutor threadExecutor = new ThreadExecutor(1000);
        Date start = new Date();
        List<Integer> cleverPrimes = threadExecutor.run();
        System.out.println("Время работы: " + (System.currentTimeMillis() - start.getTime()) + " мск");
        start = new Date();
        List<Integer> silyPrimes = threadExecutor.primes();
        System.out.println("Время работы: " + (System.currentTimeMillis() - start.getTime()) + " мск");

        if (cleverPrimes.size() != silyPrimes.size()) {
            System.out.println("Размер массивов разный");

            return;
        }

        for (int i = 0; i < cleverPrimes.size(); ++i) {
            if (!silyPrimes.contains(cleverPrimes.get(i))) {
                System.out.println(i + ") Не найдено: " + cleverPrimes.get(i));
            }
        }
    }
}
