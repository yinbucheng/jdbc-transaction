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
                  tranactional tranactional =  method.getAnnotation(tranactional.class);
                  if(tranactional!=null){
                      try{
                         TransactionManager.preparedTran();
                         Object resut = method.invoke(service,args);
                         TransactionManager.commit();
                         return resut;
                      }catch (Exception e){
                        TransactionManager.rollBack();
                        throw new RuntimeException(e);
                      }finally {
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
