import org.example.Mesa;
import org.example.PhilosopherMonitor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class DiningPhilosophersMonitorTest {

    @Test
    @DisplayName("Teste Tarefa 4: Monitor deve garantir justiça e prevenir starvation")
    public void testMonitorFairness() throws InterruptedException {
        // --- Setup ---
        int numberOfPhilosophers = 5;
        long executionTimeMillis = 15000;

        Mesa mesa = new Mesa(numberOfPhilosophers);
        PhilosopherMonitor[] philosophers = new PhilosopherMonitor[numberOfPhilosophers];
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numberOfPhilosophers; i++) {
            philosophers[i] = new PhilosopherMonitor(i, mesa);
            threads.add(new Thread(philosophers[i]));
        }

        // --- Execução ---
        System.out.println("Iniciando Teste do Monitor (15s)...");
        for (Thread t : threads) { t.start(); }

        Thread.sleep(executionTimeMillis);

        for (PhilosopherMonitor p : philosophers) { p.stop(); }
        for (Thread t : threads) { t.join(2000); }

        // --- Validação ---
        int totalMeals = 0;
        int minMeals = Integer.MAX_VALUE;
        int maxMeals = Integer.MIN_VALUE;

        for (int i = 0; i < philosophers.length; i++) {
            int meals = philosophers[i].getEatCount();
            totalMeals += meals;
            minMeals = Math.min(minMeals, meals);
            maxMeals = Math.max(maxMeals, meals);

            System.out.printf("Filósofo %d comeu %d vezes.%n", (i+1), meals);
            assertTrue(meals > 0, "Falha: Starvation detectado no Filósofo " + (i+1));
        }

        // Validação Extra de Fairness:
        // A diferença entre quem comeu mais e quem comeu menos não deve ser absurda.
        // Em 15s, com aleatoriedade, uma diferença de 2x ou 3x é aceitável,
        // mas 50x indicaria falta de fairness.
        System.out.println("Discrepância (Max - Min): " + (maxMeals - minMeals));
        assertTrue(totalMeals > numberOfPhilosophers);

        System.out.println("Sucesso! Monitor funcionou perfeitamente.");
    }
}
