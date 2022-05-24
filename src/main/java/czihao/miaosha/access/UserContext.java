package czihao.miaosha.access;

import czihao.miaosha.domain.MiaoshaUser;

public class UserContext {

    //居然用到了ThreadLocal耶（可以解决多线程访问共享资源时的并发访问控制问题）
    private static final ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal();

    public static void setUser(MiaoshaUser user) {
        userHolder.set(user);
    }

    public static MiaoshaUser getUser() {
        return userHolder.get();
    }

}
