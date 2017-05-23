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

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * meta java bean
 * 
 * @author iMinusMinus
 * @since 2017-02-09
 * @version 0.0.1
 *
 */
public class JavaBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Logger log = LoggerFactory.getLogger(JavaBean.class);
	
	private static Set<String> keywords;
	
	static {
		keywords = new HashSet<String>();
        keywords.add("package");
        keywords.add("class");
        keywords.add("interface");
        keywords.add("import");
        keywords.add("extends");
        keywords.add("implements");
        keywords.add("public");
        keywords.add("protected");
        keywords.add("private");
        keywords.add("abstract");
        keywords.add("native");
        keywords.add("static");
        keywords.add("final");
        keywords.add("transient");
        keywords.add("volatile");
        keywords.add("boolean");
        keywords.add("byte");
        keywords.add("char");
        keywords.add("short");
        keywords.add("int");
        keywords.add("long");
        keywords.add("float");
        keywords.add("double");
        keywords.add("void");
        keywords.add("return");
        keywords.add("if");
        keywords.add("else");
        keywords.add("for");
        keywords.add("do");
        keywords.add("while");
        keywords.add("switch");
        keywords.add("case");
        keywords.add("default");
        keywords.add("continue");
        keywords.add("break");
        keywords.add("goto");
        keywords.add("try");
        keywords.add("catch");
        keywords.add("finally");
        keywords.add("throw");
        keywords.add("throws");
        keywords.add("new");
        keywords.add("true");
        keywords.add("false");
        keywords.add("null");
        keywords.add("instanceof");
        keywords.add("synchronized");
        keywords.add("super");
        keywords.add("this");
        keywords.add("strictfp");
        keywords.add("enum");
        keywords.add("const");
        keywords.add("assert");
	}
	
	private String name;
	
	private String comment;
	
	private List<Property> properties;
	
//	private Constraint cascade;
	
	private Set<String> imports = new TreeSet<String>();
	
	public JavaBean(String name) {
		if(keywords.contains(name)) {
			log.error("field name must not be java keyword: [{}]", name);
			throw new IllegalArgumentException("field name must not be java keyword");
		}
		else this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(keywords.contains(name)) {
			log.error("field name must not be java keyword: [{}]", name);
			throw new IllegalArgumentException("field name must not be java keyword");
		}
		else this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public void addProperty(Property property) {
		if(properties == null)
			properties = new ArrayList<Property>();
		properties.add(property);
	}

	public Set<String> getImports() {
		return imports;
	}


	public class Property implements Serializable {

		private static final long serialVersionUID = 1L;
		
		/**
		 * field name
		 */
		private String name;
		
		/**
		 * field type name
		 */
		private String type;
		
		/**
		 * field type
		 */
		private Class<?> klazz;
		
		/**
		 * field default value
		 */
		private Object defaultValue;
		
		/**
		 * field value
		 */
		private Object value;
		
		/**
		 * field comment
		 */
		private String comment;
		
		/**
		 * field constraints
		 */
		private List<Constraint> constraints;
		
		/**
		 * inner bean
		 */
		private JavaBean bean;
		
		public Property(String name, Class<?> javaType) {
			this.klazz = javaType;
			if(keywords.contains(name)) {
				log.error("field name must not be java keyword: [{}]", name);
				throw new IllegalArgumentException("field name must not be java keyword");
			}
			else this.name = name;
			this.type = javaType.getName();
			if(!javaType.isPrimitive() && !javaType.getPackage().getName().equals("java.lang"))
				imports.add(javaType.getName());
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			if(keywords.contains(name)) {
				log.error("field name must not be java keyword: [{}]", name);
				throw new IllegalArgumentException("field name must not be java keyword");
			}
			else this.name = name;
		}

		public String getType() {
			return type;
		}

		void setType(String type) {
			this.type = type;
		}
		
		public Class<?> getKlazz() {
			return klazz;
		}

		public Object getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(Object defaultValue) {
			this.defaultValue = defaultValue;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public List<Constraint> getConstraints() {
			return constraints;
		}
		
		public void addConstraint(Constraint constraint) {
			if(constraints == null)
				constraints = new ArrayList<Constraint>();
			constraints.add(constraint);
		}

		public void setConstraints(List<Constraint> constraints) {
			this.constraints = constraints;
		}

		public JavaBean getBean() {
			return bean;
		}

		public void setBean(JavaBean bean) {
			this.bean = bean;
		}
		
	}
	
	public class Constraint implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private String type;
		
		private Class<? extends Annotation> annotationType;
		
		private Map<String, Object> values;
		
		//ignore message, payload and groups
		
		public Constraint(Class<? extends Annotation> type) {
			this.type = type.getSimpleName();
			this.annotationType = type;
			if(!type.getPackage().getName().equals("java.lang"))
				imports.add(type.getName());
		}
		
		public Constraint(Class<? extends Annotation> type, Map<String, Object> values) {
			this.type = type.getSimpleName();
			this.annotationType = type;
			if(!type.getPackage().getName().equals("java.lang"))
				imports.add(type.getName());
			this.values = values;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Class<? extends Annotation> getAnnotationType() {
			return annotationType;
		}

		public Map<String, Object> getValues() {
			return values;
		}

		public void setValues(Map<String, Object> values) {
			this.values = values;
		}
		
	}

}
