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

import java.io.InputStream;

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
	
	private Dialect dialect;
	
	public SQLParser(Dialect dialect) {
		this.dialect = dialect;
	}
	
	public Table parse(InputStream is) {
		return parse(is, "UTF-8");
	}
	
	public Table parse(InputStream is, String charset) {
		Table table = new Table(dialect);
		//TODO
		return table;
	}

}
