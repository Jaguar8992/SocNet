package main.controller;

import lombok.RequiredArgsConstructor;
import main.api.response.account.Error;
import main.api.dto.DTOError;
import main.service.notification.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class ApiNotificationController {

    private final NotificationService notificationService;


    @GetMapping("/api/v1/notifications")
    private ResponseEntity<?> getMyNotifications(
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer itemPerPage,
            Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(
                    new Error(DTOError.INVALID_REQUEST.get(), DTOError.UNAUTHORIZED.get()), HttpStatus.UNAUTHORIZED);
        }

        return notificationService.getNotifications(offset, itemPerPage);
    }

    @PutMapping("/api/v1/notifications")
    private ResponseEntity<?> readMyNotifications(
            @RequestParam(required = false, defaultValue = "0") Integer id,
            @RequestParam(required = false, defaultValue = "20") Boolean all,
            Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>(
                    new Error(DTOError.INVALID_REQUEST.get(), DTOError.UNAUTHORIZED.get()), HttpStatus.UNAUTHORIZED);
        }

        System.out.println(id + " " + all);
        return notificationService.reedNotifications(all, id, principal);
    }

}
