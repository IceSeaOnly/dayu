package site.binghai.biz.service;

import org.springframework.stereotype.Service;
import site.binghai.biz.entity.ExcelConvertEntity;
import site.binghai.lib.service.BaseService;


@Service
public class ExcelConvertEntityService extends BaseService<ExcelConvertEntity> {

    public ExcelConvertEntity findByToken(String token) {
        ExcelConvertEntity exp = new ExcelConvertEntity();
        exp.setToken(token);
        return queryOne(exp);
    }
}
