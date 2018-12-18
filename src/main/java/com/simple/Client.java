package com.simple;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {
    public static void main(String[] args) {

        int cnt = 0;
        while (true) {
            try (Socket socket = new Socket(args[0], Integer.valueOf(args[1]));
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            ) {
                String serverWord = in.readLine();

                System.out.println(serverWord);

                String[] split = serverWord.split(":");
                List<Integer> result = new ArrayList<>();

                for (int i = Integer.valueOf(split[1]); i < Integer.valueOf(split[3]); ++i) {
                    if (Operation.ferma(i)) {
                        result.add(i);
                    }
                }

                String simpleString = result.toString();
                System.out.println("simples: " + simpleString);

                out.write(simpleString + "\r\n");
                out.flush();
                break;

            } catch (IOException e) {
                System.out.println(++cnt + "wait");
            }
        }
    }
}