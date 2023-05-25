package sideeffect.project.security.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.common.exception.InvalidValueException;
import sideeffect.project.dto.user.ResponseUserInfo;

@Component
public class KakaoOAuth implements Oauth{

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String KAKAO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";
    @Override
    public ResponseUserInfo getUserInfo(String token) {
        ResponseEntity<String> responseEntity = sendToServer(token);
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoUser kakaoUser;
        try {
            kakaoUser = objectMapper.readValue(responseEntity.getBody(), KakaoUser.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return ResponseUserInfo.builder()
                .email(kakaoUser.getKakao_account().getEmail())
                .build();
    }
    public ResponseEntity<String> sendToServer(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.exchange(KAKAO_REQUEST_URL, HttpMethod.GET, httpEntity, String.class);
        }catch (HttpClientErrorException e){
            throw new InvalidValueException(ErrorCode.USER_SOCIAL_ACCESS_TOKEN_EXPIRED);
        }
        return responseEntity;
    }

}
