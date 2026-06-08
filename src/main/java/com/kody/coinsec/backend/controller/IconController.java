package com.kody.coinsec.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "图标资源", description = "静态 SVG 图标文件")
public class IconController {

    @Operation(summary = "获取图标", description = "通过文件名获取 SVG 图标，用于账户和分类的图标展示")
    @GetMapping("/{name}.svg")
    public ResponseEntity<byte[]> getIcon(
            @Parameter(description = "图标文件名（不含扩展名）") @PathVariable String name) throws IOException {
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
