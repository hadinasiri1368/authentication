package org.authentication.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.authentication.common.CommonUtils;
import org.authentication.dto.ResponseDto.ExceptionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.sql.SQLException;

@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {
    @ExceptionHandler(value = FeignException.Unauthorized.class)
    public ResponseEntity<ExceptionDto> response(FeignException.Unauthorized e, HttpServletRequest request) {
        ExceptionDto exceptionDto = CommonUtils.getException(e);
        log.info("RequestURL:" + request.getRequestURL() + "  UUID=" + request.getHeader("X-UUID") + "  ServiceUnauthorized:" + (CommonUtils.isNull(exceptionDto) ? e.getMessage().split("]:")[1] : exceptionDto.getErrorMessage()));
        return new ResponseEntity<>(ExceptionDto.builder()
                .errorMessage("unauthorized exception")
                .errorCode(HttpStatus.UNAUTHORIZED.value())
                .uuid(request.getHeader("X-UUID"))
                .errorStatus(HttpStatus.UNAUTHORIZED.value())
                .build(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = FeignException.ServiceUnavailable.class)
    public ResponseEntity<ExceptionDto> response(FeignException.ServiceUnavailable e, HttpServletRequest request) {
        ExceptionDto exceptionDto = CommonUtils.getException(e);
        log.info("RequestURL:" + request.getRequestURL() + "  UUID=" + request.getHeader("X-UUID") + "  ServiceUnavailable:" + (CommonUtils.isNull(exceptionDto) ? e.getMessage().split("]:")[1] : exceptionDto.getErrorMessage()));
        return new ResponseEntity<>(ExceptionDto.builder()
                .errorMessage(CommonUtils.isNull(exceptionDto) ? "internal server error" : exceptionDto.getErrorMessage())
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .uuid(request.getHeader("X-UUID"))
                .errorStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = FeignException.InternalServerError.class)
    public ResponseEntity<ExceptionDto> response(FeignException.InternalServerError e, HttpServletRequest request) {
        ExceptionDto exceptionDto = CommonUtils.getException(e);
        log.info("RequestURL:" + request.getRequestURL() + "  UUID=" + request.getHeader("X-UUID") + "  ServiceInternalServerError:" + (CommonUtils.isNull(exceptionDto) ? e.getMessage().split("]:")[1] : exceptionDto.getErrorMessage()));
        return new ResponseEntity<>(ExceptionDto.builder()
                .errorMessage(CommonUtils.isNull(exceptionDto) ? "internal server error" : exceptionDto.getErrorMessage())
                .errorCode(CommonUtils.isNull(exceptionDto) ? HttpStatus.INTERNAL_SERVER_ERROR.value() : exceptionDto.getErrorCode())
                .uuid(request.getHeader("X-UUID"))
                .errorStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = SQLServerException.class)
    public ResponseEntity<ExceptionDto> handleDuplicateKeyException(SQLServerException e, HttpServletRequest request) {
        ExceptionDto exceptionDto = CommonUtils.getException(e);
        log.info("RequestURL:" + request.getRequestURL() + "  UUID=" + request.getHeader("X-UUID") + "  DuplicateKey:" + e.getMessage());
        return new ResponseEntity<>(ExceptionDto.builder()
                .errorMessage(exceptionDto.getErrorMessage())
                .errorCode(exceptionDto.getErrorCode())
                .uuid(request.getHeader("X-UUID"))
                .errorStatus(HttpStatus.CONFLICT.value())
                .build(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ExceptionDto> response(RuntimeException e, HttpServletRequest request) {
        ExceptionDto exceptionDto = CommonUtils.getException(e);
        log.info("RequestURL:" + request.getRequestURL() + "  UUID=" + request.getHeader("X-UUID") + "  ServiceRuntimeException:" + (!CommonUtils.isNull(exceptionDto) ? exceptionDto.getErrorMessage() : CommonUtils.getMessage(e.getMessage())));
        return new ResponseEntity<>(ExceptionDto.builder()
                .errorMessage(!CommonUtils.isNull(exceptionDto) ? exceptionDto.getErrorMessage() : CommonUtils.getMessage(e.getMessage()))
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .uuid(request.getHeader("X-UUID"))
                .errorStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
