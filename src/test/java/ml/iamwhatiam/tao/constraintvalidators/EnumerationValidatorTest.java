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
package ml.iamwhatiam.tao.constraintvalidators;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import ml.iamwhatiam.tao.constraints.Enumeration;
import ml.iamwhatiam.tao.domain.Ter;

import org.junit.Assert;
import org.junit.Test;

/**
 * test EnumerationValidator.
 * require hibernate-validator and jboss-logging
 * 
 * @author iMinusMinus
 * @since 2016-12-05
 * @version 0.0.1
 *
 */
public class EnumerationValidatorTest {
	
	//zero argument constructor must be defined in test class

	@Enumeration(Ter.class)
	private String ter;
	
	public void setTer(String arg) {
		ter = arg;
	}
	
	@Test
	public void testEnumValidate() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		EnumerationValidatorTest $ = new EnumerationValidatorTest();
		$.setTer("ABC");
		Set<ConstraintViolation<EnumerationValidatorTest>> set = validator.validate($);
		String msg = set.iterator().next().getMessage();
		System.out.println(msg);
		Assert.assertNotNull(msg);
		EnumerationValidatorTest x = new EnumerationValidatorTest();
		x.setTer("FAIL");
		Assert.assertEquals(0, validator.validate(x).size());
	}

}
