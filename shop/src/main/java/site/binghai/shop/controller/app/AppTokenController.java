package site.binghai.shop.controller.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.shop.entity.AppToken;
import site.binghai.shop.service.AppTokenService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author icesea
 * @date 2020/3/14 下午2:13
 **/
@RestController
@RequestMapping("app")
public class AppTokenController extends AppBaseController {
    @Autowired
    private AppTokenService appTokenService;

    @GetMapping("otherAppTokenList")
    public Object otherAppTokenList(@RequestParam String token) {
        return verifyDoing(token, appToken -> {
            List<AppToken> appTokens = appTokenService.findAll();
            appTokens = appTokens.stream()
                .filter(t -> !t.getId().equals(appToken.getId()))
                .peek(t -> t.setToken(null))
                .collect(Collectors.toList());
            return success(appTokens, null);
        });
    }
}
