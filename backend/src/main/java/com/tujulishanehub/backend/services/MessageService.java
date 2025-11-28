package com.tujulishanehub.backend.services;

import com.tujulishanehub.backend.models.Announcement;
import com.tujulishanehub.backend.models.Message;
import com.tujulishanehub.backend.models.User;
import com.tujulishanehub.backend.repositories.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MessageService {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    
    @Autowired
    private MessageRepository messageRepository;
    
    public List<Message> getMessagesForAnnouncement(Announcement announcement) {
        return messageRepository.findByAnnouncementOrderByCreatedAtAsc(announcement);
    }
    
    public Message saveMessage(String messageText, User sender, Announcement announcement) {
        Message message = new Message();
        message.setMessage(messageText);
        message.setSender(sender);
        message.setAnnouncement(announcement);
        return messageRepository.save(message);
    }
    
    public Message updateMessage(Long messageId, String newMessageText) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new RuntimeException("Message not found"));
        message.setMessage(newMessageText);
        return messageRepository.save(message);
    }
    
    public void deleteMessage(Long messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new RuntimeException("Message not found"));
        messageRepository.delete(message);
    }
}