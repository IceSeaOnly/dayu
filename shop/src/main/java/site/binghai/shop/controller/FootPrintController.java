package site.binghai.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import site.binghai.lib.controller.BaseController;
import site.binghai.shop.entity.FootHistory;
import site.binghai.shop.service.FootHistoryService;
import site.binghai.shop.service.ProductService;

import java.util.List;

/**
 * @author huaishuo
 * @date 2020/3/6 下午12:02
 **/
@RequestMapping("shop")
@Controller
public class FootPrintController extends BaseController {
    @Autowired
    private FootHistoryService footHistoryService;
    @Autowired
    private ProductService productService;

    @GetMapping("footPrint")
    public String footPrint(ModelMap map) {
        List<FootHistory> prints = footHistoryService.findByUser(getUser().getId());
        prints.stream().forEach(f->f.setProduct(productService.findById(f.getProductId())));
        map.put("prints", prints);
        return "footPrint";
    }

    @GetMapping("deleteAllFootPrint")
    public String deleteAll() {
        footHistoryService.deleteAll(getUser().getId());
        return "redirect:footPrint";
    }

    @GetMapping("deleteFootPrint")
    @ResponseBody
    public Object deleteFootPrint(@RequestParam Long footId) {
        FootHistory history = footHistoryService.findById(footId);
        if (history != null && history.getBuyerId().equals(getUser().getId())) {
            footHistoryService.delete(footId);
        }
        return success();
    }
}
