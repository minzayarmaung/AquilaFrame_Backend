package com.nexusforge.AquilaFramework.dao;

import com.nexusforge.AquilaFramework.entity.Result;
import com.nexusforge.AquilaFramework.entity.User;
import com.nexusforge.AquilaFramework.Repository.UserRepository;
import com.nexusforge.AquilaFramework.Util.ServerUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UserAuthDao {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ServerUtil serverUtil;

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

    public Result sendUserVerifyEmail(String userEmail, String resetCode) {
        Result res = new Result();
        String userName = userEmail.split("@")[0];

        try {
            String htmlTemplate = serverUtil.loadTemplate("templates/VerifyUserMail.html");
            String htmlContent = htmlTemplate
                    .replace("{{username}}", userName)
                    .replace("{{code}}", resetCode);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(userEmail);
            helper.setSubject("🔒 Verify Code - OSMS");
            helper.setText(htmlContent, true);
            mailSender.send(message);

            res.setState(true);
            res.setMsgDesc("Verify Code Mail Sent Successfully to " + userEmail);
            res.setMsgCode("200");
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            res.setState(false);
        }
        return res;
    }

    public Result saveSignupUser(User newUser) {
        Result res = new Result();
        try {
            userRepository.save(newUser);
            res.setState(true);
            res.setMsgDesc("Sign Up Successfully.");
            res.setMsgCode("200");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return res;
    }
}
