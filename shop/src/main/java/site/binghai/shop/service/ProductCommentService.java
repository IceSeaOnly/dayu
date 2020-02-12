package site.binghai.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.lib.service.BaseService;
import site.binghai.shop.dao.ProductCommentDao;
import site.binghai.shop.entity.ProductComment;

import java.util.List;

/**
 *
 * @date 2020/2/1 上午11:12
 **/
@Service
public class ProductCommentService extends BaseService<ProductComment> {
    @Autowired
    private ProductCommentDao productCommentDao;

    public List<ProductComment> findByProductId(Long productId, int limit) {
        return productCommentDao.findAllByProductIdOrderByIdDesc(productId, new PageRequest(0, limit));
    }

    public long countByProductId(Long productId) {
        ProductComment exp = new ProductComment();
        exp.setProductId(productId);
        return count(exp);
    }

    @Override
    protected JpaRepository<ProductComment, Long> getDao() {
        return productCommentDao;
    }
}
