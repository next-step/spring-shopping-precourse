package shopping.auth.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import shopping.common.error.ErrorCode;
import shopping.common.error.ShoppingException;

public final class BearerToken {

    private static final Pattern FORMAT = Pattern.compile("(?i:Bearer) ([A-Za-z0-9_-]+)");

    private BearerToken() {
    }

    public static String parse(String header) {
        if (header == null) {
            throw new ShoppingException(ErrorCode.INVALID_TOKEN);
        }

        Matcher matcher = FORMAT.matcher(header);
        if (!matcher.matches()) {
            throw new ShoppingException(ErrorCode.INVALID_TOKEN);
        }

        return matcher.group(1);
    }
}
