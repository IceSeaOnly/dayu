package site.binghai.shop.controller.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huaishuo
 * @date 2020/3/14 下午8:27
 **/
@RestController
@RequestMapping("app")
public class MyInfoController extends AppBaseController {

    @GetMapping("myInfo")
    public Object myInfo(@RequestParam String token) {
        return verifyDoing(token, appToken -> {
            appToken.setPassWord(null);
            appToken.setToken(null);
            return success(appToken, null);
        });
    }

    @GetMapping("updateMyInfo")
    public Object updateMyInfo(@RequestParam String token, @RequestParam String userName, String passWord) {
        return verifyDoing(token, appToken -> {
            if (passWord != null) {
                appToken.setPassWord(passWord);
            }
            if (userName != null) {
                appToken.setUserName(userName);
            }
            appTokenService.update(appToken);
            return success();
        });
    }
}
