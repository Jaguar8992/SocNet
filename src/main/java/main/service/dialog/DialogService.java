package main.service.dialog;

import main.api.dto.dialog.DTODialogId;
import main.api.dto.dialog.DTODialogUnreadCount;
import main.api.response.account.Error;
import main.api.dto.DTOError;
import main.api.dto.DTOErrorDescription;
import main.api.response.dialog.DialogResponse;
import main.api.response.dialog.DialogResponseList;
import main.api.response.dialog.MessageResponse;
import main.api.response.dialog.NewDialogResponse;
import main.model.entity.Dialog;
import main.model.entity.Message;
import main.model.entity.User;
import main.model.repository.DialogRepository;
import main.model.repository.MessageRepository;
import main.model.repository.UserRepository;
import main.service.UserService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class DialogService {

    private final UserService userService;
    private final DialogRepository dialogRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final Logger log = Logger.getLogger(DialogService.class.getName());

    @Autowired
    public DialogService(UserService userService, DialogRepository dialogRepository, MessageRepository messageRepository, UserRepository userRepository) {
        this.userService = userService;
        this.dialogRepository = dialogRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> getDialogsUser(String query, Integer offset, Integer itemPerPage) {
        User currentUser;
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOError.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(
                    new Error(DTOError.UNAUTHORIZED.get(), DTOErrorDescription.UNAUTHORIZED.get()));
        }

        Page<Dialog> dialogs = dialogRepository
                .getAllDialog(currentUser.getId(), query, PageRequest.of(offset, itemPerPage));

        List<DialogResponse> data = new ArrayList<>();
        dialogs.forEach(dialog ->
                data.add(new DialogResponse(dialog)));

        long timestamp = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toEpochSecond();

        return ResponseEntity
                .ok(new DialogResponseList<>("string", timestamp, data.size(), offset, itemPerPage, data));
    }

    public ResponseEntity<?> getMessages(Integer id, String query, Integer offset, Integer itemPerPage) {
        User currentUser;
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOError.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(
                    new Error(DTOError.UNAUTHORIZED.get(), DTOErrorDescription.UNAUTHORIZED.get()));
        }

        Dialog dialog = getDialogById(id);

        Page<Message> messages = messageRepository
                .findAllByDialogAndMessageTextContaining(dialog, query, PageRequest.of(offset, itemPerPage));

        List<MessageResponse> data = new ArrayList<>();
        messages.forEach(message -> data.add(new MessageResponse(message)));

        long timestamp = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toEpochSecond();

        return ResponseEntity.ok(new DialogResponseList<>("strings", timestamp, data.size(), offset, itemPerPage, data));
    }

    public Dialog getDialogById(Integer dialogId) {
        return dialogRepository.findDialogById(dialogId)
                .orElseThrow(() -> {
                    log.error(DTOError.BAD_REQUEST.get());
                    return new EntityNotFoundException("Dialog " + dialogId + " not found");
                });
    }

    public ResponseEntity <?> newDialog (List <Integer> userIds){
        User currentUser;
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOError.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(
                    new Error(DTOError.UNAUTHORIZED.get(), DTOErrorDescription.UNAUTHORIZED.get()));
        }

        List <User> users;
        try {
            users = userRepository.getUsersForDialog(userIds);
            users.add(currentUser);
        } catch (Exception ex) {
            log.error(ex);
            return ResponseEntity.badRequest().body(new Error(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.BAD_CREDENTIALS.get()));
        }

        Dialog dialog = new Dialog();

        dialog.setOwner(currentUser);
        dialog.setRecipients(users);
        dialog.setDeleted(false);

        dialogRepository.save(dialog);

        long timestamp = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toEpochSecond();

        return ResponseEntity.ok(new NewDialogResponse("string", timestamp, new DTODialogId(dialog.getId())));
    }

    public ResponseEntity <?> getUnread (){
        User currentUser;
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOError.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(
                    new Error(DTOError.UNAUTHORIZED.get(), DTOErrorDescription.UNAUTHORIZED.get()));
        }

        long unreadCount = messageRepository.getCountOfUnreadMessage(currentUser);
        long timestamp = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toEpochSecond();

        return ResponseEntity.ok(new NewDialogResponse("string", timestamp, new DTODialogUnreadCount(unreadCount)));
    }

}
