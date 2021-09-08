package main.service.dialog;

import main.api.dto.DTOError;
import main.api.dto.DTOErrorDescription;
import main.api.dto.DTOSuccessfully;
import main.api.dto.dialog.DTODialogId;
import main.api.dto.dialog.DTODialogUnreadCount;
import main.api.response.PageCommonResponseList;
import main.api.response.dialog.DialogResponse;
import main.api.response.dialog.MessageResponse;
import main.api.response.error.ErrorResponse;
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
    private final MessageService messageService;
    private final UserRepository userRepository;
    private final Logger log = Logger.getLogger(DialogService.class.getName());

    @Autowired
    public DialogService(UserService userService, DialogRepository dialogRepository, MessageRepository messageRepository, MessageService messageService, UserRepository userRepository) {
        this.userService = userService;
        this.dialogRepository = dialogRepository;
        this.messageRepository = messageRepository;
        this.messageService = messageService;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> getDialogsUser(String query, Integer offset, Integer itemPerPage) {
        User currentUser;
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOError.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOErrorDescription.UNAUTHORIZED.get()));
        }

        Page<Dialog> dialogs = dialogRepository
                .getAllDialog(currentUser.getId(), query, PageRequest.of(offset, itemPerPage));

        List<DialogResponse> data = new ArrayList<>();
        dialogs.forEach(dialog ->
        {
            Long unreadMessage = dialogRepository.calculateUnreadMessage(currentUser, dialog);
            data.add(new DialogResponse(dialog, currentUser, unreadMessage));
        });

        return ResponseEntity
                .ok(new PageCommonResponseList<>("string", data.size(), offset, itemPerPage, data));
    }

    public ResponseEntity<?> getMessages(Integer id, String query, Integer offset, Integer itemPerPage) {
        User currentUser;
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOError.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOErrorDescription.UNAUTHORIZED.get()));
        }

        Dialog dialog = getDialogById(id);

        Page<Message> messages = messageRepository
                .findAllByDialogAndMessageTextContaining(dialog, query, PageRequest.of(offset, itemPerPage));

        List<MessageResponse> data = new ArrayList<>();
        messages.forEach(message -> data.add(new MessageResponse(message, currentUser)));

        return ResponseEntity.ok(new PageCommonResponseList<>("strings", data.size(), offset, itemPerPage, data));
    }

    public Dialog getDialogById(Integer dialogId) {
        return dialogRepository.findDialogById(dialogId)
                .orElseThrow(() -> {
                    log.error(DTOError.BAD_REQUEST.get());
                    return new EntityNotFoundException("Dialog " + dialogId + " not found");
                });
    }

    public ResponseEntity<?> newDialog(List<Integer> userIds) {

        User currentUser;
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOError.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOErrorDescription.UNAUTHORIZED.get()));
        }

        List<User> users = userRepository.getUsersForDialog(userIds);
        if (users.isEmpty() || currentUser.equals(users.get(0))) {
            log.error(DTOError.BAD_REQUEST.get());
            return ResponseEntity.status(400).body(
                    new ErrorResponse(DTOError.BAD_REQUEST.get(), DTOErrorDescription.BAD_REQUEST.get()));
        }


        List<Dialog> dialogFor2Users = dialogRepository.getDialogFor2Users(currentUser, users.get(0));

        Dialog dialog;
        StringBuilder sb = new StringBuilder();
        if (dialogFor2Users.isEmpty()) {
            sb.append(users.get(0).getLastName());
            sb.append(" ");
            sb.append(users.get(0).getFirstName());

            dialog = new Dialog();

            dialog.setOwner(currentUser);
            dialog.setRecipient(users.get(0));
            dialog.setDeleted(false);
            Dialog save = dialogRepository.save(dialog);

            Message firstMessage = messageService.newMessage(save, currentUser,
                    "Новый диалог с пользователем " + sb.toString());

        } else {
            dialog = dialogFor2Users.get(0);
        }

        long timestamp = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toEpochSecond();

        return ResponseEntity.ok(new DTOSuccessfully("string", timestamp, new DTODialogId(dialog.getId())));
    }

    public ResponseEntity<?> getUnread() {
        User currentUser;
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOError.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOErrorDescription.UNAUTHORIZED.get()));
        }

        long unreadCount = messageRepository.getCountOfUnreadMessage(currentUser);
        long timestamp = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toEpochSecond();

        return ResponseEntity.ok(new DTOSuccessfully("string", timestamp, new DTODialogUnreadCount(unreadCount)));
    }

}
