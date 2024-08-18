package shopping.apps.shopping.api.product.docs;

public class ProductApiDocs {
    public static final class Create {
        public static final String SUMMARY = "상품 등록";

        public static final String DESCRIPTION = """
            
            """;
    }

    public static final class Update {
        public static final String SUMMARY = "상품 정보 수정";

        public static final String DESCRIPTION = """
            
            """;
    }

    public static final class Delete {
        public static final String SUMMARY = "상품 삭제";

        public static final String DESCRIPTION = """
            
            """;
    }

    public static final class GetInfo {
        public static final String SUMMARY = "상품 정보 조회";

        public static final String DESCRIPTION = """
            
            """;
    }

    public static final class GetAllInfo {
        public static final String SUMMARY = "모든 상품 정보 조회";

        public static final String DESCRIPTION = """
            
            """;
    }
    public static final class Name {
        public static final String EXAMPLE = "바나나우유";

        public static final String DESCRIPTION = "상품 이름";

        public static final String REG_EXP_ERROR_MESSAGE = "올바르지 않은 이름입니다.";
    }

    public static final class Price {
        public static final String DESCRIPTION = "상품 가격";
    }

    public static final class Image {
        public static final String DOWNLOAD_URL_EXAMPLE = "https://www.example.com/image.png";
        public static final String DOWNLOAD_URL_DESCRIPTION = "이미지 다운로드 경로";

        public static final String DOWNLOAD_URL_REG_EXP_ERROR_MESSAGE = "올바르지 않은 이미지 다운로드 경로 형식입니다.";
    }
}
