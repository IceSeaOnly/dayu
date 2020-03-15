package site.binghai.shop.service;

import org.springframework.stereotype.Service;
import site.binghai.lib.service.BaseService;
import site.binghai.lib.utils.SchoolIdThreadLocal;
import site.binghai.shop.entity.AppToken;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author huaishuo
 * @date 2020/3/10 下午8:04
 **/
@Service
public class AppTokenService extends BaseService<AppToken> {

    public AppToken findByUserNameAndPass(String u, String pass) {
        AppToken exp = new AppToken();
        exp.setUserName(u);
        exp.setPassWord(pass);
        AppToken token = queryOne(exp);
        if (token != null) {
            SchoolIdThreadLocal.setSchoolId(token.getSchoolId());
        }
        return token;
    }

    public AppToken findByToken(String token) {
        AppToken exp = new AppToken();
        exp.setToken(token);
        return queryOne(exp);
    }

    @Transactional
    public boolean verify(String token) {
        AppToken t = findByToken(token);
        boolean ret = verifyToken(t);
        if (ret && t.getInvalidTs() - now() < 86400000 * 2) {
            t.setInvalidTs(now() + 86400000 * 14);
            update(t);
        }
        return ret;
    }

    public boolean verifyToken(AppToken t) {
        if (t == null || t.getInvalidTs() < now()) {
            return false;
        }

        SchoolIdThreadLocal.setSchoolId(t.getSchoolId());
        return true;
    }

    public List<AppToken> findAll() {
        return getDao().findAll();
    }

    public List<AppToken> findAllBySchool() {
        AppToken exp = new AppToken();
        return query(exp);
    }
}
