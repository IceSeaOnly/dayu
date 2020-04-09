package site.binghai.shop.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.lib.controller.BaseController;
import site.binghai.shop.entity.ShopOrder;
import site.binghai.shop.pinter.CloudPrinter;
import site.binghai.shop.service.ShopOrderService;

import java.util.List;

/**
 * @author icesea
 * @date 2020/3/15 下午9:41
 **/
@RestController
@RequestMapping("manage")
public class PrintOrderController extends BaseController {
    @Autowired
    private ShopOrderService shopOrderService;
    @Autowired
    private CloudPrinter cloudPrinter;


    @GetMapping("printOrder")
    public Object printOrder(@RequestParam Long batchId) {
        List<ShopOrder> orders = shopOrderService.findByBatchId(batchId);
        try {
            String resp = cloudPrinter.print(orders, String.valueOf(batchId));
        } catch (Exception e) {
            return fail(e.getMessage());
        }
        return success();
    }
}
