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
package ml.iamwhatiam.tao.util;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * principle: short and unique
 * reference:
 * <ul>
 * <li>UUID</li>
 * f81d4fae-7dec-11d0-a765-00a0c91e6bf6: 4 byte time low, "-", 2 byte time mid, "-", 2 byte time high and version, "-", 
 * 1 byte clock seq and reserved, 1 byte clock seq low, 6 byte node
 * <li>MongoDB ObjectID</li>
 * 507f1f77bcf86cd799439011: 4 byte seconds since the Unix epoch, 3 byte machine id, 2 byte process id, 3 byte counter
 * <li>twitter snowflake</li>
 * long: 41 bit million seconds, 5 bit data center id, 5 bit machine id, 12 bit sequence, 1 bit 0
 * </ul>
 * @author iMinusMinus
 * @version 0.0.1
 *
 */
public class ID {
	
	private static ID instance;
	
	private long epoch;
	
	private volatile long latest;
	
	private AtomicInteger counter;
	
	public ID(long epoch) {
		this.epoch = epoch;
		latest = this.epoch;
		counter = new AtomicInteger(0);
	}
	
	public static ID getInstance() {
		if(instance == null) {
			instance = Holder.instance;
		}
		return instance;
	}
	
	public String next() {
		if(System.currentTimeMillis() != latest) {
			counter = new AtomicInteger(0);
			latest = System.currentTimeMillis();
		} else {
			counter.incrementAndGet();
		}
		return null;//TODO
	}
	
	public Date getDateTime(ID id) {
		return null;//TODO
	}
	
	private static class Holder {
		private static ID instance = new ID(System.currentTimeMillis());
	}

}
