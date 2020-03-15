package site.binghai.shop.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.lib.controller.BaseController;

/**
 * @author huaishuo
 * @date 2020/3/15 下午9:41
 **/
@RestController
@RequestMapping("manage")
public class PrintOrderController extends BaseController {
    @GetMapping("printOrder")
    public Object printOrder(@RequestParam Long batchId) {
        return fail("打印机不存在!");
    }
}
