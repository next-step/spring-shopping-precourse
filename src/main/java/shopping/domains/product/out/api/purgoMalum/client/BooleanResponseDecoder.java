package shopping.domains.product.out.api.purgoMalum.client;

import feign.Response;
import feign.codec.Decoder;
import lombok.NonNull;

import java.io.IOException;
import java.lang.reflect.Type;

public class BooleanResponseDecoder implements Decoder {

    private final Decoder delegate;

    public BooleanResponseDecoder(@NonNull final Decoder delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object decode(
            @NonNull final Response response,
            @NonNull final Type type
    ) throws IOException {
        if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            final String body = (String) delegate.decode(response, String.class);
            return Boolean.parseBoolean(body);
        }
        return delegate.decode(response, type);
    }
}