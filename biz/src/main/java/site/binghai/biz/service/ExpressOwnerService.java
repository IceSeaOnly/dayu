package site.binghai.biz.service;

import org.springframework.stereotype.Service;
import site.binghai.biz.entity.windWheel.ExpressBrand;
import site.binghai.biz.entity.windWheel.ExpressOwner;
import site.binghai.lib.service.BaseService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huaishuo
 * @date 2018/12/9 下午12:30
 **/
@Service
public class ExpressOwnerService extends BaseService<ExpressOwner> {

    public List<ExpressBrand> listByOwnerId(Long userId, ExpressBrandService expressBrandService) {
        ExpressOwner exp = new ExpressOwner();
        exp.setWxUserId(userId);
        List<Long> brandIds = query(exp)
            .stream()
            .map(v -> v.getBrandId())
            .collect(Collectors.toList());

        return expressBrandService.findByIds(brandIds);
    }

    public ExpressOwner findByUserIdAndBrandId(Long userId, Long eid) {
        ExpressOwner exp = new ExpressOwner();
        exp.setWxUserId(userId);
        exp.setBrandId(eid);
        return queryOne(exp);
    }
}
