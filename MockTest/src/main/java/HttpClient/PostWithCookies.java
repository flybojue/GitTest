package HttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class PostWithCookies {

    private ResourceBundle bundle;
    private String url;
    private String uriPostWithCookies;
    private String uriGetCookies;
    private String responseInfo;
    private CookieStore cookieStore;

    @BeforeTest
    public void resInit() {
        this.bundle = ResourceBundle.getBundle("test.ResBundle", Locale.CHINA);
        this.url = bundle.getString("url");
        this.uriGetCookies = bundle.getString("uri_getCookies");
        this.uriPostWithCookies = bundle.getString("uri_postWithCookies");
    }

    @Test
    public void getCookies() throws IOException {
        String url_getCookies = this.url + this.uriGetCookies; // 拼接要访问的url
        HttpGet httpGet = new HttpGet(url_getCookies); // 参数：要访问的url地址

        DefaultHttpClient httpClient = new DefaultHttpClient();
        // httpClient用以执行httpGet
        httpClient.execute(httpGet);

        // 获取Cookies信息
        this.cookieStore = httpClient.getCookieStore();
        List<Cookie> cookieList = this.cookieStore.getCookies();
        // 循环获取cookieList里所有的key和value
        for (Cookie cookie : cookieList) {
            String key = cookie.getName();
            String value = cookie.getValue();
            System.out.println("cookie key=" + key + "; cookie value= " + value);
        }
    }

    @Test(dependsOnMethods = {"getCookies"})
    public void postWithCookies() throws IOException {
        String url_postWithCookies = this.url + this.uriPostWithCookies;

        // 声明一个httpClient方法
        DefaultHttpClient httpClient = new DefaultHttpClient();
        // 设置cookies
        httpClient.setCookieStore(this.cookieStore);

        // 声明一个post的HttpPost方法，用以执行url
        HttpPost httpPost = new HttpPost(url_postWithCookies);

        // 设置请求头信息 设置Header，若有多个参数需多次setHeader
        httpPost.setHeader("content-type", "application/json");
        // 添加请求参数，若有多个参数就需要多个put
        JSONObject param = new JSONObject();
        param.put("name", "huhansan");
        param.put("age", "18");

        // 将参数整合到httpPost中
        StringEntity stringEntity = new StringEntity(param.toString(), "utf-8");
        httpPost.setEntity(stringEntity);

        // 参数全部set完后，最后再执行；并获取response
        HttpResponse httpResponse = httpClient.execute(httpPost);
        // 将返回的response转成String
        this.responseInfo = EntityUtils.toString(httpResponse.getEntity(), "utf-8");

        // 将返回的String response转成json
        JSONObject resultJson = new JSONObject(this.responseInfo);
        System.out.println("获取到的响应结果：" + resultJson);
        System.out.println(resultJson.getJSONObject("msg").get("content"));

        // 分别获取返回值，并根据需要进行判断
        String result = (String) resultJson.get("result");
        Assert.assertEquals("success", result);

    }

}
