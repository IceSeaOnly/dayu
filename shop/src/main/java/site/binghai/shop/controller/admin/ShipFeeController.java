package site.binghai.shop.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.binghai.lib.controller.BaseController;
import site.binghai.shop.entity.ShipFeeRule;
import site.binghai.shop.service.ShipFeeRuleService;

/**
 * @author icesea
 * @date 2020/3/15 下午11:12
 **/
@RequestMapping("manage")
@Controller
public class ShipFeeController extends BaseController {
    @Autowired
    private ShipFeeRuleService shipFeeRuleService;

    @GetMapping("shipFeeRules")
    public String shipFeeRules(ModelMap map) {
        map.put("rules", shipFeeRuleService.findAll());
        return "manage/shipFeeRules";
    }

    @GetMapping("deleteShipFeeRule")
    public String deleteShipFeeRule(@RequestParam Long id) {
        shipFeeRuleService.delete(id);
        return "redirect:shipFeeRules";
    }

    @PostMapping("addNewShipFeeRule")
    public String addNewShipFeeRule(@RequestParam Integer much, @RequestParam Integer fee) {
        ShipFeeRule rule = new ShipFeeRule();
        rule.setMuch(much);
        rule.setFee(fee);
        shipFeeRuleService.save(rule);
        return "redirect:shipFeeRules";
    }
}
