package site.binghai.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.lib.service.BaseService;
import site.binghai.shop.dao.ProductDao;
import site.binghai.shop.entity.Product;

import java.util.List;

/**
 *
 * @date 2020/2/1 上午11:11
 **/
@Service
public class ProductService extends BaseService<Product> {
    @Autowired
    private ProductDao productDao;

    public List<Product> searchBy(String search) {
        List<Product> pages = emptyList();

        int page = 0;
        List<Product> tmp = productDao.findProductsByOfflineAndTitleLike(Boolean.FALSE, "%" + search + "%",
            new PageRequest(page, 100));
        while (!isEmptyList(tmp)) {
            pages.addAll(tmp);
            page++;
            tmp = productDao.findProductsByOfflineAndTitleLike(Boolean.FALSE, "%" + search + "%",
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
        List<Product> tmp = productDao.findProductsByOfflineAndCategoryId(Boolean.FALSE,category,
            new PageRequest(page, 100));
        while (!isEmptyList(tmp)) {
            pages.addAll(tmp);
            page++;
            tmp = productDao.findProductsByOfflineAndCategoryId(Boolean.FALSE,category,
                new PageRequest(page, 100));
        }
        return pages;
    }
}
