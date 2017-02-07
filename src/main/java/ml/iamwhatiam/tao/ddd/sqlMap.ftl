<?xml version="1.0" encoding="UTF-8"?>
<#-- condition definition -->
<#if version == "ibatis">
<#assign ROOT = "sqlMap">
<#assign START_TAG = "#">
<#assign END_TAG = "#">
<#assign IS_NOT_NULL_START_TAG = "<isNotNull property='">
<#assign IS_NOT_NULL_END_TAG = "' />">

<!DOCTYPE sqlMap PUBLIC "-//ibatis.apache.org//DTD SQL Map 2.0//EN" "http://ibatis.apache.org/dtd/sql-map-2.dtd">
<#else>
<#assign ROOT = "mapper">
<#assign START_TAG = "#{">
<#assign END_TAG = "}">
<#assign IS_NOT_NULL_START_TAG = "<if test='">
<#assign IS_NOT_NULL_END_TAG = "!=null' />">

<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
</#if>
<#if version == "ibatis">
<sqlMap namespace="ml.iamwhatiam.tao.ddd.mapper.${module}">
	<resultMap class="ml.iamwhatiam.tao.ddd.domain.${module?upper_first}" id="${module}ResultMap">
	<#list ${table.columns} as column>
		<result column="${column.name}" property="${}" javaType="${}" /><!-- jdbcType=${} -->
	</#list>
	</resultMap>
	
	
	<sql id="columns">
		<#list ${table.columns} as column >
		column.name<#if column?has_next>,</#if>
		</#list>
	</sql>
	
	<sql id="condition">
	<#list ${bean.properties} as property>
	
	</#list>
	</sql>
	
	
	<insert id="insert" parameterClass="ml.iamwhatiam.tao.ddd.domain.${module?upper_first}">
	INSERT INTO ${table.name} (<include refid="${module}.columns" />) VALUES ()
	</insert>
	<insert id="insertSelective" parameterClass="ml.iamwhatiam.tao.ddd.domain.${module?upper_first}">
	INSERT INTO ${table.name} 
		<dynamic>
	(
			<isNotNull prepend="," property="">
		${column.name}	
			</isNotNull>
	)
		</dynamic>
	VALUES 
		<dynamic>
	(
			<isNotNull prepend="," property="">
		${bean.property}	
			</isNotNull>
	)
		</dynamic>
	</insert>
	
	<select id="findById" parameterClass="long" resultMap="${module}ResultMap">
	SELECT <include refid="${module}.columns" /> FROM ${table.name} T WHERE T.ID = #{id}
	</select>
	<select id="findSelective" parameterClass="long" resultMap="${module}ResultMap">
	SELECT <include refid="${module}.columns" /> 
	  FROM ${table.name} T 
	 WHERE T.ID = #{id}
	</select>
	<select id="count" parameterMap="${module}ResultMap" resultMap="long">
	SELECT COUNT(1) 
	  FROM ${table.name} T 
	<dynamic prepend="WHERE">
	
	</dynamic>
	</select>
	
	<update id="updateById" parameterClass="ml.iamwhatiam.tao.ddd.domain.${module?upper_first}">
	UPDATE ${table.name}
		<dynamic prepend='SET'>
			<isNotNull prepend="," property="${}">
			
			</isNotNull>
		</dynamic>
	 WHERE ID = #{}	
	</update>
	<update id="updateSelective" parameterClass="ml.iamwhatiam.tao.ddd.domain.${module?upper_first}">
	UPDATE ${table.name}
		<dynamic prepend='SET'>
			<isNotNull prepend="," property="${}">
			
			</isNotNull>
		</dynamic>
	</update>
	
	<delete id="deleteById" parameterClass="long">
	DELETE FROM ${table.name} WHERE ID = #{id}
	</delete>
<#else>
</#if>