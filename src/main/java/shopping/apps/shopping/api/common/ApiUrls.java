package shopping.apps.shopping.api.common;

import lombok.NonNull;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class ApiUrls {
    public static String API_PREFIX;

    public static final class ApiDocs {
        public static final String API_DOCS = "/v3/api-docs/**";

        public static final String UI = "/swagger-ui/**";

        public static final String RESOURCES =   "/swagger-resources/**";
    }

    public static final class User {
        public static final String PREFIX = "/members";

        public static final String REGISTER = "/register";

        public static final String LOGIN = "/login";
    }


    public static final class Product {
        public static final String PREFIX = "/products";

        public static final String GET_ALL_INFO = "";

        public static final String CREATE = "";

        public static final class GetInfo {
            public static final String FULL_URI = "/{productId}";

            public static final String PRODUCT_ID_PATH_VAR = "productId";
        }


        public static final class Update {
            public static final String FULL_URI = "/{productId}";

            public static final String PRODUCT_ID_PATH_VAR = "productId";
        }

        public static final class Delete {
            public static final String FULL_URI = "/{productId}";

            public static final String PRODUCT_ID_PATH_VAR = "productId";
        }
    }

    public static final class Wishlist {
        public static final String PREFIX = "/wishes";

        public static final String CREATE = "";

        public static final String GET_ALL_INFO = "";


        public static final class Delete {
            public static final String FULL_URI = "/{wishId}";

            public static final String WISHLIST_ID_PATH_VAR = "wishId";
        }
    }


    static {
        final Yaml yaml = new Yaml();
        try (final InputStream in = ApiUrls.class.getClassLoader().getResourceAsStream("application.yaml")) {
            if (in == null) {
                throw new RuntimeException("application.yaml not found");
            }

            handleProperty(yaml.load(in));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load application.yaml", e);
        }
    }

    private static void handleProperty(@NonNull final Map<String, Object> property) {
        final Map<String, Object> server = (Map<String, Object>) property.get("server");
        final Map<String, Object> servlet = (Map<String, Object>) server.get("servlet");

        API_PREFIX = (String) servlet.get("context-path");
    }
}
