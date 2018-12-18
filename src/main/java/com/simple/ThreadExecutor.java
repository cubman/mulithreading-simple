package com.simple;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class ThreadExecutor {
    private static final int MAX_THREAD_COUNT = 3;
    private static final int PORT = 8080;

    int maxValue;
    ServerSocket serverSocket;

    public ThreadExecutor(int maxValue) throws IOException {
        this.maxValue = maxValue;

        serverSocket = new ServerSocket(PORT);


        if (serverSocket == null) {
            throw new NullPointerException("serverSocket is null");
        }
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
        }

        return result;
    }

    private List<Future<List<Integer>>> cleverDivide(int maxValue) throws IOException {
        int threadCount = MAX_THREAD_COUNT; // maxValue / 30 == 0 ? 1 : Math.min(MAX_THREAD_COUNT, maxValue / 30);
        ExecutorService service = Executors.newFixedThreadPool(threadCount);

        List<Future<List<Integer>>> futures = new ArrayList<>();
        int div = maxValue / threadCount;

        for (int i = 0; i < threadCount; ++i) {
            futures.add(service.submit(new Executor(serverSocket.accept(), Math.max(2, div * i + 1),
                    i == threadCount - 1 ? maxValue : Math.min(maxValue, div * (i + 1)))));

        }

        service.shutdown();

        return futures;
    }

    private static class Executor implements Callable<List<Integer>> {
        int from, to;
        Socket socket;

        public Executor(Socket socket, int from, int to) {
            this.socket = socket;
            this.from = from;
            this.to = to;
        }

        public List<Integer> call() {
            String resList = null;
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            ) {

                String text = String.format("f:%d:t:%d\r\n", from, to);

                out.write(text);
                out.flush(); // выталкиваем все из буфера
                resList = in.readLine();
                System.out.println("В промежутке " + text + "получено" + resList);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return fromStringToList(resList);
        }


        private List<Integer> fromStringToList(String listString) {
            List<Integer> result = new ArrayList<>();

            String[] split = listString.substring(1, listString.length() - 1).split(", ");
            for (String s : split) {
                result.add(Integer.valueOf(s));
            }

            return result;
        }
    }

    List<Integer> primes() {
        int i = 0;
        int num = 0;

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

    public static void main(String[] args) throws Exception {
        int maxValue = 5000;
        ThreadExecutor threadExecutor = new ThreadExecutor(maxValue);
        Date start = new Date();
        List<Integer> cleverPrimes = threadExecutor.run();
        System.out.println("Время работы: " + (System.currentTimeMillis() - start.getTime()) + " мск");
        start = new Date();
        List<Integer> silyPrimes = threadExecutor.primes(); // не многопоточный вариант
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
