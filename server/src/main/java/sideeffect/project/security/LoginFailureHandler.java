package sideeffect.project.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import sideeffect.project.common.dto.ErrorResponse;
import sideeffect.project.common.exception.ErrorCode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        setRedirectResponse(response);
    }

    private void setRedirectResponse(HttpServletResponse response) throws IOException {
        ObjectMapper om = new ObjectMapper();
        ErrorCode errorCode = ErrorCode.USER_UNAUTHENTICATION;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        response.setStatus(400);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(om.writeValueAsString(errorResponse));
    }
}
