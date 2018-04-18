package cn.intellif.base;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class BasicFactory {
    private static BasicFactory basicFactory = new BasicFactory();
    private static Map<String,String> cache = new HashMap<>();
    static {
        Properties properties = new Properties();
        try{
            properties.load(BasicFactory.class.getClassLoader().getResourceAsStream("bean.properties"));
            Enumeration<?> keys = properties.keys();
            while(keys.hasMoreElements()){
                String key = (String) keys.nextElement();
                String value = (String) properties.get(key);
                cache.put(key,value);
            }

        }catch (Exception e){
            throw  new RuntimeException(e);
        }
    }

    public static BasicFactory getInstance(){
        return basicFactory;
    }

    public  <T> T getDao(Class<T> interfaceClazz){
        String className = cache.get(interfaceClazz.getSimpleName());
        try{
            Class<T> clazz = (Class<T>) Class.forName(className);
            return clazz.newInstance();
        }catch (Exception e){
            throw  new RuntimeException(e);
        }
    }

    public  <T> T getService(Class<T> interfaceClazz){
        String className = cache.get(interfaceClazz.getSimpleName());
        try{
            Class<T> clazz = (Class<T>) Class.forName(className);
            final T service = clazz.newInstance();
           T proxyService = (T) Proxy.newProxyInstance(BasicFactory.class.getClassLoader(), new Class[]{interfaceClazz}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                  //这里是获取接口上面方法是否存在制定注解
                  Tranactional tranactional =  method.getAnnotation(Tranactional.class);
                  //获取实现类上面的方法是否存在指定注解
                  if(tranactional==null){
                     Method m =  service.getClass().getMethod(method.getName(),method.getParameterTypes());
                     tranactional = m.getAnnotation(Tranactional.class);
                  }
                  if(tranactional!=null){
                      try{
                          System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>开始事务。。。。。。");
                         TransactionManager.preparedTran();
                         Object resut = method.invoke(service,args);
                         TransactionManager.commit();
                         return resut;
                      }catch (Exception e){
                        TransactionManager.rollBack();
                        throw new RuntimeException(e);
                      }finally {
                          System.out.println(">>>>>>>>>>>>>>>>>>>>>>>结束事务");
                          TransactionManager.releaseResource();;
                      }
                  }else{
                      return method.invoke(service,args);
                  }
                }
            });
           return proxyService;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
