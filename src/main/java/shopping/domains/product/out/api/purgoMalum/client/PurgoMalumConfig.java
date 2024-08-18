package shopping.domains.product.out.api.purgoMalum.client;

import feign.codec.Decoder;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.nio.charset.StandardCharsets;

@Configuration
public class PurgoMalumConfig {
    @Bean
    public Decoder feignDecoder() {
        final HttpMessageConverter<?> stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        final HttpMessageConverters converters = new HttpMessageConverters(stringConverter);
        return new BooleanResponseDecoder(new ResponseEntityDecoder(new SpringDecoder(() -> converters)));
    }
}
