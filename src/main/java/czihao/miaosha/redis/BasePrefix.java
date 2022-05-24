package czihao.miaosha.redis;


/*
 * 设计的思考
 * 之前我们已经做好了redis的service，但是为了防止key冲突，这里做一个前缀空间的引入
 * 主要思想是不同业务模块使用不同的前缀空间
 * 因此我们可以使用 接口+抽象类 来描述
 * 不同的业务模块去生产不同的前缀就行。
 * 这里的设计其实有很多 方案的。
 * 我们可以使用很多的设计模式进行设计。
 * 比如 模板方法模式---用抽象类作为模板，下一层次的子类只要 稍微配置就行
 * 大家可以实践下设计模式的使用，其他的模式也可以使用的。当然这里不是滥用设计模式，只是为了体验下模式的思想
 * 采用模板方法模式进行设计前缀空间
 * 原文链接：https://blog.csdn.net/fk002008/article/details/80031113
 * */
public abstract class BasePrefix implements KeyPrefix {

    private final int expireSeconds;
    private final String prefix;

    public BasePrefix(String prefix) {
        this(0, prefix);    //0代表永不过期
    }

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public int expireSeconds() {
        return expireSeconds;   //默认0代表永不过期
    }

    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }

}
