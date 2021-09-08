package main.model.repository;

import main.model.entity.UserPrintStatus;
import main.model.entity.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserPrintStatusRepository extends JpaRepository<UserPrintStatus, Integer> {

    @Query("FROM UserPrintStatus WHERE dialogId = :dialogId AND userId = :userId")
    Optional<UserPrintStatus> findByDialogIdAndUserId(@Param("dialogId") Integer dialogId, @Param("userId") Integer userId);

    @Query(value = "SELECT status FROM user_print_status WHERE dialogId = :dialogId AND userId = :userId " +
            "AND time < DATE_SUB(:time, INTERVAL 10 second)", nativeQuery = true)
    UserStatus selectStatus(@Param("time") LocalDateTime time, @Param("dialogId") Integer dialogId, @Param("userId") Integer userId);
}
