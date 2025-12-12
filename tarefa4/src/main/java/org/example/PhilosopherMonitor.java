package org.example;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class PhilosopherMonitor implements Runnable {
    private final int id;
    private final Mesa mesa;
    private final String name;
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss.SSS");

    private int eatCount = 0;
    private volatile boolean running = true;

    public PhilosopherMonitor(int id, Mesa mesa) {
        this.id = id;
        this.mesa = mesa;
        this.name = "Filósofo " + (id + 1);
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

                // 2. Solicitar Garfos ao Monitor
                log("Está com FOME. Solicitando garfos à Mesa...");
                mesa.pegarGarfos(id);

                // 3. Comer (Se o método acima retornou, é garantido que estou comendo)
                log("Conseguiu os garfos e começou a COMER.");
                performAction("Comendo");
                eatCount++;

                // 4. Devolver Garfos
                log("Terminou de comer. Soltando garfos.");
                mesa.soltarGarfos(id);
            }
        } catch (InterruptedException e) {
            log("Foi interrompido.");
            Thread.currentThread().interrupt();
        }
    }
}
