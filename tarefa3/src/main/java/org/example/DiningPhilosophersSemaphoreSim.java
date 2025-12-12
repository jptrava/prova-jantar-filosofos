package org.example;

import org.example.PhilosopherSemaphore;

import java.util.concurrent.Semaphore;

public class DiningPhilosophersSemaphoreSim {

    public static void main(String[] args) {
        System.out.println("=== Tarefa 3: Jantar dos Filósofos com Semáforo ===");
        System.out.println("Cenário: 5 Filósofos presentes, Semáforo com 4 Permissões.\n");

        int numberOfPhilosophers = 5;

        // CONFIGURAÇÃO CRÍTICA:
        // Criamos 5 filósofos, mas o semáforo só tem 4 permissões.
        // Isso garante que sempre haverá pelo menos 1 garfo livre na mesa.
        Semaphore diningHall = new Semaphore(4);

        PhilosopherSemaphore[] philosophers = new PhilosopherSemaphore[numberOfPhilosophers];
        Object[] forks = new Object[numberOfPhilosophers];

        // Inicializa garfos
        for (int i = 0; i < forks.length; i++) {
            forks[i] = new Object();
        }

        // Inicializa filósofos
        for (int i = 0; i < numberOfPhilosophers; i++) {
            Object leftFork = forks[i];
            Object rightFork = forks[(i + 1) % numberOfPhilosophers];


            philosophers[i] = new PhilosopherSemaphore(
                    "Filósofo " + (i + 1),
                    leftFork,
                    rightFork,
                    diningHall
            );

            new Thread(philosophers[i]).start();
        }

        // Executa a simulação por 2 minutos (120 segundos)
        try {
            System.out.println("Executando simulação por 120 segundos...");
            Thread.sleep(120 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Encerramento
        System.out.println("\n=== Fim da Simulação ===");


        int totalMeals = 0;
        for (PhilosopherSemaphore p : philosophers) {
            p.stop();
        }


        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        for (int i = 0; i < philosophers.length; i++) {
            System.out.printf("Filósofo %d comeu %d vezes.%n", (i+1), philosophers[i].getEatCount());
            totalMeals += philosophers[i].getEatCount();
        }

        System.out.println("Total de refeições servidas: " + totalMeals);
        System.exit(0);
    }
}