package crud.software.Models;

import java.util.List;
import java.util.Map;

import crud.software.Postman.PostmanModel;

public class CodeInput<T> {

    private Map<String, T> finalDataDic;
    private String destinationFolder;
	private List<PostmanModel> postmanJson;

    // Getters and Setters

    public Map<String, T> getFinalDataDic() {
        return finalDataDic;
    }

    public void setFinalDataDic(Map<String, T> finalDataDic) {
        this.finalDataDic = finalDataDic;
    }

    public String getDestinationFolder() {
        return destinationFolder;
    }

    public void setDestinationFolder(String destinationFolder) {
        this.destinationFolder = destinationFolder;
    }
	
	public List<PostmanModel> getPostmanJson() {
        return postmanJson;
    }

    public void setPostmanJson(List<PostmanModel> postmanJson) {
        this.postmanJson = postmanJson;
    }
}
