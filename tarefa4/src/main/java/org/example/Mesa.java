package org.example;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Monitor que gerencia os garfos e garante justiça (Fairness).
 */
public class Mesa {
    // Estados possíveis de um filósofo
    private static final int PENSANDO = 0;
    private static final int FAMINTO = 1;
    private static final int COMENDO = 2;

    private final int numFilosofos;
    private final int[] estados;
    // Fila para garantir ordem de chegada (Fairness)
    private final Queue<Integer> filaDeEspera;

    public Mesa(int numFilosofos) {
        this.numFilosofos = numFilosofos;
        this.estados = new int[numFilosofos];
        this.filaDeEspera = new LinkedList<>();

        for (int i = 0; i < numFilosofos; i++) {
            estados[i] = PENSANDO;
        }
    }

    /**
     * Método bloqueante para pegar garfos.
     * Só retorna quando o filósofo consegue comer.
     */
    public synchronized void pegarGarfos(int id) throws InterruptedException {
        estados[id] = FAMINTO;
        filaDeEspera.add(id);


        while (!podeComer(id)) {
            wait();
        }


        filaDeEspera.remove(Integer.valueOf(id));
        estados[id] = COMENDO;
    }

    /**
     * Libera os garfos e notifica os colegas.
     */
    public synchronized void soltarGarfos(int id) {
        estados[id] = PENSANDO;
        // Notifica TODOS para que acordem e verifiquem se agora podem comer
        notifyAll();
    }

    /**
     * Verifica se os vizinhos não estão comendo E se não estou furando fila.
     */
    private boolean podeComer(int id) {
        int vizinhoEsq = (id + numFilosofos - 1) % numFilosofos;
        int vizinhoDir = (id + 1) % numFilosofos;

        // 1. Verificação Física: Vizinhos estão comendo?
        if (estados[vizinhoEsq] == COMENDO || estados[vizinhoDir] == COMENDO) {
            return false;
        }

        // 2. Verificação de Justiça (Fairness):
        // Percorre a fila para ver se algum vizinho pediu antes de mim.
        // Se meu vizinho está na fila ANTES de mim, eu devo esperar (ceder a vez).
        for (Integer filId : filaDeEspera) {
            if (filId == id) {
                // Cheguei em mim mesmo na fila sem encontrar vizinhos antes.
                // Significa que sou o mais antigo entre meus vizinhos. Posso comer.
                return true;
            }
            if (filId == vizinhoEsq || filId == vizinhoDir) {
                // Opa! Um vizinho meu pediu antes. Devo esperar ele comer.
                return false;
            }
        }

        return true;
    }
}