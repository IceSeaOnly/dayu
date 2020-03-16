package site.binghai.shop.controller.app;

import org.springframework.beans.factory.annotation.Autowired;
import site.binghai.lib.controller.BaseController;
import site.binghai.shop.entity.AppToken;
import site.binghai.shop.service.AppTokenService;

import java.util.function.Function;

/**
 * @author icesea
 * @date 2020/3/10 下午10:26
 **/
public abstract class AppBaseController extends BaseController {
    @Autowired
    protected AppTokenService appTokenService;

    protected Object verifyDoing(String token, Function<AppToken, Object> supplier) {
        token = token.startsWith("$") ? token.substring(1) : token;
        AppToken appToken = appTokenService.findByToken(token);
        if (appTokenService.verifyToken(appToken)) {
            System.out.println(token + " successfully verified");
            return supplier.apply(appToken);
        }
        System.out.println(token + " verify failed");
        return fail("auth fail");
    }
}
