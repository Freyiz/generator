package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import config.Config;

public abstract class Util {
	// 判断字符串是否以数字开头
	public static boolean isStartsWithDigit(String string) {
		return Pattern.compile("^\\d+?.*$").matcher(string).matches();
	}

	// 将字符串首字母小写
	public static String re_capitalized(String string) {
		char[] c = string.toCharArray();
		if (c[0] >= 'A' && c[0] <= 'Z') {
			c[0] = (char) (c[0] + 32);
		}
		return String.valueOf(c);
	}

	// 将字符串首字母大写
	public static String capitalized(String string) {
		char[] c = string.toCharArray();
		if (c[0] >= 'a' && c[0] <= 'z') {
			c[0] = (char) (c[0] - 32);
		}
		return String.valueOf(c);
	}

	// 将字符串转化为小写，再将字符串首字母大写
	public static String capitalized(String string, boolean isToLowerCase) {
		string = isToLowerCase ? string.toLowerCase() : string;
		return capitalized(string);
	}

	// 驼峰式命名
	public static String getCamelCase(String string) {
		String[] stringArray = string.toLowerCase().split("_|-| ");
		int length = stringArray.length;
		StringBuffer sb = new StringBuffer();
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				if (!(string = stringArray[i]).equals("")) {
					if (i == 0) {
						sb.append(string);
					} else {
						sb.append(capitalized(string, true));
					}
				}
			}
		}
		return String.valueOf(sb);
	}

	// 连接包名
	public static String getPackageNameJoined(String left, String right) {
		if (left.equals("")) {
			return right;
		}
		if (right.equals("")) {
			return left;
		}
		return left + "." + right;
	}

	// 将包名转化为路径，如com.test --> /com/test
	public static String getPath(String package_name) {
		String[] packageNameArray = package_name.split("\\.");
		int len = packageNameArray.length;
		if (len > 0) {
			StringBuffer sb = new StringBuffer();
			for (String packageName : packageNameArray) {
				if (isStartsWithDigit(packageName)) {
					try {
						throw new Exception("包名不得以数字开头。");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				sb.append(Config.sep);
				sb.append(packageName);
				String dir_path = Config.default_package_path + String.valueOf(sb);
				File dir = new File(dir_path);
				dir.mkdir();
			}
			return String.valueOf(sb);
		}
		return "";
	}

	// 返回一一对应的表名与类名
	public static String[][] getTableNamesAndClassNames(String[] tableNameArray, String[] classNameArray) {
		int len = tableNameArray.length;
		String[][] tableNamesAndClassNames = new String[len][2];
		for (int i = 0; i < len; i++) {
			tableNamesAndClassNames[i][0] = tableNameArray[i].trim();
			tableNamesAndClassNames[i][1] = classNameArray[i].trim();
		}
		return tableNamesAndClassNames;
	}

	// 生成文件
	public static void generateFile(String file_path, String content) throws IOException {
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file_path), "utf-8");
		osw.write(content);
		osw.flush();
		osw.close();
	}

	// 将txt文件键值对存储到Map
	public static Map<String, String> getMapFromTxt(String path) {
		File file = new File(path);
		InputStream is;
		InputStreamReader isr;
		BufferedReader br;
		Map<String, String> map = null;
		try {
			is = new FileInputStream(file);
			isr = new InputStreamReader(is, "utf-8");
			br = new BufferedReader(isr);
			String line;
			map = new HashMap<String, String>();
			while ((line = br.readLine()) != null) {
				String[] k_v = line.split("=", -1);
				if (!k_v[0].startsWith("#") && k_v.length == 2) {
					String key = k_v[0].trim();
					String value = k_v[1].trim();
					map.put(key, value);
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	// 连接数据库
	public static Connection getConnectionWithDB(String driver, String url, String username, String password)
			throws ClassNotFoundException, SQLException {
		Connection conn = null;
		Class.forName(driver);
		conn = DriverManager.getConnection(url, username, password);
		return conn;
	}

	// 将数据库类型转化为Java类型,此方法表示的类型对应关系可能不准确
	public static String getJavaType(String sqlType) {
		sqlType = sqlType.toLowerCase();
		if (sqlType.equals("bit")) {
			return "Boolean";
		} else if (sqlType.equals("tinyint")) {
			return "Byte";
		} else if (sqlType.equals("smallint")) {
			return "Short";
		} else if (sqlType.equals("int")) {
			return "Integer";
		} else if (sqlType.equals("bigint") || sqlType.equals("number")) {
			return "Long";
		} else if (sqlType.equals("float")) {
			return "Float";
		} else if (sqlType.equals("decimal") || sqlType.equals("numeric") || sqlType.equals("real")
				|| sqlType.equals("money") || sqlType.equals("smallmoney")) {
			return "Double";
		} else if (sqlType.equals("varchar") || sqlType.equals("varchar2") || sqlType.equals("char")
				|| sqlType.equals("nvarchar") || sqlType.equals("nvarchar2") || sqlType.equals("nchar")
				|| sqlType.equals("text") || sqlType.equals("long")) {
			return "String";
		} else if (sqlType.equals("date")) {
			return "Date";
		} else if (sqlType.equals("time")) {
			return "Time";
		} else if (sqlType.startsWith("timestamp")) {
			return "Timestamp";
		} else if (sqlType.equals("image") || sqlType.equals("blob")) {
			return "Blob";
		} else if (sqlType.equals("clob")) {
			return "Clob";
		}
		return null;
	}

	// 将数据库类型转化为Jdbc类型,此方法表示的类型对应关系可能不准确
	public static String getJdbcType(String sqlType) {
		sqlType = sqlType.toLowerCase();
		if (sqlType.equals("bit") || sqlType.equals("tinyint") || sqlType.equals("int") || sqlType.equals("decimal")
				|| sqlType.equals("bigint") || sqlType.equals("number") || sqlType.equals("float")
				|| sqlType.equals("numeric") || sqlType.equals("real") || sqlType.equals("money")
				|| sqlType.equals("smallmoney")) {
			return "DECIMAL";
		} else if (sqlType.equals("varchar") || sqlType.equals("varchar2")) {
			return "VARCHAR";
		} else if (sqlType.equals("long")) {
			return "LONGVARCHAR";
		} else if (sqlType.equals("char")) {
			return "CHAR";
		} else if (sqlType.equals("date")) {
			return "DATE";
		} else if (sqlType.equals("time")) {
			return "TIME";
		} else if (sqlType.startsWith("timestamp")) {
			return "TIMESTAMP";
		} else if (sqlType.equals("image") || sqlType.equals("blob")) {
			return "BLOB";
		} else if (sqlType.equals("clob")) {
			return "CLOB";
		}
		return "OTHER";
	}
}
