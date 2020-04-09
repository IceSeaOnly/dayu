package site.binghai.shop.service;

import org.springframework.stereotype.Service;
import site.binghai.lib.service.BaseService;
import site.binghai.shop.entity.School;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author icesea
 * @date 2020/3/8 上午11:08
 **/
@Service
public class SchoolService extends BaseService<School> {

    public List<School> findAll() {
        return getDao().findAll();
    }

    public List<School> findAllExcepte(Long schoolId) {
        return findAll().stream().filter(s -> !s.getId().equals(schoolId)).collect(Collectors.toList());
    }

    @Transactional
    public School saveNew(School school) {
        school = getDao().save(school);
        school.setSchoolId(school.getId());
        return update(school);
    }
}
