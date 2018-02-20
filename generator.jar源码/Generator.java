import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import config.Config;
import layer.ControllerGenerator;
import layer.DaoGenerator;
import layer.MapperGenerator;
import layer.PojoGenerator;
import layer.ServiceGenerator;
import layer.ServiceImplGenerator;

public class Generator extends Config {
	public static void main(String[] args) throws Exception {
		sep = File.separator;
		methodNameArray = new String[] { "insertSelective", "deleteByPrimaryKey", "selectByPrimaryKey", "selectPrecise",
				"selectFuzzy", "updateSelective" };
		pageNameArray = new String[] { "insert", "select", "update" };

		default_package_path = new File("").getAbsolutePath();
		String txt_path = default_package_path + sep + "config.txt";
		Map<String, String> map = getMapFromTxt(txt_path);

		driver = map.get("driver");
		url = map.get("url");
		username = map.get("username");
		password = map.get("password");

		String style = map.get("name_style");
		// SSM框架只能使用01命名风格，即方法名为驼峰式，属性名与数据库字段相同
		name_style = style == null ? "01" : style;
		isCamelCaseMethodName = true;
		isCamelCaseAttrName = true;
		if (name_style.startsWith("1")) {
			isCamelCaseMethodName = false;
			methodNameArray = new String[] { "insert_selective", "delete_by_primary_key", "select_by_primary_key",
					"select_precise", "select_fuzzy", "update_selective" };
		}
		if (name_style.endsWith("1")) {
			isCamelCaseAttrName = false;
		}

		package_name = map.get("package_name");

		pojo_package_name = map.get("pojo_package_name").toLowerCase();
		pojo_full_package_name = getPackageNameJoined(package_name, pojo_package_name);

		controller_package_name = map.get("controller_package_name").toLowerCase();
		controller_full_package_name = getPackageNameJoined(package_name, controller_package_name);

		service_package_name = map.get("service_package_name").toLowerCase();
		service_full_package_name = getPackageNameJoined(package_name, service_package_name);

		service_impl_package_name = map.get("service_impl_package_name").toLowerCase();
		service_impl_full_package_name = getPackageNameJoined(package_name, service_impl_package_name);

		dao_package_name = map.get("dao_package_name").toLowerCase();
		dao_full_package_name = getPackageNameJoined(package_name, dao_package_name);

		mapper_package_name = map.get("mapper_package_name").toLowerCase();

		table_name = map.get("table_name");
		String[] tableNameArray = table_name.split(",");
		int lenT = tableNameArray.length;

		String calss_name = map.get("class_name");
		String[] classNameArray = calss_name.split(",");
		int lenC = classNameArray.length;

		if (lenT != lenC) {
			throw new Exception("表名与类名数量不一致！");
		}

		String[][] tableNamesAndClassNames = getTableNamesAndClassNames(tableNameArray, classNameArray);
		Connection conn = getConnectionWithDB(driver, url, username, password);
		// 根据一一对应的表名与类名生成相应的文件
		for (String[] tableNameAndClassName : tableNamesAndClassNames) {
			table_name = tableNameAndClassName[0];
			initByClassName(tableNameAndClassName[1]);
			initFormDB(conn);
			generateAll();
		}
	}

	// 根据不同的模型类名生成相应的层
	public static void initByClassName(String class_name) {
		pojo_class_name = capitalized(class_name);
		pojo_import_name = getPackageNameJoined(pojo_full_package_name, pojo_class_name);
		pojo_file_path = default_package_path + getPath(pojo_full_package_name) + sep + pojo_class_name + ".java";

		controller_class_name = pojo_class_name + "Controller";
		controller_file_path = default_package_path + getPath(controller_full_package_name) + sep
				+ controller_class_name + ".java";

		service_class_name = pojo_class_name + "Service";
		service_import_name = getPackageNameJoined(service_full_package_name, service_class_name);
		service_file_path = default_package_path + getPath(service_full_package_name) + sep + service_class_name
				+ ".java";

		service_impl_class_name = service_class_name + "Impl";
		service_impl_file_path = default_package_path + getPath(service_impl_full_package_name) + sep
				+ service_impl_class_name + ".java";

		dao_class_name = pojo_class_name + "Dao";
		dao_import_name = getPackageNameJoined(dao_full_package_name, dao_class_name);
		dao_file_path = default_package_path + getPath(dao_full_package_name) + sep + dao_class_name + ".java";

		mapper_class_name = pojo_class_name + "Mapper";
		mapper_file_path = default_package_path + getPath(getPackageNameJoined(package_name, mapper_package_name)) + sep
				+ mapper_class_name + ".xml";
	}

	// 将数据库表列名、列类型和列注释存储到指定数组
	public static void initFormDB(Connection conn) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql;

		sql = "select * from " + table_name + " where 1=0";
		rs = selectFromDB(conn, ps, rs, sql);
		colCount = rs.getMetaData().getColumnCount();

		sql = "select column_name from user_cons_columns where table_name=upper('" + table_name
				+ "') and constraint_name=(select constraint_name from user_constraints where table_name=upper('"
				+ table_name + "') and constraint_type='P')";
		rs = selectFromDB(conn, ps, rs, sql);
		if (rs.next()) {
			pk_name = rs.getString(1).toLowerCase();
		} 
		// 若找不到主键，则默认第一个字段为主键
		else {
			sql = "select column_name from user_tab_columns where table_name=upper('"+table_name+"') and rownum=1";
			rs = selectFromDB(conn, ps, rs, sql);
			while (rs.next()) {
				pk_name = rs.getString(1).toLowerCase();
			}
			if (name_style.startsWith("1")) {
				methodNameArray[1] = "delete_by_foreign_key";
				methodNameArray[2] = "select_by_foreign_key";
			} else {
				methodNameArray[1] = "deleteByForeignKey";
				methodNameArray[2] = "selectByForeignKey";				
			}
		}
		pk_name = isCamelCaseAttrName ? getCamelCase(pk_name) : pk_name;

		sql = "select a.column_name, a.data_type, b.comments from user_tab_columns a, user_col_comments b "
				+ "where a.table_name=upper('" + table_name
				+ "') and a.table_name=b.table_name and a.column_name=b.column_name";
		rs = selectFromDB(conn, ps, rs, sql);

		isImportSql = false;
		colNameArray = new String[colCount];
		dataTypeArray = new String[colCount];
		colCommentArray = new String[colCount];
		int i = 0;
		while (rs.next()) {
			if (isCamelCaseAttrName) {
				colNameArray[i] = getCamelCase(rs.getString(1));
			} else {
				colNameArray[i] = rs.getString(1).toLowerCase();
			}
			dataTypeArray[i] = rs.getString(2);
			if (colNameArray[i].equals(pk_name)) {
				pk_type = dataTypeArray[i];
			}
			colCommentArray[i] = rs.getString(3);
			i++;
			String data_type = rs.getString(2).toLowerCase();
			if (data_type.equals("date") || data_type.equals("time") || data_type.startsWith("timestamp")
					|| data_type.equals("blob") || data_type.equals("clob")) {
				isImportSql = true;
			}
		}
	}

	public static ResultSet selectFromDB(Connection conn, PreparedStatement ps, ResultSet rs, String sql)
			throws SQLException {
		return conn.prepareStatement(sql).executeQuery();
	}

	public static void generateAll() throws IOException {
		PojoGenerator.generate();
		ControllerGenerator.generate();
		ServiceGenerator.generate();
		ServiceImplGenerator.generate();
		DaoGenerator.generate();
		MapperGenerator.generate();
	}
}
