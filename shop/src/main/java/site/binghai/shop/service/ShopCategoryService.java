package site.binghai.shop.service;

import org.springframework.stereotype.Service;
import site.binghai.lib.service.BaseService;
import site.binghai.shop.entity.ShopCategory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @date 2020/2/1 上午9:06
 **/
@Service
public class ShopCategoryService extends BaseService<ShopCategory> {

    public Map<ShopCategory, List<ShopCategory>> list() {
        Map<ShopCategory, List<ShopCategory>> ret = new HashMap<>();

        List<ShopCategory> all = empty(findAll(999))
            .stream().filter(c -> c.getHidden() == null || !c.getHidden())
            .collect(Collectors.toList());

        Map<Long, List<ShopCategory>> maps = all.stream().filter(c -> !c.getSuperCategory())
            .collect(Collectors.groupingBy(c -> c.getSuperId()));

        all.stream().filter(c -> c.getSuperCategory())
            .forEach(s -> ret.put(s, maps.get(s.getId())));
        return ret;
    }

    private List<ShopCategory> findAll(int size) {
        return pageQuery(new ShopCategory(), 0, size);
    }

    public Map<ShopCategory, List<ShopCategory>> listAll() {
        Map<ShopCategory, List<ShopCategory>> ret = new LinkedHashMap<>();
        List<ShopCategory> all = empty(findAll(999));
        Map<Long, List<ShopCategory>> maps = all.stream().filter(c -> !c.getSuperCategory())
            .collect(Collectors.groupingBy(c -> c.getSuperId()));

        all.stream().filter(c -> c.getSuperCategory())
            .sorted((a, b) -> a.getSuperId() > b.getSuperId() ? -1 : 1)
            .forEach(s -> ret.put(s, maps.get(s.getId())));
        return ret;
    }

    public List<ShopCategory> hotList() {
        List<ShopCategory> all = empty(findAll(999));
        return all.stream().filter(p -> !p.getSuperCategory())
            .filter(p -> p.getRecommend() != null && p.getRecommend())
            .collect(Collectors.toList());
    }
}
