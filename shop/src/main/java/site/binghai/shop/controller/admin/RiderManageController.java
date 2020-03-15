package site.binghai.shop.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.utils.TimeTools;
import site.binghai.shop.entity.AppToken;
import site.binghai.shop.entity.ShopOrder;
import site.binghai.shop.service.AppTokenService;
import site.binghai.shop.service.SchoolService;
import site.binghai.shop.service.ShopOrderService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author huaishuo
 * @date 2020/3/15 下午9:57
 **/
@RequestMapping("manage")
@Controller
public class RiderManageController extends BaseController {
    @Autowired
    private SchoolService schoolService;
    @Autowired
    private AppTokenService appTokenService;
    @Autowired
    private ShopOrderService shopOrderService;

    @GetMapping("riders")
    public String riders(ModelMap map) {
        Long totalProcessing = shopOrderService.countByStateAndTime(TimeTools.today()[0], TimeTools.today()[1],
            OrderStatusEnum.PROCESSING);
        Long totalDelivery = 0L;
        Long totalComplete = 0L;

        List<AppToken> tokens = appTokenService.findAllBySchool();
        for (AppToken token : tokens) {
            token.setTodayComplete(
                shopOrderService.countByRiderAndStatusAndTime(OrderStatusEnum.COMPLETE, token.getId(),
                    TimeTools.today()));
            token.setTodayDelivery(
                shopOrderService.countByRiderAndStatusAndTime(OrderStatusEnum.DELIVERY, token.getId(),
                    TimeTools.today()));
            token.setTodayTake(token.getTodayComplete() + token.getTodayDelivery());

            totalDelivery += token.getTodayDelivery();
            totalComplete += token.getTodayComplete();
        }
        map.put("totalProcessing", totalProcessing);
        map.put("totalDelivery", totalDelivery);
        map.put("totalComplete", totalComplete);
        map.put("tokens", tokens);
        map.put("school", schoolService.findById(getManager().getSchoolId()));
        map.put("initPass", String.valueOf(System.currentTimeMillis()).substring(7));
        map.put("initToken", UUID.randomUUID().toString());
        return "manage/riders";
    }

    @PostMapping("addNewRider")
    public String addNewRider(@RequestParam String token, @RequestParam String userName, @RequestParam String passWord,
                              @RequestParam String remark) {

        AppToken appToken = new AppToken();
        appToken.setUserName(userName);
        appToken.setPassWord(passWord);
        appToken.setRemark(remark);
        appToken.setToken(token);
        appToken.setInvalidTs(now());

        appTokenService.save(appToken);
        return "redirect:riders";
    }

    @GetMapping("resetPassWord")
    @ResponseBody
    public Object resetPassWord(@RequestParam Long token) {
        AppToken app = appTokenService.findById(token);
        app.setPassWord(String.valueOf(System.currentTimeMillis()).substring(7));
        app.setToken(UUID.randomUUID().toString());
        appTokenService.update(app);
        return success(app.getPassWord(), null);
    }

    @GetMapping("privilegeControl")
    @ResponseBody
    public Object privilegeControl(@RequestParam Long token) {
        AppToken app = appTokenService.findById(token);
        app.setSchoolReview(!app.getSchoolReview());
        appTokenService.update(app);
        return success();
    }

    @GetMapping("releaseDelivery")
    @ResponseBody
    public Object releaseDelivery(@RequestParam Long token) {
        Map<Long, List<ShopOrder>> orders = shopOrderService.findByStatusAndRider(OrderStatusEnum.DELIVERY, token, 0);
        while (isNotEmpty(orders)) {
            orders.forEach((k, v) -> {
                v.forEach(order -> {
                    order.setStatus(OrderStatusEnum.PROCESSING.getCode());
                    order.setRider(null);
                    shopOrderService.update(order);
                });
            });
        }
        return success();
    }
}
