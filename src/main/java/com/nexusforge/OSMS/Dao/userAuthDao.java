package com.nexusforge.OSMS.Dao;

import com.nexusforge.OSMS.Entity.Result;
import com.nexusforge.OSMS.Util.serverUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class userAuthDao {

    @Autowired
    private serverUtil serverUtil;

    @Autowired
    private JavaMailSender mailSender;

    // Sending Email
    public Result sendEmail(String userEmail, String resetCode) {
        Result res = new Result();
        String userName = userEmail.split("@")[0];

        try {
            String htmlTemplate = serverUtil.loadTemplate("templates/resetMail.html");
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
}
