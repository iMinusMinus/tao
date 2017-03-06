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
package ml.iamwhatiam.tao.service;

import java.util.List;

/**
 * basic service: CRUD
 * 
 * @author iMinusMinus
 * @since 2017-02-09
 * @version 0.0.1
 *
 */
public interface CrudService<T> {
	
	/**
	 * find all data, use carefully!
	 * 
	 * @return
	 */
	List<T> findAll();
	
	/**
	 * find qualified data from offset
	 * 
	 * @param form condition
	 * @param offset from
	 * @param limit at most
	 * @return
	 */
	List<T> find(T form, int offset, int limit);
	
	/**
	 * count qualified data
	 * @param form condition
	 * @return
	 */
	long count(T form);
	
	/**
	 * find one by specified id
	 * 
	 * @param id
	 * @return
	 */
	T findOne(long id);
	
	/**
	 * persist data
	 * 
	 * @param form to persist
	 * @return
	 */
	long save(T form);
	
	/**
	 * renew data
	 * 
	 * @param form to renew
	 * @return
	 */
	boolean update(T form);
	
	/**
	 * remove data from persistence
	 * 
	 * @param id
	 * @return success
	 */
	boolean remove(long id);
	
	//int delete(List<Long> ids);

}
