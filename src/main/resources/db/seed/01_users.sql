-- ==========================================
-- SEED: 01 - USUARIOS
-- Contraseña para todos: TestPassword123
-- ==========================================

INSERT INTO user_account (id_user_account, profile_username, email, password, role, avatar, name, last_name, email_verified) VALUES
(1, 'mferradans',              'mateo@test.com',       '$2a$10$wPpCaSsVWMND5AEtYqLKfuKdgZb2JyHGSQA/poP/9jbYosU59ShBW', 'ADMIN',       'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=400&fit=crop&crop=face', 'Mateo',                          'Ferradans', true),
(2, 'Museo Nacional Argentino','museo@test.com',        '$2a$10$wPpCaSsVWMND5AEtYqLKfuKdgZb2JyHGSQA/poP/9jbYosU59ShBW', 'INSTITUTION', 'https://images.unsplash.com/photo-1554907984-15263bfd63bd?w=400',                                'Museo Nacional Argentino',       null,        true),
(3, 'Historia Argentina',      'historia@test.com',    '$2a$10$wPpCaSsVWMND5AEtYqLKfuKdgZb2JyHGSQA/poP/9jbYosU59ShBW', 'NORMAL',      'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&h=400&fit=crop&crop=face', 'Historia',                       'Argentina', true),
(4, 'Universidad Nacional',    'universidad@test.com', '$2a$10$wPpCaSsVWMND5AEtYqLKfuKdgZb2JyHGSQA/poP/9jbYosU59ShBW', 'INSTITUTION', 'https://images.unsplash.com/photo-1562774053-701939374585?w=400',                                'Universidad Nacional de Córdoba', null,       true),
(5, 'usuario_normal',          'normal@test.com',      '$2a$10$wPpCaSsVWMND5AEtYqLKfuKdgZb2JyHGSQA/poP/9jbYosU59ShBW', 'NORMAL',      'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400',                                'Juan',                           'Pérez',     true);

SELECT setval(pg_get_serial_sequence('user_account', 'id_user_account'), (SELECT MAX(id_user_account) FROM user_account));
