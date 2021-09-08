package main.model.repository;

import main.model.entity.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, String> {

    @Query(value = "SELECT fi FROM FileInfo fi WHERE fi.hashFile = :hash AND fi.ownerId.id = :userId ")
    Optional<FileInfo> getFileByHashAndUser(@Param("hash") String hash, @Param("userId") Integer userId);

}
