package com.mypresentpast.backend.service.impl;

import com.mypresentpast.backend.dto.request.EmailRequest;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailServiceImpl emailService;

    private EmailRequest testRequest;
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        // MimeMessage real porque MimeMessageHelper lo necesita para funcionar internamente
        mimeMessage = new MimeMessage(Session.getInstance(new Properties()));

        testRequest = new EmailRequest();
        testRequest.setRecipient("test@example.com");
        testRequest.setSubject("Verificá tu cuenta");
        testRequest.setName("Juan");
        testRequest.setVerificationUrl("https://example.com/verify?token=abc123");
        testRequest.setEmailChange(false);
    }

    // Verifica que se envía el correo correctamente y el Context tiene las variables correctas para verificación de cuenta.
    @Test
    void sendMail_VerificationEmail_SendsEmailAndSetsContextVariables() {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("email"), any(Context.class))).thenReturn("<html>Verificá tu cuenta</html>");

        assertDoesNotThrow(() -> emailService.sendMail(testRequest));

        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("email"), contextCaptor.capture());
        Context capturedContext = contextCaptor.getValue();
        assertEquals("Juan", capturedContext.getVariable("name"));
        assertEquals("https://example.com/verify?token=abc123", capturedContext.getVariable("verificationUrl"));
        assertEquals(false, capturedContext.getVariable("emailChange"));

        verify(javaMailSender).send(mimeMessage);
    }

    // Verifica que el Context incluye emailChange=true cuando el request corresponde a un cambio de email.
    @Test
    void sendMail_EmailChangeRequest_SetsEmailChangeFlagInContext() {
        testRequest.setEmailChange(true);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("email"), any(Context.class))).thenReturn("<html>Cambio de email</html>");

        assertDoesNotThrow(() -> emailService.sendMail(testRequest));

        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("email"), contextCaptor.capture());
        assertEquals(true, contextCaptor.getValue().getVariable("emailChange"));

        verify(javaMailSender).send(mimeMessage);
    }

    // Verifica que si el templateEngine lanza una excepción, se relanza como RuntimeException con el mensaje esperado y no se llama a send().
    @Test
    void sendMail_TemplateEngineThrowsException_ThrowsRuntimeException() {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("email"), any(Context.class)))
                .thenThrow(new RuntimeException("template error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> emailService.sendMail(testRequest));

        assertTrue(ex.getMessage().startsWith("Error al enviar el correo:"));
        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }

    // Verifica que si javaMailSender.send() lanza una excepción, se captura y se relanza como RuntimeException.
    @Test
    void sendMail_MailSenderThrowsException_ThrowsRuntimeException() {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("email"), any(Context.class))).thenReturn("<html>content</html>");
        doThrow(new RuntimeException("mail error")).when(javaMailSender).send(mimeMessage);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> emailService.sendMail(testRequest));

        assertTrue(ex.getMessage().startsWith("Error al enviar el correo:"));
    }
}
