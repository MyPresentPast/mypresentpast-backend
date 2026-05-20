# Diagrama de secuencia - Inicio de sesion

Este diagrama representa el flujo principal de inicio de sesion en MyPresentPast.

El objetivo es mantener el diagrama legible para incluirlo como captura en el documento de desarrollo de producto. Por eso se dejan fuera del flujo principal las validaciones visuales del formulario y los errores, que quedan documentados en las notas posteriores.

## Diagrama PlantUML

```plantuml
@startuml
title Inicio de sesion

actor Usuario
boundary "LoginComponent" as LoginComponent
boundary "RecaptchaComponent" as RecaptchaComponent
control "LoginService" as LoginService
boundary "AuthController" as AuthController
control "AuthServiceImpl" as AuthService
control "RecaptchaServiceImpl" as RecaptchaService
control "Google reCAPTCHA" as GoogleRecaptcha
database "UserRepository" as UserRepository
control "PasswordEncoder" as PasswordEncoder
control "JwtServiceImpl" as JwtService

Usuario -> LoginComponent : Completa formulario y presiona "Iniciar sesion"
LoginComponent -> LoginComponent : Valida email, password y reCAPTCHA
LoginComponent -> RecaptchaComponent : Obtiene token reCAPTCHA
RecaptchaComponent --> LoginComponent : recaptchaToken
LoginComponent -> LoginService : login(email, password, rememberMe, recaptchaToken)
LoginService -> AuthController : POST /auth/login
AuthController -> AuthService : login(request)

AuthService -> RecaptchaService : verify(recaptchaToken)
RecaptchaService -> GoogleRecaptcha : Verifica token
GoogleRecaptcha --> RecaptchaService : success=true
RecaptchaService --> AuthService : true

AuthService -> UserRepository : findByEmail(email)
UserRepository --> AuthService : User
AuthService -> PasswordEncoder : matches(password, user.password)
PasswordEncoder --> AuthService : true
AuthService -> AuthService : Verifica email confirmado

alt Email confirmado
  AuthService -> JwtService : getToken(user)
  JwtService --> AuthService : JWT token
  AuthService --> AuthController : AuthResponse(token)
  AuthController --> LoginService : 200 OK
  LoginService -> LoginService : Guarda token segun rememberMe
  LoginService --> LoginComponent : response
  LoginComponent -> Usuario : Muestra exito y navega a /home
else Email no confirmado
  AuthService --> AuthController : DisabledException
  AuthController --> LoginService : 401 ErrorResponse
  LoginService -> LoginComponent : Navega a /email-not-confirmed
  LoginComponent --> Usuario : Muestra pantalla de email no confirmado
end

@enduml
```

## Detalles importantes

- El flujo real valida reCAPTCHA en `AuthServiceImpl.login()` antes de buscar el usuario y validar password.
- La validacion de credenciales se hace con `UserRepository.findByEmail(email)` y `PasswordEncoder.matches(password, user.getPassword())`.
- El endpoint real es `POST /auth/login`.
- El request frontend envia `email`, `password` y `recaptcha_token`. En backend, `LoginRequest` espera `recaptchaToken`; la configuracion `spring.jackson.property-naming-strategy=SNAKE_CASE` permite el mapeo.
- `RecaptchaComponent` carga el script de Google reCAPTCHA, renderiza el widget y emite el token al `LoginComponent`.
- `RecaptchaServiceImpl.verify(token)` llama al servicio externo de Google reCAPTCHA usando `RestTemplate`.
- Si el usuario existe, la password coincide y el email esta verificado, `JwtServiceImpl.getToken(user)` genera el JWT.
- `LoginService` guarda el token en `localStorage` si `rememberMe` esta activo; si no, lo guarda en `sessionStorage`.
- Tras respuesta exitosa, `LoginComponent` muestra mensaje de exito, espera 1 segundo, navega a `/home` y recarga la pagina.

## Alternativas y errores relevantes

Estos casos pueden mencionarse en el documento sin agregarlos al diagrama principal, para evitar que la captura quede demasiado grande:

- Formulario invalido: el frontend muestra error y no envia el request.
- reCAPTCHA no completado: `LoginComponent` muestra error y no envia el request.
- reCAPTCHA invalido o error contra Google: `RecaptchaServiceImpl.verify()` devuelve `false` y `AuthServiceImpl.login()` lanza `BadRequestException`.
- Email inexistente o password incorrecta: se lanza `BadRequestException` con mensaje generico de login fallido.
- Email no verificado: se modelo en el diagrama porque deriva en una pantalla especifica (`/email-not-confirmed`).
- Error de login en frontend: se muestra `Email o contrasena incorrectos.`, se limpia el token reCAPTCHA y se resetea el widget.

## Referencias de codigo

- Frontend: `mypresentpast-frontend/src/app/features/usuarios/sesion/login/login.component.ts`
- Frontend: `mypresentpast-frontend/src/app/features/usuarios/sesion/login/recaptcha.component.ts`
- Frontend: `mypresentpast-frontend/src/app/core/services/login/login.service.ts`
- Frontend: `mypresentpast-frontend/src/app/model/response/auth-response.model.ts`
- Backend: `mypresentpast-backend/src/main/java/com/mypresentpast/backend/controller/auth/AuthController.java`
- Backend: `mypresentpast-backend/src/main/java/com/mypresentpast/backend/dto/request/LoginRequest.java`
- Backend: `mypresentpast-backend/src/main/java/com/mypresentpast/backend/dto/response/AuthResponse.java`
- Backend: `mypresentpast-backend/src/main/java/com/mypresentpast/backend/service/impl/AuthServiceImpl.java`
- Backend: `mypresentpast-backend/src/main/java/com/mypresentpast/backend/service/impl/RecaptchaServiceImpl.java`
- Backend: `mypresentpast-backend/src/main/java/com/mypresentpast/backend/repository/UserRepository.java`
- Backend: `mypresentpast-backend/src/main/java/com/mypresentpast/backend/service/JwtService.java`
- Backend: `mypresentpast-backend/src/main/resources/application.properties`
