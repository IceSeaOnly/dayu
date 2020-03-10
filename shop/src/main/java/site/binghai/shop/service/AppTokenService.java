package site.binghai.shop.service;

import org.springframework.stereotype.Service;
import site.binghai.lib.service.BaseService;
import site.binghai.lib.utils.SchoolIdThreadLocal;
import site.binghai.shop.entity.AppToken;

import javax.transaction.Transactional;

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
        return queryOne(exp);
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
}
