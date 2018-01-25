<#import "pub.ftl" as tool>
<?xml version="1.0" encoding="UTF-8"?>
<#-- condition definition -->
<#if config?contains("iBatis")>
<#assign ROOT = "sqlMap">
<#assign TYPE = "class">
<#assign ID = "result">
<#assign ONE_TO_ONE = "association"><#-- @see http://www.mybatis.org/mybatis-3/zh/sqlmap-xml.html#Auto-mapping -->
<#assign ONE_TO_MANY = "collection">
<#assign PARAMETER_TYPE = "parameterClass">
<#assign START_TAG = "#">
<#assign END_TAG = "#">
<#assign IS_NOT_NULL_START = "<isNotNull property='">
<#assign IS_NOT_NULL_END = "' >">
<#assign IS_NOT_NULL_END_TAG = "</isNotNull>">
<#assign IS_NOT_EQUAL_START = "<isNotEqual property='">
<#assign IS_NOT_EQUAL_STOP = "' compareValue=">
<#assign IS_NOT_EQUAL_END = " >">
<#assign IS_NOT_EQUAL_END_TAG = "</isNotEqual>">
<#assign SET_START_TAG = "<dynamic prepend='SET'>">
<#assign SET_END_TAG = "</dynamic>">
<#assign WHERE_START_TAG = "<dynamic prepend='WHERE'>">
<#assign WHERE_END_TAG = "</dynamic>">
<#t>
<!DOCTYPE sqlMap PUBLIC "-//ibatis.apache.org//DTD SQL Map 2.0//EN" "http://ibatis.apache.org/dtd/sql-map-2.dtd">
<#else>
<#assign ROOT = "mapper">
<#assign TYPE = "type">
<#assign ID = "id">
<#assign ONE_TO_ONE = "result">
<#assign ONE_TO_MANY = "result">
<#assign PARAMETER_TYPE = "parameterType"><#-- parameterMap is deprecated -->
<#assign START_TAG = "#{">
<#assign END_TAG = "}">
<#assign IS_NOT_NULL_START = "<if test= '">
<#assign IS_NOT_NULL_END = " != null'>">
<#assign IS_NOT_NULL_END_TAG = "</if>">
<#assign IS_NOT_EQUAL_START = "<if test= '">
<#assign IS_NOT_EQUAL_STOP = " != ">
<#assign IS_NOT_EQUAL_END = "' >">
<#assign IS_NOT_EQUAL_END_TAG = "</if>">
<#assign SET_START_TAG = "<trim prefix='SET' suffixOverrides=','>" /><#-- or use set tag -->
<#assign SET_END_TAG = "</trim>">
<#assign WHERE_START_TAG = "<trim prefix='WHERE' prefixOverrides='AND'>"><#-- or use where tag -->
<#assign WHERE_END_TAG = "</trim>">
<#-- foreach, trim, bind, choose when otherwise -->
<#t>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
</#if>
<${ROOT} namespace="ml.iamwhatiam.tao.ddd.${namespace}<#if !samePackage>.dao</#if>.${bean.name?cap_first}DAO">

	<#if config?contains("iBatis")>
    <typeAlias alias="${bean.name}DO" type="ml.iamwhatiam.tao.ddd.${namespace}<#if !samePackage>.domain</#if>.${bean.name?cap_first}Domain" />
	<#else>
	<#-- parameterMap is deprecated -->
	<parameterMap id="${bean.name}DO" type="ml.iamwhatiam.tao.ddd.${namespace}<#if !samePackage>.domain</#if>.${bean.name?cap_first}Domain" />
	</#if>
	
	<resultMap ${TYPE}="${bean.name}DO" id="BaseResultMap">
	<#list table.columns as column>
		<#list bean.properties as property>
		<#if property.name?upper_case == column.name?replace('_', '')?upper_case>
		<#if property.name == "oid">
		<${ID} property="oid" column="id" javaType="long" />
		<#elseif property.type == "java.lang.List" || property.type == "java.lang.Colletion">
		<${ONE_TO_MANY} property="${property.name}" javaType="java.util.ArrayList" column="${column.name}" select="${namespace}.findSelective" />
		<#elseif table.fks?? && table.fks?size() > 0>
		<${ONE_TO_ONE} property="${property.name}" resultMap="${namespace}.BaseResultMap" columnPrefix="${column.table.fks[0].name}" />
		<#else>
		<result column="${column.name}" property="${property.name}" javaType="${property.type}" /><!-- jdbcType=${column.dataType.dataType} -->
		</#if>
		</#if>
		</#list>
	</#list>
	</resultMap>
	
	
	<sql id="columns">
		<#list table.columns as column >
		${column.name}<#sep>,
		</#list>
	<#nt>
	</sql>
	
	<sql id="condition">
	<#list table.columns as column>
	<#if column.defaultValue?? && column.defaultValue?length gt 0>
	${IS_NOT_EQUAL_START}<@tool.snake2camel snakeCase="${column.name}" />${IS_NOT_EQUAL_STOP}${column.defaultValue}${IS_NOT_EQUAL_END}
		AND T.${column.name} = ${START_TAG}<@tool.snake2camel snakeCase="${column.name}" />${END_TAG}
	${IS_NOT_EQUAL_END_TAG}	
	<#else>
	${IS_NOT_NULL_START}<@tool.snake2camel snakeCase="${column.name}" />${IS_NOT_NULL_END}
	    AND T.${column.name} = ${START_TAG}<@tool.snake2camel snakeCase="${column.name}" />${END_TAG}
	${IS_NOT_NULL_END_TAG}
	</#if>
	</#list>
	</sql>


	<insert id="insert" ${PARAMETER_TYPE}="${bean.name}DO">
	INSERT INTO ${table.name} 
	(
	<include refid="columns" />
	) 
	VALUES 
	(
	<#list table.columns as column>
	${START_TAG}<@tool.snake2camel snakeCase=column.name />${END_TAG}<#sep>,
	</#list>
	<#nt>
	)
	</insert>
	<insert id="insertSelective" ${PARAMETER_TYPE}="${bean.name}DO">
	INSERT INTO ${table.name} 
	(
		<#list table.columns as column>
	<#if column.defaultValue?? && column.defaultValue?length gt 0>
	${IS_NOT_EQUAL_START}<@tool.snake2camel snakeCase="${column.name}" />${IS_NOT_EQUAL_STOP}${column.defaultValue}${IS_NOT_EQUAL_END}
		${START_TAG}<@tool.snake2camel snakeCase="${column.name}" />${END_TAG}<#sep>,</#sep>
	${IS_NOT_EQUAL_END_TAG}	
	<#else>
	${IS_NOT_NULL_START}<@tool.snake2camel snakeCase="${column.name}" />${IS_NOT_NULL_END}
	    ${START_TAG}<@tool.snake2camel snakeCase="${column.name}" />${END_TAG}<#sep>,</#sep>
	${IS_NOT_NULL_END_TAG}
	</#if>
		</#list>	
	)
	VALUES 
	(
		<#list table.columns as column>
			<#if column.defaultValue?? && column.defaultValue?length gt 0>
	${IS_NOT_EQUAL_START}<@tool.snake2camel snakeCase="${column.name}" />${IS_NOT_EQUAL_STOP}${column.defaultValue}${IS_NOT_EQUAL_END}
		${START_TAG}<@tool.snake2camel snakeCase="${column.name}" />${END_TAG}<#sep>,</#sep>
	${IS_NOT_EQUAL_END_TAG}	
			<#else>
	${IS_NOT_NULL_START}<@tool.snake2camel snakeCase="${column.name}" />${IS_NOT_NULL_END}
	   ${START_TAG}<@tool.snake2camel snakeCase="${column.name}" />${END_TAG}<#sep>,</#sep>
	${IS_NOT_NULL_END_TAG}
			</#if>
		</#list>	
	<#nt>)
	</insert>
	
	<select id="findById" ${PARAMETER_TYPE}="long" resultMap="BaseResultMap">
	SELECT <include refid="columns" /> FROM ${table.name} T WHERE T.ID = ${START_TAG}id${END_TAG}
	</select>
	<select id="findSelective" ${PARAMETER_TYPE}="${bean.name}DO" resultMap="${bean.name}ResultMap">
	SELECT <include refid="columns" /> 
	  FROM ${table.name} T 
		${WHERE_START_TAG}
		<include refid="condition" />		
		${WHERE_END_TAG}
	</select>
	<select id="count" ${PARAMETER_TYPE}="${bean.name}DO" resultMap="long">
	SELECT COUNT(1) 
	  FROM ${table.name} T 
	${WHERE_START_TAG}
	<include refid="condition" />
	${WHERE_END_TAG}
	</select>
	
	<update id="updateById" ${PARAMETER_TYPE}="${bean.name}DO">
	UPDATE ${table.name}
		${SET_START_TAG}
			<#list table.columns as column>
			${IS_NOT_NULL_START}<@tool.snake2camel snakeCase=column.name />${IS_NOT_NULL_END}
		   T.${column.name} = ${START_TAG}<@tool.snake2camel snakeCase=column.name />${END_TAG},
			${IS_NOT_NULL_END_TAG}
			</#list>
		${SET_END_TAG}
	 WHERE ID = ${START_TAG}id${END_TAG}
	</update>
	<#---->
	<update id="updateSelective" ${PARAMETER_TYPE}="${bean.name}DO">
	UPDATE ${table.name}
		${SET_START_TAG}
			<#list table.columns as column>
			${IS_NOT_NULL_START}<@tool.snake2camel snakeCase=column.name />${IS_NOT_NULL_END}
		   T.${column.name} = ${START_TAG}<@tool.snake2camel snakeCase=column.name />${END_TAG},
			${IS_NOT_NULL_END_TAG}
			</#list>
		${SET_END_TAG}
	 WHERE	
	 	${WHERE_START_TAG}
			<include refid="condition" />
		${WHERE_END_TAG}
	</update>
	
	
	<delete id="deleteById" ${PARAMETER_TYPE}="long">
	DELETE FROM ${table.name} WHERE ID = ${START_TAG}id${END_TAG}
	</delete>
</${ROOT}>