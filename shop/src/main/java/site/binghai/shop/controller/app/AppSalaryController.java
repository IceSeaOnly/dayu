package site.binghai.shop.controller.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.shop.entity.SalaryLog;
import site.binghai.shop.enums.SalaryScene;
import site.binghai.shop.enums.SalaryState;
import site.binghai.shop.service.SalaryLogService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author icesea
 * @date 2020/3/14 下午9:35
 **/
@RestController
@RequestMapping("app")
public class AppSalaryController extends AppBaseController {

    @Autowired
    private SalaryLogService salaryLogService;

    @GetMapping("salaryLog")
    public Object salaryLog(@RequestParam String token, @RequestParam Integer page) {
        return verifyDoing(token, appToken -> {
            List<SalaryLog> logs = salaryLogService.sortPageQuery(appToken.getId(), page);
            return success(logs, null);
        });
    }

    @GetMapping("salary")
    public Object salary(@RequestParam String token) {
        return verifyDoing(token, appToken -> {
            Map<SalaryScene, Map<SalaryState, Integer>> map = new HashMap<>();
            for (SalaryScene scene : SalaryScene.values()) {
                map.put(scene, new HashMap<>());
                for (SalaryState state : SalaryState.values()) {
                    Integer total = salaryLogService.sumByStateAndScene(appToken.getId(), scene, state);
                    map.get(scene).put(state, total);
                }
            }
            return success(map, null);
        });
    }
}
