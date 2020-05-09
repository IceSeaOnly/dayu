package site.binghai.shop.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.Manager;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.service.ManagerService;
import site.binghai.lib.utils.TimeTools;
import site.binghai.shop.entity.AppToken;
import site.binghai.shop.entity.ShopOrder;
import site.binghai.shop.service.AppTokenService;
import site.binghai.shop.service.SchoolService;
import site.binghai.shop.service.ShopOrderService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author icesea
 * @date 2020/3/15 下午9:57
 **/
@RequestMapping("manage")
@Controller
public class ManagerManageController extends BaseController {
    @Autowired
    private SchoolService schoolService;
    @Autowired
    private ManagerService managerService;

    @GetMapping("managers")
    public String riders(ModelMap map) {
        List<Manager> managers = managerService.findAll();
        managers.forEach(m -> m.setSchoolName(schoolService.findById(m.getSchoolId()).getSchoolName()));
        map.put("manager", getManager());
        map.put("managers", managers);
        map.put("schools", schoolService.findAll());
        return "manage/managers";
    }

    @GetMapping("managerCtrl")
    @ResponseBody
    public Object managerCtrl(@RequestParam Long managerId, ModelMap map) {
        Manager manager = getManager();
        if (manager.getAdmin() == null || !manager.getAdmin()) {
            return fail("权限不足");
        }
        Manager m = managerService.findById(managerId);
        m.setForbidden(!m.getForbidden());
        managerService.update(m);
        return success();
    }

    @PostMapping("addNewManager")
    public String addNewManager(@RequestParam String userName,
                                @RequestParam Long schoolId) {
        List<Manager> exist = managerService.findByUserName(userName);
        if (!isEmptyList(exist)) {
            return "redirect:managers";
        }

        Manager manager = new Manager();
        manager.setUserName(userName);
        manager.setNickName(userName);
        manager.setAdmin(false);
        manager.setForbidden(false);
        manager.setPassWord("123456");
        manager.setSchoolId(schoolId);
        managerService.save(manager);
        return "redirect:managers";
    }

    @PostMapping("resetMyPassword")
    public String resetMyPassword(@RequestParam String passwd) {
        Manager m = managerService.findById(getManager().getId());
        m.setPassWord(passwd);
        managerService.update(m);
        return "redirect:/p/logout";
    }

    @GetMapping("resetManagerPassWord")
    @ResponseBody
    public Object resetPassWord(@RequestParam Long managerId) {
        Manager manager = getManager();
        if (manager.getAdmin() == null || !manager.getAdmin()) {
            return fail("权限不足");
        }
        Manager m = managerService.findById(managerId);
        m.setPassWord("123456");
        managerService.update(m);
        return success(m.getPassWord(), null);
    }

    @GetMapping("accountActive")
    @ResponseBody
    public Object accountActive() {
        Manager manager = getManager();
        Manager m = managerService.findById(manager.getId());
        if (m == null || m.getForbidden()) {
            return fail("");
        }
        return success();
    }
}
