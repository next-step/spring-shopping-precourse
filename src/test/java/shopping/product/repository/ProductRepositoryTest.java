package shopping.product.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import shopping.product.entity.Product;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Test
    void 이름으로_존재_여부_확인() {
        productRepository.save(new Product("돼지뚱냥이", 1000, 1, "https://i.ibb.co/a.jpg"));

        assertThat(productRepository.existsByName("돼지뚱냥이")).isTrue();
        assertThat(productRepository.existsByName("없는상품")).isFalse();
    }

    @Test
    void 자기_자신을_제외한_이름_중복_확인() {
        Product saved = productRepository.save(new Product("돼지뚱냥이", 1000, 1, "https://i.ibb.co/a.jpg"));
        Product other = productRepository.save(new Product("뚱냥이", 1000, 1, "https://i.ibb.co/b.jpg"));

        assertThat(productRepository.existsByNameAndIdNot("돼지뚱냥이", saved.getId())).isFalse();
        assertThat(productRepository.existsByNameAndIdNot("돼지뚱냥이", other.getId())).isTrue();
    }
}
