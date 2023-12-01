package crud.software.Models;

import java.util.List;

public class CrudAdminPanelPermissionConfig {

    private String superAdminUsername;
    private String superAdminPassword;
    private String adminUsername;
    private String adminPassword;
    private String guestUsername;
    private String guestPassword;
    private List<String> imageColumns;
    private List<String> fileColumns;

    // Getters and Setters

    public String getSuperAdminUsername() {
        return superAdminUsername;
    }

    public void setSuperAdminUsername(String superAdminUsername) {
        this.superAdminUsername = superAdminUsername;
    }

    public String getSuperAdminPassword() {
        return superAdminPassword;
    }

    public void setSuperAdminPassword(String superAdminPassword) {
        this.superAdminPassword = superAdminPassword;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getGuestUsername() {
        return guestUsername;
    }

    public void setGuestUsername(String guestUsername) {
        this.guestUsername = guestUsername;
    }

    public String getGuestPassword() {
        return guestPassword;
    }

    public void setGuestPassword(String guestPassword) {
        this.guestPassword = guestPassword;
    }

    public List<String> getImageColumns() {
        return imageColumns;
    }

    public void setImageColumns(List<String> imageColumns) {
        this.imageColumns = imageColumns;
    }

    public List<String> getFileColumns() {
        return fileColumns;
    }

    public void setFileColumns(List<String> fileColumns) {
        this.fileColumns = fileColumns;
    }
}
