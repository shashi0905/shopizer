package com.salesmanager.shop.init.data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.category.CategoryService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.availability.ProductAvailabilityService;
import com.salesmanager.core.business.services.catalog.product.manufacturer.ManufacturerService;
import com.salesmanager.core.business.services.catalog.product.price.ProductPriceService;
import com.salesmanager.core.business.services.catalog.product.type.ProductTypeService;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.catalog.category.Category;
import com.salesmanager.core.model.catalog.category.CategoryDescription;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.description.ProductDescription;
import com.salesmanager.core.model.catalog.product.availability.ProductAvailability;
import com.salesmanager.core.model.catalog.product.image.ProductImage;
import com.salesmanager.core.model.catalog.product.manufacturer.Manufacturer;
import com.salesmanager.core.model.catalog.product.manufacturer.ManufacturerDescription;
import com.salesmanager.core.model.catalog.product.price.ProductPrice;
import com.salesmanager.core.model.catalog.product.price.ProductPriceDescription;
import com.salesmanager.core.model.catalog.product.type.ProductType;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;

@Component
public class InitDataImpl implements InitData {

    @Inject
    private MerchantStoreService merchantStoreService;
    
    @Inject
    private LanguageService languageService;
    
    @Inject
    private CategoryService categoryService;
    
    @Inject
    private ProductService productService;
    
    @Inject
    private ProductTypeService productTypeService;
    
    @Inject
    private ManufacturerService manufacturerService;
    
    @Inject
    private ProductAvailabilityService productAvailabilityService;
    
    @Inject
    private ProductPriceService productPriceService;

    @Override
    public void initInitialData() throws ServiceException {
        
        MerchantStore store = merchantStoreService.getByCode(MerchantStore.DEFAULT_STORE);
        Language en = languageService.getByCode("en");
        
        // Create manufacturer
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setMerchantStore(store);
        manufacturer.setCode("generic");
        
        ManufacturerDescription manufacturerDesc = new ManufacturerDescription();
        manufacturerDesc.setLanguage(en);
        manufacturerDesc.setName("Generic");
        manufacturerDesc.setManufacturer(manufacturer);
        manufacturer.getDescriptions().add(manufacturerDesc);
        
        manufacturerService.create(manufacturer);
        
        // Create product type
        ProductType productType = new ProductType();
        productType.setCode("GENERAL");
        productType.setMerchantStore(store);
        productType.setAllowAddToCart(true);
        productTypeService.saveOrUpdate(productType);
        
        // Create categories
        Category furniture = createCategory(store, en, "furniture", "Furniture", "Furniture and home items", 0);
        Category chairs = createCategory(store, en, "chairs", "Chairs", "Office and home chairs", 100);
        Category tables = createCategory(store, en, "tables", "Tables", "Office and home tables", 200);
        
        // Create products
        createProduct(store, en, chairs, manufacturer, productType, 
            "CHAIR-001", "Office Chair", "Comfortable office chair with adjustable height", 99.99, 100);
        
        createProduct(store, en, chairs, manufacturer, productType,
            "CHAIR-002", "Ergonomic Chair", "Ergonomic office chair with lumbar support", 149.99, 50);
        
        createProduct(store, en, chairs, manufacturer, productType,
            "CHAIR-003", "Executive Chair", "Leather executive chair", 299.99, 30);
        
        createProduct(store, en, tables, manufacturer, productType,
            "TABLE-001", "Office Desk", "Modern office desk", 199.99, 40);
        
        createProduct(store, en, tables, manufacturer, productType,
            "TABLE-002", "Conference Table", "Large conference table", 499.99, 20);
    }
    
    private Category createCategory(MerchantStore store, Language language, 
            String code, String name, String description, int sortOrder) throws ServiceException {
        
        Category category = new Category();
        category.setMerchantStore(store);
        category.setCode(code);
        category.setVisible(true);
        category.setSortOrder(sortOrder);
        
        CategoryDescription categoryDesc = new CategoryDescription();
        categoryDesc.setLanguage(language);
        categoryDesc.setName(name);
        categoryDesc.setDescription(description);
        categoryDesc.setSeUrl(code);
        categoryDesc.setCategory(category);
        category.getDescriptions().add(categoryDesc);
        
        categoryService.create(category);
        return category;
    }
    
    private void createProduct(MerchantStore store, Language language, Category category,
            Manufacturer manufacturer, ProductType productType,
            String sku, String name, String description, double price, int quantity) throws ServiceException {
        
        Product product = new Product();
        product.setMerchantStore(store);
        product.setSku(sku);
        product.setAvailable(true);
        product.setProductShipeable(true);
        product.setManufacturer(manufacturer);
        product.setType(productType);
        product.setDateAvailable(new Date());
        
        // Add category
        Set<Category> categories = new HashSet<>();
        categories.add(category);
        product.setCategories(categories);
        
        // Product description
        ProductDescription productDesc = new ProductDescription();
        productDesc.setLanguage(language);
        productDesc.setName(name);
        productDesc.setDescription(description);
        productDesc.setSeUrl(sku.toLowerCase());
        productDesc.setProduct(product);
        product.getDescriptions().add(productDesc);
        
        // Create availability BEFORE saving product
        ProductAvailability availability = new ProductAvailability();
        availability.setProduct(product);
        availability.setMerchantStore(store);
        availability.setProductQuantity(quantity);
        availability.setProductQuantityOrderMin(1);
        availability.setProductQuantityOrderMax(-1);
        availability.setRegion("*");
        availability.setAvailable(true);
        
        // Create price
        ProductPrice productPrice = new ProductPrice();
        productPrice.setProductAvailability(availability);
        productPrice.setDefaultPrice(true);
        productPrice.setProductPriceAmount(new BigDecimal(price));
        productPrice.setCode("base");
        
        ProductPriceDescription priceDesc = new ProductPriceDescription();
        priceDesc.setLanguage(language);
        priceDesc.setName("Default price");
        priceDesc.setProductPrice(productPrice);
        productPrice.getDescriptions().add(priceDesc);
        
        availability.getPrices().add(productPrice);
        product.getAvailabilities().add(availability);
        
        // Set review count before saving
        if (sku.equals("CHAIR-001")) {
            product.setProductReviewCount(150);
        }
        
        productService.create(product);
    }
}
