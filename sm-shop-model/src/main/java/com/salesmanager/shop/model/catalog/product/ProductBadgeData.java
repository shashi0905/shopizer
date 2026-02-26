package com.salesmanager.shop.model.catalog.product;

import java.io.Serializable;

public class ProductBadgeData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String code;
    private String label;
    private String color;
    private Integer value;
    
    public ProductBadgeData() {}
    
    public ProductBadgeData(String code, String label, String color) {
        this.code = code;
        this.label = label;
        this.color = color;
    }
    
    public ProductBadgeData(String code, String label, String color, Integer value) {
        this.code = code;
        this.label = label;
        this.color = color;
        this.value = value;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public Integer getValue() {
        return value;
    }
    
    public void setValue(Integer value) {
        this.value = value;
    }
}
