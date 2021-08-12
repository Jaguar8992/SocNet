package main.controller;

import main.service.DialogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dialogs")
public class ApiDialogController {

    private final DialogService dialogService;

    public ApiDialogController(DialogService dialogService) {
        this.dialogService = dialogService;
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
    public ResponseEntity <?> newDialog (@RequestParam (name = "user_ids") List<Integer> userIds){
        return dialogService.newDialog(userIds);
    }

    @GetMapping("/unreaded")
    public ResponseEntity <?> getUnread () {
        return dialogService.getUnread();
    }
}
