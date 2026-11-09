package io.github.saratrajan.samplems.error;

import feign.RetryableException;
import io.github.saratrajan.samplems.api.dto.ErrorResponse;
import io.github.saratrajan.samplems.web.CorrelationIdFilter;
import java.util.List;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> invalidBody(MethodArgumentNotValidException ex) {
        List<ErrorResponse.FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> new ErrorResponse.FieldViolation(f.getField(), f.getDefaultMessage()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Request body is invalid", violations);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    ResponseEntity<ErrorResponse> invalidParameter(HandlerMethodValidationException ex) {
        List<ErrorResponse.FieldViolation> violations = ex.getParameterValidationResults().stream()
                .flatMap(r -> r.getResolvableErrors().stream()
                        .map(e -> new ErrorResponse.FieldViolation(
                                e instanceof FieldError fe ? fe.getField() : r.getMethodParameter().getParameterName(),
                                e.getDefaultMessage())))
                .toList();
        return build(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", "Request parameter is invalid", violations);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ErrorResponse> unreadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "Request body could not be read", List.of());
    }

    @ExceptionHandler({MissingClientIdException.class, MissingRequestHeaderException.class})
    ResponseEntity<ErrorResponse> missingClient(Exception ex) {
        return build(HttpStatus.BAD_REQUEST, "MISSING_CLIENT_ID", "X-Client-Id header is missing or invalid", List.of());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<ErrorResponse> mediaType(HttpMediaTypeNotSupportedException ex) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE", "Use application/json", List.of());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ErrorResponse> method(HttpRequestMethodNotSupportedException ex) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "Method not allowed", List.of());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    ResponseEntity<ErrorResponse> notFound(OrderNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), List.of());
    }

    @ExceptionHandler(InvalidOrderStateException.class)
    ResponseEntity<ErrorResponse> conflict(InvalidOrderStateException ex) {
        return build(HttpStatus.CONFLICT, "INVALID_STATE", ex.getMessage(), List.of());
    }

    @ExceptionHandler(DownstreamServiceException.class)
    ResponseEntity<ErrorResponse> downstream(DownstreamServiceException ex) {
        String code = "DOWNSTREAM_" + ex.getService().toUpperCase() + "_" + ex.getStatus();
        return build(HttpStatus.BAD_GATEWAY, code, "A dependency failed", List.of());
    }

    @ExceptionHandler(RetryableException.class)
    ResponseEntity<ErrorResponse> timeout(RetryableException ex) {
        return build(HttpStatus.GATEWAY_TIMEOUT, "DOWNSTREAM_TIMEOUT", "A dependency did not respond in time", List.of());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> unexpected(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected error", List.of());
    }

    private static ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message,
                                                       List<ErrorResponse.FieldViolation> violations) {
        return ResponseEntity.status(status).body(ErrorResponse.builder()
                .code(code).message(message).fieldErrors(violations)
                .correlationId(MDC.get(CorrelationIdFilter.MDC_KEY))
                .build());
    }
}
