package main.controller;

import main.api.request.dialogs.MessageRequest;
import main.api.request.dialogs.NewDialogRequest;
import main.service.dialog.ActivityService;
import main.service.dialog.DialogService;
import main.service.dialog.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dialogs")
public class ApiDialogController {

    private final DialogService dialogService;
    private final MessageService messageService;
    private final ActivityService activityService;

    public ApiDialogController(DialogService dialogService, MessageService messageService, ActivityService activityService) {
        this.dialogService = dialogService;
        this.messageService = messageService;
        this.activityService = activityService;
    }

    @GetMapping("")
    public ResponseEntity<?> getAllDialog(@RequestParam(defaultValue = "") String query,
                                          @RequestParam(defaultValue = "0") Integer offset,
                                          @RequestParam(defaultValue = "20") Integer itemPerPage) {
        return dialogService.getDialogsUser(query, offset, itemPerPage);
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<?> getMessagesFromDialog(@PathVariable Integer id,
                                                   @RequestParam(defaultValue = "") String query,
                                                   @RequestParam(defaultValue = "0") Integer offset,
                                                   @RequestParam(defaultValue = "20") Integer itemPerPage) {

        return dialogService.getMessages(id, query, offset, itemPerPage);
    }

    @PostMapping("")
    public ResponseEntity<?> newDialog(@RequestBody NewDialogRequest request) {
        return dialogService.newDialog(request.getUsersIds());
    }

    @GetMapping("/unreaded")
    public ResponseEntity<?> getUnread() {
        return dialogService.getUnread();
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<?> sendMessage(@PathVariable Integer id, @RequestBody MessageRequest messageRequest) {
        return messageService.sendMessage(id, messageRequest);
    }

    @PostMapping("/{id}/activity/{user_id}")
    public ResponseEntity<?> getPrintingStatus(@PathVariable Integer id, @PathVariable(name = "user_id") Integer userId) {
        return activityService.getPrintingStatus(id, userId);
    }

    @GetMapping("/{id}/activity/{user_id}")
    public ResponseEntity<?> getOnlineStatus(@PathVariable Integer id, @PathVariable(name = "user_id") Integer userId) {
        return activityService.getOnlineStatus(id, userId);
    }

    @PutMapping("/{dialog_id}/messages/{message_id}/read")
    public ResponseEntity<?> readMessage(@PathVariable(name = "dialog_id") Integer dialogId,
                                         @PathVariable(name = "message_id") Integer messageId) {
        return messageService.readMessage(dialogId, messageId);
    }
}
