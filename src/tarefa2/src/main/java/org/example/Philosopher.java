package org.example;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class Philosopher implements Runnable {
    private final String name;
    private final Object firstFork;
    private final Object secondFork;

    private final String firstForkLabel;
    private final String secondForkLabel;

    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss.SSS");
    private int eatCount = 0;
    private boolean running = true;

    /**
     * Construtor flexível que define a ordem de pegada dos garfos.
     */
    public Philosopher(String name, Object firstFork, String firstLabel, Object secondFork, String secondLabel) {
        this.name = name;
        this.firstFork = firstFork;
        this.firstForkLabel = firstLabel;
        this.secondFork = secondFork;
        this.secondForkLabel = secondLabel;
    }

    public int getEatCount() {
        return eatCount;
    }

    public void stop() {
        this.running = false;
    }

    private void log(String message) {
        System.out.printf("[%s] %s: %s%n", timeFormatter.format(new Date()), name, message);
    }

    private void performAction(String action) throws InterruptedException {

        int duration = ThreadLocalRandom.current().nextInt(1000, 3001);
        Thread.sleep(duration);
    }

    @Override
    public void run() {
        try {
            while (running) {
                // 1. Pensar
                log("Começou a PENSAR.");
                performAction("Pensando");

                // 2. Tentar pegar o PRIMEIRO garfo
                log("Está com FOME e tentando pegar o garfo " + firstForkLabel + ".");

                synchronized (firstFork) {
                    log("Pegou o garfo " + firstForkLabel + ". Tentando pegar o " + secondForkLabel + ".");


                    Thread.sleep(100);

                    synchronized (secondFork) {

                        log("Pegou o garfo " + secondForkLabel + ". Começou a COMER.");
                        performAction("Comendo");

                        eatCount++; // Contabiliza a refeição
                    }
                }

                // 4. Soltar garfos
                log("Terminou de comer. Soltou os garfos e voltou a pensar.");
            }
        } catch (InterruptedException e) {
            log("Foi interrompido.");
            Thread.currentThread().interrupt();
        }
    }
}