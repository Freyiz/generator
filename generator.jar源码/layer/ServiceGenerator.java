package layer;

import java.io.IOException;

import template.Appender;

public class ServiceGenerator extends Appender {
	public static void generate() throws IOException {
		sb.setLength(0);
		appendHeader(service_full_package_name, "public", "interface", service_class_name, 1);
		appendInterfaceMethods();
		appendFooter();
		generateFile(service_file_path, String.valueOf(sb));
		System.out.println("生成文件 " + service_file_path);
	}
}
