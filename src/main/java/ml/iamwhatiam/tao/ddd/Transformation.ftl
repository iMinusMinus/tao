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
package ml.iamwhatiam.tao.ddd.${namespace};

import ml.iamwhatiam.tao.ddd.${namespace}.domain.${bean.name}Domain;
import ml.iamwhatiam.tao.ddd.${namespace}.vo.${bean.name}VO;
 
/**
 * transformation between Domain and VO
 * 
 * @author iMinusMinus
 * @since 2017-02-09
 * @version 0.0.1
 *
 */
public class TransformationHelper {

    private TransformationHelper() {}

    public static ${bean.name}Domain vo2domain(${bean.name}VO vo) {
        ${bean.name}Domain domain = new ${bean.name}Domain();
        <#list bean.properties as property>
        domain.set${property.name?upper_first}(vo.get${property.name?upper_first}());
        </#list>
        return domain;
    }
    
    public static ${bean.name}VO domain2vo(${bean.name}Domain domain) {
        ${bean.name}VO vo = new ${bean.name}VO();
        <#list bean.properties as property>
        vo.set${property.name?upper_first}(domain.get${property.name?upper_first}());
        </#list>
        return vo;
    }

} 