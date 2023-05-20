package sideeffect.project.security.oauth;


import lombok.Data;

@Data
public class KakaoUser {
    private String id;
    private String connected_at;
    private Properities properties;
    private KakaoAccount kakao_account;

    @Data
    public class Properities{
        private String profile_image;
        private String thumbnail_image;
    }

    @Data
    public class KakaoAccount{
        private Boolean profile_image_needs_agreement;
        private Profile profile;
        private Boolean has_email;
        private Boolean email_needs_agreement;
        private Boolean is_email_valid;
        private Boolean is_email_verified;
        private String email;

        @Data
        public class Profile{
            private String thumbnail_image_url;
            private String profile_image_url;
            private Boolean is_default_image;
        }
    }
}
