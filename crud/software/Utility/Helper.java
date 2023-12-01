package crud.software.Utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.*;

public class Helper {

    public static void copy(String sourceDirectory, String targetDirectory) {
        File source = new File(sourceDirectory);
        File target = new File(targetDirectory);
        copyAll(source, target);
    }
	
	public static String getTableCharacter(List<String> characterUsed, String tableName) {
        return getTableCharacter(characterUsed, tableName, 2);
    }

    public static String getTableCharacter(List<String> characterUsed, String tableName, int size) {
        List<String> list = List.of("as", "on", "t");
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        text.append(tableName.charAt(0));

        while (text.length() < size) {
            text.append(tableName.charAt(random.nextInt(tableName.length())));
        }
        
        if (!characterUsed.contains(text.toString()) && !list.contains(text.toString())) {
            return text.toString();
        }
        return getTableCharacter(characterUsed, tableName, size + 1);
    }

    public static void copyAll(File source, File target) {
        if (!target.exists()) {
            target.mkdirs();
        }

        File[] files = source.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                File targetFile = new File(target, file.getName());
                try {
                    Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        File[] directories = source.listFiles(File::isDirectory);
        for (File dir : directories) {
            File targetDir = new File(target, dir.getName());
            copyAll(dir, targetDir);
        }
    }

    public static String removeSpecialCharacters(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : str.toCharArray()) {
            if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == ' ' || c == '_') {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }
	
	public static String getDataTypePHP(String input) {
		String text = "";
		String text2 = input;

		if (input.contains("unsigned")) {
			text = "unsigned";
		}

		if (input.contains("(")) {
			text2 = input.split("\\(", 2)[0];
		}

		switch (text2.toLowerCase()) {
			case "binary":
			case "bit":
			case "boolean":
				return "boolean";
			case "datetime2":
			case "datetime":
			case "date":
			case "timestamp":
				return "datetime";
			case "time":
				return "string";
			case "int":
			case "int32":
			case "int16":
			case "tinyint":
			case "mediumint":
			case "smallint":
				// Note: The original C# code had a check for 'unsigned' but didn't use the result. 
				// If you need to use it, you can check the 'text' variable here.
				return "integer";
			case "bigint":
			case "int64":
				// Similarly, the original C# code had a check for 'unsigned' here but didn't use the result.
				return "integer";
			case "decimal":
				return "float";
			case "double":
			case "float":
			case "real":
				return "float";
			case "nvarchar":
			case "varchar":
			case "char":
			case "nchar":
				return "string";
			case "text":
			case "tinytext":
			case "tinyblob":
			case "longtext":
			case "mediumblob":
			case "longblob":
			case "blob":
			case "mediumtext":
				return "string";
			default:
				return "string";
		}
	}


    public static String getDataTypeCSharpSQL(String input) {
        String text = "";
        String text2 = input;
        if (input.contains("unsigned")) {
            text = "unsigned";
        }
        if (input.contains("(")) {
            text2 = input.split("\\(")[0];
        }
        switch (text2.toLowerCase()) {

        case "binary":
        case "bit":
        case "boolean":
            return "Boolean";

        case "datetime":
        case "date":
            return "Date";

        case "timestamp":
            return "DateTime";

        case "time":
            return "TimeSpan";

        case "tinyint":
            return "Int16";

        case "int":
        case "mediumint":
        case "smallint":
            if (text.equals("unsigned")) {
                return "UInt32";
            }
            return "Int32";

        case "bigint":
            if (text.equals("unsigned")) {
                return "UInt64";
            }
            return "Int64";

        case "double":
        case "decimal":
        case "float":
        case "real":
            return "Double";

        case "varchar":
        case "char":
            return "String";

        case "text":
        case "tinytext":
        case "tinyblob":
        case "longtext":
        case "mediumblob":
        case "longblob":
        case "blob":
        case "mediumtext":
            return "String";

        default:
            return "String";
        }
    }
	
	public static String getDataTypeDart(String input) {
		String text = "";
		String text2 = input;

		if (input.contains("unsigned")) {
			text = "unsigned";
		}
		
		if (input.contains("(")) {
			text2 = input.split("\\(", 2)[0];
		}

		switch (text2.toLowerCase()) {
			case "binary":
			case "bit":
			case "boolean":
			case "tinyint":
				return "bool";
			case "datetime2":
			case "datetime":
			case "date":
			case "timestamp":
				return "DateTime";
			case "time":
				return "String";
			case "int":
			case "int32":
			case "int16":
			case "mediumint":
			case "smallint":
				// Note: The original C# code had a check for 'unsigned' but didn't use the result. 
				// If you need to use it, you can check the 'text' variable here.
				return "int";
			case "bigint":
			case "int64":
				// Similarly, the original C# code had a check for 'unsigned' here but didn't use the result.
				return "int";
			case "decimal":
			case "numeric":
				return "double";
			case "double":
			case "float":
			case "real":
				return "double";
			case "nvarchar":
			case "varchar":
			case "char":
			case "nchar":
			case "text":
			case "tinytext":
			case "tinyblob":
			case "longtext":
			case "mediumblob":
			case "longblob":
			case "blob":
			case "mediumtext":
				return "String";
			default:
				return "String";
		}
	}


	public static String toPascalCase(String original) {
		Pattern regex = Pattern.compile("[^_a-zA-Z0-9]");
		Pattern regex2 = Pattern.compile("(?<=\\s)");
		Pattern startsWithLowerCaseChar = Pattern.compile("^[a-z]");
		Pattern firstCharFollowedByUpperCasesOnly = Pattern.compile("(?<=[A-Z])[A-Z0-9]+$");
		Pattern lowerCaseNextToNumber = Pattern.compile("(?<=[0-9])[a-z]");
		Pattern upperCaseInside = Pattern.compile("(?<=[A-Z])[A-Z]+?((?=[A-Z][a-z])|(?=[0-9]))");

		return Stream.of(regex.matcher(regex2.matcher(original).replaceAll("_")).replaceAll("").split("_"))
				.filter(w -> !w.isEmpty())
				.map(w -> startsWithLowerCaseChar.matcher(w).replaceFirst(match -> match.group().toUpperCase()))
				.map(w -> firstCharFollowedByUpperCasesOnly.matcher(w).replaceFirst(match -> match.group().toLowerCase()))
				.map(w -> lowerCaseNextToNumber.matcher(w).replaceFirst(match -> match.group().toUpperCase()))
				.map(w -> upperCaseInside.matcher(w).replaceFirst(match -> match.group().toLowerCase()))
				.collect(Collectors.joining());
	}
	
	public static String toCamelCase(String original) {
		String pascalCase = toPascalCase(original);
		if (pascalCase.isEmpty()) {
			return "";
		}
		return Character.toLowerCase(pascalCase.charAt(0)) + pascalCase.substring(1);
	}
	
	public static String toPascalCaseWithSpaces(String original) {
        // Use the toPascalCase method to convert each word to Pascal Case
        return Stream.of(original.split("_"))
                .filter(w -> !w.isEmpty())
                .map(Helper::toPascalCase)
                .collect(Collectors.joining(" ")); // Join with spaces instead of an empty string
    }
}