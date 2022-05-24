package czihao.miaosha.controller;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import czihao.miaosha.redis.KeyPrefix;
import czihao.miaosha.redis.RedisService;


@Controller
public class BaseController {

    //通过@Value将application.properties配置文件中的pageCache.enbale值动态注入到pageCacheEnable中
    @Value("#{'${pageCache.enbale}'}")
    private boolean pageCacheEnable;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    RedisService redisService;


    /*
     * 页面优化
     * ”秒杀商品列表“页面（goods_list.html）的缓存呢，其实只是登录的秒杀用户不一样而已，其他的部分对大部分用户来说是一样的。
     * render()方法只被用过一次：GoodsController.java->list()方法->/goods/to_list
     * */
    public String render(HttpServletRequest request, HttpServletResponse response, Model model, String templateName, KeyPrefix prefix, String key) {

        if (!pageCacheEnable) {//如果不允许页面缓存，则直接返回逻辑视图名
            return templateName;//goods_list
        }
        //获取页面缓存
        String html = redisService.get(prefix, key, String.class);
        if (!StringUtils.isEmpty(html)) {
            out(response, html);
            return null;
        }
        //没有页面缓存，则手动渲染goods_list.html模板
        WebContext ctx = new WebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process(templateName, ctx);
        //进行页面缓存，即将秒杀商品列表页面（goods_list.html）缓存到redis中
        if (!StringUtils.isEmpty(html)) {
            redisService.set(prefix, key, html);
        }
        out(response, html);
        return null;
    }

    /*
     * 将html写入到响应response中
     * */
    public static void out(HttpServletResponse response, String html) {
        //设置响应的内容类型以及编码方式
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        //将html写入到响应response中
        try {
            OutputStream out = response.getOutputStream();
            //使用指定字符集将该字符串编码为一个字节序列，并将结果存储到一个新的字节数组中。
            out.write(html.getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
