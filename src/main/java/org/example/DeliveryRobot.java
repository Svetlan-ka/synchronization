package org.example;

import java.util.*;
import java.util.concurrent.Callable;

public class DeliveryRobot {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void countNumberCommands() throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        String[] routes = new String[1000];
        for (int i = 0; i < routes.length; i++) {
            routes[i] = generateRoute("RLRFR", 100).concat(" ");
        }

        for (String route : routes) {
            Thread thread = new Thread(() -> {
                int freqHitR = 0; //частота попадания символа

                for (int i = 0; i < route.length(); i++) {
                    char c = route.charAt(i);

                    if (c == 'R') {
                        freqHitR++;
                    } else if (freqHitR != 0) {
                        synchronized (sizeToFreq) {   //ПРОБЛЕМА ЗДЕСЬ
                            if (sizeToFreq.containsKey(freqHitR)) {
                                int newCount = sizeToFreq.get(freqHitR) + 1; //увеличиваем кол-во частот
                                sizeToFreq.put(freqHitR, newCount);
                            } else {
                                sizeToFreq.put(freqHitR, 1); //создаем новую частоту
                            }
                            freqHitR = 0;
                        }
                    }
                }
            });
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        printResult();
    }

    public static void printResult() {
        int maxCountFreq = Collections.max(sizeToFreq.entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.printf("Самое частое количество повторений %d (встретилось %d раз)\n", maxCountFreq, sizeToFreq.get(maxCountFreq));
        sizeToFreq.remove(maxCountFreq);
        System.out.println("Другие размеры:");

        for (Map.Entry<Integer, Integer> value : sizeToFreq.entrySet()) {
            System.out.printf("-  %d (%d раз)\n", value.getKey(), value.getValue());
        }
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}
