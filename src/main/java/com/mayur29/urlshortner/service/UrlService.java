package com.mayur29.urlshortner.service;

import com.mayur29.urlshortner.entities.UrlMapping;
import com.mayur29.urlshortner.repository.UrlRepository;
import com.mayur29.urlshortner.utils.Base62;
import com.mayur29.urlshortner.utils.SnowflakeId;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
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
        }catch (Exception e){
            UrlMapping existing = urlRepository.findByLongUrl(url);
            if(existing!=null){
                log.info("existing ::{}",existing);
                return BIT_LY +existing.getCode();
            }
        }

        return BIT_LY+code;
    }

    @RateLimiter(name = "findByCode",fallbackMethod = "findByCodeFallback",permits = 2)
    public String findByCode(String code) {
        UrlMapping cacheUrl = redisService.get(code,UrlMapping.class);
        if(cacheUrl!=null){
            log.info("cacheUrl ::{}",cacheUrl);
            return BIT_LY +cacheUrl.getLongUrl();
        }

        UrlMapping urlMapping = urlRepository.findLongUrlByCode(code);
        if(urlMapping!=null){
            log.info("url found in db ::{}",urlMapping);
            redisService.set(code,urlMapping,5L);
            return BIT_LY +urlMapping.getLongUrl();
        }

        throw new RuntimeException("Url Not found!!!!");
    }

    public String findByCodeFallback(String code) {
        return "Only two hits allowed";
    }
}
