package czihao.miaosha.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import czihao.miaosha.domain.MiaoshaUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import czihao.miaosha.redis.GoodsKey;
import czihao.miaosha.redis.RedisService;
import czihao.miaosha.result.Result;
import czihao.miaosha.service.GoodsService;
import czihao.miaosha.service.MiaoshaUserService;
import czihao.miaosha.vo.GoodsDetailVo;
import czihao.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/goods")
public class GoodsController extends BaseController {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;

    /**
     * QPS:1267 load:15 mysql
     * 5000 * 10
     * QPS:2884, load:5
     */
    /*
     * 返回字符串（官方推荐此种方式）   --返回视图路径，数据通过形参 Model model 或者 ModelMap model
     * controller方法返回字符串可以指定逻辑视图名，通过视图解析器解析为物理视图地址。
     * https://www.cnblogs.com/zhaojiankai/p/8184207.html
     * 如果返回了null（相当于返回类型为void），且往响应response中写了内容（比如html），即通过response指定了响应结果，则使用response直接显示.
     * */
    @RequestMapping(value = "/to_list")
    public String list(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user) {
        model.addAttribute("user", user);
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        /*
         * 怎么渲染模板的？
         * 就是把数据放到model里面，最终呢搞到goods_list.html页面上.
         * */
        return render(request, response, model, "goods_list", GoodsKey.getGoodsList, "");
    }

    /*
     * 处理来自”秒杀商品详情页面（goods_detail.html）“的请求
     * 即 http://localhost:8080/goods_detail.htm?goodsId=1
     * 或 http://localhost:8080/goods/to_detail2/1
     * url缓存
     * */
    @RequestMapping(value = "/to_detail2/{goodsId}", produces = "text/html")
    @ResponseBody
    public String detail2(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user,
                          @PathVariable("goodsId") long goodsId) {
        model.addAttribute("user", user);

        /*
         * 取”秒杀商品详情页面（goods_detail.html）“缓存
         * url缓存
         * */
        String html = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        //redis没有缓存该秒杀商品详情的信息，则从mysql后端数据库中去取
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if (now < startAt) {//秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int) ((startAt - now) / 1000);
        } else if (now > endAt) {//秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        WebContext ctx = new WebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
        /*
         * 进行url页面缓存，即将商品详情页面（goods_detail.html）缓存到redis中
         * */
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsDetail, "" + goodsId, html);
        }
        return html;
    }

    /*
     * 处理来自”秒杀商品详情页面（goods_detail.html）“的请求
     * 即 http://localhost:8080/goods/detail/2
     * 页面静态化，浏览器（本地）缓存
     * */
    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user,
                                        @PathVariable("goodsId") long goodsId) {
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if (now < startAt) {//秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int) ((startAt - now) / 1000);
        } else if (now > endAt) {//秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setRemainSeconds(remainSeconds);
        vo.setMiaoshaStatus(miaoshaStatus);
        return Result.success(vo);
    }

}
