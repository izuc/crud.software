package crud.software.Models;

public class CrudConfiguration {

    private CrudAuthTableConfig authTableConfig;
    private CrudAdminPanelPermissionConfig adminPanelConfig;

    // Getters and Setters

    public CrudAuthTableConfig getAuthTableConfig() {
        return authTableConfig;
    }

    public void setAuthTableConfig(CrudAuthTableConfig authTableConfig) {
        this.authTableConfig = authTableConfig;
    }

    public CrudAdminPanelPermissionConfig getAdminPanelConfig() {
        return adminPanelConfig;
    }

    public void setAdminPanelConfig(CrudAdminPanelPermissionConfig adminPanelConfig) {
        this.adminPanelConfig = adminPanelConfig;
    }
}
