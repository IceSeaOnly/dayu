package site.binghai.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.shop.entity.Product;

import java.util.List;
import java.util.Random;

/**
 * @date 2020/2/2 上午10:01
 **/
@Service
public class RecommendService {
    @Autowired
    private ProductService productService;

    public List<Product> recommend(int size) {
        Long total = productService.count();
        int pages = total.intValue() / size;
        int page = 0;
        if (pages > 0) {
            Random random = new Random();
            page = random.nextInt(pages + 1);
        }

        return productService.pageQuery(new Product(), page, size);
    }
}
