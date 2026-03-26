package com.mayur29.urlshortner.controller;

import com.mayur29.urlshortner.service.UrlService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
    public ResponseEntity<String> getOriginalUrl(@PathVariable String code){
        String url =  urlService.findByCode(code);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
