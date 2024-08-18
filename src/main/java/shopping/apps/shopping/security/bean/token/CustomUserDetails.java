package shopping.apps.shopping.security.bean.token;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * 인증된 사용자 정보입니다.
 * <p>
 * JWT를 사용하기 때문에 username과 password는 사용하지 않습니다.
 */
public record CustomUserDetails(UUID id) implements UserDetails {
    /**
     * 사용하지 않는 메소드입니다.
     * */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    /**
     * 사용하지 않는 메소드입니다.
     * */
    @Override
    public String getPassword() {
        return null;
    }

    /**
     * 사용하지 않는 메소드입니다.
     *
     * throw 하면 정상 응답임에도 에러가 발생합니다.
     * */
    @Override
    public String getUsername() {
        return null;
    }
}
