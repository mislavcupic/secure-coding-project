package hr.algebra.semregprojectbackend.exception;


public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() {
        super("Token is expired!");
    }
}
