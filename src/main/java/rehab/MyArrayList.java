package rehab;

import java.util.*;

/**
 * 训练营 W1D1T3：手写 ArrayList。
 * <p>
 * 约定（照 JDK 的写法来，不要偷懒用 java.util.ArrayList 代劳）：
 * - 底层 Object[] elementData，int size，int modCount
 * - 默认容量 10，但空构造器不许立刻 new Object[10]（懒加载，第一次 add 才分配）
 * - 扩容 oldCapacity + (oldCapacity >> 1)，不足则取 minCapacity
 * - 迭代器 fail-fast：next() 先比 modCount 与 expectedModCount
 * <p>
 * 每个 TODO 都是你要写的；写之前先回 N05/N08/N09 看自己整理的链路。
 */
public class MyArrayList<E> implements Iterable<E> {

    private static final int DEFAULT_CAPACITY = 10;
    private static final Object[] EMPTY_ELEMENTDATA = {};
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    private Object[] elementData;
    private int size;
    private int modCount;

    /**
     * 空构造器：先挂空数组，第一次 add 才给 10。
     */
    public MyArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    /**
     * 指定容量：立刻分配（和空构造器的区别就是这条）。
     */
    public MyArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("非法容量：" + initialCapacity);
        }
    }

    public int size() {
        return size;
    }

    public boolean add(E e) {
        // TODO: 先 ensureCapacity(size + 1)，再 elementData[size++] = e
        modCount++;
        ensureCapacity(size + 1);
        int s = size;
        elementData[s] = e;
        size = s + 1;
        return true;
    }

    public void add(int index, E element) {
        // TODO: index 必须在 0..size，否则 IndexOutOfBoundsException
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        modCount++;
        ensureCapacity(size + 1);
        System.arraycopy(elementData, index, elementData, index + 1, size - index);
        elementData[index] = element;
        size = size + 1;
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        // TODO: 越界检查（可以自己写，也可以 Objects.checkIndex）
        Objects.checkIndex(index, size);
        return (E) elementData[index];
    }

    public E set(int index, E element) {
        Objects.checkIndex(index, size);
        E oldValue = (E) elementData[index];
        elementData[index] = element;
        return oldValue;
    }

    /**
     * 返回被删掉的元素，后面的整体左移一格，末位槽置 null。
     */
    public E remove(int index) {
        Objects.checkIndex(index, size);
        final Object[] oldEd = elementData;
        E removeData = (E) oldEd[index];
        modCount++;
        System.arraycopy(elementData, index + 1, elementData, index, size - index - 1);
        elementData[--size] = null;
        return removeData;
    }

    public int indexOf(Object o) {
        final Object[] es = elementData;
        if (o == null) {
            for (int i = 0; i < size; i++) {
                if (es[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (o.equals(es[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        Itr itr = new Itr();
        if (!itr.hasNext()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (; ; ) {
            E next = itr.next();
            sb.append(next == this ? "(this Collection)" : next);
            if (!itr.hasNext()) {
                return sb.append(']').toString();
            }
            sb.append(',').append(' ');
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity - elementData.length > 0) {
            grow(minCapacity);
        }
    }

    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;
        if (oldCapacity > 0 || elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            int newCapacity = oldCapacity + (Math.max((minCapacity - oldCapacity), oldCapacity >> 1));
            elementData = Arrays.copyOf(elementData, newCapacity);
        } else {
            elementData = new Object[Math.max(DEFAULT_CAPACITY, minCapacity)];
        }
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
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }





            throw new UnsupportedOperationException("TODO");
        }

        @Override
        public void remove() {
            // TODO: lastRet < 0 抛 IllegalStateException；删完 expectedModCount = modCount
            throw new UnsupportedOperationException("TODO");
        }
    }
}
