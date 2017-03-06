<#import "pub.ftl" as software>
<@software.license />
 
package ml.iamwhatiam.tao.ddd.${namespace}<#if !samePackage>.dao</#if>;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.stereotype.Repository;

import ml.iamwhatiam.tao.dao.CrudDao;
<#if !samePackage>import ml.iamwhatiam.tao.ddd.${namespace}.domain.${bean.name?cap_first}Domain;</#if>


/**
  * @author iMinusMinus
  * @since ${date?string('yyyy-MM-dd')}
  * @version 0.0.1
  */
@Repository("${bean.name}Dao")  
public class ${bean.name?cap_first}DAOImpl extends SqlMapClientDaoSupport implements CrudDao<${bean.name?cap_first}Domain> {

	public List<${bean.name?cap_first}Domain> findAll() {
	    return getSqlMapClientTemplate().queryForList("ml.iamwhatiam.tao.ddd.${namespace}.mapper.${bean.name}.findSelective");
	}
	
	public List<${bean.name?cap_first}Domain> query(${bean.name?cap_first}Domain condition, int offset, int limit) {
	    return getSqlMapClientTemplate().queryForList("ml.iamwhatiam.tao.ddd.${namespace}.mapper.${bean.name}.findSelective", condition, offset, limit);
	}
	
	public long count(${bean.name?cap_first}Domain condition) {
	    return getSqlMapClientTemplate().queryForObject("ml.iamwhatiam.tao.ddd.${namespace}.mapper.${bean.name}.count", condition);
	}
	
	public ${bean.name?cap_first}Domain findOne(long id) {
	    return getSqlMapClientTemplate().queryForObject("ml.iamwhatiam.tao.ddd.${namespace}.mapper.${bean.name}.findById", id);
	}
	
	public long save(${bean.name?cap_first}Domain domain) {
	    return getSqlMapClientTemplate().insert("ml.iamwhatiam.tao.ddd.${namespace}.mapper.${bean.name}.insertSelective", domain);
	}
	
	public boolean update(${bean.name?cap_first}Domain domain) {
	    int effected = getSqlMapClientTemplate().update("ml.iamwhatiam.tao.ddd.${namespace}.mapper.${bean.name}.updateById", domain);
	    return effected > 0;
	}
	
	public boolean delete(long id) {
	    int effected = getSqlMapClientTemplate().delete("ml.iamwhatiam.tao.ddd.${namespace}.mapper.${bean.name}.deleteById", id);
	    return effected > 0;
	}
	
	<#--
	public int execute(List<${bean.name?cap_first}Domain> batch) {
	    return getSqlMapClientTemplate().execute(new SqlMapClientCallback<T>(){
	        T doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
	          //TODO
	        }
	    });
	}
	-->

} 