package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.consts.DiamondKey;
import site.binghai.biz.entity.CommonBuyEvidence;
import site.binghai.biz.entity.TreeHole;
import site.binghai.biz.service.CommonBuyEvidenceService;
import site.binghai.biz.service.DiamondService;
import site.binghai.biz.service.TreeHoleService;
import site.binghai.lib.controller.AbstractPayBizController;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.PayBizServiceFactory;
import site.binghai.lib.service.UnifiedOrderService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/treehole/")
public class TreeHoleController extends AbstractPayBizController<CommonBuyEvidence> {

    @Autowired
    private TreeHoleService treeHoleService;
    @Autowired
    private CommonBuyEvidenceService commonBuyEvidenceService;
    @Autowired
    private DiamondService diamondService;

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

    @GetMapping("read")
    public Object read(@RequestParam Long thId) {
        TreeHole hole = treeHoleService.findById(thId);
        if (hole == null) {
            return fail("树洞里好像没有这个秘密诶...");
        }

        if (commonBuyEvidenceService.hasBuy(hole.getId(), getUser().getId(), PayBizEnum.TREE_HOLE_BUY_FEE)) {
            return fail("NEED_BUY");
        }

        return success(hole, null);
    }

    @GetMapping("delete")
    public Object delete(@RequestParam Long id) {
        treeHoleService.authDelete(getUser(), id);
        return success();
    }

    @GetMapping("buy")
    public Object buy(@RequestParam Long thId) {
        TreeHole hole = treeHoleService.findById(thId);
        if (hole == null || !hole.getPassed()) {
            return fail("来晚了，秘密已经被别人取走啦");
        }

        if (commonBuyEvidenceService.hasBuy(hole.getId(), getUser().getId(), PayBizEnum.TREE_HOLE_BUY_FEE)) {
            return fail("你已经购买过啦！");
        }

        CommonBuyEvidence evidence = new CommonBuyEvidence();
        evidence.setTargetId(hole.getId());
        evidence.setPayBiz(PayBizEnum.COMMON_BUY_EVIDENCE.getCode());
        try {
            return create(evidence, Integer.valueOf(diamondService.get(DiamondKey.TREE_HOLE_BUY_FEE)));
        } catch (Exception e) {
            logger.error("创单失败!CommonBuyEvidence:{}", evidence);
        }
        return fail("创单失败,请稍后重试");
    }
}
