package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.biz.def.ManualInvoke;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.controller.BaseController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/debuger/")
public class DebugerController extends BaseController {
    private Map<String, ManualInvoke> manualInvokeMap;
    @Autowired
    private IceConfig iceConfig;

    @RequestMapping("invoke")
    public Object invoke(@RequestParam String clazz, @RequestParam String token) {
        if (!iceConfig.getDebugCode().equals(token)) {
            return fail("token error");
        }

        return success(manualInvokeMap.get(clazz).invoke(), null);
    }

    public void joinList(List<ManualInvoke> list) {
        manualInvokeMap = new HashMap<>();
        list.forEach(v -> manualInvokeMap.put(v.getClass().getSimpleName(), v));
    }
}
