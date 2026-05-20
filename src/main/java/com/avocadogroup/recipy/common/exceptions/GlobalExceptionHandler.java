package com.avocadogroup.recipy.common.exceptions;

import com.avocadogroup.recipy.common.dtos.ErrorDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice //@ControllerAdvice // Annotation to handle exceptions globally across all controllers
public class GlobalExceptionHandler {
    /**
     * Handles validation errors triggered by {@code @Valid} or {@code @Validated} annotations.
     * Extracts field names and their corresponding error messages.
     *
     * @param exception the validation exception containing field errors
     * @return a map of field names to error messages with a 400 Bad Request status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationViolation(
            MethodArgumentNotValidException exception
    ) {
        // Map to store field-specific validation errors (e.g., "email": "must be valid")
        HashMap<String, String> errors = new HashMap<>();

        // Extract and map each field error from the binding result
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            // EX: ["email", "must be a well-formed email address"]
            errors.put(error.getField(), error.getDefaultMessage());
        });

        // Return bad request error with the errors as the body
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles raw SQL integrity constraint violations.
     * Note: Prefer handling {@code DataIntegrityViolationException} for Spring-level abstraction.
     *
     * @param exception the SQL exception caught during database operations
     * @return a wrapped error message with a 400 Bad Request status
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorDto> handleSQLIntegrityViolation(
            SQLIntegrityConstraintViolationException exception
    ) {
        // If the violation is `Duplicate entry` return 400 error
        return ResponseEntity.badRequest().body(new ErrorDto(exception.getMessage()));
    }

    /**
     * Handles exceptions when an uploaded file exceeds the configured maximum size.
     *
     * @param exception the exception indicating file size limit breach
     * @return a 400 Bad Request response containing the limit violation message
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorDto> handleMaxUploadSizeExceededViolation(
            MaxUploadSizeExceededException exception
    ) {
        // Return bad request error if the maximum upload size exceeded
        return ResponseEntity.badRequest().body(new ErrorDto(exception.getMessage()));
    }

    /**
     * Handles custom exceptions for requested resources that do not exist in the system.
     *
     * @param exception the custom resource not found exception
     * @return a 404 Not Found response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handleResourceNotFoundException(ResourceNotFoundException exception) {
        // Return bad request error if trying to access a missing resource
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(exception.getMessage()));
    }

    /**
     * Handles Spring Data integrity violations, typically used for duplicate entries
     * or foreign key constraint failures.
     *
     * @param exception the data integrity exception
     * @return a 409 Conflict response to indicate data collision
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDto> handleDuplicateResourceException(DataIntegrityViolationException exception) {
        // Return conflict request error if trying to create a record duplicated resource
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDto(exception.getMessage()));
    }

    /**
     * Handles generic business logic violations or malformed requests.
     *
     * @param exception the custom bad request exception
     * @return a 400 Bad Request response
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDto> handleBadRequestException(BadRequestException exception) {
        // Return bad request error if the trying to create a record with bad inputs
        return ResponseEntity.badRequest().body(new ErrorDto(exception.getMessage()));
    }

    /**
     * Handles unexpected internal failures within the application service layer.
     *
     * @param exception the custom internal server exception
     * @return a 500 Internal Server Error response
     */
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorDto> handleInternalServerErrorException(InternalServerErrorException exception) {
        // Return internal server error 500 if any service failed
        return ResponseEntity.internalServerError().body(new ErrorDto(exception.getMessage()));
    }

    /**
     * Handles cases where the HTTP request body is missing or malformed (e.g., invalid JSON (bad request from the user mostly)).
     *
     * @return a 400 Bad Request response with a user-friendly message
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> handleUnReadableMessage() {
        // Return bad request error if the body is unreadable
        return ResponseEntity.badRequest().body(new ErrorDto("Unreadable Message, Please make a valid request body"));
    }

    /**
     * Catch-all handler for unchecked RuntimeExceptions.
     * Logs the actual error details internally for debugging while returning
     * a sanitized message to the client to prevent sensitive data leakage.
     *
     * @param exception the runtime exception caught during execution
     * @return a 400 Bad Request response with a generic error message
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDto> handleRuntimeException(RuntimeException exception) {
        // Return a generic message to the user for security purposes
        return ResponseEntity.badRequest().body(new ErrorDto(exception.getMessage()));
    }

    /**
     * Handles {@link UnauthorizedException} globally across the application.
     * Returns a 401 Unauthorized response with a sanitized error message to prevent
     * sensitive information leakage.
     * * @param exception The exception thrown when authentication fails or is missing.
     * @return A {@link ResponseEntity} containing an {@link ErrorDto} and the 401 status code.
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorDto> handleUnauthorizedException(UnauthorizedException exception) {
        // Return a generic message to the user for security purposes
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDto(exception.getMessage()));
    }
}
