package site.binghai.lib.service;

import org.springframework.stereotype.Service;
import site.binghai.lib.entity.Manager;

import javax.transaction.Transactional;

/**
 * @author huaishuo
 * @date 2020/2/27 下午11:54
 **/
@Service
public class ManagerService extends BaseService<Manager> {
    public Manager findByUserNameAndPass(String userName, String passWord) {
        Manager manager = new Manager();
        manager.setUserName(userName);
        manager.setPassWord(passWord);
        manager.setForbidden(Boolean.FALSE);
        return queryOne(manager);
    }

    @Transactional
    public void updateSchool(Manager manager) {
        getDao().save(manager);
    }
}
