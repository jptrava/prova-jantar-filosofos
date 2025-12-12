# prova-jantar-filosofos

# Projeto: O Problema do Jantar dos Filósofos (Concorrência em Java)

Este repositório contém múltiplas implementações para o clássico problema de computação concorrente "Jantar dos Filósofos", demonstrando cenários de **Deadlock**, **Prevenção de Deadlock**, **Semáforos** e **Monitores** com prevenção de Starvation.

## 📂 Estrutura do Projeto

Abaixo está a organização dos arquivos fonte (`src`) e documentação:

```text
/
├── RELATORIO.md                      # Relatório Comparativo Técnico (Tarefas 2, 3 e 4)
├── README.md                         # Este arquivo (Documentação Principal)
│
├── src/
│   │   # --- Tarefa 1: Demonstração de Deadlock ---
│   ├── DiningPhilosophersSimulation.java   # Main: Simulação que trava (Deadlock)
│   ├── Philosopher.java                    # Thread do Filósofo (Lógica Básica)
│   │
│   │   # --- Tarefa 2: Solução Hierarquia de Recursos ---
│   ├── DiningPhilosophersNoDeadlock.java   # Main: Solução sem Deadlock (Inversão do último)
│   ├── DiningPhilosophersTest.java         # Teste JUnit para Tarefa 2
│   │
│   │   # --- Tarefa 3: Solução com Semáforos ---
│   ├── DiningPhilosophersSemaphoreSim.java # Main: Solução com limitação de concorrência
│   ├── PhilosopherSemaphore.java           # Filósofo adaptado para usar Semáforo
│   ├── DiningPhilosophersSemaphoreTest.java# Teste JUnit para Tarefa 3
│   │
│   │   # --- Tarefa 4: Solução com Monitores (Fairness) ---
│   ├── DiningPhilosophersMonitorSim.java   # Main: Solução Robusta com Monitor
│   ├── PhilosopherMonitor.java             # Filósofo adaptado para usar Monitor
│   ├── Mesa.java                           # Monitor Centralizado (Lógica de Fila/Justiça)
│   └── DiningPhilosophersMonitorTest.java  # Teste JUnit para Tarefa 4
## tarefa 1
# Simulação: Jantar dos Filósofos (Demonstração de Deadlock)

Este projeto implementa uma versão "ingênua" do problema clássico de computação concorrente proposto por Edsger Dijkstra. O objetivo é demonstrar explicitamente como a gestão incorreta de recursos compartilhados leva a um **Deadlock**.

## Estrutura do Projeto

* `DiningPhilosophersSimulation.java`: Ponto de entrada. Inicializa 5 garfos e 5 filósofos.
* `Philosopher.java`: A thread que representa o filósofo. Controla o ciclo de vida (Pensar -> Tentar Comer -> Comer -> Soltar).

## Como Executar

1.  Compile os arquivos:
    ```bash
    javac DiningPhilosophersSimulation.java Philosopher.java
    ```
2.  Execute a simulação:
    ```bash
    java DiningPhilosophersSimulation
    ```
3.  **Observação:** O programa executará normalmente por alguns segundos e então "congelará". Isso é o comportamento esperado. Pressione `CTRL+C` para encerrar.

## Análise do Deadlock

### O que acontece?
Após executar o programa, você notará nos logs que, eventualmente, todos os 5 filósofos exibirão a mensagem:
> *"Pegou o garfo ESQUERDO. Tenta pegar o DIREITO."*

Imediatamente após isso, o fluxo de logs cessa. O programa continua rodando, mas nenhuma thread progride.

### Por que ocorre?
Esta implementação satisfaz as quatro condições de Coffman necessárias para um Deadlock, com destaque para a **Espera Circular**:

1.  Todos os filósofos tentam pegar o garfo à sua **esquerda** ao mesmo tempo.
2.  Todos conseguem (pois todos estavam livres).
3.  Todos tentam pegar o garfo à sua **direita**.
4.  O garfo à direita do Filósofo 1 está ocupado (segurado pelo Filósofo 2).
5.  O garfo à direita do Filósofo 2 está ocupado (segurado pelo Filósofo 3).
6.  ...
7.  O garfo à direita do Filósofo 5 está ocupado (segurado pelo Filósofo 1).

Nenhum filósofo solta o garfo esquerdo até conseguir o direito e comer. Como todos estão esperando pelo vizinho, cria-se um ciclo de dependência infinito.

### Evidência Técnica no Código
Adicionamos propositalmente um `Thread.sleep(100)` entre a aquisição do primeiro e do segundo garfo em `Philosopher.java`. Isso simula uma latência de processamento que "garante" que todos os filósofos tenham tempo de pegar o primeiro garfo antes que qualquer um consiga pegar o segundo, forçando o deadlock a acontecer rapidamente para fins de avaliação.

## tarefa 2

# Tarefa 2: Jantar dos Filósofos com Prevenção de Deadlock

## Visão Geral
Esta implementação modifica a solução anterior para garantir que o sistema possa rodar indefinidamente sem atingir um estado de *Deadlock*. Utiliza-se a técnica de **Quebra de Simetria** na aquisição de recursos.

## Como Executar
1. Compile: `javac *.java`
2. Execute: `java DiningPhilosophersNoDeadlock`
3. O programa rodará por 120 segundos e exibirá estatísticas ao final.

## Explicação Técnica

### Por que essa solução previne Deadlock?
O deadlock na versão anterior ocorria devido à existência de um ciclo de dependências (Espera Circular):
`P1->F1->P2->F2->P3->F3->P4->F4->P5->F0->P1...`

Nesta solução, alteramos a ordem de aquisição para o último filósofo (Filósofo 5):
1. Filósofos 1 a 4 tentam pegar: **Esquerda** depois **Direita**.
2. Filósofo 5 tenta pegar: **Direita** depois **Esquerda**.

**Cenário de Resolução:**
Se todos sentirem fome ao mesmo tempo:
* Os Filósofos 1, 2, 3 e 4 pegam seus garfos esquerdos.
* O Filósofo 5 tentaria pegar seu garfo direito (que é o garfo 0, o esquerdo do Filósofo 1).
* Se o Filósofo 1 já pegou o garfo 0, o Filósofo 5 fica bloqueado esperando o primeiro recurso.
* Como o Filósofo 5 **não pegou** seu garfo esquerdo (garfo 4), este garfo continua livre.
* O Filósofo 4 pode pegar o garfo 4 (sua direita), comer e liberar seus recursos.
* Isso quebra o ciclo, permitindo que o sistema progrida.

### Comparação com a Tarefa 1
* **Tarefa 1 (Naive):** Resulta em deadlock em poucos segundos sob alta carga ou eventualmente em execução normal. Todos param (0 refeições após o travamento).
* **Tarefa 2 (Hierarquia):** O sistema continua fluindo. A soma total de refeições cresce linearmente com o tempo.

### Possibilidade de Starvation
Embora o Deadlock (travamento total) seja impossível, o **Starvation** (inanição) ainda é teoricamente possível, embora improvável com o agendador (scheduler) moderno do Java.

Como usamos `synchronized` (monitor locks), não há garantia de justiça (fairness) na fila de espera dos garfos. Se, por exemplo, os filósofos 1 e 3 forem extremamente rápidos e o sistema operacional sempre der preferência a eles, o filósofo 2 (que fica entre eles) pode nunca conseguir adquirir os dois garfos simultaneamente, pois sempre que um libera, o outro pega.

Para resolver starvation completamente, seria necessário usar `ReentrantLock` com a flag `fair=true`.

## Estatísticas de Execução 
<img width="484" height="298" alt="image" src="https://github.com/user-attachments/assets/045a8a65-26ae-4b8d-b103-1c01b7ba8a16" />


Observa-se uma distribuição razoavelmente uniforme, indicando que o starvation não ocorreu na prática durante o teste.

# Tarefa 3: Solução com Semáforos (Limitação de Concorrência)

## Visão Geral
Nesta implementação, todos os 5 filósofos (threads) estão presentes e ativos na simulação simultaneamente. No entanto, utilizamos um `Semaphore` para limitar a **concorrência na aquisição de recursos**. 

A regra imposta é: embora existam 5 filósofos sentados à mesa, apenas **4 permissões** são concedidas para a tentativa de pegar os garfos em um dado momento.

## Como Funciona a Solução
1.  **Semáforo Limitador:** Criamos um `java.util.concurrent.Semaphore` com **4 permissões** (tickets).
2.  **Entrada na Região de Disputa:** * Quando um filósofo sente fome, ele chama `semaphore.acquire()`.
    * Se houver permissão disponível, ele passa e tenta pegar os garfos (esquerdo depois direito).
    * Se 4 filósofos já estiverem tentando comer, o contador do semáforo estará em 0.
3.  **Bloqueio do Quinto Elemento:**
    * Nesse cenário de lotação máxima, o **5º filósofo** será bloqueado pelo Sistema Operacional no momento do `acquire()`.
    * Ele permanece ativo no sistema, mas aguarda passivamente até que uma permissão seja liberada.
4.  **Liberação:** Ao terminar de comer, o filósofo executa `semaphore.release()`, devolvendo o ticket e acordando quem estava na fila de espera.

## Por que previne Deadlock?
O Deadlock clássico neste problema ocorre quando **todos os 5** filósofos pegam o garfo esquerdo ao mesmo tempo e esperam pelo direito (Espera Circular).

Ao limitar a disputa a 4 participantes ativos pelo **Princípio da Casa dos Pombos**:
* No pior caso possível, 4 filósofos pegam o garfo esquerdo simultaneamente.
* Temos **5 Garfos Totais** e **4 Garfos Ocupados**.
* Logo: $5 - 4 = 1$ Garfo Livre.
* Esse garfo livre estará necessariamente à direita de um dos 4 filósofos que possuem permissão.
* Esse filósofo pegará o segundo garfo, comerá e liberará seus recursos, garantindo que o ciclo de dependência nunca se feche.

## Comparação de Desempenho
* **Tarefa 2 (Hierarquia/Inversão):** Resolve o deadlock alterando a lógica de um filósofo específico.
* **Tarefa 3 (Semáforo):** * **Vantagens:** Código simétrico (todos os filósofos executam a mesma lógica) e robustez matemática baseada em capacidade.
    * **Desvantagens:** Introduz um leve gargalo (overhead) pois um filósofo pode ser bloqueado pelo semáforo mesmo que seus vizinhos estejam pensando, caso outros pares distantes estejam comendo.
 
## estatística da execução

<img width="291" height="163" alt="image" src="https://github.com/user-attachments/assets/10b4dd07-3eac-42d5-bc02-ca26434e16ff" />



# Tarefa 4: Solução com Monitor e Garantia de Fairness

## Visão Geral
Esta implementação utiliza o padrão de projeto **Monitor** para centralizar o controle de acesso aos recursos (garfos). A classe `Mesa` encapsula todo o estado do sistema e utiliza métodos `synchronized` junto com `wait()` e `notifyAll()`.

Para atender ao requisito de **Fairness (Justiça)** e **Prioridade**, implementamos uma fila lógica de pedidos dentro do monitor.

## Como o Monitor Garante Fairness
A inovação desta implementação está no método `podeComer(int id)`. Diferente da implementação padrão (Tanenbaum) que apenas olha se os vizinhos estão comendo, nossa solução olha também para a **intenção**.

1.  **Fila de Chegada:** Quando um filósofo sente fome, ele entra em uma `Queue<Integer>`.
2.  **A Regra de Ouro:** Um filósofo só pode comer se:
    * Seus vizinhos **não estão comendo**.
    * **E** nenhum de seus vizinhos está na fila de espera **à sua frente**.
3.  **Resultado:** Isso impede a "conspiração dos vizinhos", onde o Filósofo 1 e 3 poderiam alternar turnos indefinidamente, deixando o Filósofo 2 (que está no meio) esperando para sempre. Se o Filósofo 2 pediu primeiro, o Filósofo 1 e 3 serão obrigados a esperar o 2 comer, mesmo que os garfos estejam livres para eles naquele instante.

## Prevenção de Deadlock e Starvation
* **Deadlock:** É prevenido porque a aquisição dos garfos é atômica dentro do Monitor (`synchronized`). O filósofo nunca segura um garfo enquanto espera pelo outro; ele ou pega os dois (muda estado para COMENDO) ou não pega nenhum e dorme (`wait`). Não há "Hold and Wait".
* **Starvation:** É prevenido pela Fila FIFO. É garantido que, eventualmente, qualquer filósofo chegará ao topo da prioridade em relação aos seus vizinhos e será servido.

## Trade-offs e Comparação
| Característica | Tarefa 2 (Hierarquia) | Tarefa 3 (Semáforo) | Tarefa 4 (Monitor + Fila) |
| :--- | :--- | :--- | :--- |
| **Complexidade** | Baixa | Média | Alta (Lógica de fila customizada) |
| **Fairness** | Aleatória (Sem garantia) | Boa (Semáforo FIFO) | **Perfeita** (Garantia determinística) |
| **Throughput** | Alto | Médio/Alto | Médio (Overhead de verificação da fila) |

**Conclusão:** A solução com Monitor é a mais robusta para sistemas críticos onde a justiça é obrigatória, embora introduza um pouco mais de complexidade de código e processamento (overhead de percorrer a fila e notifyAll).

## estatística da execução

<img width="594" height="212" alt="image" src="https://github.com/user-attachments/assets/d4a5ffd1-af77-4884-a737-03c5976b3109" />

