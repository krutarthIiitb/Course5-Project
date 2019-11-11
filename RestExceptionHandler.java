
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ServiceConfiguration.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(ResourceNotFoundException ex, Webrequest req) {
     return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exe.getcode()).message(exe.getErrorMessage()),HTTPStatus.NOT_FOUND);
    }


    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(ResourceNotFoundException ex, Webrequest req) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exe.getcode()).message(exe.getErrorMessage()),HTTPStatus.UNAUTHORIZED);
    }
}

