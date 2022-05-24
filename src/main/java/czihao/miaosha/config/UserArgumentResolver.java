package czihao.miaosha.config;

import czihao.miaosha.access.UserContext;
import czihao.miaosha.domain.MiaoshaUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import czihao.miaosha.service.MiaoshaUserService;

/*
 * HandlerMethodArgumentResolver的应用场景：获取当前登陆人（当前用户）的基本信息.
 * 只有俩个方法，supportsParameter返回true表示支持解析，之后调用resolveArgument将结果作为参数传入.
 * */
/*
 * 面试可能会问的问题
 * 在Controller层获取当前登陆人的基本信息（如id、名字…）是一个必须的、频繁的功能需求，
 * 这个时候如果团队内没有提供相关封装好的方法来调用，你便可看到大量的、重复的获取当前用户的代码，
 * 这就是各位经常吐槽的垃圾代码~
 * https://cloud.tencent.com/developer/article/1497397
 * */
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    MiaoshaUserService userService;

    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz == MiaoshaUser.class;
    }

    /*
     * loginUser不需要前台显示传参，
     * 通常前端传sessionId值或者token信息，以此在MyArgumentResolver 处理器里面进行获取并设置.
     * 在请求方法里面就可以直接使用loginUser
     * */
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return UserContext.getUser();
    }

}
