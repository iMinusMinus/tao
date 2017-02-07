<#switch table.dialect>
<#case "MySQL">
<#-- from HeidiSQL-->
CREATE TABLE `${table.name}` (
    <#list table.columns as column>
    `${column.name}` ${column.dataType?toSQL} <#if !column.nullable>NOT </#if>NULL<#if column.defaultValue??> DEFAULT ${column.defaultValue}</#if><#if column.comment??> COMMENT ${column.comment}<#/if><#if column?has_next || table.pk?? || table.fks?? || table.uks?? || table.indexes??>,</#if>
    <#-- ${column?toSQL} -->
    </#list>
    <#if table.indexes?? && tanle.indexes?size gt 0>
    <#list table.indexes as index>
    INDEX <#if index.name??>${index.name} </#if>(<#list index.columns as column>`${column.name}`<#if column?has_next>,</#if></#list>)<$if index.alogrithm??>USING ${index.alogrithm}</#if><#if index?has_next || table.pk?? || table.uks?? || table.fks??>,</#if>
    </#list>
    </#if>
    <#if table.pk??>
    PRIMARY KEY (<#list table.pk.columns as column>`${column.name}`<#if column?has_next>,<#if>)<#if table.uks?? || table.fks??>,</#if>
    </#if>
    <#if table.uks?? && table.uks?size gt 0>
    <#list table.uks as uk>
    UNIQUE INDEX <#if uk.name??>${uk.name} </#if>(<#list uk.columns as column>`${column.name}`<#if column?has_next>,</#if></#list>)<#if uk?has_next || table.fks??>,</#if>
    </#list>
    </#if>
    <#if table.fks?? && table.fks?size gt 0>
    <#list table.fks as fk>
    CONSTRAINT <#if fk.name??>${fk.name} </#if>FOREIGN KEY (<#list fk.columns as column>`${column.name}`<#if column?has_next>,</#if></#list>) REFERENCES `${fk.references[0].table.name}` (<#list fk.references as refCol>`${refCol.name}`<#if refCol?has_next>,</#if></#list>)<#if fk?has_next>,</#if>
    </#list>
    </#if>
)
<#if table.comment??>COMMENT='${table.comment}'</#if>
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;
</#if>
<#break>
<#case "Oracle">
<#-- from PL/SQL Developer-->
-- Create table
create table ${table.name}
(
  <#list table.columns as column>
  ${column.name} ${column.dataType?toSQL} <#if !column.nullable>not null </#if><#if column.defaultValue??>default ${column.defaultValue}</#if><#if column?has_next>,</#if>
  </#list>
)
tablespace APPS
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
-- Add comments to the table  
<#if table.comment??>comment on table <#if table.schema??>${table.schema}.</#if>${table.name} 
  is '${table.comment}';</#if>
-- Add comments to the columns
<#list table.columns as column>
<#if column.comment??>comment on column ${table.name}.${column.name}
  is ''${column.comment};
</#list>
-- Create/Recreate indexes
<#if table.indexes?? && table.indexes?size gt 0>
<#list table.indexes as index>
create <#if index.alorithm??>${index.alorithm}</#if> index ${index.name} on ${table.name} (<#list index.columns as column>${column.name}<#if column?has_next>,</#if></#list>)<#if index.reverse> reverse</#if>;
</#list>
<#/if>
-- Create/Recreate primary, unique and foreign key constraint
<#if table.pk??>
alter table ${table.name}
  add constraint ${table.pk.name} primary key (<#list table.pk.columns as column>${column.name}<#if column?has_next>,</#if></#list>);
</#if>
<#if table.uks?? && table.uks?size gt 0>
<#list table.uks as uk>
alter table ${table.name}
  add constraint ${uk.name} unique (<#list uk.columns as column>${column.name}<#if column?has_next>,</#if></#list>);
</#list>
</#if>
<#if table.uks?? && table.uks?size gt 0>
<#list table.fks as fk>
alter table ${table.name}
  add constraint ${fk.name} foreign key (<#list fk.columns as column>${column.name}<#if column?has_next>,</#if></#list>)
  references ${fk.references[0].table.name} (<#list fk.references as column>${column.name}<#if column?has_next>,</#if></#list>);
</#list>
</#if>
-- Create/Recreate check constraints
<#if table.checks?? && table.checks?size gt 0>
<#list table.checks as ck>
alter table ${table.name}
  add constraint ${ck.name}
  check (${ck.searchCondition});
</#list>
</#if>
<#-- Create synonym: table, view, index, package, procedure  
CREATE PUBLIC SYNONYM ${table.synonym} FOR <#if table.schema??>${table.schema}.</#if>${table.name};
-->
-- Grant/Revoke object privileges
grant select on <#if table.schema??>${table.schema}.</#if>${table.name} to R_DEV;
grant insert,delete,update,select on <#if table.schema??>${table.schema}.</#if>${table.name} to R_OPR;
<#break>
<#case "Postgres">
<#-- from PgAdmin-->
-- Table: public.${table.name}

-- DROP TABLE public.${table.name}

CREATE TABLE public.${table.name}
(
    <#list table.columns as column>
    ${column.name} ${column.dataType?toSQL} <#if !column.nullable>NOT NULL </#if><#if column.defaultValue??>DEFAULT ${column.defaultValue}</#if><#if column?has_next || table.pk?? || table.fks?? || table.uks?? || table.checks??>,</#if>
    </#list>
    <#if table.pk??>CONSTRAINT ${table.pk.name} PRIMARY KEY (<#list table.pk.columns as column>${column.name}<#if column?has_next>,</#if></#list>)<#if table.fks?? || table.uks?? || table.checks??>,</#if></#if>
    <#if table.fks?? && table.fks?size gt 0>
    <#list table.fks as fk>
    CONSTRAINT ${fk.name} FOREIGN KEY (<#list fk.columns as column>${column.name}<#if column?has_next>,</#if></#list>) REFERENCES public.${fk.references[0].table.name} (<#list fk.references as column>${column.name}<#if column?has_next>,</#if></#list>)<#if fk?has_next || table.uks?? || table.checks??>,</#if>
    </#list>
    </#if>
    <#if table.uks?? && table.uks?size gt 0>
    <#list table.uks as uk>
    CONSTRAINT ${uk.name} UNIQUE (<#list uk.columns as column>${column.name}<#if column?has_next>,</#if></#list>)<#if uk?has_next || table.checks??>,</#if>
    </#list>
    </#if>
    <#if table.checks?? && table.checks?size gt 0>
    <#list table.checks as ck>
    CONSTRAINT ${ck.name} CHECK (${ck.searchCondition})<#if ck?has_next>,</#if>
    </#list>
    </#if>
)
WITH (
OIDS = FALSE
)
TABLESPACE pg_default;

<#list table.columns as column>
<#if column.comment??>
COMMENT ON COLUMN public.${table.name}.${column.name} IS '${column.comment}';

</#if>
</#list>
<#if table.comment??>
COMMENT ON TABLE public.${table.name}
  IS '${table.comment}';
</#if>
<#if table.indexes?? && table.indexes?size gt 0>
<#list table.indexes as index>
-- Index: ${index.name}
-- DROP INDEX public.${index.name}

CREATE INDEX ${index.name}
    ON public.${table.name} <#if index.algorithm??>USING ${index.algorithm}</#if>
    (<#list index.columns as column>${column.name}<#if column?has_next>,</#if></#list>)
    TABLESPACE pg_default;
</#list>
</#if>
<#break>
<#default>
${table?toSQL}
<#/switch>