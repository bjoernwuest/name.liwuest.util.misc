package name.liwuest.util.types;

/** <p>Mutable implementation of {@link CPair}.</p>
 * 
 * @author Bjoern Wuest, Germany
 * @version 2011-12-20
 * @param <T> Type of left hand side of pair.
 * @param <U> Type of right hand side of pair.
 */
public class CMutablePair <T, U> extends CPair<T, U> {
	/** <p>Create new instance.</p>
	 * 
	 * @param Left Left hand side of pair.
	 * @param Right Right hand side of pair.
	 */
	public CMutablePair(T Left, U Right) { super(Left, Right); }
	
	
	/** <p>Set new value for left hand side of pair.</p>
	 * 
	 * @param Left Left hand side of pair.
	 */
	public void setLeft(T Left) { p_Left = Left; }
	
	
	/** <p>Set new value for right hand side of pair.</p>
	 * 
	 * @param Right Right hand side of pair.
	 */
	public void setRight(U Right) { p_Right = Right; }
}
