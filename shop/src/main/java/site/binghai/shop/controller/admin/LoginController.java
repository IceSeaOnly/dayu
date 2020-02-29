package site.binghai.shop.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.Manager;
import site.binghai.lib.service.ManagerService;

/**
 * @author huaishuo
 * @date 2020/2/26 上午12:19
 **/
@RequestMapping("p")
@Controller
public class LoginController extends BaseController {
    @Autowired
    private ManagerService managerService;

    @GetMapping("login")
    public String loginPage() {
        return "manage/login";
    }

    @PostMapping("doLogin")
    public String doLogin(@RequestParam String userName, @RequestParam String passWord) {
        if (hasEmptyString(userName, passWord)) {
            return "redirect:/p/login?err=1";
        }

        Manager manager = managerService.findByUserNameAndPass(userName, passWord);
        if (manager == null) {
            return "redirect:/p/login?err=1";
        }
        persistent(manager);
        return "redirect:/manage/index";
    }

    @GetMapping("logout")
    public String logout() {
        getSession().invalidate();
        return "redirect:/p/login";
    }

}
