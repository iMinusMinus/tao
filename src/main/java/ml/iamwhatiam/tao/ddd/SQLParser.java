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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * SQL parser:
 * CREATE [] table [] table_name ({column_name data_type []}[, {column_name data_type []} ...]) []
 * 
 * @see https://dev.mysql.com/doc/refman/5.6/en/create-table.html
 * @see https://www.postgresql.org/docs/9.5/static/sql-createtable.html
 * @see http://docs.oracle.com/cd/B28359_01/server.111/b28286/statements_7002.htm#SQLRF01402
 * 
 * @author iMinusMinus
 * @since 2017-01-24
 * @version 0.0.1
 *
 */
public class SQLParser {
	
	private static final char TAB = '\t';//0x09

	private static final char CARRIAGE_RETURN = '\r';//0x0D

	private static final char LINE_FEED = '\n';//0x0A
	
	private static final char SPACE = ' ';//0x20
	
	private static final char DOT = '.';
	
	private static final char COMMA = ',';

	private Logger log = LoggerFactory.getLogger(SQLParser.class);
	
	protected Table table;
	
	private Dialect dialect;
	
	private StringBuilder sb;
	
	private char quote;
	
	private int position;
	
	public SQLParser(Dialect dialect) {
		this.dialect = dialect;
		if(dialect == Dialect.MYSQL)
			quote = '`';
		else quote = '"';
	}
	
	public Table parse(InputStream is) {
		return parse(is, "UTF-8");
	}
	
	public Table parse(InputStream is, String charset) {
		table = new Table(dialect);
		BufferedReader br = null;
		sb = new StringBuilder();
		//remove comment
		analyze(is, charset, br);
		if(log.isDebugEnabled())
			log.debug(sb.toString());
		//keyword match
		match(nextWord(), "CREATE");
		parseTablePrefixInfo();
		match(nextWord(), "TABLE");
		//table name
		Stack<String> names = parseTableName();
		table.setName(names.pop());
		if(!names.empty())
			table.setSchema(names.pop());
		if(!names.empty())
			table.setCatalog(names.pop());
		//table columns and same block constraint
		parseColumns();
		parseTableSuffixInfo();
		//comment, constraint in alter clause
		parseComment();
		parseConstraint();
		if(log.isDebugEnabled())
			log.debug("accepted ddl: {}", table.toSQL());
		return table;
	}

	protected void analyze(InputStream is, String charset, BufferedReader br) {
		try {
			boolean ignore = false;
			boolean literal = false;
			boolean special = false;
			CommentType type = null;
			br = new BufferedReader(new InputStreamReader(is, charset));
			int current = -1;
			int prev = -1;
			while((current = br.read()) != -1) {
				switch(current) {
				case '/'://block comment end?
					if(prev == '*' && type == CommentType.BLOCK && !literal) {
						type = null;
						ignore = false;
						prev = '/';
						continue;
					}
					break;
				case '-'://line comment start
					if(prev == '-' && !literal && !ignore) {
						type = CommentType.LINE;
						ignore = true;
						sb.deleteCharAt(sb.length() - 1);
						continue;
					}
					break;
				case '\n':
					if(ignore && type == CommentType.LINE) {
						type = null;
						ignore = false;
						prev = SPACE;
						continue;
					}
					break;
				case '*'://block comment begin?
					if(prev == '/' && !ignore && !literal) {
						type = CommentType.BLOCK;
						ignore = true;
						sb.deleteCharAt(sb.length() - 1);
						if(sb.length() - 2 > 0)
							prev = sb.charAt(sb.length() - 2);
						else prev = -1;
						continue;
					}
					break;
				case '\\':
					if(!ignore)
						special = !special;
					break;
				case '\''://text?
					if(!ignore && !special)
						literal = !literal;
					break;
				default:
					if(prev == '/' && !ignore) {//syntax error: CR/*comment*/EATE 
						if(!isSpace((char) current))
								throw new RuntimeException("bad sql, comment should not separate keywords");
						prev = SPACE;
					}
				}
				if(isSpace((char) prev) && isSpace((char) current)) {
					prev = SPACE;
					continue;
				}	
				if(!ignore) {
					if(isSpace((char) current) && sb.length() != 0)
						current = SPACE;
					sb.append((char) current);
				}	
				prev = current;
			}
		} catch (UnsupportedEncodingException e) {
			log.error("bad charset", e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					log.warn("cannot close read buffer", e);
				}
			}
		}
	}
	
	public void error(int line, int position) {
		throw new RuntimeException(String.format("syntax error %d:%d", line, position));
	}
	
	private String nextWord() {
		int offset = position;
		for(; position < sb.length(); position++) {
			if(shouldStop(sb.charAt(position))) {
				return sb.substring(offset, position++);
			}
		}
		if(position >= sb.length())
			throw new RuntimeException("exceed string length");
		return sb.substring(offset, position);
	}
	
	private String nextWord(int position, int length) {
		return nextWord(position, length, SPACE);
	}
	
	
	private String nextWord(int position, int length, char stop) {//stop: ',', '(', ')', ' '
		int offset = position;
		boolean quoted = false;
		for(; position < sb.length() && position < length; position++) {
			if(sb.charAt(position) == '\'')
				quoted = !quoted;
			if(shouldStop(sb.charAt(position), stop) && !quoted) {
				return sb.substring(offset, position);
			}
		}
		if(position >= sb.length())
			throw new RuntimeException("exceed string length");
		return sb.substring(offset, position);
	}
	
	protected void parseTablePrefixInfo() {
		int offset = position;
		String notSure = nextWord();
		while(!"TABLE".equalsIgnoreCase(notSure)) {
			offset = position;
			notSure = nextWord();
		}
		position = offset;
	}
	
	protected Stack<String> parseTableName() {
		Stack<String> names = new Stack<String>();
		char[] identifier = nextWord().toCharArray();
		if(log.isDebugEnabled()) log.debug("table name to parse: {}", new String(identifier));
		boolean quoted = false;
		int pos = 0;
		for(int i = 0; i < identifier.length; i++) {
			if(identifier[i] == quote) {
				quoted = !quoted;
			}
			if(identifier[i] == DOT && !quoted) {
				names.push(new String(identifier, pos, i - pos));
				pos = i + 1;
				continue;
			}
			if(i == identifier.length - 1) {
				names.push(new String(identifier, pos, i - pos + 1));
			}
		}
		return names;
	}
	
	protected void parseColumns() {
		List<Table.Column> columns = new ArrayList<Table.Column>();
		table.setColumns(columns);//constraint can be same block with columns!
		int deep = 0;
		int offset = position + 1;
		for(; position < sb.length(); position++) {
			if(sb.charAt(position) == '(') {
				deep++;
				continue;
			}
			if(sb.charAt(position) == ')') {
				deep--;
				if(deep == 0) {
					String intro = nextWord(offset + 1, position);
					if("PRIMARY".equalsIgnoreCase(intro) || "INDEX".equalsIgnoreCase(intro) || "CHECK".equalsIgnoreCase(intro)
							|| "CONSTRAINT".equalsIgnoreCase(intro) || "UNIQUE".equalsIgnoreCase(intro)) {//constraint
						parseConstraint(offset + 1, position);
						offset = ++position;
						break;
					}
					Table.Column column = parseColumn(offset, position);
					offset = ++position;
					columns.add(column);
					break;
				}
				continue;
			}
			if(sb.charAt(position) == COMMA && deep == 1) {
				String intro = nextWord(offset + 1, position);
				if("PRIMARY".equalsIgnoreCase(intro) || "INDEX".equalsIgnoreCase(intro) || "CHECK".equalsIgnoreCase(intro)
						|| "CONSTRAINT".equalsIgnoreCase(intro) || "UNIQUE".equalsIgnoreCase(intro)) {//constraint
					parseConstraint(offset + 1, position);
					offset = position + 1;
					continue;
				}
				Table.Column column = parseColumn(offset, position);
				offset = position + 1;
				columns.add(column);
			}
		}
	}
	
	protected Table.Column parseColumn(int offset, int length) {
		if(log.isDebugEnabled()) log.debug("column to parse: {}", sb.substring(offset, length));
		if(sb.charAt(offset) == SPACE)
			offset += 1;
		String name = parseColumnName(offset, length);
		Table.Column.DataType dataType = parseColumnDataType(offset + name.length() + 1, length);
		Table.Column column = new Table.Column(name, dataType);
		column.setTable(table);
		Boolean nullable = null, autoIncrement = null;
		String defaultValue = null, comment = null;
		for(int start = offset + name.length() + 1; start < length; ) {
			String unknow = nextWord(start, length);
			start = start + unknow.length() + 1;
			if("DEFAULT".equalsIgnoreCase(unknow)) {
				defaultValue = nextWord(start, length);
			}
			else if("NOT".equalsIgnoreCase(unknow)) {
				String tmp = nextWord(start, length);
				start = start + tmp.length() + 1;
				if("NULL".equalsIgnoreCase(tmp))
					nullable = Boolean.FALSE;
			}
			else if("NULL".equalsIgnoreCase(unknow))
				nullable = Boolean.TRUE;
			else if("AUTO_INCREMENT".equalsIgnoreCase(unknow) && dialect == Dialect.MYSQL)
				autoIncrement = Boolean.TRUE;
			else if("COMMENT".equalsIgnoreCase(unknow))
				comment = nextWord(start, length);
				
		}
		if(nullable != null)
			column.setNullable(nullable.booleanValue());
		column.setDefaultValue(defaultValue);
		if(autoIncrement != null)
			column.setAutoIncrement(autoIncrement.booleanValue());
		column.setComment(comment);
		return column;
	}
	
	protected String parseColumnName(int offset, int length) {
		return nextWord(offset, length);
	}
	
	protected Table.Column.DataType parseColumnDataType(int offset, int length) {
		String type = nextWord(offset, length);
		boolean enumerable = false;
		int len = type.length();
		String[] names = null;
		Integer precision = null, scale = null;
		Integer intervalType = null, iPrecision = null, iScale = null;
		Boolean withTimeZone = null;
		Boolean unsigned = null, zerofill = null;
		int leftBracket = type.indexOf("(");
		int rightBracket = type.indexOf(")");
		int comma = type.indexOf(",");
		if(type.indexOf("[") > 0 && type.indexOf("]") > 0) {//hack for pg!
			int dimension = 0;
			names = new String[1];
			names[0] = type.substring(0, type.indexOf("["));
			for(int start = offset + type.indexOf("["); start < length; start += 2) {
				if(sb.charAt(start) == '[' && sb.charAt(start + 1) == ']')
					dimension++;
				else log.error("bad data type: {}", type);
			}
			type = "ARRAY";
			precision = Integer.valueOf(dimension);
		}
		if(leftBracket > 0 && ("SET".equalsIgnoreCase(type.substring(0, leftBracket))
				|| "ENUM".equalsIgnoreCase(type.substring(0, leftBracket))
				|| "ENUMERATED".equalsIgnoreCase(type.substring(0, leftBracket))))
			enumerable = true;
		if(enumerable) {//enum, set
			names = type.substring(leftBracket + 1, rightBracket).split(",");
			type = type.substring(0, leftBracket);
		} else {//character, number, datetime
			if(leftBracket > 0) {
				if(comma > 0) {
					precision = Integer.valueOf(type.substring(leftBracket + 1, comma).trim());
					scale = Integer.valueOf(type.substring(comma + 1, rightBracket).trim());
				}
				else {
					precision = Integer.valueOf(type.substring(leftBracket + 1, rightBracket).trim());
				}
				type = type.substring(0, leftBracket);
			}
		}
		type = type.toUpperCase();
		
		for(int start = offset + len + 1; start < length;) {
			String guess = nextWord(start, length);
			start = start + guess.length() + 1;
			if("LONG".equals(type)) {
				if("RAW".equalsIgnoreCase(guess))
					type = "LONG RAW";
			}
			else if("DOUBLE".equals(type)) {
				if("PRECISION".equalsIgnoreCase(guess))
					type = "DOUBLE PRECISION";
			}
			else if("CHARACTER".equals(type)) {
				if("VARING".equalsIgnoreCase(guess))
					type = "CHARACTER VARING";
				else if("LARGE".equalsIgnoreCase(guess)) {
					if(start < length) {
						guess = nextWord(start, length);
						start = start + guess.length() + 1;
					}	
					if("OBJECT".equalsIgnoreCase(guess))
						type = "CHARACTER LARGE OBJECT";
				}
			}
			else if("BIT".equals(type)) {
				if("VARING".equalsIgnoreCase(guess))
					type = "BIT VARING";
			}
			else if("BINARY".equals(type)) {
				if("VARING".equalsIgnoreCase(guess))
					type = "BINARY VARING";
				else if("LARGE".equalsIgnoreCase(guess)) {
					if(start < length) {
						guess = nextWord(start, length);
						start = start + guess.length() + 1;
					}	
					if("OBJECT".equalsIgnoreCase(guess))
						type = "BINARY LARGE OBJECT";
				}
			}
			else if("INTERVAL".equals(type)) {
				String prev = nextWord(start, length);
				int __length = prev.length();
				int lbracket = prev.indexOf("(");
				int rbracket = prev.indexOf(")");
				if(lbracket != -1 && rbracket != -1) {
					iPrecision = Integer.valueOf(prev.substring(lbracket + 1, rbracket).trim());
					prev = prev.substring(0, lbracket);
				}
				if("YEAR".equalsIgnoreCase(prev)) {
					String to = nextWord(start + __length + 1, length);
					match(to, "TO");
					String __type = nextWord(start + __length + 1 + to.length() + 1, length);
					match(__type, "MONTH");
					int lb = __type.indexOf("(");
					int rb = __type.indexOf(")");
					if(lb != -1 && rb != -1) {
						iScale = Integer.valueOf(__type.substring(lb + 1, rb));
					}
					intervalType = Integer.valueOf(0);
				}
				else if("DAY".equalsIgnoreCase(prev)) {
					String to = nextWord(start + __length + 1, length);
					match(to, "TO");
					String __type = nextWord(start + __length + 1 + to.length() + 1, length);
					int lb = __type.indexOf("(");
					int rb = __type.indexOf(")");
					if(lb != -1 && rb != -1) {
						iScale = Integer.valueOf(__type.substring(lb + 1, rb));
						__type = __type.substring(0, lb);
					}
					if("HOUR".equalsIgnoreCase(__type)) {
						intervalType = Integer.valueOf(1);
					}
					else if("MINUTE".equalsIgnoreCase(__type)) {
						intervalType = Integer.valueOf(2);
					}
					else if("SECOND".equalsIgnoreCase(__type)) {
						intervalType = Integer.valueOf(3);
					}
				}
				else if("HOUR".equalsIgnoreCase(prev)) {
					String to = nextWord(start + __length + 1, length);
					match(to, "TO");
					String __type = nextWord(start + __length + 1 + to.length() + 1, length);
					if("MINUTE".equalsIgnoreCase(__type)) {
						intervalType = Integer.valueOf(4);
					}
					else if("SECOND".equalsIgnoreCase(__type)) {
						intervalType = Integer.valueOf(5);
					}
				}
				else if("MINUTE".equalsIgnoreCase(prev)) {
					String to = nextWord(start + __length + 1, length);
					match(to, "TO");
					String __type = nextWord(start + __length + 1 + to.length() + 1, length);
					if("SECOND".equalsIgnoreCase(__type)) {
						intervalType = Integer.valueOf(5);
					}
				}
			}
			else if("TIME".equals(type) || "TIMESTAMP".equals(type)) {
				if(guess.toUpperCase().indexOf("WITH") != -1) {
					if("WITH".equalsIgnoreCase(guess))
						withTimeZone = Boolean.TRUE;
					else if("WITHOUT".equalsIgnoreCase(guess))
						withTimeZone = Boolean.FALSE;
					String time = nextWord(start + guess.length() + 1, length);
					match(time, "TIME");
					match(nextWord(start + guess.length() + 6, length), "ZONE");
				}
			}
			if("UNSIGNED".equalsIgnoreCase(guess)) {
				unsigned = true;
			}
			if("ZEROFILL".equalsIgnoreCase(guess)) {
				zerofill = true;
			}
		}
		
		Table.Column.DataType dataType = null;
		switch(dialect) {
		case MYSQL:
			Table.Column.MySQLDataType mysql = new Table.Column.MySQLDataType(type);
			if(names != null) mysql.set(names);
			if(precision != null) {
				if(scale != null) {
					if(unsigned != null && zerofill != null)
						mysql.set(precision.intValue(), scale.intValue(), unsigned.booleanValue(), zerofill.booleanValue());
					else if(unsigned != null) 
						mysql.set(precision.intValue(), scale.intValue(), unsigned.booleanValue(), false);
					else if(zerofill != null)
						mysql.set(precision.intValue(), scale.intValue(), false, zerofill.booleanValue());
					else mysql.set(precision.intValue(), scale.intValue());
				}
				else {
					if(unsigned != null && zerofill != null)
						mysql.set(precision.intValue(), unsigned.booleanValue(), zerofill.booleanValue());
					else if(unsigned != null) 
						mysql.set(precision.intValue(), unsigned.booleanValue(), false);
					else if(zerofill != null)
						mysql.set(precision.intValue(), false, zerofill.booleanValue());
					else mysql.set(precision.intValue());
				}
			}
			dataType = mysql;
			break;
		case POSTGRES:
			Table.Column.PostgresDataType pg = new Table.Column.PostgresDataType(type);
			if(names != null) pg.set(names);
			if(precision != null) {
				if(scale != null)
					pg.set(precision.intValue(), scale.intValue());
				else pg.set(precision.intValue());
			}
			if(intervalType != null) {
				if(iPrecision != null && iScale != null)
					pg.set(iPrecision.intValue(), intervalType.intValue(), iScale.intValue());
				else if(iPrecision != null)
					pg.set(iPrecision.intValue(), intervalType.intValue(), 0);
				else pg.set(intervalType.intValue());
			}
			if(withTimeZone != null) {
				if(precision != null)
					pg.set(precision.intValue(), withTimeZone.booleanValue());
				else pg.set(withTimeZone.booleanValue());
			}
			dataType = pg;
			break;
		case ORACLE:
			Table.Column.OracleDataType oracle = new Table.Column.OracleDataType(type);
			if(precision != null) {
				if(scale != null)
					oracle.set(precision.intValue(), scale.intValue());
				else oracle.set(precision.intValue());
			}
			if(intervalType != null) {
				if(iPrecision != null && iScale != null)
					oracle.set(iPrecision.intValue(), intervalType.intValue(), iScale.intValue());
				else if(iPrecision != null)
					oracle.set(iPrecision.intValue(), intervalType.intValue(), 0);
				else oracle.set(intervalType.intValue());
			}
			if(withTimeZone != null) {
				if(precision != null)
					oracle.set(precision.intValue(), withTimeZone.booleanValue());
				else oracle.set(withTimeZone.booleanValue());
			}
			dataType = oracle;
			break;
		default:
			log.info("no such dialect data type handler, use default");
			Table.Column.StandardDataType iso = new Table.Column.StandardDataType(type);
			if(precision != null) {
				if(scale != null)
					iso.set(precision.intValue(), scale.intValue());
				else iso.set(precision.intValue());
			}
			if(intervalType != null) {
				if(iPrecision != null && iScale != null)
					iso.set(iPrecision.intValue(), intervalType.intValue(), iScale.intValue());
				else if(iPrecision != null)
					iso.set(iPrecision.intValue(), intervalType.intValue(), 0);
				else iso.set(intervalType.intValue());
			}
			if(withTimeZone != null) {
				if(precision != null)
					iso.set(precision.intValue(), withTimeZone.booleanValue());
				else iso.set(withTimeZone.booleanValue());
			}
			dataType = iso;
		}
		return dataType;
	}
	
	protected void parseConstraint(int offset, int length) {
		if(log.isDebugEnabled()) log.debug("constraint to parse: {}", sb.substring(offset, length));
		int origin = position;
		position = offset;
		Table.Constraint constraint = null;
		String keyword = nextWord();
		//mysql way
		if("PRIMARY".equalsIgnoreCase(keyword)) {
			match(nextWord(), "KEY");
			constraint = table.new PrimaryKey();
		}
		else if("UNIQUE".equalsIgnoreCase(keyword)) {
			match(nextWord(), "INDEX");
			constraint = table.new UniqueKey();
		}
		else if("INDEX".equalsIgnoreCase(keyword)) {
			constraint = table.new Index();
		}
		else if("CONSTRAINT".equalsIgnoreCase(keyword)) {
			constraint = table.new ForeignKey();
		}
		else if("CHECK".equalsIgnoreCase(keyword)) {
			constraint = table.new Check();
		}
		if(sb.charAt(position) != '(') {//may not exist!
			String name = nextWord();
			constraint.setName(name);
		}
		if(constraint instanceof Table.ForeignKey) {
			match(nextWord(), "FOREIGN");
			match(nextWord(), "KEY");
		}
		if(constraint instanceof Table.Check) {
			parseCheck((Table.Check) constraint);
		}
		else {
			String[] columnNames = findColumnNames();
			Table.Column[] columns = new Table.Column[columnNames.length];
			for(int i = 0; i < columnNames.length; i++)
				columns[i] = findColumn(columnNames[i]);
			constraint.setColumns(columns);
		}
		if(constraint instanceof Table.ForeignKey) {
			parseForeignKey((Table.ForeignKey) constraint);
		}
		else if(constraint instanceof Table.Index) {
			parseIndex((Table.Index) constraint);
		}
		addConstraint(constraint);//common process
		position = origin;
	}
	
	protected void addConstraint(Table.Constraint constaint) {
		table.addConstraint(constaint);
	}
	
	protected void parseForeignKey(Table.ForeignKey fk) {
		position++;
		match(nextWord(), "REFERENCES");
		Table reference = new Table(dialect);
		Stack<String> names = parseTableName();
		reference.setName(names.pop());
		if(!names.empty())
			reference.setSchema(names.pop());
		if(!names.empty())
			reference.setCatalog(names.pop());
		String[] columnNames = findColumnNames();
		Table.Column[] columns = new Table.Column[columnNames.length];
		for(int i = 0; i < columnNames.length; i++) {
			columns[i] = new Table.Column(columnNames[i], fk.getColumns()[i].getDataType());
			columns[i].setTable(reference);
		}
		fk.setReferences(columns);
	}
	
	protected void parseIndex(Table.Index index) {
		String guess = nextWord();
		if("USING".equalsIgnoreCase(guess)) {
			index.setAlgorithm(nextWord());
		}
	}
	
	protected void parseCheck(Table.Check check) {
		int deep = 0, lb = 0, rb = 0;
		boolean quoted = false;
		for(; position < sb.length(); position++) {
			if(sb.charAt(position) == '(' && !quoted) {
				if(deep == 0) lb = position;
				deep++;
			}	
			else if(sb.charAt(position) == ')' && !quoted) {
				deep--;
				if(deep == 0) {
					rb = position;
					Table.Column[] clumns = new Table.Column[1];
					clumns[0] = findColumn(nextWord(lb + 1, rb - 1));
					check.setColumns(clumns);
					check.setSearchCondition(sb.substring(lb + 1, rb));
					break;
				}
			}
			else if(sb.charAt(position) == '\'')
				quoted = !quoted;
		}
	}
	
	/**
	 * COLLATE, COMMENT, ENGINE etc.
	 * param=value
	 */
	protected void parseTableSuffixInfo() {
		String total = nextWord(position, sb.length(), ';');
		int length = position + total.length();
		while(position < length) {
			String param = nextWord(position, length, '=');//mysql
			position = position + param.length() + 1;
			if(sb.charAt(position) == ' ') position++;
			String value = nextWord(position, sb.length());
			if("COMMENT".equalsIgnoreCase(param.trim()))
				table.setComment(value);
			position = position + value.length() + 1;

		}
		position = length + 1;
	}
	
	protected void parseComment() {
		if(position < sb.length() && sb.charAt(position) == SPACE)
			position++;
		if(position == sb.length())//maybe not exist!
			return;
		int offset = position;
		String test = nextWord();
		if(!"COMMENT".equalsIgnoreCase(test)) {
			position = offset;
			if("ALTER".equalsIgnoreCase(test) || "CREATE".equalsIgnoreCase(test))
				parseConstraint();
			else skip(offset);
			return;
		}
		match(nextWord(), "ON");
		String type = nextWord();
		if("TABLE".equalsIgnoreCase(type))
			parseTableComment();
		else if("COLUMN".equalsIgnoreCase(type))
			parseColumnComment();
		position++;
		if(position < sb.length())
			parseComment();
	}
	
	protected void parseTableComment() {
		Stack<String> names = parseTableName();
		match(Table.unquote(names.pop()), table.getName());
		match(nextWord(), "IS");
		table.setComment(nextWord());
	}
	
	protected void parseColumnComment() {
		Stack<String> names = parseTableName();
		String columnName = Table.unquote(names.pop());
		match(Table.unquote(names.pop()), table.getName());
		match(nextWord(), "IS");
		for(Table.Column column : table.getColumns()) {
			if(column.getName().equals(columnName))
				column.setComment(nextWord());
		}
	}
	
	protected void parseConstraint() {
		if(position < sb.length() && sb.charAt(position) == SPACE)
			position++;
		if(position == sb.length())//maybe not exist!
			return;
		int offset = position;
		String test = nextWord();
		if("CREATE".equalsIgnoreCase(test)) {
			parseIndex();
			position++;
		}
		else if("ALTER".equalsIgnoreCase(test)) {
			match(nextWord(), "TABLE");
			Stack<String> names = parseTableName();
			match(Table.unquote(names.pop()), table.getName());
			String tmp = nextWord();
			if(!"ADD".equalsIgnoreCase(tmp)) {
				skip(offset);
				return;
			}
			match(nextWord(), "CONSTRAINT");
			String unknow = nextWord();
			String name = null;
			Table.Constraint constraint = null;
			if(!"PRIMARY".equalsIgnoreCase(unknow) && !"UNIQUE".equalsIgnoreCase(unknow) 
					&& !"FOREIGN".equalsIgnoreCase(unknow) && !"CHECK".equalsIgnoreCase(unknow)) {
				name = unknow;
				unknow = nextWord();
			}
			if("PRIMARY".equalsIgnoreCase(unknow)) {
				match(nextWord(), "KEY");
				constraint = table.new PrimaryKey();
			}
			else if("UNIQUE".equalsIgnoreCase(unknow)) {
				if(sb.charAt(position) != '(')
					match(nextWord(), "INDEX");
				constraint = table.new UniqueKey();
			}
			else if("FOREIGN".equalsIgnoreCase(unknow)) {
				match(nextWord(), "KEY");
				constraint = table.new ForeignKey();
			}
			else if("CHECK".equalsIgnoreCase(unknow)) {
				constraint = table.new Check();
			}
			constraint.setName(name);
			if("CHECK".equalsIgnoreCase(unknow)) {
				parseCheck((Table.Check) constraint);
			} else {
				String[] columnNames = findColumnNames();
				Table.Column[] columns = new Table.Column[columnNames.length];
				for(int i = 0; i <  columnNames.length; i++) {
					columns[i] = findColumn(columnNames[i]);
				}
				constraint.setColumns(columns);
			}
			if("FOREIGN".equalsIgnoreCase(unknow)) {
				if(position < sb.length() && sb.charAt(position) == SPACE)
					position++;
				match(nextWord(), "REFERENCES");
				Table reference = new Table(dialect);
				reference.setName(parseTableName().pop());
				String[] refColumns = findColumnNames();
				Table.Column[] cols = new Table.Column[refColumns.length];
				for(int i = 0; i <  refColumns.length; i++) {
					cols[i] = new Table.Column(refColumns[i], constraint.getColumns()[i].getDataType());
					cols[i].setTable(reference);
				}
				((Table.ForeignKey) constraint).setReferences(cols);
			}
			addConstraint(constraint);
			position++;
		}
		else if("COMMENT".equalsIgnoreCase(test)){
			position = offset;
			parseComment();
			return;
		} 
		else {
			skip(offset);
		}
		if(position < sb.length())
			parseConstraint();
	}
	
	protected void parseIndex() {
		String test = nextWord();
		if("UNIQUE".equalsIgnoreCase(test))
			test = nextWord();
		if(!"INDEX".equalsIgnoreCase(test)) {//synonym
			skip();
			return;
		}
		Table.Index index = table.new Index();
		String unknow = nextWord();
		if(!"ON".equalsIgnoreCase(unknow)) {
			index.setName(unknow);
			match(nextWord(), "ON");
		}
		Stack<String> names = parseTableName();
		match(Table.unquote(names.pop()), table.getName());
		if(sb.charAt(position) != '(') {
			String guess = nextWord();
			if("USING".equalsIgnoreCase(guess))
				index.setAlgorithm(nextWord());
		}
		String[] columnNames = findColumnNames();
		Table.Column[] columns = new Table.Column[columnNames.length];
		for(int i = 0; i <  columnNames.length; i++) {
			columns[i] = findColumn(columnNames[i]);
		}
		index.setColumns(columns);
		table.addIndex(index);
	}
	
	private boolean isSpace(char c) {
		return c == SPACE || c == LINE_FEED || c == CARRIAGE_RETURN || c == TAB;
	}
	
	private boolean shouldStop(char c) {
		return shouldStop(c, SPACE);
	}
	
	private boolean shouldStop(char actual, char expect) {
		return actual == expect;
	}
	
	private void match(String actual, String expect) {
		if(!actual.equalsIgnoreCase(expect))
			throw new RuntimeException(String.format("syntax error, keyword [%s] expected, but actual is [%s]", expect, actual));
	}
	
	private String[] findColumnNames() {
		List<String> columnNames = new ArrayList<String>();
		int deep = 0, offset = position + 1;
		for(; position < sb.length(); position++) {
			if(sb.charAt(position) == '(') {
				deep++;
			}
			if(sb.charAt(position) == ')') {
				deep--;
				if(deep == 0) {
					columnNames.add(sb.substring(offset, position++).trim());
					break;
				}
			}
			if(sb.charAt(position) == ',') {
				columnNames.add(sb.substring(offset, position).trim());
				offset = position + 1;
			}
		}
		String[] result = new String[columnNames.size()];
		return columnNames.toArray(result);
	}
	
	private Table.Column findColumn(String name) {
		return findColumn(name, table);
	}
	
	private Table.Column findColumn(String name, Table table) {
		for(Table.Column column : table.getColumns()) {
			if(column.getName().equals(Table.unquote(name)) || (dialect != Dialect.POSTGRES && column.getName().equalsIgnoreCase(Table.unquote(name))))
				return column;
		}
		log.error("column [{}] not find", name);
		return null;
	}
	
	private void skip() {
		int offset = position;
		for(; position < sb.length(); position++) {
			if(sb.charAt(position) == ';') {
				position++;
				break;
			}	
		}
		skip(offset, position);
	}
	
	private void skip(int start) {
		position = start;
		skip();
	}
	
	private void skip(int start, int end) {
		if(log.isDebugEnabled())
			log.debug("skip some ddl information: {}", sb.substring(start, end));
	}
	
	private enum CommentType {
		LINE,
		BLOCK
	}

}
