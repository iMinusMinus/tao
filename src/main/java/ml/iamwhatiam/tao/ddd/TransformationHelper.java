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

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import ml.iamwhatiam.tao.vo.Form;

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
		List<Table.ForeignKey> fks = table.getFks();
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
		Set<Table> properties = new HashSet<Table>();
		for(Table.Column column : table.getColumns()) {
			JavaBean.Property property = null;
			Table reference = getReferenceTable(fks, column);
			if(reference != null) {
				//if one to many, List<JavaBean>; if one to one, JavaBean; if many to many/one, JavaBean
				property = bean.new Property(snake2camel(reference.getName()), JavaBean.class);
				property.setBean(table2bean(reference));//XXX cannot hold multiply table info
				properties.add(reference);
			} else {
				property = column2property(bean, column);
			}	
			if(constraints.get(column.getName()) != null)
				property.setConstraints(constraints.get(column.getName()));
			if(!properties.contains(reference))
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
	
	private static Table getReferenceTable(List<Table.ForeignKey> fks, Table.Column column) {
		if(fks != null && !fks.isEmpty()) {
			for(Table.ForeignKey fk : fks)
				for(Table.Column col : fk.getColumns())
					if(col == column)
						return fk.getReferences()[0].getTable();
		}
		return null;
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
		else if(dataType instanceof Table.Column.StandardDataType) 
			return dataType2javaType((Table.Column.StandardDataType) dataType);
		log.error("no defined data type handler!");
		return String.class;
	}
	
	/**
	 * transform standard data type to java type
	 * 
	 * @param dataType
	 * @return
	 */
	public static Class<?> dataType2javaType(Table.Column.StandardDataType dataType) {
		Class<?> javaType = String.class;
		if(dataType.getDataType().equals("BOOLEAN")) javaType = Boolean.TYPE;
		else if(dataType.getDataType().equals("SMALLINT") || dataType.getDataType().equals("INTEGER"))	javaType = Integer.TYPE;
		else if(dataType.getDataType().equals("BIGINT")) javaType = Long.TYPE;
		else if(dataType.getDataType().equals("FLOAT")) javaType = Float.TYPE;
		else if(dataType.getDataType().equals("REAL") || dataType.getDataType().equals("DOUBLE PRECISION")) javaType = Double.TYPE;
		else if(dataType.getDataType().equals("NUMERIC") || dataType.getDataType().equals("DECIMAL")) {
			if(dataType.getScale() != null && dataType.getScale() == 0) {
				if(dataType.getPrecision() != null && dataType.getPrecision() < 10) javaType = Integer.TYPE;
				else if(dataType.getPrecision() != null && dataType.getPrecision() < 19 ) javaType = Long.TYPE;
				else javaType = BigInteger.class;
			} else javaType = BigDecimal.class;
		}
		else if(dataType.getDataType().equals("ENUMERATED")) javaType = Enum.class;
		else if(dataType.getDataType().equals("DATE") || dataType.getDataType().equals("INTERVAL")
				|| dataType.getDataType().equals("TIMESTAMP") || dataType.equals("TIME"))
			javaType = Date.class;
		else if(dataType.getDataType().indexOf("BINARY") >= 0) javaType = String.class;//may be byte[]
		return javaType;
	}
	
	/**
	 * transform MySql data type to java type
	 * 
	 * @param dataType MySql data type
	 * @return java type
	 */
	private static Class<?> dataType2javaType(Table.Column.MySQLDataType dataType) {
		Class<?> javaType = String.class;
		if(dataType.getDataType().equals("TINYINT")) javaType = Integer.TYPE;//maybe boolean
		else if(dataType.getDataType().equals("SMALLINT")) javaType = Integer.TYPE;
		else if(dataType.getDataType().equals("MEDIUMINT") || dataType.getDataType().equals("INT")) {
			if(dataType.isUnsigned()) javaType = Long.TYPE;
			else javaType = Integer.TYPE;
		}
		else if(dataType.getDataType().equals("BIGINT")) {
			if(dataType.isUnsigned()) javaType = BigInteger.class;
			else javaType = Long.TYPE;
		}
		else if(dataType.getDataType().equals("DECIMAL")) {
			if(dataType.getScale() != null && dataType.getScale() == 0) {
				if(dataType.getPrecision() != null && dataType.getPrecision() < 10) javaType = Integer.TYPE;
				else if(dataType.getPrecision() != null && dataType.getPrecision() < 19 ) javaType = Long.TYPE;
				else javaType = BigInteger.class;
			}
			else javaType = BigDecimal.class;
		}
		else if(dataType.getDataType().equals("FLOAT")) javaType = Float.TYPE;
		else if(dataType.getDataType().equals("DOUBLE")) javaType = Double.TYPE;
		else if(dataType.getDataType().equals("BIT")) {
			if(dataType.get() != null && dataType.get() == 1) javaType = Boolean.TYPE; 
			else javaType = byte[].class;
		}
		else if(dataType.getDataType().equals("DATE") || dataType.getDataType().equals("DATETIME") || dataType.getDataType().equals("TIMESTAMP") || dataType.equals("TIME") || dataType.equals("YEAR"))
			javaType = Date.class;
		else if(dataType.getDataType().equals("ENUM")) javaType = Enum.class;
		else if(dataType.equals("SET")) javaType = Set.class;
		else if(dataType.getDataType().equals("CHAR")) javaType = String.class;//maybe char[]
		else if(dataType.getDataType().equals("GEOMETRY")) javaType = String.class;//user defined java bean!
		return javaType;
	}
	
	/**
	 * transform postgres data type to java type
	 * 
	 * @param dataType MySql data type
	 * @return java type
	 */
	private static Class<?> dataType2javaType(Table.Column.PostgresDataType dataType) {
		Class<?> javaType = String.class;
		if(dataType.getDataType().equals("SMALLSERIAL") || dataType.getDataType().equals("SMALLINT")) javaType = Short.TYPE;
		else if(dataType.getDataType().equals("SERIAL") || dataType.getDataType().equals("INTEGER")) javaType = Integer.TYPE;
		else if(dataType.getDataType().equals("BIGSERIAL") || dataType.getDataType().equals("BIGINT")) javaType = Long.TYPE;
		else if(dataType.getDataType().equals("NUMERIC")) {
			if(dataType.getScale() != null && dataType.getScale() == 0) {
				if(dataType.getPrecision() != null && dataType.getPrecision() < 10) javaType = Integer.TYPE;
				else if(dataType.getPrecision() != null && dataType.getPrecision() < 19) javaType = Long.TYPE;
				else javaType = BigInteger.class;
			}
			else javaType = BigDecimal.class;
		}
		else if(dataType.getDataType().equals("REAL")) javaType = Float.TYPE;
		else if(dataType.getDataType().equals("DOUBLE PRECISION")) javaType = Double.TYPE;
		else if(dataType.getDataType().equals("DATE") || dataType.getDataType().equals("TIME") || dataType.getDataType().equals("TIMESTAMP")) javaType = Date.class;
		else if(dataType.getDataType().equals("BOOLEAN")) javaType = Boolean.TYPE;
		else if(dataType.getDataType().equals("ENUM")) javaType = Enum.class;
		else if(dataType.getDataType().equals("JSON")) javaType = String.class;//maybe JSONObject
		else if(dataType.getDataType().equals("LINE")) javaType = String.class;//geo type should be java bean
		return javaType;
	}
	
	/**
	 * transform Oracle data type to java type
	 * 
	 * @param dataType MySql data type
	 * @return java type
	 */
	private static Class<?> dataType2javaType(Table.Column.OracleDataType dataType) {
		Class<?> javaType = String.class;
		if(dataType.getDataType().equals("NUMBER")) {
			if(dataType.getScale() != null && dataType.getScale() == 0) {
				if(dataType.getPrecision() != null && dataType.getPrecision() < 10) javaType = Integer.TYPE;
				else if(dataType.getPrecision() != null && dataType.getPrecision() < 19) javaType = Long.TYPE;
				else javaType = BigInteger.class;
			}
			else javaType = BigDecimal.class;
		}
		else if(dataType.getDataType().equals("BINARY_FLOAT")) javaType = Float.TYPE;
		else if(dataType.getDataType().equals("BINARY_DOUBLE")) javaType = Double.TYPE;
		else if(dataType.getDataType().equals("DATE") || dataType.getDataType().equals("TIMESTAMP")) javaType = Date.class;
		else if(dataType.getDataType().equals("BLOB")) javaType = String.class;//maybe byte[] or InputStream
		else if(dataType.getDataType().equals("BFILE")) javaType = String.class;//maybe File or URL
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
			else if(property.getKlazz() == List.class)
				form.addDatalist(property2datalist(property));
			else if(property.getKlazz() == byte[].class)
				form.addTextAreas(property2textArea(property));
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
		Class<?> klazz = property.getKlazz();
		if(Set.class.isAssignableFrom(klazz) && property.getValue() != null) {
			for(Form prop : (Set<Form>) property.getValue())
				select.addOption(new ViewModel.Option(prop.getAlt(), prop.getOid()));
		}
		return select;
	}
	
	private static ViewModel.DataList property2datalist(JavaBean.Property property) {
		ViewModel.DataList dataList = new ViewModel.DataList(property.getName());
		if(List.class.isAssignableFrom(property.getKlazz()) && property.getValue() != null) {
			for(Form prop : (List<Form>) property.getValue())
				dataList.addOption(new ViewModel.Option(prop.getAlt(), prop.getOid()));
		}
		return dataList;
	}
	
	private static ViewModel.TextArea property2textArea(JavaBean.Property property) {
		ViewModel.TextArea textArea = new ViewModel.TextArea();
		textArea.setName(property.getName());
		if(property.getDefaultValue() != null)
			textArea.setDefaultValue(String.valueOf(property.getDefaultValue()));
		if(property.getValue() != null)
			textArea.setValue(String.valueOf(property.getValue()));
		return textArea;
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
		else if(File.class.isAssignableFrom(klazz))
			type = ViewModel.Input.Type.FILE;
		else if(URL.class.isAssignableFrom(klazz))//regexp
			type = ViewModel.Input.Type.URL;
		//TODO other type
		return type;
	}
	
}
