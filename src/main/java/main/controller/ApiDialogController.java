package main.controller;

import main.api.request.NewDialogRequest;
import main.service.dialog.DialogService;
import main.service.dialog.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dialogs")
public class ApiDialogController {

    private final DialogService dialogService;
    public final MessageService messageService;

    public ApiDialogController(DialogService dialogService, MessageService messageService) {
        this.dialogService = dialogService;
        this.messageService = messageService;
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

    @PostMapping("/")
    public ResponseEntity <?> newDialog (@RequestBody NewDialogRequest request){
        return dialogService.newDialog(request.getUserIds());
    }

    @GetMapping("/unreaded")
    public ResponseEntity <?> getUnread () {
        return dialogService.getUnread();
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity <?> sendMessage (@PathVariable Integer id, @RequestParam(name = "message_text") String messageText){
        return messageService.sendMessage(id, messageText);
    }
}
