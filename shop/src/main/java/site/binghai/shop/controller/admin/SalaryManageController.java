package site.binghai.shop.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.utils.TimeTools;
import site.binghai.shop.entity.AppToken;
import site.binghai.shop.entity.SalaryLog;
import site.binghai.shop.enums.SalaryScene;
import site.binghai.shop.enums.SalaryState;
import site.binghai.shop.service.AppTokenService;
import site.binghai.shop.service.SalaryLogService;

import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("manage")
@Controller
public class SalaryManageController extends BaseController {

    @Autowired
    private AppTokenService appTokenService;
    @Autowired
    private SalaryLogService salaryLogService;

    @GetMapping("salaryManage")
    public String salaryManage(ModelMap map, String selected, String start, String end) {
        List<AppToken> riders = null;
        Map<Long, Map<String, Integer>> logs = new HashMap<>();
        if (hasEmptyString(selected)) {
            riders = appTokenService.findAllBySchool();
        } else {
            List<Long> ids = Arrays.stream(selected.split(",")).map(s -> Long.valueOf(s)).collect(Collectors.toList());
            riders = appTokenService.findByIds(ids);
        }
        Long s = hasEmptyString(start) ? TimeTools.getTimesWeekmorning() : TimeTools.dataTime2Timestamp(start, "MM/dd/yyyy");
        Long e = hasEmptyString(end) ? now() : TimeTools.dataTime2Timestamp(end, "MM/dd/yyyy");
        enrichLogs(riders, logs, s, e);
        map.put("currentSelected", selected);
        map.put("riders", appTokenService.findAllBySchool());
        map.put("selectedRiders", riders);
        Map<String, String> scenes = new HashMap<>();
        for (SalaryScene scene : SalaryScene.values()) {
            scenes.put(scene.name(), scene.getDesc());
        }
        Map<String, String> status = new HashMap<>();
        for (SalaryState state : SalaryState.values()) {
            status.put(state.name(), state.getDesc());
        }
        map.put("salaryScenes", scenes);
        map.put("salaryStates", status);
        map.put("logs", logs);
        map.put("start", TimeTools.format(s, "MM/dd/yyyy"));
        map.put("end", TimeTools.format(e, "MM/dd/yyyy"));
        return "manage/salaryManage";
    }

    @GetMapping("settle")
    @ResponseBody
    public Object settle(@RequestParam Long rider, @RequestParam String start, @RequestParam String end) {
        int total = 0;
        Long s = TimeTools.dataTime2Timestamp(start, "MM/dd/yyyy");
        Long e = TimeTools.dataTime2Timestamp(end, "MM/dd/yyyy") + 86400000L;
        for (SalaryScene scene : SalaryScene.values()) {
            List<SalaryLog> ret = salaryLogService.findByStateAndSceneAndDate(rider, scene, SalaryState.INIT, s, e);
            for (SalaryLog log : ret) {
                total += log.getSalary();
                log.setState(SalaryState.DONE);
                log.setSummary(getManager().getNickName() + "操作结算;" + (log.getSummary() == null ? "" : log.getSummary()));
                log.setSettlementDate(TimeTools.now());
                salaryLogService.update(log);
            }
        }
        return success(total, null);
    }

    @GetMapping("ajaxBook")
    @ResponseBody
    public Object ajaxBook(@RequestParam Long rider, @RequestParam String status, @RequestParam String scene, @RequestParam String remark, @RequestParam Integer salary) {
        SalaryLog log = new SalaryLog();
        log.setUserId(rider);
        log.setState(SalaryState.valueOf(status));
        log.setScene(SalaryScene.valueOf(scene));
        log.setSummary(getManager().getNickName() + "调账:" + remark);
        log.setSalary(salary);
        salaryLogService.save(log);
        return success();
    }

    @GetMapping("salaryDetail")
    public String salaryDetail(@RequestParam Long rider, @RequestParam String start, @RequestParam String end, ModelMap map) {
        Long s = TimeTools.dataTime2Timestamp(start, "MM/dd/yyyy");
        Long e = TimeTools.dataTime2Timestamp(end, "MM/dd/yyyy") + 86400000L;
        List<SalaryLog> ret = new ArrayList<>();
        for (SalaryScene scene : SalaryScene.values()) {
            for (SalaryState stat : SalaryState.values()) {
                ret.addAll(salaryLogService.findByStateAndSceneAndDate(rider, scene, stat, s, e));
            }
        }
        ret.sort((a, b) -> a.getId() > b.getId() ? -1 : 1);
        map.put("logs", ret);
        return "manage/salaryDetail";
    }

    private void enrichLogs(List<AppToken> riders, Map<Long, Map<String, Integer>> logs, Long s, Long e) {
        for (AppToken rider : riders) {
            Map<String, Integer> tmp = new HashMap<>();
            for (SalaryState state : SalaryState.values()) {
                int total = 0;
                for (SalaryScene scene : SalaryScene.values()) {
                    Integer sum = salaryLogService.sumByStateAndSceneAndDate(rider.getId(), scene, state, s, e);
                    total += sum == null ? 0 : sum;
                }
                tmp.put(state.name(), total);
            }
            logs.put(rider.getId(), tmp);
        }
    }
}
