package HttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class GetWithCookies {

    private ResourceBundle bundle;
    private String url;
    private String uriGetCookies;
    private String uriGetWithCookies;
    private String responseInfo;
    private CookieStore cookieStore;

    @BeforeTest
    public void testInit() {
        this.bundle = ResourceBundle.getBundle("test.ResBundle", Locale.CHINA);
        this.url = bundle.getString("url");
        this.uriGetCookies = bundle.getString("uri_getCookies");
        this.uriGetWithCookies = bundle.getString("uri_getWithCookies");
    }

    @Test
    public void getCookies() throws IOException {
        String url_getCookies = this.url + this.uriGetCookies; // 拼接要访问的url
        HttpGet httpGet = new HttpGet(url_getCookies); // 参数：要访问的url地址

        DefaultHttpClient httpClient = new DefaultHttpClient();
        // httpClient用以执行httpGet，并且返回httpResponse
        HttpResponse httpResponse = httpClient.execute(httpGet);
        // 获取response返回值
        this.responseInfo = EntityUtils.toString(httpResponse.getEntity(), "utf-8");

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
    public void getWithCookies() throws IOException {
        String url_getWithCookies = this.url + this.uriGetWithCookies;
        HttpGet httpGet = new HttpGet(url_getWithCookies);

        DefaultHttpClient httpClient = new DefaultHttpClient();

        // 设置cookies
        httpClient.setCookieStore(this.cookieStore);
        // 获取response里的status
        HttpResponse httpResponse = httpClient.execute(httpGet);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        System.out.println("statusCode = " + statusCode);
    }

}
