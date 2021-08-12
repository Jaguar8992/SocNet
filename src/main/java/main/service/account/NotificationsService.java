package main.service.account;

import main.api.request.account.Notification;
import main.api.response.account.Error;
import main.api.response.account.ErrorListResponse;
import main.api.dto.DTOSuccessfully;
import main.api.dto.DTOError;
import main.api.dto.DTOErrorDescription;
import main.api.dto.DTOMessage;
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

        //Проверка авторизирвоан ли пользователь
        try {
            currentUser = userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOErrorDescription.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(new Error(
                    DTOError.UNAUTHORIZED.get(),
                    DTOErrorDescription.UNAUTHORIZED.get()));
        }

        //Корректность тип уведомления
        if (notificationType == null) {
            log.error(DTOErrorDescription.BAD_CREDENTIALS.get());
            return ResponseEntity.badRequest().body(new Error(
                    DTOError.INVALID_REQUEST.get(),
                    DTOErrorDescription.BAD_CREDENTIALS.get()));
        }

        //Проверка существует ли у пользователя настройки по даному типу уведомления,
        // если нет то они будут созданы
        Optional<NotificationSetting> notificationSettingOptional =
                settingRepository.findByTypeAndPersonId(notificationType, currentUser.getId());
        setting = notificationSettingOptional.orElseGet(() -> createSetting(notificationType, currentUser.getId()));
        //Сохраняем настройки
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
            return ResponseEntity.status(401).body(new Error(
                    DTOError.UNAUTHORIZED.get(),
                    DTOErrorDescription.UNAUTHORIZED.get()));
        }

        List<NotificationSetting> settings = settingRepository.findByUserId(currentUser.getId());
        List<Notification> notificationSetting = new ArrayList<>();

        for (NotificationSetting setting : settings) {
            Notification notification = new Notification();
            notification.setEnable(setting.getIsEnable() == 1);
            notification.setNotificationType(setting.getType());
            notificationSetting.add(notification);
        }

        log.info("Get notification");

        return ResponseEntity.ok(new ErrorListResponse(notificationSetting));
    }

    private NotificationSetting createSetting(NotificationType type, int userId) {
        NotificationSetting setting = new NotificationSetting();
        setting.setType(type);
        setting.setUserId(userId);
        return setting;
    }
}
