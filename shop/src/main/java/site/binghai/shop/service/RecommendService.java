package site.binghai.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.shop.entity.Product;

import java.util.List;

/**
 *
 * @date 2020/2/2 上午10:01
 **/
@Service
public class RecommendService {
    @Autowired
    private ProductService productService;

    public List<Product> recommend(int size) {
        return productService.findAll(size);
    }
}
