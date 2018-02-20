package layer;

import java.io.IOException;

import template.Appender;

public class DaoGenerator extends Appender {
	public static void generate() throws IOException {
		sb.setLength(0);
		appendHeader(dao_full_package_name, "public", "interface", dao_class_name, 1);
		appendInterfaceMethods();
		appendFooter();
		generateFile(dao_file_path, String.valueOf(sb));
		System.out.println("生成文件 " + dao_file_path);
	}
}