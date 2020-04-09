package site.binghai.shop.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.lib.service.BaseService;
import site.binghai.lib.utils.SchoolIdThreadLocal;
import site.binghai.shop.dao.ProductDao;
import site.binghai.shop.entity.Product;
import site.binghai.shop.entity.School;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @date 2020/2/1 上午11:11
 **/
@Service
public class ProductService extends BaseService<Product> {
    @Autowired
    private ProductDao productDao;
    @Autowired
    private SchoolService schoolService;

    public List<Product> searchBy(String search) {
        List<Product> pages = emptyList();

        int page = 0;
        List<Product> tmp = productDao.findProductsBySchoolIdAndOfflineAndTitleLike(SchoolIdThreadLocal.getSchoolId(),
                Boolean.FALSE, "%" + search + "%",
                new PageRequest(page, 100));
        while (!isEmptyList(tmp)) {
            pages.addAll(tmp);
            page++;
            tmp = productDao.findProductsBySchoolIdAndOfflineAndTitleLike(SchoolIdThreadLocal.getSchoolId(),
                    Boolean.FALSE, "%" + search + "%",
                    new PageRequest(page, 100));
        }
        return pages;
    }

    @Override
    protected JpaRepository<Product, Long> getDao() {
        return productDao;
    }

    public List<Product> searchByCategory(Long category) {
        List<Product> pages = emptyList();

        int page = 0;
        List<Product> tmp = productDao.findProductsBySchoolIdAndOfflineAndCategoryId(SchoolIdThreadLocal.getSchoolId(),
                Boolean.FALSE, category,
                new PageRequest(page, 100));
        while (!isEmptyList(tmp)) {
            pages.addAll(tmp);
            page++;
            tmp = productDao.findProductsBySchoolIdAndOfflineAndCategoryId(SchoolIdThreadLocal.getSchoolId(),
                    Boolean.FALSE, category,
                    new PageRequest(page, 100));
        }
        return pages;
    }

    public List<Product> ptSearch() {
        School school = schoolService.findById(SchoolIdThreadLocal.getSchoolId());
        List<Product> products = searchByCategory(school.getPintTuanCategoryId());
        List<Product> ret = products.stream().filter(p -> p.getPtStartTs() < now() && p.getPtEndTs() > now())
                .filter(p -> p.getStock() > 0)
                .collect(Collectors.toList());
        return empty(ret);
    }

    public List<Product> findRecommendPtItems() {
        return ptSearch().stream().filter(p -> p.getRecommend() != null && p.getRecommend())
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<Long,Long> copyFromWithCategoryMapping(Long from, Map<Long, Long> mapping) {
        Map<Long,Long> retMapping = new HashMap<>();
        for (int i = 0; i < 99; i++) {
            List<Product> ret = pageQuery(new Product(), i, 100);
            if (isEmptyList(ret)) {
                break;
            }
            ret.forEach(r -> getDao().deleteById(r.getId()));
        }
        Product example = new Product();
        example.setCreated(null);
        example.setCreatedTime(null);
        example.setDeleted(null);
        example.setUpdated(null);
        example.setUpdatedTime(null);
        example.setSchoolId(from);
        Example<Product> ex = Example.of(example);
        for (int i = 0; i < 99; i++) {
            Page<Product> page = getDao().findAll(ex, new PageRequest(i, 10));
            if (null == page || isEmptyList(page.getContent())) {
                break;
            }
            for (Product product : page.getContent()) {
                Product tmp = new Product();
                BeanUtils.copyProperties(product, tmp);
                tmp.setId(null);
                tmp.setCategoryId(mapping.get(product.getCategoryId()));
                tmp.setSchoolId(SchoolIdThreadLocal.getSchoolId());
                tmp = save(tmp);
                retMapping.put(product.getId(),tmp.getId());
            }
        }
        return retMapping;
    }
}
