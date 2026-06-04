package com.kody.coinsec.backend.dto;

import lombok.Data;

@Data
public class CategoryRequest {

    private String name;
    private String type;
    private String icon;
    private Integer sort;
}
