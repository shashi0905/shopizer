# Java 11 to Java 21 LTS Migration Plan

## Document Information
- **Project**: Shopizer E-commerce Platform
- **Current Version**: Java 11, Spring Boot 2.5.12
- **Target Version**: Java 21 LTS, Spring Boot 3.2.x
- **Document Version**: 1.0
- **Date**: February 27, 2026
- **Author**: Development Team
- **Status**: Planning Phase

---

## Table of Contents
1. [Executive Summary](#executive-summary)
2. [Current State Analysis](#current-state-analysis)
3. [Migration Phases](#migration-phases)
4. [Risk Assessment](#risk-assessment)
5. [Testing Strategy](#testing-strategy)
6. [Timeline & Resources](#timeline--resources)
7. [Success Criteria](#success-criteria)
8. [Rollback Plan](#rollback-plan)

---

## Executive Summary

### Overview
This document outlines the comprehensive plan for migrating Shopizer from Java 11 to Java 21 LTS, including Spring Boot upgrade from 2.5.12 to 3.2.x.

### Key Objectives
- ✅ Upgrade to Java 21 LTS for long-term support
- ✅ Modernize dependency stack (Spring Boot 3.x, Hibernate 6.x)
- ✅ Maintain 100% functional compatibility
- ✅ Improve performance and reduce memory footprint
- ✅ Leverage Java 21 features (Virtual Threads, Pattern Matching, Records)

### Timeline
- **Total Duration**: 6-8 weeks
- **Start Date**: TBD
- **Target Completion**: TBD

### Risk Level
**Medium-High** - Major framework upgrades with breaking changes

---

## Current State Analysis

### Technology Stack

| Component | Current Version | Target Version | Change Type |
|-----------|----------------|----------------|-------------|
| Java | 11 | 21 LTS | Major |
| Spring Boot | 2.5.12 | 3.2.2 | Major |
| Spring Framework | 5.3.x | 6.1.x | Major |
| Hibernate | 5.6.x | 6.4.x | Major |
| Jakarta EE | javax.* | jakarta.* | Namespace |
| H2 Database | 1.4.x | 2.2.x | Major |
| Lombok | 1.18.x | 1.18.30+ | Minor |
| Jackson | 2.13.x | 2.16.x | Minor |

### Module Structure
```
shopizer/
├── sm-core-model/      # Domain entities
├── sm-core/            # Business logic
├── sm-shop-model/      # DTOs
└── sm-shop/            # REST API & Web
```

### Critical Dependencies
- Spring Security (authentication/authorization)
- Hibernate/JPA (data persistence)
- H2 Database (embedded database)
- Jackson (JSON serialization)
- Apache Commons (utilities)

### Known Issues
- Low test coverage (~30-40%)
- Heavy use of deprecated APIs
- Custom security configurations
- Legacy code patterns

---

## Migration Phases

### Phase 0: Pre-Migration Assessment & Preparation
**Duration**: 1 week | **Priority**: Critical

#### Tasks

##### 0.1 Codebase Analysis (2 days)
```bash
# Run dependency analysis
mvn versions:display-dependency-updates > dependency-updates.txt
mvn versions:display-plugin-updates > plugin-updates.txt

# Identify deprecated APIs
grep -r "javax\." --include="*.java" sm-*/src/
```

**Deliverables**:
- Dependency update matrix
- Deprecated API usage report
- Breaking changes document

##### 0.2 Test Coverage Assessment (2 days)
```bash
# Generate coverage report
mvn clean test jacoco:report
```

**Actions**:
- [ ] Measure current test coverage
- [ ] Identify untested critical paths
- [ ] Document manual test procedures
- [ ] Create test data backup scripts

**Target**: Establish baseline metrics

##### 0.3 Environment Setup (1 day)
- [ ] Install Java 21 (Eclipse Temurin)
- [ ] Configure IDE for Java 21
- [ ] Create feature branch: `feature/java-21-migration`
- [ ] Set up parallel CI pipeline
- [ ] Document rollback procedures

**Acceptance Criteria**:
- ✅ Java 21 environment verified
- ✅ Feature branch created
- ✅ Baseline metrics documented

---

### Phase 1: Increase Test Coverage
**Duration**: 2 weeks | **Priority**: Critical

> **Rationale**: Must have comprehensive tests before making breaking changes

#### Module 1.1: Core Business Logic Tests (1 week)

##### Product Module (2 days)
**Files**: `ProductServiceImpl.java`, `ProductBadgeServiceImpl.java`

```java
@SpringBootTest
class ProductServiceTest {
    @Test
    void shouldCreateProduct() { }
    
    @Test
    void shouldCalculateBadges() { }
    
    @Test
    void shouldManageInventory() { }
}
```

**Test Cases**: 15-20
**Coverage Target**: >70%

##### Category Module (1 day)
**Files**: `CategoryServiceImpl.java`

**Test Cases**: 10-12
**Coverage Target**: >70%

##### Order Module (2 days)
**Files**: `OrderServiceImpl.java`

**Test Cases**: 20-25
**Coverage Target**: >70%

##### Customer Module (1 day)
**Files**: `CustomerServiceImpl.java`

**Test Cases**: 10-15
**Coverage Target**: >70%

#### Module 1.2: API Integration Tests (1 week)

##### REST API Tests (3 days)
```java
@SpringBootTest
@AutoConfigureMockMvc
class ProductApiIntegrationTest {
    @Test
    void shouldGetProducts() { }
    
    @Test
    void shouldCreateProduct() { }
    
    @Test
    void shouldHandleAuthentication() { }
}
```

**Coverage**: All v1 and v2 endpoints

##### Database Integration Tests (2 days)
```java
@DataJpaTest
class ProductRepositoryTest {
    @Test
    void shouldFindByCategory() { }
    
    @Test
    void shouldHandleTransactions() { }
}
```

##### H2 Profile Tests (2 days)
- [ ] Test H2 initialization
- [ ] Test sample data creation
- [ ] Test InitDataImpl

**Phase 1 Deliverable**: Test coverage >60%

---

### Phase 2: Dependency Analysis & Updates
**Duration**: 1 week | **Priority**: High

#### 2.1 Create Dependency Matrix (1 day)

##### Spring Boot Migration Path
```
2.5.12 → 2.6.x → 2.7.x → 3.0.x → 3.1.x → 3.2.x
```

**Strategy**: Direct jump to 3.2.x (recommended by Spring team)

##### Breaking Changes Inventory

**Spring Boot 3.x**:
- `javax.*` → `jakarta.*` (all packages)
- Spring Security 6.x configuration changes
- Property name changes
- Actuator endpoint changes
- Removed auto-configurations

**Hibernate 6.x**:
- Criteria API changes
- Type system updates
- Query language modifications

#### 2.2 Library Compatibility Check (2 days)

| Library | Current | Compatible Version | Notes |
|---------|---------|-------------------|-------|
| Lombok | 1.18.x | 1.18.30+ | Update required |
| MapStruct | 1.4.x | 1.5.5+ | Update required |
| Apache Commons | Various | Latest | Check each |
| Jackson | 2.13.x | 2.16.x | Update required |
| Swagger | 2.x | SpringDoc 2.x | Replace |

#### 2.3 Create Migration Scripts (2 days)

##### Automated Refactoring Script
```bash
#!/bin/bash
# javax-to-jakarta.sh

find . -name "*.java" -type f -exec sed -i '' \
  -e 's/import javax\.persistence\./import jakarta.persistence./g' \
  -e 's/import javax\.validation\./import jakarta.validation./g' \
  -e 's/import javax\.servlet\./import jakarta.servlet./g' \
  {} +
```

**Deliverable**: Migration checklist with automated scripts

---

### Phase 3: Incremental Migration
**Duration**: 3-4 weeks | **Priority**: Critical

#### Module 3.1: Core Module Migration (1 week)

##### Step 1: Update Parent POM (1 day)
```xml
<properties>
    <java.version>21</java.version>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
    <spring-boot.version>3.2.2</spring-boot.version>
</properties>

<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.2</version>
</parent>
```

##### Step 2: Update sm-core-model (2 days)
```bash
# Run automated refactoring
./scripts/javax-to-jakarta.sh sm-core-model/

# Manual updates
- Update @Entity annotations
- Update @Column annotations
- Update validation annotations
- Update serialization

# Test
mvn clean test -pl sm-core-model
```

**Risk**: Medium - 100+ entity classes

##### Step 3: Update sm-core (2 days)
```bash
# Update service layer
- Update @Transactional imports
- Update exception handling
- Update Hibernate configurations

# Test
mvn clean test -pl sm-core
```

**Risk**: High - Core business logic

##### Step 4: Validate Core Module (1 day)
```bash
mvn clean install -pl sm-core-model,sm-core
mvn verify -pl sm-core-model,sm-core
```

#### Module 3.2: Shop Module Migration (1 week)

##### Step 1: Update sm-shop-model (1 day)
- [ ] Update DTOs
- [ ] Update validation annotations
- [ ] Update Jackson annotations

##### Step 2: Update sm-shop (3 days)

**Critical Files**:
```
sm-shop/src/main/java/com/salesmanager/shop/
├── application/ShopApplication.java
├── store/security/MultipleEntryPointsSecurityConfig.java
├── store/api/v1/**/*.java
└── store/api/v2/**/*.java
```

**Changes Required**:
```java
// Spring Security 6.x
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        // New configuration style
    }
}
```

##### Step 3: Update Configuration (1 day)
```properties
# application.properties updates
# Old: spring.jpa.hibernate.ddl-auto
# New: spring.jpa.hibernate.ddl-auto (same, but verify)

# Old: management.endpoints.web.exposure.include
# New: management.endpoints.web.exposure.include (verify format)
```

##### Step 4: Validate Shop Module (2 days)
```bash
mvn clean install -pl sm-shop
mvn spring-boot:run -pl sm-shop

# Smoke tests
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/v1/products?store=DEFAULT&lang=en
```

#### Module 3.3: Integration Testing (1 week)

##### Full Build Test (1 day)
```bash
mvn clean install
```

##### API Integration Tests (2 days)
- [ ] Test all v1 endpoints
- [ ] Test all v2 endpoints  
- [ ] Test authentication/authorization
- [ ] Test file uploads
- [ ] Test H2 console

##### Frontend Integration (2 days)
- [ ] Test Shop frontend
- [ ] Test Admin frontend
- [ ] Test all CRUD operations
- [ ] Test badge system

##### Performance Baseline (2 days)
```bash
# Benchmark Java 11 vs Java 21
ab -n 1000 -c 10 http://localhost:8080/api/v1/products

# Metrics
- Startup time
- Memory usage
- Response times
- Throughput
```

---

### Phase 4: Optimization & Java 21 Features
**Duration**: 1 week | **Priority**: Medium

#### 4.1 Enable Java 21 Features (3 days)

##### Virtual Threads
```properties
# application.properties
spring.threads.virtual.enabled=true
```

**Expected Benefit**: 20-30% throughput improvement for I/O operations

##### Pattern Matching
```java
// Refactor instanceof checks
if (obj instanceof String s) {
    return s.toUpperCase();
}

// Switch pattern matching
return switch (status) {
    case PENDING -> "Processing";
    case COMPLETED -> "Done";
    default -> "Unknown";
};
```

**Estimate**: 50-100 occurrences

##### Record Classes
```java
// Convert simple DTOs
public record ProductSummary(
    Long id,
    String name,
    BigDecimal price,
    String sku
) {}
```

**Candidates**: 10-15 DTO classes

##### Sequenced Collections
```java
// Use new methods
List<Product> products = repository.findAll();
Product first = products.getFirst();
Product last = products.getLast();
List<Product> reversed = products.reversed();
```

#### 4.2 Performance Optimizations (2 days)

##### JVM Tuning
```dockerfile
ENV JAVA_OPTS="-XX:+UseG1GC \
               -XX:MaxGCPauseMillis=200 \
               -XX:+UseStringDeduplication \
               -Xmx512m -Xms256m"
```

##### Query Optimization
- [ ] Add missing database indexes
- [ ] Fix N+1 query problems
- [ ] Add query hints
- [ ] Enable query caching

#### 4.3 Code Modernization (2 days)
- [ ] Replace Optional.get() with orElseThrow()
- [ ] Use var for local variables
- [ ] Update to text blocks for SQL/JSON
- [ ] Remove unnecessary null checks

---

### Phase 5: CI/CD & Infrastructure Updates
**Duration**: 3 days | **Priority**: High

#### 5.1 Update GitHub Actions (1 day)
```yaml
name: CI Pipeline

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: 'maven'
      
      - name: Build with Maven
        run: mvn clean install
```

#### 5.2 Update Docker Images (1 day)
```dockerfile
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY shopizer.jar /app/
ENV JAVA_OPTS="-XX:+UseG1GC -Xmx512m"
CMD ["java", "-jar", "shopizer.jar"]
```

#### 5.3 Update Infrastructure Repo (1 day)
- [ ] Update docker-compose.yml
- [ ] Update deployment scripts
- [ ] Update documentation
- [ ] Test full deployment

---

### Phase 6: Final Validation & Rollout
**Duration**: 3 days | **Priority**: Critical

#### 6.1 Comprehensive Testing (2 days)

##### Test Checklist
```markdown
## Functional Tests
- [ ] Product CRUD
- [ ] Category management
- [ ] Order processing
- [ ] Customer authentication
- [ ] Badge system
- [ ] Image upload
- [ ] Search functionality
- [ ] H2 console access

## Non-Functional Tests
- [ ] Performance (no regression)
- [ ] Security (auth/authz)
- [ ] Database migrations
- [ ] API compatibility
- [ ] Error handling
- [ ] Logging
```

#### 6.2 Staged Rollout (1 day)

**Deployment Strategy**:
1. **Dev Environment**: Deploy and test
2. **Staging Environment**: Full regression
3. **Production**: Gradual rollout with monitoring

**Monitoring**:
- Application logs
- Error rates
- Response times
- Memory usage
- CPU usage

---

## Risk Assessment

### High-Risk Areas

| Risk | Impact | Probability | Mitigation |
|------|--------|-------------|------------|
| Spring Security breaking changes | High | High | Extensive testing, reference docs |
| Hibernate query incompatibility | High | Medium | Test all custom queries |
| javax → jakarta namespace issues | Medium | High | Automated scripts + verification |
| H2 database compatibility | Medium | Medium | Test early, MySQL fallback |
| Third-party library conflicts | Medium | Medium | Check compatibility matrix |
| Performance regression | Medium | Low | Benchmark before/after |
| Production deployment issues | High | Low | Staged rollout, quick rollback |

### Risk Mitigation Strategies

#### 1. Comprehensive Testing
- Achieve >60% test coverage before migration
- Add integration tests for all APIs
- Performance benchmarking

#### 2. Incremental Approach
- Migrate module by module
- Validate each module before proceeding
- Easy rollback at each step

#### 3. Parallel Development
- Keep Java 11 branch stable
- Develop Java 21 in feature branch
- Merge only after full validation

#### 4. Monitoring & Alerting
- Set up application monitoring
- Define success metrics
- Alert on anomalies

---

## Testing Strategy

### Test Pyramid

```
        /\
       /E2E\      10% - End-to-End Tests
      /____\
     /      \
    /  INT   \    30% - Integration Tests
   /________\
  /          \
 /    UNIT    \   60% - Unit Tests
/______________\
```

### Coverage Targets

| Module | Current | Target | Priority |
|--------|---------|--------|----------|
| sm-core-model | ~40% | >60% | High |
| sm-core | ~35% | >70% | Critical |
| sm-shop-model | ~30% | >60% | High |
| sm-shop | ~25% | >60% | Critical |

### Test Categories

#### Unit Tests
```java
@Test
void shouldCalculateProductBadge() {
    Product product = createTestProduct();
    product.setProductReviewCount(150);
    
    List<ProductBadge> badges = badgeService.calculateBadges(product, store);
    
    assertTrue(badges.contains(ProductBadge.BESTSELLER));
}
```

#### Integration Tests
```java
@SpringBootTest
@AutoConfigureMockMvc
class ProductApiTest {
    @Test
    void shouldGetProducts() throws Exception {
        mockMvc.perform(get("/api/v1/products")
            .param("store", "DEFAULT")
            .param("lang", "en"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.products").isArray());
    }
}
```

#### E2E Tests
```bash
# Automated E2E with Selenium/Playwright
- User registration flow
- Product purchase flow
- Admin product management
```

---

## Timeline & Resources

### Detailed Schedule

| Phase | Duration | Start | End | Dependencies |
|-------|----------|-------|-----|--------------|
| Phase 0: Assessment | 1 week | Week 1 | Week 1 | None |
| Phase 1: Test Coverage | 2 weeks | Week 2 | Week 3 | Phase 0 |
| Phase 2: Dependency Analysis | 1 week | Week 2 | Week 2 | Phase 0 |
| Phase 3: Migration | 3-4 weeks | Week 4 | Week 7 | Phase 1, 2 |
| Phase 4: Optimization | 1 week | Week 7 | Week 7 | Phase 3 |
| Phase 5: CI/CD Updates | 3 days | Week 8 | Week 8 | Phase 3 |
| Phase 6: Validation | 3 days | Week 8 | Week 8 | All |
| **Total** | **6-8 weeks** | | | |

### Resource Allocation

#### Team Composition
- **1 Senior Java Developer** (Full-time, 6-8 weeks)
  - Lead migration effort
  - Code refactoring
  - Technical decisions

- **1 QA Engineer** (Part-time, 3 weeks)
  - Test case creation (Phase 1)
  - Integration testing (Phase 3)
  - Final validation (Phase 6)

- **1 DevOps Engineer** (Part-time, 1 week)
  - CI/CD updates (Phase 5)
  - Infrastructure updates
  - Deployment support

#### Estimated Effort
- **Development**: 240-320 hours
- **Testing**: 80-100 hours
- **DevOps**: 24-32 hours
- **Total**: 344-452 hours

---

## Success Criteria

### Must Have (Go/No-Go)

#### Functional Requirements
- ✅ All existing features working
- ✅ All API endpoints functional
- ✅ Authentication/authorization working
- ✅ Database operations successful
- ✅ File upload/download working
- ✅ H2 console accessible

#### Quality Requirements
- ✅ Test coverage >60%
- ✅ All tests passing
- ✅ Zero critical bugs
- ✅ Zero security vulnerabilities
- ✅ Code review completed

#### Performance Requirements
- ✅ No performance degradation
- ✅ Startup time ≤ 60 seconds
- ✅ API response time (p95) ≤ 200ms
- ✅ Memory usage ≤ 512MB

#### Documentation Requirements
- ✅ Migration guide updated
- ✅ API documentation updated
- ✅ Deployment guide updated
- ✅ Troubleshooting guide created

### Nice to Have

#### Performance Improvements
- ✅ 10-20% faster startup time
- ✅ 15-25% better throughput (virtual threads)
- ✅ Reduced memory footprint

#### Code Quality
- ✅ Modern Java idioms (records, pattern matching)
- ✅ Reduced code complexity
- ✅ Better error handling

---

## Rollback Plan

### Rollback Triggers
- Critical functionality broken
- Performance degradation >20%
- Security vulnerabilities introduced
- Data corruption detected
- Unresolvable production issues

### Rollback Procedure

#### Step 1: Immediate Actions (5 minutes)
```bash
# Stop Java 21 containers
docker-compose down

# Switch to Java 11 image
docker pull shopizer:java11-latest
docker-compose up -d
```

#### Step 2: Verification (10 minutes)
- [ ] Check application health
- [ ] Verify API endpoints
- [ ] Test critical flows
- [ ] Monitor error logs

#### Step 3: Communication (15 minutes)
- [ ] Notify stakeholders
- [ ] Update status page
- [ ] Document issues
- [ ] Plan remediation

### Rollback Testing
- [ ] Practice rollback in staging
- [ ] Document rollback time
- [ ] Verify data integrity after rollback

**Target Rollback Time**: <15 minutes

---

## Monitoring & Validation

### Key Metrics

#### Application Metrics
```yaml
Performance:
  startup_time:
    baseline: 60s
    target: 45s
    alert_threshold: 75s
  
  api_response_time_p95:
    baseline: 200ms
    target: 180ms
    alert_threshold: 300ms
  
  memory_usage:
    baseline: 450MB
    target: 400MB
    alert_threshold: 600MB
  
  throughput:
    baseline: 100 req/s
    target: 120 req/s
    alert_threshold: 80 req/s

Quality:
  test_coverage:
    baseline: 35%
    target: 60%
    minimum: 55%
  
  build_time:
    baseline: 5min
    target: 4min
    alert_threshold: 7min
  
  critical_bugs:
    target: 0
    alert_threshold: 1
```

#### Business Metrics
- Order completion rate
- User registration success rate
- API error rate
- Customer satisfaction

### Monitoring Tools
- **Application**: Spring Boot Actuator
- **Logs**: ELK Stack / CloudWatch
- **Metrics**: Prometheus + Grafana
- **APM**: New Relic / Datadog (optional)

---

## Appendices

### Appendix A: Dependency Update Matrix

| Dependency | Current | Target | Breaking Changes |
|------------|---------|--------|------------------|
| Spring Boot | 2.5.12 | 3.2.2 | Yes - Major |
| Spring Framework | 5.3.x | 6.1.x | Yes - Major |
| Spring Security | 5.6.x | 6.2.x | Yes - Config changes |
| Hibernate | 5.6.x | 6.4.x | Yes - API changes |
| Jakarta EE | javax.* | jakarta.* | Yes - Namespace |
| H2 Database | 1.4.200 | 2.2.224 | Yes - SQL changes |
| Lombok | 1.18.22 | 1.18.30 | No |
| Jackson | 2.13.x | 2.16.x | No |
| Apache Commons Lang | 3.12.0 | 3.14.0 | No |
| Apache Commons IO | 2.11.0 | 2.15.1 | No |

### Appendix B: Breaking Changes Reference

#### Spring Boot 3.x Breaking Changes
1. `javax.*` → `jakarta.*` namespace
2. Spring Security configuration API changes
3. Property name changes
4. Actuator endpoint changes
5. Removed auto-configurations
6. Minimum Java version: 17

#### Hibernate 6.x Breaking Changes
1. Criteria API changes
2. Type system updates
3. Query language modifications
4. Removed deprecated APIs

### Appendix C: Useful Resources

#### Official Documentation
- [Spring Boot 3.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)
- [Spring Security 6.0 Migration](https://docs.spring.io/spring-security/reference/migration/index.html)
- [Hibernate 6.0 Migration Guide](https://docs.jboss.org/hibernate/orm/6.0/migration-guide/migration-guide.html)
- [Java 21 Release Notes](https://openjdk.org/projects/jdk/21/)

#### Tools
- [OpenRewrite](https://docs.openrewrite.org/) - Automated refactoring
- [Spring Boot Migrator](https://github.com/spring-projects-experimental/spring-boot-migrator)
- [JaCoCo](https://www.jacoco.org/) - Code coverage

---

## Document History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2026-02-27 | Development Team | Initial version |

---

## Approval

| Role | Name | Signature | Date |
|------|------|-----------|------|
| Technical Lead | | | |
| QA Lead | | | |
| DevOps Lead | | | |
| Project Manager | | | |

---

**End of Document**
