package cloud.tenant;

import cloud.entity.TenantInfo;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 这个类负责根据租户ID来提供对应的数据源
 * @author lanyuanxiaoyao
 * @version 1.0
 */
public class TenantDataSourceProvider {

    // 使用一个map来存储我们租户和对应的数据源，租户和数据源的信息就是从我们的tenant_info表中读出来
    private static final ConcurrentMap<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    /*
     * 静态建立一个数据源，也就是我们的默认数据源，假如我们的访问信息里面没有指定tenantId，就使用默认数据源。
     * 在我这里默认数据源是cloud_config，实际上你可以指向你们的公共信息的库，或者拦截这个操作返回错误。
     */
    static {
        DataSourceBuilder<? extends DataSource> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url("jdbc:mysql://localhost:3306/tenant_tx?allowMultiQueries=true&autoReconnect=true&useSSL=false&serverTimezone=UTC");
        dataSourceBuilder.username("root");
        dataSourceBuilder.password("0558YinLe!341676");
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceMap.put("Default", dataSourceBuilder.build());
    }

    // 根据传进来的tenantId决定返回的数据源
    public static DataSource getTenantDataSource(String tenantId) {
        if (dataSourceMap.containsKey(tenantId)) {
            System.out.println("GetDataSource:" + tenantId);
            return dataSourceMap.get(tenantId);
        } else {
            System.out.println("GetDataSource:" + " Default");
            return dataSourceMap.get("Default");
        }
    }

    // 初始化的时候用于添加数据源的方法
    public static void addDataSource(TenantInfo tenantInfo) {
        DataSourceBuilder<? extends DataSource> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(tenantInfo.getUrl());
        dataSourceBuilder.username(tenantInfo.getUsername());
        dataSourceBuilder.password(tenantInfo.getPassword());
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceMap.put(tenantInfo.getTenantId(), dataSourceBuilder.build());
    }

}
