package site.binghai.biz.tasks;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class PostSendBase {
    private String content;
    private String postUrl;

    public String postSend() {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(this.postUrl);
        httpPost.addHeader("Content-Type", "text/xml");
        StringEntity postEntity = new StringEntity(this.content, "UTF-8");
        httpPost.setEntity(postEntity);
        CloseableHttpResponse response2 = null;
        try {
            response2 = httpclient.execute(httpPost);
        } catch (ClientProtocolException var19) {
            var19.printStackTrace();
        } catch (IOException var20) {
            var20.printStackTrace();
        }
        String msg = null;
        try {
            HttpEntity entity2 = response2.getEntity();
            msg = EntityUtils.toString(entity2, "UTF-8");
            EntityUtils.consume(entity2);

            return msg;
        } catch (IOException var17) {
            var17.printStackTrace();
        } finally {
            try {
                response2.close();
            } catch (IOException var16) {
                var16.printStackTrace();
            }
        }
        return msg;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostUrl() {
        return this.postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }
}
