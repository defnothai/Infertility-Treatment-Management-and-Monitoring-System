package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.dto.response.EmailDetail;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    private static final String BASE_EMAIL_ADDRESS = "benhvienthanhnhan156@gmail.com";


    public EmailService(TemplateEngine templateEngine, JavaMailSender javaMailSender) {
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendRegistrationEmail(EmailDetail emailDetail) {
        try {
            Context context = new Context();
            context.setVariable("fullName", emailDetail.getFullName());
            context.setVariable("confirmationLink", emailDetail.getLink());
            String templateRegisterEmail = templateEngine.process("registerEmail", context);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(BASE_EMAIL_ADDRESS);
            messageHelper.setTo(emailDetail.getRecipient());
            messageHelper.setSubject(emailDetail.getSubject());
            messageHelper.setText(templateRegisterEmail, true);
            javaMailSender.send(messageHelper.getMimeMessage());
        } catch (Exception e) {
            throw new RuntimeException("Gửi mail xác nhận thất bại");
        }
    }

    @Async
    public void sendForgotPasswordEmail(EmailDetail emailDetail) {
        try {
            Context context = new Context();
            context.setVariable("fullName", emailDetail.getFullName());
            context.setVariable("resetPasswordLink", emailDetail.getLink());
            String templateRegisterEmail = templateEngine.process("forgotPasswordEmail", context);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(BASE_EMAIL_ADDRESS);
            messageHelper.setTo(emailDetail.getRecipient());
            messageHelper.setSubject(emailDetail.getSubject());
            messageHelper.setText(templateRegisterEmail, true);
            javaMailSender.send(messageHelper.getMimeMessage());
        } catch (Exception e) {
            throw new RuntimeException("Gửi mail xác nhận thất bại");
        }
    }

//    @Async
//    public void sendReminderEmail(EmailDetail emailDetail) {
//        try {
//            Context context = new Context();
//            context.setVariable("fullName", emailDetail.getFullName());
//            context.setVariable("message", emailDetail.getMessage());
//
//            String body = templateEngine.process("reminderEmail", context);
//            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
//            helper.setFrom(BASE_EMAIL_ADDRESS);
//            helper.setTo(emailDetail.getRecipient());
//            helper.setSubject(emailDetail.getSubject());
//            helper.setText(body, true);
//
//            javaMailSender.send(mimeMessage);
//        } catch (Exception e) {
//            throw new RuntimeException("Gửi email nhắc nhở thất bại");
//        }
//    }

}
