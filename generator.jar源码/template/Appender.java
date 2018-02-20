package template;

import config.Config;

public class Appender extends Config {
	public static StringBuffer sb = new StringBuffer();

	// style表示层类型：0--pojo 1--dao,service 2--serviceImpl 3--controller
	public static void appendHeader(String package_name, String modifier, String type, String className, int style) {
		boolean isImportOther = false;
		if (!package_name.equals("")) {
			sb.append("package " + package_name + ";\n\n");
		}
		if (style == 0 && isImportSql) {
			sb.append("import java.sql.*;\n\n");
			sb.append("import com.fasterxml.jackson.annotation.JsonFormat;\n\n");
		} else if (style == 1) {
			sb.append("import java.util.List;\n\n");
			if (!package_name.equals(pojo_full_package_name)) {
				sb.append("import " + re_capitalized(pojo_import_name) + ";\n\n");
			}
		} else if (style == 2) {
			sb.append("import java.util.List;\n\n");
			sb.append("import org.springframework.stereotype.Service;\n");
			sb.append("import org.springframework.beans.factory.annotation.Autowired;\n\n");
			if (!package_name.equals(pojo_full_package_name)) {
				sb.append("import " + pojo_import_name + ";\n");
				isImportOther = true;
			}
			if (!package_name.equals(dao_full_package_name)) {
				sb.append("import " + dao_import_name + ";\n");
				isImportOther = true;
			}
			if (!package_name.equals(service_full_package_name)) {
				sb.append("import " + service_import_name + ";\n");
				isImportOther = true;
			}
			if (isImportOther) {
				sb.append("\n");
			}
			sb.append("@Service\n");
		} else if (style == 3) {
			sb.append("import java.util.List;\n\n");
			sb.append("import org.springframework.stereotype.Controller;\n");
			sb.append("import org.springframework.web.bind.annotation.PathVariable;\n");
			sb.append("import org.springframework.web.bind.annotation.ResponseBody;\n");
			sb.append("import org.springframework.web.bind.annotation.RequestMapping;\n");
			sb.append("import org.springframework.beans.factory.annotation.Autowired;\n\n");
			isImportOther = false;
			if (!package_name.equals(pojo_full_package_name)) {
				sb.append("import " + pojo_import_name + ";\n");
				isImportOther = true;
			}
			if (!package_name.equals(service_full_package_name)) {
				sb.append("import " + service_import_name + ";\n");
				isImportOther = true;
			}
			if (isImportOther) {
				sb.append("\n");
			}
			sb.append("@Controller\n");
			sb.append("@RequestMapping(\"/" + re_capitalized(pojo_class_name) + "\")\n");
		}
		if (style == 2) {
			sb.append(modifier + " " + type + " " + className + " implements " + service_class_name + " {");
		} else {
			sb.append(modifier + " " + type + " " + className + " {");
		}
		if (style != 1) {
			sb.append("\n");
		}
	}

	public static void appendInterfaceMethod(String returnType, String methodName, String paramType, String paramName) {
		sb.append("\n\t" + returnType + " " + methodName + "(" + paramType + " " + paramName + ");\n");
	}

	public static void appendInterfaceMethods() {
		for (int i = 0; i < methodNameArray.length; i++) {
			String methodName = methodNameArray[i];
			boolean isEndsWithPrimaryKey = methodName.endsWith("ey");
			boolean isReturnPojoArray = methodName.startsWith("select");
			boolean isReturnPojo = isEndsWithPrimaryKey && isReturnPojoArray;
			String returnType = isReturnPojoArray ? isReturnPojo ? pojo_class_name : "List<" + pojo_class_name + ">"
					: "int";
			String paramType = isEndsWithPrimaryKey ? getJavaType(pk_type) : pojo_class_name;
			String paramName = isEndsWithPrimaryKey ? pk_name : re_capitalized(pojo_class_name);
			appendInterfaceMethod(returnType, methodName, paramType, paramName);
		}
	}

	public static void appendAttribute(String attrType, String attrName, String colComment) {
		sb.append("\tprivate " + attrType + " " + attrName + ";" + colComment + "\n");
	}

	public static void appendMethod(String annotation, String returnType, String methodName, String paramType,
			String paramName, String methodBody) {
		sb.append("\n" + annotation);
		sb.append("\tpublic " + returnType + " " + methodName + "(" + paramType + " " + paramName + ") {\n");
		sb.append(methodBody);
		sb.append("\t}\n");
	}

	public static void appendGetAndSetMethod(String annotation, String type, String attrName, String getMethodName,
			String setMethodName) {
		sb.append("\n" + annotation);
		sb.append("\tpublic " + type + " " + getMethodName + "() {\n");
		sb.append("\t\treturn " + attrName + ";\n");
		sb.append("\t}\n");
		sb.append("\n\tpublic void " + setMethodName + "(" + type + " " + attrName + ") {\n");
		sb.append("\t\tthis." + attrName + " = " + attrName + ";\n");
		sb.append("\t}\n");
	}

	public static void appendViewMethod(String pageName) {
		String methodName = isCamelCaseMethodName ? pageName + pojo_class_name
				: pageName + "_" + re_capitalized(pojo_class_name);
		sb.append("\n\t@RequestMapping(\"/" + methodName + "\")\n");
		sb.append("\tpublic String " + methodName + "() {\n");
		sb.append("\t\treturn \"" + methodName + "\";\n");
		sb.append("\t}\n");
	}

	public static void appendFooter() {
		sb.append("}\n");
	}

	public static void appendMapperStmt(boolean isAnd, boolean iscol, int selectStyle, boolean isPojo) {
		String prefixForLike = "";
		String suffixForLike = "";
		for (int i = 0; i < colCount; i++) {
			sb.append("\t\t\t<if test=\"" + colNameArray[i] + " != null and " + colNameArray[i] + " != ''\">\n");
			sb.append("\t\t\t\t");
			if (isAnd) {
				sb.append("and ");
			}
			if (iscol) {
				sb.append(colNameArray[i]);
			}
			if (selectStyle == 0) {
				sb.append(" = ");
			} else if (selectStyle == 1) {
				sb.append(" like ");
				prefixForLike = "'%'||";
				suffixForLike = "||'%'";
			}
			if (isPojo) {
				sb.append(prefixForLike + "#{" + colNameArray[i] + ",jdbcType=" + getJdbcType(dataTypeArray[i]) + "}"
						+ suffixForLike);
			}
			if (!isAnd) {
				sb.append(",");
			}
			sb.append("\n\t\t\t</if>\n");
		}
	}
}
