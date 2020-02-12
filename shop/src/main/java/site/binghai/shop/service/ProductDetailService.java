package site.binghai.shop.service;

import org.springframework.stereotype.Service;
import site.binghai.lib.service.BaseService;
import site.binghai.shop.entity.ProductDetail;

/**
 *
 * @date 2020/2/1 上午11:12
 **/
@Service
public class ProductDetailService extends BaseService<ProductDetail> {

    public ProductDetail findByProductId(Long productId) {
        ProductDetail exp = new ProductDetail();
        exp.setProductId(productId);
        return queryOne(exp);
    }
}
