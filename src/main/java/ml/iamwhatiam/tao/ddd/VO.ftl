<#import "pub.ftl" as software>
<@software.license />

package ml.iamwhatiam.tao.ddd.${module}<#if !samePackage>.vo</#if>;
 
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
<#list ${bean.imports} as im>
${im};
</#list>

 
/**
 * <#if bean.comment??>${bean.comment}</#if>
 *
 * @author iMinusMinus
 * @version 0.0.1
 * @since ${date?string.iso}
 *
 */
@XmlRootElement(name = "${bean.name}")
@XmlAccessorType(XmlAccessType.FIELD)
public class ${bean.name?upper_first}VO implements Serializable {
 
 	private static final long serialVersionUID = 1L;
 	
 	<#list ${bean.properties} as property>
 	<#if property.comment??>
 	/**
 	 * ${property.comment}
 	 */
 	</#if>
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
 	sb.append("\"${property.name}\":");
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