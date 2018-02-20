package layer;

import java.io.IOException;

import template.Appender;

public class ControllerGenerator extends Appender {
	public static void generate() throws IOException {
		sb.setLength(0);
		appendHeader(controller_full_package_name, "public", "class", controller_class_name, 3);
		appendControllerMethods();
		appendFooter();
		generateFile(controller_file_path, String.valueOf(sb));
		System.out.println("生成文件 " + controller_file_path);
	}

	public static void appendControllerMethods() {
		sb.append("\t@Autowired\n");
		sb.append("\tprivate " + service_class_name + " " + re_capitalized(service_class_name) + ";\n");
		for (String pageName : pageNameArray) {
			appendViewMethod(pageName);
		}
		for (int i = 0; i < methodNameArray.length; i++) {
			String methodName = methodNameArray[i];
			boolean isEndsWithPrimaryKey = methodName.endsWith("ey");
			boolean isReturnPojoArray = methodName.startsWith("select");
			boolean isReturnPojo = isEndsWithPrimaryKey && isReturnPojoArray;
			String primaryKey = isEndsWithPrimaryKey ? "/{" + pk_name + "}" : "";
			String pathVariable = isEndsWithPrimaryKey ? "@PathVariable " : "";
			String annotation = "\t@RequestMapping(\"/" + methodName + primaryKey + "\")\n\t@ResponseBody\n";
			String returnType = isReturnPojoArray ? isReturnPojo ? pojo_class_name : "List<" + pojo_class_name + ">"
					: "int";
			String paramType = isEndsWithPrimaryKey ? getJavaType(pk_type) : pojo_class_name;
			String paramName = isEndsWithPrimaryKey ? pk_name : re_capitalized(pojo_class_name);
			String methodBody = "\t\treturn " + re_capitalized(service_class_name) + "." + methodNameArray[i] + "("
					+ paramName + ");\n";
			appendMethod(annotation, returnType, methodName, pathVariable + paramType, paramName, methodBody);
		}
	}
}