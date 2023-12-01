package crud.software.Models;

public class CrudAuthTableConfig {

    private boolean isEmail;
    private boolean isSkipAuth;
    private String authTableName;
    private String usernameColumnName;
    private String passwordColumnName;

    // Getters and Setters

    public boolean isEmail() {
        return isEmail;
    }

    public void setEmail(boolean isEmail) {
        this.isEmail = isEmail;
    }

    public boolean isSkipAuth() {
        return isSkipAuth;
    }

    public void setSkipAuth(boolean isSkipAuth) {
        this.isSkipAuth = isSkipAuth;
    }

    public String getAuthTableName() {
        return authTableName;
    }

    public void setAuthTableName(String authTableName) {
        this.authTableName = authTableName;
    }

    public String getUsernameColumnName() {
        return usernameColumnName;
    }

    public void setUsernameColumnName(String usernameColumnName) {
        this.usernameColumnName = usernameColumnName;
    }

    public String getPasswordColumnName() {
        return passwordColumnName;
    }

    public void setPasswordColumnName(String passwordColumnName) {
        this.passwordColumnName = passwordColumnName;
    }
}
