# 🛡️ L2JLucera2 — Modern High-Performance Interlude Server

[![Java 25](https://img.shields.io/badge/Java-25%20LTS-orange.svg)](https://www.oracle.com/java/)
[![GraalVM Native / PGO](https://img.shields.io/badge/GraalVM-PGO%20--O3-red.svg)](https://www.graalvm.org/)
[![Netty](https://img.shields.io/badge/Network-Netty%20Zero--Alloc-blue.svg)](https://netty.io/)
[![Disruptor](https://img.shields.io/badge/Concurrency-LMAX%20Disruptor-green.svg)](https://lmax-exchange.github.io/disruptor/)
[![Tests](https://img.shields.io/badge/Tests-176%20Passing%20(100%25)-brightgreen.svg)](docs/baseline/BASELINE_TESTS_AND_METRICS.md)
[![License](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE.txt)

> **L2JLucera2** é uma evolução arquitetural profunda do clássico emulador Lineage II Interlude (Chronicle 6). O projeto foi reestruturado sob os pilares de **Mechanical Sympathy**, **Zero-GC**, auto-vetorização **SIMD**, concorrência **Lock-Free** e instrumentação moderna de microsserviços para suportar milhares de jogadores simultâneos com estabilidade e latência ultra-baixa.

---

## ⚡ Destaques e Diferenciais em Relação à Lucera Original

### ☕ 1. Runtime e Eficiência de Memória (Java 25 LTS)
* **Java 25 LTS + GraalVM Native / PGO (-O3):** Pipeline de compilação moderna com treinamento de JIT e boot nativo em ~1.6s.
* **Compact Object Headers (`-XX:+UseCompactObjectHeaders`):** Redução do overhead de cabeçalho de objetos no heap de 16 para 8 bytes em todos os tipos.
* **G1 Periodic GC:** Coleta preditiva sem travar o loop de jogo de 120 Hz.
* **Paperdoll Flattening:** O inventário no pacote `UserInfo` foi linearizado de `int[17][3]` para um array primitivo contíguo `int[51]`, economizando 17 alocações de sub-arrays por pacote emitido.
* **Scheduler Idle Purge:** O `SteppingRunnableQueueManager` agora expurga tarefas canceladas de forma ativa, impedindo vazamentos lentos de memória ao longo de semanas de uptime.

### 🚀 2. Rede de Alta Velocidade (Netty Zero-Alloc & LMAX Disruptor)
* **Zero-Alloc Packet Pipeline:** Codificação e decodificação reescritas sobre `ByteBuf` pooled direto do Netty, eliminando cópias intermediárias de arrays de bytes e boxing de primitivos.
* **`SendablePacketWriteTo`:** Os pacotes server-to-client escrevem diretamente no buffer do canal de saída sem buffers intermediários.
* **LMAX Disruptor 100k Warmup:** Aquecimento forçado de 100.000 iterações em runtime antes da abertura das portas de rede para garantir compilação HotSpot C2 imediata no primeiro login.
* **Protocolo Fermata (Opcode `0xFD`):** Codecs de alta performance para extensões de interface moderna: Atlas Data, Economy 64-bit, Combat Meter Reports, Equipment Preview e QoL.

### 🧮 3. Geometria Vetorial e SIMD (`SimdGeoMath`)
* **Aceleração Hardware (AVX2 / AVX-512 / ARM NEON):** Loops desenrolados em 4-way e 8-way em `SimdGeoMath.java` (`batchDistanceSquared2D`, `batchDistance2D`, `batchOctileDistance8`).
* **Early-Exit Euclidiano 3D:** Checagem rápida por caixas delimitadoras (`|dx| > r`, `|dy| > r`, `|dz| > r`) antes do produto escalar, evitando milhares de chamadas a `Math.sqrt` por tick.
* **AutoFarm SIMD Targeting:** O seletor de alvos do sistema de AutoFarm avalia e ranqueia monstros em lote via instruções vetoriais, eliminando iterações com lambdas e comparators no heap.

### 🗺️ 4. Geodata de Próxima Geração e Movimento Fluido
* **Zstandard Level 3 (Zstd):** Grafos de nós do geodata compactados via `L2BRSerializer` com descompressão a mais de 2 GB/s no boot.
* **`SmoothObstacleAvoidance`:** Interpolação suave em quinas de paredes e desníveis para NPCs e monstros, eliminando travamentos angulares e movimentos em ziguezague.
* **ValidatePosition em 3 Zonas:**
  * *Green Zone ($\le 180\text{u}$):* Posição aceita normalmente, sem correções bruscas.
  * *Yellow Zone ($180\text{u} < \Delta \le 500\text{u}$):* Reconciliação elástica suave (`CharMoveToLocation`), eliminando o rubberbanding para conexões com oscilação de ping.
  * *Red Zone ($> 500\text{u}$ ou queda em Z):* Snap de segurança anti-speedhack e resgate automático para a cidade após tentativas inválidas consecutivas.
* **Monster AI L2OFF (Return Home):** Monstros em combate melee que falham o pathfinding por mais de 250 unidades desistem da perseguição e retornam suavemente ao spawn, eliminando o clássico bug de teleportar para cima de telhados ou montanhas.

### 🌪️ 5. Supressão de Tempestade de Broadcast (`BulkBuffScope`)
* Durante o Full Buff (via NPC ou Community Board), o servidor original disparava dezenas de pacotes de broadcast imediatos por segundo para cada buff individual.
* O **`BulkBuffScope`** reentrante suprime todos os broadcasts intermediários e consolida a operação em apenas **1 pacote final** via `AutoCloseable`, reduzindo em até **90% o tráfego de rede** em praças e cidades movimentadas.

### 🛡️ 6. Segurança Nativa e Proteção Anti-DDoS
* **Fail2Ban Nativo:** Detecção em nível de socket integrada ao Netty com persistência de bans em tabela MariaDB (`fail2ban_bans`) e whitelist estrita de IPs de loopback (`127.0.0.1`, `::1`) e redes locais (RFC 1918).
* **PROXY Protocol v2 (Cloudflare Tunnel):** Suporte nativo para túneis reversos da Cloudflare preservando o IP de origem do jogador no backend e nos logs.
* **Anti-DoS no `RequestBypassToServer`:** Debounce atômico de 250ms contra macros repetitivas e teto de segurança de 15 requisições por segundo por jogador.
* **Safe Disconnect:** Janela de retenção do personagem durante quedas bruscas de conexão, prevenindo duplicação de itens e perda de progresso.

### 📊 7. Observabilidade e Métricas (Prometheus & Micrometer)
* Servidor HTTP de métricas nativo (`ServerMetrics` e `MetricsHttpServer`) expondo métricas no formato Prometheus para integração direta com dashboards Grafana:
  * Conexões ativas no GameServer e AuthServer
  * Utilização do pool de memória do Netty (PooledByteBufAllocator)
  * Taxa de pacotes recebidos e enviados por segundo
  * Tempos de ciclo dos ticks de IA e agendador

---

## 🧪 Cobertura de Testes Automatizados

O projeto conta com **176 testes unitários automatizados** em JUnit 5 cobrindo 100% dos fluxos críticos:

```text
Testes Executados: 176 | Falhas: 0 | Erros: 0 | Ignorados: 0 | Sucesso: 100%
Tempo de Execução da Suíte: ~2.4s
```

Para rodar a suíte de testes:
```bash
./gradlew test
```

---

## 🛠️ Requisitos de Compilação e Execução

* **JDK:** Java 25 LTS (Oracle GraalVM recomendado)
* **Gradle:** 9.0+
* **Banco de Dados:** MariaDB 10.6+ ou MySQL 8.0+

### Compilando o Projeto:
```bash
./gradlew build -x test
```

### Inicialização com Flags Otimizadas:
```bash
./StartGameServer.sh
./StartAuthServer.sh
```

---

## 📜 Licença

Distribuído sob a licença GNU General Public License v3 (GPLv3). Veja `LICENSE.txt` para detalhes.
