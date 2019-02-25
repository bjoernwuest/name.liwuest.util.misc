package name.liwuest.util.types;

/** <p>Immutable pair implementation.</p>
 * 
 * @author Bjoern Wuest, Germany
 * @version 2011-12-20
 * @param <T> Type of left hand side of pair.
 * @param <U> Type of right hand side of pair.
 */
@SuppressWarnings("rawtypes") public class CPair <T, U> implements Comparable<CPair<? extends Comparable, U>> {
	/** <p>Left hand side of pair.</p> */
	protected T p_Left;
	/** <p>Right hand side of pair.</p> */
	protected U p_Right;
	
	
	/** <p>Create new instance.</p>
	 * 
	 * @param Left Left hand side of pair.
	 * @param Right Right hand side of pair.
	 */
	public CPair(T Left, U Right) {
		p_Left = Left;
		p_Right = Right;
	}
	
	
	/** <p>Get left hand side of pair.</p>
	 * 
	 * @return Left hand side of pair.
	 */
	public final T getLeft() { return p_Left; }
	
	
	/** <p>Get right hand side of pair.</p>
	 * 
	 * @return Right hand side of pair.
	 */
	public final U getRight() { return p_Right; }
	
	
	@Override public boolean equals(Object O) {
		if (O instanceof CPair) {
			CPair<?, ?> p = (CPair<?, ?>)O;
			return p_Left.equals(p.p_Left) && p_Right.equals(p.getRight());
		}
		return false;
	}
	@Override public int hashCode() { return p_Left.hashCode() | p_Right.hashCode(); }
	@SuppressWarnings("unchecked") @Override public int compareTo(CPair<? extends Comparable, U> o) { return -o.getLeft().compareTo(p_Left); }
}
