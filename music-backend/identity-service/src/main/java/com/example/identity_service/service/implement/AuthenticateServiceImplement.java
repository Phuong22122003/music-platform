package com.example.identity_service.service.implement;

import com.example.identity_service.dto.request.*;
import com.example.identity_service.dto.response.*;
import com.example.identity_service.entity.InvalidatedToken;
import com.example.identity_service.entity.Role;
import com.example.identity_service.entity.User;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.repository.InvalidatedTokenRepository;
import com.example.identity_service.repository.NotificationClient;
import com.example.identity_service.repository.ProfileClient;
import com.example.identity_service.repository.UserRepository;
import com.example.identity_service.service.AuthenticateService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Slf4j
@Service
@Data
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticateServiceImplement  implements AuthenticateService{
    RedisTemplate<String, String> redisTemplate;
    @Value("${app.frontend.url}")
    @NonFinal
    protected String frontendUrl;
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;
    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected Long REFRESHABLE_DURATION;
    @NonFinal
    @Value("${jwt.valid-duration}")
    protected Long VALID_DURATION;
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    ProfileClient profileClient;
    NotificationClient notificationClient;
    PasswordEncoder passwordEncoder;
    private static final long OTP_TTL_MINUTES = 5;
    public AuthenticatedResponse authenticate(AuthenticatedRequest authenticatedRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        String username = authenticatedRequest.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        String password = authenticatedRequest.getPassword();
        boolean authenticated = passwordEncoder.matches(password, user.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        String token = generateToken(user);

        return AuthenticatedResponse.builder().token(token).build();
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        String token = request.getToken();
        SignedJWT jwt =  verifyToken(token, false);
        boolean isLogout = invalidatedTokenRepository.existsById(jwt.getJWTClaimsSet().getJWTID());
        boolean isValid = !isLogout;
        return IntrospectResponse.builder().isAuthenticated(isValid).build();
    }

    public String generateToken(User user) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("QuangTran")
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .claim("scope", buildScope(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = isRefresh
                ? new Date((signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();
        boolean verified = signedJWT.verify(verifier);
        boolean result = verified && expiryTime.after(new Date());
        if (!result) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            SignedJWT jwt = verifyToken(request.getToken(), true);
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .jid(jwt.getJWTClaimsSet().getJWTID())
                    .expiryTime(jwt.getJWTClaimsSet().getExpirationTime())
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception) {
            log.warn("Token already expired");
        }

    }

    public AuthenticatedResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        SignedJWT jwt = verifyToken(request.getToken(), true);
        User user = userRepository.findByUsername(jwt.getJWTClaimsSet().getSubject()).orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .jid(jwt.getJWTClaimsSet().getJWTID())
                .expiryTime(jwt.getJWTClaimsSet().getExpirationTime())
                .build();
        invalidatedTokenRepository.save(invalidatedToken);
        String newToken = generateToken(user);
        return AuthenticatedResponse.builder().token(newToken).build();
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        Set<Role> roles = user.getRoles();
        if (!CollectionUtils.isEmpty(roles)) {
            roles.forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
            });
        }
        return stringJoiner.toString();
    }


    public CheckUsernameResponse checkUsername(String username) {
        if ( userRepository.findByUsername(username).isEmpty()) {
            return  CheckUsernameResponse.builder().isExisted(  false).build();
        } return  CheckUsernameResponse.builder().isExisted(true).build();
    }

    public void sendResetLink(EmailRequest request) {
        ApiResponse<UserProfileResponse> userProfileResponse = profileClient.getProfile(request.getEmail());
        User user = userRepository.findById(userProfileResponse.getData().getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Xoá token cũ (nếu có)
        String oldTokenKey = "reset:user:" + user.getId();
        String oldToken = redisTemplate.opsForValue().get(oldTokenKey);
        if (oldToken != null) {
            redisTemplate.delete("reset:token:" + oldToken);
            redisTemplate.delete(oldTokenKey);
        }

        // Tạo token mới
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("reset:token:" + token, user.getId(), Duration.ofMinutes(30));
        redisTemplate.opsForValue().set(oldTokenKey, token, Duration.ofMinutes(30)); // mapping userId → token

        String resetLink = frontendUrl + "/reset-password?token=" + token;

        log.info("Reset password link: {}", resetLink);
    }


    public void resetPassword(ResetPasswordRequest request) {
        String key = "reset:token:" + request.getToken();
        String userId = redisTemplate.opsForValue().get(key);

        if (userId == null) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setPassword(new BCryptPasswordEncoder().encode(request.getNewPassword()));
        userRepository.save(user);

        redisTemplate.delete(key);
        redisTemplate.delete("reset:user:" + userId);
    }
    
    private String generatePassword() {
        Random random = new Random();
        int otp = 10000000 + random.nextInt(90000000); // 6-digit
        return String.valueOf(otp);
    }
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6-digit
        return String.valueOf(otp);
    }

    public void sendOtp(EmailRequest email){
        ApiResponse<UserProfileResponse> userProfileResponse;
        try {
            userProfileResponse = profileClient.getProfile(email.getEmail());
            
        } catch (Exception e) {
           throw new AppException(ErrorCode.EMAIL_INVALID);
        }
        userRepository.findById(userProfileResponse.getData().getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        String otp = generateOtp();
        String otpKey = "otp:" + email.getEmail();
        redisTemplate.opsForValue().set(otpKey, otp, Duration.ofMinutes(OTP_TTL_MINUTES));
        EmailResetPasswordRequest emailResetPasswordRequest = EmailResetPasswordRequest
                                                            .builder()
                                                            .otp(otp)
                                                            .userEmail(email.getEmail())
                                                            .build();
        notificationClient.sendOtp(emailResetPasswordRequest);
    }
    @Transactional
    public void confirmOtp(ConfirmOtpRequest request){
        String otpKey = "otp:" + request.getEmail();
        String storedOtp = redisTemplate.opsForValue().get(otpKey);

        if (storedOtp == null || !storedOtp.equals(request.getOtp())) {
            throw new AppException(ErrorCode.OTP_INVALID);
        }

        ApiResponse<UserProfileResponse> userProfileResponse = profileClient.getProfile(request.getEmail());
        User user = userRepository.findById(userProfileResponse.getData().getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String newPassword= request.getNewPassword();
        if(newPassword==null){
            newPassword = generatePassword();
            PasswordRequest passwordRequest = PasswordRequest.builder().email(request.getEmail()).password(newPassword).build();
            notificationClient.sendNewPassword(passwordRequest);
        }
        
        // waiting to send email
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        redisTemplate.delete(otpKey); // invalidate OTP
    }
}
