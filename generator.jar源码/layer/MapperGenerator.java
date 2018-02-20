package layer;

import java.io.IOException;

import template.Appender;

public class MapperGenerator extends Appender {
	public static void generate() throws IOException {
		sb.setLength(0);
		generateHeader();
		generateResultMap();
		// generateInsert();
		generateInsertSelective();
		generateDeleteByPrimaryKey();
		generateSelectByPrimaryKey();
		generateSelect(methodNameArray[3]);
		generateSelect(methodNameArray[4]);
		generateUpdateSelective();
		generateFooter();
		generateFile(mapper_file_path, String.valueOf(sb));
		System.out.println("生成文件 " + mapper_file_path);
	}

	private static void generateHeader() {
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append(
				"<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
		sb.append("<mapper namespace=\"" + dao_import_name + "\">\n");
	}

	private static void generateResultMap() {
		sb.append("\t<resultMap id=\"BaseResultMap\" type=\"" + pojo_import_name + "\">\n");
		sb.append("\t\t<id column=\"" + pk_name + "\" jdbcType=\"" + getJdbcType(pk_type) + "\" property=\"" + pk_name
				+ "\" />\n");
		for (int i = 0; i < colCount; i++) {
			if (!colNameArray[i].equals(pk_name)) {
				sb.append("\t\t<result column=\"" + colNameArray[i] + "\" jdbcType=\"" + getJdbcType(dataTypeArray[i])
						+ "\" property=\"" + colNameArray[i] + "\" />\n");
			}
		}
		sb.append("\t</resultMap>\n");
	}

	// private static void generateInsert() {
	// sb.append("\n\t<insert id=\"insert\" parameterType=\"" + pojo_import_name +
	// "\">\n");
	// sb.append("\t\tinsert into " + table_name + " values(");
	// for (int i = 0; i < colCount; i++) {
	// if (i != 0) {
	// sb.append(", ");
	// }
	// if (i % 2 == 0) {
	// sb.append("\n\t\t");
	// }
	// sb.append("#{" + colNameArray[i] + ",jdbcType=" +
	// getJdbcType(dataTypeArray[i]) + "}");
	// }
	// sb.append(")\n\t</insert>\n");
	// }

	private static void generateInsertSelective() {
		sb.append("\n\t<insert id=\"" + methodNameArray[0] + "\" parameterType=\"" + pojo_import_name + "\">\n");
		sb.append("\t\tinsert into " + table_name + "\n");
		sb.append("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
		appendMapperStmt(false, true, -1, false);
		sb.append("\t\t</trim>\n");
		sb.append("\t\t<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">\n");
		appendMapperStmt(false, false, -1, true);
		sb.append("\t\t</trim>\n");
		sb.append("\t</insert>\n");
	}

	private static void generateDeleteByPrimaryKey() {
		sb.append("\n\t<delete id=\"" + methodNameArray[1] + "\" parameterType=\"java.lang." + getJavaType(pk_type)
				+ "\">\n");
		sb.append("\t\tdelete from " + table_name);
		sb.append("\n\t\twhere " + pk_name + " = #{" + pk_name + ",jdbcType=" + getJdbcType(pk_type) + "}\n");
		sb.append("\t</delete>\n");
	}

	private static void generateSelectByPrimaryKey() {
		sb.append("\n\t<select id=\"" + methodNameArray[2] + "\" parameterType=\"java.lang." + getJavaType(pk_type)
				+ "\" resultMap=\"BaseResultMap\">\n");
		sb.append("\t\tselect * from " + table_name);
		sb.append("\n\t\twhere " + pk_name + " = #{" + pk_name + ",jdbcType=" + getJdbcType(pk_type) + "}\n");
		sb.append("\t</select>\n");
	}

	private static void generateSelect(String style) {
		int i = style.endsWith("recise") ? 0 : 1;
		sb.append("\n\t<select id=\"" + style + "\" parameterType=\"" + pojo_import_name
				+ "\" resultMap=\"BaseResultMap\">\n");
		sb.append("\t\tselect * from " + table_name);
		sb.append("\n\t\twhere 1=1\n");
		appendMapperStmt(true, true, i, true);
		sb.append("\t</select>\n");
	}

	private static void generateUpdateSelective() {
		sb.append("\n\t<update id=\"" + methodNameArray[5] + "\" parameterType=\"" + pojo_import_name + "\">\n");
		sb.append("\t\tupdate " + table_name);
		sb.append("\n\t\t<set>\n");
		appendMapperStmt(false, true, 0, true);
		sb.append("\t\t</set>\n");
		sb.append("\t\twhere " + pk_name + " = " + "#{" + pk_name + ",jdbcType=" + getJdbcType(pk_type) + "}\n");
		sb.append("\t</update>\n");
	}

	private static void generateFooter() {
		sb.append("</mapper>\n");
	}
}