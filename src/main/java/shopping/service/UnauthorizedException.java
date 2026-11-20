package shopping.service;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("로그인이 필요합니다. 먼저 로그인해 주세요.");
    }
}
