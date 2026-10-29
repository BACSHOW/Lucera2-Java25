-- ==============================================================================
-- L2JLucera2: Migração e Modernização de Schema para MariaDB 10.11+ / InnoDB
-- Objetivo: Eliminar Table Locks do MyISAM, otimizar índices e habilitar MVCC
-- ==============================================================================

-- 1. Conversão das Tabelas Críticas de Jogador e Jogo para InnoDB
ALTER TABLE `characters` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `character_subclasses` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `character_skills` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `character_skills_save` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `character_effects_save` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `character_quests` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `character_shortcuts` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `character_variables` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `character_macroses` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `character_recipebook` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `character_hennas` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `character_friends` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `character_blocklist` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `character_group_reuse` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `character_instances` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `character_premium_items` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `character_post_friends` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `character_recommends` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;

-- 2. Conversão das Tabelas de Clã, Aliança e Castelos
ALTER TABLE `clan_data` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `clan_subpledges` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `clan_subpledges_skills` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `clan_skills` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `clan_privs` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `clan_wars` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `ally_data` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `castle` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `clanhall` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `siege_clans` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `siege_players` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;

-- 3. Conversão de Tabelas Auxiliares, Pets e Sistemas Globais
ALTER TABLE `pets` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `pet_data` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `pets_skills` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `couples` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `cursed_weapons` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `epic_boss_spawn` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `heroes` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `heroes_diary` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `oly_comps` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `oly_heroes` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `oly_nobles` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `oly_season` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `raidboss_status` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `raidboss_points` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `seven_signs` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `seven_signs_status` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `seven_signs_festival` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;
ALTER TABLE `server_variables` ENGINE=InnoDB ROW_FORMAT=DYNAMIC;

-- ==============================================================================
-- 4. Otimização Cirúrgica de Índices
-- ==============================================================================

-- character_variables: Remover índices redundantes/prejudiciais e manter prim + expire_time
ALTER TABLE `character_variables` 
  DROP INDEX `obj_id`,
  DROP INDEX `type`,
  DROP INDEX `name`,
  DROP INDEX `value`;

-- items: Criar índice composto para otimizar queries por dono e localização
ALTER TABLE `items` 
  ADD INDEX `idx_owner_location_slot` (`owner_id`, `location`, `slot`);

-- character_skills: Criar índice de busca reversa por skill
ALTER TABLE `character_skills` 
  ADD INDEX `idx_skill_id` (`skill_id`);

-- clan_data: Criar índice para ranking de clãs por reputação
ALTER TABLE `clan_data` 
  ADD INDEX `idx_reputation_score` (`reputation_score` DESC);
