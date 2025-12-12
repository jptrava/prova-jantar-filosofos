package org.example;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Representa um filósofo que utiliza um Semáforo para controlar
 * a entrada na zona de disputa pelos garfos.
 */
public class PhilosopherSemaphore implements Runnable {
    private final String name;
    private final Object leftFork;
    private final Object rightFork;
    private final Semaphore diningHall; // O "Garçom" que limita a concorrência

    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss.SSS");
    private int eatCount = 0;
    private volatile boolean running = true;

    public PhilosopherSemaphore(String name, Object leftFork, Object rightFork, Semaphore diningHall) {
        this.name = name;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.diningHall = diningHall;
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
        // Simula tempo aleatório entre 1 e 3 segundos
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

                // 2. Tentar permissão para disputar garfos
                // Se houver 4 filósofos tentando/comendo, este comando BLOQUEIA a thread aqui.
                log("Sente FOME. Aguardando permissão do semáforo...");
                diningHall.acquire();

                try {

                    log("Conseguiu permissão! Tentando pegar o garfo ESQUERDO.");

                    synchronized (leftFork) {
                        log("Pegou garfo ESQUERDO. Tentando pegar o DIREITO.");
                        Thread.sleep(100);

                        synchronized (rightFork) {
                            // 3. Comer (Sucesso na aquisição de todos os recursos)
                            log("Pegou garfo DIREITO. Começou a COMER.");
                            performAction("Comendo");
                            eatCount++;
                        }
                    }
                } finally {
                    // 4. Liberar permissão (Crucial: deve ser no finally)
                    diningHall.release();
                }
            }
        } catch (InterruptedException e) {
            log("Foi interrompido.");
            Thread.currentThread().interrupt();
        }
    }
}