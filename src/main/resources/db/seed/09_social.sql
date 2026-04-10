-- ==========================================
-- SEED: 09 - DATOS SOCIALES
-- Likes, follows, colecciones y verificaciones
-- NOTA: IDs de posts actualizados respecto al import.sql original
-- ==========================================

-- ── LIKES ──────────────────────────────────────────────────────────────────
-- Buenos Aires — posts con más engagement (efecto cluster)
INSERT INTO post_like (user_id, post_id, created_at) VALUES
(1, 2, '2024-01-26 10:00:00'),  -- Obelisco
(2, 2, '2024-01-26 11:00:00'),
(3, 2, '2024-01-26 12:00:00'),
(4, 2, '2024-01-26 13:00:00');

INSERT INTO post_like (user_id, post_id, created_at) VALUES
(1, 4, '2024-01-28 10:00:00'),  -- Tango La Boca
(2, 4, '2024-01-28 11:00:00'),
(3, 4, '2024-01-28 12:00:00');

INSERT INTO post_like (user_id, post_id, created_at) VALUES
(1, 3, '2024-01-27 10:00:00'),  -- Puerto Madero
(2, 3, '2024-01-27 11:00:00');

INSERT INTO post_like (user_id, post_id, created_at) VALUES
(1, 1, '2024-01-25 10:00:00'),  -- Fundación BA
(4, 1, '2024-01-25 13:00:00');

INSERT INTO post_like (user_id, post_id, created_at) VALUES
(2, 6, '2024-01-30 11:00:00'),  -- Palermo
(3, 6, '2024-01-30 12:00:00');

INSERT INTO post_like (user_id, post_id, created_at) VALUES
(1, 5, '2024-01-29 10:00:00');  -- Recoleta

INSERT INTO post_like (user_id, post_id, created_at) VALUES
(4, 7, '2024-01-31 13:00:00');  -- San Telmo

-- Argentina — likes variados
INSERT INTO post_like (user_id, post_id, created_at) VALUES
(1, 8, '2024-03-01 10:00:00'),  -- Cataratas Iguazú
(2, 8, '2024-03-01 11:00:00'),
(3, 8, '2024-03-01 12:00:00'),
(4, 8, '2024-03-01 13:00:00');

INSERT INTO post_like (user_id, post_id, created_at) VALUES
(1, 9, '2024-03-05 10:00:00'),  -- Perito Moreno
(2, 9, '2024-03-05 11:00:00'),
(3, 9, '2024-03-05 12:00:00');

-- ── FOLLOWS ────────────────────────────────────────────────────────────────
INSERT INTO follow (follower_id, followee_id) VALUES
(1, 2),
(1, 3),
(1, 4),
(2, 3),
(2, 4),
(3, 2),
(3, 4),
(4, 1),
(4, 2);

-- ── COLECCIONES ────────────────────────────────────────────────────────────
INSERT INTO collection (id, name, description, author_id, created_at, updated_at) VALUES
(1, 'Maravillas del Mundo Antiguo', 'Las construcciones más impresionantes de las civilizaciones antiguas',  2, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),
(2, 'Arquitectura Europea',         'Los monumentos más emblemáticos de Europa',                             1, '2024-01-15 10:00:00', '2024-01-15 10:00:00'),
(3, 'Buenos Aires Histórica',       'Los momentos más importantes en la historia de la capital argentina',   3, '2024-02-15 10:00:00', '2024-02-15 10:00:00');

SELECT setval(pg_get_serial_sequence('collection', 'id'), (SELECT MAX(id) FROM collection));

-- ── POSTS EN COLECCIONES ───────────────────────────────────────────────────
INSERT INTO collection_post (collection_id, post_id, added_at) VALUES
(1, 22, '2024-01-02 10:00:00'),  -- Coliseo
(1, 25, '2024-01-02 11:00:00'),  -- Acrópolis
(1, 30, '2024-01-02 12:00:00');  -- Pirámides de Giza

INSERT INTO collection_post (collection_id, post_id, added_at) VALUES
(2, 23, '2024-01-16 10:00:00'),  -- Torre Eiffel
(2, 26, '2024-01-16 11:00:00'),  -- Sagrada Familia
(2,  2, '2024-01-16 12:00:00');  -- Obelisco (antes post 7)

INSERT INTO collection_post (collection_id, post_id, added_at) VALUES
(3, 1, '2024-02-16 10:00:00'),  -- Fundación BA (antes post 6)
(3, 2, '2024-02-16 11:00:00'),  -- Obelisco (antes post 7)
(3, 3, '2024-02-16 12:00:00'),  -- Puerto Madero (antes post 8)
(3, 4, '2024-02-16 13:00:00'),  -- Tango La Boca (antes post 9)
(3, 5, '2024-02-16 14:00:00'),  -- Recoleta (antes post 10)
(3, 6, '2024-02-16 15:00:00'),  -- Palermo (antes post 11)
(3, 7, '2024-02-16 16:00:00');  -- San Telmo (antes post 12)

-- ── VERIFICACIONES ─────────────────────────────────────────────────────────
-- Las instituciones (usuarios 2 y 4) están auto-verificadas por lógica del backend.
-- Solo se incluyen verificaciones EXTERNAS de posts de usuarios NORMALES (1 y 3).

INSERT INTO post_verification (post_id, verified_by_id, verified_at, is_active) VALUES
(1,  2, '2024-02-01 10:00:00', true),  -- Fundación BA (antes post 6) verificado por Museo (id=2)
(10, 2, '2024-03-11 10:00:00', true),  -- Aconcagua (antes post 15) verificado por Museo (id=2)
(2,  4, '2024-02-16 10:00:00', true);  -- Obelisco (antes post 7) verificado por Universidad (id=4)
