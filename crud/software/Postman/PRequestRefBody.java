package crud.software.Postman;

public class PRequestRefBody {

    private String actualColumnName;
    private String actualTableName;
    private String propName;
    private String propDataType;
    private String refTableName;
    private String refColumnName;
    private boolean isRequired;
    private String defaultValue;

    // Constructor
    public PRequestRefBody(String actualColName, String actualTableName, String propName, 
                           String propDataType, String refColName, String refTableName, 
                           boolean isRequired, String defaultValue) {
        this.actualColumnName = actualColName;
        this.actualTableName = actualTableName;
        this.propName = propName;
        this.propDataType = propDataType;
        this.refTableName = refTableName;
        this.refColumnName = refColName;
        this.isRequired = isRequired;
        this.defaultValue = defaultValue;
    }

    // Getters and Setters

    public String getActualColumnName() {
        return actualColumnName;
    }

    public void setActualColumnName(String actualColumnName) {
        this.actualColumnName = actualColumnName;
    }

    public String getActualTableName() {
        return actualTableName;
    }

    public void setActualTableName(String actualTableName) {
        this.actualTableName = actualTableName;
    }

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

    public String getRefTableName() {
        return refTableName;
    }

    public void setRefTableName(String refTableName) {
        this.refTableName = refTableName;
    }

    public String getRefColumnName() {
        return refColumnName;
    }

    public void setRefColumnName(String refColumnName) {
        this.refColumnName = refColumnName;
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
