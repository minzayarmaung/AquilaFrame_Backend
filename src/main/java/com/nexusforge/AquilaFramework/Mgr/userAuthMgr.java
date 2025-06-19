package com.nexusforge.AquilaFramework.Mgr;

import com.nexusforge.AquilaFramework.Dao.userAuthDao;
import com.nexusforge.AquilaFramework.Entity.PasswordResetToken;
import com.nexusforge.AquilaFramework.Entity.Result;
import com.nexusforge.AquilaFramework.Entity.User;
import com.nexusforge.AquilaFramework.Repository.PasswordResetTokenRepository;
import com.nexusforge.AquilaFramework.Repository.UserRepository;
import com.nexusforge.AquilaFramework.Util.passwordEncoder;
import com.nexusforge.AquilaFramework.Util.serverUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
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
    public Result verifySignUpCode(Map<String , String> body) {
        Result res = new Result();
        String email = body.get("email");
        String code = body.get("code");
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

        // Remove used token
        passwordResetTokenRepository.deleteByEmail(email);

        res.setState(true);
        res.setMsgCode("200");
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

    public Result signUpUserMgr(Map<String, String> body) {
        Result res = new Result();
        String email = body.get("email");
        String userName = body.get("username");
        try {
            res = checkUserEmailAlreadyExist(email);
            if(res.isState()){
                res = checkUserNameAlreadyExist(userName);
            }
            if(res.isState()){
                res = sendSignupVerifyEmail(email);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public Result saveNewUser(Map<String, String> body) {
        Result res = new Result();
        User newUser = new User();

        try {
            newUser.setCreateddate(serverUtil.getLocalDate());
            newUser.setModifieddate(serverUtil.getLocalDate());
            newUser.setUsername(body.get("username"));
            newUser.setStatus(1);
            newUser.setEmail(body.get("email"));
            newUser.setPassword(passwordEncoder.encodePassword(body.get("password")));
            newUser.setPhone(body.get("phone"));

            newUser.setParentid(parseOrDefault(body.get("parentid"), 0));
            newUser.setN1(parseOrDefault(body.get("n1"), 0));
            newUser.setN2(parseOrDefault(body.get("n2"), 0));
            newUser.setN3(parseOrDefault(body.get("n3"), 0));
            newUser.setN4(parseOrDefault(body.get("n4"), 0));
            newUser.setN5(parseOrDefault(body.get("n5"), 0));

            res = userAuthDao.saveSignupUser(newUser);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public Result sendSignupVerifyEmail(String email) {
        Result res = new Result();
        String code = serverUtil.generateRandomCode();

        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(15);

        // Save new token
        PasswordResetToken resetToken = new PasswordResetToken(email , code , expiryTime);
        passwordResetTokenRepository.save(resetToken);

        res = userAuthDao.sendUserVerifyEmail(email , code);
        return res;
    }

    private Result checkUserNameAlreadyExist(String username) {
        Result res = new Result();
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            res.setState(false);
            res.setMsgDesc("""
                    Username Already Exists. Please choose another Username. 
                    """);
            res.setMsgCode("200");
        } else {
            res.setState(true);
        }
        return res;
    }

    public Result checkUserEmailAlreadyExist(String email) {
        Result res = new Result();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            res.setState(false);
            res.setMsgDesc("Email Already Registered !");
            res.setMsgCode("200");
        } else {
            res.setState(true);
        }
        return res;
    }

    public Result checkEmailExistInSystem(String email) {
        Result res = new Result();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            res.setState(true);
            res.setMsgCode("200");
            res.setMsgDesc("Email exists in System.");
        } else {
            res.setState(false);
            res.setMsgCode("500");
            res.setMsgDesc("Email is not Registered in System.");
        }
        return res;
    }

    private int parseOrDefault(String value, int defaultVal) {
        try {
            return value != null ? Integer.parseInt(value) : defaultVal;
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }


}
