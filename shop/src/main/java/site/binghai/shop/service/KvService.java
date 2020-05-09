package site.binghai.shop.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import site.binghai.lib.service.BaseService;
import site.binghai.shop.def.KvSupport;
import site.binghai.shop.entity.KeyValueEntity;

import javax.transaction.Transactional;

/**
 * @date 2020/2/10 上午9:52
 **/
@Service
public class KvService extends BaseService<KeyValueEntity> {

    public <T extends KvSupport> T get(Class<T> tClass) {
        KeyValueEntity p = findByKey(tClass.getSimpleName());
        return p == null ? null : JSONObject.parseObject(p.getSvalue(), tClass);
    }

    public <T extends KvSupport> T get(Class<T> tClass, Long schoolId) {
        KeyValueEntity exp = new KeyValueEntity();
        exp.setClassName(tClass.getSimpleName());
        exp.setSchoolId(schoolId);
        KeyValueEntity p = queryOne(exp);
        return p == null ? null : JSONObject.parseObject(p.getSvalue(), tClass);
    }

    @Transactional
    public <T extends KvSupport> void save(T entity) {
        KeyValueEntity p = findByKey(entity.getClass().getSimpleName());
        if (p == null) {
            p = new KeyValueEntity();
            p.setClassName(entity.getClass().getSimpleName());
            p.setSvalue(JSONObject.toJSONString(entity));
            save(p);
        } else {
            p.setSvalue(JSONObject.toJSONString(entity));
            update(p);
        }
    }

    @Transactional
    public <T extends KvSupport> void remove(Class<T> tClass) {
        KeyValueEntity p = findByKey(tClass.getSimpleName());
        if (p == null) {
            return;
        }
        delete(p.getId());
    }

    public KeyValueEntity findByKey(String className) {
        KeyValueEntity exp = new KeyValueEntity();
        exp.setClassName(className);
        return queryOne(exp);
    }
}
