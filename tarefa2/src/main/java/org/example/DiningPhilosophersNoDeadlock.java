package org.example;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DiningPhilosophersNoDeadlock {

    public static void main(String[] args) {
        System.out.println("=== Solução Jantar dos Filósofos: Prevenção de Deadlock ===");
        System.out.println("Executando por 2 minutos...\n");

        int numberOfPhilosophers = 5;
        Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
        Object[] forks = new Object[numberOfPhilosophers];

        // Inicializa garfos
        for (int i = 0; i < forks.length; i++) {
            forks[i] = new Object();
        }

        // Cria e inicia os filósofos
        for (int i = 0; i < numberOfPhilosophers; i++) {
            Object leftFork = forks[i];
            Object rightFork = forks[(i + 1) % numberOfPhilosophers];

            // --- LÓGICA DE PREVENÇÃO DE DEADLOCK ---
            if (i == numberOfPhilosophers - 1) {

                philosophers[i] = new Philosopher("Filósofo " + (i + 1), rightFork, "DIREITO", leftFork, "ESQUERDO");
            } else {

                philosophers[i] = new Philosopher("Filósofo " + (i + 1), leftFork, "ESQUERDO", rightFork, "DIREITO");
            }

            new Thread(philosophers[i]).start();
        }

        try {
            Thread.sleep(120 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("\n=== Fim da Simulação (2 Minutos) ===");
        System.out.println("=== Estatísticas de Refeição ===");

        int totalRefeicoes = 0;
        for (Philosopher p : philosophers) {
            p.stop(); // Para o loop
            System.out.printf("%s comeu %d vezes.%n", p.getClass().getSimpleName(), p.getEatCount()); // Nome simples na saida

        }

        // Refazendo o print para ficar bonito e explícito:
        for (int i = 0; i < philosophers.length; i++) {
            System.out.printf("Filósofo %d comeu: %d vezes%n", (i+1), philosophers[i].getEatCount());
            totalRefeicoes += philosophers[i].getEatCount();
        }

        System.out.println("Total de refeições servidas: " + totalRefeicoes);
        System.exit(0);
    }
}
