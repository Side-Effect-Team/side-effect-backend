package sideeffect.project.common.fileupload.service;

import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sideeffect.project.common.fileupload.ImageType;

@Service
public class FreeBoardUploadService extends FileUploadService {

    @Value("${file.free-base-img}")
    private String baseImg;

    public FreeBoardUploadService(FilePathService filePathService) {
        super(filePathService, ImageType.FREE);
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
