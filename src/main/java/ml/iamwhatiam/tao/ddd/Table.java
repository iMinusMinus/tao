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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DBMS table abstract:
 * <ol>
 * <li>CREATE [GLOBAL | LOCAL TEMPORARY] TABLE <code>name</code>
 * (
 * &lt;table element&gt;[, &lt;table element&gt;...]
 * )
 * [WITH SYSTEM VERSIONING] [ON COMMIT PRESERVE | DELETE ROWS]</li>
 * <ul>table element := &lt;column definition&gt; | &lt;table period definition &gt; | &lt;table constraint definition&gt; | &lt;like clause &gt;
 * <li>column definition := <code>columnName</code> [<code>data type</code> | <code> domain type</code>]
 * [DEFAULT <code>defaultValue</code> | &lt;identity column specification&gt; | &lt;generation clause&gt; | GENERATED ALWAYS AS ROW START | GENERATED ALWAYS AS ROW END]
 * [[CONSTRAINT <code>constraintName</code>] NOT NULL | UNIQUE | PRIMARY KEY | 
 * REFERENCES <code>referTableName</code> (<code>referColumnName</code>[{, <code>referColumnName</code>}...]) 
 * [MATCH FULL | PARTIAL | SIMPLE] 
 * [ON DELETE CASCADE | SET NULL | SET DEFAULT | RESTRICT | NO ACTION] 
 * [ON UPDATE CASCADE | SET NULL | SET DEFAULT | RESTRICT | NO ACTION] |
 * CHECK (<code>searchCondition</code>)]
 * [COLLATE <code>schema.</code><code>collate</code>]</li>
 * <li>table period definition := PERIOD FOR SYSTEM_TIME | PERIOD FOR <code>appTime</code>(<code>beginColumnName</code>,<code>endColumnName</code>)
 * <li>table constraint definition := [CONSTRAINT <code>name</code>]  [INITIALLY DEFERRED | INITIALLY IMMEDIATE [[NOT] DEFERRABLE] [[NOT] ENFORCED]] | 
 * [NOT] DEFERRABLE [INITIALLY DEFERRED | INITIALLY IMMEDIATE [[NOT] DEFERRABLE] [[NOT] ENFORCED] |
 * [NOT] ENFORCED]
 * <li>like clause := LIKE <code>tableName</code> [INCLUDING IDENTITY | EXCLUDING IDENTITY | INCLUDING DEFAULTS | EXCLUDING DEFAULTS | INCLUDING GENERATED | EXCLUDING GENERATED]
 * </ul>
 * <li>CREATE [GLOBAL | LOCAL TEMPORARY] TABLE <code>name</code>
 * OF [[<code>catalog.</code>]<code>schema.</code>]<code>referTable</code> [UNDER [<code>schema</code>.]<code>tableName</code>] [(&lt;typed table element&gt;[, &lt;typed table element&gt;]...)]
 * [WITH SYSTEM VERSIONING] [ON COMMIT PRESERVE | DELETE ROWS]</li>
 * <li>CREATE [GLOBAL | LOCAL TEMPORARY] TABLE <code>name</code>
 * [(<code>columnName</code>[{, <code>columnName</code>}...])] AS  ([WITH [RECURSIVE] &lt;with list&gt;] &lt;query expression body&gt; [ORDER BY &lt;sort specification list&gt;] [&lt;result offset clause&gt;]  [&lt;fetch first clause&gt;]) 
 * WITH NO DATA | WITH DATA [WITH SYSTEM VERSIONING] [ON COMMIT PRESERVE | DELETE ROWS]</li>
 * </ol>
 * @author iMinusMinus
 * @since 2017-01-24
 * @version 0.0.1
 *
 */
public class Table {
	
	private static Logger log = LoggerFactory.getLogger(Table.class);
	
	private static Set<String> keywords;
	
	static {
		keywords = new HashSet<String>();
		keywords.add("SELECT");
		keywords.add("DISTINCT");
		keywords.add("FROM");
		keywords.add("WHERE");
		keywords.add("BETWEEN");
		keywords.add("AND");
		keywords.add("GROUP");
		keywords.add("BY");
		keywords.add("HAVING");
		keywords.add("ORDER");
		keywords.add("UNION");
		keywords.add("ALL");
		keywords.add("INSERT");
		keywords.add("INTO");
		keywords.add("VALUES");
		keywords.add("UPDATE");
		keywords.add("SET");
		keywords.add("DELETE");
		keywords.add("WITH");
		keywords.add("AS");
		keywords.add("NOT");
		keywords.add("NULL");
		keywords.add("IS");
		keywords.add("CASE");
		keywords.add("WHEN");
		//XXX not sure miss nor redundant
	}
	
	private final Dialect dialect;
	
	private static char quote;
	
	public Table (Dialect dialect) {
		this.dialect = dialect;
		switch(dialect) {
		case MYSQL:
			quote = '`';
			break;
		case ORACLE://pass through	
		case POSTGRES:
			quote = '"';
			break;
		default:	
		}
	}
	
	private String catalog;//Sybase/MS SQLServer: database name
	
	private String catalogIdentifier;
	
	private String schema;//Oracle: user, MySQL: database name
	
	private String schemaIdentifier;
	
	@NotNull
	private String name;//catalog.schema.tbl_name
	
	private String identifier;
	
	@Size(max = 1017/*, groups = MySQL*/)
	private List<Column> columns;
	
	private PrimaryKey pk;
	
	private List<UniqueKey> uks;
	
	private List<ForeignKey> fks;
	
	private List<Index> indexes;
	
	private List<Check> checks;
	
	private String comment;
	
	//ignore some table options
	
	//ignore partition options
	
	private static boolean isKeyword(String name) {
		return keywords.contains(name);
	}
	
	static String unquote(String origin) {
		char[] tmp = origin.toCharArray();
		if(tmp[0] == quote && tmp[tmp.length - 1] == quote)
			return new String(tmp, 1, tmp.length - 2);
		return origin;
	}
	
	public Dialect getDialect() {
		return dialect;
	}
	
	
	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalogIdentifier) {
		this.catalogIdentifier = catalogIdentifier;
		String catalog = unquote(catalogIdentifier);
		if(isKeyword(catalog))
			log.warn("table catalog [{}] should better not be sql keywrod", catalog);
		this.catalog = catalog;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schemaIdentifier) {
		this.schemaIdentifier = schemaIdentifier;
		String schema = unquote(schemaIdentifier);
		if(isKeyword(schema))
			log.warn("table schema [{}] should better not be sql keywrod", schema);
		this.schema = schema;
	}

	public String getName() {
		return name;
	}

	public void setName(String identifier) {
		this.identifier = identifier;
		String name = unquote(identifier);
		if(isKeyword(name))
			log.warn("table name [{}] should better not be sql keywrod", name);
		this.name = name;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public PrimaryKey getPk() {
		return pk;
	}

	public void setPk(PrimaryKey pk) {
		this.pk = pk;
	}
	
	public void addConstraint(Constraint constraint) {
		if(constraint instanceof PrimaryKey)
			setPk((PrimaryKey) constraint);
		else if(constraint instanceof UniqueKey) 
			addUniqueKey((UniqueKey) constraint);
		else if(constraint instanceof ForeignKey) 
			addForeignKey((ForeignKey) constraint);
		else if(constraint instanceof Index) 
			addIndex((Index) constraint);
		else if(constraint instanceof Check) 
			addCheck((Check) constraint);
	}

	public List<UniqueKey> getUks() {
		return uks;
	}

	public void setUks(List<UniqueKey> uks) {
		this.uks = uks;
	}
	
	public void addUniqueKey(UniqueKey uk) {
		if(uks == null)
			uks = new ArrayList<UniqueKey>();
		uks.add(uk);
	}

	public List<ForeignKey> getFks() {
		return fks;
	}

	public void setFks(List<ForeignKey> fks) {
		this.fks = fks;
	}
	
	public void addForeignKey(ForeignKey fk) {
		if(fks == null)
			fks = new ArrayList<ForeignKey>();
		fks.add(fk);
	}

	public List<Index> getIndexes() {
		return indexes;
	}

	public void setIndexes(List<Index> indexes) {
		this.indexes = indexes;
	}
	
	public void addIndex(Index index) {
		if(indexes == null)
			indexes = new ArrayList<Index>();
		indexes.add(index);
	}

	public List<Check> getChecks() {
		return checks;
	}

	public void setChecks(List<Check> checks) {
		this.checks = checks;
	}
	
	public void addCheck(Check ck) {
		if(checks == null)
			checks = new ArrayList<Check>();
		checks.add(ck);
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}


	/**
	 * Column abstract
	 *
	 */
	public static class Column {
		
		public Column(String identifier, DataType dataType) {
			this.identifier = identifier;
			String name = unquote(identifier);
			if(isKeyword(name))
				log.warn("column name [{}] should better not be sql keywrod", name);
			this.name = name;
			this.dataType = dataType;
		}
		
		private Table table;
		
		@NotNull
		private String name;
		
		private String identifier;
		
		private DataType dataType;
		
		private boolean nullable = true;//a constraint!
		
		private String defaultValue;
		
		private boolean autoIncrement;
		
		private String comment;
		
		//ignore some column information
		
//		private PrimaryKey pk;
//		
//		private Index index;
//		
//		private UniqueKey uk;
//		
//		private ForeignKey fk;
//		
//		private Check check;
		
		public Table getTable() {
			return table;
		}

		public void setTable(Table table) {
			this.table = table;
		}

		public String getName() {
			return name;
		}

		public void setName(String identifier) {
			this.identifier = identifier;
			String name = unquote(identifier);
			if(isKeyword(name))
				log.warn("table name [{}] should better not be sql keywrod", name);
			this.name = name;
		}

		public DataType getDataType() {
			return dataType;
		}

		public void setDataType(DataType dataType) {
			this.dataType = dataType;
		}

		public boolean isNullable() {
			return nullable;
		}

		public void setNullable(boolean nullable) {
			this.nullable = nullable;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(String defaultValue) {
			if(defaultValue == null || "NULL".equalsIgnoreCase(defaultValue)) 
				return;
			if("''".equals(defaultValue)) {
				this.defaultValue = "";
				return;
			}
			if(getAvailableType().get(dataType) == CharacterType.class)
				defaultValue = "\"" + defaultValue.substring(1, defaultValue.length() - 2) + "\"";
			this.defaultValue = defaultValue;
		}

		public boolean isAutoIncrement() {
			return autoIncrement;
		}

		public void setAutoIncrement(boolean autoIncrement) {
			this.autoIncrement = autoIncrement;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}
		
		private Map<String, Class<? extends DataType>> getAvailableType() {
			Map<String, Class<? extends DataType>> available = null;
			switch(table.getDialect()) {
			case MYSQL: available = MySQLDataType.types; break;
			case ORACLE: available = OracleDataType.types; break;
			case POSTGRES: available = PostgresDataType.types; break;
			default: available = StandardDataType.types;
			}
			return available;
		}

		/**
		 * @see http://www.inf.fu-berlin.de/lehre/SS94/einfdb/SQL3/sql-foundation-mar94.txt
		 * @see http://www.iso.org/iso/home/store/catalogue_tc/catalogue_detail.htm?csnumber=63556
		 * 
	     * primitive data types, or named built-in data types or predefined types
		 */
		public interface DataType {
			
			String toSQL();
			
		}
		
		public static interface CharacterType extends DataType {
			
			Integer get();
			
			void set(int length);
			
		}
		
		public static interface NumericType extends DataType {
			
			Integer getPrecision();
			
			Integer getScale();
			
			void set(int precision);
			
			void set(int precision, int scale);
			
		}	
		public static interface EnumeratedType extends DataType {	
			
			String[] names();
			
			void set(String...names);
			
		}	
		
		public static interface DateTimeType extends DataType {	
			
			Integer get();
			
			void set(int precision);
			
			Boolean withTimeZone();
			
			void set(boolean withTimeZone);
			
			void set(int precision, boolean withTimeZone);
			
			void set(int precision, int intervalClass, int fracionalPrecision);
			
		}
		
		/**
		 * abstract data types: defined by a standard, by an implementation, or by an application
		 */
		public static interface UserDefinedType extends DataType {
			//source(type,)
		}
		
		public static class StandardDataType implements CharacterType, NumericType, EnumeratedType, DateTimeType, UserDefinedType {

			static final Map<String, Class<? extends DataType>> types;
			
			private final int YEAR_TO_MONTH = 0, DAY_TO_HOUR = 1, DAY_TO_MINUTE = 2/*, DAY_TO_SECOND = 3*/;
			
			private String dataType;
			
			private Integer precision;
			
			private Integer intervalClass;
			
			private Integer scale;
			
			private String[] names;
			
			private Boolean withTimeZone;
			
			static {
				types = new HashMap<String, Class<? extends DataType>>();
				/**
				 * <li>boolean type</li>
				 */
				types.put("BOOLEAN", DataType.class);
				/**
				 * <li>string types</li>
				 */
				//character string types
				types.put("CHARACTER", CharacterType.class);
				types.put("CHARACTER VARYING", CharacterType.class);
				types.put("CHARACTER LARGE OBJECT", CharacterType.class);//CLOB, TEXT
				//binary string types
				types.put("BINARY", CharacterType.class);
				types.put("BINARY VARYING", CharacterType.class);
				types.put("BINARY LARGE OBJECT", CharacterType.class);//BLOB
				//bit string types
				types.put("BIT", CharacterType.class);//Deprecated, deleted from SQL:2003
				types.put("BIT VARYING", CharacterType.class);//Deprecated, deleted from SQL:2003
				/**
				 * <li>numeric types</li>
				 */
				//exact numeric types
				types.put("NUMERIC", NumericType.class);
				types.put("DECIMAL", NumericType.class);
				types.put("SMALLINT", NumericType.class);
				types.put("INTEGER", NumericType.class);
				types.put("BIGINT", NumericType.class);
				//approximate numeric types
				types.put("FLOAT", NumericType.class);
				types.put("REAL", NumericType.class);
				types.put("DOUBLE PRECISION", NumericType.class);
				/**
				 * <li>enumerated types</li>
				 */
				types.put("ENUMERATED", EnumeratedType.class);//CREATE TYPE name ENUMERATED(names...);
				/**
				 * <li>datetime types</li>
				 */
				types.put("DATE", DateTimeType.class);
				types.put("TIME", DateTimeType.class);
				types.put("TIMESTAMP", DateTimeType.class);
				//interval type
				types.put("INTERVAL", DateTimeType.class);
			}
			
			public StandardDataType(String dataType) {
				this.dataType = dataType.toUpperCase();
				if(types.get(dataType) == null)
					log.error("no such jdbc type, DO NOT USE SYNONYM!");
			}
			
			public String getDataType() {
				return dataType;
			}

			public String toSQL() {
				StringBuilder sb = new StringBuilder(dataType);
				if(intervalClass != null) {
					if(intervalClass.intValue() == YEAR_TO_MONTH)  sb.append(" YEAR");
					else sb.append(" DAY");
					if(precision != null) sb.append("(").append(precision.intValue()).append(")");
					sb.append(" TO");
					if(intervalClass.intValue() == YEAR_TO_MONTH)  sb.append(" MONTH");
					else if(intervalClass.intValue() == DAY_TO_HOUR) sb.append(" HOUR");
					else if(intervalClass.intValue() == DAY_TO_MINUTE) sb.append(" MINUTE");
					else sb.append(" SECOND");
					if(scale != null) sb.append("(").append(scale.intValue()).append(")");
				}
				else if(types.get(dataType) == EnumeratedType.class){
					sb.append("(");
					for(int i = 0; i < names.length; i++) {
						sb.append(names[i]);
						if(i < names.length - 1)
							sb.append(",");
					}
					sb.append(")");
				}
				else {
					if(precision != null) sb.append("(").append(precision.intValue());
					if(scale != null) sb.append(",").append(scale.intValue());
					sb.append(")");
					if(dataType.equals("TIME") || dataType.equals("TIMESTAMP")) {
						sb.append(" WITH");
						if(withTimeZone != null && !withTimeZone.booleanValue())
							sb.append("OUT");
						sb.append(" TIME ZONE");
					}
				}
				return sb.toString();
			}

			public Boolean withTimeZone() {
				if(!dataType.equals("TIMESTAMP") && !dataType.equals("TIME")) throw new UnsupportedOperationException("data type not support");
				return withTimeZone;
			}

			public void set(boolean withTimeZone) {
				if(!dataType.equals("TIMESTAMP") && !dataType.equals("TIME")) throw new UnsupportedOperationException("data type not support");
				this.withTimeZone = Boolean.valueOf(withTimeZone);
			}

			public void set(int precision, boolean withTimeZone) {
				if(!dataType.equals("TIMESTAMP") && !dataType.equals("TIME")) throw new UnsupportedOperationException("data type not support");
				this.withTimeZone = Boolean.valueOf(withTimeZone);
				this.precision = Integer.valueOf(precision);
				
			}

			public void set(int precision, @Min(0) @Max(4) int intervalClass, int fracionalPrecision) {
				if(!dataType.equals("INTERVAL")) throw new UnsupportedOperationException("data type not support");
				this.precision = Integer.valueOf(precision);
				this.intervalClass = Integer.valueOf(intervalClass);
				this.scale = Integer.valueOf(fracionalPrecision);
			}

			public String[] names() {
				if(names != null) return names;
				if(!dataType.equals("ENUMERATED")) throw new UnsupportedOperationException("data type not support");
				log.info("ENUMERATED value is null");
				return null;
			}

			public void set(String... names) {
				if(!dataType.equals("ENUMERATED")) throw new UnsupportedOperationException("data type not support");
				this.names = names;
			}

			public Integer getPrecision() {
				if(!dataType.equals("NUMERIC") && !dataType.equals("DECIMAL") && !dataType.equals("INTERVAL"))
					throw new UnsupportedOperationException("data type not support");
				if(precision == null && dataType.equals("INTERVAL")) {
					if(log.isDebugEnabled()) log.debug("precision not set, use default");
					return 2;
				}
				return precision;
			}

			public Integer getScale() {
				if(!dataType.equals("NUMERIC") && !dataType.equals("DECIMAL") && !dataType.equals("INTERVAL"))
					throw new UnsupportedOperationException("data type not support");
				return scale;
			}

			public void set(@Min(1) int precision, @Min(0) int scale) {
				if(!dataType.equals("NUMERIC") && !dataType.equals("DECIMAL")) throw new UnsupportedOperationException("data type not support");
				this.precision = Integer.valueOf(precision);
				this.scale = Integer.valueOf(scale);
			}

			public Integer get() {
				if(!dataType.equals("CHARACTER") && !dataType.equals("CHARACTER VARYING") && !dataType.equals("BINARY") && !dataType.equals("BINARY VARYING")
						&& !dataType.equals("BIT") && !dataType.equals("BIT VARYING") && !dataType.equals("TIME") && !dataType.equals("TIMESTAMP")
						&& !dataType.equals("INTERVAL") && types.get(dataType) != NumericType.class) 
					throw new UnsupportedOperationException("data type not support");
				return precision;
			}

			public void set(@Min(0) int length) {
				if(!dataType.equals("CHARACTER") && !dataType.equals("CHARACTER VARYING") && !dataType.equals("BINARY") && !dataType.equals("BINARY VARYING")
						&& !dataType.equals("BIT") && !dataType.equals("BIT VARYING") && !dataType.equals("TIME") && !dataType.equals("TIMESTAMP")
						&& !dataType.equals("INTERVAL") && types.get(dataType) != NumericType.class) 
					throw new UnsupportedOperationException("data type not support");
				if((types.get(dataType) == CharacterType.class || types.get(dataType) == NumericType.class) && length < 1)
					log.warn("length or precision must greater than 1");
				this.precision = Integer.valueOf(length);
			}
		}
		
		
		/**
		 * @see http://dev.mysql.com/doc/refman/5.6/en/data-types.html
		 * @see https://dev.mysql.com/doc/refman/5.6/en/other-vendor-data-types.html
		 *
		 */
		public static class MySQLDataType implements CharacterType, NumericType, EnumeratedType, DateTimeType, UserDefinedType {
			
			static final Map<String, Class<? extends DataType>> types;
			
			private String dataType;
			
			private Integer precision;
			
			/**
			 * In MySQL m precision, d scale like NUMERIC(5,2) max is 999.99
			 */
			private Integer scale;
			
			private boolean unsigned;
			
			private boolean zerofill;
			
			private String[] names;
			
			/**
			 * MySQL converts TIMESTAMP values from the current time zone to UTC for storage, and back from UTC to the current time zone for retrieval
			 */
			@SuppressWarnings("unused")
			private boolean withTimeZone;
			
			static {
				types = new HashMap<String, Class<? extends DataType>>();
				/**
				 * <li>Numeric Types</li>
				 */
				//Integer Types(Extract Value)
				types.put("TINYINT", NumericType.class);//synonyms for BIT(8), BOOL, BOOLEAN are synonyms for TINYINT(1), if precision > 1, value also cannot exceed 128 or 255(unsigned)
				types.put("SMALLINT", NumericType.class);
				types.put("MEDIUMINT", NumericType.class);
				types.put("INT", NumericType.class);//synonyms INTEGER
				types.put("BIGINT", NumericType.class);//SERIAL is an alias for BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE
				//Fixed-Point Types (Exact Value)
				types.put("DECIMAL", NumericType.class);
				//Floating-Point Types (Approximate Value)
				types.put("FLOAT", NumericType.class);// If the REAL_AS_FLOAT SQL mode is enabled, REAL is a synonym for FLOAT rather than DOUBLE
				types.put("DOUBLE", NumericType.class);//synonyms REAL, DOUBLE PRECISION
				//BIT-Value Type
				types.put("BIT", NumericType.class);
				/**
				 * <li>Date and Time Types</li> if ALLOW_INVALID_DATES, a value is out of range or invalid, MySQL converts the value to the “zero” value 
				 */
				types.put("DATE", DateTimeType.class);////supported range is '1000-01-01' to '9999-12-31'
				types.put("DATETIME", DateTimeType.class);////supported range is '1000-01-01 00:00:00' to '9999-12-31 23:59:59'
				types.put("TIMESTAMP", DateTimeType.class);////range of '1970-01-01 00:00:01' UTC to '2038-01-19 03:14:07' UTC
				types.put("YEAR", DateTimeType.class);
				/**
				 * <li>String Types</li>
				 */
				types.put("CHAR", CharacterType.class);
				types.put("VARCHAR", CharacterType.class);
				types.put("BINARY", CharacterType.class);
				types.put("VARBINARY", CharacterType.class);
				//Blob and Text Types
				types.put("TINYBLOB", CharacterType.class);//8
				types.put("BLOB", CharacterType.class);
				types.put("MEDIUMBLOB", CharacterType.class);//24
				types.put("LONGBLOB", CharacterType.class);//32
				types.put("TINYTEXT", CharacterType.class);
				types.put("TEXT", CharacterType.class);
				types.put("MEDIUMTEXT", CharacterType.class);//LONG and LONG VARCHAR map to the MEDIUMTEXT
				types.put("LONGTEXT", CharacterType.class);
				//ENUM Type
				types.put("ENUM", EnumeratedType.class);
				//SET type
				types.put("SET", UserDefinedType.class);
				/**
				 * <li>Spatial Data Types</li>
				 */
				types.put("GEOMETRY", UserDefinedType.class);
				types.put("POINT", UserDefinedType.class);
				types.put("LINESTRING", UserDefinedType.class);
				types.put("POLYGON", UserDefinedType.class);
				types.put("MULTIPOINT", UserDefinedType.class);
				types.put("MULTILINESTRING", UserDefinedType.class);
				types.put("MULTIPOLYGON", UserDefinedType.class);
				types.put("GEOMETRYCOLLECTION", UserDefinedType.class);
			}
			
			public MySQLDataType(String dataType) {
				this.dataType = dataType.toUpperCase();
				if(types.get(this.dataType) == null)
					log.warn("no such jdbc type, DO NOT USE SYNONYM!");
			}
			
			public String getDataType() {
				return dataType;
			}

			public String toSQL() {
				StringBuilder sb = new StringBuilder(dataType);
				if(precision != null || names != null) {
					sb.append(" (");
					if(precision != null) {
						sb.append(precision.intValue());
						if(scale != null)
							sb.append(",").append(scale.intValue());
					}
					else {
						for(String name : names)
							sb.append(name).append(",");
						sb.deleteCharAt(sb.length() - 1);
					}
					sb.append(")");
				}
				if(zerofill) sb.append(" UNSIGNED ZEROFILL");
				else if(unsigned) sb.append(" UNSIGNED");
				return sb.toString();
			}

			public Boolean withTimeZone() {
				if(types.get(dataType) != DateTimeType.class)
					throw new UnsupportedOperationException("data type not support");
				throw new UnsupportedOperationException("mysql not support");
			}
			
			public void set(boolean withTimeZone) {
				if(types.get(dataType) != DateTimeType.class)
					throw new UnsupportedOperationException("data type not support");
				throw new UnsupportedOperationException("mysql not support");
			}

			public void set(int precision, boolean withTimeZone) {
				if(types.get(dataType) != DateTimeType.class)
					throw new UnsupportedOperationException("data type not support");
				throw new UnsupportedOperationException("mysql not support");
			}

			public void set(int precision, int intervalClass, int fracionalPrecision) {
				if(types.get(dataType) != DateTimeType.class)
					throw new UnsupportedOperationException("data type not support");
				throw new UnsupportedOperationException("mysql not support");
			}
			
			public boolean isUnsigned() {
				if(types.get(dataType) != NumericType.class)
					throw new UnsupportedOperationException("data type not support");
				return unsigned;
			}

			public String[] names() {
				if(!dataType.equals("SET") && !dataType.equals("ENUM"))
					throw new UnsupportedOperationException("data type not support");
				return names;
			}

			public void set(String... names) {
				if(!dataType.equals("SET") && !dataType.equals("ENUM"))
					throw new UnsupportedOperationException("data type not support");
				this.names = names;
			}

			public Integer getPrecision() {
				if(types.get(dataType) != NumericType.class && !dataType.equals("TIME") && !dataType.equals("DATETIME") && !dataType.equals("TIMESTAMP"))
					throw new UnsupportedOperationException("data type not support");
				if(precision == null) {
					if(log.isDebugEnabled()) log.debug("precision not set, try to use default");
					if(dataType.equals("DECIMAL")) return 10;
					else if(dataType.equals("FLOAT")) return 23;
					else if(dataType.equals("DOUBLE")) {
						if(scale == null) return 53;
						else return 255;
					}
				}
				return precision;
			}

			public Integer getScale() {
				if(!dataType.equals("DECIMAL") && !dataType.equals("FLOAT") && !dataType.equals("DOUBLE"))
					throw new UnsupportedOperationException("data type not support");
				if(scale == null) {
					if(log.isDebugEnabled()) log.debug("scale not set, try to use default");
					if(dataType.equals("DECIMAL")) return 0;
				}
				return scale;
			}

			public void set(int precision, int scale) {
				set(precision, scale, false, false);
			}

			public Integer get() {
				if(types.get(dataType) == UserDefinedType.class || (types.get(dataType) == DateTimeType.class && dataType.equals("DATE"))
						|| (types.get(dataType) == CharacterType.class && (dataType.indexOf("BLOB") > 0 || dataType.indexOf("TEXT") > 0))
						|| types.get(dataType) == EnumeratedType.class)
					throw new UnsupportedOperationException("data type not support");
				if(precision == null) {
					if(log.isDebugEnabled()) log.debug("length or fraction not set, use default[max] value");
					if(dataType.equals("TINYINT")) return 1;
					else if(dataType.equals("SMALLINT")) return 2;
					else if(dataType.equals("MEDIUMINT")) return 3;
					else if(dataType.equals("INT")) return 4;
					else if(dataType.equals("BIGINT")) return 8;
					else if(dataType.equals("CHAR") || dataType.equals("BINARY")) return 255;
					else if(dataType.equals("VARCHAR") || dataType.equals("VARBINARY")) return 65535;
					else if(dataType.equals("TIME") || dataType.equals("DATETIME") || dataType.equals("DATETIME"))
							return 0;//differs from the standard SQL default of 6, for compatibility with previous MySQL versions.
					else if(dataType.equals("YEAR")) return 4;
				}
				return precision;
			}

			public void set(@Min(0) @Max(65535) int length) {
				if(types.get(dataType) == CharacterType.class) {
					if(dataType.indexOf("BLOB") > 0 || dataType.indexOf("TEXT") > 0)
						throw new UnsupportedOperationException("data type not support");
					if((dataType.equals("CHAR") || dataType.equals("BINARY")) && length > 255)
						log.error("length cannot exceed 255");
					precision = Integer.valueOf(length);
				}
				else if(types.get(dataType) == NumericType.class) {
					set(length, false, false);
				}
				else if(types.get(dataType) == DateTimeType.class) {
					if(dataType.equals("DATE")) throw new UnsupportedOperationException("data type not support");
					if(length > 6 || (dataType.equals("YEAR") && length != 2 && length != 4))
						log.error("datetime fraction cannot be more than 6, type [year] accept 2 or 4");
					this.precision = Integer.valueOf(length);
				}
				else throw new UnsupportedOperationException("data type not support");
			}

			public void set(@Min(0) @Max(65) int precision, boolean unsigned, boolean zerofill) {
				if(types.get(dataType) != NumericType.class)
					throw new UnsupportedOperationException("data type not support");
				if(dataType.equals("FLOAT") && precision > 24) {
					log.warn("automatic change float to double when precision greater than 24");
					dataType = "DOUBLE";
				}
				if((dataType.equals("TINYINT") && precision > 1) || (dataType.equals("SMALLINT") && precision > 2) || (dataType.equals("MEDIUMINT") && precision > 3)
						|| (dataType.equals("INT") && precision > 4) || (dataType.equals("BIGINT") && precision > 8) || (dataType.equals("BIT") && precision > 64)
						|| (dataType.equals("DOUBLE") && precision > 53))
					log.warn("precision greater than maxium precision, value also cannot exceed");
				this.precision = Integer.valueOf(precision);
				this.unsigned = unsigned;
				this.zerofill = zerofill;
				if(zerofill && !unsigned)
					log.error("if you specify ZEROFILL for a numeric column, MySQL automatically adds the UNSIGNED attribute to the column");
				if(zerofill) this.unsigned = true;
			}
			
			public void set(@Min(0) @Max(255) int precision, @Min(0) @Max(253) int scale, boolean unsigned, boolean zerofill) {
				if(!dataType.equals("DECIMAL") && !dataType.equals("FLOAT") && !dataType.equals("DOUBLE"))
					throw new UnsupportedOperationException("data type not support");
				if(dataType.equals("DECIMAL") && precision + scale > 65)
					log.error("too many digits");
				if((dataType.equals("TINYINT") && precision > 1) || (dataType.equals("SMALLINT") && precision > 2) || (dataType.equals("MEDIUMINT") && precision > 3)
						|| (dataType.equals("INT") && precision > 4) || (dataType.equals("BIGINT") && precision > 8) || (dataType.equals("BIT") && precision > 64)
						|| (dataType.equals("DOUBLE") && precision > 53))
					log.warn("precision greater than maxium precision, value also cannot exceed");
				if(zerofill && !unsigned)
					log.error("if you specify ZEROFILL for a numeric column, MySQL automatically adds the UNSIGNED attribute to the column");
				this.precision = Integer.valueOf(precision);
				this.scale = Integer.valueOf(scale);
				this.zerofill = zerofill;
				if(zerofill) this.unsigned = true;
			}
			
		}
		
		/**
		 * @see http://docs.oracle.com/cd/B28359_01/server.111/b28286/sql_elements001.htm#SQLRF0021
		 *
		 */
		public static class OracleDataType implements CharacterType, NumericType, DateTimeType, UserDefinedType {
			
			static final Map<String, Class<? extends DataType>> types;
			
			private static final int YEAR_TO_MONTH = 0, DAY_TO_SECOND = 3;
			
			private String dataType;
			
			private Integer precision;
			
			private Integer intervalClass;
			
			private Integer scale;
			
			private Boolean withTimeZone;
			
			static {
				types = new HashMap<String, Class<? extends DataType>>();
				/**
				 * <li>Character Datatypes</li>
				 */
				types.put("CHAR", CharacterType.class);
				types.put("VARCHAR2", CharacterType.class);
				types.put("NCHAR", CharacterType.class);
				types.put("NVARCHAR2", CharacterType.class);
				/**
				 * <li>Long and RAW Datatypes</li>
				 */
				types.put("LONG", CharacterType.class);//<2G, use CLOB or NCLOB instead!
				types.put("RAW", CharacterType.class);//<2000bytes, can be indexed
				types.put("LONG RAW", CharacterType.class);//<2G, cannot be indexed, use the BLOB or BFILE instead!
				/**
				 * <li>Large Object Datatypes</li>
				 */
				types.put("CLOB", CharacterType.class);
				types.put("NCLOB", CharacterType.class);
				types.put("BLOB", CharacterType.class);//<128T
				types.put("BFILE", CharacterType.class);//<4G, read only; stores unstructured binary data in operating-system files outside the database
				/**
				 * <li>Number Datatypes</li>
				 */
				types.put("NUMBER", NumericType.class);//FLOAT[(p)] A subtype of the NUMBER datatype having precision p
				types.put("BINARY_FLOAT", NumericType.class);//approximate numeric datatypes
				types.put("BINARY_DOUBLE", NumericType.class);//approximate numeric datatypes
				/**
				 * <li>Date Time Datatypes</li>
				 */
				types.put("DATE", DateTimeType.class);
				types.put("TIMESTAMP", DateTimeType.class);//ignored TIMESTAMP WITH LOCAL TIME ZONE
				types.put("INTERVAL", DateTimeType.class);
				/**
				 * <li>ROWID Datatyppes</li>
				 */
				types.put("ROWID", UserDefinedType.class);
				types.put("UROWID", UserDefinedType.class);
				/**
				 * <li>Oracle-Supplied Types</li>
				 */
				//Any Types
				types.put("ANYTYPE", UserDefinedType.class);
				types.put("ANYDATA", UserDefinedType.class);
				types.put("ANYDATASET", UserDefinedType.class);
				//XML Types
				types.put("XMLType", UserDefinedType.class);
				types.put("URIType", UserDefinedType.class);//subtypes: DBURIType, XDBURIType, HTTPURIType
				//Spatial Types
				types.put("SDO_GEOMETRY", UserDefinedType.class);
				types.put("SDO_TOPO_GEOMETRY", UserDefinedType.class);
				types.put("SDO_GEORASTER", UserDefinedType.class);
				//Media Types: ORDAudio, ORDImage, ORDVideo, ORDDoc, ORDDicom, SI_StillImage, SI_Color, SI_AverageColor, SI_ColorHistogram, SI_PositionalColor, SI_Texture, SI_FeatureList, ORDImageSignature
			}
			
			public OracleDataType(String dataType) {
				this.dataType = dataType.toUpperCase();
				if(types.get(this.dataType) == null)
					log.warn("no such jdbc type, DO NOT USE SYNONYM!");
			}
			
			public String getDataType() {
				return dataType;
			}

			public String toSQL() {
				StringBuilder sb = new StringBuilder(dataType);
				if(intervalClass != null) {
					if(intervalClass.intValue() == YEAR_TO_MONTH)  sb.append(" YEAR");
					else sb.append(" DAY");
					if(precision != null) sb.append("(").append(precision.intValue()).append(")");
					sb.append(" TO");
					if(intervalClass.intValue() == YEAR_TO_MONTH)  sb.append(" MONTH");
					else sb.append(" SECOND");
					if(scale != null) sb.append("(").append(scale.intValue()).append(")");
				} else {
					if(precision != null) sb.append(" (").append(precision.intValue());
					if(scale != null) sb.append(",").append(scale.intValue());
					sb.append(")");
					if(withTimeZone != null && withTimeZone) sb.append(" WITH TIME ZONE");
				}
				return sb.toString();
			}

			public Boolean withTimeZone() {
				if(!dataType.equals("TIMESTAMP")) throw new UnsupportedOperationException("data type not support");
				if(withTimeZone == null) {
					if(log.isDebugEnabled()) log.debug("WITH[OUT] TIME ZONE not set, use default");
					return true;
				}
				return withTimeZone;
			}
			
			public void set(boolean withTimeZone) {
				if(!dataType.equals("TIMESTAMP")) throw new UnsupportedOperationException("data type not support");
				this.withTimeZone = Boolean.valueOf(withTimeZone);
			}

			public void set(@Min(0) @Max(9) int precision, boolean withTimeZone) {
				if(!dataType.equals("TIMESTAMP")) throw new UnsupportedOperationException("data type not support");
				this.precision = Integer.valueOf(precision);
				this.withTimeZone = Boolean.valueOf(withTimeZone);
			}

			public void set(@Min(0) @Max(9) int precision, int intervalClass, @Min(0) @Max(9) int fracionalPrecision) {
				if(!dataType.equals("INTERVAL")) throw new UnsupportedOperationException("data type not support");
				if(intervalClass != YEAR_TO_MONTH && intervalClass != DAY_TO_SECOND)
					log.error("oracle has no such interval");
				this.precision = Integer.valueOf(precision);
				if(intervalClass == DAY_TO_SECOND)
					this.scale = Integer.valueOf(fracionalPrecision);
				
			}

			public Integer getPrecision() {
				if(!dataType.equals("NUMBER") && !dataType.equals("FLOAT") && !dataType.equals("TIMESTAMP") && !dataType.equals("INTERVAL"))
					throw new UnsupportedOperationException("data type not support");
				if(precision == null) {
					if(log.isDebugEnabled()) log.debug("precision not set, use default value");
					if(dataType.equals("NUMBER")) return 38;//max
					else if(dataType.equals("INTERVAL")) return 2;
				}
				return precision;
			}

			public Integer getScale() {
				if(!dataType.equals("NUMBER") && !dataType.equals("INTERVAL"))
					throw new UnsupportedOperationException("data type not support");
				if(scale == null) {
					if(log.isDebugEnabled()) log.debug("scale not set, use default value");
					if(dataType.equals("NUMBER")) return 0;
					else if(intervalClass != null && intervalClass == DAY_TO_SECOND) return 6;
				}
				return scale;
			}

			public void set(@Min(1) @Max(38) int precision, @Min(-84) @Max(127) int scale) {
				if(!dataType.equals("NUMBER")) throw new UnsupportedOperationException("data type not support");
				this.precision = Integer.valueOf(precision);
				this.scale = Integer.valueOf(scale);
			}

			public Integer get() {
				if(!dataType.equals("CHAR") && !dataType.equals("NCHAR") && !dataType.equals("VARCHAR2") && !dataType.equals("NVARCHAR2")
						&& !dataType.equals("RAW") && !dataType.equals("NUMBER") && !dataType.equals("FLOAT")
						&& !dataType.equals("TIMESTAMP") && !dataType.equals("INTERVAL") && !dataType.equals("UROWID"))
					throw new UnsupportedOperationException("data type not support");
				if(precision == null) {
					if(log.isDebugEnabled()) log.debug("length not set, use default value");
					if(dataType.equals("CHAR") || dataType.equals("NCHAR")) return 1;
					else if(dataType.equals("TIMESTAMP")) return 6;
					else if(dataType.equals("UROWID") || dataType.equals("NVARCHAR2") || dataType.equals("VARCHAR2")) return 4000;//must specify size
				}
				return precision;
			}

			public void set(@Min(0) @Max(4000) int length) {
				if(!dataType.equals("CHAR") && !dataType.equals("NCHAR") && !dataType.equals("NVARCHAR2") && !dataType.equals("VARCHAR2")
						&& !dataType.equals("UROWID") && !dataType.equals("TIMESTAMP") && !dataType.equals("RAW") && !dataType.equals("NUMBER"))
					throw new UnsupportedOperationException("data type not support");
				if((dataType.equals("CHAR") || dataType.equals("NCHAR") || dataType.equals("RAW")) && length > 2000)
					log.error("length is too big");
				else if(dataType.equals("TIMESTAMP") && length > 9)
					log.error("length is too big");
				precision = Integer.valueOf(length);
				if(dataType.equals("NUMBER"))
					set(length, 0);
			}

		}
		
		/**
		 * @see http://www.postgresql.org/docs/9.5/interactive/datatype.html
		 *
		 */
		public static class PostgresDataType implements CharacterType, NumericType, EnumeratedType, DateTimeType, UserDefinedType {

			static final Map<String, Class<? extends DataType>> types;
			
			private String dataType;
			
			private Integer precision;
			
			private Integer intervalClass;
			
			private final int YEAR_TO_MONTH = 0, DAY_TO_HOUR = 1, DAY_TO_MINUTE = 2, DAY_TO_SECOND = 3, HOUR_TO_MINUTE = 4, HOUR_TO_SECOND = 5, MINUTE_TO_SECOND = 6;
			
			private Integer scale;
			
			private Boolean withTimeZone;
			
			private String[] names;
			
			static {
				types = new HashMap<String, Class<? extends DataType>>();
				/**
				 * <li>Numeric Types</li>
				 */
				types.put("SMALLINT", NumericType.class);//alias INT2
				types.put("INTEGER", NumericType.class);//alias INT or INT4
				types.put("BIGINT", NumericType.class);//alias INT8
				types.put("NUMERIC", NumericType.class);//alias DECIMAL
				types.put("REAL", NumericType.class);//alias FLOAT4, FLOAT(p) 1=<p<=24
				types.put("DOUBLE PRECISION", NumericType.class);//alias FLOAT8, FLOAT(p) 25=<p<=53
				types.put("SMALLSERIAL", NumericType.class);//alias SERIAL2
				types.put("SERIAL", NumericType.class);//alias SERIAL4
				types.put("BIGSERIAL", NumericType.class);//alias SERIAL8
				/**
				 * <li>Monetary Types</li>
				 */
				types.put("MONEY", UserDefinedType.class);
				/**
				 * <li>Character Types</li>
				 */
				types.put("CHARACTER", CharacterType.class);//alias CHAR
				types.put("CHARACTER VARING", CharacterType.class);//alias VARCHAR
				types.put("TEXT", CharacterType.class);
				/**
				 * <li>Binary Data Types</li>
				 */
				types.put("BYTEA", CharacterType.class);
				/**
				 * Date/Time Types
				 */
				types.put("DATE", DateTimeType.class);
				types.put("TIME", DateTimeType.class);//alias TIMETZ
				types.put("TIMESTAMP", DateTimeType.class);//alias TIMESTAMPTZ
				types.put("INTERVAL", DateTimeType.class);
				/**
				 * <li>Boolean Type</li>
				 */
				types.put("BOOLEAN", DataType.class);//alias BOOL: true, false, unknow
				/**
				 * <li>Enumerated Types</li>
				 */
				types.put("ENUM", EnumeratedType.class);//create type name as ENUM(strings...)
				/**
				 * <li>Geometric Types</li>
				 */
				types.put("POINT", UserDefinedType.class);//(x, y)
				types.put("LINE", UserDefinedType.class);//{A,B,C}
				types.put("LSEG", UserDefinedType.class);//((x1,y1),(x2,y2))
				types.put("BOX", UserDefinedType.class);//((x1,y1),(x2,y2))
				types.put("PATH", UserDefinedType.class);//close path: ((x1,y1),...) or open path: [(x1,y1),...]
				types.put("POLYGON", UserDefinedType.class);//((x1,y1),...)
				types.put("CIRCLE", UserDefinedType.class);//<(x,y),r>
				/**
				 * <li>Network Address Types</li>
				 */
				types.put("CIDR", UserDefinedType.class);
				types.put("INET", UserDefinedType.class);
				types.put("MACADDR", UserDefinedType.class);
				/**
				 * <li>Bit String Types</li>
				 */
				types.put("BIT", CharacterType.class);
				types.put("BIT VARING", CharacterType.class);
				/**
				 * <li>Text Search Types</li>
				 */
				types.put("TSQUERY", UserDefinedType.class);
				types.put("TSVECTOR", UserDefinedType.class);
				/**
				 * <li>UUID Type</li>
				 */
				types.put("UUID", UserDefinedType.class);
				/**
				 * <li>XML Type</li>
				 */
				types.put("XML", UserDefinedType.class);
				/**
				 * <li>JSON Types</li>
				 */
				types.put("JSON", UserDefinedType.class);
				types.put("JSONB", UserDefinedType.class);
				/**
				 * <li>Arrays</li>
				 */
				types.put("ARRAY", UserDefinedType.class);//Arrays: TEXT[][], INTEGER[][], ...
				/**
				 * <li>Composite Types</li>CREATE TYPE name AS {col type}
				 */
				/**
				 * <li>Range Types</li>
				 */
				types.put("INT4RANGE", UserDefinedType.class);
				types.put("INT8RANGE", UserDefinedType.class);
				types.put("NUMRANGE", UserDefinedType.class);
				types.put("TSRANGE", UserDefinedType.class);
				types.put("TSTZRANGE", UserDefinedType.class);
				types.put("DATERANGE", UserDefinedType.class);
				/**
				 * <li>Object Identifier Types</li>
				 */
				types.put("OID", UserDefinedType.class);//alias as regproc, regprocedure, regoper, regoperator, regclass, regtype, regrole, regnamespace, regconfig, or regdictionary
				/**
				 * <li>pg_lsn Type</li>
				 */
				types.put("PG_SLN", UserDefinedType.class);
				/**
				 * <li>Pseudo-Types</li>any, anyelement, anyarray, anynonarray, anyenum, 
				 * anyrange, cstring, internal, language_handler, fdw_handler, tsm_handler,
				 * record, trigger, event_trigger, pg_ddl_command, void, opaque
				 */
			}
			
			public PostgresDataType(String dataType) {
				this.dataType = dataType.toUpperCase();
				if(types.get(this.dataType) == null)
					log.warn("no such jdbc type, DO NOT USE SYNONYM!");
			}
			
			public String getDataType() {
				return dataType;
			}

			public String toSQL() {
				StringBuilder sb = new StringBuilder();
				if(dataType.equals("ARRAY")) {
					sb.append(names[0]);
					for(int i = 0; i < precision.intValue(); i++)
						sb.append("[]");
				} else {
					sb.append(dataType);
					if(intervalClass != null) {
						switch (intervalClass.intValue()) {
						case YEAR_TO_MONTH:
							sb.append(" YEAR TO MONTH");
							break;
						case DAY_TO_HOUR:
							sb.append(" DAY TO HOUR");
							break;
						case DAY_TO_MINUTE:
							sb.append(" DAY TO MINUTE");
							break;
						case DAY_TO_SECOND:
							sb.append(" DAY TO SECOND");
							break;
						case HOUR_TO_MINUTE:
							sb.append(" HOUR TO MINUTE");
							break;
						case HOUR_TO_SECOND:
							sb.append(" HOUR TO SECOND");
							break;
						case MINUTE_TO_SECOND:
							sb.append(" MINUTE TO SECOND");
							break;
						default:
						}	
					}
					if(precision != null) {
						sb.append("(").append(precision.intValue());
						if(scale != null) sb.append(",").append(scale.intValue());
						sb.append(")");
					}
					if(withTimeZone != null) {
						sb.append(" WITH");
						if(!withTimeZone.booleanValue()) sb.append("OUT");
						sb.append(" TIME ZONE");
					}
					if(names != null) {
						sb.append("(");
						for(int i = 0; i < names.length; i++) {
							sb.append(names[i]);
							if(i < names.length - 1)
								sb.append(",");
						}
						sb.append(")");
					}
				}
				return sb.toString();
			}

			public Boolean withTimeZone() {
				if(!dataType.equals("TIME") && !dataType.equals("TIMESTAMP"))
					throw new UnsupportedOperationException("data type not support");
				if(withTimeZone == null) {
					if(log.isDebugEnabled()) log.debug("WITH TIME ZONE not set, use default");
					if(dataType.equals("TIME") || dataType.equals("TIMESTAMP")) return false;
				}
				return withTimeZone;
			}
			
			public void set(boolean withTimeZone) {
				if(!dataType.equals("TIME") && !dataType.equals("TIMESTAMP"))
					throw new UnsupportedOperationException("data type not support");
				this.withTimeZone = Boolean.valueOf(withTimeZone);
			}

			public void set(@Min(0) @Max(6) int precision, boolean withTimeZone) {
				if(!dataType.equals("TIME") && !dataType.equals("TIMESTAMP"))
					throw new UnsupportedOperationException("data type not support");
				this.precision = Integer.valueOf(precision);
				this.withTimeZone = Boolean.valueOf(withTimeZone);
			}

			public void set(@Min(0) @Max(6) int precision, int intervalClass, int fracionalPrecision) {
				if(!dataType.equals("INTERVAL"))
					throw new UnsupportedOperationException("data type not support");
				if(intervalClass > MINUTE_TO_SECOND)
					log.error("no such interval data type");
				this.precision = Integer.valueOf(precision);
				//XXX pgAdmin: interval year to month --auto translate --> "interval year to month"(65535), but exec this create script will fail
			}

			public Integer getPrecision() {
				if(!dataType.equals("NUMERIC") && !dataType.equals("INTERVAL"))
					throw new UnsupportedOperationException("data type not support");
				if(precision == null) {
					if(log.isDebugEnabled()) log.debug("precision not set, use default");
					if(dataType.equals("NUMERIC")) return 1000;//values of any precision and scale
				}
				return precision;
			}

			public Integer getScale() {
				if(!dataType.equals("NUMERIC"))
					throw new UnsupportedOperationException("data type not support");
				return scale;
			}

			public void set(@Min(1) @Max(1000) int precision, @Min(0) @Max(1000) int scale) {
				if(!dataType.equals("NUMERIC"))
					throw new UnsupportedOperationException("data type not support");
				this.precision = Integer.valueOf(precision);
				this.scale = Integer.valueOf(scale);
			}

			public Integer get() {
				if(!dataType.equals("BIT") && !dataType.equals("BIT VARING") && !dataType.equals("CHARACTER") && !dataType.equals("CHARACTER VARING")
						&& !dataType.equals("INTERVAL") && !dataType.equals("NUMERIC") && !dataType.equals("TIME") && !dataType.equals("TIMESTAMP"))
					throw new UnsupportedOperationException("data type not support");
				if(precision == null) {
					if(log.isDebugEnabled()) log.debug("precision or length not set, use default or max");
					if(dataType.equals("CHARACTER") || dataType.equals("BIT")) return 1;
					else if(dataType.equals("TIME") || dataType.equals("TIMESTAMP")) return 6;//no explicit bound on precision
				}
				return precision;
			}

			public void set(@Min(0) int length) {
				if(!dataType.equals("BIT") && !dataType.equals("BIT VARING")
						&& !dataType.equals("CHARACTER") && !dataType.equals("CHARACTER VARING")
						&& !dataType.equals("INTERVAL") && !dataType.equals("NUMERIC")
						&& !dataType.equals("TIME") && !dataType.equals("TIMESTAMP")
						&& !dataType.equals("ARRAY"))
					throw new UnsupportedOperationException("data type not support");
				if((dataType.equals("ARRAY") || dataType.equals("BIT") || dataType.equals("BIT VARING")
						|| dataType.equals("CHARACTER") || dataType.equals("CHARACTER VARING")) 
						&& length < 1)
					log.error("length must greater than 1");
				this.precision = Integer.valueOf(length);
			}

			public String[] names() {
				if(!dataType.equals("ENUM"))
					throw new UnsupportedOperationException("data type not support");
				return names;
			}

			public void set(String... names) {
				if(!dataType.equals("ENUM") && !dataType.equals("ARRAY"))
					throw new UnsupportedOperationException("data type not support");
				if(dataType.equals("ARRAY") && names.length != 1)
					log.error("ARRAY is single type");
				this.names = names;
			}
			
		}
		
		/*
		public static enum Format {
			FIXED,
			DYNAMIC,
			DEFAULT
		}
		
		public static enum Storage {
			DISK,
			MEMORY,
			DEFAULT;
		}
		*/
		
		public String toSQL() {
			StringBuilder sb = new StringBuilder();
			sb.append(identifier).append(" ").append(dataType.toSQL());
			if(!nullable)
				sb.append(" NOT NULL");
			if(defaultValue != null)
				sb.append(" DEFAULT ").append(defaultValue);
			if(autoIncrement)
				sb.append(" AUTO_INCREMENT");
			return sb.toString();
		}
	}
	
	public class Constraint {
		
		@Pattern(regexp = "[PK|UK|FK|IDX|C]_[0-9A-Z_]+")
		protected String name;
		
		protected String identifier;
		
		@Size(min = 1)
		protected Column[] columns;
		
		public Constraint(Column... columns) {
			this.columns = columns;
		}
		
		public Constraint(String identifier, Column... columns) {
			this.identifier = identifier;
			String name = unquote(identifier);
			if(isKeyword(name))
				log.warn("constraint name [{}] should better not be sql keywrod", name);
			this.name = name;
			this.columns = columns;
		}
		
		protected String getType() {
			return "KEY";
		}
		
		public String getName() {
			return name;
		}

		public void setName(String identifier) {
			this.identifier = identifier;
			String name = unquote(identifier);
			if(isKeyword(name))
				log.warn("constraint name [{}] should better not be sql keywrod", name);
			this.name = name;
		}

		public Column[] getColumns() {
			return columns;
		}

		public void setColumns(Column[] columns) {
			this.columns = columns;
		}

		public String toSQL() {
			StringBuilder sb = new StringBuilder(getType());
			if(identifier != null) sb.append(" ").append(identifier);
			sb.append(" ").append("(");
			for(int i = 0, j = columns.length; i < j; i++) {
				sb.append(columns[i].identifier);
				if(i < j - 1)
					sb.append(",");
			}
			return sb.append(")").toString();
		}
		
		//ignore type and options
	}
	
	public class PrimaryKey extends Constraint {

		@Override
		protected String getType() {
			return "PRIMARY KEY";
		}
		
	}
	
	public class Index extends Constraint {
		
		private String algorithm;
		
//		private boolean reverse = false;
		
		public String getAlgorithm() {
			return algorithm;
		}


		public void setAlgorithm(String algorithm) {
			if(algorithm == null || algorithm.trim().length() == 0) throw new RuntimeException("if set algorithm, algorithm shoud not be null");
			switch(dialect) {
			case MYSQL : 
				if(!Arrays.asList("BTREE", "HASH", "RTREE").contains(algorithm.toUpperCase())) throw new RuntimeException("Unsupport algorithm: " + algorithm);
				break;
			case POSTGRES : 
				if(!Arrays.asList("btree", "hash", "gist", "gin", "spgist", "brin").contains(algorithm.toLowerCase())) throw new RuntimeException("Unsupport algorithm: " + algorithm);
				break;
			case ORACLE : 
				if(!Arrays.asList("btree", "bitmap").contains(algorithm.toLowerCase())) throw new RuntimeException("Unsupport algorithm: " + algorithm);
				break;
			default:
			}
			this.algorithm = algorithm;
		}


		@Override
		protected String getType() {
			return "INDEX";
		}
		
		@Override
		public String toSQL() {
			return super.toSQL() + (algorithm == null ? "" : " USING " + algorithm);
		}
		
	}
	
	public class UniqueKey extends Constraint {
		
		@Override
		protected String getType() {
			return "UNIQUE INDEX";
		}
	}
	
	public class ForeignKey extends Constraint {
		
		@Size(min = 1)
		private Column[] references;
		
//		private Action onUpdate = Action.RESTRICT;
//		
//		private Action onDelete = Action.RESTRICT;
		
		public ForeignKey() {
			
		}
		
		public ForeignKey(String name, Column[] self, Column[] reference) {
			if(isKeyword(name))
				log.warn("foreign key name [{}] should better not be sql keywrod", name);
			this.name = name;
			assert self.length == reference.length;
			this.columns = self;
			this.references = reference;
		}
		
		public Column[] getReferences() {
			return references;
		}

		public void setReferences(Column[] references) {
			this.references = references;
		}

		@Override
		public String toSQL() {
			StringBuilder sb = new StringBuilder("CONSTRAINT");
			sb.append(" ").append(identifier);
			sb.append(" FOREIGN KEY (");
			for(int i = 0, j = columns.length; i < j; i++) {
				sb.append(columns[i].identifier);
				if(i != j - 1)
					sb.append(",");
			}
			sb.append(")").append(" REFERENCES ");
			sb.append(references[0].table.identifier);
			sb.append(" (");
			for(int i = 0, j = references.length; i < j; i++) {
				sb.append(references[i].identifier);
				if(i < j - 1)
					sb.append(",");
			}
			sb.append(")");
			return sb.toString();
		}
		
		/*
		public enum Action {
			RESTRICT,
			CASCADE,
			SET_NULL,
			SET_DEFAULT,
			NO_ACTION
		}*/
	}
	
	public class Check extends Constraint {
		
		private String searchCondition;//exec will return true or false
		
		public String getSearchCondition() {
			return searchCondition;
		}

		public void setSearchCondition(String searchCondition) {
			this.searchCondition = searchCondition;
		}
		
		@Override
		public String toSQL() {
			return "CHECK (" + searchCondition + ")";
		}
	}
	
	/**
	 * @see https://dev.mysql.com/doc/refman/5.7/en/storage-engines.html
	 *
	 */
	/*private enum Engine {
		InnoDB,//The default storage engine in MySQL 5.7
		MyISAM,
		MEMORY,
		CSV,
		ARCHIVE,//compact, unindexed
		BLACKHOLE,
		NDB,
		MRG_MYISAM,//Merge
		Federated,
		Example,
	}*/
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		sb.append("\"").append(name).append("\":").append("[");
		for(int i = 0, j = columns.size(); i < j; i++) {
			sb.append("\"").append(columns.get(i).name).append("\"");
			if(i < j - 1)
				sb.append(",");
		}
		return sb.append("]").append("}").toString();
	}
	
	public String toSQL() {
		StringBuilder sb = new StringBuilder("CREATE TABLE ");
		if(catalogIdentifier != null)
			sb.append(catalogIdentifier).append(".");
		if(schemaIdentifier != null)
			sb.append(schemaIdentifier).append(".");
		sb.append(identifier).append(" (\n");
		for(int i = 0, j = columns.size(); i < j; i++) {
			sb.append(columns.get(i).toSQL()).append(",\n");
		}
		if(indexes != null && !indexes.isEmpty()) {
			for(Index index : indexes)
				sb.append(index.toSQL()).append(",\n");
		}
		if(pk != null) 
			sb.append(pk.toSQL()).append(",\n");
		if(uks != null && !uks.isEmpty()) {
			for(UniqueKey uk : uks)
				sb.append(uk.toSQL()).append(",\n");
		}
		if(fks != null && !fks.isEmpty()) {
			for(ForeignKey fk : fks)
				sb.append(fk.toSQL()).append(",\n");
		}
		if(checks != null && !checks.isEmpty()) {
			for(Check ck : checks)
				sb.append(ck.toSQL()).append(",\n");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append(");");
		return sb.toString();
	}
	
}
