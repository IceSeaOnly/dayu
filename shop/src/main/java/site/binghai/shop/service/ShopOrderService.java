package site.binghai.shop.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
import site.binghai.lib.utils.SchoolIdThreadLocal;
import site.binghai.shop.dao.ShopOrderDao;
import site.binghai.shop.entity.Product;
import site.binghai.shop.entity.ShopOrder;
import site.binghai.shop.entity.Tuan;
import site.binghai.shop.enums.TuanStatus;
import site.binghai.shop.kv.AutoAcceptOrderConfig;
import site.binghai.shop.pinter.CloudPrinter;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    @Autowired
    private AppTokenService appTokenService;
    @Autowired
    private KvService kvService;
    @Autowired
    private CloudPrinter cloudPrinter;

    private Map<Long, Boolean> printCache = new ConcurrentHashMap<>();

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
        if (shopOrder == null) {
            return null;
        }
        if (shopOrder.getPtOrder() != null && shopOrder.getPtOrder()) {
            if (CompareUtils.inAny(shopOrder.getTuanStatus(), TuanStatus.FULL, TuanStatus.FAIL)) {
                throw new RuntimeException("拼团订单请在拼团成功后取消或等待超时自动取消");
            } else {
                wxEventHandler.onTuanFail(shopOrder.getTuanId(), shopOrder.getTitle(), order.getShouldPay(),
                        order.getOpenId());
            }
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
        boolean tuanSuccess = false;
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
                } else if (tuan.getStatus() == TuanStatus.FULL) {
                    tuanSuccess = true;
                }
            }
        }
        shopOrder.setStatus(OrderStatusEnum.PAIED.getCode());
        shopOrder.setPaid(Boolean.TRUE);
        update(shopOrder);
        if (tuanSuccess) {
            markAllTuanSuccess(shopOrder.getTuanId());
        }
        printByBatchId(shopOrder.getBatchId());
    }

    private void printByBatchId(Long batchId) {
        if (printCache.get(batchId) != null) {
            return;
        }
        printCache.put(batchId, Boolean.TRUE);
        AutoAcceptOrderConfig config = kvService.get(AutoAcceptOrderConfig.class);
        if (null == config || !config.getEnable().equals("Y")) {
            return;
        }
        List<ShopOrder> orders = findByBatchId(batchId);
        if ("Y".equals(config.getMarkProcessing())) {
            orders.forEach(shopOrder -> {
                shopOrder.setStatus(OrderStatusEnum.PROCESSING.getCode());
                update(shopOrder);
            });
        }
        cloudPrinter.print(orders, batchId.toString(), config.getPrintPieces());
    }

    private void markAllTuanSuccess(Long tuanId) {
        List<ShopOrder> shopOrders = findByTuanId(tuanId);
        for (ShopOrder shopOrder : shopOrders) {
            if (shopOrder.getTuanStatus() == TuanStatus.INIT) {
                shopOrder.setTuanStatus(TuanStatus.FULL);
                update(shopOrder);
            }
            printByBatchId(shopOrder.getBatchId());
        }
    }

    public List<ShopOrder> findByTuanId(Long tuanId) {
        ShopOrder order = new ShopOrder();
        order.setTuanId(tuanId);
        return query(order);
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

    public long countPtByUserId(Long userId) {
        ShopOrder exp = new ShopOrder();
        exp.setUserId(userId);
        exp.setPtOrder(Boolean.TRUE);
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

    public Map<Long, List<ShopOrder>> findByStatusAndTime(Long ts, Long end, OrderStatusEnum... status) {
        List<Integer> ss = Arrays.stream(status).map(s -> s.getCode()).collect(Collectors.toList());
        List<ShopOrder> ret = shopOrderDao.findAllBySchoolIdAndStatusInAndCreatedBetween(
                SchoolIdThreadLocal.getSchoolId(), ss, ts, end);
        return empty(ret).stream().peek(s -> s.setProduct(productService.findById(s.getProductId()))).collect(
                Collectors.groupingBy(s -> s.getBatchId()));
    }

    public List<ShopOrder> findTuanByUserId(Long userId) {
        ShopOrder exp = new ShopOrder();
        exp.setUserId(userId);
        exp.setPtOrder(Boolean.TRUE);
        return sortQuery(exp, "id", true);
    }

    public Map<Long, List<ShopOrder>> findByStatusAndRider(OrderStatusEnum status, Long rider, Integer page) {
        List<ShopOrder> ret = null;
        if (status == null) {
            ret = shopOrderDao.findAllBySchoolIdAndBindRiderOrderByUpdatedDesc(SchoolIdThreadLocal.getSchoolId(), rider,
                    new PageRequest(page, 1000));
        } else {
            ret = shopOrderDao.findAllBySchoolIdAndStatusAndBindRiderOrderByIdDesc(SchoolIdThreadLocal.getSchoolId(),
                    status.getCode(), rider,
                    new PageRequest(page, 1000));
        }
        return ret.stream().collect(
                Collectors.groupingBy(s -> s.getBatchId()));
    }

    public Long countByRiderAndStatus(OrderStatusEnum status, Long id) {
        Long ret = shopOrderDao.countByRiderAndStatus(id, status.getCode(), SchoolIdThreadLocal.getSchoolId());
        return ret == null ? 0 : ret;
    }

    public List<ShopOrder> findByBatchId(Long batchId) {
        ShopOrder exp = new ShopOrder();
        exp.setBatchId(batchId);
        List<ShopOrder> ret = query(exp);
        return empty(ret).stream().peek(s -> s.setProduct(productService.findById(s.getProductId()))).collect(
                Collectors.toList());
    }

    public Map<Long, List<ShopOrder>> findByStatusAndTimeGroupingByBatchId(long start, long end,
                                                                           OrderStatusEnum... status) {
        List<ShopOrder> ret = null;
        List<Integer> ss = Arrays.stream(status).map(s -> s.getCode()).collect(Collectors.toList());
        if (status != null && status.length == 1 && status[0] == OrderStatusEnum.PAIED) {
            ret = shopOrderDao.findAllBySchoolIdAndStatusInAndCreatedBetween(
                    SchoolIdThreadLocal.getSchoolId(), ss, 0l, end);
        } else {
            ret = shopOrderDao.findAllBySchoolIdAndStatusInAndCreatedBetween(
                    SchoolIdThreadLocal.getSchoolId(), ss, start, end);
        }
        return ret.stream().peek(s -> {
            s.setProduct(productService.findById(s.getProductId()));
            if (s.getBindRider() != null) {
                s.setRider(appTokenService.findById(s.getBindRider()));
            }
        }).collect(
                Collectors.groupingBy(s -> s.getBatchId()));
    }

    public Long countByRiderAndStatusAndTime(OrderStatusEnum status, Long tokenId, Long[] time) {
        Long ret = shopOrderDao.countByRiderAndStatusAndTime(SchoolIdThreadLocal.getSchoolId(), time[0], time[1],
                tokenId, status.getCode());
        return ret == null ? 0 : ret;
    }

    public Long countByStateAndTime(Long begin, Long end, OrderStatusEnum... status) {
        List<Integer> ss = Arrays.stream(status).map(s -> s.getCode()).collect(Collectors.toList());
        Long ret = shopOrderDao.countByStatusAndTime(SchoolIdThreadLocal.getSchoolId(), begin, end, ss);
        return ret == null ? 0 : ret;
    }

    public Map<Long, List<ShopOrder>> searchBy(String search) {
        search = "%" + search + "%";
        List<ShopOrder> ret = shopOrderDao.findAllBySchoolIdAndBuyerNameLikeOrReceiverLikeOrReceiverPhoneLike(SchoolIdThreadLocal.getSchoolId(),
                search, search, search);
        return ret.stream().peek(s -> {
            s.setProduct(productService.findById(s.getProductId()));
            if (s.getBindRider() != null) {
                s.setRider(appTokenService.findById(s.getBindRider()));
            }
        }).collect(
                Collectors.groupingBy(s -> s.getBatchId()));
    }
}
