package com.salesmanager.core.model.catalog.product;

public enum ProductBadge {
    NEW("new", "New", "#9b59b6"),
    BESTSELLER("bestseller", "Bestseller", "#ff9800"),
    SALE("sale", "Sale", "#e91e63"),
    LIMITED("limited", "Limited", "#f44336"),
    FEATURED("featured", "Featured", "#2196f3"),
    TRENDING("trending", "Trending", "#ff5722");
    
    private final String code;
    private final String label;
    private final String color;
    
    ProductBadge(String code, String label, String color) {
        this.code = code;
        this.label = label;
        this.color = color;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getLabel() {
        return label;
    }
    
    public String getColor() {
        return color;
    }
}
