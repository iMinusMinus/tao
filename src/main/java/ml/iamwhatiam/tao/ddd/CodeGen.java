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
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
	
	private String[] tpls;
	
	public CodeGen() {
		policy = GeneratePolicy.SOURCE;
		tpls = new String[] {"Controller.ftl", "Service.ftl", "DAO.ftl", "Domain.ftl", "sqlMap.ftl"};
	}
	
	public static void main(String[] args) {
		if(args == null || args.length < 2) throw new RuntimeException("-d --dialect must be specified!");
		Dialect dialect = null;
		String target = null;
		Map<String, String> mapping = null;
		String tmp = null;
		String namespace = null;
		for(int i = 0, j = args.length; i < j; i += 2) {
			if("-d".equals(args[i]) || "--dialect".equals(args[i])) {
				dialect = Dialect.valueOf(args[i + 1]);
			}
			else if("-t".equals(args[i]) || "--target".equals(args[i])) {
				target = args[i + 1];
			}
			else if("-m".equals(args[i]) || "--mapping".equals(args[i])) {
				tmp = args[i + 1];
			}
			else if("-n".equals(args[i]) || "--namespace".equals(args[i])) {
				namespace = args[i + 1];
			}
		}
		if(tmp != null) {
			String[] pairs = tmp.trim().split(";");
			mapping = new HashMap<String, String>(pairs.length);
			for(int i = 0, j = pairs.length; i < j; i++) {
				if(pairs[i] == null || pairs[i].trim().length() == 0) throw new IllegalArgumentException("mapping arguments not correct: " + tmp);
				String[] pair = pairs[i].split("=");
				if(pair.length != 2) throw new IllegalArgumentException("mapping arguments must be pair: " + pairs[i]);
				mapping.put(pair[0], pair[1]);
			}
			TransformationHelper.registeDataTypeMapping(dialect, mapping);
		}
		SQLParser parser = new SQLParser(dialect);
		FileInputStream fis;
		try {
			fis = new FileInputStream(new File(args[args.length - 1]));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(String.format("file [%s] not exist", args[args.length - 1]));
		}
		Map<String, Object> root = new HashMap<String, Object>();
		Table table = parser.parse(fis);
		if(namespace == null) {
			namespace = table.getCatalog() == null ? table.getSchema() : table.getCatalog();
			namespace = TransformationHelper.snake2camel(namespace);
		}
		JavaBean bean = TransformationHelper.table2bean(table);
		root.put("table", table);
		root.put("bean", bean);
		root.put("namespace", namespace);
		root.put("date", new Date());
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setClassForTemplateLoading(CodeGen.class, "/ml/iamwhatiam/tao/ddd");
        CodeGen cg = new CodeGen();
        for(String tpl : cg.tpls) {
        	Writer out = null;
        	String name = null;
        	try {
        		switch(cg.policy) {
        		case SOURCE: 
	        		name = tpl.substring(0, tpl.indexOf(".") + 1) + "java";
	        		out = new FileWriter(new File(target + File.separator + name));
	        		break;
        		case CLASS:
        		case RUNTIME: 	
        			name = tpl.substring(0, tpl.indexOf(".") + 1) + "java";
        			out = new StringWriter();
        			break;
        		default:	
        		}
        		cfg.getTemplate(tpl).process(root, out);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
	}

}