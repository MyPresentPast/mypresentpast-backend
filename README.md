# MyPresentPast Backend

Backend API para la aplicación MyPresentPast construido con Spring Boot.

## 🚀 Requisitos previos

- Java 17 o superior
- Maven 3.6 o superior
- PostgreSQL 12.0 o superior
- IDE recomendado: IntelliJ IDEA o VS Code

## 📁 Estructura del proyecto

```
src/
├── main/
│   ├── java/com/mypresentpast/backend/
│   │   ├── config/          # Configuraciones
│   │   ├── controller/      # Controladores REST
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── model/          # Entidades JPA
│   │   ├── repository/     # Repositorios JPA
│   │   ├── service/        # Lógica de negocio
│   │   └── MypresentpastBackendApplication.java
│   └── resources/
│       └── application.properties
└── test/                   # Pruebas unitarias
```

## ⚙️ Configuración

### 1. Base de datos

Crear una base de datos PostgreSQL:

```sql
-- Conectarse a PostgreSQL como superusuario
CREATE DATABASE mypresentpast_db;
CREATE DATABASE mypresentpast_dev; -- Para desarrollo

-- Crear usuario (opcional)
CREATE USER dev_user WITH PASSWORD 'dev_password';
GRANT ALL PRIVILEGES ON DATABASE mypresentpast_dev TO dev_user;
GRANT ALL PRIVILEGES ON DATABASE mypresentpast_db TO dev_user;
```

**Instalación de PostgreSQL:**
- **MacOS**: `brew install postgresql`
- **Ubuntu**: `sudo apt-get install postgresql postgresql-contrib`
- **Windows**: Descargar desde https://www.postgresql.org/download/

### 2. Configuración de variables de entorno

El proyecto usa un archivo `.env` para configuración local:

1. **Copia el archivo de ejemplo:**
   ```bash
   cp .env.example .env
   ```

2. **Edita `.env` con tus configuraciones:**
   ```env
   DB_HOST=localhost
   DB_PORT=5432
   DB_NAME=mypresentpast_db
   DB_USERNAME=postgres
   DB_PASSWORD=tu_password_aqui
   FRONTEND_URL_REACT=http://localhost:3000
   FRONTEND_URL_ANGULAR=http://localhost:4200
   ```

**⚠️ Importante:** El archivo `.env` contiene credenciales locales y NO debe subirse a git (ya está en `.gitignore`).

## 🛠️ Instalación y ejecución

### 1. Clonar el repositorio
```bash
git clone <url-del-repositorio>
cd mypresentpast-backend
```

### 2. Instalar dependencias
```bash
mvn clean install
```

### 3. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

#### Desde el IDE:
- Ejecutar la clase `MypresentpastBackendApplication.java`

## 🐳 Ejecución con Docker Compose

El stack Docker levanta el frontend Angular con Nginx, el backend Spring Boot y PostgreSQL. Es necesario tener Docker Desktop en ejecución y un archivo `.env` configurado en este directorio.

```bash
docker compose up --build
```

Una vez iniciado, los servicios quedan disponibles en:

- **Frontend**: `http://localhost`
- **Backend**: `http://localhost:8081`
- **PostgreSQL**: `localhost:5433`

El backend escucha internamente en el puerto `8092`; Compose lo publica en `8081` para conservar la configuración habitual del frontend. Para detener el stack sin eliminar los datos de PostgreSQL:

```bash
docker compose down
```

## 🌐 Endpoints

La aplicación estará disponible en:
- **URL base**: `http://localhost:8080/api`
- **Health check**: `http://localhost:8080/api/actuator/health`

## 🔧 Configuración adicional

### CORS

Configurado para permitir requests desde:
- `http://localhost:3000` (React)
- `http://localhost:4200` (Angular)

## 📝 Notas para el desarrollo

1. **Hot reload**: El proyecto incluye Spring Boot DevTools para recarga automática
2. **Security**: Configuración básica con usuario admin/admin123 (deshabilitada por ahora)
3. **Database**: Hibernate configurado con `ddl-auto=update` para crear/actualizar tablas automáticamente
4. **Logging**: Configurado para mostrar solo errores e información importante

## 🚀 Próximos pasos

1. Implementar entidades en `model/`
2. Crear repositorios en `repository/`
3. Desarrollar servicios en `service/`
4. Implementar controladores REST en `controller/`
5. Crear DTOs para las respuestas API en `dto/`
6. Configurar seguridad personalizada en `config/`

## 🤝 Contribución

1. Crear una rama para la nueva feature: `git checkout -b feature/nueva-funcionalidad`
2. Hacer commit de los cambios: `git commit -m 'Agregar nueva funcionalidad'`
3. Push a la rama: `git push origin feature/nueva-funcionalidad`
4. Crear un Pull Request
