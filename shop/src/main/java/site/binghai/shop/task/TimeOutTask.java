package site.binghai.shop.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.service.PayBizServiceFactory;
import site.binghai.lib.service.UnifiedOrderService;
import site.binghai.lib.utils.BaseBean;
import site.binghai.shop.entity.Tuan;
import site.binghai.shop.service.CartItemService;
import site.binghai.shop.service.ShopOrderService;
import site.binghai.shop.service.TuanService;

import java.util.List;

/**
 * @author huaishuo
 * @date 2020/2/23 下午11:17
 **/
//@Component
//@EnableScheduling
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

    @Scheduled(cron = "0 * * * * ?")
    public void run() {
        List<UnifiedOrder> orderList = unifiedOrderService.scanTimeOut();
        if (!isEmptyList(orderList)) {
            orderList.forEach(o -> payBizServiceFactory.cancel(o));
        }
        List<Tuan> tuans = tuanService.scanTimeOut();
        tuans.forEach(t -> {
            tuanService.cancel(t);
        });

        cartItemService.clean();
    }
}
