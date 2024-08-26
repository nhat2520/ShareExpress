package com.nhat.fileservice.Response;

import com.nhat.fileservice.Model.File;
import com.nhat.fileservice.Model.Folder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilesResponse {
    List<Folder> folderList;
    List<File> fileList;
}
