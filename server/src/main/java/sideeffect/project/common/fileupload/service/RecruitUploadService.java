package sideeffect.project.common.fileupload.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sideeffect.project.common.fileupload.ImageType;

import java.io.File;
import java.io.IOException;

@Service
public class RecruitUploadService extends FileUploadService {

    @Value("${file.recruit-base-img}")
    private String baseImg;

    public RecruitUploadService(FilePathService filePathService) {
        super(filePathService, ImageType.RECRUIT);
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
