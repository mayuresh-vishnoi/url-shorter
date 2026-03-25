package com.mayur29.urlshortner.service;

import com.mayur29.urlshortner.entities.UrlMapping;
import com.mayur29.urlshortner.repository.UrlRepository;
import com.mayur29.urlshortner.utils.Base62;
import com.mayur29.urlshortner.utils.SnowflakeId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UrlService {

    public static final String BIT_LY = "http://bit.ly/";
    private final UrlRepository urlRepository;
    private final SnowflakeId snowflakeId;
    private final RedisService redisService;

    public UrlService(UrlRepository urlRepository,SnowflakeId snowflakeId,RedisService redisService){
        this.urlRepository = urlRepository;
        this.snowflakeId = snowflakeId;
        this.redisService = redisService;
    }

    public String shorten(String url) {
        UrlMapping cacheUrl = redisService.get(url,UrlMapping.class);
        if(cacheUrl!=null){
            log.info("foundUrl ::{}",cacheUrl);
            return BIT_LY +cacheUrl.getCode();
        }

        UrlMapping foundUrl = urlRepository.findByLongUrl(url);
        if(foundUrl!=null){
            log.info("foundUrl ::{}",foundUrl);
            return BIT_LY +foundUrl.getCode();
        }

        Long id = snowflakeId.generateId();
        String code = Base62.encode(id);
        try {
            UrlMapping mapping = UrlMapping.builder()
                    .id(id)
                    .code(code)
                    .longUrl(url)
                    .build();
            urlRepository.save(mapping);
            redisService.set(url,code,5L);
        }catch (Exception e){
            UrlMapping existing = urlRepository.findByLongUrl(url);
            if(existing!=null){
                log.info("existing ::{}",existing);
                return BIT_LY +existing.getCode();
            }
        }

        return BIT_LY+code;
    }

    public String findByCode(String code) {
        return urlRepository.findLongUrlByCode(code).getLongUrl();
    }
}
