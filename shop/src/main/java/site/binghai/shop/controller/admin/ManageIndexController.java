package site.binghai.shop.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.binghai.lib.controller.BaseController;

/**
 * @author huaishuo
 * @date 2020/2/28 上午12:02
 **/
@Controller
@RequestMapping("manage")
public class ManageIndexController extends BaseController {
    @GetMapping("index")
    public String index(ModelMap map) {
        return "manage/index";
    }

}
