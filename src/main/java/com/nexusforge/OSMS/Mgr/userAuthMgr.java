package com.nexusforge.OSMS.Mgr;

import com.nexusforge.OSMS.Dao.userAuthDao;
import com.nexusforge.OSMS.Entity.PasswordResetToken;
import com.nexusforge.OSMS.Entity.Result;
import com.nexusforge.OSMS.Entity.User;
import com.nexusforge.OSMS.Repository.PasswordResetTokenRepository;
import com.nexusforge.OSMS.Repository.UserRepository;
import com.nexusforge.OSMS.Util.passwordEncoder;
import com.nexusforge.OSMS.Util.serverUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class userAuthMgr {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private serverUtil serverUtil;

    @Autowired
    private userAuthDao userAuthDao;

    @Autowired
    private passwordEncoder passwordEncoder;


    @Transactional
    public Result verifyLoginUser(String email, String password) {
        Result res = new Result();
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            res.setState(false);
            res.setMsgDesc("Email not registered.");
            res.setMsgCode("500");
            return res;
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            res.setState(false);
            res.setMsgDesc("Incorrect password.");
            res.setMsgCode("500");
            return res;
        }

        res.setState(true);
        res.setMsgDesc("Login successful.");
        return res;
    }


    @Transactional
    public Result sendResetEmail(String email){
        Result res = new Result();
        String code = serverUtil.generateRandomCode();

        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(15);

        // Delete old tokens
        passwordResetTokenRepository.deleteByEmail(email);

        // Save new token
        PasswordResetToken resetToken = new PasswordResetToken(email , code , expiryTime);
        passwordResetTokenRepository.save(resetToken);

        res = userAuthDao.sendEmail(email , code);
        return res;
    }

    public Result verifyResetCode(String email, String code) {
        Result res = new Result();
        Optional<PasswordResetToken> optionalToken = passwordResetTokenRepository.findByEmailAndToken(email, code);

        if(optionalToken.isEmpty()){
            res.setState(false);
            res.setMsgDesc("Invalid Code.");
            return res;
        }

        PasswordResetToken token = optionalToken.get();
        if (token.getExpiry().isBefore(LocalDateTime.now())) {
            res.setState(false);
            res.setMsgDesc("Code expired.");
            return res;
        }

        res.setState(true);
        res.setMsgDesc("Code verified.");
        return res;
    }

    @Transactional
    public Result resetPassword(String email, String newPassword) {
        Result res = new Result();
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            res.setState(false);
            res.setMsgDesc("User not found.");
            return res;
        }

        User user = optionalUser.get();
        String hashedPassword = passwordEncoder.encodePassword(newPassword);
        user.setPassword(hashedPassword);
        userRepository.save(user);

        // Remove used token
        passwordResetTokenRepository.deleteByEmail(email);

        res.setState(true);
        res.setMsgDesc("Password reset successful.");
        return res;
    }
}
