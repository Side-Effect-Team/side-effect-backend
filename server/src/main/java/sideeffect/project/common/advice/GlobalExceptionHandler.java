package sideeffect.project.common.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sideeffect.project.common.dto.ErrorResponse;
import sideeffect.project.common.exception.BaseException;
import sideeffect.project.common.exception.ErrorCode;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BaseException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse.of(errorCode);
        return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
    }
}
