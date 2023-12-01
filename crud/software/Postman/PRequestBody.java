package crud.software.Postman;

public class PRequestBody {

    private String propName;
    private String propDataType;
    private boolean isRequired;
    private String defaultValue;

    // Constructor
    public PRequestBody(String propName, String propDataType, boolean isRequired, String defaultValue) {
        this.propName = propName;
        this.propDataType = propDataType;
        this.isRequired = isRequired;
        this.defaultValue = defaultValue;
    }

    // Getters and Setters

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }

    public String getPropDataType() {
        return propDataType;
    }

    public void setPropDataType(String propDataType) {
        this.propDataType = propDataType;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
