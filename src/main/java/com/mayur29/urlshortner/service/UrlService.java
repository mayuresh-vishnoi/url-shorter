package com.mayur29.urlshortner.service;

import com.mayur29.urlshortner.entities.UrlMapping;
import com.mayur29.urlshortner.repository.UrlRepository;
import com.mayur29.urlshortner.utils.Base62;
import org.springframework.stereotype.Service;

@Service
public class UrlService {
    private UrlRepository urlRepository;
    private Base62 base62;

    public UrlService(UrlRepository urlRepository,Base62 base62){
        this.urlRepository = urlRepository;
        this.base62 = base62;
    }

    public String shorten(String url) {
        UrlMapping saved =  urlRepository.save(UrlMapping.builder()
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
