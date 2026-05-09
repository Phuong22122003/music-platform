package com.MusicPlatForm.notification_service.service.implement;

import com.MusicPlatForm.notification_service.dto.request.EmailResetPasswordRequest;
import com.MusicPlatForm.notification_service.dto.request.NotificationRequest;
import com.MusicPlatForm.notification_service.dto.request.PasswordRequest;
import com.MusicPlatForm.notification_service.dto.response.NotificationResponse;
import com.MusicPlatForm.notification_service.entity.Notification;
import com.MusicPlatForm.notification_service.entity.Token;
import com.MusicPlatForm.notification_service.repository.NotificationRepository;
import com.MusicPlatForm.notification_service.repository.TokenRepository;
import com.MusicPlatForm.notification_service.service.FireBaseService;
import com.MusicPlatForm.notification_service.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class NotificationServiceImplement implements NotificationService{
    NotificationRepository notificationRepository;
    TokenRepository tokenRepository;
    JavaMailSender mailSender;
    SimpMessagingTemplate simpMessagingTemplate;
    FireBaseService fireBaseService;

    public void sendNotificationWebSocket(NotificationRequest request) {
        Notification notification = Notification.builder()
                .senderId(request.getSenderId())
                .recipientId(request.getRecipientId())
                .message(request.getMessage())
                .trackId(request.getTrackId())
                .commentId(request.getCommentId())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        NotificationResponse response = convertTotNotificationResponse(notification);
        simpMessagingTemplate.convertAndSendToUser(
                notification.getRecipientId(),
                "/queue/message",
                response
        );
        Token token = this.tokenRepository.findByUserId(response.getRecipientId());
        fireBaseService.sendNotifcationWithDataToFirebase(token.getToken(), response);

    }

    public void emailNotification(EmailResetPasswordRequest request) {

        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(request.getUserEmail());
            mailMessage.setSubject("Password Reset OTP");
            mailMessage.setText("Your OTP is: " + request.getOtp());
            mailSender.send(mailMessage);
        }
        catch(Exception ex){

        }
    }
    public void emailNewPassword(PasswordRequest passwordRequest){
        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(passwordRequest.getEmail());
            mailMessage.setSubject("Password Reset");
            mailMessage.setText("Your new password is: " + passwordRequest.getPassword());
            mailSender.send(mailMessage);
        }
        catch(Exception ex){

        }
    }

    // Gửi thông tin tới các service khác qua WebSocket
    private void sendToServiceOther(String destination, NotificationRequest request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", request.getRecipientId());
        payload.put("trackId", request.getTrackId());
        payload.put("senderId", request.getSenderId());
        if (request.getCommentId() != null) {
            payload.put("commentId", request.getCommentId());
        }
        simpMessagingTemplate.convertAndSend(destination, payload);
    }

    public List<NotificationResponse> getNotificationByIds(List<String> ids){
        List<Notification> notifications = this.notificationRepository.findAllById(ids);
        Collections.sort(notifications, Comparator.comparing(Notification::getCreatedAt).reversed());
        return notifications.stream().map(n->{
            return convertTotNotificationResponse(n);
        }).toList();
    }
    public List<String> getAllByUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        List<Notification> notifications = this.notificationRepository.findByRecipientIdOrderByCreatedAtAsc(userId);
        Collections.sort(notifications, Comparator.comparing(Notification::getCreatedAt).reversed());
        return notifications.stream().map(n->n.getId()).toList();
    }

    public void markAsRead(List<String>ids){
        List<Notification> notifications = this.notificationRepository.findAllById(ids);
        notifications.forEach(n->n.setRead(true));
        this.notificationRepository.saveAll(notifications);
    }
    private NotificationResponse convertTotNotificationResponse(Notification notification){
         NotificationResponse response = NotificationResponse.builder()
                .id(notification.getId())
                .senderId(notification.getSenderId())
                .recipientId(notification.getRecipientId())
                .message(notification.getMessage())
                .trackId(notification.getTrackId())
                .commentId(notification.getCommentId())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
        if(notification.getCommentId()==null&& notification.getTrackId()==null){
            response.setType("Follow");
        }
        else if(notification.getCommentId()==null){
            response.setType("Like");
        }
        else{
            response.setType("Comment");
        }
        return response;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void saveToke(String token) {
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       String userId = authentication.getName();
       Token entity = tokenRepository.findByUserId(userId);
       if(entity == null){
           entity = Token.builder()
           .token(token)
           .userId(userId)
           .updateAt(LocalDateTime.now()).build();
       }
       entity.setToken(token);
       this.tokenRepository.save(entity);
    }
}
