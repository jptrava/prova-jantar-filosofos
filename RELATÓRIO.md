# Relatório Comparativo: Soluções para o Jantar dos Filósofos

## 1. Introdução
O problema do Jantar dos Filósofos é um estudo de caso clássico em sistemas operacionais e computação concorrente. Ele ilustra os desafios de sincronização, deadlock (impasse) e starvation (inanição) quando múltiplos processos competem por recursos limitados (garfos).

Este relatório apresenta uma análise comparativa de três estratégias implementadas para resolver o problema:
1.  **Tarefa 2:** Hierarquia de Recursos (Quebra de Simetria).
2.  **Tarefa 3:** Limitação de Concorrência via Semáforos (Solução do Garçom).
3.  **Tarefa 4:** Monitor Centralizado com Fila de Prioridade (Fairness).

## 2. Metodologia
Os testes foram executados em um ambiente Java multithreaded sob as seguintes condições controladas:

* **Duração do Teste:** 5 minutos (300 segundos) para cada solução.
* **População:** 5 Filósofos (Threads) e 5 Garfos (Recursos).
* **Carga de Trabalho:**
    * Tempo de Pensar: Aleatório entre 1s e 3s.
    * Tempo de Comer: Aleatório entre 1s e 3s.
* **Métricas Coletadas:** Contagem total de refeições e distribuição individual por filósofo.

## 3. Resultados Experimentais

Abaixo, apresentamos os dados coletados durante as execuções.

### 3.1. Tabela Resumo

| Métrica | Tarefa 2 (Hierarquia) | Tarefa 3 (Semáforos) | Tarefa 4 (Monitores) |
| :--- | :---: | :---: | :---: |
| **Total de Refeições** | **274** | 196 | 195 |
| **Throughput (Ref/min)** | **54.8** | 39.2 | 39.0 |
| **Média por Filósofo** | 54.8 | 39.2 | 39.0 |
| **Amplitude (Max - Min)** | 9 (51 a 60) | **2 (38 a 40)** | **2 (38 a 40)** |
| **Desvio Padrão** | 3.12 | 0.75 | 0.63 |

### 3.2. Detalhamento dos Dados

* **Tarefa 2 (Hierarquia):** O Filósofo 4 obteve 60 refeições, enquanto o Filósofo 1 obteve 51. Embora eficiente, houve uma variação notável de ~17% entre o mais e o menos servido.
* **Tarefa 3 (Semáforo):** Distribuição extremamente uniforme (entre 38 e 40 refeições). Houve uma redução de **28,5% no total de refeições** comparado à Tarefa 2.
* **Tarefa 4 (Monitor):** Resultados quase idênticos à Tarefa 3 em termos de volume e uniformidade, com uma ligeira vantagem matemática no desvio padrão (0.63 vs 0.75), indicando a precisão da fila de prioridade.

## 4. Análise Crítica

### 4.1. Prevenção de Deadlock
* **Hierarquia (T2):** Previne deadlock estruturalmente. Ao forçar o último filósofo a pegar os garfos na ordem inversa, a condição de "Espera Circular" de Coffman torna-se impossível.
* **Semáforo (T3):** Previne deadlock limitando a capacidade. Ao permitir apenas 4 filósofos na disputa por 5 garfos, garante-se matematicamente (Princípio da Casa dos Pombos) que pelo menos um terá acesso a dois garfos.
* **Monitor (T4):** Previne deadlock pela atomicidade. O estado do filósofo é gerenciado centralmente; ele não segura um recurso enquanto espera por outro (elimina "Hold and Wait").

### 4.2. Prevenção de Starvation (Inanição)
* **Hierarquia (T2): Não previne Starvation garantidamente.** Embora improvável com o agendador moderno do Java, é teoricamente possível que threads vizinhas "conspirem" e sejam sempre mais rápidas, deixando um filósofo específico esperando indefinidamente.
* **Semáforo (T3): Prevenção Parcial.** O semáforo do Java não garante a ordem de atendimento por padrão (Fairness não garantido). Um filósofo poderia ficar preso na fila do semáforo enquanto outros furam a fila, embora a limitação de capacidade reduza esse risco.
* **Monitor (T4): Prevenção Total e Determinística.** Esta é a única solução que implementou uma **Fila de Prioridade (FIFO)** explícita. O método `podeComer()` verifica se há vizinhos na fila de espera que chegaram antes. Isso garante matematicamente que todo pedido será eventualmente atendido.

### 4.3. Performance (Throughput) vs. Justiça (Fairness)
Os dados revelam um trade-off claro:
* **A Tarefa 2 foi a mais rápida (274 refeições).** Ela permite comportamento "ganancioso". Se os garfos estão livres, o filósofo pega, sem overhead de filas.
* **As Tarefas 3 e 4 foram mais lentas (~195 refeições).**
    * *T3:* Perde performance pela limitação artificial (o 5º filósofo é bloqueado mesmo se garfos estiverem livres).
    * *T4:* Perde performance pela burocracia da justiça (um filósofo espera se o vizinho pediu primeiro).

### 4.4. Complexidade de Implementação
1.  **Baixa - Tarefa 2:** Mudança trivial na lógica (`if/else`). Código limpo.
2.  **Média - Tarefa 3:** Requer objeto `Semaphore` externo.
3.  **Alta - Tarefa 4:** Exige classe `Mesa` dedicada, gestão manual de estados, `synchronized`, `wait/notify` e estrutura de dados (`Queue`).

## 5. Conclusão

Com base nos dados coletados e na análise teórica, concluímos:

1.  **Vencedor em Performance: Tarefa 2 (Hierarquia).** Ideal para sistemas onde a produtividade máxima é o objetivo e pequenas desigualdades são toleráveis. É a implementação mais leve para a CPU.
2.  **Vencedor em Equilíbrio: Tarefa 3 (Semáforo).** Oferece justiça excelente com complexidade razoável.
3.  **Vencedor em Determinismo: Tarefa 4 (Monitor).** Embora tenha performance similar à Tarefa 3, é a única que oferece garantias teóricas contra *starvation* via fila de prioridade. Recomendada para sistemas de tempo real ou missão crítica.

**Recomendação Final:** Para a maioria das aplicações generalistas, a **Solução de Hierarquia (Tarefa 2)** é a mais indicada devido ao seu throughput superior (+40%) e baixa complexidade.
