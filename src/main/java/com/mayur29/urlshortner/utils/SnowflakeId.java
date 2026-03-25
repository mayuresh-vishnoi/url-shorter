package com.mayur29.urlshortner.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SnowflakeId {

    private static final Long epoch = 1774394560954L;
    private static final Long workerId = 1L;
    private static final Long dataCenterId = 1L;
    private Long lastTimeStamp = -1L;
    private Long sequence = 0L;

    public synchronized Long generateId(){
        log.info("inside generateId");
        Long currentTimeStamp = System.currentTimeMillis();
        if(currentTimeStamp<lastTimeStamp){
            throw new RuntimeException("Clock is running backwards");
        }

        if(currentTimeStamp == lastTimeStamp){
            sequence++;
            long seq = sequence&4026;
            if (seq == 0){
                currentTimeStamp = waitForNextTimeStamp(lastTimeStamp);
            }else {
                sequence = 0L;
            }

            lastTimeStamp = currentTimeStamp;
        }

        return (currentTimeStamp-epoch)<<22 |
                dataCenterId << 17 |
                workerId << 12 | sequence;
    }

    private Long waitForNextTimeStamp(Long lastTimeStamp) {
        long currTs = System.currentTimeMillis();

        if(currTs<=lastTimeStamp){
            try {
                log.info("waitForNextTimeStamp");
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();;
                throw new RuntimeException(e);
            }

            lastTimeStamp = System.currentTimeMillis();
        }

        return lastTimeStamp;
    }
}
