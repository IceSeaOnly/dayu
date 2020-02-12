package site.binghai.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.binghai.lib.controller.BaseController;

/**
 *
 * @date 2020/2/4 下午11:23
 **/
@RequestMapping("shop")
@Controller
public class CustomerServiceController extends BaseController {
    @GetMapping("customerService")
    public String customerService(){
        return "customerService";
    }
}
