package com.nexusforge.OSMS.Mgr;

import com.nexusforge.OSMS.Entity.Result;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

@Service
public class userAuthMgr {

    @Autowired
    private JavaMailSender mailSender;

    public Result sendResetEmail(String email){
        Result res = new Result();
        String code = generateRandomCode();
        res = sendEmail(email , code);
        return res;
    }

    private void sendEmail1(String userEmail , String resetCode){
        String userName = userEmail.split("@")[0];
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(userEmail);
        msg.setSubject("Password Reset Code");
        msg.setText("Hello " + userName + ". Please use this code :" + resetCode +
                " to reset the password for OSMS User Account. It will valid for 15 Minutes.");

        mailSender.send(msg);
    }

    private Result sendEmail(String userEmail, String resetCode) {
        Result res = new Result();
        String userName = userEmail.split("@")[0];

        try {
            String htmlTemplate = loadTemplate("templates/resetMail.html");
            String htmlContent = htmlTemplate
                    .replace("{{username}}", userName)
                    .replace("{{code}}", resetCode);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(userEmail);
            helper.setSubject("🔒 Password Reset Code - OSMS");
            helper.setText(htmlContent, true);
            mailSender.send(message);

            res.setState(true);
            res.setMsgDesc("Password Reset Mail Sent Successfully to " + userEmail);
            res.setMsgCode("200");
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            res.setState(false);
        }
        return res;
    }

    private String loadTemplate(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        byte[] bytes = Files.readAllBytes(resource.getFile().toPath());
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private String generateRandomCode(){
        return UUID.randomUUID().toString().substring(0,8);
    }
}
