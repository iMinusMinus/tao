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

import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * DBMS table abstract
 * 
 * @author iMinusMinus
 * @since 2017-01-24
 * @version 0.0.1
 *
 */
public class Table {
	
	private final Dialect dialect;
	
	public Table (Dialect dialect) {
		this.dialect = dialect;
	}
	
	private String catalog;//Sybase/MS SQLServer: database name
	
	private String schema;//Oracle: user, MySQL: database name
	
	@NotNull
	private String name;//catalog.schema.tbl_name
	
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
	
	public Dialect getDialect() {
		return dialect;
	}
	
	
	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
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

	public List<UniqueKey> getUks() {
		return uks;
	}

	public void setUks(List<UniqueKey> uks) {
		this.uks = uks;
	}

	public List<ForeignKey> getFks() {
		return fks;
	}

	public void setFks(List<ForeignKey> fks) {
		this.fks = fks;
	}

	public List<Index> getIndexes() {
		return indexes;
	}

	public void setIndexes(List<Index> indexes) {
		this.indexes = indexes;
	}

	public List<Check> getChecks() {
		return checks;
	}

	public void setChecks(List<Check> checks) {
		this.checks = checks;
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
		
		public Column(String name, DataType dataType) {
			this.name = name;
			this.dataType = dataType;
		}
		
		private Table table;
		
		private String name;
		
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

		public void setName(String name) {
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

		/**
		 * @see http://www.inf.fu-berlin.de/lehre/SS94/einfdb/SQL3/sql-foundation-mar94.txt
		 * @see http://www.iso.org/iso/home/store/catalogue_tc/catalogue_detail.htm?csnumber=63556
		 * 
	     * primitive data types, or named built-in data types or predefined types
		 */
		public static interface DataType {
			
			String toSQL();
			
			public static final DataType BOOLEAN = new DataType() {
				public String toSQL() {
					return "BOOLEAN";
				}
			};
			
		}
		
		public static interface CharacterDataType extends DataType {
			
			int get();
			
			void set(int length);
			
			public static final DataType CHARACTER = new CharacterDataType() {
				
				@Min(1)
				private int length;
				
				public void set(int length) {
					this.length = length;
				}
				
				public int get() {
					return length;
				}
				
				public String toSQL() {
					StringBuilder sb = new StringBuilder("CHARACTER");
					if(length > 0)
						sb.append("(").append(length).append(")");
					return sb.toString();
				}
			};
			
			public static final DataType CHARACTER_VARYING = new CharacterDataType() {
				
				@Min(1)
				private int length;
				
				public void set(int length) {
					this.length = length;
				}
				
				public int get() {
					return length;
				}

				public String toSQL() {
					StringBuilder sb = new StringBuilder("CHARACTER VARYING");
					if(length > 0)
						sb.append("(").append(length).append(")");
					return sb.toString();
				}
				
			};
			
			public static final DataType CHARACTER_LARGE_OBJECT = new CharacterDataType() {//CLOB, TEXT

				public void set(int length) {
					throw new NotImplementedException();
					
				}

				public int get() {
					throw new NotImplementedException();
				}
				
				public String toSQL() {
					return "CHARACTER LARGE OBJECT VARYING";
				}
				
			};
			
			public static final DataType BINARY = new CharacterDataType() {
				
				@Min(1)
				private int length;

				public void set(int length) {
					this.length = length;
				}

				public int get() {
					return length;
				}

				public String toSQL() {
					StringBuilder sb = new StringBuilder("BINARY");
					if(length > 0)
						sb.append("(").append(length).append(")");
					return sb.toString();
				}
				
			};
			
			public static final DataType BINARY_VARYING = new CharacterDataType() {
				
				@Min(1)
				private int length;

				public void set(int length) {
					this.length = length;
				}

				public int get() {
					return length;
				}
				
				public String toSQL() {
					StringBuilder sb = new StringBuilder("BINARY VARYING");
					if(length > 0)
						sb.append("(").append(length).append(")");
					return sb.toString();
				}
				
			};
			
			public static final DataType BINARY_LARGE_OBJECT = new CharacterDataType() {//BLOB

				public void set(int length) {
					throw new NotImplementedException();
				}

				public int get() {
					throw new NotImplementedException();
				}
				
				public String toSQL() {
					return "BINARY LARGE OBJECT";
				}
				
			};
			
			@Deprecated//deleted from SQL:2003
			public static final DataType BIT = new CharacterDataType() {
				
				@Min(1)
				private int length;

				public void set(int length) {
					this.length = length;
				}

				public int get() {
					return length;
				}
				
				public String toSQL() {
					StringBuilder sb = new StringBuilder("BIT");
					if(length > 0)
						sb.append("(").append(length).append(")");
					return sb.toString();
				}
				
			};
			
			@Deprecated//deleted from SQL:2003
			public static final DataType BIT_VARYING = new CharacterDataType() {

				@Min(1)
				private int length;
				
				public void set(int length) {
					this.length = length;
					
				}

				public int get() {
					return length;
				}
				
				public String toSQL() {
					StringBuilder sb = new StringBuilder("BIT VARYING");
					if(length > 0)
						sb.append("(").append(length).append(")");
					return sb.toString();
				}
				
			};
			
		}
		
		public static interface NumeberDataType extends DataType {
			
			int getPrecision();
			
			int getScale();
			
			void set(int precision);
			
			void set(int precision, int scale);
			
			public static final DataType NUMERIC = new NumeberDataType() {
				
				@Min(1)
				private int precision;
				
				@Min(0)
				private int scale;
				
				public int getPrecision() {
					return precision;
				}
				
				public int getScale() {
					return scale;
				}

				public void set(int precision) {
					this.precision = precision;
					scale = 0;
				}

				public void set(int precision, int scale) {
					this.precision = precision;
					this.scale = scale;
				}
				
				public String toSQL() {
					StringBuilder sb = new StringBuilder("NUMERIC");
					if(precision > 0)
						sb.append("(").append(precision).append(",").append(scale).append(")");
					return sb.toString();
				}
				
			};
			
			public static final DataType DECIMAL = new NumeberDataType() {
				
				@Min(1)
				private int precision;
				
				@Min(0)
				private int scale;
				
				public int getPrecision() {
					return precision;
				}
				
				public int getScale() {
					return scale;
				}
				
				public void set(int length) {
					precision = length;
					scale = 0;
				}

				public void set(int precision, int scale) {
					this.precision = precision;
					this.scale = scale;
				}
				
				public String toSQL() {
					StringBuilder sb = new StringBuilder("DECIMAL");
					if(precision > 0)
						sb.append("(").append(precision).append(",").append(scale).append(")");
					return sb.toString();
				}
				
			};
			
			public static final DataType SMALLINT = new NumeberDataType() {
				
				@Min(1)
				private int precision;
				
				public int getPrecision() {
					return precision;
				}
				
				public int getScale() {
					throw new NotImplementedException();
				}

				public void set(int length) {
					precision = length;
				}

				public void set(int precision, int scale) {
					throw new NotImplementedException();
				}
				
				public String toSQL() {
					StringBuilder sb = new StringBuilder("SMALLINT");
					if(precision > 0)
						sb.append("(").append(precision).append(")");
					return sb.toString();
				}
				
			};

			public static final DataType INTEGER = new NumeberDataType() {
				
				@Min(1)
				private int precision;
				
				public int getPrecision() {
					return precision;
				}
				
				public int getScale() {
					throw new NotImplementedException();
				}
				
				public void set(int precision) {
					this.precision = precision;
				}

				public void set(int precision, int scale) {
					throw new NotImplementedException();
					
				}
				
				public String toSQL() {
					StringBuilder sb = new StringBuilder("INTEGER");
					if(precision > 0)
						sb.append("(").append(precision).append(")");
					return sb.toString();
				}
				
			};
			
			public static final DataType BIGINT = new NumeberDataType() {

				@Min(1)
				private int precision;
				
				public int getPrecision() {
					return precision;
				}
				
				public int getScale() {
					throw new NotImplementedException();
				}
				
				public void set(int precision) {
					this.precision = precision;
				}

				public void set(int precision, int scale) {
					throw new NotImplementedException();
					
				}
				
				public String toSQL() {
					StringBuilder sb = new StringBuilder("BIGINT");
					if(precision > 0)
						sb.append("(").append(precision).append(")");
					return sb.toString();
				}
				
			};
			
			public static final DataType FLOAT = new NumeberDataType() {

				@Min(1)
				private int precision;
				
				public int getPrecision() {
					return precision;
				}
				
				public int getScale() {
					throw new NotImplementedException();
				}
				
				public void set(int precision) {
					this.precision = precision;
				}

				public void set(int precision, int scale) {
					throw new NotImplementedException();
					
				}
				
				public String toSQL() {
					StringBuilder sb = new StringBuilder("FLOAT");
					if(precision > 0)
						sb.append("(").append(precision).append(")");
					return sb.toString();
				}
				
			};
			
			public static final DataType REAL = new NumeberDataType() {

				@Min(1)
				private int precision;
				
				public int getPrecision() {
					return precision;
				}
				
				public int getScale() {
					throw new NotImplementedException();
				}
				
				public void set(int precision) {
					this.precision = precision;
				}

				public void set(int precision, int scale) {
					throw new NotImplementedException();
					
				}
				
				public String toSQL() {
					StringBuilder sb = new StringBuilder("REAL");
					if(precision > 0)
						sb.append("(").append(precision).append(")");
					return sb.toString();
				}
				
			};
			
			public static final DataType DOUBLE_PRECISION = new NumeberDataType() {

				@Min(1)
				private int precision;
				
				public int getPrecision() {
					return precision;
				}
				
				public int getScale() {
					throw new NotImplementedException();
				}
				
				public void set(int precision) {
					this.precision = precision;
				}

				public void set(int precision, int scale) {
					throw new NotImplementedException();
					
				}
				
				public String toSQL() {
					StringBuilder sb = new StringBuilder("DOUBLE PRECISION");
					if(precision > 0)
						sb.append("(").append(precision).append(")");
					return sb.toString();
				}
				
			};
		}	
		public static interface EnumeratedDataType extends DataType {	
			
			String[] names();
			
			void set(String...names);
			
			public static final DataType ENUMERATED = new EnumeratedDataType() {
				
				private String[] names;
				
				public String[] names() {
					return names;
				}

				public void set(String...names) {
					this.names = names;
				}

				public String toSQL() {//CREATE TYPE name ENUMERATED(names...);
					StringBuilder sb = new StringBuilder("ENUMERATED(");
					for(String name : names)
						sb.append(name).append(",");
					sb.setLength(sb.length() - 1);
					return sb.append(")").toString();
				}
				
			};
		}	
		
		public static interface DateTimeDataType extends DataType {	
			
			int get();
			
			void set(int precision);
			
			boolean withTimeZone();
			
			void set(int precision, boolean withTimeZone);
			
			void set(int precision, int intervalClass, int fracionalPrecision);
			
			public static final DataType DATE = new DateTimeDataType() {
				
				public int get() {
					throw new UnsupportedOperationException("[DATE] has no fractional seconds precision!");
				}

				public void set(int precision) {
					throw new UnsupportedOperationException("[DATE] has no fractional seconds precision!");
				}
				
				public boolean withTimeZone() {
					throw new UnsupportedOperationException();
				}

				public void set(int precision, boolean withTimeZone) {
					throw new UnsupportedOperationException();
				}
				
				public void set(int precision, int intervalClass, int fracionalPrecision) {
					throw new UnsupportedOperationException();
				}
				
				public String toSQL() {
					return "DATE";
				}
				
			};
			
			public static final DataType TIME = new DateTimeDataType() {
				
				private int precision;
				
				private boolean withTimeZone = false;
				
				public int get() {
					return precision;
				}

				public void set(int precision) {
					this.precision = precision;
				}
				
				public boolean withTimeZone() {
					return withTimeZone;
				}

				public void set(int precision, boolean withTimeZone) {
					this.precision = precision;
					this.withTimeZone = withTimeZone;
				}
				
				public void set(int precision, int intervalClass, int fracionalPrecision) {
					throw new UnsupportedOperationException();
				}
				
				public String toSQL() {
					StringBuilder sb = new StringBuilder("TIME");
					if(precision >= 0)
						sb.append(" (").append(precision).append(")");
					sb.append(" WITH");
					if(!withTimeZone)
						sb.append("OUT");
					sb.append(" TIME ZONE");
					return sb.toString();
				}
				
			};
			
			public static final DataType TIMESTAMP = new DateTimeDataType() {

				private int precision;
				
				private boolean withTimeZone = false;
				
				public int get() {
					return precision;
				}

				public void set(int precision) {
					this.precision = precision;
				}
				
				public boolean withTimeZone() {
					return withTimeZone;
				}

				public void set(int precision, boolean withTimeZone) {
					this.precision = precision;
					this.withTimeZone = withTimeZone;
				}
				
				public void set(int precision, int intervalClass, int fracionalPrecision) {
					throw new UnsupportedOperationException();
				}
				
				public String toSQL() {
					StringBuilder sb = new StringBuilder("TIMESTAMP");
					if(precision >= 0)
						sb.append(" (").append(precision).append(")");
					sb.append(" WITH");
					if(!withTimeZone)
						sb.append("OUT");
					sb.append(" TIME ZONE");
					return sb.toString();
				}
				
			};
			
			public static final DataType INTERVAL = new DateTimeDataType() {
				
				private int precision = 2;
				
				private int fractionalSecondPrecision;
				
				@Min(0)
				@Max(4)
				private int intervalClass;
				
				private final int YEAR_TO_MONTH = 0, DAY_TO_HOUR = 1, DAY_TO_MINUTE = 2/*, DAY_TO_SECOND = 3*/;
				
				public int get() {
					return precision;
				}

				public void set(int intervalClass) {
					this.intervalClass = intervalClass;
					
				}
				
				public boolean withTimeZone() {
					throw new NotImplementedException();
				}

				public void set(int precision, boolean withTimeZone) {
					throw new NotImplementedException();
				}
				
				public void set(int precision, int intervalClass, int fracionalPrecision) {
					this.precision = precision;
					this.intervalClass = intervalClass;
					this.fractionalSecondPrecision = fracionalPrecision;
				}
				
				public String toSQL() {
					StringBuilder sb = new StringBuilder("INTERVAL ");
					if(intervalClass == YEAR_TO_MONTH) 
						sb.append("YEAR");
					else sb.append("DAY");
					if(precision > 0) 
						sb.append(" (").append(precision).append(")");
					sb.append(" ");
					if(intervalClass == YEAR_TO_MONTH)
						sb.append("MONTH");
					else if(intervalClass == DAY_TO_HOUR)
						sb.append("HOUR");
					else if(intervalClass == DAY_TO_MINUTE)
						sb.append("MINUTE");
					else sb.append("SECOND");
					if(fractionalSecondPrecision > 0)
						sb.append(" (").append(fractionalSecondPrecision).append(")");
					return sb.toString();
				}
				
			};
			
		}
		
		/**
		 * abstract data types: defined by a standard, by an implementation, or by an application
		 */
		public static interface UserDefinedDataType extends DataType {
			//source(type,)
		}
		
		
		/**
		 * @see http://dev.mysql.com/doc/refman/5.6/en/data-types.html
		 * @see https://dev.mysql.com/doc/refman/5.6/en/other-vendor-data-types.html
		 *
		 */
		public static enum MySQLDataType implements CharacterDataType, NumeberDataType, EnumeratedDataType, DateTimeDataType, UserDefinedDataType {
			TINYINT {//synonyms for BIT(8)
				
				private int m = 1;
				
				private boolean unsigned = false;
				
				private boolean zerofill = false;
				
				private boolean init = false;
				
				public int get() {
					return m;
				}
				
				/**
				 * @param m the number of bits per value, if m > 1, value also cannot exceed 128 or 255(unsigned)
				 */
				public void set(int m) {
					set(m, false, false);
				}
				
				public void set(int m, boolean unsigned, boolean zerofill) {
					this.m = m;
					this.unsigned = unsigned;
					this.zerofill = zerofill;
					init = true;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(m).append(")");
					if(unsigned)
						sb.append(" UNSIGNED");
					if(zerofill)
						sb.append(" ZEROFILL");
					return sb.toString();
				}
			},
			SMALLINT {
				private int m = 2;
				
				private boolean unsigned = false;
				
				private boolean zerofill = false;
				
				private boolean init = false;
				
				public void set(int m) {
					set(m, false, false);
				}
				
				public void set(int m, boolean unsigned, boolean zerofill) {
					this.m = m;
					this.unsigned = unsigned;
					this.zerofill = zerofill;
					init = true;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(m).append(")");
					if(unsigned)
						sb.append(" UNSIGNED");
					if(zerofill)
						sb.append(" ZEROFILL");
					return sb.toString();
				}
			},
			MEDIUMINT {
				
				private int m = 3;
				
				private boolean unsigned = false;
				
				private boolean zerofill = false;
				
				private boolean init = false;
				
				public int get() {
					return m;
				}
				
				public void set(int m) {
					set(m, false, false);
				}
				
				public void set(int m, boolean unsigned, boolean zerofill) {
					this.m = m;
					this.unsigned = unsigned;
					this.zerofill = zerofill;
					init = true;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(m).append(")");
					if(unsigned)
						sb.append(" UNSIGNED");
					if(zerofill)
						sb.append(" ZEROFILL");
					return sb.toString();
				}
			},
			INT {//synonyms INTEGER
				
				private int m = 4;
				
				private boolean unsigned = false;
				
				private boolean zerofill = false;
				
				private boolean init = false;
				
				public int get() {
					return m;
				}
				
				public void set(int m) {
					set(m, false, false);
				}
				
				public void set(int m, boolean unsigned, boolean zerofill) {
					this.m = m;
					this.unsigned = unsigned;
					this.zerofill = zerofill;
					init = true;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(m).append(")");
					if(unsigned)
						sb.append(" UNSIGNED");
					if(zerofill)
						sb.append(" ZEROFILL");
					return sb.toString();
				}
			},
			BIGINT {//

				private int m = 8;
				
				private boolean unsigned = false;
				
				private boolean zerofill = false;
				
				private boolean init = false;
				
				public int get() {
					return m;
				}
				
				public void set(int m) {
					set(m, false, false);
				}
				
				public void set(int m, boolean unsigned, boolean zerofill) {
					this.m = m;
					this.unsigned = unsigned;
					this.zerofill = zerofill;
					init = true;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(m).append(")");
					if(unsigned)
						sb.append(" UNSIGNED");
					if(zerofill)
						sb.append(" ZEROFILL");
					return sb.toString();
				}
			},
			/**
			 * <li>Fixed-Point Types (Exact Value)
			 */
			DECIMAL {
				
				private boolean init = false;
				
				private int m = 10;
				
				private int d;
				
				private boolean unsigned = false;
				
				private boolean zerofill = false;
				
				public int getPrecision() {
					return m;
				}
				
				public int getScale() {
					return d;
				}
				
				public void set(int precision) {
					set(precision, 0);
				}
				
				public void set(int precision, int scale) {
					set(precision, scale, false, false);
				}
				
				public void set(int precision, int scale, boolean unsigned, boolean zerofill) {
					m = precision;
					d = scale;
					this.unsigned = unsigned;
					this.zerofill = zerofill;
					init = true;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(m).append(",").append(d).append(")");
					if(unsigned)
						sb.append(" UNSIGNED");
					if(zerofill)
						sb.append(" ZEROFILL");
					return sb.toString();
				}
			},
			/**
			 * <li>Floating-Point Types (Approximate Value)
			 */
			FLOAT{
				
				private int m;
				
				private int d;
				
				private boolean unsigned = false;
				
				private boolean zerofill = false;
				
				public int getPrecision() {
					return m;
				}
				
				public void set(int m) {//if m > 23, ddl column definition automatic change to double
					set(m, 0);
				}
				
				public int getScale() {
					return d;
				}
				
				public void set(int precision, int scale) {
					set(precision, scale, false, false);
				}
				
				public void set(@Min(1) @Max(23)int precision, int scale, boolean unsigned, boolean zerofill) {
					assert precision >= scale;
					m = precision;
					d = scale;
					this.unsigned = unsigned;
					this.zerofill = zerofill;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(m > 0)
						sb.append("(").append(m).append(",").append(d).append(")");
					if(unsigned)
						sb.append(" UNSIGNED");
					if(zerofill)
						sb.append(" ZEROFILL");
					return sb.toString();
				}
			},
			DOUBLE {//synonyms REAL
				
				private int m;
				
				private int d;
				
				private boolean unsigned = false;
				
				private boolean zerofill = false;
				
				public int getPrecision() {
					return m;
				}
				
				public void set(int m) {
					set(m, 0);
				}
				
				public int getScale() {
					return d;
				}
				
				public void set(int m, int d) {
					set(m, d, false, false);
				}
				
				public void set(@Min(24) @Max(53)int precision, int scale, boolean unsigned, boolean zerofill) {
					assert precision >= scale;
					m = precision;
					d = scale;
					this.unsigned = unsigned;
					this.zerofill = zerofill;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(m > 0)
						sb.append("(").append(m).append(",").append(d).append(")");
					if(unsigned)
						sb.append(" UNSIGNED");
					if(zerofill)
						sb.append(" ZEROFILL");
					return sb.toString();
				}
			},
			//DOUBLE_PRECISION,
			
			BIT {//b'111' --> 7
				
				private int m;
				
				private boolean init = false;
				
				public int get() {
					return m;
				}
				
				public void set(@Min(1) @Max(64) int precision) {
					m = precision;
				}
				
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(m).append(")");
					return sb.toString();
				}
				
			},
			
			/**
			 * <ul>Date and Time Types:</ul>if ALLOW_INVALID_DATES, a value is out of range or invalid, MySQL converts the value to the “zero” value 
			 */
			DATE,//supported range is '1000-01-01' to '9999-12-31'.
			DATETIME{//supported range is '1000-01-01 00:00:00' to '9999-12-31 23:59:59'.
				
				private int fraction;
				
				private boolean init = false;
				
				public int get() {
					return fraction;
				}
				
				public void set(@Max(6) int precision) {
					this.fraction = precision;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(fraction).append(")");
					return sb.toString();
				}
				
			},
			TIMESTAMP{//range of '1970-01-01 00:00:01' UTC to '2038-01-19 03:14:07' UTC.

				private int fraction;
				
				private boolean init = false;
				
				public int get() {
					return fraction;
				}
				
				public void set(@Max(6) int precision) {
					this.fraction = precision;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(fraction).append(")");
					return sb.toString();
				}
				
			},
			
			TIME{

				private int fraction;
				
				private boolean init = false;
				
				public int get() {
					return fraction;
				}
				
				public void set(@Max(6) int precision) {
					this.fraction = precision;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(fraction).append(")");
					return sb.toString();
				}
			},
			YEAR{
				
				private int digit = 4;//2 or 4
				
				private boolean init = false;
				
				public void set(@Pattern(regexp = "[2|4]") int m) {
					digit = m;
					init = true;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(digit).append(")");
					return sb.toString();
				}
				
			},
			
			/**
			 * <ul>String Types:
			 */
			CHAR {
				
				private int length;
				
				private boolean init = false;
				
				public int get() {
					return length;
				}
				
				public void set(@Min(0) @Max(255) int length) {
					this.length = length;
					init = true;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(length).append(")");
					return sb.toString();
				}
			},
			VARCHAR {
				
				private int length;
				
				private boolean init = false;
				
				public int get() {
					return length;
				}
				
				public void set(@Min(0) @Max(65535) int length) {
					this.length = length;
					init = true;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(length).append(")");
					return sb.toString();
				}
			},
			
			BINARY {
				
				private int length;
				
				private boolean init = false;
				
				public int get() {
					return length;
				}
				
				public void set(@Min(0) @Max(255) int length) {
					this.length = length;
					init = true;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(length).append(")");
					return sb.toString();
				}
			},
			VARBINARY {
				
				private int length;
				
				private boolean init = false;
				
				public int get() {
					return length;
				}
				
				public void set(@Min(0) @Max(65535) int length) {
					this.length = length;
					init = true;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(length).append(")");
					return sb.toString();
				}
			},
			/**
			 * <li>Blob and Text Types
			 */
			 TINYBLOB, BLOB, MEDIUMBLOB, LONGBLOB,//8,,24,32
			 TINYTEXT, TEXT, MEDIUMTEXT, LONGTEXT,//8,,24,32; LONG and LONG VARCHAR map to the MEDIUMTEXT
			 
			 /**
			  * <li>ENUM Type
			  */
			 ENUM {
				
				private String[] names;
				
				public String[] names() {
					return names;
				}
				
				public void set(String...names) {
					this.names = names;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(names != null && names.length > 0) {
						sb.append("(");
						for(int i = 0, j = names.length; i < j; i++) {
							sb.append(names[i]);
							if(i < j - 1)
								sb.append(",");
						}
						sb.append(")");
					}
					return sb.toString();
				}
			},
			 /**
			  * <li>SET type
			  */
			 SET {
				
				private String[] names;
				
				public String[] names() {
					return names;
				}
				
				public void set(String...names) {
					this.names = names;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(names != null && names.length > 0) {
						sb.append("(");
						for(int i = 0, j = names.length; i < j; i++) {
							sb.append(names[i]);
							if(i < j - 1)
								sb.append(",");
						}
						sb.append(")");
					}
					return sb.toString();
				}
			},
			 
			 /**
			  * <ul>Spatial Data Types
			  */
			 GEOMETRY,
			 POINT,
			 LINESTRING,
			 POLYGON,
			 
			 MULTIPOINT,
			 MULTILINESTRING,
			 MULTIPOLYGON,
			 GEOMETRYCOLLECTION,;

			public String toSQL() {
				return super.toString();
			}

			public boolean withTimeZone() {
				throw new UnsupportedOperationException();
			}

			public void set(int precision, boolean withTimeZone) {
				throw new UnsupportedOperationException();
			}

			public void set(int precision, int intervalClass, int fracionalPrecision) {
				throw new UnsupportedOperationException();
			}

			public String[] names() {
				throw new AbstractMethodError();
			}

			public void set(String... names) {
				throw new AbstractMethodError();
			}

			public int getPrecision() {
				throw new AbstractMethodError();
			}

			public int getScale() {
				throw new AbstractMethodError();
			}

			public void set(int precision, int scale) {
				throw new AbstractMethodError();
			}

			public int get() {
				throw new AbstractMethodError();
			}

			public void set(int length) {
				throw new AbstractMethodError();
			}

			public void set(int precision, boolean unsigned, boolean zerofill) {
				throw new AbstractMethodError();
			}
			
			public void set(int precision, int scale, boolean unsigned, boolean zerofill) {
				throw new AbstractMethodError();
			}
			
		}
		
		/**
		 * @see https://docs.oracle.com/cd/B28359_01/server.111/b28318/datatype.htm#CNCPT313
		 *
		 */
		public static enum OracleDataType implements CharacterDataType, NumeberDataType, DateTimeDataType, UserDefinedDataType {
			CHAR{
				
				private int length = 1;
				
				private boolean init = false;
				
				public int get() {
					return length;
				}
				
				public void set(@Min(1) @Max(2000) int length) {
					this.length = length;
					init = true;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(length).append(")");
					return sb.toString();
				}
			},
			VARCHAR2{
				
				private int length = 1;
				
				private boolean init = false;
				
				public int get() {
					return length;
				}
				
				public void set(@Min(1) @Max(4000) int length) {
					this.length = length;
					init = true;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(length).append(")");
					return sb.toString();
				}
			},
			NCHAR{
				
				private int length = 1;
				
				private boolean init = false;
				
				public int get() {
					return length;
				}
				
				public void set(@Min(1) @Max(2000) int length) {
					this.length = length;
					init = true;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(length).append(")");
					return sb.toString();
				}
			},
			NVARCHAR2{
				
				private int length = 1;
				
				private boolean init = false;
				
				public int get() {
					return length;
				}
				
				public void set(@Min(1) @Max(4000) int length) {
					this.length = length;
					init = true;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(length).append(")");
					return sb.toString();
				}
			},
			
			CLOB,
			NCLOB,
			@Deprecated LONG,//<2G, use CLOB or NCLOB instead!
			
			NUMBER{
				
				private int precision;
				
				private int scale;//maybe negative
				
				public int getPrecision() {
					return precision;
				}
				
				
				public void set(int length) {
					set(length, 0);
				}
				
				public int getScale() {
					return scale;
				}
				
				public void set(@Max(38) @Min(1) int precision, @Max(127) @Min(-84) int scale) {
					if(precision > 38)
						throw new IllegalArgumentException("Data Type [NUMBER] cannot exceed 38!");
					if(precision + scale < 0 || scale > precision)
						throw new IllegalArgumentException("Data Type [NUMBER] |scale| cannot exceed precision!");
					this.precision = precision;
					this.scale = scale;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(precision > 0)
						sb.append("(").append(precision).append(",").append(scale).append(")");
					return sb.toString();
				}
			},
			BINARY_FLOAT,//approximate numeric datatypes
			BINARY_DOUBLE,//approximate numeric datatypes
			DATE,
			TIMESTAMP{
				
				private int fraction;
				
				private boolean withTimeZone;
				
				public int get() {
					return fraction;
				}
				
				public void set(int fraction) {
					this.fraction = fraction;
				}
				
				public boolean withTimeZone() {
					return withTimeZone;
				}
				
				public void set(int fraction, boolean withTimeZone) {
					this.fraction = fraction;
					this.withTimeZone = withTimeZone;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(fraction > 0)
						sb.append("(").append(fraction).append(")");
					if(withTimeZone)
						sb.append(" WITH TIME ZONE");//XXX ignored TIMESTAMP WITH LOCAL TIME ZONE
					return sb.toString();
				}
			},
			INTERVAL {
				
				private int precision;
				
				private int fractionalSecondPrecision;
				
				private int intervalClass;
				
				private final int YEAR_TO_MONTH = 0, DAY_TO_SECOND = 3;
				
				public int get() {
					return precision;
				}

				public void set(int intervalClass) {
					this.intervalClass = intervalClass;
					
				}
				
				public boolean withTimeZone() {
					throw new NotImplementedException();
				}

				public void set(int precision, boolean withTimeZone) {
					throw new NotImplementedException();
				}
				
				public void set(int precision, int intervalClass, int fracionalPrecision) {
					this.precision = precision;
					this.intervalClass = intervalClass;
					this.fractionalSecondPrecision = fracionalPrecision;
				}
				
				public String toSQL() {
					StringBuilder sb = new StringBuilder("INTERVAL ");
					if(intervalClass == YEAR_TO_MONTH) 
						sb.append("YEAR");
					else sb.append("DAY");
					if(precision > 0) 
						sb.append(" (").append(precision).append(")");
					sb.append(" ");
					if(intervalClass == YEAR_TO_MONTH)
						sb.append("MONTH");
					else sb.append("SECOND");
					if(fractionalSecondPrecision > 0)
						sb.append(" (").append(fractionalSecondPrecision).append(")");
					return sb.toString();
				}
			},
			BLOB,//<128T
			BFILE,//<4G, read only; stores unstructured binary data in operating-system files outside the database
			@Deprecated RAW,//<2000bytes, can be indexed
			@Deprecated LONG_RAW,//<2G, //cannot be indexed, use the BLOB or BFILE instead!
			ROWID,//pseudocolumn 
			UROWID,
			XMLType,
			UriType,
			;

			public String toSQL() {
				return super.toString().replace("_", " ");
			}

			public boolean withTimeZone() {
				throw new AbstractMethodError();
			}

			public void set(int precision, boolean withTimeZone) {
				throw new AbstractMethodError();
			}

			public void set(int precision, int intervalClass, int fracionalPrecision) {
				throw new UnsupportedOperationException();
				
			}

			public int getPrecision() {
				throw new AbstractMethodError();
			}

			public int getScale() {
				throw new AbstractMethodError();
			}

			public void set(int precision, int scale) {
				throw new AbstractMethodError();
			}

			public int get() {
				throw new AbstractMethodError();
			}

			public void set(int length) {
				throw new AbstractMethodError();
			}

		}
		
		/**
		 * @see http://www.postgresql.org/docs/9.5/interactive/datatype.html
		 *
		 */
		public static enum PostgresDataType implements CharacterDataType, NumeberDataType, EnumeratedDataType, DateTimeDataType, UserDefinedDataType {
			/**
			 * Numeric Types
			 */
			SMALLINT,//alias INT2
			INTEGER,//alias INT or INT4
			BIGINT,//alias int8
			NUMERIC {//alias DECIMAL
				
				private int precision;
				
				private int scale;
				
				public int getPrecision() {
					return precision;
				}
				
				public void set(int precision) {
					set(precision, 0);
				}
				
				public int getScale() {
					return scale;
				}
				
				public void set(@Max(1000) int precision, int scale) {
					this.precision = precision;
					this.scale =scale;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(precision > 0)
						sb.append("(").append(precision).append(",").append(scale).append(")");
					return sb.toString();
				}
			},
			REAL,//alias FLOAT4, FLOAT(p) 1=<p<=24
			DOUBLE_PRECISION,//alias FLOAT8, FLOAT(p) 25=<p<=53
			SMALLSERIAL,//alias SERIAL2
			SERIAL,//alias SERIAL4
			BIGSERIAL,//alias SERIAL8
			
			/**
			 * Monetary Types
			 */
			MONEY,
			
			/**
			 * Character Types
			 */
			CHARACTER {//alias CHAR
				
				private int length = 1;
				
				private boolean init = true;
				
				public int get() {
					return length;
				}
				
				public void set(@Min(1) int length) {
					this.length = length;
					init = true;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(length).append(")");
					return sb.toString();
				}
			},
			CHARACTER_VARING {//alias VARCHAR

				private int length;
				
				public int get() {
					return length;
				}
				
				public void set(int length) {
					this.length = length;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(length > 0)
						sb.append("(").append(length).append(")");
					return sb.toString();
				}
			},
			TEXT,
			
			/**
			 * Binary Data Types
			 */
			BYTEA,
			
			/**
			 * Date/Time Types
			 */
			DATE,
			TIME {//alias TIMETZ
				
				private int precision;
				
				private boolean withTimeZone;
				
				private Boolean init = null;
				
				public int get() {
					return precision;
				}
				
				public void set(@Min(0) @Max(6) int precision) {
					this.precision = precision;
					init = Boolean.FALSE;
				}
				
				public boolean withTimeZone() {
					return withTimeZone;
				}
				
				public void set(@Min(0) @Max(10) int precision, boolean withTimeZone) {
					this.precision = precision;
					this.withTimeZone = withTimeZone;
					init = Boolean.TRUE;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init != null) {
						sb.append(" (").append(precision).append(")");
						if(init) {
							sb.append(" WITH");
							if(!withTimeZone)
								sb.append("OUT");
							sb.append(" TIME ZONE");
						}	
					}
					return sb.toString();
				}
			},
			TIMESTAMP {//alias TIMESTAMPTZ
				
				private int precision;
				
				private boolean withTimeZone;
				
				private Boolean init = null;
				
				public int get() {
					return precision;
				}
				
				public void set(@Min(0) @Max(6) int precision) {
					this.precision = precision;
					init = Boolean.FALSE;
				}
				
				public boolean withTimeZone() {
					return withTimeZone;
				}
				
				public void set(@Min(0) @Max(6) int precision, boolean withTimeZone) {
					this.precision = precision;
					this.withTimeZone = withTimeZone;
					init = Boolean.TRUE;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init != null) {
						sb.append(" (").append(precision).append(")");
						if(init) {
							sb.append(" WITH");
							if(!withTimeZone)
								sb.append("OUT");
							sb.append(" TIME ZONE");
						}	
					}
					return sb.toString();
				}
			},
			INTERVAL {
				private int intervalClass;
				private int precision;
				private final int YEAR_TO_MONTH = 0, DAY_TO_HOUR = 1, DAY_TO_MINUTE = 2, DAY_TO_SECOND = 3, HOUR_TO_MINUTE = 4, HOUR_TO_SECOND = 5, MINUTE_TO_SECOND = 6;
				
				@Override public String toSQL() {
					return null;//TODO
				}
			},
			
			/**
			 * Boolean Type
			 */
			BOOLEAN,//alias BOOL: true, false, unknow
			
			/**
			 * Enumerated Types
			 */
			ENUM {//create type name as ENUM(strings...)
				
				private String[] names;
				
				public String[] names() {
					return names;
				}

				public void set(String... names) {
					this.names = names;
				}
			},
			
			/**
			 * Geometric Types
			 */
			POINT,//(x, y)
			LINE,//{A,B,C}
			LSEG,//((x1,y1),(x2,y2))
			BOX,//((x1,y1),(x2,y2))
			PATH,//close path: ((x1,y1),...) or open path: [(x1,y1),...]
			POLYGON,//((x1,y1),...)
			CIRCLE,//<(x,y),r>
			
			/**
			 * Network Address Types
			 */
			CIDR,
			INET,
			MACADDR,
			
			/**
			 * Bit String Types
			 */
			BIT {
				
				private int length = 1;
				
				private boolean init = false;
				
				public int get() {
					return length;
				}
				
				public void set(int length) {
					this.length = length;
					init = true;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(init)
						sb.append("(").append(length).append(")");
					return sb.toString();
				}
			},
			BIT_VARING {//alias VARBIT
				
				private int length;
				
				public int get() {
					return length;
				}
				
				public void set(int length) {
					this.length = length;
				}
				
				@Override public String toSQL() {
					StringBuilder sb = new StringBuilder(super.toSQL());
					if(length > 0)
						sb.append("(").append(length).append(")");
					return sb.toString();
				}
			},
			
			/**
			 * Text Search Types
			 */
			TSQUERY,
			TSVECTOR,
			
			UUID,
			
			XML,
			
			JSON,
			JSONB,
			
			//Arrays: TEXT[][], INTEGER[][], ...
			
			//Composite Types: CREATE TYPE name AS {col type}
			
			//Range Types: INT4RANGE, INT8RANGE, NUMRANGE, TSRANGE, DATERANGE
			
			//Object Identifier Types: oid alias as regproc, regprocedure, regoper, regoperator, regclass, regtype, regrole, regnamespace, regconfig, or regdictionary
			
			PG_SLN {
				@Override public String toSQL() {
					return "PG_SLN";
				}
			},
			
			//Pseudo-Types: cannot be used as a column data type
			;

			public String toSQL() {
				return super.toString().replace("_", " ");
			}

			public boolean withTimeZone() {
				throw new AbstractMethodError();
			}

			public void set(int precision, boolean withTimeZone) {
				throw new AbstractMethodError();
			}

			public void set(int precision, int intervalClass, int fracionalPrecision) {
				throw new AbstractMethodError();
			}

			public int getPrecision() {
				throw new AbstractMethodError();
			}

			public int getScale() {
				throw new AbstractMethodError();
			}

			public void set(int precision, int scale) {
				throw new AbstractMethodError();
			}

			public int get() {
				throw new AbstractMethodError();
			}

			public void set(int length) {
				throw new AbstractMethodError();
			}

			public String[] names() {
				throw new AbstractMethodError();
			}

			public void set(String... names) {
				throw new AbstractMethodError();
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
	}
	
	public static class Constraint {
		
		@Pattern(regexp = "[PK|UK|FK|IDX|C]_[0-9A-Z_]+")
		protected String name;
		
		@Size(min = 1)
		protected Column[] columns;
		
		public Constraint(Column... columns) {
			this.columns = columns;
		}
		
		public Constraint(String name, Column... columns) {
			this.name = name;
			this.columns = columns;
		}
		
		protected String getType() {
			return "KEY";
		}
		
		public String toSQL() {
			StringBuilder sb = new StringBuilder(getType());
			if(name != null) sb.append(" ").append(name);
			sb.append(" ").append("(");
			for(int i = 0, j = columns.length; i < j; i++) {
				sb.append(columns[i].name);
				if(i < j - 1)
					sb.append(",");
			}
			return sb.append(")").toString();
		}
		
		//ignore type and options
	}
	
	public static class PrimaryKey extends Constraint {

		@Override
		protected String getType() {
			return "PRIMARY KEY";
		}
		
	}
	
	public class Index extends Constraint {
		
		private String algorithm;
		
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
				if(!Arrays.asList("btree", "hash", "gist", "gin", "spgist", "brin").contains(algorithm.toUpperCase())) throw new RuntimeException("Unsupport algorithm: " + algorithm);
				break;
			case ORACLE : 
				if(!Arrays.asList("btree", "bitmap").contains(algorithm.toUpperCase())) throw new RuntimeException("Unsupport algorithm: " + algorithm);//TODO
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
			return super.toSQL() + (algorithm == null ? "" : "USING " + algorithm);
		}
		
	}
	
	public static class UniqueKey extends Constraint {
		
		@Override
		protected String getType() {
			return "UNIQUE INDEX";
		}
	}
	
	public static class ForeignKey extends Constraint {
		
		@Size(min = 1)
		private Column[] references;
		
//		private Action update = Action.RESTRICT;
//		
//		private Action delete = Action.RESTRICT;
		
		public ForeignKey(String name, Column[] self, Column[] reference) {
			this.name = name;
			assert self.length == reference.length;
			this.columns = self;
			this.references = reference;
		}
		
		@Override
		public String toSQL() {
			StringBuilder sb = new StringBuilder("CONSTRAINT");
			sb.append(" ").append(name);
			sb.append(" FOREIGN KEY (");
			for(int i = 0, j = columns.length; i < j; i++) {
				sb.append(columns[i].name);
				if(i != j - 1)
				sb.append(",");
			}
			sb.append(")").append(" REFERENCES ");
			sb.append(references[0].table.name);
			sb.append(" (");
			for(int i = 0, j = references.length; i < j; i++) {
				sb.append(references[i].name);
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
			SET_NULL,NO_ACTION
		}*/
	}
	
	public static class Check extends Constraint {//XXX
		
		private String expr;
		
		public String getExpr() {
			return expr;
		}

		public void setExpr(String expr) {
			this.expr = expr;
		}
		
		@Override
		public String toSQL() {
			return "CHECK (" + expr + ")";
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
		sb.append(name).append(" (\n");
		for(int i = 0, j = columns.size(); i < j; i++) {
			sb.append("\"").append(columns.get(i).name).append(" ").append(columns.get(i).getDataType());
			if(!columns.get(i).nullable)
				sb.append(" NOT NULL");
			if(columns.get(i).defaultValue != null)
				sb.append(" DEFAULT ").append(columns.get(i).defaultValue);
			sb.append(",\n");
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
