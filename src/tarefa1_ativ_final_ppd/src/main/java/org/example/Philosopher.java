package org.example;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class Philosopher implements Runnable {
    private final String name;
    private final Object leftFork;
    private final Object rightFork;
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss.SSS");

    public Philosopher(String name, Object leftFork, Object rightFork) {
        this.name = name;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
    }

    private void log(String message) {
        System.out.printf("[%s] %s: %s%n", timeFormatter.format(new Date()), name, message);
    }

    private void performAction(int minMillis, int maxMillis) throws InterruptedException {
        int duration = ThreadLocalRandom.current().nextInt(minMillis, maxMillis + 1);
        Thread.sleep(duration);
    }

    @Override
    public void run() {
        try {
            while (true) {
                // --- Estado: PENSANDO ---
                // AJUSTE 1: Tempo de pensamento reduzido (0 a 100ms).
                // Isso faz com que todos queiram comer quase ao mesmo tempo.
                log("Começou a PENSAR.");
                performAction(0, 100);

                // --- Estado: COM FOME ---
                log("Está com FOME e tenta pegar o garfo ESQUERDO.");

                synchronized (leftFork) {
                    log("Pegou o garfo ESQUERDO. Tenta pegar o DIREITO.");


                    Thread.sleep(4000);

                    synchronized (rightFork) {
                        // --- Estado: COMENDO ---
                        log("Consegue pegar ambos os garfos e começa a COMER.");
                        performAction(1000, 2000);
                    }
                }

                // --- Estado: FINALIZADO ---
                log("Terminou de comer e solta os garfos.");
            }
        } catch (InterruptedException e) {
            log("Foi interrompido.");
            Thread.currentThread().interrupt();
        }
    }
}