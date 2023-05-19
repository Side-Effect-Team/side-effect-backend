package sideeffect.project.security.oauth;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import sideeffect.project.common.exception.JoinException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class OauthExceptionHandler extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request, response);
        }catch (JoinException e){
            setRedirectResponse(response, e);
        }
    }

    private void setRedirectResponse(HttpServletResponse response, JoinException e) throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("message", "회원가입이 필요합니다");
        map.put("email", e.getEmail());
        map.put("providerType", e.getProviderType().toString());
        JSONObject jsonObject = new JSONObject(map);
        response.setStatus(307);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonObject.toString());
    }
}
