
import org.example.PhilosopherSemaphore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class DiningPhilosophersSemaphoreTest {

    @Test
    @DisplayName("Teste Tarefa 3: Semáforo deve prevenir deadlock e permitir fluxo contínuo")
    public void testSemaphoreStrategy() throws InterruptedException {
        // --- 1. CONFIGURAÇÃO (Arrange) ---
        int numberOfPhilosophers = 5;
        long executionTimeMillis = 15000;

        // O Semáforo limitador (4 permissões para 5 threads)
        Semaphore diningHall = new Semaphore(4);

        PhilosopherSemaphore[] philosophers = new PhilosopherSemaphore[numberOfPhilosophers];
        Object[] forks = new Object[numberOfPhilosophers];
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < forks.length; i++) { forks[i] = new Object(); }

        for (int i = 0; i < numberOfPhilosophers; i++) {
            Object leftFork = forks[i];
            Object rightFork = forks[(i + 1) % numberOfPhilosophers];

            philosophers[i] = new PhilosopherSemaphore("Filósofo " + (i+1), leftFork, rightFork, diningHall);
            threads.add(new Thread(philosophers[i]));
        }

        // --- 2. EXECUÇÃO (Act) ---
        System.out.println("Iniciando Teste com Semáforo (15s)...");
        for (Thread t : threads) { t.start(); }

        // Aguarda a simulação rodar
        Thread.sleep(executionTimeMillis);

        // Encerra threads
        for (PhilosopherSemaphore p : philosophers) { p.stop(); }
        for (Thread t : threads) { t.join(2000); }

        // --- 3. VALIDAÇÃO (Assert) ---
        int totalMeals = 0;
        for (int i = 0; i < philosophers.length; i++) {
            int meals = philosophers[i].getEatCount();
            totalMeals += meals;
            System.out.printf("Filósofo %d comeu %d vezes.%n", (i+1), meals);

            assertTrue(meals > 0, "Falha: Filósofo " + (i+1) + " não conseguiu comer nenhuma vez.");
        }

        // Verifica Throughput global
        assertTrue(totalMeals > numberOfPhilosophers, "O sistema deve ter um fluxo constante de refeições.");
        System.out.println("Sucesso! Total de refeições com Semáforo: " + totalMeals);
    }
}
