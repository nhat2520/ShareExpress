package com.nhat.fileservice.Repository;

import com.nhat.fileservice.Model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    @Query("select f from Folder f where f.parentFolder.id = :folderID and f.ownerId = :userID")
    List<Folder> findAllInFolder(@Param("folderID") Long folderID, @Param("userID") Long userID);

    @Query("select f from Folder f where f.parentFolder.path = :path and f.ownerId = :userID")
    List<Folder> findByPath(@Param("path") String path, @Param("userID") Long userID);

    @Query("select f from Folder f where f.id = :folderID and f.ownerId = :userID")
    Folder findFolderByID(@Param("folderID") Long id, @Param("userID") Long userID);

    boolean existsByPathAndOwnerId(String path, long userID);

    Folder findById(long id);
}
