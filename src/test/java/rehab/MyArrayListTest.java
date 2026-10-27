package rehab;

import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author:li_honBin
 * @Version:1.0.0
 * @Date:2026/10/27
 * @Description:
 */
public class MyArrayListTest {

    @Test
    void defaultCapacity() {
        MyArrayList<Integer> list = new MyArrayList<>();
        list.add(1);
        assertEquals(1, list.size());
        assertEquals(1, list.get(0));
    }

    @Test
    void capacityGrowDefault() {
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        list.add(10);
        assertEquals(11, list.size());
        assertEquals(10, list.get(10));
    }


    @Test
    void getThrow() {
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(11));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertDoesNotThrow(() -> list.get(9));
    }

    @Test
    void itrRemove1() {
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        assertThrows(ConcurrentModificationException.class, () -> {
            for (Integer i : list) {
                if (i % 2 == 0) {
                    list.remove(i);
                }
            }
        });



//        // 提前查阅资料，知道了增强for编译的时候会变成迭代器的写法
//        for (Integer next : list) {
//            if (next % 2 == 0) list.remove(next);
//        }
    }

    @Test
    void itrRemove2() {
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            if (next % 2 == 0) iterator.remove();
        }
        assertEquals("[1, 3, 5, 7, 9]", list.toString());
    }


    @Test
    void removeAfterSize() {
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        list.remove(0);
        assertEquals(9, list.size());
        assertEquals(1, list.get(0));
        assertEquals(9, list.get(8));
    }

    @Test
    void lazyCapacity() {
        MyArrayList<Integer> list = new MyArrayList<>();
        assertEquals(0,list.capacity());
        list.add(1);
        assertEquals(10,list.capacity());
    }

    @Test
    void growByCapacityFill() {
        MyArrayList<Integer> list = new MyArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        assertEquals(10,list.capacity());
        list.add(10);
        assertEquals(15,list.capacity());
        for (int i = 0; i < 5; i++) {
            list.add(11 + i);
        }
        assertEquals(22,list.capacity());
    }


    @Test
    void removeClearsLastSlot()  {
        MyArrayList<String> list = new MyArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.remove(2);
        assertEquals(2, list.size());
        assertNull(list.elementDataForTest()[2], "被删的元素必须从数组里清掉，否则 GC 回收不了");
    }

}
