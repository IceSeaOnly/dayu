import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import site.binghai.lib.utils.HttpUtils;

import java.util.UUID;

/**
 * @author huaishuo
 * @date 2018/12/5 下午11:18
 **/
public class TestTicket {

    public static void main(String[] args) {

        for (int i = 0; i < 50; i++) {
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                            String url = "http://ticketgame.binghai.site/user/grabTicket?from=ghy&openId=" + UUID.randomUUID()
                            .toString();
                        JSONObject resp = HttpUtils.sendJSONGet(url, null);
                        System.out.println(resp.toJSONString());
                    }
                }
            }.start();
        }

    }
}
