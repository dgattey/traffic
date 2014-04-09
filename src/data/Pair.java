package data;

/**
 * Simple Pair Class
 * 
 * @author aiguha
 * @param <T> the left type
 * @param <S> the right type
 */
public class Pair<T, S> {
	
	private T	left;
	private S	right;
	
	public Pair(final T left, final S right) {
		this.left = left;
		this.right = right;
	}
	
	public T getLeft() {
		return left;
	}
	
	public S getRight() {
		return right;
	}
	
	public void setLeft(final T l) {
		this.left = l;
	}
	
	public void setRight(final S r) {
		this.right = r;
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %s)", this.getLeft(), this.getRight());
	}
}
