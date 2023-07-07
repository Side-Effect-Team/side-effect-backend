package sideeffect.project.common.fileupload.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sideeffect.project.common.exception.BaseException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.common.fileupload.ImageType;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class UserUploadService extends FileUploadService{

    @Value("${file.user-base-img}")
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

    public String getBaseImgPath(){
        return baseImg;
    }

    public void deleteFile(String fileName) {
        if(!StringUtils.hasText(fileName) || fileName.equals(baseImg)) {
            return;
        }

        File deleteFile = new File(getFullPath(fileName));

        if(!deleteFile.exists()) {
            throw new BaseException(ErrorCode.FILE_NOT_FOUND);
        }

        deleteFile.delete();
    }
}
