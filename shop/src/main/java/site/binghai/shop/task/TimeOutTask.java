package site.binghai.shop.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.service.PayBizServiceFactory;
import site.binghai.lib.service.UnifiedOrderService;
import site.binghai.lib.utils.BaseBean;
import site.binghai.lib.utils.HttpUtils;
import site.binghai.lib.utils.MD5;
import site.binghai.shop.entity.Tuan;
import site.binghai.shop.service.CartItemService;
import site.binghai.shop.service.ShopOrderService;
import site.binghai.shop.service.TuanService;

import java.util.List;

/**
 * @author icesea
 * @date 2020/2/23 下午11:17
 **/
@Component
@EnableScheduling
public class TimeOutTask extends BaseBean {
    @Autowired
    private ShopOrderService shopOrderService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;
    @Autowired
    private PayBizServiceFactory payBizServiceFactory;
    @Autowired
    private TuanService tuanService;
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private IceConfig iceConfig;


    @Scheduled(cron = "0 * * * * ?")
    public void run() {
        List<UnifiedOrder> orderList = unifiedOrderService.scanTimeOut();
        if (!isEmptyList(orderList)) {
            orderList.forEach(o -> {
                payBizServiceFactory.cancelByTimeout(o);
                o.setStatus(OrderStatusEnum.CANCELED.getCode());
                unifiedOrderService.update(o);
            });
        }
        List<Tuan> tuans = tuanService.scanTimeOut();
        tuans.forEach(t -> {
            tuanService.cancel(t);
        });
        cartItemService.clean();
        tryRefund();
    }

    private void tryRefund() {
        List<UnifiedOrder> orders = unifiedOrderService.loadRefunding();
        if (isEmptyList(orders)) {
            System.out.println("no refunding order found.");
        }else{
            orders.forEach(this::refund);
            System.out.println("refunded "+orders.size());
        }
    }

    private void refund(UnifiedOrder order) {
        String validate = MD5.encryption(order.getOrderId() + iceConfig.getWxValidateMD5Key() + order.getShouldPay());
        String resp = HttpUtils.sendGet(iceConfig.getWxRefundUrl(), "out_trade_no=" + order.getOrderId() + "&refund_fee=" + order.getShouldPay() + "&validate=" + validate);
        System.out.println("cancel order " + order.getId() + " resp = " + resp);
        if (resp.contains("ERROR")) {
            System.out.println(resp);
        } else {
            order.setStatus(OrderStatusEnum.CANCELED.getCode());
            unifiedOrderService.update(order);
        }
    }
}
