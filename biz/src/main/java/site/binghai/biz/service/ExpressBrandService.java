package site.binghai.biz.service;

import org.springframework.stereotype.Service;
import site.binghai.biz.entity.windWheel.ExpressBrand;
import site.binghai.lib.service.BaseService;

import java.util.List;

/**
 * @author huaishuo
 * @date 2018/12/3 下午11:05
 **/
@Service
public class ExpressBrandService extends BaseService<ExpressBrand> {
    public List findByType(Integer type) {
        ExpressBrand exp = new ExpressBrand();
        if (type == 0) {
            exp.setEnableSend(true);
        } else {
            exp.setEnableTake(true);
        }
        return query(exp);
    }
}
