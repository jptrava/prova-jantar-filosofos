package org.example;
public class DiningPhilosophersMonitorSim {

    public static void main(String[] args) {
        System.out.println("=== Tarefa 4: Solução com Monitor e Justiça (Fairness) ===");
        System.out.println("Garantia: Sem Deadlock e Sem Starvation (Fila de Prioridade).\n");

        int numberOfPhilosophers = 5;

        Mesa mesa = new Mesa(numberOfPhilosophers);

        PhilosopherMonitor[] philosophers = new PhilosopherMonitor[numberOfPhilosophers];

        // Criação das threads
        for (int i = 0; i < numberOfPhilosophers; i++) {
            philosophers[i] = new PhilosopherMonitor(i, mesa);
            new Thread(philosophers[i]).start();
        }

        // Executa por 2 minutos
        try {
            System.out.println("Executando simulação por 120 segundos...");
            Thread.sleep(120 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Encerramento
        System.out.println("\n=== Fim da Simulação ===");
        int totalRefeicoes = 0;

        for (PhilosopherMonitor p : philosophers) {
            p.stop();
        }

        // Pausa para limpeza
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        for (int i = 0; i < numberOfPhilosophers; i++) {
            System.out.printf("Filósofo %d comeu %d vezes.%n", (i+1), philosophers[i].getEatCount());
            totalRefeicoes += philosophers[i].getEatCount();
        }

        System.out.println("Total de refeições servidas: " + totalRefeicoes);
        System.exit(0);
    }
}