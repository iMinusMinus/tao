<?xml version="1.0" encoding="UTF-8"?>
<#-- condition definition -->
<#if config?contains("iBatis")>
<#assign ROOT = "sqlMap">
<#assign START_TAG = "#">
<#assign END_TAG = "#">
<#assign IS_NOT_NULL_START = "<isNotNull property='">
<#assign IS_NOT_NULL_END = "' />">
<#assign IS_NOT_NULL_END_TAG = "</isNotNull>">
<#assign SET_START_TAG = "<dynamic prepend='SET'>" />
<#assign SET_END_TAG = "</dynamic>" />
<#assign WHERE_START_TAG = "<dynamic prepend='WHERE'>" />
<#assign WHERE_END_TAG = "</dynamic>" />
<#t>
<!DOCTYPE sqlMap PUBLIC "-//ibatis.apache.org//DTD SQL Map 2.0//EN" "http://ibatis.apache.org/dtd/sql-map-2.dtd">
<#else>
<#assign ROOT = "mapper">
<#assign START_TAG = "#{">
<#assign END_TAG = "}">
<#assign IS_NOT_NULL_START = "<if test='">
<#assign IS_NOT_NULL_END = "!=null' />">
<#assign IS_NOT_NULL_END_TAG = "</if>">
<#assign SET_START_TAG = "<trim prefix='SET' suffixOverrides=','>" /><#-- or use set tag -->
<#assign SET_END_TAG = "</trim>" />
<#assign WHERE_START_TAG = "<trim prefix='WHERE' prefixOverrides='AND'> /><#-- or use where tag -->
<#assign WHERE_END_TAG = "</trim>" />
<#-- foreach, trim, bind, choose when otherwise -->
<#t>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
</#if>
<${ROOT} namespace="ml.iamwhatiam.tao.ddd.${namespace}.mapper.${bean.name}">

    <typeAlias alias="${bean.name}DO" class="ml.iamwhatiam.tao.ddd.${namespace}.domain.${bean.name?upper_first}Domain" />

	<resultMap class="${bean.name}DO" id="${bean.name}ResultMap">
	<#list ${table.columns} as column>
		<result column="${column.name}" property="<@snake2camel snakeCase=column.name />" javaType="${}" /><!-- jdbcType=${column.dataType} -->
	</#list>
	</resultMap>
	
	
	<sql id="columns">
		<#list ${table.columns} as column >
		column.name<#seq>,
		</#list>
	</sql>
	
	<sql id="condition">
	<#list ${table.columns} as column>
	${IS_NOT_NULL_START}<@snake2camel snakeCase=column.name />${IS_NOT_NULL_END}
	    AND T.${column.name} = ${START_TAG}<@snake2camel snakeCase=column.name />${END_TAG}
	${IS_NOT_NULL_END_TAG}
	</#list>
	</sql>
	
	
	<insert id="insert" parameterClass="${bean.name}DO">
	INSERT INTO ${table.name} 
	(
	<include refid="columns" />
	) 
	VALUES 
	(
	<#list table.columns as column>${START_TAG}<@snake2camel snakeCase=column.name />${END_TAG}<#sep>,</#list>
	)
	</insert>
	<insert id="insertSelective" parameterClass="${bean.name}DO">
	INSERT INTO ${table.name} 
	(
		<#list table.columns as column>
		<isNotNull prepend="," property="<@snake2camel snakeCase=column.name />">
	${column.name}	
		</isNotNull>
		</#list>	
	)
	VALUES 
	(
		<#list table.columns as column>
		<isNotNull prepend="," property="<@snake2camel snakeCase=column.name />">
	${START_TAG}<@snake2camel snakeCase=column.name />${END_TAG}
		</isNotNull>
		</#list>	
	)
	</insert>
	
	<select id="findById" parameterClass="long" resultMap="${bean.name}ResultMap">
	SELECT <include refid="columns" /> FROM ${table.name} T WHERE T.ID = ${START_TAG}id${END_TAG}
	</select>
	<select id="findSelective" parameterClass="${bean.name}DO" resultMap="${bean.name}ResultMap">
	SELECT <include refid="columns" /> 
	  FROM ${table.name} T 
		${WHERE_START_TAG}
		<include refid="condition" />		
		${WHERE_END_TAG}
	</select>
	<select id="count" parameterMap="${bean.name}ResultMap" resultMap="long">
	SELECT COUNT(1) 
	  FROM ${table.name} T 
	${WHERE_START_TAG}
	<include refid="condition" />
	${WHERE_END_TAG}
	</select>
	
	<update id="updateById" parameterClass="${bean.name}DO" resultMap="${bean.name}ResultMap">
	UPDATE ${table.name}
		${SET_START_TAG}
			<#list ${table.columns} as column>
			${IS_NOT_NULL_START}<@snake2camel snakeCase=column.name />"${IS_NOT_NULL_END}
			    T.${column.name} = ${START_TAG}<@snake2camel snakeCase=column.name />${END_TAG},
			${IS_NOT_NULL_END_TAG}
			</#list>
		${SET_START_TAG}
	 WHERE ID = ${START_TAG}id${END_TAG}
	</update>
	<#--
	<update id="updateSelective" parameterClass="${bean.name}DO" resultMap="${bean.name}ResultMap">
	UPDATE ${table.name}
		${SET_START_TAG}
			<#list ${table.columns} as column>
			${IS_NOT_NULL_START}<@snake2camel snakeCase=column.name />"${IS_NOT_NULL_END}
			    T.${column.name} = ${START_TAG}<@snake2camel snakeCase=column.name />${END_TAG},
			${IS_NOT_NULL_END_TAG}
			</#list>
		${SET_START_TAG}
	 WHERE	
	 	${WHERE_START_TAG}
			<include refid="condition" />
		${WHERE_START_TAG}
	</update>
	-->
	
	<delete id="deleteById" parameterClass="long">
	DELETE FROM ${table.name} WHERE ID = ${START_TAG}id${END_TAG}
	</delete>
</${ROOT}>