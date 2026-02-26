package com.salesmanager.core.business.services.catalog.product.badge;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.ProductBadge;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.merchant.MerchantStore;

@Service("productBadgeService")
public class ProductBadgeServiceImpl implements ProductBadgeService {
    
    private static final int NEW_BADGE_DAYS = 15;
    private static final int BESTSELLER_THRESHOLD = 100;
    
    @Override
    public List<ProductBadge> calculateBadges(Product product, MerchantStore store) {
        List<ProductBadge> badges = new ArrayList<>();
        
        if(isNew(product)) {
            badges.add(ProductBadge.NEW);
        }
        
        if(isBestseller(product, store)) {
            badges.add(ProductBadge.BESTSELLER);
        }
        
        if(hasDiscount(product)) {
            badges.add(ProductBadge.SALE);
        }
        
        return badges;
    }
    
    private boolean isNew(Product product) {
        if(product.getAuditSection() == null || 
           product.getAuditSection().getDateCreated() == null) {
            return false;
        }
        
        Date createdDate = product.getAuditSection().getDateCreated();
        Date now = new Date();
        long diffInMillis = now.getTime() - createdDate.getTime();
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        
        return diffInDays <= NEW_BADGE_DAYS;
    }
    
    private boolean isBestseller(Product product, MerchantStore store) {
        if(product.getProductOrdered() == null) {
            return false;
        }
        
        return product.getProductOrdered() >= BESTSELLER_THRESHOLD;
    }
    
    private boolean hasDiscount(Product product) {
        if(product.getAvailabilities() != null) {
            for(ProductAvailability avail : product.getAvailabilities()) {
                if(avail.getPrices() != null) {
                    for(ProductPrice price : avail.getPrices()) {
                        BigDecimal specialAmount = price.getProductPriceSpecialAmount();
                        BigDecimal regularAmount = price.getProductPriceAmount();
                        
                        if(specialAmount != null && regularAmount != null &&
                           specialAmount.compareTo(regularAmount) < 0) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public Integer getBestsellerThreshold(MerchantStore store) {
        return BESTSELLER_THRESHOLD;
    }
}
