package sideeffect.project.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import sideeffect.project.common.dto.ErrorResponse;
import sideeffect.project.common.exception.AuthException;
import sideeffect.project.common.exception.ErrorCode;
import sideeffect.project.common.exception.JoinException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //refactor: GlobalException Bean 주입해서 보내기
        String token = request.getHeader("Token");
        String providerType = request.getHeader("ProviderType");

        try{
            filterChain.doFilter(request, response);
        }catch (AuthException e){
            setErrorResponse(response, e);
        }catch (JoinException e){
            if(token!=null && providerType!=null){
                setRedirectResponse(response, e.getEmail());
            }else{
                setErrorResponse(response, new AuthException(ErrorCode.USER_UNAUTHENTICATION));
            }
        }
    }

    private void setRedirectResponse(HttpServletResponse response, String email) throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("message", "회원가입이 필요합니다");
        map.put("email", email);
        JSONObject jsonObject = new JSONObject(map);
        response.setStatus(307);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonObject.toString());
    }

    private void setErrorResponse(HttpServletResponse response, AuthException e) throws IOException {
        ObjectMapper om = new ObjectMapper();
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        response.setStatus(401);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(om.writeValueAsString(errorResponse));
    }
}
