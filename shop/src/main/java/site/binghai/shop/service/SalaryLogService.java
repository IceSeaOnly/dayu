package site.binghai.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import site.binghai.lib.service.BaseService;
import site.binghai.shop.dao.SalaryLogDao;
import site.binghai.shop.entity.SalaryLog;
import site.binghai.shop.enums.SalaryScene;
import site.binghai.shop.enums.SalaryState;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author huaishuo
 * @date 2020/3/14 下午9:24
 **/
@Service
public class SalaryLogService extends BaseService<SalaryLog> {

    @Autowired
    private SalaryLogDao salaryLogDao;

    @Transactional
    public void book(Long userId, Long relatedId, SalaryScene scene, Integer salary) {
        SalaryLog log = new SalaryLog();
        log.setUserId(userId);
        log.setSalary(salary);
        log.setScene(scene);
        log.setState(SalaryState.INIT);
        log.setRelateId(relatedId);
        save(log);
    }

    public Integer sumByStateAndScene(Long userId, SalaryScene scene, SalaryState state) {
        Integer sum = salaryLogDao.sumByUserIdAndSceneAndState(userId, state.ordinal(), scene.ordinal());
        return sum == null ? 0 : sum;
    }

    public List<SalaryLog> sortPageQuery(Long appId, Integer page) {
        return salaryLogDao.findAllByUserIdOrderByIdDesc(appId, new PageRequest(page, 100));
    }
}
