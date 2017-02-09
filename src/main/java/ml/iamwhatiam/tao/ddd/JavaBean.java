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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
	
	private String name;
	
	private String comment;
	
	private List<Property> properties;
	
//	private Constraint cascade;
	
	private Set<String> imports = new TreeSet<String>();
	
	public JavaBean(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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


	public class Property implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private String name;
		
		private String type;
		
		private Object defaultValue;
		
		private String comment;
		
		private List<Constraint> constraints;
		
		public Property(String name, Class<?> javaType) {
			this.name = name;
			this.type = javaType.getSimpleName();
			if(!javaType.isPrimitive() && !javaType.getName().startsWith("java.lang."))
				imports.add(javaType.getName());
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Object getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(Object defaultValue) {
			this.defaultValue = defaultValue;
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
		
	}
	
	public class Constraint implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private String type;
		
		private Map<String, Object> values;
		
		//ignore message, payload and groups
		
		public Constraint(Class<? extends Annotation> type) {
			this.type = type.getSimpleName();
			if(!type.getName().startsWith("java.lang."))
				imports.add(type.getName());
		}
		
		public Constraint(Class<? extends Annotation> type, Map<String, Object> values) {
			this.type = type.getSimpleName();
			if(!type.getName().startsWith("java.lang."))
				imports.add(type.getName());
			this.values = values;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Map<String, Object> getValues() {
			return values;
		}

		public void setValues(Map<String, Object> values) {
			this.values = values;
		}
		
	}

}
