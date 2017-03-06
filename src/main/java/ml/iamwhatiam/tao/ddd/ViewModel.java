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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * view model
 * @see https://www.w3.org/TR/2000/CR-DOM-Level-2-20000510/html.html#ID-40002357
 * @see https://www.w3.org/TR/html5/forms.html#forms
 * 
 * @author iMinusMinus
 * @since 2017-02-23
 * @version 0.0.1
 *
 */
public class ViewModel {
	
	private static volatile Logger log;

	private String name;
	
	/* ignored attributes: 
	private String id;
	
	private String[] classes;
	
	private Charset acceptCharset;
	
	private boolean autoComplete = true;
	
	private EncType encType = "application/x-www-form-urlencoded";//multipart/form-data, text/plain
	
	private Method method = "GET";//POST
	
	private boolean noValidate = true;
	
	private Target target = "_self";//_blank, _parent, _top
	*/
	
	private String action;
	
	private List<Input> inputs;
	
	private List<Select> selects;
	
	private List<DataList> datalist;
	
	private List<TextArea> textAreas;
	
	//button
	
	//keygen
	
	//output
	
	private static Logger getLogger() {
		if(log == null)
			log = LoggerFactory.getLogger(ViewModel.class);
		return log;
	}
	
	public static class Input {
		
		private String name;
		
		private Type type = Type.TEXT;
		
		private String value;
		
		private Integer maxLength;//maxlength
		
		private Integer minLength;//minlength
		
		private Integer size;//20
		
		private boolean required;
		
		/*ignored attributes:
		private boolean checked;//checkbox, radio
		
		private boolean readOnly;//readonly="readonly"; text, search, url, tel, email, password, date, time, month, week, datetime, number, range
		
		private boolean disabled;//disabled="disabled"
		
		private boolean multiple;//email, file
		
		private String src;//image
		
		private String list;//text, search, url, tel, email, date, time, number, range, color
		*/
		
		private String pattern;
		
		private String min;
		
		private String max;
		
		private String step;
		
		private String placeHolder;
		
		private int dispaly;//0, 1 display:none, 2 visibility:hidden, 4 read, 8 query field, 16 editable, 32 newborn
		
		public Input(String name) {
			this.name = name;
		}
		
		public Input(String name, Type type) {
			this.name = name;
			this.type = type;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Integer getMaxLength() {
			return maxLength;
		}

		public void setMaxLength(Integer maxLength) {
			if(type != Type.TEXT && type != Type.SEARCH && type != Type.URL && type != Type.TEL 
					&& type != Type.EMAIL && type != Type.PASSWORD)
				getLogger().warn("input with type [{}] has no attribute 'maxlength'", type);
			else this.maxLength = maxLength;
		}

		public Integer getMinLength() {
			return minLength;
		}

		public void setMinLength(Integer minLength) {
			if(type != Type.TEXT && type != Type.SEARCH && type != Type.URL && type != Type.TEL 
					&& type != Type.EMAIL && type != Type.PASSWORD)
				getLogger().warn("input with type [{}] has no attribute 'minlength'", type);
			else this.minLength = minLength;
		}

		public Integer getSize() {
			return size;
		}

		public void setSize(Integer size) {
			if(type != Type.TEXT && type != Type.SEARCH && type != Type.URL && type != Type.TEL 
					&& type != Type.EMAIL && type != Type.PASSWORD)
				getLogger().warn("input with type [{}] has no attribute 'size'", type);
			else this.size = size;
		}

		public boolean isRequired() {
			return required;
		}

		public void setRequired(boolean required) {
			if(type == Type.HIDDEN || type == Type.RANGE || type == Type.COLOR || type == Type.CHECKBOX 
					|| type == Type.RADIO || type == Type.FILE || type == Type.SUBMIT || type == Type.IMAGE
					|| type == Type.BUTTON || type == Type.RESET)
				getLogger().warn("input with type [{}] has no attribute 'required'", type);
			else this.required = required;
		}

		public String getPattern() {
			return pattern;
		}

		public void setPattern(String pattern) {
			if(type != Type.TEXT && type != Type.SEARCH && type != Type.URL && type != Type.TEL 
					&& type != Type.EMAIL && type != Type.PASSWORD)
				getLogger().warn("input with type [{}] has no attribute 'pattern'", type);
			else this.pattern = pattern;
		}

		public String getMin() {
			return min;
		}

		public void setMin(String min) {
			if(type != Type.NUMBER && type != Type.RANGE && type != Type.DATE && type != Type.TIME 
					&& type != Type.MONTH && type != Type.WEEK && type != Type.DATETIME)
				getLogger().warn("input with type [{}] has no attribute 'min'", type);
			else this.min = min;
		}

		public String getMax() {
			return max;
		}

		public void setMax(String max) {
			if(type != Type.NUMBER && type != Type.RANGE && type != Type.DATE && type != Type.TIME 
					&& type != Type.MONTH && type != Type.WEEK && type != Type.DATETIME)
				getLogger().warn("input with type [{}] has no attribute 'max'", type);
			else this.max = max;
		}

		public String getStep() {
			return step;
		}

		public void setStep(String step) {
			if(type != Type.NUMBER && type != Type.RANGE && type != Type.DATE && type != Type.TIME 
					&& type != Type.MONTH && type != Type.WEEK && type != Type.DATETIME)
				getLogger().warn("input with type [{}] has no attribute 'step'", type);
			else this.step = step;
		}

		public String getPlaceHolder() {
			return placeHolder;
		}

		public void setPlaceHolder(String placeHolder) {
			if(type != Type.TEXT && type != Type.SEARCH && type != Type.URL && type != Type.TEL 
					&& type != Type.EMAIL && type != Type.PASSWORD && type != Type.RANGE)
				getLogger().warn("input with type [{}] has no attribute 'placeholder'", type);
			else this.placeHolder = placeHolder;
		}

		public int getDispaly() {
			return dispaly;
		}

		public void setDispaly(int dispaly) {
			this.dispaly = dispaly;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("{");
			sb.append("\"name\":\"").append(name).append("\",");
			sb.append("\"type\":\"").append(type).append("\",");
			if(value != null)
				sb.append("\"value\":\"").append(value).append("\",");
			sb.setLength(sb.length() - 1);
			return sb.append("}").toString();
		}
		
		public String toHTML() {
			StringBuilder sb = new StringBuilder("    <input name=\"");
			sb.append(name).append("\" type=\"").append(type).append("\" ");
			if(value != null)
				sb.append("value=\"").append(value).append("\" ");
			if(maxLength != null)
				sb.append("maxlength=").append(maxLength).append(" ");
			if(minLength != null)
				sb.append("minlength=").append(minLength).append(" ");
			if(size != null)
				sb.append("size=").append(size).append(" ");
			if(required)
				sb.append("required ");
			if(pattern != null)
				sb.append("pattern=\"").append(pattern).append("\" ");
			if(min != null)
				sb.append("min=\"").append(min).append("\" ");
			if(max != null)
				sb.append("max=\"").append(max).append("\" ");
			if(step != null)
				sb.append("step=\"").append(step).append("\" ");
			if(placeHolder != null)
				sb.append("placeholder=\"").append(placeHolder).append("\" ");
			return sb.append(">").toString();
		}

		public static enum Type {
			TEXT,
			PASSWORD,
			CHECKBOX,
			RADIO,
			SUBMIT,
			IMAGE,
			RESET,
			BUTTON,
			HIDDEN,
			FILE,
			
			//HTML5 added:
			EMAIL,
			URL,
			NUMBER,
			RANGE,
			DATE,
			MONTH,
			WEEK,
			TIME,
			DATETIME,
			SEARCH,
			COLOR,
			TEL;
			
			@Override
			public String toString() {
				return super.toString().toLowerCase();
			}
		}
	}
	
	public static class Option {
		
		private String text;
		
		private String value;
		
		private Boolean selected;
		
		public Option(String text, String value) {
			this.text = text;
			this.value = value;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Boolean getSelected() {
			return selected;
		}

		public void setSelected(Boolean selected) {
			this.selected = selected;
		}
		
		@Override
		public String toString() {
			return String.format("{\"text\":%s,\"value\":%s}", text, value);
		}
		
		public String toHTML() {
			StringBuilder sb = new StringBuilder("        <option value=\"");
			sb.append(value).append("\">");
			sb.append(text).append("</option>");
			return sb.toString();
		}
	}
	
	public static class Select {
		
		private String name;
		
		private Integer size;
		
		private boolean multiple = false;
		
		private List<Option> options;
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getSize() {
			return size;
		}

		public void setSize(Integer size) {
			this.size = size;
		}

		public boolean isMultiple() {
			return multiple;
		}

		public void setMultiple(boolean multiple) {
			this.multiple = multiple;
		}

		public List<Option> getOptions() {
			return options;
		}

		public void setOptions(List<Option> options) {
			this.options = options;
		}
		
		public void addOption(Option option) {
			if(options == null)
				options = new ArrayList<Option>();
			options.add(option);
		}
		
		@Override
		public String toString() {
			return String.format("{\"name\":%s,\"options\":%s}", name, options);
		}
		
		public String toHTML() {
			StringBuilder sb = new StringBuilder("    <select name=\"");
			sb.append(name).append("\">");
			if(options != null) {
				for(Option option : options)
					sb.append(option.toHTML());
			}
			return sb.append("</select>").toString();
		}

	}
	
	/**
	 * usage:<pre>
	 * &lt;input name="name" id="id" list="datalist_id" /&gt;
	 * &lt;datalist id="datalist_id"&gt;
	 *     &lt;option value="${value}"&gt;
	 * &lt;/datalist&gt;</pre>
	 * 
	 * @author iMinusMinus
	 * @since 2017-02-24
	 * @version 0.0.1
	 *
	 */
	public static class DataList {
		
		private String id;
		
		private List<Option> options;
		
		public DataList(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public List<Option> getOptions() {
			return options;
		}

		public void setOptions(List<Option> options) {
			this.options = options;
		}
		
		public void addOption(Option option) {
			if(options == null)
				options = new ArrayList<Option>();
			options.add(option);
		}
		
		@Override
		public String toString() {
			return String.format("{\"id\":%s,\"options\":%s}", id, options);
		}
		
		public String toHTML() {
			StringBuilder sb = new StringBuilder("    <datalist id=\"");
			sb.append(id).append("\">");
			if(options != null) {
				for(Option option : options)
					sb.append(option.toHTML());
			}
			return sb.append("</datalist>").toString();
		}
		
	}
	
	public static class TextArea {
		
		private String name;
		
		/*ignored attributes:
		private boolean disabled;
		
		private boolean readOnly;
		*/
		
		private String value;
		
		private String defaultValue;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}
		
		@Override
		public String toString() {
			return String.format("{\"name\":\"%s\"}", name);
		}
		
		public String toHTML() {
			StringBuilder sb = new StringBuilder("    <textarea name=\"");
			sb.append(name).append("\" ");
			if(value != null)
				sb.append("value=\"").append(value).append("\" ");
			sb.append(">");
			if(defaultValue != null)
				sb.append(defaultValue);
			return sb.append("</textarea>").toString();
				
		}
		
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getAction() {
		return action;
	}


	public void setAction(String action) {
		this.action = action;
	}


	public List<Input> getInputs() {
		return inputs;
	}


	public void setInputs(List<Input> inputs) {
		this.inputs = inputs;
	}
	
	public void addInput(Input input) {
		if(inputs == null)
			inputs = new ArrayList<Input>();
		inputs.add(input);
	}


	public List<Select> getSelects() {
		return selects;
	}


	public void setSelects(List<Select> selects) {
		this.selects = selects;
	}
	
	public void addSelect(Select select) {
		if(selects == null)
			selects = new ArrayList<Select>();
		selects.add(select);
	}


	public List<DataList> getDatalist() {
		return datalist;
	}


	public void setDatalist(List<DataList> datalist) {
		this.datalist = datalist;
	}
	
	public void addDatalist(DataList dataList) {
		if(datalist == null)
			datalist = new ArrayList<DataList>();
		datalist.add(dataList);
	}


	public List<TextArea> getTextAreas() {
		return textAreas;
	}


	public void setTextAreas(List<TextArea> textAreas) {
		this.textAreas = textAreas;
	}
	
	public void addTextAreas(TextArea textArea) {
		if(textAreas == null)
			textAreas = new ArrayList<TextArea>();
		textAreas.add(textArea);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{").append("\"name\":\"").append(name).append("\",");
		sb.append("\"action\":\"").append(action).append("\",");
		if(inputs != null && !inputs.isEmpty()) {
			sb.append("\"inputs\":[");
			for(Input input : inputs)
				sb.append(input).append(",");
			sb.setLength(sb.length() - 1);
			sb.append("]");
		}
		if(selects != null && !selects.isEmpty()) {
			sb.append("\"selects\":[");
			for(Select select : selects)
				sb.append(select).append(",");
			sb.setLength(sb.length() - 1);
			sb.append("]").append(",");
		}
		if(datalist != null && !datalist.isEmpty()) {
			sb.append("\"datalist\":[");
			for(DataList dataList : datalist)
				sb.append(dataList).append(",");
			sb.setLength(sb.length() - 1);
			sb.append("]").append(",");
		}
		if(textAreas != null && !textAreas.isEmpty()) {
			sb.append("\"textAreas\":[");
			for(TextArea textArea : textAreas)
				sb.append(textArea).append(",");
			sb.setLength(sb.length() - 1);
			sb.append("]").append(",");
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}
	
	/**
	 * output data model to html form
	 * 
	 * @return form
	 */
	public String toHTML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<form ");
		if(name != null)
			sb.append("name=\"").append(name).append("\" ");
		if(action != null)
			sb.append("action=\"").append(action).append("\">");
		if(inputs != null && !inputs.isEmpty()) {
			for(Input input : inputs)
				sb.append(input.toHTML());
		}
		if(selects != null && !selects.isEmpty()) {
			for(Select select : selects)
				sb.append(select.toHTML());
		}
		if(datalist != null && !datalist.isEmpty()) {
			for(DataList dataList : datalist)
				sb.append(dataList.toHTML());
		}
		if(textAreas != null && !textAreas.isEmpty()) {
			for(TextArea textArea : textAreas)
				sb.append(textArea.toHTML());
		}
		sb.append("</form>");
		return sb.toString();
	}


}
