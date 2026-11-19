-- Battle Pass: progresso de cada personagem na temporada.
--
-- O servidor cria esta tabela sozinho ao carregar o script BattlePass. Rode
-- este arquivo só se o log mostrar "não consegui criar a tabela
-- character_battle_pass" (usuário do banco sem permissão de CREATE).
--
--   season           dia em que a temporada começou (dias desde 1970);
--                    diferente da temporada atual = progresso zera
--   points           pontos acumulados
--   premium          1 = premium ativo nesta temporada
--   free_claimed     bit (degrau - 1) ligado = recompensa grátis já pega
--   premium_claimed  idem, recompensa premium

CREATE TABLE IF NOT EXISTS character_battle_pass (
  char_id INT NOT NULL,
  season INT NOT NULL DEFAULT 0,
  points INT NOT NULL DEFAULT 0,
  premium TINYINT NOT NULL DEFAULT 0,
  free_claimed BIGINT NOT NULL DEFAULT 0,
  premium_claimed BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (char_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
