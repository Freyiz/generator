package layer;

import java.io.IOException;

import template.Appender;

public class ServiceImplGenerator extends Appender {
	public static void generate() throws IOException {
		sb.setLength(0);
		appendHeader(service_impl_full_package_name, "public", "class", service_impl_class_name, 2);
		appendImplMethods();
		appendFooter();
		generateFile(service_impl_file_path, String.valueOf(sb));
		System.out.println("生成文件 " + service_impl_file_path);
	}

	public static void appendImplMethods() {
		sb.append("\t@Autowired\n");
		sb.append("\tprivate " + dao_class_name + " " + re_capitalized(dao_class_name) + ";\n");
		for (int i = 0; i < methodNameArray.length; i++) {
			String methodName = methodNameArray[i];
			boolean isEndsWithPrimaryKey = methodName.endsWith("ey");
			boolean isReturnPojoArray = methodName.startsWith("select");
			boolean isReturnPojo = isEndsWithPrimaryKey && isReturnPojoArray;
			String annotation = "\t@Override\n";
			String returnType = isReturnPojoArray ? isReturnPojo ? pojo_class_name : "List<" + pojo_class_name + ">"
					: "int";
			String paramType = isEndsWithPrimaryKey ? getJavaType(pk_type) : pojo_class_name;
			String paramName = isEndsWithPrimaryKey ? pk_name : re_capitalized(pojo_class_name);
			String methodBody = "\t\treturn " + re_capitalized(dao_class_name) + "." + methodNameArray[i] + "("
					+ paramName + ");\n";
			appendMethod(annotation, returnType, methodName, paramType, paramName, methodBody);
		}
	}
}
