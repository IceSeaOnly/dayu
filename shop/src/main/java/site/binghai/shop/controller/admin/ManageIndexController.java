package site.binghai.shop.controller.admin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.UnifiedOrderService;
import site.binghai.lib.service.WxUserService;
import site.binghai.lib.utils.TimeTools;
import site.binghai.shop.service.SchoolService;

/**
 * @author icesea
 * @date 2020/2/28 上午12:02
 **/
@Controller
@RequestMapping("manage")
public class ManageIndexController extends BaseController {
    @Autowired
    private WxUserService wxUserService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;
    @Autowired
    private SchoolService schoolService;

    @GetMapping("index")
    public String index(ModelMap map) {
        map.put("newMemberSize", wxUserService.countToday());
        map.put("totalMemberSize", wxUserService.count());
        Long paidOrder = unifiedOrderService
            .countByDate(PayBizEnum.SCHOOL_SHOP, TimeTools.today(), OrderStatusEnum.PAIED, OrderStatusEnum.PROCESSING,
                OrderStatusEnum.COMPLETE, OrderStatusEnum.FEED_DONE);
        map.put("todayPaidOrder", paidOrder);
        map.put("todayTotalOrder", unifiedOrderService.countToday(PayBizEnum.SCHOOL_SHOP));
        Integer totalPaid = unifiedOrderService
            .sumShouldPay(PayBizEnum.SCHOOL_SHOP, TimeTools.today(), OrderStatusEnum.PAIED, OrderStatusEnum.PROCESSING,
                OrderStatusEnum.COMPLETE, OrderStatusEnum.FEED_DONE);
        map.put("todayPaidAmount", totalPaid);
        map.put("todayAvgAmount", totalPaid > 0 ? totalPaid / paidOrder : 0);
        map.put("weekDealData", generateWeekDealData());
        map.put("weekOrderData", generateWeekOrderData());
        map.put("school", schoolService.findById(getManager().getSchoolId()));
        return "manage/index";
    }

    private JSONArray generateWeekOrderData() {
        JSONArray ret = new JSONArray();
        long begin = TimeTools.today()[0] - 6 * 86400000L;
        for (int i = 0; i < 7; i++) {
            String date = TimeTools.format2yyyy_MM_dd(begin + i * 86400000L);
            Long orderSize = unifiedOrderService
                .countByDate(PayBizEnum.SCHOOL_SHOP, new Long[] {begin + i * 86400000L, begin + (i + 1) * 86400000L},
                    OrderStatusEnum.values());

            Long paidSize = unifiedOrderService
                .countByDate(PayBizEnum.SCHOOL_SHOP, new Long[] {begin + i * 86400000L, begin + (i + 1) * 86400000L},
                    OrderStatusEnum.PAIED, OrderStatusEnum.PROCESSING,
                    OrderStatusEnum.COMPLETE, OrderStatusEnum.FEED_DONE);
            JSONObject obj = newJSONObject();
            obj.put("date", date);
            obj.put("orderSize", orderSize);
            obj.put("paidSize", paidSize);
            ret.add(obj);
        }
        return ret;
    }

    private JSONArray generateWeekDealData() {
        JSONArray ret = new JSONArray();
        long begin = TimeTools.today()[0] - 6 * 86400000L;
        for (int i = 0; i < 7; i++) {
            String date = TimeTools.format2yyyy_MM_dd(begin + i * 86400000L);
            Integer orderAmt = unifiedOrderService
                .sumShouldPay(PayBizEnum.SCHOOL_SHOP, new Long[] {begin + i * 86400000L, begin + (i + 1) * 86400000L},
                    OrderStatusEnum.values());
            Integer paidAmt = unifiedOrderService
                .sumShouldPay(PayBizEnum.SCHOOL_SHOP, new Long[] {begin + i * 86400000L, begin + (i + 1) * 86400000L},
                    OrderStatusEnum.PAIED,
                    OrderStatusEnum.PROCESSING,
                    OrderStatusEnum.COMPLETE, OrderStatusEnum.FEED_DONE);
            JSONObject obj = newJSONObject();
            obj.put("date", date);
            obj.put("orderAmt", orderAmt / 100.0);
            obj.put("paidAmt", paidAmt / 100.0);
            ret.add(obj);
        }
        return ret;
    }

}
