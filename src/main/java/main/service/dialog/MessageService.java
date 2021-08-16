package main.service.dialog;

import main.api.dto.DTOError;
import main.api.dto.DTOErrorDescription;
import main.api.dto.dialog.DTOSendMessage;
import main.api.response.account.Error;
import main.api.response.dialog.NewDialogResponse;
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
import java.util.List;
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

    public ResponseEntity <?> sendMessage (int id, String messageText) {

        User currentUser;
        Dialog dialog;
        long timestamp = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toEpochSecond();

        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(new Error(
                    DTOError.UNAUTHORIZED.get(),
                    DTOErrorDescription.UNAUTHORIZED.get()));
        }

        Optional <Dialog> optionalDialog = dialogRepository.findDialogById(id);
        if (optionalDialog.isEmpty()){
            log.error(DTOErrorDescription.BAD_REQUEST);
            return ResponseEntity.badRequest().body(new Error(
                    DTOError.BAD_REQUEST.get(),
                    DTOErrorDescription.BAD_REQUEST.get()));
        } else dialog = optionalDialog.get();

        int messageId = newMessage(dialog, currentUser, messageText);

        List <User> recipients =  dialog.getRecipients();
        for (User user : recipients) {
            if (!user.equals(currentUser)) {
                notificationApi.createNotification(NotificationType.MESSAGE, user, messageId);
            }
        }

        return ResponseEntity.ok(new NewDialogResponse("String", timestamp, new DTOSendMessage(
                messageId,
                timestamp,
                currentUser.getId(),
                messageText)));
    }

    private int newMessage (Dialog dialog, User author,  String textMessage) {

        Message message = new Message();
        message.setMessageText(textMessage);
        message.setAuthor(author);
        message.setTime(LocalDateTime.now());
        message.setReadStatus(ReadMessageStatus.SENT);
        message.setDialog(dialog);
        messageRepository.save(message);

        dialog.setLastMessage(message);
        dialog.incrementUnread();
        dialogRepository.save(dialog);

        return message.getId();
    }
}
