package site.binghai.shop.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.lib.def.UnifiedOrderMethods;
import site.binghai.lib.def.WxEventHandler;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.BaseService;
import site.binghai.lib.service.WxUserService;
import site.binghai.lib.utils.CompareUtils;
import site.binghai.shop.dao.ShopOrderDao;
import site.binghai.shop.entity.Product;
import site.binghai.shop.entity.ShopOrder;
import site.binghai.shop.entity.Tuan;
import site.binghai.shop.enums.TuanStatus;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @date 2020/2/2 下午12:47
 **/
@Service
public class ShopOrderService extends BaseService<ShopOrder> implements UnifiedOrderMethods<ShopOrder> {
    @Autowired
    private TuanService tuanService;
    @Autowired
    private WxUserService wxUserService;
    @Autowired
    private ProductService productService;
    @Autowired
    private WxEventHandler wxEventHandler;
    @Autowired
    private ShopOrderDao shopOrderDao;

    @Override
    protected JpaRepository<ShopOrder, Long> getDao() {
        return shopOrderDao;
    }

    @Override
    public JSONObject moreInfo(UnifiedOrder order) {
        ShopOrder shopOrder = loadByUnifiedOrder(order);
        JSONObject ret = newJSONObject();
        ret.put("商品款式", shopOrder.getStandardInfo());
        ret.put("收货人", shopOrder.getReceiver());
        ret.put("收货地址", shopOrder.getReceiverAddress());
        ret.put("收货人手机", shopOrder.getReceiverPhone());
        ret.put("备注信息", shopOrder.getRemark());
        return ret;
    }

    private ShopOrder findByUnifiedId(Long unifiedId) {
        ShopOrder exp = new ShopOrder();
        exp.setUnifiedId(unifiedId);
        return queryOne(exp);
    }

    @Override
    public ShopOrder loadByUnifiedOrder(UnifiedOrder order) {
        return findByUnifiedId(order.getId());
    }

    @Override
    @Transactional
    public ShopOrder cancel(UnifiedOrder order) {
        ShopOrder shopOrder = loadByUnifiedOrder(order);
        if (CompareUtils.inAny(shopOrder.getTuanStatus(), TuanStatus.FULL, TuanStatus.FAIL)) {
            throw new RuntimeException("拼团订单请在拼团成功后取消或等待超时自动取消");
        } else {
            wxEventHandler.onTuanFail(shopOrder.getTuanId(), shopOrder.getTitle(), order.getShouldPay(),
                order.getOpenId());
        }
        shopOrder.setStatus(OrderStatusEnum.CANCELED.getCode());
        update(shopOrder);
        return shopOrder;
    }

    @Override
    @Transactional
    public void onPaid(UnifiedOrder order) {
        ShopOrder shopOrder = loadByUnifiedOrder(order);
        WxUser user = wxUserService.findById(shopOrder.getUserId());
        Product product = productService.findById(shopOrder.getProductId());
        if (shopOrder.getPtOrder() != null && shopOrder.getPtOrder()) {
            if (shopOrder.getTuanId() == null) {
                Tuan tuan = tuanService.create(user, product.getTags(), shopOrder, product.getPtSize());
                shopOrder.setTuanId(tuan.getId());
                wxEventHandler.onTuanCreate(tuan.getId(), product.getTitle(), user.getOpenId(), product.getPrice(),
                    product.getPtSize());
            } else {
                Tuan tuan = tuanService.join(user, shopOrder);
                if (tuan.getStatus() == TuanStatus.INIT) {
                    wxEventHandler.onTuanJoin(shopOrder.getTuanId(), product.getTitle(), order.getShouldPay(),
                        order.getOpenId());
                }
            }
        }
        shopOrder.setStatus(OrderStatusEnum.PAIED.getCode());
        update(shopOrder);
    }

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.SCHOOL_SHOP;
    }

    public long countByUserIdAndState(Long userId, OrderStatusEnum status) {
        ShopOrder exp = new ShopOrder();
        exp.setUserId(userId);
        exp.setStatus(status.getCode());
        return count(exp);
    }

    public long countByUserId(Long userId) {
        ShopOrder exp = new ShopOrder();
        exp.setUserId(userId);
        return count(exp);
    }

    public List<ShopOrder> findByUserId(Long userId) {
        ShopOrder exp = new ShopOrder();
        exp.setUserId(userId);
        return sortQuery(exp, "id", true);
    }

    public List<ShopOrder> findByUserIdAndState(Long userId, OrderStatusEnum status) {
        ShopOrder exp = new ShopOrder();
        exp.setUserId(userId);
        exp.setStatus(status.getCode());
        return query(exp);
    }

    @Override
    public String buildPayCallbackUrl(UnifiedOrder unifiedOrder) {
        return "/shop/orders";
    }

    public List<ShopOrder> findByStatusAndTime(Long ts, long end, OrderStatusEnum... status) {
        List<Integer> ss = Arrays.stream(status).map(s -> s.getCode()).collect(Collectors.toList());
        List<ShopOrder> ret = shopOrderDao.findAllByStatusInAndCreatedBetween(ss, ts, end);
        return empty(ret).stream().peek(s -> s.setProduct(productService.findById(s.getProductId()))).collect(
            Collectors.toList());
    }
}
