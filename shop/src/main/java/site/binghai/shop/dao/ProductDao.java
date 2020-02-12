package site.binghai.shop.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.shop.entity.Product;

import java.util.List;

/**
 *
 * @date 2020/2/1 上午11:14
 **/
public interface ProductDao extends JpaRepository<Product, Long> {
    List<Product> findProductsByOfflineAndTitleLike(Boolean offline, String search, Pageable pageable);

    List<Product> findProductsByOfflineAndCategoryId(Boolean offline, Long cid, Pageable pageable);

    List<Product> findProductsByOfflineAndCategoryIdOrderBySoldDesc(Boolean offline, Long cid, Pageable pageable);

    List<Product> findProductsByOfflineAndCategoryIdOrderByPriceDesc(Boolean offline, Long cid, Pageable pageable);

    List<Product> findProductsByOfflineAndCategoryIdOrderByPriceAsc(Boolean offline, Long cid, Pageable pageable);
}
