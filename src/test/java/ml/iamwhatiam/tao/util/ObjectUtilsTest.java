package ml.iamwhatiam.tao.util;

import org.junit.Assert;
import org.junit.Test;

public class ObjectUtilsTest {
	
	@Test
	public void testEquals() {
		Object a = new Object();
		Assert.assertEquals(a, a);
		Object b = new Object();
		if(a.equals(b)) {
			Assert.assertEquals(b, a);
			Assert.assertEquals(a.hashCode(), b.hashCode());
		}
		Object c = new Object();
		if(a.equals(b) && b.equals(c)) {
			Assert.assertEquals(a, c);
			Assert.assertEquals(a.hashCode(), c.hashCode());
		}
		Object d = new Object();
		boolean result = a.equals(d);
		int i = 0;
		while(i < 100) {
			Assert.assertTrue(a.equals(d) == result);
			i++;
		}
		if(a != null)
			Assert.assertFalse(a.equals(null));
	}

}
