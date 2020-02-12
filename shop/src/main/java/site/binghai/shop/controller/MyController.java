package site.binghai.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.service.WxUserService;
import site.binghai.shop.service.FootHistoryService;
import site.binghai.shop.service.RecommendService;
import site.binghai.shop.service.ShopCollectionService;
import site.binghai.shop.service.ShopOrderService;

/**
 *
 * @date 2020/2/2 下午8:37
 **/
@RequestMapping("shop")
@Controller
public class MyController extends BaseController {
    @Autowired
    private ShopCollectionService shopCollectionService;
    @Autowired
    private FootHistoryService footHistoryService;
    @Autowired
    private ShopOrderService shopOrderService;
    @Autowired
    private RecommendService recommendService;
    @Autowired
    private WxUserService wxUserService;

    @GetMapping("my")
    public String my(ModelMap map) {
        map.put("user", getUser());
        map.put("collectCnt", shopCollectionService.countByUserId(getUser()));
        map.put("footPrints", footHistoryService.countByUserId(getUser()));
        map.put("orderCnt", shopOrderService.countByUserId(getUser().getId()));
        map.put("created", shopOrderService.countByUserIdAndState(getUser().getId(), OrderStatusEnum.CREATED));
        map.put("shipping", shopOrderService.countByUserIdAndState(getUser().getId(), OrderStatusEnum.PAIED));
        map.put("shipped", shopOrderService.countByUserIdAndState(getUser().getId(), OrderStatusEnum.PROCESSING));
        map.put("feeding", shopOrderService.countByUserIdAndState(getUser().getId(), OrderStatusEnum.COMPLETE));
        map.put("recommends", recommendService.recommend(100));
        return "my";
    }

    @GetMapping("myInfo")
    public String myInfo(ModelMap map) {
        WxUser user = getUser();
        map.put("user", user);
        map.put("needCompleteInfo", needCompleteInfo(user));
        return "myInfo";
    }

    @PostMapping("updateMyInfo")
    public String updateMyInfo(@RequestParam String gender,
                               @RequestParam String country,
                               @RequestParam String province,
                               @RequestParam String city,
                               @RequestParam String phone) {

        WxUser user = getUser();
        user.setCity(city);
        user.setCountry(country);
        user.setProvince(province);
        user.setPhone(phone);
        user.setGender(gender);
        wxUserService.update(user);
        persistent(user);
        return "redirect:my";
    }

    private boolean needCompleteInfo(WxUser wxUser) {
        if (hasEmptyString(wxUser.getUserName(), wxUser.getPhone())) {
            return true;
        }
        return false;
    }
}
