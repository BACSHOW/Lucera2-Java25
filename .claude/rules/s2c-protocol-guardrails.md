# Server-to-Client Protocol Guardrails (Lineage II Interlude / Unreal Engine 2.5 / Fermata)

Diretrizes de integridade física e serialização de rede Server-to-Client para prevenir crashes nativos (General Protection Fault, Access Violation 0xC0000005) e desconexões.

---

## 🚫 REGRA 1: Proibições de Estrutura e Serialização (Buffer Rules)

1. **Nunca quebre o Endianness:** O cliente UE2.5 processa dados estritamente em **Little-Endian**. Todos os tipos primitivos (`short`, `int`, `long`, `float`) devem ser gravados no buffer nesta ordem de bytes.
2. **Nunca envie Strings sem Dupla Terminação Nula:** O cliente lê strings em formato **UTF-16LE**. É terminantemente proibido enviar strings sem garantir a terminação `\0\0` no final do array de bytes. Omissão disso causa *Access Violation (0xC0000005)* por leitura de memória fora dos limites.
3. **Nunca crie Underflow de Payload:** O tamanho declarado no cabeçalho do pacote (2 bytes iniciais) deve corresponder exatamente à soma de bytes do opcode + payload. Se o servidor enviar menos dados que a estrutura requer, o cliente chamará um `Assertion failed: Ar.Tell() <= Ar.TotalSize()` e fechará imediatamente.

---

## 🚫 REGRA 2: Proibições de Alocação de Memória (Listas e Arrays)

1. **Nunca envie contadores de tamanho arbitrários ou não validados:** Ao enviar pacotes baseados em listas (ex: `ItemList`, `SkillList`, `QuestList`), o primeiro dado numérico após o opcode costuma ser o `count` (tamanho da lista).
   - **Restrição:** Se o servidor enviar um valor corrompido, negativo ou excessivamente alto (ex: `0xFFFF`), o cliente tentará alocar um array na memória com esse tamanho, disparando um *Out of Memory (OOM)* imediato. Valide o tamanho da coleção no backend antes de escrever o buffer.
2. **Nunca ultrapasse os limites máximos de UI:** O cliente possui limites rígidos de slots (ex: limites de inventário, macros, buffs). Enviar mais elementos do que a interface suporta corromperá a renderização da HUD.

---

## 🚫 REGRA 3: Proibições de Máquina de Estado e Contexto de Mundo

1. **Nunca envie pacotes de Entidade (CharInfo, Move, Status) para um `ObjectId` inexistente no cliente:**
   - O cliente mantém um *World Cache*. Se o servidor enviar um pacote como `MoveToLocation` ou `MagicSkillUse` referenciando um `ObjectId` que ainda não foi apresentado ao cliente via `CharInfo` ou `NpcInfo`, o cliente tentará acessar um ponteiro nulo (Null Pointer Dereference) e o jogo vai crashar.
2. **Nunca pule pacotes de limpeza de estado:** Ao remover uma entidade do alcance de visão (Broadcast), é obrigatório enviar o pacote `DeleteObject`. Não envie novos pacotes de inicialização para o mesmo objeto sem antes limpá-lo, sob pena de duplicar meshes (*z-fighting*) e corromper o geodata local do cliente.
3. **Nunca envie pacotes In-Game antes de concluir o Handshake:** O fluxo `AuthLogin -> CharSelect -> EnterWorld` deve ser estrito. Enviar opcodes de gameplay antes de o cliente carregar o mapa (receber `ActionFailed` final do login) travará o estado da tela de carregamento.

---

## 🚫 REGRA 4: Restrições de Sessão e Interação

1. **Nunca envie opcodes perigosos em broadcasts de área:**
   - Opcodes como `0x84 (ServerClose)`, `0x27 (RestartResponse)` ou `0x7E (LeaveWorld)` alteram diretamente o ciclo de vida do soquete e da thread principal do cliente. Eles devem ser roteados estritamente via unicast e apenas mediante ação explícita do usuário.
2. **Nunca deixe janelas modais pendentes em transições:**
   - Se o jogador tem uma janela de Trade, Enchant ou HTML aberta e sofre um teleport (`TeleportToLocation`), o servidor deve enviar o pacote adequado de fechamento da UI antes de iniciar o load do mapa, caso contrário, a interface ficará permanentemente travada sobre a nova tela, sem aceitar cliques.

---

## 🚫 REGRA 5: Gerenciamento de Memória Netty/Backend

1. **Nunca perca a referência (Memory Leaks):** Ao manipular instâncias de `ByteBuf` para construção de pacotes, sempre garanta a liberação (`ReferenceCountUtil.release()` ou `.release()`) em caso de exceções no meio da serialização.
2. **Nunca faça operações bloqueantes na thread de rede (EventLoop):** A gravação do buffer no canal (`ctx.writeAndFlush()`) deve ser não-bloqueante. Consultas pesadas ao banco de dados ou algoritmos de pathfinding para montar o payload do pacote devem ocorrer em *worker threads* externas, devolvendo apenas a instrução de I/O finalizada para a pipeline.
