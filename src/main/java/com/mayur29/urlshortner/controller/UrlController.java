package com.mayur29.urlshortner.controller;

import com.mayur29.urlshortner.service.UrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UrlController {

    private UrlService urlService;

    public UrlController(UrlService urlService){
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public String shorten(@RequestBody String url){
        return urlService.shorten(url);
    }

    @GetMapping("/{code}")
    public String getOriginalUrl(@PathVariable String code){
        return urlService.findByCode(code);
    }
}
