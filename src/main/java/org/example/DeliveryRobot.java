package org.example;

import java.util.*;

public class DeliveryRobot {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void countNumberCommands() throws InterruptedException {
        List<Thread> threads1 = new ArrayList<>();
        List<Thread> threads2 = new ArrayList<>();

        String[] routes = new String[1000];
        for (int i = 0; i < routes.length; i++) {
            routes[i] = generateRoute("RLRFR", 100).concat(" ");
//            System.out.println(routes[i]);
        }

        for (String route : routes) {
            Thread thread1 = new Thread(() -> {
                int freqHitR = 0; //частота попадания символа

                for (int i = 0; i < route.length(); i++) {
                    char c = route.charAt(i);

                    if (c == 'R') {
                        freqHitR++;
                    } else if (freqHitR != 0) {

                        if (sizeToFreq.containsKey(freqHitR)) {
                            int newCount = sizeToFreq.get(freqHitR) + 1; //увеличиваем кол-во частот
                            synchronized (sizeToFreq) {
                                sizeToFreq.put(freqHitR, newCount);
                                sizeToFreq.notify();
                            }
                        } else {
                            synchronized (sizeToFreq) {
                                sizeToFreq.put(freqHitR, 1); //создаем новую частоту
                                sizeToFreq.notify();
                            }
                        }
                        freqHitR = 0;
                    }
                }
            });
            threads1.add(thread1);
            thread1.start();

            Thread thread2 = new Thread(() -> {
                synchronized (sizeToFreq) {
                    while (!Thread.interrupted()) {
                        try {
                            sizeToFreq.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    int currentFreqLeader = Collections.max(sizeToFreq.entrySet(), Map.Entry.comparingByValue()).getKey();
                    System.out.printf("Текущий лидер среди частот %d (встретиласть %d раз)\n", currentFreqLeader, sizeToFreq.get(currentFreqLeader));
                }
            });
            threads2.add(thread2);
            thread2.start();

            for (Thread thread : threads1) {
                thread.join();
                thread2.interrupt();
            }
            for (Thread thread : threads2) {
                thread.join();
            }
        }
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
