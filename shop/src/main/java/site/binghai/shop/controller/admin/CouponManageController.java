package site.binghai.shop.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.service.CouponPlanService;

@RequestMapping("manage")
@Controller
public class CouponManageController extends BaseController {
    @Autowired
    private CouponPlanService couponPlanService;
    @GetMapping("coupon")
    public String couponManage(ModelMap map){
        map.put("plans",couponPlanService.findAll());
        return "manage/couponManage";
    }
}
