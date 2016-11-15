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
package ml.iamwhatiam.tao.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @author iMinusMinus
 * @version 0.0.1
 *
 */
//@MappedSuperclass
public abstract class Taichi implements Serializable {

	private static final long serialVersionUID = 5124015363954822808L;

	/**
	 * object identifier
	 */
	//@Id
	//@GeneratedValue
	protected long oid;
	
	/**
	 * founder
	 */
	//@NotNull
	protected String createdBy;
	
	/**
	 * create time
	 */
	//@NotNull
	protected Date createdDate;
	
	/**
	 * last reviser
	 */
	protected String lastModifiedBy;
	
	/**
	 * last revised time
	 */
	protected Date lastModifiedDate;
}
