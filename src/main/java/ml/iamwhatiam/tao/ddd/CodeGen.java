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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private static Logger log = LoggerFactory.getLogger(CodeGen.class);
	
	private final List<String> templates = new ArrayList<String>(
			Arrays.asList("Controller.ftl", "ServiceImpl.ftl", "DAOImpl.ftl", "VO.ftl", "Domain.ftl",
					"sqlMap.ftl", "TransformationHelper.ftl", "package-info.ftl"));
	
	private final List<String> directories = new ArrayList<String>(Arrays.asList("web", "service", "dao", "vo", "domain", "mapper", "support", ""));
	
	/**
	 * generate source code or class file, or just runtime
	 */
	private GeneratePolicy policy;
	
	/**
	 * dbms dialect
	 */
	private Dialect dialect;
	
	/**
	 * templates
	 */
	private String[] tpls;
	
	/**
	 * directories template will output to
	 */
	private String[] dirs;
	
	/**
	 * generate files merged in one package
	 */
	private boolean samePackage;
	
	/**
	 * package
	 */
	private String namespace;
	
	/**
	 * SQL dataType mapping to java type
	 */
	private Map<String, String> dataTypeMapping;
	
	/**
	 * directory source file generated in 
	 */
	private String target;
	
	/**
	 * class name with fixed[tableName] prefix or not
	 */
	private boolean withClassNamePrefix;
	
	private String[] sqlFiles;
	
	private Convension convension;
	
	private Framework framework;
	
	public CodeGen() {
		policy = GeneratePolicy.SOURCE;
		tpls = new String[] {"Controller.ftl", "ServiceImpl.ftl", "DAO.ftl", "DAOImpl.ftl", "VO.ftl", "Domain.ftl", "sqlMap.ftl", "TransformationHelper.ftl", "package-info.ftl"};
		dirs = new String[] {"web", "service", "dao", "dao/impl", "vo", "domain", "mapper", "support", ""};
		target = "src/main/java/ml/iamwhatiam/tao/ddd";
		withClassNamePrefix = true;
		convension = new Convension() {
			public boolean isUseXml() {return true;}
			public boolean isUseAnnotation() {return true;}
		};
		framework = new Framework() {
			public boolean useSpringMvc() {return true;}
			public boolean useStruts() {return false;}
			public boolean useSpring() {return true;}
			public boolean useIbatis() {return true;}
			public boolean useMyBatis() {return false;}
			public boolean useHibernate() {return false;}
			public boolean useLombok() {return false;}
			public String list() {return "[SpringMVC, Spring, iBatis]";}
		};
	}
	
	public void registeDataTypeMapping() {
		if(dataTypeMapping != null)
			TransformationHelper.registeDataTypeMapping(dialect, dataTypeMapping);
	}
	
	public void generate() {
		SQLParser parser = new SQLParser(dialect);
		FileInputStream fis;
		try {
			fis = new FileInputStream(new File(sqlFiles[0]));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(String.format("file [%s] not exist", sqlFiles[0]));
		}
		Map<String, Object> root = new HashMap<String, Object>();
		Table table = parser.parse(fis);
		if(namespace == null)
			namespace = table.getCatalog() == null ? table.getSchema() : table.getCatalog();
		if(namespace == null) {
			log.warn("use radon namespace while namespace not find");
			namespace = Long.toHexString(System.currentTimeMillis());
		}
		if(dialect != Dialect.POSTGRES)
			namespace = namespace.toLowerCase();
		namespace = TransformationHelper.snake2camel(namespace);
		target = target + File.separator + namespace + File.separator;
		JavaBean bean = TransformationHelper.table2bean(table);
		root.put("table", table);
		root.put("bean", bean);
		root.put("namespace", namespace);
		root.put("date", new Date());
		root.put("config", framework.list());
		root.put("samePackage", samePackage);
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setClassForTemplateLoading(CodeGen.class, "/ml/iamwhatiam/tao/ddd");
        
        for(int i = 0; i < tpls.length; i++) {
        	String tpl = tpls[i];
        	Writer out = null;
        	String name = null;
        	try {
        		switch(policy) {
        		case SOURCE: 
        			String old = tpl.substring(0, tpl.indexOf(".") + 1);
        			if("sqlMap.".equals(old))
        				name = old + ".xml";
        			else name = old + "java";//XxxPattern or Pattern?
	        		File file = null;
	        		if(!samePackage)
	        			file = new File(target + dirs[i] + File.separator + getFileName(bean.getName(), name));
	        		else file = new File(target + getFileName(bean.getName(), name));
	        		if(!file.getParentFile().exists())
	        			file.getParentFile().mkdirs();
	        		file.createNewFile();
	        		out = new FileWriter(file);
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
	
	public static void main(String[] args) {
		if(args == null) System.out.println("Try 'java -jar tao -h' or 'java -jar tao --help' for more information");
		else if("-h".equals(args[0]) || "--help".equals(args[0])) {
			System.out.println("-d, --dialect                specify sql dialect. MYSQL, POSTGRES, ORACLE, DB2, MS_SQL, SYBASE, INFOMIX expected.");
			System.out.println("-t, --target                 which directory generated fiels will be place in. if not specified, use /ml/iamwhatiam/tao/ddd.");
			System.out.println("-n, --namespace              parent package of generated files. use table.schema or table.catalog by default.");
			System.out.println("-c, --config                 Annotation: 1, XML: 2, SpringMVC: 4, Spring: 8, iBatis: 16, Struts: 32, MyBatis: 64, Hibernate: 128, Lombok : 256");
			System.out.println("-m, --mapping                how sql data type mapping to java type. for example: TINYINT=byte;NUMBER=java.math.BigDecimal.");
			System.out.println("-s, --simple                 1: no VO only DO, 2: no Controller, 4: no Service, 8: VO, DO, Controller, Service, DAO and sqlMap in same package");
		}
		else if(args.length < 5) throw new RuntimeException("-d --dialect must be specified!");
		CodeGen cg = new CodeGen();
		Map<String, String> mapping = null;
		String tmp = null;
		int enabled = 0;
		int simplify = 0;
		for(int i = 0, j = args.length; i < j; i += 2) {
			if("-d".equals(args[i]) || "--dialect".equals(args[i])) {
				cg.setDialect(Dialect.valueOf(args[i + 1].toUpperCase()));
			}
			else if("-t".equals(args[i]) || "--target".equals(args[i])) {
				cg.setTarget(args[i + 1]);
			}
			else if("-m".equals(args[i]) || "--mapping".equals(args[i])) {
				tmp = args[i + 1];
			}
			else if("-n".equals(args[i]) || "--namespace".equals(args[i])) {
				cg.setNamespace(args[i + 1]);
			}
			else if("-c".equals(args[i]) || "--config".equals(args[i])) {
				enabled = Integer.parseInt(args[i + 1]);
				final boolean useAnnotation = (enabled & 1) != 0;
				final boolean useXml = (enabled & 2) != 0;
				Convension convension = new Convension() {
					public boolean isUseXml() {return useXml;}
					public boolean isUseAnnotation() {return useAnnotation;}
				};
				cg.setConvension(convension);
				final boolean useSpringMvc = (enabled & 4) != 0;
				final boolean useSpring = (enabled & 8) != 0;
				final boolean useIbatis = (enabled & 16) != 0;
				final boolean useStruts = (enabled & 32) != 0;
				final boolean useMyBatis = (enabled & 64) != 0;
				final boolean useHibernate = (enabled & 128) != 0; 
				final boolean useLombok = (enabled & 256) != 0;
				Framework framework = new Framework() {
					public boolean useSpringMvc() {return useSpringMvc;}
					public boolean useStruts() {return useStruts;}
					public boolean useSpring() {return useSpring;}
					public boolean useIbatis() {return useIbatis;}
					public boolean useMyBatis() {return useMyBatis;}
					public boolean useHibernate() {return useHibernate;}
					public boolean useLombok() {return useLombok;}
					public String list() {
						StringBuilder sb = new StringBuilder("[");
						if(useSpringMvc) sb.append("SpringMVC").append(",");
						if(useSpring) sb.append("Spring").append(",");
						if(useIbatis) sb.append("iBatis").append(",");
						if(useStruts) sb.append("Struts").append(",");
						if(useMyBatis) sb.append("MyBatis").append(",");
						if(useHibernate) sb.append("Hibernate").append(",");
						if(useLombok) sb.append("Lombok").append(",");
						return sb.append("]").toString();
						}
				};
				cg.setFramework(framework);
			}
			else if("-s".equals(args[i]) || "--simple".equals(args[i])) {
				simplify = Integer.parseInt(args[i + 1]);
				String[] tpls, dirs;
				if((simplify & 1) != 0) {
					int index = cg.templates.indexOf("VO.ftl");
					cg.templates.remove(index);
					cg.directories.remove(index);
				}
				if((simplify & 2) != 0) {
					int index = cg.templates.indexOf("Controller.ftl");
					cg.templates.remove(index);
					cg.directories.remove(index);
				}
				if((simplify & 4) != 0) {
					int index = cg.templates.indexOf("ServiceImpl.ftl");
					cg.templates.remove(index);
					cg.directories.remove(index);
				}
				if((simplify & 8) != 0) cg.setSamePackage(true);
				tpls = new String[cg.templates.size()];
				dirs = new String[cg.directories.size()];
				cg.templates.toArray(tpls);
				cg.directories.toArray(dirs);
				cg.setTpls(tpls);
				cg.setDirs(dirs);
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
			cg.setDataTypeMapping(mapping);
			cg.registeDataTypeMapping();
		}
		cg.setSqlFiles(args[args.length - 1].split(";"));
		cg.generate();
	}
	
	private static String getFileName(String prefix, String fileName) {
		StringBuilder sb = new StringBuilder();
		if(!fileName.equals("package-info.java"))
			sb.append(capFirst(prefix));
		sb.append(fileName);
		return sb.toString();
	}
	
	private static String capFirst(String camelCase) {
		if(camelCase == null) return null;
		char[] array = camelCase.toCharArray();
		if(array[0] >= 'a' && array[0] <= 'z')
			array[0] = Character.toUpperCase(array[0]);
		return new String(array);
	}
	
	public static interface Convension {
		
		/**
		 * prefer xml configure application
		 */
		boolean isUseXml();

		/**
		 * prefer annotation configure application
		 */
		boolean isUseAnnotation();
		
	}
	
	public static interface Framework {
		
		/**
		 * prefer Spring MVC as MVC framework
		 * @return
		 */
		boolean useSpringMvc();
		
		/**
		 * prefer Struts2 as MVC framework
		 * @return
		 */
		boolean useStruts();
		
		/**
		 * prefer Spring as DI framework
		 * @return
		 */
		boolean useSpring();
		
		/**
		 * prefer iBatis2 as ORM framework
		 * @return
		 */
		boolean useIbatis();
		
		/**
		 * prefer MyBatis3 as ORM framework
		 * @return
		 */
		boolean useMyBatis();
		
		/**
		 * prefer Hibernate as ORM framework
		 * @return
		 */
		boolean useHibernate();
		
		/**
		 * enable lombok
		 * @return
		 */
		boolean useLombok();
		
		/**
		 * list used framework as string
		 * @return
		 */
		String list();
	}

	public GeneratePolicy getPolicy() {
		return policy;
	}

	public void setPolicy(GeneratePolicy policy) {
		this.policy = policy;
	}

	public Dialect getDialect() {
		return dialect;
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	public String[] getTpls() {
		return tpls;
	}

	public void setTpls(String[] tpls) {
		this.tpls = tpls;
	}

	public String[] getDirs() {
		return dirs;
	}

	public void setDirs(String[] dirs) {
		this.dirs = dirs;
	}

	public boolean isSamePackage() {
		return samePackage;
	}

	public void setSamePackage(boolean samePackage) {
		this.samePackage = samePackage;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public Map<String, String> getDataTypeMapping() {
		return dataTypeMapping;
	}

	public void setDataTypeMapping(Map<String, String> dataTypeMapping) {
		this.dataTypeMapping = dataTypeMapping;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public boolean isWithClassNamePrefix() {
		return withClassNamePrefix;
	}

	public void setWithClassNamePrefix(boolean withClassNamePrefix) {
		this.withClassNamePrefix = withClassNamePrefix;
	}

	public String[] getSqlFiles() {
		return sqlFiles;
	}

	public void setSqlFiles(String[] sqlFiles) {
		this.sqlFiles = sqlFiles;
	}

	public Convension getConvension() {
		return convension;
	}

	public void setConvension(Convension convension) {
		this.convension = convension;
	}

	public Framework getFramework() {
		return framework;
	}

	public void setFramework(Framework framework) {
		this.framework = framework;
	}

}
