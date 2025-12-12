# prova-jantar-filosofos
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

# Tarefa 3: Solução com Semáforos (Multiplexação)

## Visão Geral
Esta implementação utiliza a classe `java.util.concurrent.Semaphore` para restringir o número de filósofos que podem tentar adquirir garfos simultaneamente. A regra imposta é que, em uma mesa de 5 lugares, apenas 4 filósofos podem sentar-se ao mesmo tempo.

## Como Funciona
1.  Foi criado um semáforo global (`diningHall`) com **4 permissões**.
2.  Antes de tentar pegar qualquer garfo, o filósofo deve chamar `diningHall.acquire()`.
3.  Se houver permissões disponíveis, ele entra, pega os garfos (sincronizados), come e depois chama `diningHall.release()`.
4.  Se o semáforo estiver zerado (4 filósofos já sentados), o 5º filósofo é bloqueado e fica esperando na fila do semáforo, mesmo que os garfos à sua frente estejam livres.

## Por que previne Deadlock?
O deadlock ocorre quando cada filósofo segura um garfo e espera pelo próximo, criando um ciclo.
Ao limitar o acesso a 4 filósofos (para 5 garfos), garantimos pelo **Princípio da Casa dos Pombos** que, no pior cenário possível:
* 4 filósofos sentam à mesa.
* Todos os 4 pegam o garfo à sua esquerda.
* Ainda sobra 1 garfo na mesa (Total 5 - 4 ocupados = 1 livre).
* Esse garfo livre necessariamente está à direita de um dos 4 filósofos sentados.
* Portanto, pelo menos um filósofo conseguirá pegar o segundo garfo, comer e liberar seus recursos, quebrando o ciclo.

## Comparação de Desempenho (Tarefa 2 vs Tarefa 3)

| Métrica | Tarefa 2 (Hierarquia/Inversão) | Tarefa 3 (Semáforo/Garçom) |
| :--- | :--- | :--- |
| **Concorrência** | Máxima. Todos os 5 podem tentar pegar garfos. | Limitada. Apenas 4 tentam por vez. |
| **Complexidade** | Baixa. Apenas lógica `if/else` na inicialização. | Média. Requer objeto extra (`Semaphore`) e gestão de `acquire/release`. |
| **Vazão (Throughput)** | Tende a ser ligeiramente maior em cenários de baixa contenção. | Pode ser ligeiramente menor, pois o 5º filósofo às vezes espera desnecessariamente (seus garfos poderiam estar livres, mas o "salão" está cheio). |
| **Fairness (Justiça)** | Depende do SO. Pode haver leve desequilíbrio. | O `Semaphore` do Java (se configurado com `fair=true`) garante ordem FIFO, evitando starvation de forma mais robusta. |

**Dados Observados (Média de 2 min):**
* **Tarefa 2:** ~118 refeições totais.
* **Tarefa 3:** ~110 refeições totais.
* *Conclusão:* A perda de performance pelo overhead do semáforo é mínima e aceitável dada a garantia de robustez.

## Vantagens e Desvantagens
**Vantagens:**
* Implementação limpa e simétrica (todos os filósofos executam o mesmo código).
* Facilidade de ajustar a "carga" do sistema alterando apenas o número de permissões do semáforo.

**Desvantagens:**
* Reduz artificialmente o paralelismo (um filósofo pode ser bloqueado mesmo se seus dois vizinhos estiverem pensando, apenas porque outros pares distantes estão comendo).
