package com.common.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.common.serializer.mySerializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpiLoader {
    private static Map<String, Map<String, Class<? extends Serializer>>> loadedSpiMap = new ConcurrentHashMap<>();
    //缓存实例
    private static final Map<String, Object> instanceCache = new ConcurrentHashMap<>();
    private static final String SPI_CONFIG_DIR = "META-INF/serializer/";

    public static void loadSpi(Class<?> serviceInterface) {
        String interfaceName = serviceInterface.getName();
        if (loadedSpiMap.containsKey(interfaceName)) {
            return;
        }
        List<URL> list = ResourceUtil.getResources(SPI_CONFIG_DIR + interfaceName);
        if (list.isEmpty())
            return;
        Map<String, Class<? extends Serializer>> keyClassMap = new ConcurrentHashMap<>();
        for (URL url : list) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(url.openStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && !line.startsWith("#")) {
                        String[] parts = line.split("=");
                        if (parts.length == 2) {
                            String key = parts[0].trim();         // 如 "kryo"
                            String className = parts[1].trim();   // 如 "common.serializer.myserializer.KryoSerializer"

                            // ★ 反射加载类，并校验是否实现了目标接口
                            Class<?> implClass = Class.forName(className);
                            if (serviceInterface.isAssignableFrom(implClass)) {
                                keyClassMap.put(key,
                                        (Class<? extends Serializer>) implClass);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.info("Failed to load SPI configuration: {}", url, e);
            }
        }
        loadedSpiMap.put(interfaceName, keyClassMap);
    }

    public static <T> T getInstance(Class<T> serviceInterface, String key) {
        String interfaceName = serviceInterface.getName();
        if (!loadedSpiMap.containsKey(interfaceName)) {
            loadSpi(serviceInterface);
        }
        Map<String, Class<? extends Serializer>> keyClassMap = loadedSpiMap.get(interfaceName);
        if (keyClassMap == null || !keyClassMap.containsKey(key)) {
            throw new RuntimeException("No implementation found for key: " + key);
        }
        Class<? extends Serializer> implClass = keyClassMap.get(key);
        if (implClass == null) {
            throw new RuntimeException("No implementation found for key: " + key);
        }
        String implClassName = implClass.getName();
        if (!instanceCache.containsKey(implClassName)) {
            try{
                instanceCache.put(implClassName, implClass.newInstance());
            }catch(Exception e){
                throw new RuntimeException("Failed to create instance for key: " + key, e);
            }

        }
        return (T) instanceCache.get(implClassName);

    }
}
