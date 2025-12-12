import org.example.Philosopher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class DiningPhilosophersTest {

    @Test
    @DisplayName("Deve executar sem Deadlock e garantir que todos comam")
    public void testDeadlockPreventionAndLiveness() throws InterruptedException {
        // --- Configuração (Setup) ---
        int numberOfPhilosophers = 5;
        long executionTimeMillis = 15000; // Duração do teste

        Philosopher[] philosophers = new Philosopher[numberOfPhilosophers];
        Object[] forks = new Object[numberOfPhilosophers];
        List<Thread> threads = new ArrayList<>();

        // Inicializa recursos
        for (int i = 0; i < forks.length; i++) {
            forks[i] = new Object();
        }

        // Inicializa filósofos aplicando a Solução de Hierarquia de Recursos
        for (int i = 0; i < numberOfPhilosophers; i++) {
            Object leftFork = forks[i];
            Object rightFork = forks[(i + 1) % numberOfPhilosophers];

            // O último filósofo pega os garfos na ordem inversa para quebrar a espera circular
            if (i == numberOfPhilosophers - 1) {
                philosophers[i] = new Philosopher("Filósofo " + (i + 1), rightFork, "DIREITO", leftFork, "ESQUERDO");
            } else {
                philosophers[i] = new Philosopher("Filósofo " + (i + 1), leftFork, "ESQUERDO", rightFork, "DIREITO");
            }

            threads.add(new Thread(philosophers[i]));
        }

        // --- Execução ---
        System.out.println("Iniciando teste de concorrência...");

        for (Thread t : threads) {
            t.start();
        }

        // Aguarda a execução da simulação
        Thread.sleep(executionTimeMillis);

        // --- Encerramento (Teardown) ---
        for (Philosopher p : philosophers) {
            p.stop();
        }

        // Aguarda as threads finalizarem
        for (Thread t : threads) {
            t.join(2000);
        }

        // --- Validação (Asserts) ---
        int totalMeals = 0;

        for (int i = 0; i < philosophers.length; i++) {
            int meals = philosophers[i].getEatCount();
            totalMeals += meals;
            System.out.printf("Filósofo %d comeu %d vezes.%n", (i + 1), meals);

            // Verifica Liveness: Se for 0, houve Deadlock ou Starvation
            assertTrue(meals > 0, "Falha: O Filósofo " + (i + 1) + " não conseguiu comer.");
        }

        // Verifica Progresso Global
        assertTrue(totalMeals >= numberOfPhilosophers, "Falha: Throughput do sistema muito baixo.");

        System.out.println("Teste finalizado com sucesso. Total de refeições: " + totalMeals);
    }
}