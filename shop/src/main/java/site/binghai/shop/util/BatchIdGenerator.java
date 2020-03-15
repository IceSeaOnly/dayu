package site.binghai.shop.util;

/**
 * @author huaishuo
 * @date 2020/3/15 下午12:38
 **/
public class BatchIdGenerator {
    private static Long start = System.currentTimeMillis();

    public static Long nextBatchId(Long userId) {
        if (System.currentTimeMillis() - start > 5000) {
            start = System.currentTimeMillis();
        }
        return Long.parseLong(String.valueOf(++start) + userId);
    }
}
