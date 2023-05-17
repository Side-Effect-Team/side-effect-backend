package sideeffect.project.common.fileupload.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sideeffect.project.common.fileupload.FilePath;
import sideeffect.project.common.fileupload.ImageType;

@Service
@RequiredArgsConstructor
public class FilePathService {

    private final FilePath filePath;

    public String getFullPath(String fileName, ImageType type) {
        return matchImageType(type) + fileName;
    }

    private String matchImageType(ImageType imageType) {
        if(imageType.equals(ImageType.USER)) {
            return filePath.getUser();
        } else if(imageType.equals(ImageType.RECRUIT)) {
            return filePath.getRecruit();
        } else if(imageType.equals(ImageType.FREE)) {
            return filePath.getFree();
        }

        return null;
    }

}
