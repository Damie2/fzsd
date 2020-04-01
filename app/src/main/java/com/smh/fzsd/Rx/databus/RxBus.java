package com.smh.fzsd.Rx.databus;


import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * 数据总线
 */
public class RxBus {
    private Set<Object> subscribers;

    /**
     * 注册
     */
    public synchronized void register(Object subscriber) {
        if (!subscribers.contains(subscriber))
            subscribers.add(subscriber);
    }

    /**
     * 取消注册
     */
    public synchronized void unRegister(Object subscriber) {
        subscribers.remove(subscriber);
    }


    private static volatile RxBus instance;

    private RxBus() {
        //读写分离的集合
        subscribers = new CopyOnWriteArraySet<>();
    }

    public static synchronized RxBus getInstance() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }

    public void send(Object data, int tag) {
        for (Object subscriber : subscribers) {
            //扫描注解,将数据发送到注册的对象标记方法的位置
            //subscriber表示层
            callMethodByAnnotion(subscriber, data, tag);
        }
    }

    /**
     * @param target
     * @param data
     */
    private void callMethodByAnnotion(Object target, Object data, int tag) {

        //1.得到presenter中写的所有的方法
        Method[] methodArray = target.getClass().getDeclaredMethods();
        for (int i = 0; i < methodArray.length; i++) {
            try {
                RegisterRxBus registerRxBus = methodArray[i].getAnnotation(RegisterRxBus.class);
                if (registerRxBus != null) {

                    //如果哪个方法上用了我们写的注解
                    //把数据传上去,再执行这个方法
                    Class paramType = methodArray[i].getParameterTypes()[0];
                    if ((data.getClass().getName().equals(paramType.getName()) || paramType.isAssignableFrom(data.getClass())) && registerRxBus.value() == tag) {
                        //执行
                        methodArray[i].setAccessible(true);
                        methodArray[i].invoke(target, new Object[]{data, tag});
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}









