package com.ares.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class SnowFlake {
    //下面两个每个5位，加起来就是10位的工作机器id

    //-----39  bit  system time-(15 years) --------2  bit serverType-- 11 bit areaId(2048 )----11 bit(2048)： sequence num
    private static long workerId;    //areaId
    private static long serverType;   //serverTYpe
    //12位的序列号
    private static long sequence;

    public static void init(long areaId, int sType) {
        // sanity check for workerId
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (serverType > maxServerType || serverType < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxServerType));
        }
        log.info("worker starting. timestamp left shift {}, serverType id bits{}, worker id bits{}, sequence bits {} workerid {}",
                timestampLeftShift, serverType, workerIdBits, sequenceBits, workerId);

        workerId = areaId;
        serverType = sType;
        sequence = 1;
    }


    //初始时间戳
    private static long twepoch = 1706059602230L;

    //长度为5位
    private static long workerIdBits = 11L;
    private static long serverTypeBits = 2L;
    //最大值
    private static final long maxWorkerId = ~(-1L << workerIdBits);
    private static final long maxServerType = ~(-1L << serverTypeBits);
    //序列号id长度
    private static final long sequenceBits = 11L;
    //序列号最大值
    private static final long sequenceMask = ~(-1L << sequenceBits);

    //工作id需要左移的位数，12位
    private static final long workerIdShift = sequenceBits;
    //数据id需要左移位数 12+5=17位
    private static final long serverTypeShift = sequenceBits + workerIdBits;
    //时间戳需要左移位数 12+5+5=22位
    private static final long timestampLeftShift = sequenceBits + workerIdBits + serverTypeBits;

    //上次时间戳，初始值为负数
    private static long lastTimestamp = -1L;

    public static long getWorkerId() {
        return workerId;
    }

    public static long getTimestamp() {
        return System.currentTimeMillis();
    }

    //下一个ID生成算法
    public synchronized static long nextId() {
        long timestamp = timeGen();

        //获取当前时间戳如果小于上次时间戳，则表示时间戳获取出现异常
        if (timestamp < lastTimestamp) {
            System.err.printf("clock is moving backwards.  Rejecting requests until %d.", lastTimestamp);
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp));
        }

        //获取当前时间戳如果等于上次时间戳（同一毫秒内），则在序列号加一；否则序列号赋值为0，从0开始。
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        //将上次时间戳值刷新
        lastTimestamp = timestamp;

        /**
         * 返回结果：
         * (timestamp - twepoch) << timestampLeftShift) 表示将时间戳减去初始时间戳，再左移相应位数
         * (datacenterId << datacenterIdShift) 表示将数据id左移相应位数
         * (workerId << workerIdShift) 表示将工作id左移相应位数
         * | 是按位或运算符，例如：x | y，只有当x，y都为0的时候结果才为0，其它情况结果都为1。
         * 因为个部分只有相应位上的值有意义，其它位上都是0，所以将各部分的值进行 | 运算就能得到最终拼接好的id
         */
        return ((timestamp - twepoch) << timestampLeftShift) |
                (serverType << serverTypeShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    //获取时间戳，并与上次时间戳比较
    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    //获取系统时间戳
    private static long timeGen() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args){
        SnowFlake.init(2047,3);
        Set<Long>  sets = new HashSet<>(10000);
        for(int i= 0 ; i < 3000000; i++){
            long id = SnowFlake.nextId();
            System.out.println("------- id = " +id);
            if(sets.contains(id)){
                throw  new RuntimeException("FUCK");
            }
            sets.add(id);
        }
    }

}
