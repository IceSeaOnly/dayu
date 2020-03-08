package site.binghai.shop.service;

import org.springframework.stereotype.Service;
import site.binghai.lib.service.BaseService;
import site.binghai.shop.entity.School;

import java.util.List;

/**
 * @author huaishuo
 * @date 2020/3/8 上午11:08
 **/
@Service
public class SchoolService extends BaseService<School> {

    public List<School> findAll() {
        return getDao().findAll();
    }
}
