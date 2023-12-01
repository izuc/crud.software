package crud.software.Postman;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PostmanGenerator {

    public static String ItemTemplate = "{\"name\": \"{NameString}\", \"request\":{ \"auth\":{ \"type\": \"bearer\", \"bearer\": [ { \"key\": \"token\", \"value\": \"{{token}}\", \"type\": \"string\"}]},\"method\": \"{MethodString}\", \"header\": [ { \"key\": \"Content-Type\", \"name\": \"Content-Type\", \"value\": \"application/json\", \"type\": \"text\" } ], \"body\": { \"mode\": \"raw\", \"raw\": \"{BodyJson}\", \"options\": { \"raw\": { \"language\": \"json\" } } }, \"url\": { \"raw\": \"{{DEVServerURL}}{URLString}\", \"host\": [ \"{{DEVServerURL}}\" ], \"path\": {PathArray} } }, \"response\": [] }";

    public static String generatePostmanJson(List<PostmanModel> postmanModels) throws Exception {
        try {	
            StringBuilder stringBuilder = new StringBuilder();
            ObjectMapper mapper = new ObjectMapper();

            for (PostmanModel postmanModel : postmanModels) {
				String itemTemplate = ItemTemplate;

				itemTemplate = itemTemplate.replace("{NameString}", postmanModel.getName());
				itemTemplate = itemTemplate.replace("{MethodString}", postmanModel.getMethod());
				//itemTemplate = itemTemplate.replace("{URLString}", postmanModel.getUrl());
				itemTemplate = itemTemplate.replace("{PathArray}", mapper.writeValueAsString(postmanModel.generatePath(InterpolationType.PHP)));

                if ("post".equalsIgnoreCase(postmanModel.getMethod()) && postmanModel.getName().toLowerCase().contains("search")) {
                    StringBuilder bodyBuilder = new StringBuilder("[");
                    Set<PRequestBody> distinctBody = new HashSet<>(postmanModel.getBody());

                    for (PRequestBody item : distinctBody) {
                        bodyBuilder.append("{\\\"columnName\\\":\\\"\\\",");
                        bodyBuilder.append("\\\"columnLogic\\\":\\\"LIKE\\\",");
                        bodyBuilder.append("\\\"columnValue\\\":\\\"\\\"},");
                    }
                    String bodyJson = bodyBuilder.toString().substring(0, bodyBuilder.length() - 1) + "]";
                    itemTemplate = itemTemplate.replace("{BodyJson}", bodyJson);
                } else if (postmanModel.getBody() != null && !"get".equalsIgnoreCase(postmanModel.getMethod())) {
                    StringBuilder bodyBuilder = new StringBuilder("{");
                    Set<PRequestBody> distinctBody = new HashSet<>(postmanModel.getBody());

                    for (PRequestBody item : distinctBody) {
                        bodyBuilder.append("\\\"").append(item.getPropName()).append("\\\":\\\"\\\",");
                    }
                    String bodyJson = bodyBuilder.toString().substring(0, bodyBuilder.length() - 1) + "}";
                    itemTemplate = itemTemplate.replace("{BodyJson}", bodyJson);
                } else {
                    itemTemplate = itemTemplate.replace("{BodyJson}", "");
                }
                stringBuilder.append(itemTemplate).append(System.lineSeparator()).append(",");
            }

            return stringBuilder.toString().substring(0, stringBuilder.length() - 1);
        } catch (JsonProcessingException ex) {
            throw new Exception("Error processing JSON", ex);
        }
    }
}
