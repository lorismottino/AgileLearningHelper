package mottinol.alh.util;


/**
 * Agile Learning Helper
 * 
 * @version 1.1
 * @since 1.9
 * @author Loris Mottino
 *
 */

@FunctionalInterface
public interface Listener<T> {
	
	public void notify(final T value);
	
}
