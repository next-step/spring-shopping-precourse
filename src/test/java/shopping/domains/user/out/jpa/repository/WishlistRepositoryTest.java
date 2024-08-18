package shopping.domains.user.out.jpa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import shopping.domains.product.core.domain.dto.ProductDto;
import shopping.domains.product.out.jpa.entity.JpaProduct;
import shopping.domains.user.core.domain.dto.UserDto;
import shopping.domains.user.core.domain.dto.WishlistDto;
import shopping.domains.user.core.domain.entity.User;
import shopping.domains.user.out.jpa.entity.JpaUser;
import shopping.domains.user.out.jpa.entity.JpaWishlist;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static shopping.domains.product.test.fixture.ProductTestFixture.JPA_PRODUCT;
import static shopping.domains.product.test.fixture.ProductTestFixture.PRODUCT_DTO;
import static shopping.domains.user.test.fixture.UserTestFixture.JPA_USER;
import static shopping.domains.user.test.fixture.UserTestFixture.USER_DTO;
import static shopping.test.util.JpaTestUtils.persistAndFlush;

@DataJpaTest
class WishlistRepositoryTest {
    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("saveTest")
    void saveTest() {
        // Given
        final JpaUser jpaUser = persistAndFlush(JPA_USER, testEntityManager);
        final JpaProduct jpaProduct = persistAndFlush(JPA_PRODUCT, testEntityManager);
        final WishlistDto wishlistDto = WishlistDto.builder()
                .user(jpaUser.toDto())
                .product(jpaProduct.toDto())
                .build();
        final JpaWishlist jpaWishlist = new JpaWishlist(wishlistDto);

        // When
        final JpaWishlist savedWishlist = wishlistRepository.save(jpaWishlist);

        // Then
        assertThat(savedWishlist.getId()).isNotNull();
        assertThat(savedWishlist.getCreatedAt()).isNotNull();
        assertThat(savedWishlist.getUpdatedAt()).isNotNull();
        assertThat(savedWishlist.toDto().getUser()).isEqualTo(jpaUser.toDto());
        assertThat(savedWishlist.toDto().getProduct()).isEqualTo(jpaProduct.toDto());
    }

    @Test
    @DisplayName("findByUser_IdAndProduct_IdTest")
    void findByUser_IdAndProduct_IdTest() {
        // Given
        final JpaUser jpaUser = persistAndFlush(JPA_USER, testEntityManager);
        final JpaProduct jpaProduct = persistAndFlush(JPA_PRODUCT, testEntityManager);
        final WishlistDto wishlistDto = WishlistDto.builder()
                .user(jpaUser.toDto())
                .product(jpaProduct.toDto())
                .build();
        final JpaWishlist jpaWishlist = persistAndFlush(new JpaWishlist(wishlistDto), testEntityManager);

        // When
        final Optional<JpaWishlist> savedWishlist = wishlistRepository.findByUser_IdAndProduct_Id(
                jpaUser.getId(),
                jpaProduct.getId()
        );

        // Then
        assertThat(savedWishlist.isPresent()).isTrue();
        assertThat(savedWishlist.get()).isEqualTo(jpaWishlist);
    }
}