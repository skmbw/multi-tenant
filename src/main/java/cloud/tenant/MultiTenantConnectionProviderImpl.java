package cloud.tenant;

import cloud.service.StudentService;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 这个类是Hibernate框架拦截sql语句并在执行sql语句之前更换数据源提供的类
 * @author yinlei
 * @version 1.0
 */
@Component
public class MultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    @Autowired
    private StudentService studentService;

    // 在没有提供tenantId的情况下返回默认数据源
    @Override
    protected DataSource selectAnyDataSource() {
        return TenantDataSourceProvider.getTenantDataSource("Default");
    }

    // 提供了tenantId的话就根据ID来返回数据源
    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        return TenantDataSourceProvider.getTenantDataSource(tenantIdentifier);
    }
}
