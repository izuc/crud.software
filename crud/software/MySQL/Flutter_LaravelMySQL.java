package crud.software.MySQL;

import java.nio.charset.StandardCharsets;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import crud.software.Models.*;
import crud.software.Postman.*;
import crud.software.Utility.*;


public class Flutter_LaravelMySQL {

    private ArrayList<CrudMessage> messages;

    private List<Exception> exList;
    private Map<String, List<CodeRepo>> flutterRepoDic;
    private Map<String, FinalQueryData> finalDataDic;

    private String templateFolder;
    private String templateFolderSeparator;
    private String destinationFolderSeparator;
    private String destinationFolder;
    private String projectName;

    public Flutter_LaravelMySQL(String projectName, String destinationFolder, String destinationFolderSeparator) {
        this.destinationFolder = destinationFolder;
        this.projectName = projectName;
        this.exList = new ArrayList<>();
        this.templateFolder = "FlutterTemplate";
        this.templateFolderSeparator = "\\";
        this.destinationFolderSeparator = destinationFolderSeparator;
        this.flutterRepoDic = new HashMap<>();
        this.exList = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public List<Exception> getExList() {
        return exList;
    }

    public void setExList(List<Exception> exList) {
        this.exList = exList;
    }

    public Map<String, List<CodeRepo>> getCodeRepoDic() {
        return flutterRepoDic;
    }

    public void setCodeRepoDic(Map<String, List<CodeRepo>> flutterRepoDic) {
        this.flutterRepoDic = flutterRepoDic;
    }

    public Map<String, FinalQueryData> getFinalDataDic() {
        return finalDataDic;
    }

    public void setFinalDataDic(Map<String, FinalQueryData> finalDataDic) {
        this.finalDataDic = finalDataDic;
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
        return destinationFolder;
    }

    public void setDestinationFolder(String destinationFolder) {
        this.destinationFolder = destinationFolder;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public ArrayList<CrudMessage> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<CrudMessage> messages) {
        this.messages = messages;
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
		String path = templateFolder;
		String[] parts = filePathString.split(",");

		for (String part : parts) {
			if (part != null && !part.trim().isEmpty()) {
				path = path + templateFolderSeparator + part;
			}
		}
		
		return path;
	}
	
	private String createDestinationPath(String filePathString) {
		String path = destinationFolder;
		String[] parts = filePathString.split(",");

		for (String part : parts) {
			if (part != null && !part.trim().isEmpty()) {
				path = path + destinationFolderSeparator + part;
			}
		}

		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		
		return path;
	}
	
	private String getDestinationPath(String filePathString) {
		String path = destinationFolder;
		String[] parts = filePathString.split(",");
		
		for (String part : parts) {
			if (part != null && !part.trim().isEmpty()) {
				path = path + destinationFolderSeparator + part;
			}
		}
		
		return path;
	}

	private String createDirectory() {
		destinationFolder += "/FlutterAPP";
		
		try {
			Path path = Paths.get(destinationFolder);
			if (!Files.exists(path)) {
				Files.createDirectories(path);
			}

			// Assuming CopyDir is a utility class you have in Java that performs the directory copy operation.
			CopyDir.copy(createTemplatePath("flutter-admin-dashboard"), destinationFolder, projectName, "[projectName]");
		} catch (Exception e) {
			// You might want to handle exceptions more gracefully.
			e.printStackTrace();
		}
		
		return destinationFolder;
	}
	
	public void createRepoDictionary(List<PostmanModel> postmanModels) {
		Map<String, List<CodeRepo>> dictionary = new HashMap<>();
		List<CodeRepo> list = new ArrayList<>();
		String previousTableName = "";

		for (PostmanModel item : postmanModels.stream().sorted(Comparator.comparing(PostmanModel::getTableName)).collect(Collectors.toList())) {
			try {
				String tableName = item.getTableName();
				if (!previousTableName.equals(tableName)) {
					list = new ArrayList<>();
					previousTableName = tableName;
					dictionary.put(tableName, list);
				}

				String titleCasedName = Helper.toPascalCase(tableName);
				StringBuilder path = new StringBuilder();
				StringBuilder queryString = new StringBuilder();
				CodeRepo flutterRepo = new CodeRepo();

				for (String part : item.generatePath(InterpolationType.DART)) {
					if (part.startsWith(InterpolationType.DART.getPrefix())) {
						String paramName = part.substring(1);
						if (!part.contains("?")) {
							flutterRepo.addPathParam(Helper.toCamelCase(paramName));
							path.append("/").append("$").append(paramName);
						} else {
							flutterRepo.getGetList().add(new PRequestBody(paramName, "String", false, null).toString());
						}
					} else if (part.contains("?")) {
						String[] splitPart = part.split("\\?");
						path.append("/").append(splitPart[0]);
						String[] params = splitPart[1].split("&");
						for (String param : params) {
							if (param.startsWith(InterpolationType.DART.getPrefix())) {
								String paramName = param.substring(1);
								flutterRepo.addPathParam(Helper.toCamelCase(paramName));
							} else {
								String[] keyValue = param.replace("?", "").split("=");
								queryString.append("&").append(keyValue[0]).append("=").append(keyValue[1]);
								if (keyValue[1].startsWith(InterpolationType.DART.getPrefix())) {
									flutterRepo.addPathParam(Helper.toCamelCase(keyValue[1].substring(1)));
								}
								flutterRepo.getGetList().add(new PRequestBody(keyValue[0], "String", false, null).toString());
							}
						}
					} else {
						path.append("/").append(part);
					}
				}

				if (queryString.length() > 0) {
					if (path.toString().endsWith("/")) {
						path = new StringBuilder(path.substring(0, path.length() - 1));
					}
					path.append("?").append(queryString.substring(1));
				}
				
				flutterRepo.setMethodName(item.getMethod().toLowerCase());
				flutterRepo.setFinalURL(path.toString());

				if (item.getName().toLowerCase().contains("- getall")) {
					flutterRepo.setFunctionType("getall");
					flutterRepo.setFunctionName("getAll" + titleCasedName);
					flutterRepo.setFinalURL(path.toString());
				} else if (item.getName().toLowerCase().contains("- getbyid")) {
					flutterRepo.setFunctionType("getbyid");
					flutterRepo.setFunctionName("getOne" + titleCasedName);
					flutterRepo.setFinalURL(path.toString());
				} else if (item.getName().toLowerCase().contains("- search") && dictionary.get(tableName).stream().filter(i -> "search".equals(i.getFunctionType())).findFirst().orElse(null) == null) {
					flutterRepo.setFunctionType("search");
					flutterRepo.setFunctionName("search" + titleCasedName);
					flutterRepo.setFinalURL(path.toString());
				} else if (item.getName().toLowerCase().contains("- delete")) {
					flutterRepo.setFunctionType("delete");
					flutterRepo.setFunctionName("delete" + titleCasedName);
					flutterRepo.setFinalURL(path.toString());
				} else if (item.getName().toLowerCase().contains("- add")) {
					flutterRepo.setPostParamList(new ArrayList<>());
					flutterRepo.getPostParamList().add(new PRequestBody("data", "{ModelName}", false, null));
					flutterRepo.setFunctionType("create");
					flutterRepo.setFunctionName("create" + titleCasedName);
					flutterRepo.setFinalURL(path.toString());
				} else if (item.getName().toLowerCase().contains("- update") && dictionary.get(tableName).stream().filter(i -> "update".equals(i.getFunctionType())).findFirst().orElse(null) == null) {
					flutterRepo.setPostParamList(new ArrayList<>());
					flutterRepo.getPostParamList().add(new PRequestBody("data", "{ModelName}", false, null));
					flutterRepo.setFunctionType("update");
					flutterRepo.setFunctionName("update" + titleCasedName);
					flutterRepo.setFinalURL(path.toString());
				}

				if (!flutterRepo.getFunctionType().isEmpty()) {
					dictionary.get(tableName).add(flutterRepo);
				}
			} catch (Exception ignored) {
			}
		}

		flutterRepoDic = dictionary;
	}
	
	private void createControllerFile() {
		for (Map.Entry<String, List<CodeRepo>> entry : flutterRepoDic.entrySet()) {
			try {
				String tableName = entry.getKey();
				String ModelName = Helper.toPascalCase(tableName);
				String modelName = Helper.toCamelCase(tableName);

				String modelPath = getDestinationPath("lib,src,models," + tableName + ".dart");

				StringBuilder controllerFunctions = new StringBuilder();
				for (CodeRepo repo : entry.getValue()) {
					String functionType = repo.getFunctionType();
					String functionName = repo.getFunctionName();
					String serviceFunctionCall = "_" + modelName + "Service." + functionName + "(";

					// Constructing the parameters for the function call
					List<String> functionParams = new ArrayList<>();
					for (String pathParam : repo.getPathParams()) {
						functionParams.add(pathParam);
					}
					for (PRequestBody postParam : repo.getPostParamList()) {
						functionParams.add(postParam.getPropName());
					}
					serviceFunctionCall += String.join(", ", functionParams) + ")";

					// Building the controller function
					String controllerFunction = "\n  Future<void> " + functionName + "(";
					if (!functionParams.isEmpty()) {
						controllerFunction += String.join(", ", functionParams.stream().map(param -> findDataType(modelPath, param, ModelName) + " " + param).collect(Collectors.toList())) + ") ";
					} else {
						controllerFunction += ") ";
					}
					controllerFunction += "async {\n";
					controllerFunction += "    try {\n";
					controllerFunction += "      var response = await " + serviceFunctionCall + ";\n";
					controllerFunction += "      if (response.isSuccess && response.data != null) {\n";
					
					if ("getall".equals(functionType) || "search".equals(functionType)) {
						controllerFunction += "        if (response.data!.data.isNotEmpty) {\n";
						controllerFunction += "          " + modelName + "List.value = response.data!.data;\n";
						controllerFunction += "          pageCount.value = response.data!.total > 0 ? (response.data!.total / paginator).ceilToDouble() : 1.0;\n";
						controllerFunction += "        } else {\n";
						controllerFunction += "          " + modelName + "List.value = [];\n";
						controllerFunction += "          pageCount.value = 0;\n";
						controllerFunction += "        } \n";
					} else if ("create".equals(functionType) || "update".equals(functionType) || "getbyid".equals(functionType)) {
						controllerFunction += "        " + modelName + ".value = response.data!;\n";
					} else if ("delete".equals(functionType)) {
						String deleteParam = repo.getPathParams().get(0);
						controllerFunction += "        " + modelName + "List.removeWhere((item) => item." + deleteParam + " == " + deleteParam + ");\n";
					}
					
					controllerFunction += "      } else {\n";
					controllerFunction += "        showError('Error in " + functionName + ": ${response.errorMessage}');\n";
					controllerFunction += "      }\n";
					controllerFunction += "    } catch (e) {\n";
					controllerFunction += "      showError('Error in " + functionName + ": $e');\n";
					controllerFunction += "    }\n";
					controllerFunction += "  }\n";
					
					controllerFunctions.append(controllerFunction);
				}

				String templateContent = new String(Files.readAllBytes(Paths.get(createTemplatePath("controller.txt"))));
				templateContent = templateContent.replace("{controllerFunctions}", controllerFunctions.toString());

				templateContent = templateContent.replace("{tableName}", tableName);
				templateContent = templateContent.replace("{modelName}", modelName);
				templateContent = templateContent.replace("{ModelName}", ModelName);

				String path = createDestinationPath("lib,src,controllers," + tableName + "_controller.dart");
				CopyDir.writeWithoutBOM(path, templateContent);
			} catch (Exception ex) {
				ex.printStackTrace();
				// Handle the exception as needed
			}
		}
	}
	
	private String findDataType(String filePath, String variableName, String modelName) {
		try {
			String content = new String(Files.readAllBytes(Paths.get(filePath)));
			Pattern pattern = Pattern.compile("late (\\S+)? " + variableName + ";");
			Matcher matcher = pattern.matcher(content);
			if (matcher.find()) {
				return matcher.group(1).replace("?", "");
			}
			
			if (variableName.equals("searchKey")) {
				return "String";
			}
			
			if (variableName.equals("page") || variableName.equals("paginator")) {
				return "int";
			}
			
			if (variableName.equals("data")) {
				return modelName;
			}
		} catch (IOException ex) {}
		
        return "String";
    }
	
	private void createServiceFile() {
		String functionTemplate = "  Future<Response<{ReturnType}>> {functionName}({paramList}) async {\n"
                            + "    try {\n"
                            + "      final response = await APIService.{methodName}('{apiURL}'{paramData});\n"
                            + "      return {ReturnStatement};\n"
                            + "    } catch (e) {\n"
                            + "      return Response<{ReturnType}>.error('Failed to {methodName}: $e');\n"
                            + "    }\n"
                            + "  }\n";


		for (Map.Entry<String, List<CodeRepo>> item : flutterRepoDic.entrySet()) {
			try {
				String key = item.getKey();
				String ModelName = Helper.toPascalCase(key);

				String path = createDestinationPath("lib,src,services," + key + "_service.dart");
				
				
				String modelPath = getDestinationPath("lib,src,models," + key + ".dart");
				
				
				StringBuilder serviceFunctions = new StringBuilder();

				for (CodeRepo repo : item.getValue()) {
					String functionText = new String(functionTemplate);
					functionText = functionText.replace("{functionName}", repo.getFunctionName());
					functionText = functionText.replace("{apiURL}", repo.getFinalURL());
					functionText = functionText.replace("{methodName}", repo.getMethodName());
					functionText = functionText.replace("{ModelName}", ModelName);

					StringBuilder paramList = new StringBuilder();
					StringBuilder paramData = new StringBuilder();

					// Handle Path Parameters
					for (String pathParam : repo.getPathParams()) {
						if (paramList.length() > 0) {
							paramList.append(", ");
						}
						
						paramList.append(findDataType(modelPath, pathParam, ModelName) + " ").append(pathParam);
					}

					// Handle Query Parameters
					for (PRequestBody postParam : repo.getPostParamList()) {
						if (paramList.length() > 0) {
							paramList.append(", ");
						}
						String paramDataType = postParam.getPropDataType().replace("{ModelName}", ModelName);
						paramList.append(paramDataType).append(" ").append(postParam.getPropName());
						paramData.append(", " + postParam.getPropName() + ".toJSON()");
					}
					
					if ("getall".equals(repo.getFunctionType()) || "search".equals(repo.getFunctionType())) {
						functionText = functionText.replace("{ReturnType}", "PagedData<" + ModelName + ">");
						functionText = functionText.replace("{ReturnStatement}", "Response.processPagedResponse(response, " + ModelName + ".fromJSON)");
					} else {
						functionText = functionText.replace("{ReturnType}", ModelName);
						functionText = functionText.replace("{ReturnStatement}", "Response.processSingleResponse(response, " + ModelName + ".fromJSON)");
					}

					functionText = functionText.replace("{paramList}", paramList.toString());
					functionText = functionText.replace("{paramData}", paramData.toString());
					serviceFunctions.append(functionText).append("\n");
				}

				String templateContent = new String(Files.readAllBytes(Paths.get(createTemplatePath("service.txt"))));
				templateContent = templateContent.replace("{importModel}", "package:admin_dashboard/src/models/" + key + ".dart");
				templateContent = templateContent.replace("{serviceFunctions}", serviceFunctions.toString());
				templateContent = templateContent.replace("{ModelName}", ModelName);
				CopyDir.writeWithoutBOM(path, templateContent);
				logMessage("Service class for " + ModelName + " generated successfully!", false);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}


	private void createModelFile() {
		for (Map.Entry<String, FinalQueryData> item : finalDataDic.entrySet()) {
			try {
				List<ColumnModel> columns = item.getValue().getSelectQueryData().getColumnList();
				String key = item.getKey();
				String modelName = Helper.toCamelCase(key);
				String ModelName = Helper.toPascalCase(key);
				String path = createDestinationPath("lib,src,models," + key + ".dart");

				StringBuilder classContent = new StringBuilder();
				classContent.append("class ").append(ModelName).append(" {\n");

				// Fields
				for (ColumnModel column : columns) {
					String fieldType = Helper.getDataTypeDart(column.getTypeName());
					classContent.append("  late ").append(fieldType).append("? ").append(Helper.toCamelCase(column.getField())).append(";\n");
				}

				// Constructor
				classContent.append("\n  ").append(ModelName).append("({\n");
				for (ColumnModel column : columns) {
					classContent.append("    this.").append(Helper.toCamelCase(column.getField())).append(",\n");
				}
				classContent.append("  });\n\n");

				// fromJSON method
				classContent.append("  factory ").append(ModelName).append(".fromJSON(Map<String, dynamic> json) {\n");
				classContent.append("    return ").append(ModelName).append("(\n");
				for (ColumnModel column : columns) {
					String fieldType = Helper.getDataTypeDart(column.getTypeName());
					classContent.append("      ").append(Helper.toCamelCase(column.getField())).append(": json['").append(column.getField()).append("'] as ").append(fieldType).append("?,\n");
				}
				classContent.append("    );\n");
				classContent.append("  }\n\n");

				// listFromJSON method
				classContent.append("  static List<").append(ModelName).append("> listFromJSON(List<dynamic> jsonList) {\n")
						.append("    return jsonList.map((json) => ").append(ModelName).append(".fromJSON(json)).toList();\n")
						.append("  }\n\n");

				// toJSON method
				classContent.append("  Map<String, dynamic> toJSON() {\n")
						.append("    return {\n");
				for (ColumnModel column : columns) {
					classContent.append("      '").append(column.getField()).append("': ").append(Helper.toCamelCase(column.getField())).append(",\n");
				}
				classContent.append("    };\n")
						.append("  }\n");

				// Close class
				classContent.append("}\n");

				// Write to file
				CopyDir.writeWithoutBOM(path, classContent.toString());

				logMessage("Model class for " + ModelName + " generated successfully!", false);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private void createViewFile() {
		for (Map.Entry<String, FinalQueryData> entry : finalDataDic.entrySet()) {
			try {
				String tableName = entry.getKey();
				String ModelName = Helper.toPascalCase(tableName);
				String modelName = Helper.toCamelCase(tableName);
				String title = Helper.toPascalCaseWithSpaces(tableName);
				List<ColumnModel> columns = entry.getValue().getSelectQueryData().getColumnList();

				String path = createDestinationPath("lib,src,admin,views,modules," + tableName + "_view.dart");

				StringBuilder viewContent = new StringBuilder();
				// Import statements
				viewContent.append("import 'package:flutter/material.dart';\n");
				viewContent.append("import 'package:get/get.dart';\n");
				viewContent.append("import 'package:syncfusion_flutter_datagrid/datagrid.dart';\n");
				viewContent.append("import 'package:auto_route/annotations.dart';\n");

				// Controller import
				viewContent.append("import 'package:admin_dashboard/src/controllers/").append(tableName).append("_controller.dart';\n\n");

				// RoutePage annotation
				viewContent.append("@RoutePage()\n");
				// Class definition
				viewContent.append("class ").append(ModelName).append("View extends StatefulWidget {\n");
				viewContent.append("  const ").append(ModelName).append("View({super.key});\n\n");
				viewContent.append("  @override\n");
				viewContent.append("  ").append(ModelName).append("ViewState createState() => ").append(ModelName).append("ViewState();\n");
				viewContent.append("}\n\n");

				// ViewState class definition
				viewContent.append("class ").append(ModelName).append("ViewState extends State<").append(ModelName).append("View> {\n");
				viewContent.append("  late ").append(ModelName).append("Controller controller;\n\n");

				// Init state method
				viewContent.append("  @override\n");
				viewContent.append("  void initState() {\n");
				viewContent.append("    super.initState();\n");
				viewContent.append("    controller = Get.put(").append(ModelName).append("Controller());\n");
				viewContent.append("  }\n\n");

				// Build method
				viewContent.append("  @override\n");
				viewContent.append("  Widget build(BuildContext context) {\n");
				viewContent.append("    double screenWidth = MediaQuery.of(context).size.width;\n");
				viewContent.append("    return Column(\n");
				viewContent.append("      crossAxisAlignment: CrossAxisAlignment.start,\n");
				viewContent.append("      children: [\n");

				// Header
				viewContent.append("              const Padding(\n");
				viewContent.append("                padding: EdgeInsets.symmetric(horizontal: 16),\n");
				viewContent.append("                child: Row(\n");
				viewContent.append("                  mainAxisAlignment: MainAxisAlignment.spaceBetween,\n");
				viewContent.append("                  children: [\n");
				viewContent.append("                    Text(\n");
				viewContent.append("                      \"").append(title).append("\",\n");
				viewContent.append("                      style: TextStyle(\n");
				viewContent.append("                        fontSize: 18,\n");
				viewContent.append("                        fontWeight: FontWeight.w600, // Replace 600 with appropriate FontWeight\n");
				viewContent.append("                      ),\n");
				viewContent.append("                    ),\n");
				viewContent.append("                  ],\n");
				viewContent.append("                ),\n");
				viewContent.append("              ),\n");

				// Main content area
				viewContent.append("              SizedBox(height: 16),\n");
				viewContent.append("              SizedBox(\n");
				viewContent.append("                width: screenWidth,\n");
				viewContent.append("                child: SfDataGrid(\n");
				viewContent.append("                  allowEditing: true,\n");
				viewContent.append("                  shrinkWrapRows: true,\n");
				viewContent.append("                  source: controller.").append(modelName).append("DataSource,\n");
				viewContent.append("                  rowsPerPage: controller.").append(modelName).append("List.length,\n");
				viewContent.append("                  columns: <GridColumn>[\n");

				// Table Headers
				for (ColumnModel column : columns) {
					viewContent.append("                    GridColumn(columnName: '").append(column.getField()).append("', label: Container(padding: EdgeInsets.all(16.0), alignment: Alignment.centerLeft, child: const Text('").append(Helper.toPascalCaseWithSpaces(column.getField())).append("'))),\n");
				}

				viewContent.append("                  ],\n");
				viewContent.append("                  selectionMode: SelectionMode.single,\n");
				viewContent.append("                  navigationMode: GridNavigationMode.cell,\n");
				viewContent.append("                  editingGestureType: EditingGestureType.tap,\n");
				viewContent.append("                  columnWidthMode: ColumnWidthMode.lastColumnFill,\n");
				viewContent.append("                ),\n");
				viewContent.append("              ),\n");

				// Data Pager
				viewContent.append("              Obx(() {\n");
				viewContent.append("                if (controller.pageCount.value <= 0) {\n");
				viewContent.append("                  return const Text('No data available');\n");
				viewContent.append("                }\n");
				viewContent.append("                return SfDataPager(\n");
				viewContent.append("                  delegate: controller.").append(modelName).append("DataSource,\n");
				viewContent.append("                  pageCount: controller.pageCount.value,\n");
				viewContent.append("                  direction: Axis.horizontal,\n");
				viewContent.append("                  onPageNavigationStart: (int pageIndex) {\n");
				viewContent.append("                    controller.loadPage(pageIndex);\n");
				viewContent.append("                  },\n");
				viewContent.append("                  initialPageIndex: controller.currentPageIndex.value - 1,\n");
				viewContent.append("                );\n");
				viewContent.append("              }),\n");

				// Closing tags for Column, Widget, and classes
				viewContent.append("      ],\n");
				viewContent.append("    );\n");
				viewContent.append("  }\n");
				viewContent.append("}\n");

				// Write to file
				CopyDir.writeWithoutBOM(path, viewContent.toString());

				logMessage("View class for " + ModelName + " generated successfully!", false);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	
	private void createRoutesFile() {
		try {
			StringBuilder routes = new StringBuilder();
			for (Map.Entry<String, FinalQueryData> entry : finalDataDic.entrySet()) {
				try {
					String tableName = entry.getKey();
					String ModelName = Helper.toPascalCase(tableName);
					//flutter packages pub run build_runner watch
					routes.append("        AutoRoute(path: '").append(tableName).append("', page: ").append(ModelName).append("View.page),\n");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			String path = getDestinationPath("lib,src,routes,routes.dart");
			StringBuilder routeContent = new StringBuilder();
			String templateContent = new String(Files.readAllBytes(Paths.get(path)));
			templateContent = templateContent.replace("//{routes}", routes.toString());
			CopyDir.writeWithoutBOM(path, templateContent);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private void createMenuFile() {
		try {
			StringBuilder imports = new StringBuilder();
			StringBuilder mainData = new StringBuilder();
			StringBuilder routes = new StringBuilder();
			StringBuilder routeIndex = new StringBuilder();
			StringBuilder routeWidget = new StringBuilder();
			StringBuilder constants = new StringBuilder();
			int index = 1;
			for (Map.Entry<String, FinalQueryData> entry : finalDataDic.entrySet()) {
				try {
					String tableName = entry.getKey();
					String modelName = Helper.toCamelCase(tableName);
					String ModelName = Helper.toPascalCase(tableName);
					String title = Helper.toPascalCaseWithSpaces(tableName);
					
					mainData.append("    Strings.").append(modelName).append(": IconlyBroken.home,\n");
					routes.append("    ").append(ModelName).append("View(),\n");
					imports.append("import 'package:admin_dashboard/src/admin/views/modules/").append(tableName).append("_view.dart';\n");
					routeIndex.append("  if (route == Strings.").append(modelName).append(") return ").append(index).append(";\n");
					routeWidget.append("  if (index == ").append(index).append(") return ").append(ModelName).append("View();\n");
					constants.append("  static const String ").append(modelName).append(" = '").append(title).append("';\n");
					//static const String dashboard = 'Dashboard';
					index++;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			String path = getDestinationPath("lib,src,admin,views,menu_bar.dart");
			String path2 = getDestinationPath("lib,src,admin,utils,routes.dart");
			String path3 = getDestinationPath("lib,src,admin,constant,string.dart");

			StringBuilder routeContent = new StringBuilder();
			String templateContent = new String(Files.readAllBytes(Paths.get(path)));
			String templateContent2 = new String(Files.readAllBytes(Paths.get(path2)));
			String templateContent3 = new String(Files.readAllBytes(Paths.get(path3)));
			
			templateContent = templateContent.replace("//{mainData}", mainData.toString());
			templateContent = templateContent.replace("//{routes}", routes.toString());
			
			templateContent2 = templateContent2.replace("//{imports}", imports.toString());
			templateContent2 = templateContent2.replace("//{routeIndex}", routeIndex.toString());
			templateContent2 = templateContent2.replace("//{routeWidget}", routeWidget.toString());
			
			templateContent3 = templateContent3.replace("//{constants}", constants.toString());
			
			CopyDir.writeWithoutBOM(path, templateContent);
			CopyDir.writeWithoutBOM(path2, templateContent2);
			CopyDir.writeWithoutBOM(path3, templateContent3);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public List<Exception> createFlutterAPP(CodeInput<FinalQueryData> flutterInput) {
		createDirectory();
		finalDataDic = flutterInput.getFinalDataDic();

		createRepoDictionary(flutterInput.getPostmanJson());
		
		createModelFile();
		logMessage("Models generated", false);
		
		createServiceFile();
		logMessage("Service api file generated", false);

		createControllerFile();
		logMessage("Controllers generated", false);
		
		createViewFile();
		logMessage("Views generated", false);
		
		createRoutesFile();
		logMessage("Routes generated", false);
		
		createMenuFile();
		logMessage("Menu generated", false);
		
		logMessage("----- Flutter APP Generated -----", false);
		logMessage("Please check the generated code at : " + destinationFolder, false);
		return exList;
	}


}
