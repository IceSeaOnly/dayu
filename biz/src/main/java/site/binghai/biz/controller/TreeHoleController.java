package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.entity.TreeHole;
import site.binghai.biz.service.TreeHoleService;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.WxUser;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/treehole/")
public class TreeHoleController extends BaseController {

    @Autowired
    private TreeHoleService treeHoleService;

    @PostMapping("create")
    public Object create(@RequestBody Map map) {
        Object ret;
        try {
            WxUser wxUser = getUser();
            map.put("openId", wxUser.getOpenId());
            map.put("userId", wxUser.getId());
            ret = treeHoleService.create(map);
        } catch (Exception e) {
            logger.error("create treehole error!,map:{}", map, e);
            return fail(e.getMessage());
        }
        return success(ret, null);
    }

    @GetMapping("list")
    public Object list() {
        List<TreeHole> treeHoleList = treeHoleService.findByUser(getUser());
        return success(treeHoleList, null);
    }

    @GetMapping("delete")
    public Object delete(@RequestParam Long id) {
        treeHoleService.authDelete(getUser(), id);
        return success();
    }
}
