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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private static Logger log = LoggerFactory.getLogger(TransformationHelper.class);
	
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
			if(toUpper)
				sb.append(Character.toUpperCase((char) string[i]));
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
				JavaBean.Constraint constraint = check2constraint(bean, check);
				if(constraint != null) {
					String key = snake2camel(check.getColumns()[0].getName());
					List<JavaBean.Constraint> list = constraints.get(key);
					if(list == null) list = new ArrayList<JavaBean.Constraint>();
					list.add(constraint);
					constraints.put(key, list);
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
	 * experiment<hr>transform table/column check to bean property constraint
	 * 
	 * @param bean meta bean instance
	 * @param check meta table check
	 * @return bean property constraint
	 */
	static JavaBean.Constraint check2constraint(JavaBean bean, Table.Check check) {
		JavaBean.Constraint constraint = null;
		if(check.getColumns().length != 1) {
			log.warn("canot understand check with more than one column");
			return null;
		}
		if(check.getSearchCondition() == null) {
			log.warn("check expression cannot be null");
			return null;
		}
		Class<?> klazz = dataType2javaType(check.getColumns()[0].getDataType());
		Map<String, Object> values = new HashMap<String, Object>();
		String value = check.getSearchCondition().split(">=?|<=?|LIKE|like")[1].trim();
		if(check.getSearchCondition().contains(">")) {
			if(klazz == Date.class) {
				constraint = bean.new Constraint(Future.class);//maybe not correct
				constraint.setValues(null);
				return constraint;
			}	
			else if(klazz == BigDecimal.class || klazz == BigInteger.class) {
				constraint = bean.new Constraint(DecimalMin.class);
				values.put("value", value);
			}	
			else {
				constraint = bean.new Constraint(Min.class);
				values.put("value", Long.parseLong(value));
			}
		}
		else if(check.getSearchCondition().contains("<")) {
			if(klazz == Date.class) {
				constraint = bean.new Constraint(Past.class);//maybe not correct
				constraint.setValues(null);
				return constraint;
			}	
			else if(klazz == BigDecimal.class || klazz == BigInteger.class) {
				constraint = bean.new Constraint(DecimalMax.class);
				values.put("value", value);
			}	
			else {
				constraint = bean.new Constraint(Max.class);
				values.put("value", Long.parseLong(value));
			}
		}
		else if(check.getSearchCondition().contains("LIKE")) {
			constraint = bean.new Constraint(Pattern.class);
			values.put("regexp", value);
		}
		constraint.setValues(values);
		return constraint;
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
			case MYSQL: converter.put(new Table.Column.MySQLDataType(entry.getKey()), ReflectionUtils.findClass(entry.getValue())); break;
			case POSTGRES: converter.put(new Table.Column.PostgresDataType(entry.getKey()), ReflectionUtils.findClass(entry.getValue())); break;
			case ORACLE: converter.put(new Table.Column.OracleDataType(entry.getKey()), ReflectionUtils.findClass(entry.getValue())); break;
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
		if(dataType.equals("TINYINT")) javaType = Integer.TYPE;//maybe boolean
		else if(dataType.equals("SMALLINT")) javaType = Integer.TYPE;
		else if(dataType.equals("MEDIUMINT") || dataType.equals("INT")) {
			if(dataType.isUnsigned()) javaType = Long.TYPE;
			else javaType = Integer.TYPE;
		}
		else if(dataType.equals("BIGINT")) {
			if(dataType.isUnsigned()) javaType = BigInteger.class;
			else javaType = Long.TYPE;
		}
		else if(dataType.equals("DECIMAL")) {
			if(dataType.getPrecision() < 10 && dataType.getScale() == 0) javaType = Integer.TYPE;
			else if(dataType.getPrecision() < 19 && dataType.getScale() == 0) javaType = Long.TYPE;
			else if(dataType.getScale() == 0) javaType = BigInteger.class;
			else javaType = BigDecimal.class;
		}
		else if(dataType.equals("FLOAT")) javaType = Float.TYPE;
		else if(dataType.equals("DOUBLE")) javaType = Double.TYPE;
		else if(dataType.equals("BIT")) {
			if(dataType.get() == 1) javaType = Boolean.TYPE; 
			else javaType = byte[].class;
		}
		else if(dataType.equals("DATE") || dataType.equals("DATETIME") || dataType.equals("TIMESTAMP") || dataType.equals("TIME") || dataType.equals("YEAR"))
			javaType = Date.class;
		else if(dataType.equals("ENUM")) javaType = Enum.class;
		else if(dataType.equals("SET")) javaType = Set.class;
		else if(dataType.equals("CHAR")) javaType = String.class;//maybe char[]
		else if(dataType.equals("GEOMETRY")) javaType = String.class;//user defined java bean!
		else javaType = String.class;
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
		if(dataType.equals("SMALLSERIAL") || dataType.equals("SMALLINT")) javaType = Short.TYPE;
		else if(dataType.equals("SERIAL") || dataType.equals("INTEGER")) javaType = Integer.TYPE;
		else if(dataType.equals("BIGSERIAL") || dataType.equals("BIGINT")) javaType = Long.TYPE;
		else if(dataType.equals("NUMERIC")) {
			if(dataType.getPrecision() < 10 && dataType.getScale() == 0) javaType = Integer.TYPE;
			else if(dataType.getPrecision() < 19 && dataType.getScale() == 0) javaType = Long.TYPE;
			else if(dataType.getScale() == 0) javaType = BigInteger.class;
			else javaType = BigDecimal.class;
		}
		else if(dataType.equals("REAL")) javaType = Float.TYPE;
		else if(dataType.equals("DOUBLE PRECISION")) javaType = Double.TYPE;
		else if(dataType.equals("DATE") || dataType.equals("TIME") || dataType.equals("TIMESTAMP")) javaType = Date.class;
		else if(dataType.equals("BOOLEAN")) javaType = Boolean.TYPE;
		else if(dataType.equals("ENUM")) javaType = Enum.class;
		else if(dataType.equals("JSON")) javaType = String.class;//maybe JSONObject
		else if(dataType.equals("LINE")) javaType = String.class;//geo type should be java bean
		else javaType = String.class;
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
		if(dataType.equals("NUMBER")) {
			if(dataType.getPrecision() < 10 && dataType.getScale() == 0) javaType = Integer.TYPE;
			else if(dataType.getPrecision() < 19 && dataType.getScale() == 0) javaType = Long.TYPE;
			else if(dataType.getScale() == 0) javaType = BigInteger.class;
			else javaType = BigDecimal.class;
		}
		else if(dataType.equals("BINARY_FLOAT")) javaType = Float.TYPE;
		else if(dataType.equals("BINARY_DOUBLE")) javaType = Double.TYPE;
		else if(dataType.equals("DATE") || dataType.equals("TIMESTAMP")) javaType = Date.class;
		else if(dataType.equals("BLOB")) javaType = String.class;//maybe byte[] or InputStream
		else if(dataType.equals("BFILE")) javaType = String.class;//maybe File or URL
		else javaType = String.class;
		return javaType;
	}
	
	public static ViewModel bean2form(JavaBean bean) {
		ViewModel form = new ViewModel();
		form.setName(bean.getName());
		form.setAction(bean.getName());
		List<JavaBean.Property> properties = bean.getProperties();
		for(JavaBean.Property property : properties) {
			if(property.getKlazz() == Set.class) 
				form.addSelect(property2select(property));
//			else if()
			else form.addInput(property2input(property));
		}
		return form;
	}
	
	private static ViewModel.Input property2input(JavaBean.Property property) {
		ViewModel.Input input = new ViewModel.Input(property.getName(), javaType2inputType(property.getKlazz()));
		input.setValue(property.getDefaultValue().toString());
		constraint2attribute(input, property.getConstraints());
		return input;
	}
	
	private static ViewModel.Select property2select(JavaBean.Property property) {
		ViewModel.Select select = new ViewModel.Select();
		select.setName(property.getName());
		List<ViewModel.Option> options = new ArrayList<ViewModel.Option>();
		Class<?> klazz = property.getKlazz();
		//TODO
		select.setOptions(options);
		property.getDefaultValue();
		
		return select;
	}
	
	private static ViewModel.DataList property2datalist(JavaBean.Property property) {
		//TODO
		return null;
	}
	
	private static ViewModel.TextArea property2textArea(JavaBean.Property property) {
		//TODO
		return null;
	}
	
	private static void constraint2attribute(ViewModel.Input owner, List<JavaBean.Constraint> constraints) {
		if(constraints == null || constraints.isEmpty()) return;
		for(JavaBean.Constraint constraint : constraints) {
			if(constraint.getAnnotationType() == Max.class || constraint.getAnnotationType() == DecimalMax.class) {
				Object value = constraint.getValues().get("value");
				if(value == null) value = constraint.getValues().get(null);
				owner.setMax(String.valueOf(value));
			}
			else if(constraint.getAnnotationType() == Min.class || constraint.getAnnotationType() == DecimalMin.class) {
				Object value = constraint.getValues().get("value");
				if(value == null) value = constraint.getValues().get(null);
				owner.setMin(String.valueOf(value));
			}
			else if(constraint.getAnnotationType() == NotNull.class) {
				owner.setRequired(true);
			}
			
			else if(constraint.getAnnotationType() == Size.class) {
				Object value = constraint.getValues().get("max");
				if(value != null)
					owner.setSize(Integer.valueOf(String.valueOf(value)));
			}
			else if(constraint.getAnnotationType() == Pattern.class) {
				Object value = constraint.getValues().get("regexp");
				if(value == null) value = constraint.getValues().get(null);
				owner.setPattern(String.valueOf(value));
			}
			else log.warn("no [{}] correspond constraint handler", constraint.getAnnotationType());
		}
		
	}
	
	private static ViewModel.Input.Type javaType2inputType(Class<?> klazz) {
		ViewModel.Input.Type type = ViewModel.Input.Type.TEXT;
		if(Number.class.isAssignableFrom(klazz))
			type = ViewModel.Input.Type.NUMBER;
		else if(Date.class.isAssignableFrom(klazz))
			type = ViewModel.Input.Type.DATETIME;
		else if(Collection.class.isAssignableFrom(klazz))
			type = ViewModel.Input.Type.CHECKBOX;
		//TODO
		return type;
	}
	
}
