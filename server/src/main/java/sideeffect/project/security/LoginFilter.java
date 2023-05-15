package sideeffect.project.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import sideeffect.project.dto.user.GoogleUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private static final String GOOGLE_REQUEST_URL = "https://www.googleapis.com/userinfo/v2/me";
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        log.info("LoginFilter 진입");
        String username;
        String token = request.getHeader("Token");
        String providerType = request.getHeader("Provider-Type");

        if(token!=null && providerType!=null){
            ResponseEntity<String> authorization_response = sendToAuthorizationServer(token, providerType);
            ObjectMapper objectMapper = new ObjectMapper();
            GoogleUser googleUser;
            try {
                googleUser = objectMapper.readValue(authorization_response.getBody(), GoogleUser.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            username = googleUser.getEmail();
        }else{
            username = obtainUsername(request);
        }

        username = (username != null) ? username.trim() : "";
        String password = obtainPassword(request);
        password = (password != null) ? password : "";
        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username,
                password);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private ResponseEntity<String> sendToAuthorizationServer(String token, String providerType) {
        ResponseEntity<String> userInfoResponse=null;
        if(providerType.equals("google")){
            userInfoResponse = getUserInfo(GOOGLE_REQUEST_URL, token);
        }
        return userInfoResponse;
    }

    private ResponseEntity<String> getUserInfo(String url, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
    }
}
