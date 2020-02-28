package site.binghai.shop.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.binghai.lib.controller.BaseController;

/**
 * @author huaishuo
 * @date 2020/2/29 上午1:13
 **/
@RequestMapping("manage")
@Controller
public class OrderManageController extends BaseController {

    @GetMapping("orderManage")
    public String orderManage() {
        return "manage/orderManage";
    }
}
