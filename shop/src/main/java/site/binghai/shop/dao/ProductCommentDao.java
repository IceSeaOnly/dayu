package site.binghai.shop.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.shop.entity.ProductComment;

import java.util.List;

/**
 *
 * @date 2020/2/2 上午9:42
 **/
public interface ProductCommentDao extends JpaRepository<ProductComment, Long> {
    List<ProductComment> findAllByProductIdOrderByIdDesc(Long productId, Pageable pageable);
}
