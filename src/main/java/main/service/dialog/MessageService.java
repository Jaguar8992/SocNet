package main.service.dialog;

import main.api.dto.DTOError;
import main.api.dto.DTOErrorDescription;
import main.api.dto.DTOMessage;
import main.api.dto.DTOSuccessfully;
import main.api.dto.dialog.DTOSendMessage;
import main.api.request.dialogs.MessageRequest;
import main.api.response.error.ErrorResponse;
import main.model.entity.Dialog;
import main.model.entity.Message;
import main.model.entity.User;
import main.model.entity.enums.NotificationType;
import main.model.entity.enums.ReadMessageStatus;
import main.model.repository.DialogRepository;
import main.model.repository.MessageRepository;
import main.service.NotificationApi;
import main.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
public class MessageService {

    private final UserService userService;
    private final DialogRepository dialogRepository;
    private final MessageRepository messageRepository;
    private final Logger log = Logger.getLogger(MessageService.class.getName());
    private final NotificationApi notificationApi;

    public MessageService(UserService userService, DialogRepository dialogRepository, MessageRepository messageRepository, NotificationApi notificationApi) {
        this.userService = userService;
        this.dialogRepository = dialogRepository;
        this.messageRepository = messageRepository;
        this.notificationApi = notificationApi;
    }

    public ResponseEntity<?> sendMessage(int id, MessageRequest messageRequest) {

        User currentUser;
        Dialog dialog;
        long timestamp = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toEpochSecond();

        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(new ErrorResponse(
                    DTOError.UNAUTHORIZED.get(),
                    DTOErrorDescription.UNAUTHORIZED.get()));
        }

        Optional<Dialog> optionalDialog = dialogRepository.findDialogById(id);

        if (optionalDialog.isEmpty()) {
            log.error(DTOErrorDescription.BAD_REQUEST);
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        } else dialog = optionalDialog.get();

        int messageId = newMessage(dialog, currentUser, messageRequest.getMessageText()).getId();

        User recipientNotification = currentUser.equals(dialog.getRecipient()) ?
                dialog.getOwner() : dialog.getRecipient();
        notificationApi.createNotification(NotificationType.MESSAGE, recipientNotification, messageId);

        return ResponseEntity.ok(new DTOSuccessfully("String", timestamp, new DTOSendMessage(
                messageId,
                timestamp,
                currentUser.getId(),
                messageRequest.getMessageText())));
    }

    public Message newMessage(Dialog dialog, User author, String textMessage) {

        Message message = new Message();
        message.setMessageText(textMessage);
        message.setAuthor(author);
        message.setTime(LocalDateTime.now());
        message.setReadStatus(ReadMessageStatus.SENT);
        message.setDialog(dialog);
        messageRepository.save(message);

        dialog.setLastMessage(message);
        dialogRepository.save(dialog);

        return message;
    }

    public ResponseEntity<?> readMessage(Integer dialogId, Integer messageId) {

        Optional<Dialog> optionalDialog = dialogRepository.findDialogById(dialogId);
        Message message;

        if (optionalDialog.isEmpty()) {
            log.error(DTOErrorDescription.BAD_REQUEST);
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        }

        Optional<Message> optionalMessage = messageRepository.findById(messageId);

        if (optionalMessage.isEmpty()) {
            log.error(DTOErrorDescription.BAD_REQUEST);
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        } else message = optionalMessage.get();

        message.setReadStatus(ReadMessageStatus.READ);
        messageRepository.save(message);

        return ResponseEntity.ok(new DTOSuccessfully(
                null,
                new Date().getTime() / 1000,
                new DTOMessage()));
    }
}
