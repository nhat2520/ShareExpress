package com.nhat.fileservice.Repository;

import com.nhat.fileservice.Model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    @Query("select f from File f where f.folder.id = :folderID and f.ownerId = :userID")
    List<File> findByFolderId(@Param("folderID") Long folderID, @Param("userID") Long userID);

    @Query("select f from File f where f.folder.path = :path and f.ownerId = :userID")
    List<File> findByPath(@Param("path") String path, @Param("userID") Long userID);

    @Query("select f from File f where f.id = :id and f.ownerId = :userID")
    File findById(@Param("id") Long id, @Param("userID") Long userID);

    boolean existsByPathAndOwnerId(String path, Long userID);
//    @Query("delete from File f where f.id = :id and f.ownerId = :userID")
//    void deleteById(@Param("id") Long id, @Param("userID") Long userID);
}