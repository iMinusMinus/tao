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
package ml.iamwhatiam.tao.ddd.${namespace}.domain;
 
import ml.iamwhatiam.tao.domain.Taichi;
<#list ${bean.imports} as im>
${im};
</#list>
 
/**
 * <#if bean.comment??>${bean.comment}</#if>
 *
 * @author iMinusMinus
 * @version 0.0.1
 *
 */
public class ${bean.name}Domain extends Taichi {
 
    private static final long serialVersionUID = 1L;
 	
 	<#list ${bean.properties} as property>
 	<#if property.comment??>/**${property.comment}*/</#if>
 	<#if property.constraints?? && property.constraints?size gt 0>
 	<#list property.constraints as constraint>
 	@${constraint.type}<#if constraint.values??>(<#list constraint.values as key value><#if key??>${key} = </#if>${value}<#sep>, </#list>)</#if>
 	</#list>
 	</#if>
 	private ${property.type} ${property.name}<#if property.defaultValue??> = ${property.defaultValue}</#if>;
 	</#list>
 	
 	<#list ${bean.properties} as property>
 	public ${property.type} <#if ${property.type} == "boolean">is<#else>get</#if>${property.name?upper_first}() {
 		return ${property.name};
 	}
 	
 	public void set${property.name?upper_first}(${property.type} ${property.name}) {
 		this.${property.name} = ${property.name};
 	}
 	</#list>
 	
 	@Override
 	public String toString() {
 	<#-- return String.format(); -->
 	StringBuilder sb = new StringBuilder("{");
 	<#list ${bean.properties} as property>
 	sb.append("${property.name}:");
 	if(${property.type} instanceof CharSequence) {
 		sb.append("\"");
 		sb.append(${property.name});
 		sb.append("\"");
 	}
 	else sb.append(${property.name});
 	sb.append(",");
 	</#list>
 	sb.setLength(sb.length() - 1);
 	return sb.append("}").toString();
 	}
 
}