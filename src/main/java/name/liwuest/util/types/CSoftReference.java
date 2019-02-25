package name.liwuest.util.types;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Objects;

/** <p>Creates a {@link SoftReference} like reference to an object of given type {@code T} with the additional ability to update the referred object.</p>
 * 
 * @author Bjoern Wuest, Germany
 * @version 2012-01-03
 * @param <T> The type of referred object.
 */
public class CSoftReference<T> {
	/** <p>Interface to implement automatic object provisioning for soft references.</p>
	 * 
	 * @author Bjoern Wuest, Germany
	 * @version 2014-05-27
	 *
	 * @param <T> The type of referred object.
	 */
	public interface IInstantiator<T> {
		/** <p>Implementation shall return object referenced by this soft reference.</p>
		 * 
		 * @return The object to be referenced by this soft reference.
		 */
		public T createInstance();
	}
	
	
	/** <p>The reference to the referred object.</p> */
	private SoftReference<T> m_Ref;
	/** <p>The reference queue, if used.</p> */
	private final ReferenceQueue<? super T> m_Queue;
	/** <p>Implementation capable to create new object instance to be held with soft reference if referenced object became unavailable.</p> */
	private IInstantiator<T> m_Instantiator;
	
	
	/** <p>Creates a new soft reference that does not refer to any object. The new reference is not registered with any queue.</p>
	 * 
	 * @See {@link SoftReference#SoftReference(Object)}
	 */
	public CSoftReference() { this(null, null, null); }
	
	
	/** <p>Creates a new soft reference that does not refer to any object. The new reference is not registered with any queue.</p>
	 * 
	 * @param Instantiator Implementation to use to regain referenced object once it becomes unavailable, e.g. is garbage collected.
	 * 
	 * @See {@link SoftReference#SoftReference(Object, ReferenceQueue)}
	 */
	public CSoftReference(IInstantiator<T> Instantiator) { this(null, Instantiator, null); }
	
	
	/** <p>Creates a new soft reference that refers to the given object. The new reference is not registered with any queue.</p>
	 * 
	 * @param Obj Object the soft reference will refer to.
	 * 
	 * @See {@link SoftReference#SoftReference(Object)}
	 */
	public CSoftReference(T Obj) { this(Obj, null, null); }
	
	
	/** <p>Creates a new soft reference that refers to the given object. The new reference is not registered with any queue.</p>
	 * 
	 * @param Obj Object the soft reference will refer to.
	 * @param Instantiator Implementation to use to regain referenced object once it becomes unavailable, e.g. is garbage collected.
	 * 
	 * @See {@link SoftReference#SoftReference(Object, ReferenceQueue)}
	 */
	public CSoftReference(T Obj, IInstantiator<T> Instantiator) { this(Obj, Instantiator, null); }
	
	
	/** <p>Creates a new soft reference that refers to the given object and is registered with the given queue.</p>
	 * 
	 * @param Obj Object the soft reference will refer to.
	 * @param Queue The queue with which the reference is to be registered, or {@code null} if registration is not required.
	 * 
	 * @See {@link SoftReference#SoftReference(Object, ReferenceQueue)}
	 */
	public CSoftReference(T Obj, ReferenceQueue<? super T> Queue) { this(Obj, null, Queue); }
	
	
	/** <p>Creates a new soft reference that refers to the given object and is registered with the given queue.</p>
	 * 
	 * @param Obj Object the soft reference will refer to.
	 * @param Instantiator Implementation to use to regain referenced object once it becomes unavailable, e.g. is garbage collected.
	 * @param Queue The queue with which the reference is to be registered, or {@code null} if registration is not required.
	 * 
	 * @See {@link SoftReference#SoftReference(Object, ReferenceQueue)}
	 */
	public CSoftReference(T Obj, IInstantiator<T> Instantiator, ReferenceQueue<? super T> Queue) {
		m_Queue = Queue;
		m_Instantiator = Instantiator;
		m_Ref = new SoftReference<>(Obj, m_Queue);
	}
	
	
	/** <p>Sets new object to refer to by this reference.</p>
	 * 
	 * @param Obj The object to refer to by this reference.
	 * @return The previously referred object, or {@code null} if no object was referred.
	 */
	public synchronized T set(T Obj) {
		T result = null;
		if (null != m_Ref) { result = m_Ref.get(); }
		m_Ref = new SoftReference<>(Obj, m_Queue);
		return result;
	}
	
	
	/** <p>Returns this reference object's referent. If this reference object has been cleared, either by the program or by the garbage collector, then this method returns {@code null}.</p>
	 * 
	 * <p>If just the referenced object became unavailable, it attempts to regain it using {@link IInstantiator#createInstance()}.</p>
	 * 
	 * @return The object to which this reference refers, or {@code null} if this reference object has been cleared.
	 * 
	 * @See {@link Reference#get()}
	 */
	public synchronized T get() {
		if (null == m_Ref) { return null; }
		T result = m_Ref.get();
		// Attempt to resurrect referenced object
		if ((null == result) && (null != m_Instantiator)) {
			synchronized (m_Instantiator) { result = m_Instantiator.createInstance(); }
			m_Ref = new SoftReference<>(result, m_Queue);
		}
		return result;
	}
	
	
	/** <p>Clears this reference object. Invoking this method will not cause this object to be enqueued.</p>
	 * 
	 * <p>This method is invoked only by Java code; when the garbage collector clears references it does so directly, without invoking this method.</p>
	 * 
	 * @return This instance.
	 * 
	 * @See {@link Reference#clear()}
	 */
	public synchronized CSoftReference<T> clear() {
		if (null != m_Ref) {
			m_Ref.clear();
			m_Ref = null;
		}
		return this;
	}
	
	
	/** <p>Returns {@code true} if this reference is cleared, i.e. refers to the {@code null} object.</p>
	 * 
	 * @return {@code true} if this reference is cleared.
	 */
	public synchronized boolean isCleared() { return (null == m_Ref) || (null == m_Ref.get()); }
	
	
	/** <p>Adds this reference object to the queue with which it is registered, if any.</p>
	 * 
	 * <p>This method is invoked only by Java code; when the garbage collector enqueues references it does so directly, without invoking this method.</p>
	 * 
	 * @return {@code true} if this reference object was successfully enqueued; {@code false} if it was already enqueued or if it was not registered with a queue when it was created.
	 * 
	 * @See {@link Reference#enqueue()}
	 */
	public synchronized boolean enqueue() {
		if (null != m_Ref) { return m_Ref.enqueue(); }
		return false;
	}
	
	
	/** <p>Tells whether or not this reference object has been enqueued, either by the program or by the garbage collector. If this reference object was not registered with a queue when it was created, then this method will always return {@code false}.</p>
	 * 
	 * @return {@code true} if and only if this reference object has been enqueued.
	 * 
	 * @See {@link Reference#isEnqueued()}
	 */
	public synchronized boolean isEnqueued() {
		if (null != m_Ref) { return m_Ref.isEnqueued(); }
		return false;
	}
	
	
	@Override public synchronized boolean equals(Object Obj) {
		if (Obj instanceof CSoftReference) {
			// Object is a soft reference
			CSoftReference<?> ref = (CSoftReference<?>)Obj;
			// Are soft references equal?
			if (this == ref) { return true; }
			// Are the reference objects equal?
			if (m_Ref == ref.m_Ref) { return true; }
			// @FIXME: check if both references do reference the same type!
			// Are the referred objects equal?
			if ((null != m_Ref) && (null != ref.m_Ref)) {
				if (null == m_Ref.get()) { return null == ref.m_Ref.get(); }
				return m_Ref.get().equals(ref.m_Ref.get());
			}
		} else if (Obj instanceof Reference) {
			// Object is a Java reference
			Reference<?> ref = (Reference<?>)Obj;
			// Are references objects equal?
			if (m_Ref == ref) { return true; }
			// @FIXME: check if both references do reference the same type!
			// Are the referred objects equal?
			if (null != m_Ref) {
				if (null == m_Ref.get()) { return null == ref.get(); }
				return m_Ref.get().equals(ref.get());
			}
		// Object is a normal object that may be referenced
		} else if ((null != m_Ref) && (null != m_Ref.get())) { return m_Ref.get().equals(Obj); }
		return null == Obj;
	}
	@Override public int hashCode() { return Objects.hash(m_Queue, m_Ref); }
	@Override public synchronized String toString() {
		if (null != m_Ref) {
			if (null != m_Ref.get()) { return m_Ref.get().toString(); }
			return m_Ref.toString();
		}
		return super.toString();
	}
}
