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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Test;


/**
 * parser test
 * 
 * @author iMinusMinus
 * @since 2017-03-01
 * @version 0.0.1
 *
 */
public class SQLParserTest {
	
	/**
	 * Traditional finance company use Oracle, like PingAn
	 */
	@Test
	public void testTraditionalStyle() {
		SQLParser parser = new SQLParser(Dialect.ORACLE);
		try {
			Table table = parser.parse(new FileInputStream(new File("src/test/resources/oracle.sql")));
			Assert.assertEquals("EMP", table.getName());
			Assert.assertEquals(8, table.getColumns().size());
			Assert.assertFalse(table.getColumns().get(0).isNullable());
			Assert.assertEquals(10, ((Table.Column.OracleDataType) table.getColumns().get(1).getDataType()).get());
			Assert.assertEquals("deptno", table.getColumns().get(7).getName());
			Assert.assertNotNull(table.getFks());
		} catch (FileNotFoundException e) {
			Assert.fail("sql not found");
		}
	}
	
	/**
	 * Internet and e-business prefer MySQL, like Alibaba and ZhongAn
	 */
	@Test
	public void testInternetStyle() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE `sakila`.`film` (").append("\n");
		sb.append("	`film_id` SMALLINT(5) UNSIGNED NOT NULL AUTO_INCREMENT,").append("\n");
		sb.append("	`title` VARCHAR(255) NOT NULL,").append("\n");
		sb.append("	`description` TEXT NULL,").append("\n");
		sb.append("	`release_year` YEAR NULL DEFAULT NULL,").append("\n");
		sb.append("	`language_id` TINYINT(3) UNSIGNED NOT NULL,").append("\n");
		sb.append("	`original_language_id` TINYINT(3) UNSIGNED NULL DEFAULT NULL,").append("\n");
		sb.append("	`rental_duration` TINYINT(3) UNSIGNED NOT NULL DEFAULT '3',").append("\n");
		sb.append("	`rental_rate` DECIMAL(4,2) NOT NULL DEFAULT '4.99',").append("\n");
		sb.append("	`length` SMALLINT(5) UNSIGNED NULL DEFAULT NULL,").append("\n");
		sb.append("	`replacement_cost` DECIMAL(5,2) NOT NULL DEFAULT '19.99',").append("\n");
		sb.append("	`rating` ENUM('G','PG','PG-13','R','NC-17') NULL DEFAULT 'G',").append("\n");
		sb.append("	`special_features` SET('Trailers','Commentaries','Deleted Scenes','Behind the Scenes') NULL DEFAULT NULL,").append("\n");
		sb.append("	`last_update` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,").append("\n");
		sb.append("	PRIMARY KEY (`film_id`),").append("\n");
		sb.append("	INDEX `idx_title` (`title`),").append("\n");
		sb.append("	INDEX `idx_fk_language_id` (`language_id`),").append("\n");
		sb.append("	INDEX `idx_fk_original_language_id` (`original_language_id`),").append("\n");
		sb.append("	CONSTRAINT `fk_film_language` FOREIGN KEY (`language_id`) REFERENCES `language` (`language_id`) ON UPDATE CASCADE,").append("\n");
		sb.append("	CONSTRAINT `fk_film_language_original` FOREIGN KEY (`original_language_id`) REFERENCES `language` (`language_id`) ON UPDATE CASCADE").append("\n");
		sb.append(")").append("\n");
		sb.append("COLLATE='utf8_general_ci'").append("\n");
		sb.append("ENGINE = InnoDB").append("\n");
		sb.append("AUTO_INCREMENT=1001").append("\n");
		sb.append(";").append("\n");
		SQLParser parser = new SQLParser(Dialect.MYSQL);
		Table table = parser.parse(new ByteArrayInputStream(sb.toString().getBytes()));
		Assert.assertEquals("sakila", table.getSchema());
		Assert.assertEquals("film", table.getName());
		Assert.assertEquals(13, table.getColumns().size());
		Assert.assertEquals("SMALLINT", ((Table.Column.MySQLDataType) table.getColumns().get(0).getDataType()).getDataType());
		Assert.assertEquals(255, ((Table.Column.MySQLDataType) table.getColumns().get(1).getDataType()).get());
		Assert.assertNotNull(table.getPk());
		Assert.assertEquals("fk_film_language_original", table.getFks().get(1).getName());
		Assert.assertNull(table.getComment());
	}
	
	/**
	 * pg becomes more and more popular!
	 */
	@Test
	public void testNewStyle() {
		StringBuilder sb = new StringBuilder();
		sb.append("-- Table: pg_catalog.pg_enum").append("\n");
		sb.append("").append("\n");
		sb.append("-- DROP TABLE pg_catalog.pg_enum;").append("\n");
		sb.append("").append("\n");
		sb.append("CREATE TABLE pg_catalog.pg_enum").append("\n");
		sb.append("(").append("\n");
		sb.append("    enumtypid oid NOT NULL,").append("\n");
		sb.append("    enumsortorder real NOT NULL,").append("\n");
		sb.append("    enumlabel text COLLATE pg_catalog.\"default\" NOT NULL").append("\n");//origin data type [name]
		sb.append(")").append("\n");
		sb.append("WITH (").append("\n");
		sb.append("    OIDS = TRUE").append("\n");
		sb.append(")").append("\n");
		sb.append("TABLESPACE pg_default;").append("\n");
		sb.append("").append("\n");
		sb.append("ALTER TABLE pg_catalog.pg_enum").append("\n");
		sb.append("    OWNER to postgres;").append("\n");
		sb.append("").append("\n");
		sb.append("GRANT ALL ON TABLE pg_catalog.pg_enum TO postgres;").append("\n");
		sb.append("").append("\n");
		sb.append("GRANT SELECT ON TABLE pg_catalog.pg_enum TO PUBLIC;").append("\n");
		sb.append("").append("\n");
		sb.append("-- Index: pg_enum_oid_index").append("\n");
		sb.append("").append("\n");
		sb.append("-- DROP INDEX pg_catalog.pg_enum_oid_index;").append("\n");
		sb.append("").append("\n");
		sb.append("CREATE UNIQUE INDEX pg_enum_oid_index").append("\n");
		sb.append("    ON pg_catalog.pg_enum USING btree").append("\n");
		sb.append("    (enumtypid)").append("\n");
		sb.append("    TABLESPACE pg_default;").append("\n");
		sb.append("").append("\n");
		sb.append("-- Index: pg_enum_typid_label_index").append("\n");
		sb.append("").append("\n");
		sb.append("-- DROP INDEX pg_catalog.pg_enum_typid_label_index;").append("\n");
		sb.append("").append("\n");
		sb.append("CREATE UNIQUE INDEX pg_enum_typid_label_index").append("\n");
		sb.append("    ON pg_catalog.pg_enum USING btree").append("\n");
		sb.append("    (enumtypid, enumlabel)").append("\n");
		sb.append("    TABLESPACE pg_default;").append("\n");
		sb.append("").append("\n");
		sb.append("-- Index: pg_enum_typid_sortorder_index").append("\n");
		sb.append("").append("\n");
		sb.append("-- DROP INDEX pg_catalog.pg_enum_typid_sortorder_index;").append("\n");
		sb.append("").append("\n");
		sb.append("CREATE UNIQUE INDEX pg_enum_typid_sortorder_index").append("\n");
		sb.append("    ON pg_catalog.pg_enum USING btree").append("\n");
		sb.append("    (enumtypid, enumsortorder)").append("\n");
		sb.append("    TABLESPACE pg_default;").append("\n");
		SQLParser parser = new SQLParser(Dialect.POSTGRES);
		Table table = parser.parse(new ByteArrayInputStream(sb.toString().getBytes()));
		Assert.assertEquals("pg_catalog", table.getSchema());
		Assert.assertEquals("pg_enum", table.getName());
		Assert.assertEquals(3, table.getColumns().size());
		Assert.assertFalse(table.getColumns().get(0).isNullable());
		Assert.assertEquals(3, table.getIndexes().size());
	}
	
	@Test
	public void testEnum() {
		Table.Column.OracleDataType type1 = new Table.Column.OracleDataType("NUMBER");
		type1.set(7, 3);
		Table.Column.OracleDataType type2 = new Table.Column.OracleDataType("NUMBER");
		type2.set(10, 4);
		Assert.assertNotEquals(type1.getPrecision(), type2.getPrecision());
		Assert.assertNotEquals(type1.getScale(), type2.getScale());
		Dialect orcl = Dialect.ORACLE;
		orcl.setVersion(11, 0, 2);
		Dialect orc = Dialect.ORACLE;
		orc.setVersion(10, 1, 2);
		Assert.assertEquals(orcl.getMajor(), orc.getMajor());
	}

}
