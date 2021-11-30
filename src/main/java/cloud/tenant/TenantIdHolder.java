package cloud.tenant;

public class TenantIdHolder {
    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    public static String getTenantId() {
        return HOLDER.get();
    }

    public static void setTenantId(String tenantId) {
        HOLDER.set(tenantId);
    }
}
