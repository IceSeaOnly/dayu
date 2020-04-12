package site.binghai.biz.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.def.WxEventHandler;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.utils.BaseBean;
import site.binghai.lib.utils.TimeTools;
import site.binghai.lib.utils.TplGenerator;

/**
 * @date 2018/12/9 下午6:28
 **/
@Component
public class WxEventHandlerImpl extends BaseBean implements WxEventHandler {
    @Autowired
    private WxTplMessageService wxTplMessageService;
    @Autowired
    private IceConfig iceConfig;

    @Override
    public void onPaid(UnifiedOrder order) {
        JSONObject msg = new TplGenerator(
                iceConfig.getPaySuccessTplId(),
                iceConfig.getAppRoot() + "/shop/orders?type=PAIED",
                order.getOpenId()
        ).put("first", order.getUserName() + "，您好！我们已经收到您的订单：" + order.getTitle() + ",感谢您的使用，祝您生活愉快！")
                .put("keyword1", order.getUserName())
                .put("keyword2", TimeTools.format2yyyyMMdd(now()) + order.getOrderId())
                .put("keyword3", order.getShouldPay() / 100.0 + "元")
                .put("keyword4", order.getTitle())
                .put("remark", "订单详情可点击本消息查看")
                .build();

        wxTplMessageService.send(msg);
    }

    @Override
    public void onCanceled(UnifiedOrder order) {
        JSONObject msg = new TplGenerator(
                iceConfig.getOrderCancelTplId(),
                iceConfig.getAppRoot() + "/shop/orders",
                order.getOpenId()
        ).put("first", order.getUserName() + "您好！您的" + order.getTitle() + "已取消，如有使用积分优惠券，将于稍后返还，感谢您的使用，祝您生活愉快！")
                .put("keyword1", TimeTools.format2yyyyMMdd(now()) + order.getOrderId())
                .put("keyword2", order.getTitle())
                .put("remark", "订单详情可点击本消息查看")
                .build();

        wxTplMessageService.send(msg);
    }


    @Override
    public void onTimeoutCanceled(UnifiedOrder order) {
        JSONObject msg = new TplGenerator(
                iceConfig.getOrderCancelTplId(),
                iceConfig.getAppRoot() + "/shop/orders",
                order.getOpenId()
        ).put("first", order.getUserName() + "您好！您的" + order.getTitle() + "订单因支付超时已取消，如有使用积分优惠券，将于稍后返还，感谢您的使用，祝您生活愉快！")
                .put("keyword1", TimeTools.format2yyyyMMdd(now()) + order.getOrderId())
                .put("keyword2", order.getTitle())
                .put("remark", "订单详情可点击本消息查看")
                .build();

        wxTplMessageService.send(msg);
    }

    @Override
    public void onTuanCreate(Long tuanId, String goodsName, String openId, Integer price, Integer ptSize) {
        JSONObject msg = new TplGenerator(
                iceConfig.getTuanCreateTplId(),
                iceConfig.getAppRoot() + "/shop/tuanDetail?t=" + tuanId,
                openId
        ).put("first", goodsName + "开团成功，快去邀请小伙伴参团吧！")
                .put("keyword1", goodsName)
                .put("keyword2", String.format("￥%.2f", price / 100.0))
                .put("keyword3", ptSize + "人团")
                .put("keyword4", "你自己")
                .put("remark", "24小时未拼团成功的将会自动退款")
                .build();

        wxTplMessageService.send(msg);
    }

    @Override
    public void onTuanFull(Long tuanId, String goodsName, Integer price, String openId) {
        JSONObject msg = new TplGenerator(
                iceConfig.getTuanFullTplId(),
                iceConfig.getAppRoot() + "/shop/tuanDetail?t=" + tuanId,
                openId
        ).put("first", goodsName + "恭喜恭喜，拼团成功，耐心等待收货吧~！")
                .put("keyword1", goodsName)
                .put("keyword2", TimeTools.now())
                .put("remark", "点击查看拼团详情")
                .build();

        wxTplMessageService.send(msg);
    }

    @Override
    public void onTuanJoin(Long tuanId, String goodsName, Integer shouldPay, String openId) {
        JSONObject msg = new TplGenerator(
                iceConfig.getTuanJoinTplId(),
                iceConfig.getAppRoot() + "/shop/tuanDetail?t=" + tuanId,
                openId
        ).put("first", goodsName + "参团成功，快去邀请小伙伴一起吧！")
                .put("keyword1", goodsName)
                .put("keyword2", String.format("￥%.2f", shouldPay / 100.0))
                .put("remark", "请耐心等待收货，点击查看拼团详情")
                .build();

        wxTplMessageService.send(msg);
    }

    @Override
    public void onTuanFail(Long tId, String title, Integer price, String openId) {
        JSONObject msg = new TplGenerator(
                iceConfig.getTuanFailTplId(),
                iceConfig.getAppRoot() + "/shop/tuanDetail?t=" + tId,
                openId
        ).put("first", "好遗憾拼团失败")
                .put("keyword1", title)
                .put("keyword2", String.format("￥%.2f", price / 100.0))
                .put("keyword3", String.format("￥%.2f", price / 100.0))
                .put("remark", "拼团失败，退款稍后到账")
                .build();

        wxTplMessageService.send(msg);
    }

    @Override
    public void onOrderProcessing(String openId, String userName, String text) {
        JSONObject msg = new TplGenerator(
                iceConfig.getOrderProcessingTplId(),
                iceConfig.getAppRoot() + "/shop/orders?type=PROCESSING",
                openId
        ).put("first", "亲，您购买的" + text + "等商品已经打包出库，等待配送员配送啦！")
                .put("keyword1", userName + "的订单")
                .put("keyword2", text + "等商品")
                .put("remark", "配送员接单后将提供配送员联系方式哦~")
                .build();

        wxTplMessageService.send(msg);
    }

    @Override
    public void onOrderDelivering(String openId, String text, String riderName, String riderPhone) {
        JSONObject msg = new TplGenerator(
                iceConfig.getOrderDeliveryTplId(),
                iceConfig.getAppRoot() + "/shop/orders?type=PROCESSING",
                openId
        ).put("first", "亲，您购买的" + text + "等商品已经在配送中啦，请注意接听配送员电话哦！")
                .put("keyword3", riderName)
                .put("keyword4", riderPhone)
                .put("keyword2", "-")
                .put("keyword1", text + "等商品")
                .put("remark", "请注意接听配送员电话哦~")
                .build();

        wxTplMessageService.send(msg);
    }

    @Override
    public void onOrderDeliveried(String openId, String text, String riderName, String riderPhone) {
        JSONObject msg = new TplGenerator(
                iceConfig.getOrderDeliveryDoneTplId(),
                iceConfig.getAppRoot() + "/shop/orders?type=COMPLETE",
                openId
        ).put("first", "亲，您购买的" + text + "等商品已经配送完成啦，记得好评哦！")
                .put("keyword1", "-")
                .put("keyword2", "配送完成")
                .put("remark", "记得好评哦，欢迎再次光临~")
                .build();

        wxTplMessageService.send(msg);
    }

}
