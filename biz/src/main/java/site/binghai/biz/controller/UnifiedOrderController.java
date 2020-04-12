package site.binghai.biz.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import site.binghai.biz.def.Result;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.CouponService;
import site.binghai.lib.service.PayBizServiceFactory;
import site.binghai.lib.service.UnifiedOrderService;
import site.binghai.lib.service.WxUserService;

import java.net.URLEncoder;
import java.util.List;

@Controller
@RequestMapping("/user/unified/")
public class UnifiedOrderController extends BaseController {

    @Autowired
    private UnifiedOrderService unifiedOrderService;
    @Autowired
    private IceConfig iceConfig;
    @Autowired
    private WxUserService wxUserService;
    @Autowired
    private PayBizServiceFactory payBizServiceFactory;
    @Autowired
    private CouponService couponService;

    @GetMapping("detail")
    @ResponseBody
    public Object detail(@RequestParam Long unifiedId) {
        WxUser wxUser = getSessionPersistent(WxUser.class);
        UnifiedOrder order = unifiedOrderService.findById(unifiedId);
        if (order == null || !order.getUserId().equals(wxUser.getId())) {
            return fail("鉴权失败");
        }

        JSONObject map = newJSONObject();
        map.put("order", order);
        map.put("extra", loadMoreInfo(order));
        map.put("payOptions", multiPay(order));
        return success(map, null);
    }

    @GetMapping("cashier")
    public String cashier(@RequestParam Long unifiedId, ModelMap map) throws Exception {
        UnifiedOrder order = unifiedOrderService.findById(unifiedId);
        WxUser user = getUser();
        if (order == null || !order.getUserId().equals(user.getId())) {
            return "redirect:/500?error=" + URLEncoder.encode("非法访问", "UTF-8");
        }
        if (order.getStatus() > 1) {
            return e500(
                    "订单状态错误:" + OrderStatusEnum.valueOf(order.getStatus()).getName());
        }
        map.put("order", order);
        map.put("user", user);
        map.put("payOptions", multiPay(order));
        return "cashier";
    }

    private JSONArray loadMoreInfo(UnifiedOrder order) {
        JSONArray ret = newJSONArray();
        payBizServiceFactory.get(order.getAppCode()).moreInfo(order)
                .forEach((k, v) -> {
                    if (hasEmptyString(v)) {
                        return;
                    }
                    JSONObject item = new JSONObject();
                    item.put("key", k);
                    item.put("value", v);
                    ret.add(item);
                });
        return ret;
    }

    @GetMapping("walletPay")
    public String walletPay(@RequestParam Long unifiedId, String callBack, ModelMap map) {
        WxUser wxUser = updateSessionUser();
        UnifiedOrder order = unifiedOrderService.findById(unifiedId);
        if (order.getStatus() > 1) {
            return "redirect:" + (callBack == null ? "detail?unifiedId=" + unifiedId : callBack);
        }
        boolean enableWalletPay = PayBizEnum.valueOf(order.getAppCode()).isWalletPay();

        if (wxUser.getWallet() == null || wxUser.getWallet() < order.getShouldPay() || !enableWalletPay) {
            return "redirect:detail?unifiedId=" + unifiedId;
        }

        wxUser.setWallet(wxUser.getWallet() - order.getShouldPay());
        wxUserService.update(wxUser);

        try {
            payBizServiceFactory.onPayNotify(order.getOrderId());
        } catch (Exception e) {
            logger.error("wallet pay failed,{}", order, e);
        }

        return "redirect:" + (callBack == null ? "detail?unifiedId=" + unifiedId : callBack);
    }

    public JSONObject multiPay(UnifiedOrder order) {
        JSONObject map = newJSONObject();

        WxUser wxUser = updateSessionUser();
        boolean enableWalletPay = PayBizEnum.valueOf(order.getAppCode()).isWalletPay();
        if (wxUser.getWallet() == null || wxUser.getWallet() < order.getShouldPay()) {
            enableWalletPay = false;
        }

        map.put("enableWalletPay", enableWalletPay);
        map.put("wxPayUrl", payBizServiceFactory.buildWxPayUrl(order));
        map.put("walletPayUrl",
                "/user/unified/walletPay?unifiedId=" + order.getId() + payBizServiceFactory.buildCallbackUrl(order));
        return map;
    }

    private WxUser updateSessionUser() {
        WxUser user = getUser();
        user = wxUserService.findById(user.getId());
        persistent(user);
        return user;
    }

    @GetMapping("list")
    @ResponseBody
    public Object list(ModelMap map) {
        JSONArray arr = newJSONArray();
        WxUser user = getUser();
        List<UnifiedOrder> data = unifiedOrderService.findByUserIdOrderByIdDesc(user.getId(), 0, 1000);
        data.stream().filter(u -> !u.getAppCode().equals(PayBizEnum.MERGE_PAY.getCode()))
                .forEach(v -> {
                    JSONObject extra = newJSONObject();
                    extra.put("img", PayBizEnum.valueOf(v.getAppCode()).getImg());
                    extra.put("title", v.getTitle());
                    extra.put("orderNo", v.getOrderId());
                    extra.put("created", v.getCreatedTime());
                    extra.put("orderStatus", OrderStatusEnum.valueOf(v.getStatus()).getName());
                    extra.put("shouldPay", v.getShouldPay());
                    extra.put("unifiedId", v.getId());

                    arr.add(extra);
                });

        return success(arr, null);
    }

    @GetMapping("pay")
    @ResponseBody
    public Object pay(@RequestParam Long unifiedId) {
        return "redirect:" + iceConfig.getWxPayUrl() + "?unifiedId=" + unifiedId;
    }

    @GetMapping("cancel")
    @ResponseBody
    public Object cancel(@RequestParam Long unifiedId) {
        UnifiedOrder unifiedOrder = unifiedOrderService.findById(unifiedId);
        WxUser wxUser = getSessionPersistent(WxUser.class);

        if (unifiedId == null || !unifiedOrder.getUserId().equals(wxUser.getId())) {
            return fail("身份认证不通过");
        }

        switch (OrderStatusEnum.valueOf(unifiedOrder.getStatus())) {
            case CREATED:
            case PAYING:
            case PAIED:
                return cancelUnifiedOrder(unifiedOrder);
            case PROCESSING:
                return cancelProcessingOrder(unifiedOrder);
            case REFUNDING:
            case CANCELED:
                return fail("该订单已取消或正在取消中，无法再次取消!");
            case COMPLETE:
                return fail("订单已完成，无法取消，如有疑问请咨询客服!");
            default:
                return fail("订单状态不正确，无法取消");
        }
    }

    /**
     * 取消处理中的订单，暂时直接拒绝
     */
    private Object cancelProcessingOrder(UnifiedOrder unifiedOrder) {
        return fail("订单处理中，无法取消，请联系客服");
    }

    /**
     * 取消未处理的订单: 退款+取消
     */
    public Object cancelUnifiedOrder(UnifiedOrder unifiedOrder) {
        if (OrderStatusEnum.valueOf(unifiedOrder.getStatus()) == OrderStatusEnum.PAIED) {
            Result<Boolean> ret = cancelBizOrder(unifiedOrder);
            if (ret.getSucceed()) {
                return refund(unifiedOrder);
            }
            return fail(ret.getMessage());
        } else {
            Result<Boolean> ret = cancelBizOrder(unifiedOrder);
            if (ret.getSucceed() && refund(unifiedOrder)) {
                return success(null, "取消成功");
            } else {
                return fail("取消失败-BIZ-NOT-SUPPORT");
            }
        }
    }

    private Result<Boolean> cancelBizOrder(UnifiedOrder unifiedOrder) {
        try {
            payBizServiceFactory.cancel(unifiedOrder);
        } catch (Exception e) {
            return new Result<>(Boolean.FALSE, e, Boolean.FALSE);
        }
        return new Result();
    }

    /**
     * 退款
     */
    private boolean refund(UnifiedOrder unifiedOrder) {
        unifiedOrderService.cancel(unifiedOrder.getId());
        WxUser user = wxUserService.findById(getUser().getId());
        persistent(user);
        return true;
    }
}
