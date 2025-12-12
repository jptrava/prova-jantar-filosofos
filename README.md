# prova-jantar-filosofos

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
