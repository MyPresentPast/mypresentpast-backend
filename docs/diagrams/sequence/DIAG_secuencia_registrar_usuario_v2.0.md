# Diagrama de secuencia - Registro de usuario

Este diagrama representa el flujo principal de registro de usuario en MyPresentPast.

El objetivo es mantener el diagrama legible para incluirlo como captura en el documento de desarrollo de producto. Por eso se deja fuera del flujo principal el detalle posterior de verificacion de email e inicio de sesion.

## Diagrama PlantUML

```plantuml
@startuml
title Registro de usuario

actor Usuario
boundary "RegisterComponent" as RegisterComponent
control "RegisterService" as RegisterService
boundary "AuthController" as AuthController
control "AuthServiceImpl" as AuthService
database "UserRepository" as UserRepository
control "PasswordEncoder" as PasswordEncoder
control "VerificationServiceImpl" as VerificationService
database "VerificationTokenRepository" as TokenRepository
control "EmailServiceImpl" as EmailService
control "JavaMailSender" as MailSender

Usuario -> RegisterComponent : Completa formulario y presiona "Crear cuenta"
RegisterComponent -> RegisterComponent : Valida campos, contrasenas y terminos
RegisterComponent -> RegisterService : register(request)
RegisterService -> AuthController : POST /auth/register
AuthController -> AuthService : register(request)

AuthService -> AuthService : Valida contrasenas y formato
AuthService -> UserRepository : existsByProfileUsername(profileUsername)
UserRepository --> AuthService : false
AuthService -> UserRepository : findByEmail(email)
UserRepository --> AuthService : empty
AuthService -> PasswordEncoder : encode(password)
PasswordEncoder --> AuthService : password hasheada
AuthService -> UserRepository : save(User emailVerified=false)
UserRepository --> AuthService : Usuario guardado

AuthService -> VerificationService : createVerificationToken(user)
VerificationService -> TokenRepository : deleteByUser(user)
VerificationService -> TokenRepository : save(VerificationToken)
TokenRepository --> VerificationService : Token de verificacion
VerificationService --> AuthService : VerificationToken

AuthService -> EmailService : sendMail(emailRequest)
EmailService -> MailSender : send(email con link de verificacion)
MailSender --> EmailService : Correo enviado
EmailService --> AuthService : OK

AuthService --> AuthController : ApiResponse("Usuario creado...")
AuthController --> RegisterService : 200 OK
RegisterService --> RegisterComponent : response
RegisterComponent -> Usuario : Navega a /register-success

@enduml
```

## Detalles importantes

- El registro no devuelve JWT ni `AuthResponse(token)`.
- El endpoint real de registro es `POST /auth/register`.
- `AuthController.register()` devuelve `ResponseEntity<ApiResponse>`.
- El usuario no queda autenticado al registrarse. Primero debe verificar el email y luego iniciar sesion.
- `AuthServiceImpl.register()` crea un usuario con rol `NORMAL` y `emailVerified=false`.
- El password se guarda hasheado mediante `PasswordEncoder.encode(password)`.
- `VerificationServiceImpl.createVerificationToken(user)` genera un token UUID, lo guarda en `verification_token` y lo asocia al usuario.
- `EmailServiceImpl.sendMail(emailRequest)` envia un correo con link a `http://localhost:4200/verify-success?token=...`.
- Tras respuesta exitosa, `RegisterComponent` navega a `/register-success` y pasa el email por `state`.
- Aunque el backend responde `ApiResponse`, el frontend `RegisterService.register()` esta tipado como `Observable<AuthResponse>`. El componente no usa el token, pero el tipo esta desactualizado y conviene corregirlo.
- Para el producto final, la respuesta de registro no debe incluir ningun token. El token de verificacion debe enviarse solo por email.

## Alternativas y errores relevantes

Estos casos pueden mencionarse en el documento sin agregarlos al diagrama principal, para evitar que la captura quede demasiado grande:

- Formulario invalido: faltan campos, email invalido, usuario con caracteres invalidos, contrasena menor a 8 caracteres o sin mayuscula.
- Terminos no aceptados: el frontend no envia el request y muestra error.
- Contrasenas distintas: el frontend lo detecta y el backend tambien valida `password` contra `confirmPassword`.
- Password invalida segun backend: debe tener minuscula, mayuscula, numero y al menos 8 caracteres.
- `profileUsername` duplicado: `DataIntegrityViolationException`.
- Email ya verificado: `DataIntegrityViolationException`.
- Email existente no verificado con token vigente: `BadRequestException`.
- Email existente no verificado con token vencido: el backend elimina token/usuario anterior y permite registrar de nuevo.
- Error al enviar email: `EmailServiceImpl` lanza `RuntimeException`.

## Mejoras futuras

- Backend: quitar el token de verificacion del mensaje de `ApiResponse` en `AuthServiceImpl.register()`.
- Frontend: corregir el tipo de `RegisterService.register()` de `AuthResponse` a `ApiResponse`. Ya existe `mypresentpast-frontend/src/app/model/response/api-response.model.ts` con el campo `message`.
- La verificacion de email puede documentarse como diagrama separado: `GET /auth/verify?token=...`.

## Referencias de codigo

- Frontend: `mypresentpast-frontend/src/app/features/usuarios/sesion/register/register.component.ts`
- Frontend: `mypresentpast-frontend/src/app/features/usuarios/sesion/register/register.component.html`
- Frontend: `mypresentpast-frontend/src/app/core/services/register/register.service.ts`
- Frontend: `mypresentpast-frontend/src/app/model/request/register-request.model.ts`
- Backend: `mypresentpast-backend/src/main/java/com/mypresentpast/backend/controller/auth/AuthController.java`
- Backend: `mypresentpast-backend/src/main/java/com/mypresentpast/backend/service/AuthService.java`
- Backend: `mypresentpast-backend/src/main/java/com/mypresentpast/backend/service/impl/AuthServiceImpl.java`
- Backend: `mypresentpast-backend/src/main/java/com/mypresentpast/backend/dto/request/RegisterRequest.java`
- Backend: `mypresentpast-backend/src/main/java/com/mypresentpast/backend/repository/UserRepository.java`
- Backend: `mypresentpast-backend/src/main/java/com/mypresentpast/backend/service/impl/VerificationServiceImpl.java`
- Backend: `mypresentpast-backend/src/main/java/com/mypresentpast/backend/repository/VerificationTokenRepository.java`
- Backend: `mypresentpast-backend/src/main/java/com/mypresentpast/backend/service/impl/EmailServiceImpl.java`
