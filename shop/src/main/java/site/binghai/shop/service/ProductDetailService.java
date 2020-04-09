package site.binghai.shop.service;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import site.binghai.lib.service.BaseService;
import site.binghai.lib.utils.SchoolIdThreadLocal;
import site.binghai.shop.entity.ProductDetail;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * @date 2020/2/1 上午11:12
 **/
@Service
public class ProductDetailService extends BaseService<ProductDetail> {

    public ProductDetail findByProductId(Long productId) {
        ProductDetail exp = new ProductDetail();
        exp.setProductId(productId);
        return queryOne(exp);
    }

    @Transactional
    public void copyFromWithMapping(Long from, Map<Long, Long> mapping) {
        for (int i = 0; i < 99; i++) {
            List<ProductDetail> ret = pageQuery(new ProductDetail(), i, 100);
            if (isEmptyList(ret)) {
                break;
            }
            ret.forEach(r -> getDao().deleteById(r.getId()));
        }
        ProductDetail example = new ProductDetail();
        example.setCreated(null);
        example.setCreatedTime(null);
        example.setDeleted(null);
        example.setUpdated(null);
        example.setUpdatedTime(null);
        example.setSchoolId(from);
        Example<ProductDetail> ex = Example.of(example);
        for (int i = 0; i < 99; i++) {
            Page<ProductDetail> page = getDao().findAll(ex, new PageRequest(i, 10));
            if (null == page || isEmptyList(page.getContent())) {
                break;
            }
            for (ProductDetail detail : page.getContent()) {
                ProductDetail tmp = new ProductDetail();
                BeanUtils.copyProperties(detail, tmp);
                tmp.setId(null);
                tmp.setProductId(mapping.get(detail.getProductId()));
                tmp.setSchoolId(SchoolIdThreadLocal.getSchoolId());
                save(tmp);
            }
        }
    }
}
