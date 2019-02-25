package name.liwuest.util.types;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/** <p>Extension of {@link CSoftReference} supporting timer related removal of referenced object.</p>
 * 
 * <p>To disable the timeout, set any negative value. To disable the reference, i.e. clear while setting the reference, set the timeout to 0.</p>
 * 
 * @author Bjoern Wuest, Germany
 * @version 2013-07-15
 * @param <T> The type of referred object.
 */
public class CTimedSoftReference<T> extends CSoftReference<T> {
	// FIXME: add constructors to support CSoftReference.IInstantiator
	
	/** <p>Central thread executor for scheduled task execution.</p> */
	private final static ScheduledExecutorService m_ClearingTasks = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), new ThreadFactory() { @Override public Thread newThread(Runnable r) { return new Thread(r, "CTimedSoftReference - Clearing Tasks"); } });
	/** <p>Flag to indicate that setting a new value to the soft reference resets the counter to clear the value of the soft reference.</p> */
	private final Boolean m_ResetTimeToClearOnSet;
	/** <p>The time to wait before the value of this soft reference is cleared.</p> */
	private final Long m_TimeToClear;
	/** <p>The unit of time to wait.</p> */
	private final TimeUnit m_Timeunit;
	/** <p>Scheduled task to clear the value of the soft reference.</p> */
	private ScheduledFuture<?> m_ClearingTask = null;
	
	/** <p>Creates a new soft reference that does not refer to any object. The
	 * new reference is not registered with any queue.</p>
	 * 
	 * <p>The timeout feature is disabled.</p>
	 * 
	 * @See {@link SoftReference#SoftReference(Object)}
	 */
	public CTimedSoftReference() { this(null, null); }
	
	/** <p>Creates a new soft reference that does not refer to any object. The
	 * new reference is not registered with any queue.</p>
	 * 
	 * @param Timeout The time to wait before the object referenced by this soft reference will be cleared, i.e. before {@link #clear()} is invoked.
	 * @param Timeunit The unit of time given in parameter {@code Timeout}.
	 * @param ResetOnWrite Set to {@code true} to reset the timeout.
	 * 
	 * @See {@link SoftReference#SoftReference(Object)}
	 */
	public CTimedSoftReference(Long Timeout, TimeUnit Timeunit, Boolean ResetOnWrite) { this(null, null, null, Timeout, Timeunit, ResetOnWrite); }
	
	/** <p>Creates a new soft reference that refers to the given object. The new
	 * reference is not registered with any queue.</p>
	 * 
	 * <p>The timeout feature is disabled.</p>
	 * 
	 * @param Obj Object the soft reference will refer to.
	 * 
	 * @See {@link SoftReference#SoftReference(Object)}
	 */
	public CTimedSoftReference(T Obj) { this(Obj, null, null, -1l, TimeUnit.NANOSECONDS, false); }
	
	/** <p>Creates a new soft reference that refers to the given object. The new
	 * reference is not registered with any queue.</p>
	 * 
	 * @param Obj Object the soft reference will refer to.
	 * @param Timeout The time to wait before the object referenced by this soft reference will be cleared, i.e. before {@link #clear()} is invoked.
	 * @param Timeunit The unit of time given in parameter {@code Timeout}.
	 * @param ResetOnWrite Set to {@code true} to reset the timeout.
	 * 
	 * @See {@link SoftReference#SoftReference(Object)}
	 */
	public CTimedSoftReference(T Obj, Long Timeout, TimeUnit Timeunit, Boolean ResetOnWrite) { this(Obj, null, null, Timeout, Timeunit, ResetOnWrite); }
	
	/** <p>Creates a new soft reference that refers to the given object and is registered with the given queue.</p>
	 * 
	 * @param Obj Object the soft reference will refer to.
	 * @param Queue The queue with which the reference is to be registered, or {@code null} if registration is not required.
	 * @param Timeout The time to wait before the object referenced by this soft reference will be cleared, i.e. before {@link #clear()} is invoked.
	 * @param Timeunit The unit of time given in parameter {@code Timeout}.
	 * @param ResetOnWrite Set to {@code true} to reset the timeout.
	 * 
	 * @See {@link SoftReference#SoftReference(Object, ReferenceQueue)}
	 */
	public CTimedSoftReference(T Obj, ReferenceQueue<? super T> Queue, Long Timeout, TimeUnit Timeunit, Boolean ResetOnWrite) { this(Obj, null, Queue, Timeout, Timeunit, ResetOnWrite); }
	
	/** <p>Creates a new soft reference that does not refer to any object. The
	 * new reference is not registered with any queue.</p>
	 * 
	 * <p>The timeout feature is disabled.</p>
	 * 
	 * @param Instantiator Implementation to use to regain referenced object once it becomes unavailable, e.g. is garbage collected.
	 * 
	 * @See {@link SoftReference#SoftReference(Object)}
	 */
	public CTimedSoftReference(CSoftReference.IInstantiator<T> Instantiator) { this(null, Instantiator); }
	
	/** <p>Creates a new soft reference that does not refer to any object. The
	 * new reference is not registered with any queue.</p>
	 * 
	 * @param Instantiator Implementation to use to regain referenced object once it becomes unavailable, e.g. is garbage collected.
	 * @param Timeout The time to wait before the object referenced by this soft reference will be cleared, i.e. before {@link #clear()} is invoked.
	 * @param Timeunit The unit of time given in parameter {@code Timeout}.
	 * @param ResetOnWrite Set to {@code true} to reset the timeout.
	 * 
	 * @See {@link SoftReference#SoftReference(Object)}
	 */
	public CTimedSoftReference(CSoftReference.IInstantiator<T> Instantiator, Long Timeout, TimeUnit Timeunit, Boolean ResetOnWrite) { this(null, Instantiator, Timeout, Timeunit, ResetOnWrite); }
	
	/** <p>Creates a new soft reference that refers to the given object. The new
	 * reference is not registered with any queue.</p>
	 * 
	 * <p>The timeout feature is disabled.</p>
	 * 
	 * @param Obj Object the soft reference will refer to.
	 * @param Instantiator Implementation to use to regain referenced object once it becomes unavailable, e.g. is garbage collected.
	 * 
	 * @See {@link SoftReference#SoftReference(Object)}
	 */
	public CTimedSoftReference(T Obj, CSoftReference.IInstantiator<T> Instantiator) { this(Obj, Instantiator, null, -1l, TimeUnit.NANOSECONDS, false); }
	
	/** <p>Creates a new soft reference that refers to the given object. The new
	 * reference is not registered with any queue.</p>
	 * 
	 * @param Obj Object the soft reference will refer to.
	 * @param Instantiator Implementation to use to regain referenced object once it becomes unavailable, e.g. is garbage collected.
	 * @param Timeout The time to wait before the object referenced by this soft reference will be cleared, i.e. before {@link #clear()} is invoked.
	 * @param Timeunit The unit of time given in parameter {@code Timeout}.
	 * @param ResetOnWrite Set to {@code true} to reset the timeout.
	 * 
	 * @See {@link SoftReference#SoftReference(Object)}
	 */
	public CTimedSoftReference(T Obj, CSoftReference.IInstantiator<T> Instantiator, Long Timeout, TimeUnit Timeunit, Boolean ResetOnWrite) { this(Obj, Instantiator, null, Timeout, Timeunit, ResetOnWrite); }
	
	/** <p>Creates a new soft reference that refers to the given object and is registered with the given queue.</p>
	 * 
	 * @param Obj Object the soft reference will refer to.
	 * @param Instantiator Implementation to use to regain referenced object once it becomes unavailable, e.g. is garbage collected.
	 * @param Queue The queue with which the reference is to be registered, or {@code null} if registration is not required.
	 * @param Timeout The time to wait before the object referenced by this soft reference will be cleared, i.e. before {@link #clear()} is invoked.
	 * @param Timeunit The unit of time given in parameter {@code Timeout}.
	 * @param ResetOnWrite Set to {@code true} to reset the timeout.
	 * 
	 * @See {@link SoftReference#SoftReference(Object, ReferenceQueue)}
	 */
	public CTimedSoftReference(T Obj, CSoftReference.IInstantiator<T> Instantiator, ReferenceQueue<? super T> Queue, Long Timeout, TimeUnit Timeunit, Boolean ResetOnWrite) {
		super(Obj, Instantiator, Queue);
		m_ResetTimeToClearOnSet = ResetOnWrite;
		m_TimeToClear = Timeout;
		m_Timeunit = Timeunit;
		if (null != Obj) {
			final CTimedSoftReference<T> _this = this;
			if (0 < m_TimeToClear) { m_ClearingTask = m_ClearingTasks.schedule(new Runnable() { @Override public void run() { _this.clear(); } }, m_TimeToClear, m_Timeunit); }
			else if (0 == m_TimeToClear) { _this.clear(); }
		}
	}
	
	
	@Override public synchronized T set(T Obj) {
		if (m_ResetTimeToClearOnSet && (null != m_ClearingTask)) {
			m_ClearingTask.cancel(false);
			while (!m_ClearingTask.isDone()) { try { Thread.sleep(1); } catch (InterruptedException Ignore) { /* ignore */ } }
		}
		if ((null != m_ClearingTask) && m_ClearingTask.isDone()) { m_ClearingTask = null; }
		T result = null;
		if (0 != m_TimeToClear) {
			result = super.set(Obj);
			if ((0 < m_TimeToClear) && (null == m_ClearingTask)) {
				final CTimedSoftReference<T> _this = this;
				m_ClearingTask = m_ClearingTasks.schedule(new Runnable() { @Override public void run() { _this.clear(); } }, m_TimeToClear, m_Timeunit);
			}
		}
		return result;
	}
}
