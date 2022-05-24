package czihao.miaosha.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import czihao.miaosha.redis.RedisService;
import czihao.miaosha.result.Result;
import czihao.miaosha.service.MiaoshaUserService;
import czihao.miaosha.vo.LoginVo;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    /*
     * 在地址栏中键入“http://localhost:8080/login/to_login”并enter时，
     * toLogin()方法会处理该请求，返回一个login.html页面
     * */
    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        log.info(loginVo.toString());
        //登录
        //把用户信息放在第三方缓存当中，业界常用做法.
        /*
         * session并没有存到服务器里面，而是存到单独的一个缓存里面，用redis单独管理我们的session，这就是所谓的分布式session
         * */
        String token = userService.login(response, loginVo);
        return Result.success(token);
    }
}
