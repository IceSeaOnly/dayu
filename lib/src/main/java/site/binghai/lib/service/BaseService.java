package site.binghai.lib.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import site.binghai.lib.entity.BaseEntity;
import site.binghai.lib.utils.BaseBean;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class BaseService<T extends BaseEntity> extends BaseBean {
    @Autowired
    private EntityManager entityManager;
    private SimpleJpaRepository<T, Long> daoHolder;

    public T newInstance(Map map) {
        JSONObject obj = JSONObject.parseObject(JSONObject.toJSONString(map));
        return obj.toJavaObject(getTypeArguement());
    }

    @Transactional
    public T newAndSave(Map map) {
        return save(newInstance(map));
    }

    protected JpaRepository<T, Long> getDao() {
        if (daoHolder != null) {
            return daoHolder;
        }
        daoHolder = new SimpleJpaRepository(getTypeArguement(), entityManager);
        return daoHolder;
    }

    /**
     * 获取某列的所有case，适用于可以枚举的列
     */
    public List distinctList(String filed) {
        List ls = entityManager.createQuery(
            String.format("select distinct %s from %s", filed, getTypeArguement().getSimpleName()))
            .getResultList();

        return filterDeleted(ls);
    }

    /**
     * 获取T的实际类型
     */
    public Class<T> getTypeArguement() {
        Class<T> tClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return tClass;
    }

    @Transactional
    public T save(T t) {
        return getDao().save(t);
    }

    @Transactional
    public T update(T t) {
        if (t.getId() >= 0) {
            t.setUpdated(now());
            return save(t);
        }
        return t;
    }


    public T findById(Long id) {
        if (id == null) { return null; }
        T t = getDao().findById(id).orElse(null);
        if (t == null || (t.getDeleted() != null && t.getDeleted())) {
            return null;
        }
        return t;
    }

    @Transactional
    public void delete(Long id) {
        getDao().deleteById(id);
        //T t = findById(id);
        //if (t != null) {
        //    t.setDeleted(Boolean.TRUE);
        //    update(t);
        //}
    }

    @Transactional
    public T deleteIfExist(Long id) {
        T t = findById(id);
        if (t == null) { return null; }
        delete(id);
        return t;
    }

    //@Transactional 启用逻辑删除，禁用此方法
    //public boolean deleteAll(String confirm) {
    //    if (confirm.equals("confirm")) {
    //        getDao().deleteAll();
    //        return true;
    //    }
    //    return false;
    //}

    public List<T> findByIds(List<Long> ids) {
        return filterDeleted(getDao().findAllById(ids));
    }

    public List<T> filterDeleted(List<T> inputs) {
        if (isEmptyList(inputs)) { return inputs; }
        return inputs.stream()
            .filter(v -> v.getDeleted() == null || !v.getDeleted())
            .collect(Collectors.toList());
    }

    public List<T> pageQuery(T example, int page, int size) {
        example.setCreated(null);
        example.setCreatedTime(null);
        example.setDeleted(null);
        example.setUpdated(null);
        example.setUpdatedTime(null);
        Example<T> ex = Example.of(example);
        return filterDeleted(getDao().findAll(ex, new PageRequest(page, size)).getContent());
    }

    public List<T> query(T example) {
        example.setCreated(null);
        example.setCreatedTime(null);
        example.setDeleted(null);
        example.setUpdated(null);
        example.setUpdatedTime(null);
        Example<T> ex = Example.of(example);
        return filterDeleted(getDao().findAll(ex));
    }

    public T queryOne(T example) {
        example.setCreated(null);
        example.setCreatedTime(null);
        example.setDeleted(false);
        example.setUpdated(null);
        example.setUpdatedTime(null);
        Example<T> ex = Example.of(example);
        Optional<T> rs = getDao().findOne(ex);
        T t = (rs == null ? null : rs.orElse(null));
        if (t == null) { return null; }
        return t.getDeleted() ? null : t;
    }

    public void delete(T example) {
        example.setCreated(null);
        example.setCreatedTime(null);
        example.setDeleted(null);
        example.setUpdated(null);
        example.setUpdatedTime(null);
        example = queryOne(example);
        if (example != null) {
            delete(example.getId());
        }
    }

    public List<T> sortQuery(T example, String sortField, Boolean desc) {
        example.setCreated(null);
        example.setCreatedTime(null);
        example.setDeleted(null);
        example.setUpdated(null);
        example.setUpdatedTime(null);
        Example<T> ex = Example.of(example);
        return filterDeleted(getDao().findAll(ex, Sort.by(desc ? Sort.Direction.DESC : Sort.Direction.ASC, sortField)));
    }

    public List<T> findAll(int limit) {
        return filterDeleted(getDao().findAll(new PageRequest(0, limit)).getContent());
    }

    public List<T> findAll(int page, int pageSize) {
        return filterDeleted(getDao().findAll(new PageRequest(page, pageSize)).getContent());
    }

    public long count() {
        try {
            T exp = getTypeArguement().newInstance();
            exp.setDeleted(Boolean.FALSE);
            exp.setCreated(null);
            exp.setCreatedTime(null);
            exp.setUpdated(null);
            exp.setUpdatedTime(null);
            Example<T> ex = Example.of(exp);
            return getDao().count(ex);
        } catch (Exception e) {
            logger.error("count error! ", e);
        }
        return 0;
    }

    public long count(T exp) {
        try {
            exp.setDeleted(Boolean.FALSE);
            exp.setCreated(null);
            exp.setCreatedTime(null);
            exp.setUpdated(null);
            exp.setUpdatedTime(null);
            Example<T> ex = Example.of(exp);
            return getDao().count(ex);
        } catch (Exception e) {
            logger.error("count error! ", e);
        }
        return 0;
    }

    @Transactional
    public List<T> batchSave(List<T> batch) {
        return getDao().saveAll(batch);
    }

    /**
     * 使用map更新entity中除id外的其他字段
     */
    private T updateParams(T t, Map map) {
        Long id = t.getId();
        JSONObject item = JSONObject.parseObject(JSONObject.toJSONString(t));
        item.putAll(map);
        item.put("id", id);
        return item.toJavaObject(getTypeArguement());
    }

    @Transactional
    public T updateAndSave(BaseEntity admin, Map map) throws Exception {
        Long id = getLong(map, "id");
        if (id == null) {
            throw new Exception("id must be present!");
        }
        T old = findById(id);
        if (old == null) {
            throw new Exception("item not exist!");
        }
        T new_ = updateParams(old, map);
        logger.warn("{} update {} from {} to {}", admin, getTypeArguement().getSimpleName(), old, new_);
        return update(new_);
    }
}


