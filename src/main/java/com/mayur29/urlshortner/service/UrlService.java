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

    private UrlRepository urlRepository;
    private SnowflakeId snowflakeId;
    private Base62 base62;

    public UrlService(UrlRepository urlRepository,Base62 base62,SnowflakeId snowflakeId){
        this.urlRepository = urlRepository;
        this.base62 = base62;
        this.snowflakeId = snowflakeId;
    }

    public String shorten(String url) {
        Long id = snowflakeId.generateId();
        Optional<UrlMapping> foundUrl = urlRepository.findByLongUrl(url);
        if(foundUrl.isPresent()){
            log.info("foundUrl ::{}",foundUrl);
            return "http://bit.ly/"+foundUrl.get().getCode();
        }
        UrlMapping saved =  urlRepository.save(UrlMapping.builder()
                        .id(id)
                        .longUrl(url)
                .build());

        String code = Base62.encode(saved.getId());
        saved.setCode(code);
        urlRepository.save(saved);
        return "http://bit.ly/"+code;
    }

    public String findByCode(String code) {
        return urlRepository.findLongUrlByCode(code).getLongUrl();
    }
}
