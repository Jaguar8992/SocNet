package main.model.repository;

import main.model.entity.NotificationBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationBaseRepository extends JpaRepository<NotificationBase, Integer> {
}
