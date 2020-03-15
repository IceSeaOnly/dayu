package site.binghai.shop.controller.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.UnifiedOrderService;
import site.binghai.lib.service.WxUserService;
import site.binghai.lib.utils.TimeTools;
import site.binghai.shop.service.SchoolService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huaishuo
 * @date 2020/3/14 下午11:20
 **/
@RestController
@RequestMapping("app")
public class AppAboutSchoolController extends AppBaseController {
    @Autowired
    private SchoolService schoolService;
    @Autowired
    private WxUserService wxUserService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;

    @GetMapping("aboutSchool")
    public Object aboutSchool(@RequestParam String token) {
        return verifyDoing(token, appToken -> {
            if (appToken.getSchoolReview() == null || !appToken.getSchoolReview()) {
                return fail("没有权限!");
            }

            Map<String, Object> map = new HashMap();

            map.put("newMemberSize", wxUserService.countToday());
            map.put("totalMemberSize", wxUserService.count());
            Long paidOrder = unifiedOrderService
                .countByDate(PayBizEnum.SCHOOL_SHOP, TimeTools.today(), OrderStatusEnum.PAIED,
                    OrderStatusEnum.PROCESSING,
                    OrderStatusEnum.COMPLETE, OrderStatusEnum.FEED_DONE);
            map.put("todayPaidOrder", paidOrder);
            map.put("todayTotalOrder", unifiedOrderService.countToday(PayBizEnum.SCHOOL_SHOP));
            Integer totalPaid = unifiedOrderService
                .sumShouldPay(PayBizEnum.SCHOOL_SHOP, TimeTools.today(), OrderStatusEnum.PAIED,
                    OrderStatusEnum.PROCESSING,
                    OrderStatusEnum.COMPLETE, OrderStatusEnum.FEED_DONE);
            map.put("todayPaidAmount", totalPaid);
            map.put("todayAvgAmount", totalPaid > 0 ? totalPaid / paidOrder : 0);
            map.put("school", schoolService.findById(appToken.getSchoolId()));

            return success(map, null);
        });
    }
}
