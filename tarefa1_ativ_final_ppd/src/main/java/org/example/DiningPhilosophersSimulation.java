package org.example;

/**
 * Classe principal para execução da simulação do Jantar dos Filósofos.
 * Configura o ambiente para demonstrar a condição de Deadlock.
 */
public class DiningPhilosophersSimulation {

    public static void main(String[] args) {
        System.out.println("=== Simulação: Jantar dos Filósofos (Demonstração de Deadlock) ===");
        System.out.println("Aviso: O programa foi desenhado para travar. Use CTRL+C para encerrar.\n");

        int numberOfPhilosophers = 5;
        Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
        Object[] forks = new Object[numberOfPhilosophers];

        // 1. Inicializa os garfos (Recursos Compartilhados)
        for (int i = 0; i < forks.length; i++) {
            forks[i] = new Object();
        }

        for (int i = 0; i < numberOfPhilosophers; i++) {
            Object leftFork = forks[i];
            Object rightFork = forks[(i + 1) % numberOfPhilosophers];


            philosophers[i] = new Philosopher("Filósofo " + (i + 1), leftFork, rightFork);


            new Thread(philosophers[i]).start();
        }
    }
}