package com.tujulishanehub.backend.services;

import com.tujulishanehub.backend.models.User;
import com.tujulishanehub.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // Register user (name + email). Create INACTIVE account and send OTP
    public void registerUser(String name, String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        String otp = generateOtp();
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(name);
            user.setOtp(otp);
            user.setVerified(false);
            user.setStatus("INACTIVE");
            userRepository.save(user);
            emailService.sendEmail(email, "Your verification OTP", "Your OTP is: " + otp);
        } else {
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setOtp(otp);
            newUser.setVerified(false);
            newUser.setStatus("INACTIVE");
            userRepository.save(newUser);
            emailService.sendEmail(email, "Your verification OTP", "Your OTP is: " + otp);
        }
    }

    // Verify OTP and activate user
    public boolean verifyOtp(String email, String otp) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getOtp() != null && user.getOtp().equals(otp)) {
                user.setVerified(true);
                user.setStatus("ACTIVE");
                user.setOtp(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    // Login using email + otp. User must be ACTIVE.
    public boolean loginWithOtp(String email, String otp) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!"ACTIVE".equals(user.getStatus())) {
                return false;
            }
            if (user.getOtp() != null && user.getOtp().equals(otp)) {
                // clear OTP after successful login
                user.setOtp(null);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Helper to (re)send login OTP for ACTIVE users if needed
    public void sendLoginOtp(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String otp = generateOtp();
            user.setOtp(otp);
            userRepository.save(user);
            emailService.sendEmail(email, "Your login OTP", "Your OTP is: " + otp);
        }
    }
}
