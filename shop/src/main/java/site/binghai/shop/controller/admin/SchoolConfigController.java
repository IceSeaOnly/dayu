package site.binghai.shop.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.Manager;
import site.binghai.lib.service.ManagerService;
import site.binghai.shop.entity.School;
import site.binghai.shop.service.SchoolService;

import java.util.Map;

/**
 * @author huaishuo
 * @date 2020/3/8 下午12:58
 **/
@Controller
@RequestMapping("manage")
public class SchoolConfigController extends BaseController {
    @Autowired
    private ManagerService managerService;
    @Autowired
    private SchoolService schoolService;

    @GetMapping("schoolConfig")
    public String schoolConfig(ModelMap map) {
        map.put("schools", schoolService.findAll());
        map.put("manager", getManager());

        return "manage/schoolConfig";
    }

    @GetMapping("schoolChangeTo")
    public String schoolChangeTo(@RequestParam Long id) {
        Manager manager = getManager();
        manager.setSchoolId(id);
        managerService.updateSchool(manager);
        getSession().invalidate();
        return "redirect:index";
    }

    @PostMapping("ajaxUpdateSchool")
    @ResponseBody
    public Object ajaxUpdateSchool(@RequestBody Map map) {
        School school = schoolService.findById(getLong(map, "id"));
        school.setSchoolImg(getString(map, "schoolImg"));
        school.setSchoolName(getString(map, "schoolName"));
        school.setVisible(getBoolean(map, "visible"));
        schoolService.update(school);
        return success();
    }

    @PostMapping("ajaxAddSchool")
    @ResponseBody
    public Object ajaxAddSchool(@RequestBody Map map) {
        School school = new School();
        school.setSchoolImg(getString(map, "schoolImg"));
        school.setSchoolName(getString(map, "schoolName"));
        school.setVisible(Boolean.FALSE);
        schoolService.save(school);
        return success();
    }
}
