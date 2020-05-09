package site.binghai.lib.service;

import org.springframework.stereotype.Service;
import site.binghai.lib.entity.Manager;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author icesea
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

    public List<Manager> findAll() {
        return getDao().findAll();
    }

    public List<Manager> findByUserName(String userName) {
        return getDao().findAll().stream().filter(p -> p.getUserName().equals(userName)).collect(Collectors.toList());
    }

}
