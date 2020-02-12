package site.binghai.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.binghai.lib.controller.BaseController;

/**
 *
 * @date 2020/2/12 下午3:49
 **/
@RequestMapping("shop")
@Controller
public class PinTuanController extends BaseController {

    @GetMapping("ptIndex")
    public String ptIndex() {
        return "ptIndex";
    }

    @GetMapping("ptDetail")
    public String ptDetail() {
        return "ptDetail";
    }
}
