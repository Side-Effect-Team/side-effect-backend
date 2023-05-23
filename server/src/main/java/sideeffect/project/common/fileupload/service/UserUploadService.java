package sideeffect.project.common.fileupload.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sideeffect.project.common.fileupload.ImageType;

import java.io.File;
import java.io.IOException;

@Service
public class UserUploadService extends FileUploadService{

    @Value("${file.recruit-base-img}")
    private String baseImg;

    public UserUploadService(FilePathService filePathService) {
        super(filePathService, ImageType.USER);
    }

    @Override
    public String storeFile(MultipartFile multipartFile) throws IOException {

        if (validate(multipartFile)) {
            String originalFilename = multipartFile.getOriginalFilename();
            String storeFileName = createStoreFileName(originalFilename);
            multipartFile.transferTo(new File(getFullPath(storeFileName)));
            return storeFileName;
        }

        return baseImg;
    }
}
