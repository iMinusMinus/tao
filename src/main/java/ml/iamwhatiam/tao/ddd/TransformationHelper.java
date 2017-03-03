/**
 * MIT License
 * 
 * Copyright (c) 2016 iMinusMinus
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ml.iamwhatiam.tao.ddd;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ml.iamwhatiam.tao.constraints.Enumeration;
import ml.iamwhatiam.tao.util.ReflectionUtils;

/**
 * transform from relation data to java bean;
 * transform from java bean to view model; 
 * 
 * @author iMinusMinus
 * @since 2017-02-09
 * @version 0.0.1
 *
 */
public class TransformationHelper {
	
	private static final Map<String, String> PRIMITIVE;
	
	private static ThreadLocal<Map<Table.Column.DataType, Class<?>>> mapping;
	
	static {
		PRIMITIVE = new HashMap<String, String>();
		PRIMITIVE.put("boolean", "Boolean");
		PRIMITIVE.put("byte", "Byte");
		PRIMITIVE.put("char", "Character");
		PRIMITIVE.put("short", "Short");
		PRIMITIVE.put("int", "Integer");
		PRIMITIVE.put("long", "Long");
		PRIMITIVE.put("float", "Float");
		PRIMITIVE.put("double", "Double");
	}
	
	/**
	 * transform literal: from snake_case_literal to snakeCaseLiteral
	 * 
	 * @param snakeCase snake case literal
	 * @return camelCase
	 */
	public static String snake2camel(String snakeCase) {
		if(snakeCase == null) return null;
		char[] string = snakeCase.toCharArray();
		boolean toUpper = false;
		StringBuilder sb = new StringBuilder();
		for(int i = 0, j = string.length; i < j; i++) {
			if(string[i] == '_') {
				toUpper = true;
				continue;
			}
			if(toUpper && string[i] >= 'a' && string[i] <= 'z')
				sb.append((char) (string[i] - 32));
			else sb.append(string[i]);
			toUpper = false;
		}
		return sb.toString();
	}
	
	/**
	 * transform meta table to meta bean
	 * 
	 * @param table meta table
	 * @return meta bean
	 */
	public static JavaBean table2bean(Table table) {
		if(table == null) return null;
		String name = table.getName();
		if(table.getDialect() != Dialect.POSTGRES)//pg case sensitive
			name = name.toLowerCase();
		JavaBean bean = new JavaBean(snake2camel(name));
		Map<String, List<JavaBean.Constraint>> constraints = new HashMap<String, List<JavaBean.Constraint>>();
		if(table.getChecks() != null)
			for(Table.Check check : table.getChecks()) {
				Map<String, JavaBean.Constraint> constraint = check2constraint(bean, check);
				if(constraint != null) {
					
				}
			}
		for(Table.Column column : table.getColumns()) {
			JavaBean.Property property = column2property(bean, column);
			if(constraints.get(column.getName()) != null)
				property.setConstraints(constraints.get(column.getName()));
			bean.addProperty(property);
		}
		return bean;
	}
	
	/**
	 * transform meta table column to meta bean property
	 * 
	 * @param bean meta java bean instance
	 * @param column meta table column
	 * @return meta bean property
	 */
	static JavaBean.Property column2property(JavaBean bean, Table.Column column) {
		String name = column.getName();
		if(column.getTable().getDialect() != Dialect.POSTGRES)
			name = name.toLowerCase();
		JavaBean.Property property = bean.new Property(snake2camel(name), dataType2javaType(column.getDataType()));
		property.setComment(column.getComment());
		property.setDefaultValue(column.getDefaultValue());
		if("Enum".equals(property.getType())) {
			property.setType("String");
			property.addConstraint(bean.new Constraint(Enumeration.class));
		}
		if(column.isNullable() && isPrimitive(property.getType())) {//correct java type
			property.setType(primitive2wrapper(property.getType()));
		}
		return property;
	}
	
	private static boolean isPrimitive(String type) {
		return PRIMITIVE.get(type) != null;
	}
	
	private static String primitive2wrapper(String type) {
		return PRIMITIVE.get(type);
	}
	
	/**
	 * transform table/column check to bean property constraint
	 * 
	 * @param bean meta bean instance
	 * @param check meta table check
	 * @return bean property constraint
	 */
	static Map<String, JavaBean.Constraint> check2constraint(JavaBean bean, Table.Check check) {
//		JavaBean.Constraint constraint = bean.new Constraint();
		//TODO
		return null;
	}
	
	/**
	 * registe type converter
	 * 
	 * @param dialect SQL Dialect
	 * @param predefined pre-defined type converter
	 */
	public static void registeDataTypeMapping(Dialect dialect, Map<String, String> predefined) {
		if(mapping == null)
			mapping = new ThreadLocal<Map<Table.Column.DataType, Class<?>>>();
		Map<Table.Column.DataType, Class<?>> converter = mapping.get();
		if(converter == null) converter = new HashMap<Table.Column.DataType, Class<?>>();
		Set<Map.Entry<String,String>> keySet = predefined.entrySet();
		for(Map.Entry<String,String> entry : keySet) {
			switch(dialect) {
			case MYSQL: converter.put(Table.Column.MySQLDataType.valueOf(entry.getKey()), ReflectionUtils.findClass(entry.getValue())); break;
			case POSTGRES: converter.put(Table.Column.PostgresDataType.valueOf(entry.getKey()), ReflectionUtils.findClass(entry.getValue())); break;
			case ORACLE: converter.put(Table.Column.OracleDataType.valueOf(entry.getKey()), ReflectionUtils.findClass(entry.getValue())); break;
			default:
			}
		}
		mapping.set(converter);
	}
	
	/**
	 * transform sql data type to java type
	 * 
	 * @param dataType sql data type
	 * @return java type
	 */
	public static Class<?> dataType2javaType(Table.Column.DataType dataType) {
		if(mapping != null && mapping.get() != null && mapping.get().get(dataType) != null)
			return mapping.get().get(dataType);
		if(dataType instanceof Table.Column.MySQLDataType)
			return dataType2javaType((Table.Column.MySQLDataType) dataType);
		else if(dataType instanceof Table.Column.PostgresDataType)
			return dataType2javaType((Table.Column.PostgresDataType) dataType);
		else if(dataType instanceof Table.Column.OracleDataType)
			return dataType2javaType((Table.Column.OracleDataType) dataType);
		else {
			throw new NotImplementedException("no data type to java type converter defined!");//TODO
		}
	}
	
	/**
	 * transform MySql data type to java type
	 * 
	 * @param dataType MySql data type
	 * @return java type
	 */
	private static Class<?> dataType2javaType(Table.Column.MySQLDataType dataType) {
		Class<?> javaType = null;
		switch(dataType) {
		case TINYINT://maybe boolean
		case SMALLINT: javaType = Integer.TYPE; break;
		case MEDIUMINT:
		case INT: 
			if(dataType.isUnsigned())
				javaType = Long.TYPE;
			else javaType = Integer.TYPE; break;
		case BIGINT:
			if(dataType.isUnsigned())
				javaType = BigInteger.class;
			else javaType = Long.TYPE; break;
		case DECIMAL:
			if(dataType.getPrecision() < 10 && dataType.getScale() == 0) javaType = Integer.TYPE;
			else if(dataType.getPrecision() < 19 && dataType.getScale() == 0) javaType = Long.TYPE;
			else if(dataType.getScale() == 0) javaType = BigInteger.class;
			else javaType = BigDecimal.class;
			break;
		case FLOAT: javaType = Float.TYPE; break;
		case DOUBLE: javaType = Double.TYPE; break;
		case BIT: 
			if(dataType.get() == 1) javaType = Boolean.TYPE; 
			else javaType = byte[].class;
			break;
		case DATE:
		case DATETIME:
		case TIMESTAMP:	
		case TIME:
		case YEAR: javaType = Date.class; break;//maybe long or String
		case CHAR://maybe char[]
		case VARCHAR:
		case TINYTEXT:
		case MEDIUMTEXT:
		case TEXT:
		case LONGTEXT: javaType = String.class; break;
		case BINARY:
		case VARBINARY:
		case TINYBLOB:
		case MEDIUMBLOB:
		case BLOB:
		case LONGBLOB: javaType = String.class; break;
		case ENUM: javaType = Enum.class; break;
		case SET: javaType = Set.class; break;
		case GEOMETRY:
		case POINT:
		case LINESTRING:
		case POLYGON:
		case MULTIPOINT:
		case MULTILINESTRING:
		case MULTIPOLYGON:
		case GEOMETRYCOLLECTION: javaType = String.class; break;//user defined java bean!
		default:
		}
		return javaType;
	}
	
	/**
	 * transform postgres data type to java type
	 * 
	 * @param dataType MySql data type
	 * @return java type
	 */
	private static Class<?> dataType2javaType(Table.Column.PostgresDataType dataType) {
		Class<?> javaType = null;
		switch(dataType) {
		case SMALLSERIAL:
		case SMALLINT: javaType = Short.TYPE; break;
		case SERIAL:
		case INTEGER: javaType = Integer.TYPE; break;
		case BIGSERIAL:
		case BIGINT: javaType = Long.TYPE; break;
		case NUMERIC:
			if(dataType.getPrecision() < 10 && dataType.getScale() == 0) javaType = Integer.TYPE;
			else if(dataType.getPrecision() < 19 && dataType.getScale() == 0) javaType = Long.TYPE;
			else if(dataType.getScale() == 0) javaType = BigInteger.class;
			else javaType = BigDecimal.class;
			break;
		case REAL: javaType = Float.TYPE; break;
		case DOUBLE_PRECISION: javaType = Double.TYPE; break;
		case MONEY: javaType = String.class; break;
		case CHARACTER:
		case CHARACTER_VARING:
		case TEXT: javaType = String.class; break;
		case BYTEA:	javaType = String.class; break;
		case DATE:
		case TIME:
		case TIMESTAMP: javaType = Date.class; break;
		case INTERVAL:	break;
		case BOOLEAN: javaType = Boolean.TYPE; break;
		case ENUM: javaType = Enum.class; break;
		case POINT:
		case LINE:
		case LSEG:
		case BOX:
		case PATH:
		case POLYGON:
		case CIRCLE:
		case CIDR:
		case INET:
		case MACADDR: javaType = String.class; break;
		case BIT:
		case BIT_VARING: javaType = String.class; break;
		case XML:
		case JSON://maybe JSONObject
		case JSONB: javaType = String.class; break;
		default:	
		}
		return javaType;
	}
	
	/**
	 * transform Oracle data type to java type
	 * 
	 * @param dataType MySql data type
	 * @return java type
	 */
	private static Class<?> dataType2javaType(Table.Column.OracleDataType dataType) {
		Class<?> javaType = null;
		switch(dataType) {
		case CHAR:
		case VARCHAR2:
		case NCHAR:
		case NVARCHAR2:
		case CLOB:
		case NCLOB: javaType = String.class; break;
		case NUMBER: 
			if(dataType.getPrecision() < 10 && dataType.getScale() == 0) javaType = Integer.TYPE;
			else if(dataType.getPrecision() < 19 && dataType.getScale() == 0) javaType = Long.TYPE;
			else if(dataType.getScale() == 0) javaType = BigInteger.class;
			else javaType = BigDecimal.class;
			break;
		case BINARY_FLOAT: javaType = Float.TYPE; break;
		case BINARY_DOUBLE: javaType = Double.TYPE; break;
		case DATE:
		case TIMESTAMP: javaType = Date.class; break;
		case INTERVAL:
		case BLOB://maybe byte[] or InputStream
		case BFILE://maybe File
		default:	
		}
		return javaType;
	}
	
	public static ViewModel bean2form(JavaBean bean) {
		ViewModel form = new ViewModel();
		//TODO
		return form;
	}
	
	private static ViewModel.Input.Type javaType2inputType(Class<?> klazz) {
		//TODO
		return null;
	}
	
}
