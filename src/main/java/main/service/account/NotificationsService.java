package main.service.account;

import main.api.dto.DTOError;
import main.api.dto.DTOErrorDescription;
import main.api.dto.DTOMessage;
import main.api.dto.DTOSuccessfully;
import main.api.request.account.DTONotification;
import main.api.response.CommonResponseList;
import main.api.response.error.ErrorResponse;
import main.model.entity.NotificationSetting;
import main.model.entity.User;
import main.model.entity.enums.NotificationType;
import main.model.repository.NotificationRepository;
import main.model.repository.NotificationSettingRepository;
import main.model.repository.UserRepository;
import main.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationsService {

    private final Logger log = Logger.getLogger(NotificationsService.class.getName());
    private final UserService userService;
    private final UserRepository userRepository;
    private final NotificationSettingRepository settingRepository;

    @Autowired
    public NotificationsService(NotificationRepository typeRepository, UserService userService, UserRepository userRepository, NotificationSettingRepository settingRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.settingRepository = settingRepository;
    }

    public ResponseEntity<?> createResponse(NotificationType notificationType, boolean isEnable) {
        NotificationSetting setting;
        User currentUser;

        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(new ErrorResponse(
                    DTOError.UNAUTHORIZED.get(),
                    DTOErrorDescription.UNAUTHORIZED.get()));
        }

        if (notificationType == null) {
            log.error(DTOErrorDescription.BAD_CREDENTIALS.get());
            return ResponseEntity.badRequest().body(new ErrorResponse(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.BAD_CREDENTIALS.get()));
        }

        Optional<NotificationSetting> notificationSettingOptional =
                settingRepository.findByTypeAndPersonId(notificationType, currentUser);
        setting = notificationSettingOptional.orElseGet(() -> createSetting(notificationType, currentUser));
        setting.setIsEnable((byte) (isEnable ? 1 : 0));
        settingRepository.save(setting);

        log.info("Notification set");

        return ResponseEntity.ok(new DTOSuccessfully(
                null,
                new Date().getTime() / 1000,
                new DTOMessage()));
    }

    public ResponseEntity<?> getNotifications() {

        User currentUser;

        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(new ErrorResponse(
                    DTOError.UNAUTHORIZED.get(),
                    DTOErrorDescription.UNAUTHORIZED.get()));
        }

        List<NotificationSetting> settings = settingRepository.findByUser(currentUser);
        List<DTONotification> notificationSetting = new ArrayList<>();

        for (NotificationSetting setting : settings) {
            DTONotification notification = new DTONotification();
            notification.setEnable(setting.getIsEnable() == 1);
            notification.setNotificationType(setting.getType());
            notificationSetting.add(notification);
        }

        log.info("Get notification");

        return ResponseEntity.ok(new CommonResponseList<>(null, notificationSetting));
    }

    private NotificationSetting createSetting(NotificationType type, User user) {
        NotificationSetting setting = new NotificationSetting();
        setting.setType(type);
        setting.setUser(user);
        return setting;
    }
}
