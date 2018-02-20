package layer;

import java.io.IOException;

import template.Appender;

public class PojoGenerator extends Appender {
	public static void generate() throws IOException {
		sb.setLength(0);
		appendHeader(pojo_full_package_name, "public", "class", pojo_class_name, 0);
		appendPojoAttributes();
		appendPojoMethods();
		appendFooter();
		generateFile(pojo_file_path, String.valueOf(sb));
		System.out.println("生成文件 " + pojo_file_path);
	}

	public static void appendPojoAttributes() {
		for (int i = 0; i < colCount; i++) {
			String colComment = colCommentArray[i];
			colComment = colComment == null ? "" : " // " + colComment;
			appendAttribute(getJavaType(dataTypeArray[i]), colNameArray[i], colComment);
		}
	}

	public static void appendPojoMethods() {
		for (int i = 0; i < colCount; i++) {
			String type = getJavaType(dataTypeArray[i]);
			boolean isDateOrTime = type.equals("Date") || type.equals("Time") || type.equals("Timestamp");
			String annotation = isDateOrTime
					? "\t@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\", timezone = \"GMT+8\")\n"
					: "";
			String attrName = colNameArray[i];
			String getMethodName = isCamelCaseMethodName ? "get" + capitalized(colNameArray[i])
					: "get_" + colNameArray[i];
			String setMethodName = isCamelCaseMethodName ? "set" + capitalized(colNameArray[i])
					: "set_" + colNameArray[i];
			appendGetAndSetMethod(annotation, type, attrName, getMethodName, setMethodName);
		}
	}
}