package czihao.miaosha.redis;

public interface KeyPrefix {
    /**
     * 有效时间
     * @return
     */
    int expireSeconds();

    /**
     * 得到一个key的前缀
     * @return
     */
    String getPrefix();

}
