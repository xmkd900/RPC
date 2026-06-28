package com.Server.ratelimit.Impl;

import com.Server.ratelimit.RateLimit;

public class TokenBucketRateLimitImpl implements RateLimit {

    //令牌产生速度 单位为ms/token
    private final  int RATE;
    //桶的容量
    private final int CAPACITY;
    //当前桶中令牌数量
    private  int curCapacity;
    //上次令牌产生时间
    private long timeStamp=System.currentTimeMillis();

    public TokenBucketRateLimitImpl(int rate, int Capacity) {
        RATE = rate;
        CAPACITY = Capacity;
        curCapacity = Capacity;  // 初始时桶是满的
    }

    @Override
    public synchronized boolean getToken() {
        if(curCapacity>0){
            curCapacity--;
            return  true;
        }
        long nowTime=System.currentTimeMillis();
        long timeGap=nowTime-timeStamp;
        if(timeGap>=RATE){
            if(timeGap/RATE>=2){
                curCapacity+=(int)(timeGap/RATE)-1;
            }
            if(curCapacity>CAPACITY){
                curCapacity=CAPACITY;
            }
            timeStamp=nowTime;
            return true;
        }
        return false;
    }
}
