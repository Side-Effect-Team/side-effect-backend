package sideeffect.project.common.fileupload;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class FilePath {

    @Value("${file.user}")
    private String user;

    @Value("${file.recruit}")
    private String recruit;

    @Value("${file.free}")
    private String free;

}
