package site.binghai.biz.controller.windWheel;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.controller.UnifiedOrderController;
import site.binghai.biz.entity.SharePage;
import site.binghai.biz.entity.windWheel.ExpressBrand;
import site.binghai.biz.entity.windWheel.DeliveryOrder;
import site.binghai.biz.entity.windWheel.ExpressOwner;
import site.binghai.biz.service.ExpressBrandService;
import site.binghai.biz.service.DeliveryOrderService;
import site.binghai.biz.service.ExpressOwnerService;
import site.binghai.biz.service.SharePageService;
import site.binghai.biz.service.jdy.JdyLogService;
import site.binghai.lib.controller.AbstractPayBizController;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.service.UnifiedOrderService;
import site.binghai.lib.service.WxUserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author huaishuo
 * @date 2018/12/3 下午11:01
 **/
@RestController
@RequestMapping("/user/exp/delivery/")
public class DeliveryController extends AbstractPayBizController<DeliveryOrder> {
    @Autowired
    private DeliveryOrderService deliveryOrderService;
    @Autowired
    private ExpressBrandService expressBrandService;
    @Autowired
    private WxUserService wxUserService;
    @Autowired
    private ExpressOwnerService expressOwnerService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;
    @Autowired
    private JdyLogService jdyLogService;
    @Autowired
    private SharePageService sharePageService;

    /**
     * 用户下单
     */
    @PostMapping("booking")
    public Object booking(@RequestBody Map map) {
        DeliveryOrder order = deliveryOrderService.newInstance(map);
        if (hasEmptyString(order.getExpressId(), order.getUserName(), order.getUserAddress(), order.getUserPhone())) {
            return fail("请输入完整信息!");
        }
        ExpressBrand brand = expressBrandService.findById(order.getExpressId());
        if (brand == null || !brand.getEnableSend()) {
            return fail("你选择的快递打烊了！明天再来看看吧！");
        }
        WxUser wxUser = getUser();
        order.setOpenId(wxUser.getOpenId());
        order.setExpressBrand(brand.getName());
        order.setExpressPhone(brand.getPhone());

        try {
            return create(order, brand.getName() + "代寄单", brand.getPrice());
        } catch (Exception e) {
            logger.error("{} 代寄订单创单失败! 参数:{}", brand.getName(), map, e);
            return fail("创单失败!" + e.getMessage());
        }
    }

    /**
     * 默认取上一次下单信息 否则留空
     */
    @GetMapping("getUserBaseInfo")
    public Object getUserBaseInfo() {
        DeliveryOrder order = deliveryOrderService.getLastOrder(getUser());
        if (order != null) {
            return success(order, null);
        }
        return fail("last order not exist");
    }

    @GetMapping("manageIndex")
    public Object manageIndex() {
        WxUser user = getLastestUser();
        List<ExpressBrand> expressBrands = null;
        if (user.getExpDeliverySuperAuth()) {
            expressBrands = expressBrandService.findAll(999);
        } else {
            expressBrands = expressOwnerService.listByOwnerId(user.getId(), expressBrandService);
        }

        JSONObject ret = newJSONObject();
        ret.put("expList", expressBrands);
        ret.put("user", user);
        ret.put("valid", !isEmptyList(expressBrands));

        return success(ret, null);
    }

    @PostMapping("createNewExpress")
    public Object createNewExpress(@RequestBody Map map) {
        WxUser user = getLastestUser();
        if (!user.getExpDeliverySuperAuth()) {
            return fail("权限不足");
        }

        ExpressBrand brand = expressBrandService.newInstance(map);
        brand.setId(null);
        if (hasEmptyString(brand.getManagerName(), brand.getName(), brand.getPhone(), brand.getPrice())) {
            return fail("填写不完整");
        }

        if (brand.getPrice() <= 0) {
            return fail("金额必须大于1分");
        }

        brand.setEnableTake(false);
        brand.setEnableSend(false);
        expressBrandService.save(brand);
        return success();
    }

    /**
     * 以我为根节点的授权树
     */
    @GetMapping("authMap")
    public Object authMap() {
        WxUser user = getLastestUser();
        List<ExpressOwner> all = expressOwnerService.findAll(9999);
        Map<Long, ExpressOwner> ret = new HashMap<>();
        boolean changed = true;
        while (changed) {
            int old_size = ret.size();
            all.forEach(one -> {
                if (user.getId().equals(one.getShareFromUserId()) || ret.containsKey(one.getShareFromUserId())) {
                    if (!ret.containsKey(one.getId())) {
                        ret.put(one.getId(), one);
                    }
                }
            });
            int now_size = ret.size();
            if (now_size == old_size) {
                changed = false;
            }
        }

        if (ret.size() == 0) {
            return fail("你暂时未作任何授权");
        }
        return success(ret.values(), null);
    }

    @GetMapping("manageOrderList")
    public Object manageOrderList(@RequestParam Long eid, @RequestParam Integer status) {
        WxUser user = getLastestUser();
        if (!user.getExpDeliverySuperAuth()) {
            ExpressOwner owner = expressOwnerService.findByUserIdAndBrandId(user.getId(), eid);
            if (owner == null) {
                return fail("未授权");
            }
        }

        JSONObject ret = newJSONObject();
        List list = deliveryOrderService.findByIdBrandIdAndStatus(eid, status);
        ExpressBrand brand = expressBrandService.findById(eid);

        ret.put("list", list);
        ret.put("brand", brand);

        return success(ret, isEmptyList(list) ? "无订单" : "已加载");
    }

    @Autowired
    private UnifiedOrderController unifiedOrderController;

    @GetMapping("manageCancelOrder")
    public Object manageCancelOrder(@RequestParam Long id) {
        WxUser user = getLastestUser();

        DeliveryOrder order = deliveryOrderService.findById(id);
        if (order == null) {
            return fail("非法参数");
        }
        if (!user.getExpDeliverySuperAuth()) {
            ExpressOwner owner = expressOwnerService.findByUserIdAndBrandId(user.getId(), order.getExpressId());
            if (owner == null) {
                return fail("未授权");
            }
        }

        jdyLogService.save(String.format("user %d canceled express delivery order %d", user.getId(), id));
        return unifiedOrderController.cancelUnifiedOrder(unifiedOrderService.findById(order.getUnifiedId()));
    }

    @GetMapping("manageCompleteOrder")
    public Object manageCompleteOrder(@RequestParam Long id) {
        WxUser user = getLastestUser();

        DeliveryOrder order = deliveryOrderService.findById(id);
        if (order == null) {
            return fail("非法参数");
        }
        if (!user.getExpDeliverySuperAuth()) {
            ExpressOwner owner = expressOwnerService.findByUserIdAndBrandId(user.getId(), order.getExpressId());
            if (owner == null) {
                return fail("未授权");
            }
        }

        jdyLogService.save(String.format("user %d complete express delivery order %d", user.getId(), id));

        order.setStatus(OrderStatusEnum.COMPLETE.getCode());
        deliveryOrderService.update(order);
        return success();
    }

    @GetMapping("shareAuth")
    public Object shareAuth(@RequestParam Long eid) {
        WxUser user = getLastestUser();

        if (!user.getExpDeliverySuperAuth()) {
            ExpressOwner owner = expressOwnerService.findByUserIdAndBrandId(user.getId(), eid);
            if (owner == null) {
                return fail("未授权");
            }
        }

        ExpressBrand brand = expressBrandService.findById(eid);

        JSONObject data = newJSONObject();
        data.put("from", user.getId());
        data.put("eid", eid);
        SharePage sharePage = sharePageService.create(
            user,
            "管理授权",
            "请分享此页面给你要授权的微信号",
            user.getUserName() + "授权给你" + brand.getName() + "代寄订单管理权限",
            "接受授权",
            "/user/exp/delivery/acceptDeliveryAuth",
            data.toJSONString());

        return success(sharePage, null);
    }

    @GetMapping("acceptDeliveryAuth")
    public Object acceptDeliveryAuth(@RequestParam Long shareId) {
        SharePage page = sharePageService.consume(shareId, getUser().getOpenId());
        if (page == null) {
            return fail("无效授权");
        }

        JSONObject data = JSONObject.parseObject(page.getData());
        ExpressBrand brand = expressBrandService.findById(data.getLong("eid"));

        ExpressOwner owner = expressOwnerService.findByUserIdAndBrandId(getUser().getId(), brand.getId());
        if (owner != null) {
            return fail("授权已存在!");
        }
        owner = new ExpressOwner();
        owner.setBrandId(brand.getId());
        owner.setWxUserId(getUser().getId());
        owner.setBrandName(brand.getName());
        owner.setOpenId(getUser().getOpenId());
        owner.setPhone(getUser().getPhone());
        owner.setUserName(getUser().getUserName());
        owner.setShareFromUserId(data.getLong("from"));
        owner.setShareFromName(wxUserService.findById(data.getLong("from")).getUserName());

        expressOwnerService.save(owner);
        return success("REDIRECT", "DeliveryManageIndex");
    }

    @GetMapping("cancelAuth")
    public Object cancelAuth(@RequestParam Long authId) {
        ExpressOwner expressOwner = expressOwnerService.findById(authId);
        WxUser user = getLastestUser();
        if (!user.getExpDeliverySuperAuth()) {
            if (expressOwner == null || !expressOwner.getShareFromUserId().equals(user.getId())) {
                return fail("未授权");
            }
        }

        List<ExpressOwner> all = expressOwnerService.findAll(9999)
            .stream()
            .filter(v -> v.getBrandId().equals(expressOwner.getBrandId()))
            .collect(Collectors.toList());

        Map<Long, ExpressOwner> ret = new HashMap<>();
        boolean changed = true;
        while (changed) {
            int old_size = ret.size();
            all.forEach(one -> {
                if (user.getId().equals(one.getShareFromUserId()) || ret.containsKey(one.getShareFromUserId())) {
                    if (!ret.containsKey(one.getId())) {
                        ret.put(one.getId(), one);
                    }
                }
            });
            int now_size = ret.size();
            if (now_size == old_size) {
                changed = false;
            }
        }

        ret.values().forEach(one -> {
            jdyLogService.save(String.format("%d deleted auth for user %d", user.getId(), one.getWxUserId()));
            expressOwnerService.delete(one.getId());
        });

        return success();
    }

    @GetMapping("optDeliveryStatus")
    public Object optDeliveryStatus(@RequestParam Long eid) {
        WxUser user = getLastestUser();

        if (!user.getExpDeliverySuperAuth()) {
            ExpressOwner owner = expressOwnerService.findByUserIdAndBrandId(user.getId(), eid);
            if (owner == null) {
                return fail("未授权");
            }
        }

        ExpressBrand brand = expressBrandService.findById(eid);
        brand.setEnableSend(!brand.getEnableSend());
        expressBrandService.update(brand);
        return success();
    }

    private WxUser getLastestUser() {
        WxUser wxUser = wxUserService.findById(getUser().getId());
        persistent(wxUser);
        return wxUser;
    }
}
