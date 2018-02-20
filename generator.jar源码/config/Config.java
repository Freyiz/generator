package config;

import util.Util;

public abstract class Config extends Util {
	public static String sep; // 路径分隔符
	public static String[] methodNameArray; // 方法名数组
	public static String[] pageNameArray; // 页面名数组
	public static String default_package_path;

	public static String driver;
	public static String url;
	public static String username;
	public static String password;
	public static String table_name;

	public static String name_style; // 命名风格
	public static boolean isCamelCaseMethodName;
	public static boolean isCamelCaseAttrName;

	public static String package_name;

	public static String pojo_package_name;
	public static String pojo_full_package_name;
	public static String pojo_class_name;
	public static String pojo_file_path;
	public static String pojo_import_name;

	public static String controller_package_name;
	public static String controller_full_package_name;
	public static String controller_class_name;
	public static String controller_file_path;

	public static String service_package_name;
	public static String service_full_package_name;
	public static String service_class_name;
	public static String service_file_path;
	public static String service_import_name;

	public static String service_impl_package_name;
	public static String service_impl_full_package_name;
	public static String service_impl_class_name;
	public static String service_impl_file_path;

	public static String dao_package_name;
	public static String dao_full_package_name;
	public static String dao_class_name;
	public static String dao_file_path;
	public static String dao_import_name;

	public static String mapper_package_name;
	public static String mapper_class_name;
	public static String mapper_file_path;

	public static boolean isImportSql; // 判断是否导入java.sql包
	public static String[] colNameArray; // 列名数组
	public static String[] dataTypeArray; // 列数据类型数组
	public static String[] colCommentArray; // 列注释数组
	public static String pk_name; // 主键列名
	public static String pk_type; // 主键列数据类型
	public static int colCount; // 列数量
}