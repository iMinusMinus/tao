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
package ml.iamwhatiam.tao.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * web application response bean 
 * 
 * @author iMinusMinus
 * @since 2017-03-17
 * @version 0.0.1
 *
 */
public class Form implements Serializable {
	
	private static final long serialVersionUID = -4077313696928676948L;

	/**
	 * request time
	 */
	protected Date epoch;
	
	/**
	 * response time
	 */
	protected Date elapse;
	
	/**
	 * persistence id
	 */
	protected String oid;
	
	/**
	 * alternation information
	 */
	protected String alt;

	public Date getEpoch() {
		return epoch;
	}

	public void setEpoch(Date epoch) {
		this.epoch = epoch;
	}

	public Date getElapse() {
		return elapse;
	}

	public void setElapse(Date elapse) {
		this.elapse = elapse;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		if(epoch != null)
			sb.append("\"epoch\":").append(epoch).append(",");
		if(elapse != null)
			sb.append("\"elapse\":").append(elapse).append(",");
		sb.append("\"oid\":").append("\"").append(oid).append("\"");
		if(alt != null)
			sb.append(",").append("\"alt\":\"").append(alt).append("\"");
		return sb.append("}").toString();
	}

}
