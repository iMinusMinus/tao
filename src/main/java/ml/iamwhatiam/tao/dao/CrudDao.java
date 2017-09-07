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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN0 THE
 * SOFTWARE.
 */
package ml.iamwhatiam.tao.dao;

import java.util.List;

/**
 * basic dao: CRUD.
 * Mybatis scanner can take it as a mark!
 * 
 * @author iMinusMinus
 * @since 2017-02-09
 * @version 0.0.1
 *
 */
public interface CrudDao<T> {
	
	/**
	* fetch all rows
	* @return entire table data
	*/
	List<T> findAll();
	
	/**
	* fetch qualified rows in range
	* @param condition query condition
	* @param offset skip some data
	* @param limit fetch less than {@code limit}
	* @return qualified data start with {@code offset} and end with {@code offset + limit}
	*/
	List<T> query(T condition, int offset, int limit);
	
	/**
	* count qualified rows
	* @param condition query condition
	* @return how many rows can we fetch
	*/
	long count(T condition);
	
	/**
	* fetch a row by id
	* @param id table primary key
	* @return one row at most
	*/
	T findById(long id);
	
	/**
	* fetch a row by unique index or other which can be identified
	* @param domain include query condition can tell one and other apart
	* @return one row
	* @throw TooManyResultsException if more than one result
	*/
	T findOne(T domain);
	
	/**
	* persist data and return id
	* @param domain data to persist
	* @return row id
	*/
	long save(T domain);
	
	/**
	* update fields by id
	* @param domain fields to update and id to tell apart
	* @return rows affected
	*/
	int update(T domain);
	
	/**
	* delete one row by id(may be logic delete).
	* @param id row id
	* @return rows affected
	*/
	int delete(long id);
	
	//boolean delete(T domain);

}
