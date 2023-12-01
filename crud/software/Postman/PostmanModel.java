package crud.software.Postman;
import crud.software.Models.FinalQueryData;

import java.util.List;
import java.util.ArrayList;
import crud.software.Utility.*;

public class PostmanModel {

    private FinalQueryData finalQueryData;
    private String tableName;
    private String modelName;
    private String type;
    private List<PRequestBody> body;
    private String url;
    private String name;
    private List<String> host;
    private List<String> path;
    private String method;
	
	public PostmanModel() {
	}
	
	public PostmanModel(FinalQueryData finalQueryData, String tableName, String modelName, String type) {
        this.finalQueryData = finalQueryData;
        this.tableName = tableName;
        this.modelName = modelName;
        this.type = type;
	}
	
	public FinalQueryData getFinalQueryData() {
        return finalQueryData;
    }
	
	public String getModelName() {
        return modelName;
    }
	
	public String getType() {
        return type;
    }

    public List<PRequestBody> getBody() {
        return body;
    }

    public void setBody(List<PRequestBody> body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getHost() {
        return host;
    }

    public void setHost(List<String> host) {
        this.host = host;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
	
	public List<String> generatePath(InterpolationType type) {
		LanguageGenerator generator = new Interpolation(type);
        return generator.generatePath(this);
    }
	
    public boolean hasPrimaryKeys() {
        return finalQueryData != null &&
                finalQueryData.getSelectQueryData() != null &&
                finalQueryData.getSelectQueryData().getPrimaryKeys() != null &&
                !finalQueryData.getSelectQueryData().getPrimaryKeys().isEmpty();
    }

    public String getFirstPrimaryKeyName() {
        return finalQueryData.getSelectQueryData().getPrimaryKeys().get(0).getFieldName();
    }

    public String getFirstColumnName() {
        return finalQueryData.getSelectQueryData().getColumnList().get(0).getField();
    }
	
}

interface LanguageGenerator {
    List<String> generatePath(PostmanModel model);
}

class Interpolation implements LanguageGenerator {
	
	private final InterpolationType interpolationType;

    public Interpolation(InterpolationType interpolationType) {
        this.interpolationType = interpolationType;
    }
	
    @Override
    public List<String> generatePath(PostmanModel model) {
        List<String> dynamicPath = new ArrayList<>();
        dynamicPath.add(model.getTableName());

        switch (model.getType()) {
            case "add":
                model.setName(model.getModelName() + " - Add");
                model.setMethod("POST");
                break;
            case "update":
                model.setName(model.getModelName() + " - Update");
                model.setMethod("PUT");
                if (model.hasPrimaryKeys()) {
                    dynamicPath.add(format(model.getFirstPrimaryKeyName()));
                } else {
                    dynamicPath.add(format(model.getFirstColumnName()));
                }
                break;
            case "delete":
                model.setName(model.getModelName() + " - Delete");
                model.setMethod("DELETE");
                if (model.hasPrimaryKeys()) {
                    dynamicPath.add(format(model.getFirstPrimaryKeyName()));
                } else {
                    dynamicPath.add(format(model.getFirstColumnName()));
                }
                break;
            case "get":
                model.setName(model.getModelName() + " - GetAll");
                dynamicPath.add("?page=" + interpolationType.getPrefix() + "page" + interpolationType.getSuffix() + "&paginator=" + interpolationType.getPrefix() + "paginator" + interpolationType.getSuffix());
                model.setMethod("GET");
                break;
            case "getbyid":
                model.setName(model.getModelName() + " - GetById");
                if (model.hasPrimaryKeys()) {
                    dynamicPath.add(format(model.getFirstPrimaryKeyName()));
                } else {
                    dynamicPath.add(format(model.getFirstColumnName()));
                }
                model.setMethod("GET");
                break;
            case "search":
                model.setName(model.getModelName() + " - Search");
                dynamicPath.add("search");
                dynamicPath.add(interpolationType.getPrefix() + "searchKey" + interpolationType.getSuffix());
                dynamicPath.add("?page=" + interpolationType.getPrefix() + "page" + interpolationType.getSuffix() + "&paginator=" + interpolationType.getPrefix() + "paginator" + interpolationType.getSuffix());
                model.setMethod("GET");
                break;
        }

        return dynamicPath;
    }
	
	
	private String format(String column) {
		return interpolationType.getPrefix() + (interpolationType.isCamelCase() ? Helper.toCamelCase(column) : column ) + interpolationType.getSuffix();
	}
}
