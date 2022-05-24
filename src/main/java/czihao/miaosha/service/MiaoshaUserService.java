package czihao.miaosha.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import czihao.miaosha.dao.MiaoshaUserDao;
import czihao.miaosha.domain.MiaoshaUser;
import czihao.miaosha.util.MD5Util;
import czihao.miaosha.util.UUIDUtil;
import czihao.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import czihao.miaosha.exception.GlobalException;
import czihao.miaosha.redis.MiaoshaUserKey;
import czihao.miaosha.redis.RedisService;
import czihao.miaosha.result.CodeMsg;

@Service
public class MiaoshaUserService {


    public static final String COOKI_NAME_TOKEN = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    public MiaoshaUser getById(long id) {
        /*
         * 取缓存
         * 根据唯一标识user的id从redis中取user缓存
         * */
        MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, "" + id, MiaoshaUser.class);
        if (user != null) {
            return user;
        }
        //取数据库
        user = miaoshaUserDao.getById(id);
        /*
         * 进行缓存
         * 将唯一标识user的用户id和user本身缓存到redis中
         * */
        if (user != null) {
            redisService.set(MiaoshaUserKey.getById, "" + id, user);
        }
        return user;
    }

    /*
     * 更新缓存时，redis缓存和后端mysql数据库不一致问题解决思路
     * http://blog.csdn.net/tTU1EvLDeLFq5btqiK/article/details/78693323
     * */
    public boolean updatePassword(String token, long id, String formPass) {
        //取user
        MiaoshaUser user = getById(id);
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //先更新数据库
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
        miaoshaUserDao.update(toBeUpdate);
        /*
         * 再更新redis缓存
         * */
        redisService.delete(MiaoshaUserKey.getById, "" + id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoshaUserKey.token, token, user);
        return true;
    }

    /*
     * getByToken()方法只被用过一次：AccessInterceptor.java->getUser()方法
     * */
    public MiaoshaUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        /*
         * 取缓存
         * 根据唯一标识user的token从redis中取user缓存
         * 这就是对象缓存
         * */
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        //延长有效期
        if (user != null) {
            addCookie(response, token, user);
        }
        return user;
    }

    /*
     * login()方法只被用过一次：LoginController.java->doLogin()方法->http://localhost:8080/login/do_login
     * */
    public String login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.uuid();   //分布式session  ，用token标识用户user
        addCookie(response, token, user);
        return token;
    }

    private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
        /*
         * 进行缓存
         * 将唯一标识user的token和user本身缓存到redis中
         * */
        redisService.set(MiaoshaUserKey.token, token, user);   //key->token  ,value->user
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());//设置cookie和token有效期一致
        cookie.setPath("/");//网站的根目录
        response.addCookie(cookie);
    }

}
