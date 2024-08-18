package shopping.apps.shopping.api.user.docs;

public class WishlistApiDocs {
    public static final class Create {
        public static final String SUMMARY = "위시리스트 생성";

        public static final String DESCRIPTION = """
            
            """;
    }

    public static final class Delete {
        public static final String SUMMARY = "위시리스트 삭제";

        public static final String DESCRIPTION = """
            
            """;
    }

    public static final class GetAllInfo {
        public static final String SUMMARY = "사용자별 위시리스트 모든 목록 가져오기";

        public static final String DESCRIPTION = """
            
            """;
    }

    public static final class Email {
        public static final String EXAMPLE = "test@test.com";

        public static final String DESCRIPTION = "이메일";

        public static final String REG_EXP_ERROR_MESSAGE = "올바르지 않은 계정 또는 비밀번호입니다.";
    }

    public static final class Password {
        public static final String EXAMPLE = "Test@123";

        public static final String DESCRIPTION = "비밀번호";

        public static final String REG_EXP_ERROR_MESSAGE = "올바르지 않은 계정 또는 비밀번호입니다.";
    }

    public static final class Token {
        public static final String ACCESS_TOKEN_EXAMPLE = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        public static final String ACCESS_TOKEN_DESCRIPTION = "액세스 토큰";

        public static final String REFRESH_TOKEN_EXAMPLE = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        public static final String REFRESH_TOKEN_DESCRIPTION = "리프레쉬 토큰";
    }
}
