package crud.software.MySQL;

import java.sql.*;
import java.util.stream.Collectors;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.mysql.cj.jdbc.MysqlDataSource;

import crud.software.Models.*;
import crud.software.Utility.Helper;

public class MySQLDBHelper {

	private Map<String, List<ColumnModel>> dicColumn = new HashMap<>();
	private Map<String, List<PrimaryKeyClass>> dicPrimaryKey = new HashMap<>();

	private String connectionString;
	private Connection dbCon;
	private String host;
	private int port;
	private String username;
	private String password;
	private String dbName;

	public MySQLDBHelper(String host, String port, String username, String password, String dbName) {
		this.host = host;
		this.port = Integer.parseInt(port);
		this.username = username;
		this.password = password;
		this.dbName = dbName;
		this.connectionString = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&characterEncoding=utf8&allowPublicKeyRetrieval=true", host, this.port, dbName);
	}

	public boolean connect() throws SQLException {
		dbCon = DriverManager.getConnection(connectionString, username, password);
		return dbCon != null;
	}
	
	private boolean isConnect() {
		try {
			// Check if connection is null and attempt to connect
			if (dbCon == null) {
				connect();
			}
			
			// Check if connection is still null or closed, and attempt to re-connect
			if (dbCon == null || dbCon.isClosed()) {
				dbCon = DriverManager.getConnection(connectionString, username, password);
			}
			
			// Return true if connection is now active, false otherwise
			return dbCon != null && !dbCon.isClosed();
			
		} catch (SQLException e) {
			// Log or handle the exception as needed
			e.printStackTrace();
			return false;
		}
	}
	
    // Getter and Setter for dicColumn
    public Map<String, List<ColumnModel>> getDicColumn() {
        return dicColumn;
    }

    public void setDicColumn(Map<String, List<ColumnModel>> dicColumn) {
        this.dicColumn = dicColumn;
    }

    // Getter and Setter for dicPrimaryKey
    public Map<String, List<PrimaryKeyClass>> getDicPrimaryKey() {
        return dicPrimaryKey;
    }

    public void setDicPrimaryKey(Map<String, List<PrimaryKeyClass>> dicPrimaryKey) {
        this.dicPrimaryKey = dicPrimaryKey;
    }

    // Getter and Setter for connectionString
    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    // Getter and Setter for dbCon
    public Connection getDbCon() {
        return dbCon;
    }

    public void setDbCon(Connection dbCon) {
        this.dbCon = dbCon;
    }

    // Getter and Setter for host
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    // Getter and Setter for port
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    // Getter and Setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and Setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter and Setter for dbName
    public String getDBName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

	public List<String> getListOfTable() throws SQLException {
		List<String> list = new ArrayList<>();
		if (dbCon != null && isConnect()) {
			Statement statement = dbCon.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT table_name FROM information_schema.tables WHERE TABLE_TYPE NOT LIKE 'VIEW' AND table_schema = '" + dbName + "'");
			while (resultSet.next()) {
				list.add(resultSet.getString(1));
			}
		}
		return list;
	}
	
	public List<String> getListOfView() throws SQLException {
		List<String> list = new ArrayList<>();
		if (dbCon != null && isConnect()) {
			Statement statement = dbCon.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT table_name FROM information_schema.tables WHERE TABLE_TYPE LIKE 'VIEW' AND table_schema = '" + dbName + "'");
			while (resultSet.next()) {
				list.add(resultSet.getString(1));
			}
		}
		return list;
	}

	public List<ColumnModel> getTableColumns(String tableName) throws SQLException {
		List<ColumnModel> list = new ArrayList<>();
		
		// Check if tableName is provided
		if (tableName == null || tableName.isEmpty()) {
			System.out.println("Error: Provided tableName is null or empty.");
			return list;
		}
		
		// Check if dbCon is initialized
		if (dbCon == null) {
			System.out.println("Error: dbCon is null. Attempting to connect...");
		}
		
		if (isConnect()) {
			System.out.println("Successfully connected to the database.");
			
			// Fetch columns from the specified table
			System.out.println("Fetching columns for table: " + tableName);
			Statement statement = dbCon.createStatement();
			ResultSet resultSet = statement.executeQuery("SHOW COLUMNS FROM " + dbName + "." + tableName);
			while (resultSet.next()) {
				ColumnModel column = new ColumnModel();
				column.setField(resultSet.getString(1) != null ? resultSet.getString(1) : null);
				column.setTypeName(resultSet.getString(2) != null ? resultSet.getString(2) : null);
				column.setIsNull(resultSet.getString(3) != null ? resultSet.getString(3) : null);
				column.setKey(resultSet.getString(4) != null ? resultSet.getString(4) : null);
				column.setDefaultValue(resultSet.getString(5) != null ? resultSet.getString(5) : null);
				column.setExtra(resultSet.getString(6) != null ? resultSet.getString(6) : null);
				list.add(column);
			}
			resultSet.close();
			System.out.println("Fetched " + list.size() + " columns for table: " + tableName);
			
			// Fetch foreign key details
			System.out.println("Fetching foreign key details for table: " + tableName);
			resultSet = statement.executeQuery("SELECT COLUMN_NAME, CONSTRAINT_NAME, REFERENCED_COLUMN_NAME, REFERENCED_TABLE_NAME FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_NAME = '" + tableName + "' AND TABLE_SCHEMA='" + dbName + "'");
			int fkCount = 0;
			while (resultSet.next()) {
				FKDetails fkDetails = new FKDetails();
				fkDetails.setCOLUMN_NAME(resultSet.getString(1) != null ? resultSet.getString(1) : null);
				fkDetails.setCONSTRAINT_NAME(resultSet.getString(2) != null ? resultSet.getString(2) : null);
				fkDetails.setREFERENCED_COLUMN_NAME(resultSet.getString(3) != null ? resultSet.getString(3) : null);
				fkDetails.setREFERENCED_TABLE_NAME(resultSet.getString(4) != null ? resultSet.getString(4) : null);
				if (fkDetails.getREFERENCED_TABLE_NAME() != null) {
					fkCount++;
					for (ColumnModel col : list) {
						if (col.getField().equalsIgnoreCase(fkDetails.getCOLUMN_NAME())) {
							col.setFkDetails(fkDetails);
							break;
						}
					}
				}
			}
			resultSet.close();
			System.out.println("Fetched " + fkCount + " foreign key details for table: " + tableName);
			
			// Close the connection
			dbCon.close();
			System.out.println("Database connection closed.");
		} else {
			System.out.println("Error: Could not establish a connection to the database.");
		}
		
		return list;
	}

	
	public List<PrimaryKeyClass> getPrimaryKey(String tableName) throws SQLException {
		List<PrimaryKeyClass> list = new ArrayList<>();
		List<ColumnModel> list2 = null;
		
		if (dicColumn.containsKey(tableName)) {
			list2 = dicColumn.get(tableName);
		} else {
			list2 = getTableColumns(tableName);
			dicColumn.put(tableName, list2);
		}
		
		List<ColumnModel> list3 = list2.stream().filter(i -> "PRI".equals(i.getKey())).collect(Collectors.toList());
		
		if (list3 != null) {
			for (ColumnModel item : list3) {
				list.add(new PrimaryKeyClass(item.getTypeName(), item.getField()));
			}
		}
		
		if (list.isEmpty()) {
			for (ColumnModel item : list2) {
				if ("auto_increment".equals(item.getExtra())) {
					list.add(new PrimaryKeyClass(item.getTypeName(), item.getField()));
				}
			}
		}
		
		if (list.isEmpty() && !list2.isEmpty()) {
			ColumnModel columnModel = list2.stream().filter(i -> i.getTypeName().toLowerCase().contains("int")).findFirst().orElse(null);
			if (columnModel != null) {
				list.add(new PrimaryKeyClass(columnModel.getTypeName(), columnModel.getField()));
			} else {
				list.add(new PrimaryKeyClass(list2.get(0).getTypeName(), list2.get(0).getField()));
			}
		}
		
		return list;
	}

	public SelectQueryData getSelectQueryData(String tableName) throws SQLException {
		SelectQueryData selectQueryData = new SelectQueryData();
		List<PrimaryKeyClass> list = null;
		
		if (dicPrimaryKey.containsKey(tableName)) {
			list = dicPrimaryKey.get(tableName);
		} else {
			list = getPrimaryKey(tableName);
			dicPrimaryKey.put(tableName, list);
		}
		
		List<ColumnModel> columns = dicColumn.get(tableName);
		if (columns == null) {
			columns = getTableColumns(tableName);
			dicColumn.put(tableName, columns);
		}
		selectQueryData.setColumnList(columns);
		
		List<String> list2 = new ArrayList<>();
		List<JoinColumnClass> list3 = new ArrayList<>();
		List<FKColumnClass> list4 = new ArrayList<>();
		List<ColumnModel> list6 = selectQueryData.getColumnList();
		List<String> list7 = new ArrayList<>();
		
		for (ColumnModel item : list6) {
			list2.add(item.getField());
			
			if (item.getFkDetails() != null) {
				List<ColumnModel> tableColumns = getTableColumns(item.getFkDetails().getREFERENCED_TABLE_NAME());
				if (tableColumns != null && !tableColumns.isEmpty()) {
					ColumnModel refColumnName = tableColumns.stream()
											.filter(i -> "NO".equalsIgnoreCase(i.getIsNull()) && !"PRI".equals(i.getKey()) && i.getTypeName().contains("char"))
											.findFirst().orElse(null);
					if (refColumnName == null) {
						refColumnName = tableColumns.stream()
											.filter(i -> !"PRI".equals(i.getKey()) && i.getTypeName().contains("char"))
											.findFirst().orElse(null);
					}
					if (refColumnName == null) {
						refColumnName = tableColumns.stream()
											.filter(i -> !"PRI".equals(i.getKey()))
											.findFirst().orElse(null);
					}
					ColumnModel finalRefColumnName = refColumnName;
					if (finalRefColumnName != null && list4.stream().noneMatch(i -> i.getFieldName2().equals(finalRefColumnName.getField()))) {
						String tableCharacter = Helper.getTableCharacter(list7, item.getFkDetails().getREFERENCED_TABLE_NAME());
						list7.add(tableCharacter);
						list4.add(new FKColumnClass(item.getField(), item.getTypeName(), item.getFkDetails().getREFERENCED_TABLE_NAME(), refColumnName.getField(), tableName, tableCharacter, item.getFkDetails().getREFERENCED_COLUMN_NAME(), refColumnName.getTypeName()));
						list3.add(new JoinColumnClass(item.getFkDetails().getREFERENCED_TABLE_NAME(), item.getFkDetails().getREFERENCED_COLUMN_NAME(), tableName, tableCharacter, item.getField(), refColumnName, item));
						list2.add(refColumnName.getField());
					}
				}
			}
		}
		
		selectQueryData.setFkColumnData(list4);
		selectQueryData.setJoinQueryData(list3);
		selectQueryData.setSelectColumnList(list2);
		
		if (!list.isEmpty()) {
			selectQueryData.setPrimaryKeys(list);
		} else {
			if (!list6.isEmpty()) {
				ColumnModel columnModel = list6.get(0);
				if (columnModel != null) {
					if (selectQueryData.getPrimaryKeys() == null) {
						selectQueryData.setPrimaryKeys(new ArrayList<>());
					}
					selectQueryData.getPrimaryKeys().add(new PrimaryKeyClass(columnModel.getTypeName(), columnModel.getField()));
				}
			}
		}

		return selectQueryData;
	}
	
	public InsertUpdateQueryData getInsertUpdateQueryData(String tableName) throws SQLException {
		InsertUpdateQueryData insertUpdateQueryData = new InsertUpdateQueryData();
		List<PrimaryKeyClass> list = null;

		if (dicPrimaryKey.containsKey(tableName)) {
			list = dicPrimaryKey.get(tableName);
		} else {
			list = getPrimaryKey(tableName);
			dicPrimaryKey.put(tableName, list);
		}
		insertUpdateQueryData.setPrimaryKeys(list);

		List<ColumnModel> list3 = dicColumn.get(tableName);
		insertUpdateQueryData.setColumnList(list3);
		List<InsertUpdateClass> list4 = new ArrayList<>();
		List<InsertUpdateClass> list5 = new ArrayList<>();
		
		for (ColumnModel item : list3) {
			if (!"auto_increment".equals(item.getExtra()) && !"CURRENT_TIMESTAMP".equals(item.getDefaultValue())) {
				boolean isRequired = "no".equalsIgnoreCase(item.getIsNull());
				list4.add(new InsertUpdateClass(item.getTypeName(), item.getField(), isRequired, item.getDefaultValue()));
				list5.add(new InsertUpdateClass(item.getTypeName(), item.getField(), isRequired, item.getDefaultValue()));
			}
		}

		insertUpdateQueryData.setInsertColumnList(list4);
		insertUpdateQueryData.setUpdateColumnList(list5);
		
		return insertUpdateQueryData;
	}

	public FinalQueryData buildLaravelQuery(String tableName) throws SQLException {
		FinalQueryData finalQueryData = new FinalQueryData();
		SelectQueryData selectQueryData = getSelectQueryData(tableName);
		InsertUpdateQueryData insertUpdateQueryData = getInsertUpdateQueryData(tableName);

		String text = "SELECT {fkColumns} t.* FROM " + tableName + " t {joinQuery} WHERE {selectPrimaryKey} LIMIT 0,1";
		String text2 = "SELECT {fkColumns} t.* FROM " + tableName + " t {joinQuery} LIMIT ?, ?";
		String text3 = "SELECT {fkColumns} t.* FROM " + tableName + " t {joinQuery} WHERE {searchCondition} LIMIT ?,?";
		String text4 = "SELECT count(*) TotalCount FROM " + tableName + " t {joinQuery} WHERE {searchCondition} ";
		String text5 = "SELECT count(*) TotalCount FROM " + tableName + " t {joinQuery} ";
		String text6 = "";
		String text7 = "";
		String text8 = "";
		String text9 = "DELETE FROM " + tableName + " WHERE {deletePrimaryKey}";
		String text10 = "";
		String text11 = "";
		String text12 = "";
		String text13 = "";
		
		for (ColumnModel column : selectQueryData.getColumnList()) {
			if ("CURRENT_TIMESTAMP".equals(column.getDefaultValue())) {
				text12 = text12 + "\nthis." + column.getField() + " = new Date();";
				text11 = text11 + "\n" + column.getField() + ": new Date(),";
			} else if ("auto_increment".equals(column.getExtra())) {
				text12 = text12 + "\nthis." + column.getField() + " = 0;";
				text11 = text11 + "\n" + column.getField() + ":0,";
			} else if (!"auto_increment".equals(column.getExtra()) && !"CURRENT_TIMESTAMP".equals(column.getDefaultValue())) {
				text12 = text12 + "\nthis." + column.getField() + " = " + tableName + "." + column.getField() + ";";
				text11 = text11 + "\n" + column.getField() + ":req.body." + column.getField() + ",";
				if ("NO".equals(column.getIsNull())) {
					text13 = (text13 != null && !text13.isEmpty()) ? (text13 + " || !createObj." + column.getField()) : ("!createObj." + column.getField());
				}
			}
			if (!"auto_increment".equals(column.getExtra())) {
				text8 = text8 + " OR LOWER(t." + column.getField() + ") LIKE CONCAT('%','\"+searchKey+\"','%')";
			}
		}
		
		List<String> stringList = new ArrayList<>();

		for (FKColumnClass fKColumnDatum : selectQueryData.getFKColumnData()) {
			text7 += " " + fKColumnDatum.getTableChar2() + "." + fKColumnDatum.getFieldName2() + " as " + fKColumnDatum.getFieldName1() + "_Value,";
			text12 += "\nthis." + fKColumnDatum.getFieldName1() + "_Value = " + tableName + "." + fKColumnDatum.getFieldName1() + "_Value;";
		}

		for (JoinColumnClass joinQueryDatum : selectQueryData.getJoinQueryData()) {
			text6 += " join " + joinQueryDatum.getTableName2() + " " + joinQueryDatum.getTableChar2() + " on t." + joinQueryDatum.getFieldName1() + " = " + joinQueryDatum.getTableChar2() + "." + joinQueryDatum.getFieldName2() + " ";
		}

		String text14 = "";
		String text15 = "";
		String text16 = "";
		String text17 = "";

		if (selectQueryData.getPrimaryKeys() != null && selectQueryData.getPrimaryKeys().size() > 1) {
			for (PrimaryKeyClass primaryKey : selectQueryData.getPrimaryKeys()) {
				if (text16.isEmpty()) {
					text16 = primaryKey.getFieldName() + "= ?";
					text17 = primaryKey.getFieldName();
				} else {
					text16 += " AND " + primaryKey.getFieldName() + "= ?";
					text17 += "," + primaryKey.getFieldName();
				}
				if (!text14.isEmpty()) {
					text14 += " AND ";
				}
				if (!text15.isEmpty()) {
					text15 += " AND ";
				}
				text10 = (text10.isEmpty() ? "req.params." + primaryKey.getFieldName() : text10 + ",req.params." + primaryKey.getFieldName());
				text14 += "t." + primaryKey.getFieldName() + "= ?";
				text15 += primaryKey.getFieldName() + "=?";
			}
			text14 = text14.trim();
			text15 = text15.trim();
			text16 = text16.trim();
			text10 = text10.trim();
		} else if (selectQueryData.getPrimaryKeys() != null && selectQueryData.getPrimaryKeys().size() == 1) {
			String fieldName = selectQueryData.getPrimaryKeys().get(0).getFieldName();
			text14 = "t." + fieldName + "= ?";
			text15 = fieldName + "=?";
			text16 = fieldName + "= ?";
			text17 = fieldName;
			text10 = "req.params." + fieldName;
		} else {
			List<ColumnModel> columns = dicColumn.get(tableName);
			if (columns == null || columns.isEmpty()) {
				columns = getTableColumns(tableName);
				if (columns == null || columns.isEmpty()) {
					// Handle the case where columns is still null or empty after fetching from the database
					throw new SQLException("No columns found for table: " + tableName);
				}
				dicColumn.put(tableName, columns);
			}
			selectQueryData.setColumnList(columns);

			// Check if columns list is not empty before accessing its first element
			if (!columns.isEmpty()) {
				String field = columns.get(0).getField();
				text14 = "t." + field + "= ?";
				text15 = field + "=?";
				text16 = field + "= ?";
				text17 = field;
				text10 = "req.params." + field;
			} else {
				// Handle the case where columns list is empty
				throw new SQLException("Columns list is empty for table: " + tableName);
			}
		}

		text2 = text2.replace("{tableName}", tableName);
		text2 = text2.replace("{joinQuery}", text6);
		text2 = text2.replace("{fkColumns}", text7);
		text = text.replace("{tableName}", tableName);
		text = text.replace("{joinQuery}", text6);
		text = text.replace("{fkColumns}", text7);
		text = text.replace("{selectPrimaryKey}", text14);
		text3 = text3.replace("{tableName}", tableName);
		text3 = text3.replace("{joinQuery}", text6);
		text3 = text3.replace("{fkColumns}", text7);
		text3 = text3.replace("{searchCondition}", text8.trim().replaceFirst("^O", "").replaceFirst("^R", ""));
		text5 = text5.replace("{tableName}", tableName);
		text5 = text5.replace("{joinQuery}", text6);
		text5 = text5.replace("{fkColumns}", text7);
		text4 = text4.replace("{tableName}", tableName);
		text4 = text4.replace("{joinQuery}", text6);
		text4 = text4.replace("{fkColumns}", text7);
		text4 = text4.replace("{searchCondition}", text8.trim().replaceFirst("^O", "").replaceFirst("^R", ""));
		text9 = text9.replace("{deletePrimaryKey}", text15);
		String empty = "";
		String text18 = "";
		String insertQuery = "INSERT INTO " + tableName + " set ?";
		String text19 = "[";

		for (InsertUpdateClass updateColumn : insertUpdateQueryData.getUpdateColumnList()) {
			text19 += " " + tableName + "." + updateColumn.getFieldName() + ",";
			text18 = text18.isEmpty() ? updateColumn.getFieldName() + " = ?" : text18 + "," + updateColumn.getFieldName() + " = ?";
		}

		text13 = text13.isEmpty() ? "true" : text13.trim().replaceFirst("^\\|", "").replaceFirst("\\|$", "");
		finalQueryData.setPrimaryKeyControllerString(text10);
		finalQueryData.setPrimaryKeys(selectQueryData.getPrimaryKeys());
		finalQueryData.setSearchQuery(text3);
		finalQueryData.setSelectAllQuery(text2);
		finalQueryData.setSelectOneQuery(text);
		finalQueryData.setSelectAllRecordCountQuery(text5);
		finalQueryData.setSearchRecordCountQuery(text4);
		finalQueryData.setSelectQueryData(selectQueryData);
		finalQueryData.setPropertyListString(text12);
		finalQueryData.setCreatePropertyListString(text11);
		finalQueryData.setPrimaryKeyString(text16);
		finalQueryData.setPrimaryKeyCommaString(text17);
		empty = empty.replace("{updateQueryParam}", text18);
		empty = empty.replace("{updateWereParam}", text16);
		finalQueryData.setUpdateQuery(empty);
		finalQueryData.setInsertQuery(insertQuery);
		text19 = text19.replaceAll(",$", "") + "," + text17 + "]";
		finalQueryData.setUpdateParam(text19);
		finalQueryData.setInsertParam("");
		finalQueryData.setDeleteQuery(text9);
		finalQueryData.setRequiredFieldString_CreateUpdate(text13);

		return finalQueryData;
	}
}