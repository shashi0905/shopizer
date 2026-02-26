package com.salesmanager.core.business.services.catalog.product.badge;

import java.util.List;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.ProductBadge;
import com.salesmanager.core.model.merchant.MerchantStore;

public interface ProductBadgeService {
    /**
     * Calculate which badges apply to a product
     * @param product Product entity
     * @param store Merchant store
     * @return List of applicable badges
     */
    List<ProductBadge> calculateBadges(Product product, MerchantStore store);
    
    /**
     * Get bestseller threshold for store
     * @param store Merchant store
     * @return Minimum order count for bestseller badge
     */
    Integer getBestsellerThreshold(MerchantStore store);
}
