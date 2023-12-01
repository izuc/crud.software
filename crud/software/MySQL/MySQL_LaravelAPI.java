package crud.software.MySQL;

import java.util.UUID;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.ArrayList;
import java.text.Collator;
import java.util.List;
import java.util.Locale;
import java.io.StringWriter;
import java.io.PrintWriter;

import crud.software.Models.FKColumnClass;
import crud.software.Models.ColumnModel;
import crud.software.Models.CrudConfiguration;
import crud.software.Models.CrudMessage;
import crud.software.Models.FinalQueryData;
import crud.software.Models.InsertUpdateClass;
import crud.software.Models.InsertUpdateQueryData;
import crud.software.Models.PrimaryKeyClass;
import crud.software.Models.CodeInput;
import crud.software.Models.SelectQueryData;
import crud.software.Postman.PRequestBody;
import crud.software.Postman.PostmanGenerator;
import crud.software.Postman.PostmanModel;
import crud.software.Utility.CopyDir;
import crud.software.Utility.Helper;

public class MySQL_LaravelAPI {
	private List<PostmanModel> postmanJson = new ArrayList<>();
    private Collator ti = Collator.getInstance(Locale.getDefault());
	private ArrayList<CrudMessage> messages;
    private String templateFolder;
    private String templateFolderSeparator;
    private String destinationFolderSeparator;
    private String projectName;
	private String projectFolder;
    private List<Exception> exceptionList;
    private List<String> selectedTable;
    private CrudConfiguration configApp;
    private boolean isMultiTenant;
    private MySQLDBHelper mysqlDB;

    public MySQL_LaravelAPI(CrudConfiguration config, boolean isMultiTenant, String destinationFolderSeparator) {
        this.isMultiTenant = isMultiTenant;
        this.configApp = config;
        this.exceptionList = new ArrayList<>();
        this.templateFolder = "LaravelAPITemplate";
        this.templateFolderSeparator = "\\\\";
        this.destinationFolderSeparator = destinationFolderSeparator;
		this.messages = new ArrayList<CrudMessage>();
    }
	
	private void logMessage(String message) {
		logMessage(message, true);
	}

	private void logMessage(String message, boolean isSuccess) {
		if (this.messages != null) {
			this.messages.add(new CrudMessage(message, isSuccess));
		}
	}
	
	private String createTemplatePath(String filePathString) {
		String text = templateFolder;
		String[] array = filePathString.split(",");
		for (String text2 : array) {
			if (text2 != null && !text2.isEmpty()) {
				text = text + templateFolderSeparator + text2;
			}
		}
		return text;
	}
	
	private String createDestinationPath(String filePathString) {
		String text = getDestinationFolder();
		String[] array = filePathString.split(",");
		for (String text2 : array) {
			if (text2 != null && !text2.isEmpty()) {
				text = text + destinationFolderSeparator + text2;
			}
		}
		File file = new File(text);
		if (file.exists()) {
			file.delete();
		}
		return text;
	}

	public CodeInput<FinalQueryData> automator(String projectName, List<String> selectedTable, MySQLDBHelper mySQLDB) throws Exception {
		mysqlDB = mySQLDB;
		this.projectName = projectName;
		this.selectedTable = selectedTable;
		String text = createDirectory();
		logMessage("Project Folder Created : " + text);
		logMessage("Generating Larvel Project...");
		logMessage("Copying Project File, Might take some time...");
		copyProject();
		logMessage("Finished Copying Project File");
		logMessage("Analyzing Database...");
		CodeInput<FinalQueryData> codeInput = new CodeInput<>();
		codeInput.setDestinationFolder(text);
		codeInput.setFinalDataDic(new HashMap<>());
		codeInput.setPostmanJson(new ArrayList<>());

		for (String item : selectedTable) {
			try {
				logMessage("Processing for Table => " + item);
				String modelName = Helper.toPascalCase(item);
				InsertUpdateQueryData insertUpdateQueryData = mysqlDB.getInsertUpdateQueryData(item);
				FinalQueryData finalQueryData = mysqlDB.buildLaravelQuery(item);
				finalQueryData.setInsertUpdateQueryData(insertUpdateQueryData); // Make sure this setter exists in your FinalQueryData class
				codeInput.getFinalDataDic().put(item, finalQueryData);
				if (item.equals(getConfigApp().getAuthTableConfig().getAuthTableName())) {
					generateAuthModels(item, insertUpdateQueryData);
				} else {
					generateModels(item, insertUpdateQueryData, finalQueryData);
				}
				generateController(item, insertUpdateQueryData, finalQueryData);
				
				postmanJson.add(new PostmanModel(finalQueryData, item, modelName, "add"));
				postmanJson.add(new PostmanModel(finalQueryData, item, modelName, "update"));
				postmanJson.add(new PostmanModel(finalQueryData, item, modelName, "get"));
				postmanJson.add(new PostmanModel(finalQueryData, item, modelName, "getbyid"));
				postmanJson.add(new PostmanModel(finalQueryData, item, modelName, "search"));
				postmanJson.add(new PostmanModel(finalQueryData, item, modelName, "delete"));
				
			} catch (Exception ex) {
				logMessage("Exception on table " + item + " - " + ex.getMessage(), false);
				StringWriter sw = new StringWriter();
				ex.printStackTrace(new PrintWriter(sw));
				String exceptionAsString = sw.toString();
				logMessage(exceptionAsString, false);
			}
		}

		generateAuthFile(getConfigApp().getAuthTableConfig().getAuthTableName());
		logMessage("JWT Token Auth generated");
		InsertUpdateQueryData insertUpdateQueryData2 = mysqlDB.getInsertUpdateQueryData(getConfigApp().getAuthTableConfig().getAuthTableName());
		generateAuthController(getConfigApp().getAuthTableConfig().getAuthTableName(), insertUpdateQueryData2);
		generateRoutes();
		logMessage("Routes created");
		createPostmanJsonFile(postmanJson, insertUpdateQueryData2);
		logMessage("Postman import collection file generated");
		codeInput.setPostmanJson(postmanJson);
		generateEnvFile();
		logMessage("Env file created");
		generateDatabaseFile();
		logMessage("Database file created");
		logMessage("----- Laravel API Generated -----");
		logMessage("Please check the generated code at : " + text);
		return codeInput;
	}
	
	// Getters and Setters

    public Collator getTi() {
        return ti;
    }

    public void setTi(Collator ti) {
        this.ti = ti;
    }

    public ArrayList<CrudMessage> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<CrudMessage> messages) {
        this.messages = messages;
    }

    public String getTemplateFolder() {
        return templateFolder;
    }

    public void setTemplateFolder(String templateFolder) {
        this.templateFolder = templateFolder;
    }

    public String getTemplateFolderSeparator() {
        return templateFolderSeparator;
    }

    public void setTemplateFolderSeparator(String templateFolderSeparator) {
        this.templateFolderSeparator = templateFolderSeparator;
    }

    public String getDestinationFolderSeparator() {
        return destinationFolderSeparator;
    }

    public void setDestinationFolderSeparator(String destinationFolderSeparator) {
        this.destinationFolderSeparator = destinationFolderSeparator;
    }

    public String getDestinationFolder() {
        return getProjectFolder() + "\\LaravelAPI";
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<Exception> getExceptionList() {
        return exceptionList;
    }

    public void setExceptionList(List<Exception> exceptionList) {
        this.exceptionList = exceptionList;
    }

    public List<String> getSelectedTable() {
        return selectedTable;
    }

    public void setSelectedTable(List<String> selectedTable) {
        this.selectedTable = selectedTable;
    }

    public CrudConfiguration getConfigApp() {
        return configApp;
    }

    public void setConfigApp(CrudConfiguration configApp) {
        this.configApp = configApp;
    }

    public boolean isMultiTenant() {
        return isMultiTenant;
    }

    public void setMultiTenant(boolean isMultiTenant) {
        this.isMultiTenant = isMultiTenant;
    }

    public MySQLDBHelper getMysqlDB() {
        return mysqlDB;
    }

    public void setMysqlDB(MySQLDBHelper mysqlDB) {
        this.mysqlDB = mysqlDB;
    }
	
	public void setProjectFolder(String projectFolder) {
		this.projectFolder = projectFolder;
	}
	
	public String getProjectFolder() {
		return this.projectFolder;
	}
	
	public String createDirectory() {
		String projectName = getProjectName();
		String text = projectName;
		if (new File(projectName).exists()) {
			int num = 0;
			do {
				num++;
				text = String.format("%s(%d)", projectName, num);
			} while (new File(text).exists());
		}
		projectName = text;
		setProjectFolder(projectName);
		new File(text).mkdirs();
		return projectName;
	}

	public void copyProject() {
		String sourceDirectory = getTemplateFolder() + "\\\\LaravelProject";
		new File(getDestinationFolder() + "\\POSTMAN_IMPORT_FILE").mkdirs();
		CopyDir.copy(sourceDirectory, getDestinationFolder(), getProjectName().toLowerCase(), "crud.LaravelProject");
		new File(getDestinationFolder() + "\\app\\Models").mkdirs();
		new File(getDestinationFolder() + "\\app\\Http\\Controllers\\Api\\Auth").mkdirs();
	}

	public void generateRoutes() {
		String path = createDestinationPath("routes,api.php");
		if (new File(path).exists()) {
			new File(path).delete();
		}
		StringBuilder text = new StringBuilder();
		StringBuilder useStatements = new StringBuilder();
		
		for (String item : getSelectedTable()) {
			String controllerName = Helper.toPascalCase(item) + "Controller";
			String controllerClass = "App\\Http\\Controllers\\Api\\Auth\\" + controllerName;
			
			useStatements.append("use ").append(controllerClass).append(";\n");
			
			text.append("\nRoute::get('/").append(item).append("', [").append(controllerName).append("::class, 'index'])->name('api.auth.index.").append(item).append("');");
			text.append("\nRoute::get('/").append(item).append("/{id}', [").append(controllerName).append("::class, 'show'])->name('api.auth.show.").append(item).append("');");
			text.append("\nRoute::post('/").append(item).append("', [").append(controllerName).append("::class, 'store'])->name('api.auth.store.").append(item).append("');");
			text.append("\nRoute::put('/").append(item).append("/{id}', [").append(controllerName).append("::class, 'update'])->name('api.auth.update.").append(item).append("');");
			text.append("\nRoute::delete('/").append(item).append("/{id}', [").append(controllerName).append("::class, 'destroy'])->name('api.auth.delete.").append(item).append("');");
			text.append("\nRoute::get('/").append(item).append("/search/{search}', [").append(controllerName).append("::class, 'search'])->name('api.auth.search.").append(item).append("');");
		}
		
		try {
			String text3 = Files.readString(Paths.get(createTemplatePath("api.txt")));
			text3 = text3.replace("{apiRoutes}", text.toString());
			text3 = text3.replace("{apiUses}", useStatements.toString());
			CopyDir.writeWithoutBOM(path, text3);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateAuthFile(String authTable) {
		String newValue = ti.getCollationKey(authTable).getSourceString(); // assuming `ti` is a Collator
		String path = createDestinationPath("config,auth.php");
		String text = "";
		try {
			text = Files.readString(Paths.get(createTemplatePath("auth.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		text = text.replace("{userModelName}", Helper.toPascalCase(newValue));
		try {
			CopyDir.writeWithoutBOM(path, text);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void generateEnvFile() {
		String path = createDestinationPath(".env");
		String text = "";
		try {
			text = Files.readString(Paths.get(createTemplatePath("env.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		text = text.replace("{projectName}", getProjectName())
				   .replace("{dbhost}", mysqlDB.getHost())
				   .replace("{dbport}", String.valueOf(mysqlDB.getPort()))
				   .replace("{dbname}", mysqlDB.getDBName())
				   .replace("{dbusername}", mysqlDB.getUsername())
				   .replace("{dbpassword}", mysqlDB.getPassword());
				   
		try {
			CopyDir.writeWithoutBOM(path, text);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void generateDatabaseFile() {
		String path = createDestinationPath("config,database.php");
		String text = "";
		try {
			text = Files.readString(Paths.get(createTemplatePath("database.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		text = text.replace("{projectName}", getProjectName())
				   .replace("{dbhost}", mysqlDB.getHost())
				   .replace("{dbport}", String.valueOf(mysqlDB.getPort()))
				   .replace("{dbname}", mysqlDB.getDBName())
				   .replace("{dbusername}", mysqlDB.getUsername())
				   .replace("{dbpassword}", mysqlDB.getPassword());

		try {
			CopyDir.writeWithoutBOM(path, text);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void generateModels(String tableName, InsertUpdateQueryData data, FinalQueryData finalData) {
		String text = Helper.toPascalCase(tableName);
		createDestinationPath("app,Models,model.txt");
		String path = createDestinationPath("app,Models," + text + ".php");
		StringBuilder text2 = new StringBuilder();
		StringBuilder text3 = new StringBuilder();
		StringBuilder text4 = new StringBuilder();
		
		String text5 = "    public function $fkTableNameFun()\n    {\n        return $this->hasOne($FKTableName::class,'$FKey','$LocalKey')->select(['$FKey','$FieldName']);\n    }";
		
		if (finalData != null && finalData.getSelectQueryData() != null && finalData.getSelectQueryData().getFKColumnData() != null) {
			for (FKColumnClass fKColumnDatum : finalData.getSelectQueryData().getFKColumnData()) {
				if (!text4.toString().contains(fKColumnDatum.getTableName2()) && fKColumnDatum.getFieldName2() != null) {
					String text6 = text5;
					text6 = text6.replace("$fkTableNameFun", fKColumnDatum.getTableName2())
								 .replace("$FKTableName", Helper.toPascalCase(fKColumnDatum.getTableName2()))
								 .replace("$FKey", fKColumnDatum.getFieldName1())
								 .replace("$FieldName", fKColumnDatum.getFieldName2())
								 .replace("$LocalKey", fKColumnDatum.getLocalField());
					text4.append("\n").append(text6);
				}
			}
		}
		
		for (InsertUpdateClass insertColumn : data.getInsertColumnList()) {
			text3.append("'").append(insertColumn.getFieldName()).append("',");
		}
		
		List<ColumnModel> list = data.getColumnList().stream()
									.filter(i -> "CURRENT_TIMESTAMP".equals(i.getDefaultValue()) || i.getTypeName().toLowerCase().contains("timestamp"))
									.collect(Collectors.toList());
		
		for (ColumnModel item : list) {
			text2.append("\n'").append(item.getField()).append("' => 'datetime',");
		}
		
		for (ColumnModel column : data.getColumnList()) {
			if ("boolean".equals(Helper.getDataTypePHP(column.getTypeName()))) {
				text2.append("\n'").append(column.getField()).append("' => 'boolean',");
			}
		}
		
		String empty = "";
		if (data.getPrimaryKeys() != null && data.getPrimaryKeys().size() > 1) {
			empty = "'" + data.getPrimaryKeys().get(0).getFieldName() + "'";
		} else if (data.getPrimaryKeys() == null || data.getPrimaryKeys().size() != 1) {
			empty = "'id'";
		} else {
			empty = "'" + data.getPrimaryKeys().get(0).getFieldName() + "'";
		}
		
		String text7 = "";
		try {
			text7 = Files.readString(Paths.get(createTemplatePath("model.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		text7 = text7.replace("{requiredProperty}", text3.toString().substring(0, text3.length() - 1))
					 .replace("{castProperty}", text2.toString())
					 .replace("{foreignKeyRelationship}", text4.toString())
					 .replace("{modelName}", text)
					 .replace("{tableName}", tableName)
					 .replace("{primaryKey}", empty);
		
		try {
			CopyDir.writeWithoutBOM(path, text7);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void generateAuthModels(String tableName, InsertUpdateQueryData data) {
		String text = Helper.toPascalCase(tableName);
		String path = createDestinationPath("app,Models," + text + ".php");
		StringBuilder text2 = new StringBuilder();
		StringBuilder text3 = new StringBuilder();
		String newValue = "'" + getConfigApp().getAuthTableConfig().getPasswordColumnName() + "'";
		
		List<ColumnModel> list = data.getColumnList().stream()
									.filter(i -> "CURRENT_TIMESTAMP".equals(i.getDefaultValue()))
									.collect(Collectors.toList());

		for (ColumnModel item : list) {
			text2.append("\n'").append(item.getField()).append("' => 'datetime',");
		}
		
		for (InsertUpdateClass insertColumn : data.getInsertColumnList()) {
			text3.append("'").append(insertColumn.getFieldName()).append("',");
		}
		
		String empty = "";
		if (data.getPrimaryKeys() != null && data.getPrimaryKeys().size() > 1) {
			empty = "'" + data.getPrimaryKeys().get(0).getFieldName() + "'";
		} else if (data.getPrimaryKeys() == null || data.getPrimaryKeys().size() != 1) {
			empty = "'id'";
		} else {
			empty = "'" + data.getPrimaryKeys().get(0).getFieldName() + "'";
		}
		
		for (ColumnModel column : data.getColumnList()) {
			if ("boolean".equals(Helper.getDataTypePHP(column.getTypeName()))) {
				text2.append("\n'").append(column.getField()).append("' => 'boolean',");
			}
		}
		
		String text4 = "";
		try {
			text4 = Files.readString(Paths.get(createTemplatePath("authmodel.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		text4 = text4.replace("{requiredProperty}", text3.toString().substring(0, text3.length() - 1))
					 .replace("{castProperty}", text2.toString())
					 .replace("{hiddenProperty}", newValue)
					 .replace("{foreignKeyRelationship}", "")
					 .replace("{tableName}", tableName)
					 .replace("{modelName}", text)
					 .replace("{primaryKey}", empty)
					 .replace("{passwordColumn}", getConfigApp().getAuthTableConfig().getPasswordColumnName());
		
		try {
			CopyDir.writeWithoutBOM(path, text4);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void generateController(String tableName, InsertUpdateQueryData data, FinalQueryData finalData) {
		String text = Helper.toPascalCase(tableName);
		String path = createDestinationPath("app,Http,Controllers,Api,Auth," + text + "Controller.php");
		
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}

		StringBuilder text2 = new StringBuilder();
		for (InsertUpdateClass updateColumn : data.getUpdateColumnList()) {
			text2.append("$").append(tableName).append("->").append(updateColumn.getFieldName()).append(" = $input['").append(updateColumn.getFieldName()).append("'];");
		}

		String empty = "";
		if (data.getPrimaryKeys() != null && data.getPrimaryKeys().size() > 1) {
			empty = data.getPrimaryKeys().get(0).getFieldName();
		} else if (data.getPrimaryKeys() == null || data.getPrimaryKeys().size() != 1) {
			empty = "id";
		} else {
			empty = data.getPrimaryKeys().get(0).getFieldName();
		}

		StringBuilder text3 = new StringBuilder();
		for (ColumnModel column : data.getColumnList()) {
			text3.append("'").append(column.getField()).append("',");
		}

		StringBuilder text4 = new StringBuilder();
		List<String> list = new ArrayList<>();
		String newValue = "::";

		if (finalData != null && finalData.getSelectQueryData() != null && finalData.getSelectQueryData().getFKColumnData() != null) {
			for (FKColumnClass fKColumnDatum : finalData.getSelectQueryData().getFKColumnData()) {
				if (!text4.toString().contains(fKColumnDatum.getTableName2()) && fKColumnDatum.getFieldName2() != null) {
					list.add("'" + fKColumnDatum.getTableName2() + "'");
				}
			}
		}

		if (!list.isEmpty()) {
			text4.append(text).append("::with([").append(String.join(",", list)).append("])->paginate($request->paginator)");
			newValue = "::with([" + String.join(",", list) + "])->";
		} else {
			text4.append(text).append("::paginate($request->paginator, ['*'], 'page', $request->page)");
		}

		String text5 = "";
		try {
			text5 = Files.readString(Paths.get(createTemplatePath("controller.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		text5 = text5.replace("{requiredProperty}", text3.substring(0, text3.length() - 1))
					 .replace("{tableName}", tableName)
					 .replace("{updateProperty}", text2.toString())
					 .replace("{modelName}", text)
					 .replace("{primaryKey}", empty)
					 .replace("{SelectAllQuery}", text4.toString())
					 .replace("{withOperator}", newValue);
		
		try {
			CopyDir.writeWithoutBOM(path, text5);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void generateAuthController(String tableName, InsertUpdateQueryData data) {
		String newValue = Helper.toPascalCase(tableName);
		String path = createDestinationPath("app,Http,Controllers,AuthController.php");
		
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}

		StringBuilder text = new StringBuilder();
		String newValue2 = "'password' => 'required|string|min:6',";
		String text2 = "";
		text2 = !getConfigApp().getAuthTableConfig().isEmail() ? ("'" + getConfigApp().getAuthTableConfig().getUsernameColumnName() + "' => 'required|string|min:6',")
				 : ("'" + getConfigApp().getAuthTableConfig().getUsernameColumnName() + "' => 'required|email',");
				 
		for (ColumnModel column : data.getColumnList()) {
			if (!"auto_increment".equals(column.getExtra()) && !"CURRENT_TIMESTAMP".equals(column.getDefaultValue())) {
				String text3 = "";
				if ("NO".equals(column.getIsNull())) {
					text3 = "required|";
				}
				if (column.getField().equals(getConfigApp().getAuthTableConfig().getUsernameColumnName())) {
					if (!getConfigApp().getAuthTableConfig().isEmail()) {
						text.append("\n'").append(column.getField()).append("' => '").append(text3).append("string|between:2,100',");
					} else {
						text.append("\n'").append(column.getField()).append("' => '").append(text3).append("string|email|max:100',");
					}
				} else if (column.getField().equals(getConfigApp().getAuthTableConfig().getPasswordColumnName())) {
					text.append("\n'").append(getConfigApp().getAuthTableConfig().getPasswordColumnName()).append("' => '").append(text3).append("string|confirmed|min:6',");
				} else {
					String dataTypePHP = Helper.getDataTypePHP(column.getTypeName());
					if (!"datetime".equalsIgnoreCase(dataTypePHP)) {
						text.append("\n'").append(column.getField()).append("' => '").append(text3).append(dataTypePHP).append("',");
					} else {
						text.append("\n'").append(column.getField()).append("' => '").append(text3).append("date_format:Y-m-d H:i:s',");
					}
				}
			}
		}

		String text4 = "";
		try {
			text4 = Files.readString(Paths.get(createTemplatePath("authcontroller.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		text4 = text4.replace("{tableName}", tableName)
					 .replace("{modelName}", newValue)
					 .replace("{usernameValidation}", text2)
					 .replace("{requiredProperty}", text.toString())
					 .replace("{passwordValidation}", newValue2)
					 .replace("{userColumn}", getConfigApp().getAuthTableConfig().getUsernameColumnName())
					 .replace("{passwordColumn}", getConfigApp().getAuthTableConfig().getPasswordColumnName());
		
		try {
			CopyDir.writeWithoutBOM(path, text4);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public PostmanModel createPostmanJson(String tableName, String modelName, String type, FinalQueryData data) {
		PostmanModel postmanModel = new PostmanModel();
		List<String> list = new ArrayList<>();
		List<PRequestBody> list2 = new ArrayList<>();
		
		list.add(tableName);
		postmanModel.setTableName(tableName);
		
		switch (type) {
			case "add":
				postmanModel.setName(modelName + " - Add");
				postmanModel.setMethod("POST");
				break;
			case "update":
				postmanModel.setName(modelName + " - Update");
				postmanModel.setMethod("PUT");
				if (data.getSelectQueryData().getPrimaryKeys() != null && data.getSelectQueryData().getPrimaryKeys().size() > 1) {
					list.add("${" + data.getSelectQueryData().getPrimaryKeys().get(0).getFieldName() + "}");
				} else if (data.getSelectQueryData().getPrimaryKeys() != null && data.getSelectQueryData().getPrimaryKeys().size() == 1) {
					list.add("${" + data.getSelectQueryData().getPrimaryKeys().get(0).getFieldName() + "}");
				} else {
					list.add("${" + data.getSelectQueryData().getColumnList().get(0).getField() + "}");
				}
				break;
			case "delete":
				postmanModel.setName(modelName + " - Delete");
				postmanModel.setMethod("DELETE");
				if (data.getSelectQueryData().getPrimaryKeys() != null && data.getSelectQueryData().getPrimaryKeys().size() > 1) {
					list.add("${" + data.getSelectQueryData().getPrimaryKeys().get(0).getFieldName() + "}");
				} else if (data.getSelectQueryData().getPrimaryKeys() != null && data.getSelectQueryData().getPrimaryKeys().size() == 1) {
					list.add("${" + data.getSelectQueryData().getPrimaryKeys().get(0).getFieldName() + "}");
				} else {
					list.add("${" + data.getSelectQueryData().getColumnList().get(0).getField() + "}");
				}
				break;
			case "get":
				postmanModel.setName(modelName + " - GetAll");
				list.add("?page=1&paginator=100");
				postmanModel.setMethod("GET");
				break;
			case "getbyid":
				postmanModel.setName(modelName + " - GetById");
				if (data.getSelectQueryData().getPrimaryKeys() != null && data.getSelectQueryData().getPrimaryKeys().size() > 1) {
					list.add("${" + data.getSelectQueryData().getPrimaryKeys().get(0).getFieldName() + "}");
				} else if (data.getSelectQueryData().getPrimaryKeys() != null && data.getSelectQueryData().getPrimaryKeys().size() == 1) {
					list.add("${" + data.getSelectQueryData().getPrimaryKeys().get(0).getFieldName() + "}");
				} else {
					list.add("${" + data.getSelectQueryData().getColumnList().get(0).getField() + "}");
				}
				postmanModel.setMethod("GET");
				break;
			case "search":
				postmanModel.setName(modelName + " - Search");
				list.add("search");
				list.add("${searchKey}");
				list.add("?page=1&paginator=30");
				postmanModel.setMethod("GET");
				break;
		}
		
		postmanModel.setPath(list);
		
		for (ColumnModel c : data.getSelectQueryData().getColumnList()) {
			if (!"auto_increment".equals(c.getExtra()) && !"CURRENT_TIMESTAMP".equals(c.getDefaultValue()) && list2.stream().noneMatch(i -> i.getPropName().equals(c.getField()))) {
				list2.add(new PRequestBody(c.getField(), c.getTypeName(), "NO".equals(c.getIsNull()), c.getDefaultValue()));
			}
		}
		
		if ("update".equals(type) || "add".equals(type)) {
			if (data.getSelectQueryData().getPrimaryKeys() != null) {
				for (PrimaryKeyClass pkey : data.getSelectQueryData().getPrimaryKeys()) {
					if (list2.stream().noneMatch(i -> i.getPropName().equals(pkey.getFieldName()))) {
						list2.add(new PRequestBody(pkey.getFieldName(), pkey.getDataType(), false, null));
					}
				}
			} else {
				ColumnModel c = data.getSelectQueryData().getColumnList().get(0);
				if (list2.stream().noneMatch(i -> i.getPropName().equals(c.getField()))) {
					list2.add(new PRequestBody(c.getField(), c.getTypeName(), "NO".equals(c.getIsNull()), c.getDefaultValue()));
				}
			}
		}

		postmanModel.setBody(list2);
		return postmanModel;
	}
	
	public void createPostmanJsonFile(List<PostmanModel> postmanModels, InsertUpdateQueryData authTableData) {
		try {
			String newValue = PostmanGenerator.generatePostmanJson(postmanModels);
			String path = createDestinationPath("POSTMAN_IMPORT_FILE,postman_import_file.json");
			String path2 = createDestinationPath("POSTMAN_IMPORT_FILE,EnvironmentVariable.postman_environment.json");
			List<PRequestBody> list = new ArrayList<>();

			for (InsertUpdateClass insertColumn : authTableData.getInsertColumnList()) {
				list.add(new PRequestBody(insertColumn.getFieldName(), insertColumn.getDataType(), false, null));
			}
			list.add(new PRequestBody(getConfigApp().getAuthTableConfig().getPasswordColumnName() + "_confirmation", "string", false, null));

			StringBuilder text = new StringBuilder("{");
			for (PRequestBody item : list) {
				text.append("\\\"").append(item.getPropName()).append("\\\":\\\"\\\",");
			}
			text = new StringBuilder(text.substring(0, text.length() - 1)); // Trim the last comma
			text.append("}");
			String text2 = new String(Files.readAllBytes(Paths.get(createTemplatePath("PostmanJson,Postmanjson_template.txt"))), StandardCharsets.UTF_8);

			text2 = text2.replace("{newGuid}", UUID.randomUUID().toString());
			text2 = text2.replace("{projectName}", getProjectName());  // Assuming projectName is a class member
			text2 = text2.replace("{itemListString}", newValue);
			text2 = text2.replace("{userRequiredProperty}", text.toString());
			CopyDir.writeWithoutBOM(path, text2);

			String text3 = new String(Files.readAllBytes(Paths.get(createTemplatePath("PostmanJson,EnvironmentVariable.postman_environment.txt"))), StandardCharsets.UTF_8);
			text3 = text3.replace("{projectName}", getProjectName());
			CopyDir.writeWithoutBOM(path2, text3);
		} catch (Exception ex) {
			logMessage("Exception encountered: " + ex.getMessage(), false);
			exceptionList.add(ex);  // Assuming exceptionList is a class member
		}
	}

}
