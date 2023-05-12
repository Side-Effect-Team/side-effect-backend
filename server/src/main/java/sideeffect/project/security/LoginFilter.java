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
    private static final String GOOGLE_TOKEN = "ya29.a0AWY7Ckl--pjLrSHZbT4QMznPEssRKy8cgdK7p83kHTPkedHnvL6M2lXL1PJHQLryh6XXaSFaEppyiKaz6pARm288cCpTaYN9qfOa84z3k7VfZdm7f-MdzNgPtbX17PEzN4_RZEBN075CNy48u_9vp6VeTquLFXfFI0YCrgaCgYKASESARESFQG1tDrpkTll7ths2UbopeSjy1YhnA0173";
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        String token = request.getHeader("Token");
        String providerType = request.getHeader("Provider-Type");
        ResponseEntity<String> authorization_response = sendToAuthorizationServer(token, providerType);
        //log.info(String.valueOf(authorization_response));
        ObjectMapper objectMapper = new ObjectMapper();
        GoogleUser googleUser;
        try {
            googleUser = objectMapper.readValue(authorization_response.getBody(), GoogleUser.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info(googleUser.getId());
        log.info(googleUser.getEmail());
        log.info(googleUser.getName());
        log.info(googleUser.getGiven_name());
        log.info(googleUser.getFamily_name());
        log.info(googleUser.getPicture());
        log.info(googleUser.getLocale());

        setUsernameParameter("email");
        String username = googleUser.getEmail();
        //String username = obtainUsername(request);
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
