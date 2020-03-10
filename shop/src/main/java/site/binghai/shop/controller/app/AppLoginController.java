package site.binghai.shop.controller.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.shop.entity.AppToken;
import site.binghai.shop.kv.AppConfig;
import site.binghai.shop.service.KvService;

import java.util.Map;
import java.util.UUID;

/**
 * @author huaishuo
 * @date 2020/3/10 下午7:59
 **/
@RestController
@RequestMapping("app")
public class AppLoginController extends AppBaseController {

    @Autowired
    private KvService kvService;

    @PostMapping("login")
    public Object login(@RequestBody Map map) {
        String userName = getString(map, "userName");
        String passWord = getString(map, "passWord");
        AppToken token = appTokenService.findByUserNameAndPass(userName, passWord);
        if (token == null) {
            System.out.println(userName + " login failed");
            return fail("FAIL");
        }
        token.setToken(UUID.randomUUID().toString());
        token.setInvalidTs(now() + 86400000 * 14);
        appTokenService.update(token);
        System.out.println(userName + " login succeed");
        return success(token.getToken(), null);
    }

    @GetMapping("autoLogin")
    public Object autoLogin(@RequestParam String token) {
        token = token.startsWith("$") ? token.substring(1) : token;
        if (appTokenService.verify(token)) {
            System.out.println(token + " auto login succeed");
            return success();
        } else {
            System.out.println(token + " auto login failed");
            return fail("FAIL");
        }
    }

    @GetMapping("indexWebView")
    public Object indexWebView(@RequestParam String token) {
        return verifyDoing(token, appToken -> success(kvService.get(AppConfig.class), null));
    }

}
