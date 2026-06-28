package com.Client.netty.serviceCenter.ZkWatcher;
import com.Client.cache.ServiceCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

@Slf4j
public class watchZK {
    private CuratorFramework client;
    private ServiceCache serviceCache;

    public watchZK(CuratorFramework client, ServiceCache serviceCache) {
        this.client = client;
        this.serviceCache = serviceCache;
    }


    public void watchToUpdate(String path){
        CuratorCache curatorCache = CuratorCache.build(client, "/");
        curatorCache.listenable().addListener(
                new CuratorCacheListener(){
                    //Type:参数的类型
                    //ChildData:节点更新前的数据和状态
                    //ChildData1:节点更新后的数据和状态
                    @Override
                    public void event(Type type, ChildData childData, ChildData childData1) {
                        switch(type){
                            case NODE_CREATED:
                                String[]paths=parse(childData1);
                                if(paths.length<=2) {
                                    break;
                                }else{
                                    String serviceName=paths[1];
                                    String address=paths[2];
                                    serviceCache.addServiceToCache(serviceName,address);
                                }
                                break;
                            case NODE_CHANGED: //更新
                                String[]oldPaths=parse(childData);
                                String[]newPaths=parse(childData1);
                                serviceCache.replaceServiceAddress(oldPaths[1],oldPaths[2],newPaths[2]);
                                log.info("修改后节点为{}",new String(childData.getData()));
                                break;
                                case NODE_DELETED:
                                    String[]d_paths=parse(childData);
                                    if(d_paths.length<=2){
                                        break;
                                    }else{
                                        String serviceName=d_paths[1];
                                        String address=d_paths[2];
                                        serviceCache.delete(serviceName,address);
                                    }
                                    break;
                                    default:
                                        break;
                        }
                    }
                }
        );
        curatorCache.start();
    }

    private String[] parse(ChildData childDate){
        String path=childDate.getPath();
        return path.split("/");
    }
}
