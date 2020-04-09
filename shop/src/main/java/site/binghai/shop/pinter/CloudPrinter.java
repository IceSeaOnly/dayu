package site.binghai.shop.pinter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.binghai.lib.config.IceConfig;
import site.binghai.shop.entity.ShopOrder;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Component
public class CloudPrinter {
    private CloudPrinterProxy printerProxy;
    @Autowired
    private IceConfig iceConfig;


    @PostConstruct
    public void init() {
        printerProxy = CloudPrinterProxy.getInstance();
        printerProxy.init(iceConfig.getYilianyunClientId(), iceConfig.getYilianyunClientSecret());
        printerProxy.getFreedomToken();
        printerProxy.refreshToken();
        printerProxy.addPrinter(iceConfig.getYilianyunMachineCode(), iceConfig.getYilianyunMechineSecret());

    }

    public String print(ShopOrder order, String orderId, int pieces) {
        return print(Arrays.asList(order), orderId, pieces);
    }

    public String print(List<ShopOrder> orders, String orderId, int pieces) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pieces; i++) {
            sb.append(print(orders, orderId));
            sb.append(";");
        }
        return sb.toString();
    }

    public String print(List<ShopOrder> orders, String orderId) {
        printerProxy.refreshToken();
        StringBuilder sb = new StringBuilder();
        sb.append("<MN>1</MN>");
        sb.append("<center><FS2>曹操速购</FS2></center>\n");
        sb.append("<FB>" + orderId + "</FB>\n");
        if (orders.get(0).getPtOrder()) {
            sb.append("<FW2>拼团订单</FW2>\n");
        }
        sb.append("买家:" + orders.get(0).getBuyerName() + "\n");
        sb.append("======================\n");
        int total = 0;
        for (ShopOrder order : orders) {
            sb.append("商品:" + order.getTitle() + "\n");
            sb.append("规格:" + order.getStandardInfo() + "\n");
            sb.append("<FB>数量:" + order.getSize() + "</FB>\n");
            sb.append("单价:" + String.format("￥%.2f", order.getPrice() / 100.0) + "\n");
            sb.append("小计:" + String.format("￥%.2f", order.getTotalPrice() / 100.0) + "\n");
            sb.append("-----------------\n");

            total += order.getTotalPrice();
        }
        sb.append("总计:" + String.format("￥%.2f", total / 100.0) + "\n");
        sb.append("收货姓名:" + orders.get(0).getReceiver() + "\n");
        sb.append("收货电话:" + orders.get(0).getReceiverPhone() + "\n");
        sb.append("配送地址:" + orders.get(0).getReceiverAddress() + "\n");
        sb.append("<QR>" + orderId + "</QR>");
        String resp = printerProxy.print(iceConfig.getYilianyunMachineCode(), sb.toString(), orderId);
        System.out.println(orderId + "打印结果：" + resp);
        return resp;
    }
}
