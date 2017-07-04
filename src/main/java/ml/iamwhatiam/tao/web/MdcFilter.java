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
package ml.iamwhatiam.tao.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ml.iamwhatiam.tao.util.ID;

/**
 * Application trace id.refer Google Dapper, Ali eagle eye. In Ali eagle, there are two kinds of thread local id:
 * trace id and rpc id. A successor rpc id take caller rpc id as prefix.
 * 
 * @author iMinusMinus
 * @version 0.0.1
 *
 */
public class MdcFilter implements Filter {
	
	private static Logger log = LoggerFactory.getLogger(MdcFilter.class);
	
	private final String MDC_KEY_NAME = "mdcKey";
	
	private final String MDC_KEY = "tid";
	
	private String tid;

	public void init(FilterConfig config) throws ServletException {
		tid = config.getInitParameter(MDC_KEY_NAME);
		if(tid == null) tid = MDC_KEY;
		if(log.isDebugEnabled()) log.debug("set context trace key [{}]", tid);
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		MDC.put(tid, ID.getInstance().next());
		try {
			chain.doFilter(request, response);
		} finally {
			MDC.remove(tid);
		}
		
	}
	
	public void destroy() {
		tid = null;
		if(log.isDebugEnabled()) log.debug("MDC filter destroyed");
	}

}
