package sideeffect.project.security.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import sideeffect.project.dto.user.ResponseUserInfo;

@Component
public class GoogleOAuth implements Oauth{

    private static final String GOOGLE_REQUEST_URL = "https://www.googleapis.com/userinfo/v2/me";
    private final RestTemplate restTemplate = new RestTemplate();
    public ResponseUserInfo getUserInfo(String token){
        ResponseEntity<String> responseEntity = sendToServer(token);
        ObjectMapper objectMapper = new ObjectMapper();
        GoogleUser googleUser;
        try {
            googleUser = objectMapper.readValue(responseEntity.getBody(), GoogleUser.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return ResponseUserInfo.builder()
                .email(googleUser.getEmail())
                .build();
    }

    public ResponseEntity<String> sendToServer(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(GOOGLE_REQUEST_URL, HttpMethod.GET, httpEntity, String.class);
    }
}
