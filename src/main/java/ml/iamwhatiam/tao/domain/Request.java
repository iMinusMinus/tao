/**
 * 
 */
package ml.iamwhatiam.tao.domain;

/**
 * abstract request!
 * 
 * @author iMinusMinus
 * @since 2016-12-06
 * @version 0.0.1
 */
public class Request<T> extends Taichi {

	private static final long serialVersionUID = 507320631278103959L;
	
	private T bean;
	
	public Request build(T bean) {
		this.bean = bean;
		return this;
	}

}
