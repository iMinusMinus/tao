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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import freemarker.template.Configuration;

/**
 * ddd code generator: refer to mybatis generator, spring data, hibernate
 * 
 * @author iMinusMinus
 * @since 2017-01-16
 * @version 0.0.1
 *
 */
public final class CodeGen {
	
	private GeneratePolicy policy;
	
	public CodeGen() {
		policy = GeneratePolicy.SOURCE;
	}
	
	public static void main(String[] args) {
		if(args == null || args.length < 2) throw new RuntimeException("-d --dialect must be specified!");
		Dialect dialect = null;
		for(int i = 0, j = args.length; i < j; i += 2) {
			if("-d".equals(args[i]) || "-dialect".equals(args[i])) {
				dialect = Dialect.valueOf(args[i + 1]);
			}
		}
		SQLParser parser = new SQLParser(dialect);
		FileInputStream fis;
		try {
			fis = new FileInputStream(new File(args[args.length - 1]));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(String.format("file [%s] not exist", args[args.length - 1]));
		}
		Table table = parser.parse(fis);
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setClassForTemplateLoading(CodeGen.class, "./");
        
	}

}
