package site.binghai.shop.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.service.PayBizServiceFactory;

/**
 * @author huaishuo
 * @date 2020/2/23 下午3:16
 **/
@RequestMapping("/manage/door")
@RestController
public class BackDoorController extends BaseController {
    @Autowired
    private PayBizServiceFactory payBizServiceFactory;

    @GetMapping("pay")
    public Object pay(@RequestParam String orderId) {
        try {
            payBizServiceFactory.onPayNotify(orderId);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "ok";
    }

}
