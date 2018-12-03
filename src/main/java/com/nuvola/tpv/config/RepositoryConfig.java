package com.nuvola.tpv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;

import com.nuvola.tpv.model.Activity;
import com.nuvola.tpv.model.ActivityGroup;
import com.nuvola.tpv.model.Deliverable;
import com.nuvola.tpv.model.Department;
import com.nuvola.tpv.model.Installation;
import com.nuvola.tpv.model.InstallationPackage;
import com.nuvola.tpv.model.Invoice;
import com.nuvola.tpv.model.Menu;
import com.nuvola.tpv.model.Milestone;
import com.nuvola.tpv.model.Payment;
import com.nuvola.tpv.model.Project;
import com.nuvola.tpv.model.PurchaseOrder;
import com.nuvola.tpv.model.ResourceLevel;
import com.nuvola.tpv.model.ResourceRole;
import com.nuvola.tpv.model.Role;
import com.nuvola.tpv.model.Training;
import com.nuvola.tpv.model.TrainingPackage;
import com.nuvola.tpv.model.User;
import com.nuvola.tpv.model.UserGroup;

@Configuration
public class RepositoryConfig extends RepositoryRestConfigurerAdapter {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Project.class);
        config.exposeIdsFor(Department.class);
        config.exposeIdsFor(Role.class);
        config.exposeIdsFor(UserGroup.class);
        config.exposeIdsFor(Menu.class);
        config.exposeIdsFor(User.class);
        config.exposeIdsFor(InstallationPackage.class);
        config.exposeIdsFor(Installation.class);
        config.exposeIdsFor(TrainingPackage.class);
        config.exposeIdsFor(Training.class);
        config.exposeIdsFor(Milestone.class);
        config.exposeIdsFor(Deliverable.class);
        config.exposeIdsFor(ResourceRole.class);
        config.exposeIdsFor(ResourceLevel.class);
        config.exposeIdsFor(ActivityGroup.class);
        config.exposeIdsFor(Activity.class);
        config.exposeIdsFor(PurchaseOrder.class);
        config.exposeIdsFor(Invoice.class);
        config.exposeIdsFor(Payment.class);
//        config.setDefaultPageSize(1000000);
//        config.setMaxPageSize(1000000);
    }
    
    @Bean
    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory,
                                       MongoMappingContext context) {

        MappingMongoConverter converter =
                new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), context);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory, converter);

        return mongoTemplate;

    }
    
//    @Configuration
//    public class RestApiConfiguration extends RepositoryRestConfigurerAdapter {
//
//        @Bean
//        public HateoasPageableHandlerMethodArgumentResolver customResolver(
//            HateoasPageableHandlerMethodArgumentResolver pageableResolver) {
//            pageableResolver.setOneIndexedParameters(true);
//            pageableResolver.setFallbackPageable(null);
//            return pageableResolver;
//        }
//    }
    
    
    /**
     * This configuration is needed to disable the default paging when invoking findAll
     * @param pageableResolver
     * @return
     */
    @Bean
    public HateoasPageableHandlerMethodArgumentResolver customResolver(
        HateoasPageableHandlerMethodArgumentResolver pageableResolver) {
        pageableResolver.setOneIndexedParameters(true);
        pageableResolver.setFallbackPageable(PageRequest.of(0, Integer.MAX_VALUE));
        pageableResolver.setMaxPageSize(Integer.MAX_VALUE);
        return pageableResolver;
    }
    
   
}