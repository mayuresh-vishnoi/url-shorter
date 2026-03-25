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
    private UrlRepository urlRepository;
    private SnowflakeId snowflakeId;
    private Base62 base62;

    public UrlService(UrlRepository urlRepository,Base62 base62,SnowflakeId snowflakeId){
        this.urlRepository = urlRepository;
        this.base62 = base62;
        this.snowflakeId = snowflakeId;
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

    public String findByCode(String code) {
        return urlRepository.findLongUrlByCode(code).getLongUrl();
    }
}
