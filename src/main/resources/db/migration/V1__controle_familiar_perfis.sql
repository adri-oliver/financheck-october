-- =============================================
-- Migração V1 - Perfil Financeiro Familiar
-- Compatível com H2 (MODE=PostgreSQL)
-- =============================================

-- Tabela de perfil financeiro
CREATE TABLE IF NOT EXISTS tb_perfil_financeiro (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome       VARCHAR(255) NOT NULL,
    tipo       VARCHAR(20)  NOT NULL,
    usuario_id BIGINT       NOT NULL
);

-- Coluna controle_familiar em tb_usuario (H2 não suporta IF NOT EXISTS em ALTER TABLE)
ALTER TABLE tb_usuario ADD COLUMN controle_familiar BOOLEAN DEFAULT FALSE;

UPDATE tb_usuario
SET controle_familiar = FALSE
WHERE controle_familiar IS NULL;

-- Inserir perfil RESPONSAVEL para usuários que ainda não possuem um
INSERT INTO tb_perfil_financeiro (nome, tipo, usuario_id)
SELECT u.nome, 'RESPONSAVEL', u.id
FROM tb_usuario u
WHERE NOT EXISTS (
    SELECT 1
    FROM tb_perfil_financeiro p
    WHERE p.usuario_id = u.id
      AND p.tipo = 'RESPONSAVEL'
);

-- Coluna perfil_id em tb_transacao
ALTER TABLE tb_transacao ADD COLUMN perfil_id BIGINT;

-- Associa transações existentes ao perfil RESPONSAVEL do mesmo usuário
UPDATE tb_transacao
SET perfil_id = (
    SELECT p.id
    FROM tb_perfil_financeiro p
    WHERE p.usuario_id = tb_transacao.usuario_id
      AND p.tipo = 'RESPONSAVEL'
    LIMIT 1
)
WHERE perfil_id IS NULL;

-- Coluna perfil_id em tb_meta
ALTER TABLE tb_meta ADD COLUMN perfil_id BIGINT;

-- Associa metas existentes ao perfil RESPONSAVEL do mesmo usuário
UPDATE tb_meta
SET perfil_id = (
    SELECT p.id
    FROM tb_perfil_financeiro p
    WHERE p.usuario_id = tb_meta.usuario_id
      AND p.tipo = 'RESPONSAVEL'
    LIMIT 1
)
WHERE perfil_id IS NULL;
