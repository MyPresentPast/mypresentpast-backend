-- ===================================
-- Setup PostgreSQL para MyPresentPast
-- ===================================

-- Crear base de datos
CREATE DATABASE mypresentpast_db;

-- Crear usuario (opcional)
-- CREATE USER mypresentpast_user WITH PASSWORD 'mypresentpast_password';
-- GRANT ALL PRIVILEGES ON DATABASE mypresentpast_db TO mypresentpast_user;

-- Conectarse a la base de datos
\c mypresentpast_db;

-- Verificar conexión
SELECT 'PostgreSQL configurado correctamente para MyPresentPast' AS status; 