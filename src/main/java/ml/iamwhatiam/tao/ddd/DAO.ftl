<#import "pub.ftl" as software>
<@software.license />
 
package ml.iamwhatiam.tao.ddd.${namespace}<#if !samePackage>.dao</#if>;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.stereotype.Repository;

<#if !samePackage>import ml.iamwhatiam.tao.ddd.${namespace}.domain.${bean.name}Domain;</#if>


/**
  * @author iMinusMinus
  * @since ${date?string('yyyy-MM-dd')}
  * @version 0.0.1
  */
@Repository("${bean.name}Dao")  
public class ${bean.name}ServiceImpl<T> extends SqlMapClientDaoSupport implements CrudDao<T> {

	public List<T> findAll() {
	    return getSqlMapClientTemplate().queryForList("ml.iamwhatiam.tao.ddd.${namespace}.mapper.${bean.name}.findSelective");
	}
	
	public List<T> query(T condition, int offset, int limit) {
	    return getSqlMapClientTemplate().queryForList("ml.iamwhatiam.tao.ddd.${namespace}.mapper.${bean.name}.findSelective", condition, offset, limit);
	}
	
	public long count(T condition) {
	    return getSqlMapClientTemplate().queryForObject("ml.iamwhatiam.tao.ddd.${namespace}.mapper.${bean.name}.count", condition);
	}
	
	public T findOne(long id) {
	    return getSqlMapClientTemplate().queryForObject("ml.iamwhatiam.tao.ddd.${namespace}.mapper.${bean.name}.findById", id);
	}
	
	public long save(T domain) {
	    return getSqlMapClientTemplate().insert("ml.iamwhatiam.tao.ddd.${namespace}.mapper.${bean.name}.insertSelective", domain);
	}
	
	public boolean update(T domain) {
	    int effected = getSqlMapClientTemplate().update("ml.iamwhatiam.tao.ddd.${namespace}.mapper.${bean.name}.updateById", domain);
	    return effected > 0;
	}
	
	public boolean delete(long id) {
	    int effected = getSqlMapClientTemplate().delete("ml.iamwhatiam.tao.ddd.${namespace}.mapper.${bean.name}.deleteById", id);
	    return effected > 0;
	}
	
	<#--
	public int execute(List<T> batch) {
	    return getSqlMapClientTemplate().execute(new SqlMapClientCallback<T>(){
	        T doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
	          //TODO
	        }
	    });
	}
	-->

} 