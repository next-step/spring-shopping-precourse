package shopping.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shopping.auth.UnauthorizedException;
import shopping.member.exception.DuplicateEmailException;
import shopping.member.exception.InvalidCredentialsException;
import shopping.product.exception.InvalidProductNameException;
import shopping.product.exception.ProductNotFoundException;
import shopping.product.exception.ProfanityCheckUnavailableException;
import shopping.wish.exception.DuplicateWishException;
import shopping.wish.exception.WishAccessDeniedException;
import shopping.wish.exception.WishNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidProductNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidProductName(InvalidProductNameException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProductNotFound(ProductNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidRequest(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return new ErrorResponse(message);
    }

    @ExceptionHandler(ProfanityCheckUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleProfanityCheckUnavailable(ProfanityCheckUnavailableException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateEmail(DuplicateEmailException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidCredentials(InvalidCredentialsException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorized(UnauthorizedException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(WishNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleWishNotFound(WishNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(WishAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleWishAccessDenied(WishAccessDeniedException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DuplicateWishException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateWish(DuplicateWishException e) {
        return new ErrorResponse(e.getMessage());
    }
}
