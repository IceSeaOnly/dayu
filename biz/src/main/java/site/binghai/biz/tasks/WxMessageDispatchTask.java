package site.binghai.biz.tasks;

import com.aliyun.mns.client.CloudAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.binghai.biz.service.WxTplMessageService;
import site.binghai.biz.service.jdy.JdyLogService;
import site.binghai.biz.service.jdy.WxTplMsgLogService;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.utils.BaseBean;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.Message;
import com.aliyuncs.exceptions.ClientException;
import site.binghai.lib.utils.IoUtils;
import site.binghai.lib.utils.TimeTools;

@Component
public class WxMessageDispatchTask extends BaseBean {

    private static String _product = "Push";
    private static String _domain = "dysmsapi.aliyuncs.com";

    @Autowired
    private IceConfig iceConfig;
    @Autowired
    private JdyLogService jdyLogService;
    @Autowired
    private WxTplMessageService wxTplMessageService;

    @Scheduled(cron = "0/5 * * * * ?")
    public void work() {
        //logger.info("(..)");
        msgDealer();
    }

    public void msgDealer() {
        CloudAccount account = new CloudAccount(iceConfig.getAliyunMQAccessKeyId(),
            iceConfig.getAliyunMQAccessKeySecret(),
            iceConfig.getAliyunMQAccountEndpoint());
        MNSClient client = account.getMNSClient();
        try {
            CloudQueue queue = client.getQueueRef("jdy-bone");
            CloudQueue logQueue = client.getQueueRef("jdy-boneLog");
            while (true) {
                Message popMsg = queue.popMessage();
                if (popMsg == null) { break; }
                DealMsg(popMsg.getMessageBodyAsString());
                queue.deleteMessage(popMsg.getReceiptHandle());
            }
            while (true) {
                Message popMsg = logQueue.popMessage();
                if (popMsg == null) { break; }
                String content = popMsg.getMessageBodyAsString();
                writeLog(content);
                logQueue.deleteMessage(popMsg.getReceiptHandle());
            }
        } catch (com.aliyun.mns.common.ClientException var8) {
            writeLog(
                "Something wrong with the network connection between client and MNS service.Please check your network"
                    + " and DNS availablity.");
            var8.printStackTrace();
        } catch (ServiceException var9) {
            var9.printStackTrace();
            if (var9.getErrorCode() != null) {
                if (var9.getErrorCode().equals("QueueNotExist")) {
                    writeLog("Queue is not exist.Please create before use");
                } else if (var9.getErrorCode().equals("TimeExpired")) {
                    writeLog("The request is time expired. Please check your local machine timeclock");
                }
            }
        } catch (Exception var10) {
            writeLog("Unknown exception happened!");
            var10.printStackTrace();
        }
        client.close();
    }

    private void writeLog(String content) {
        jdyLogService.save(content);
        //String fileName = "/data/wwwroot/notify/log_" + TimeTools.format2yyyy_MM_dd(Long.valueOf(System.currentTimeMillis())) + ".txt";
        //IoUtils.WriteCH(fileName, TimeTools.format(Long.valueOf(System.currentTimeMillis())) + " - " + content);
    }

    private void DealMsg(String messageBodyAsString) throws ClientException {
        JSONObject json = JSONObject.parseObject(messageBodyAsString);
        if (json.getString("type").equals("wxnotice")) {
            JSONArray arr = json.getJSONArray("datas");
            for (int i = 0; i < arr.size(); i++) {
                String ret = wxTplMessageService.send(arr.getJSONObject(i));
            }
        } else if (json.getString("type").equals("sms")) {
            String phone = json.getString("phone");
            String param = json.getJSONObject("param").toJSONString();
            String str1 = json.getString("tpl");
        }
    }
}
