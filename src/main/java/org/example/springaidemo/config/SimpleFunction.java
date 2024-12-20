package org.example.springaidemo.config;

import org.example.springaidemo.impl.SimpleControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

/**
 * SimpleFunction 是一个 Spring 配置类，定义了应用中使用的函数 Bean。
 * 主要用于暴露基于 Lambda 表达式的业务逻辑函数。
 */
@Configuration
public class SimpleFunction {

    // 引用业务逻辑实现类 SimpleControllerImpl
    private final SimpleControllerImpl simpleImpl;

    /**
     * 构造方法，注入 SimpleControllerImpl 实例。
     *
     * @param simpleImpl SimpleControllerImpl 的实例
     */
    @Autowired
    public SimpleFunction(SimpleControllerImpl simpleImpl) {
        this.simpleImpl = simpleImpl;
    }

    /**
     * 内部静态记录类，用于封装输入参数。
     * 在这里，PriceAll 用于传递商品的数量。
     *
     * @param count 商品的数量
     */
    public record PriceAll(int count){}

    /**
     * 定义一个 Function 类型的 Bean，用于计算总价格。
     *
     * @return 一个函数，接收 PriceAll 类型的输入，返回计算结果（总价格）的字符串表示
     */
    @Bean
    @Description("获取总价格")
    public Function<PriceAll, String> getPrice(){
        return priceCount -> {
            // 从输入中获取商品数量，并调用业务逻辑计算总价格
            Double pricedAll = simpleImpl.priceAll(priceCount.count);
            // 返回总价格的字符串表示
            return pricedAll.toString();
        };

    }
}
