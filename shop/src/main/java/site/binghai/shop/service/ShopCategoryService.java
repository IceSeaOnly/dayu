package site.binghai.shop.service;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import site.binghai.lib.service.BaseService;
import site.binghai.lib.utils.CompareUtils;
import site.binghai.lib.utils.SchoolIdThreadLocal;
import site.binghai.shop.entity.School;
import site.binghai.shop.entity.ShopCategory;

import javax.transaction.Transactional;
import java.util.*;
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

    public List<ShopCategory> listAllSuper() {
        ShopCategory exp = new ShopCategory();
        exp.setSuperId(-1L);
        return empty(query(exp));
    }

    public List<ShopCategory> hotList() {
        List<ShopCategory> all = empty(findAll(999));
        return all.stream().filter(p -> !p.getSuperCategory())
                .filter(p -> p.getRecommend() != null && p.getRecommend())
                .collect(Collectors.toList());
    }

    @Transactional
    public void createSystemCategoryFor(School school) {
        ShopCategory systemSuper = new ShopCategory();
        systemSuper.setSuperId(-1L);
        systemSuper.setSuperCategory(true);
        systemSuper.setSchoolId(school.getId());
        systemSuper.setHidden(Boolean.TRUE);
        systemSuper.setTitle("系统分组");
        systemSuper = getDao().save(systemSuper);
        school.setSystemSuperCategoryId(systemSuper.getId());

        ShopCategory pintuan = new ShopCategory();
        pintuan.setSuperId(systemSuper.getId());
        pintuan.setSuperCategory(false);
        pintuan.setSchoolId(school.getId());
        pintuan.setHidden(Boolean.TRUE);
        pintuan.setTitle("拼团商品");
        pintuan = getDao().save(pintuan);

        ShopCategory recycle = new ShopCategory();
        recycle.setSuperId(systemSuper.getId());
        recycle.setSuperCategory(false);
        recycle.setSchoolId(school.getId());
        recycle.setHidden(Boolean.TRUE);
        recycle.setTitle("待分组商品");
        recycle = getDao().save(recycle);

        school.setPintTuanCategoryId(pintuan.getId());
        school.setRecycleCategoryId(recycle.getId());
    }

    @Transactional
    public Map<Long, Long> copyFromSchool(Long from) {
        for (int i = 0; i < 99; i++) {
            List<ShopCategory> ret = pageQuery(new ShopCategory(), i, 100);
            if (isEmptyList(ret)) {
                break;
            }
            ret.forEach(r -> delete(r.getId()));
        }

        Map<Long, Long> idMapping = new HashMap<>();
        ShopCategory example = new ShopCategory();
        example.setCreated(null);
        example.setCreatedTime(null);
        example.setDeleted(null);
        example.setUpdated(null);
        example.setUpdatedTime(null);
        example.setSchoolId(from);
        Example<ShopCategory> ex = Example.of(example);
        for (int i = 0; i < 99; i++) {
            Page<ShopCategory> page = getDao().findAll(ex, new PageRequest(i, 10));
            if (null == page || isEmptyList(page.getContent())) {
                break;
            }
            for (ShopCategory category : page.getContent()) {
                if (CompareUtils.inAny(category.getTitle(), "系统分组", "拼团商品", "待分组商品")) {
                    continue;
                }
                if (category.getBindProductId() != null) {
                    continue;
                }
                ShopCategory tmp = new ShopCategory();
                BeanUtils.copyProperties(category, tmp);
                tmp.setId(null);
                tmp.setSchoolId(SchoolIdThreadLocal.getSchoolId());
                tmp = save(tmp);
                idMapping.put(category.getId(), tmp.getId());
            }
        }

        for (ShopCategory category : findAll(999)) {
            if (category.getSuperId() > 0) {
                category.setSuperId(idMapping.get(category.getSuperId()));
                update(category);
            }
        }
        return idMapping;
    }
}
