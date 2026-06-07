package com.kody.coinsec.backend.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/icons")
public class IconController {

    @GetMapping("/{name}.svg")
    public ResponseEntity<byte[]> getIcon(@PathVariable String name) throws IOException {
        ClassPathResource resource = new ClassPathResource("static/icons/" + name + ".svg");
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        byte[] bytes = resource.getInputStream().readAllBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("image/svg+xml"));
        headers.setCacheControl("public, max-age=86400");
        return ResponseEntity.ok().headers(headers).body(bytes);
    }
}
