package site.binghai.shop.service;

import org.springframework.stereotype.Service;
import site.binghai.lib.service.BaseService;
import site.binghai.shop.entity.Banner;
import site.binghai.shop.enums.BannerType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @date 2020/1/31 下午9:54
 **/
@Service
public class BannerService extends BaseService<Banner> {

    public List<Banner> findByType(BannerType type) {
        Banner exp = new Banner();
        exp.setType(type);
        return query(exp);
    }

    public Map<BannerType, List<Banner>> listAll() {
        return findAll(999).stream()
            .collect(Collectors.groupingBy(p -> p.getType()));
    }
}
