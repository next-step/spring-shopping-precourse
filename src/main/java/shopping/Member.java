package shopping;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record Member(
        Long id,
        Email email,
        Password password
) {

}