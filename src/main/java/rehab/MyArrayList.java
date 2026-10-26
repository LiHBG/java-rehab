package rehab;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 训练营 W1D1T3：手写 ArrayList。
 *
 * 约定（照 JDK 的写法来，不要偷懒用 java.util.ArrayList 代劳）：
 * - 底层 Object[] elementData，int size，int modCount
 * - 默认容量 10，但空构造器不许立刻 new Object[10]（懒加载，第一次 add 才分配）
 * - 扩容 oldCapacity + (oldCapacity >> 1)，不足则取 minCapacity
 * - 迭代器 fail-fast：next() 先比 modCount 与 expectedModCount
 *
 * 每个 TODO 都是你要写的；写之前先回 N05/N08/N09 看自己整理的链路。
 */
public class MyArrayList<E> implements Iterable<E> {

    private static final int DEFAULT_CAPACITY = 10;
    private static final Object[] EMPTY_ELEMENTDATA = {};
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    private Object[] elementData;
    private int size;
    private int modCount;

    /** 空构造器：先挂空数组，第一次 add 才给 10。 */
    public MyArrayList() {
        // TODO
    }

    /** 指定容量：立刻分配（和空构造器的区别就是这条）。 */
    public MyArrayList(int initialCapacity) {
        // TODO（initialCapacity < 0 抛 IllegalArgumentException）
    }

    public int size() {
        return size;
    }

    public boolean add(E e) {
        // TODO: 先 ensureCapacity(size + 1)，再 elementData[size++] = e
        throw new UnsupportedOperationException("TODO");
    }

    public void add(int index, E element) {
        // TODO: index 必须在 0..size，否则 IndexOutOfBoundsException
        throw new UnsupportedOperationException("TODO");
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        // TODO: 越界检查（可以自己写，也可以 Objects.checkIndex）
        throw new UnsupportedOperationException("TODO");
    }

    public E set(int index, E element) {
        throw new UnsupportedOperationException("TODO");
    }

    /** 返回被删掉的元素，后面的整体左移一格，末位槽置 null。 */
    public E remove(int index) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    public int indexOf(Object o) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    private void ensureCapacity(int minCapacity) {
        // TODO: 空数组 → 取 max(DEFAULT_CAPACITY, minCapacity)；不够就 grow
        throw new UnsupportedOperationException("TODO");
    }

    private void grow(int minCapacity) {
        // TODO: oldCapacity + (oldCapacity >> 1)；算出来还小就用 minCapacity
        throw new UnsupportedOperationException("TODO");
    }

    private class Itr implements Iterator<E> {
        int cursor;
        int lastRet = -1;
        int expectedModCount = modCount;

        @Override
        public boolean hasNext() {
            return cursor != size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next() {
            // TODO: 快照检查 -> ConcurrentModificationException；越界 -> NoSuchElementException
            throw new UnsupportedOperationException("TODO");
        }

        @Override
        public void remove() {
            // TODO: lastRet < 0 抛 IllegalStateException；删完 expectedModCount = modCount
            throw new UnsupportedOperationException("TODO");
        }
    }
}
