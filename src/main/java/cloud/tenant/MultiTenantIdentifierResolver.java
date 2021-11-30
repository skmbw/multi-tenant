package cloud.tenant;

import cloud.service.StudentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 这个类是由Hibernate提供的用于识别tenantId的类，当每次执行sql语句被拦截就会调用这个类中的方法来获取tenantId
 * @author yinlei
 * @version 1.0
 */
@Component
public class MultiTenantIdentifierResolver implements CurrentTenantIdentifierResolver, Filter {

    private static final Logger LOGGER = LogManager.getLogger(MultiTenantIdentifierResolver.class);

    private String tenantId = "Default"; // 需要一个默认的租户Id

    private Set<String> whiteList = new HashSet<>();

    @Autowired
    private StudentService studentService;

//    public MultiTenantIdentifierResolver() {
//        WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
//        ConfigurableApplicationContext configurableApplicationContext = CloudApplication.getContext();
//        LOGGER.info(context != null ? "Get WebApplicationContext success." : "Get WebApplicationContext failure.");
//    }

    // 获取tenantId的逻辑在这个方法里面写
    @Override
    public String resolveCurrentTenantIdentifier() {
        this.tenantId = TenantIdHolder.getTenantId();
        if (this.tenantId == null) {
            this.tenantId = "Default";
        }
        return tenantId;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        whiteList.add("/login");
    }

    // 这个Filter是Spring init. but the tenant config is not the instance.
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String tenantId = httpRequest.getHeader("tenantId");
        if (tenantId == null) {
            tenantId = httpRequest.getParameter("tenantId");
            if (tenantId == null || tenantId.trim().length() == 0) {
                throw new RuntimeException("请求中多租户Id为空。");
            }
        }
        this.tenantId = tenantId;
        TenantIdHolder.setTenantId(this.tenantId);
        chain.doFilter(request, response);
    }
}
