package sideeffect.project.common.fileupload.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import sideeffect.project.common.fileupload.ImageType;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class FileUploadService {

    private final FilePathService filePathService;
    private final ImageType imageType;
    public abstract String storeFile(MultipartFile multipartFile) throws IOException;

    public String getFullPath(String fileName) {
        return filePathService.getFullPath(fileName, imageType);
    }

    protected boolean validate(MultipartFile multipartFile) {
        if (multipartFile == null) {
            return false;
        }
        return !multipartFile.isEmpty();
    }

    protected String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

}
