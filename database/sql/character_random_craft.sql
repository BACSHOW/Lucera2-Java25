-- Random Craft: pontos, experiência e as 5 casas de cada personagem.
--
-- O servidor cria esta tabela sozinho ao carregar o script RandomCraft. Rode
-- este arquivo só se o log mostrar "não consegui criar a tabela
-- character_random_craft" (usuário do banco sem permissão de CREATE).
--
--   points  Craft Points
--   exp     experiência de craft acumulada (MaxExp dela = 1 ponto)
--   slots   casas: id:quantidade:grau:encantamento:travada:vezesTravada;...

CREATE TABLE IF NOT EXISTS character_random_craft (
  char_id INT NOT NULL,
  points INT NOT NULL DEFAULT 0,
  exp BIGINT NOT NULL DEFAULT 0,
  slots VARCHAR(255) NOT NULL DEFAULT '',
  PRIMARY KEY (char_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
