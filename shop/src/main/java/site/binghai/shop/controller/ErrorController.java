package site.binghai.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @date 2020/2/2 下午5:23
 **/
@RequestMapping("/")
@Controller
public class ErrorController {
    @GetMapping("404")
    public String e404() {
        return "404";
    }

    @GetMapping("500")
    public String e500(@RequestParam String error, ModelMap map) {
        map.put("errorMsg",error);
        return "500";
    }
}
