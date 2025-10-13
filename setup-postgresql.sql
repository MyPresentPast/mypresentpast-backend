-- ===================================
-- Setup PostgreSQL para MyPresentPast
-- ===================================

-- Crear base de datos
DROP DATABASE IF EXISTS mypresentpast_db;
CREATE DATABASE mypresentpast_db
  WITH ENCODING 'UTF8'
  LC_COLLATE='es_AR.UTF-8'
  LC_CTYPE='es_AR.UTF-8'
  TEMPLATE=template0;

-- Crear usuario (opcional)
-- CREATE USER mypresentpast_user WITH PASSWORD 'mypresentpast_password';
-- GRANT ALL PRIVILEGES ON DATABASE mypresentpast_db TO mypresentpast_user;

-- Conectarse a la base de datos
\c mypresentpast_db;

-- Verificar conexión
SELECT 'PostgreSQL configurado correctamente para MyPresentPast' AS status; 