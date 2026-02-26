package com.salesmanager.test.catalog;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.salesmanager.core.business.services.catalog.product.badge.ProductBadgeService;
import com.salesmanager.core.business.services.catalog.product.badge.ProductBadgeServiceImpl;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.ProductBadge;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.common.audit.AuditSection;
import com.salesmanager.core.model.merchant.MerchantStore;

public class ProductBadgeTest extends com.salesmanager.test.common.AbstractSalesManagerCoreTestCase {
    
    private ProductBadgeService badgeService = new ProductBadgeServiceImpl();
    
    @Test
    public void testNewBadge() {
        // Create product with recent date
        Product product = new Product();
        AuditSection audit = new AuditSection();
        audit.setDateCreated(new Date()); // Today
        product.setAuditSection(audit);
        
        MerchantStore store = new MerchantStore();
        store.setCode("DEFAULT");
        
        List<ProductBadge> badges = badgeService.calculateBadges(product, store);
        
        assertTrue("Product should have NEW badge", badges.contains(ProductBadge.NEW));
    }
    
    @Test
    public void testNewBadgeExpired() {
        // Create product with old date (20 days ago)
        Product product = new Product();
        AuditSection audit = new AuditSection();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -20);
        audit.setDateCreated(cal.getTime());
        product.setAuditSection(audit);
        
        MerchantStore store = new MerchantStore();
        store.setCode("DEFAULT");
        
        List<ProductBadge> badges = badgeService.calculateBadges(product, store);
        
        assertFalse("Product should NOT have NEW badge after 20 days", badges.contains(ProductBadge.NEW));
    }
    
    @Test
    public void testBestsellerBadge() {
        Product product = new Product();
        product.setProductOrdered(150); // Above threshold (100)
        
        MerchantStore store = new MerchantStore();
        store.setCode("DEFAULT");
        
        List<ProductBadge> badges = badgeService.calculateBadges(product, store);
        
        assertTrue("Product should have BESTSELLER badge", badges.contains(ProductBadge.BESTSELLER));
    }
    
    @Test
    public void testNoBestsellerBadge() {
        Product product = new Product();
        product.setProductOrdered(50); // Below threshold
        
        MerchantStore store = new MerchantStore();
        store.setCode("DEFAULT");
        
        List<ProductBadge> badges = badgeService.calculateBadges(product, store);
        
        assertFalse("Product should NOT have BESTSELLER badge", badges.contains(ProductBadge.BESTSELLER));
    }
    
    @Test
    public void testSaleBadge() {
        Product product = new Product();
        
        // Create availability with discounted price
        ProductAvailability availability = new ProductAvailability();
        Set<ProductPrice> prices = new HashSet<>();
        
        ProductPrice price = new ProductPrice();
        price.setProductPriceAmount(new BigDecimal("100.00"));
        price.setProductPriceSpecialAmount(new BigDecimal("80.00")); // 20% discount
        prices.add(price);
        
        availability.setPrices(prices);
        Set<ProductAvailability> availabilities = new HashSet<>();
        availabilities.add(availability);
        product.setAvailabilities(availabilities);
        
        MerchantStore store = new MerchantStore();
        store.setCode("DEFAULT");
        
        List<ProductBadge> badges = badgeService.calculateBadges(product, store);
        
        assertTrue("Product should have SALE badge", badges.contains(ProductBadge.SALE));
    }
    
    @Test
    public void testMultipleBadges() {
        // Create product with multiple badges
        Product product = new Product();
        
        // NEW badge
        AuditSection audit = new AuditSection();
        audit.setDateCreated(new Date());
        product.setAuditSection(audit);
        
        // BESTSELLER badge
        product.setProductOrdered(200);
        
        // SALE badge
        ProductAvailability availability = new ProductAvailability();
        Set<ProductPrice> prices = new HashSet<>();
        ProductPrice price = new ProductPrice();
        price.setProductPriceAmount(new BigDecimal("100.00"));
        price.setProductPriceSpecialAmount(new BigDecimal("75.00"));
        prices.add(price);
        availability.setPrices(prices);
        Set<ProductAvailability> availabilities = new HashSet<>();
        availabilities.add(availability);
        product.setAvailabilities(availabilities);
        
        MerchantStore store = new MerchantStore();
        store.setCode("DEFAULT");
        
        List<ProductBadge> badges = badgeService.calculateBadges(product, store);
        
        assertEquals("Product should have 3 badges", 3, badges.size());
        assertTrue("Should have NEW badge", badges.contains(ProductBadge.NEW));
        assertTrue("Should have BESTSELLER badge", badges.contains(ProductBadge.BESTSELLER));
        assertTrue("Should have SALE badge", badges.contains(ProductBadge.SALE));
    }
    
    @Test
    public void testNoBadges() {
        // Product with no qualifying badges
        Product product = new Product();
        
        // Old product
        AuditSection audit = new AuditSection();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        audit.setDateCreated(cal.getTime());
        product.setAuditSection(audit);
        
        // Low sales
        product.setProductOrdered(10);
        
        // No discount
        ProductAvailability availability = new ProductAvailability();
        Set<ProductPrice> prices = new HashSet<>();
        ProductPrice price = new ProductPrice();
        price.setProductPriceAmount(new BigDecimal("100.00"));
        price.setProductPriceSpecialAmount(new BigDecimal("100.00")); // No discount
        prices.add(price);
        availability.setPrices(prices);
        Set<ProductAvailability> availabilities = new HashSet<>();
        availabilities.add(availability);
        product.setAvailabilities(availabilities);
        
        MerchantStore store = new MerchantStore();
        store.setCode("DEFAULT");
        
        List<ProductBadge> badges = badgeService.calculateBadges(product, store);
        
        assertEquals("Product should have no badges", 0, badges.size());
    }
}
