package rehab;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * ArrayList / LinkedList 三场景耗时对比 —— 修正版。
 *
 * <p>和 {@link Bench} 的区别只有四处，但每一处都会把数字改掉一个数量级：
 * <ol>
 *   <li>预热用同一份数据，不用副本；</li>
 *   <li>计时区间里不放 println；</li>
 *   <li>要测的取值写在循环体内，避免 JIT 把它当循环不变量提到外面；</li>
 *   <li>取多轮最小值而不是单轮值（GC 只会让数字变大，min 最接近真实）。</li>
 * </ol>
 *
 * <p>运行：{@code java -cp target/classes rehab.BenchFixed}
 */
public class BenchFixed {

    /** 防止 JIT 把整个循环当死代码删掉。 */
    static long sink = 0;

    static final int N = 100_000;
    static final int ROUNDS = 10;

    public static void main(String[] args) {
        ArrayList<Integer> arrayList = new ArrayList<>(N);
        LinkedList<Integer> linkedList = new LinkedList<>();
        for (int i = 0; i < N; i++) {
            arrayList.add(i);
            linkedList.add(i);
        }

        scenario1GetMiddle(arrayList, linkedList);
        scenario1bRandomGet(arrayList, linkedList);
        scenario2AddHead(arrayList, linkedList);
        scenario3ForEach(arrayList, linkedList);
        scenario4ArrayDeque();

        System.out.println();
        System.out.println("sink = " + sink + "（只为防止死代码消除，无实际意义）");
    }

    /** 场景一：固定取 size/2，各 10 万次。 */
    private static void scenario1GetMiddle(List<Integer> arrayList, List<Integer> linkedList) {
        // 关键：ArrayList 那边不能让 JIT 看出下标是常量。
        // get(50000) 里 50000 是编译期常量，C2 会把它提到循环外，测出 0.007 ms / 10 万次
        // = 0.07 ns/次，比一个时钟周期还短 —— 那不是快，是循环被删了。
        // 改成从一个填满 50000 的数组里取，JIT 证明不了它恒定，循环就留住了；
        // LinkedList 的 node(index) 本来也提不出去，这样两边待遇才一致。
        int[] indexes = new int[16];
        java.util.Arrays.fill(indexes, N >> 1);
        int index = N >> 1;

        for (int r = 0; r < 5; r++) {
            for (int i = 0; i < N; i++) {
                sink += arrayList.get(indexes[i & 15]);
            }
            for (int i = 0; i < N; i++) {
                sink += linkedList.get(index);
            }
        }

        long alMin = Long.MAX_VALUE;
        long llMin = Long.MAX_VALUE;
        for (int r = 0; r < ROUNDS; r++) {
            long t0 = System.nanoTime();
            for (int i = 0; i < N; i++) {
                sink += arrayList.get(indexes[i & 15]);
            }
            long t1 = System.nanoTime();
            for (int i = 0; i < N; i++) {
                sink += linkedList.get(index);
            }
            long t2 = System.nanoTime();
            alMin = Math.min(alMin, t1 - t0);
            llMin = Math.min(llMin, t2 - t1);
        }

        System.out.println("=== 场景一：get(" + index + ") × " + N + " 次 ===");
        System.out.printf("  ArrayList   %10.3f ms%n", alMin / 1e6);
        System.out.printf("  LinkedList  %10.3f ms%n", llMin / 1e6);
        System.out.printf("  LinkedList 慢 %.0f 倍%n", (double) llMin / alMin);
    }

    /** 场景一变种：真随机下标，nextInt 写在循环体内。 */
    private static void scenario1bRandomGet(List<Integer> arrayList, List<Integer> linkedList) {
        Random random = new Random(42);
        for (int r = 0; r < 3; r++) {
            for (int i = 0; i < N / 5; i++) {
                sink += arrayList.get(random.nextInt(N));
                sink += linkedList.get(random.nextInt(N));
            }
        }

        long alMin = Long.MAX_VALUE;
        long llMin = Long.MAX_VALUE;
        for (int r = 0; r < 5; r++) {
            long t0 = System.nanoTime();
            for (int i = 0; i < N; i++) {
                sink += arrayList.get(random.nextInt(N));
            }
            long t1 = System.nanoTime();
            for (int i = 0; i < N; i++) {
                sink += linkedList.get(random.nextInt(N));
            }
            long t2 = System.nanoTime();
            alMin = Math.min(alMin, t1 - t0);
            llMin = Math.min(llMin, t2 - t1);
        }

        System.out.println();
        System.out.println("=== 场景一变种：随机下标 get × " + N + " 次（nextInt 在循环内）===");
        System.out.printf("  ArrayList   %10.3f ms%n", alMin / 1e6);
        System.out.printf("  LinkedList  %10.3f ms%n", llMin / 1e6);
        System.out.printf("  LinkedList 慢 %.0f 倍%n", (double) llMin / alMin);
    }

    /** 场景二：头部插入 10 万次。 */
    private static void scenario2AddHead(List<Integer> arrayList, List<Integer> linkedList) {
        for (int r = 0; r < 3; r++) {
            for (int i = 0; i < 20_000; i++) {
                arrayList.add(0, i);
                linkedList.add(0, i);
            }
            for (int i = 0; i < 20_000; i++) {
                arrayList.remove(0);
                linkedList.remove(0);
            }
        }

        long alMin = Long.MAX_VALUE;
        long llMin = Long.MAX_VALUE;
        for (int r = 0; r < 5; r++) {
            long t0 = System.nanoTime();
            for (int i = 0; i < N; i++) {
                arrayList.add(0, i);
            }
            long t1 = System.nanoTime();
            for (int i = 0; i < N; i++) {
                linkedList.add(0, i);
            }
            long t2 = System.nanoTime();
            alMin = Math.min(alMin, t1 - t0);
            llMin = Math.min(llMin, t2 - t1);
            for (int i = 0; i < N; i++) {
                arrayList.remove(0);
                linkedList.remove(0);
            }
        }

        System.out.println();
        System.out.println("=== 场景二：头部插入 " + N + " 次 ===");
        System.out.printf("  ArrayList   %10.3f ms%n", alMin / 1e6);
        System.out.printf("  LinkedList  %10.3f ms%n", llMin / 1e6);
        System.out.printf("  LinkedList 快 %.0f 倍%n", (double) alMin / llMin);
    }

    /** 场景三：for-each 遍历。 */
    private static void scenario3ForEach(List<Integer> arrayList, List<Integer> linkedList) {
        for (int r = 0; r < 20; r++) {
            for (Integer v : arrayList) {
                sink += v;
            }
            for (Integer v : linkedList) {
                sink += v;
            }
        }

        long alMin = Long.MAX_VALUE;
        long llMin = Long.MAX_VALUE;
        for (int r = 0; r < ROUNDS; r++) {
            long t0 = System.nanoTime();
            for (Integer v : arrayList) {
                sink += v;
            }
            long t1 = System.nanoTime();
            for (Integer v : linkedList) {
                sink += v;
            }
            long t2 = System.nanoTime();
            alMin = Math.min(alMin, t1 - t0);
            llMin = Math.min(llMin, t2 - t1);
        }

        System.out.println();
        System.out.println("=== 场景三：for-each 遍历 " + N + " 个元素 ===");
        System.out.printf("  ArrayList   %10.4f ms%n", alMin / 1e6);
        System.out.printf("  LinkedList  %10.4f ms%n", llMin / 1e6);
        System.out.printf("  LinkedList 慢 %.2f 倍%n", (double) llMin / alMin);
    }

    /** 补充：当队列用（offer + poll），ArrayDeque vs LinkedList。 */
    private static void scenario4ArrayDeque() {
        java.util.ArrayDeque<Integer> deque = new java.util.ArrayDeque<>();
        LinkedList<Integer> linkedList = new LinkedList<>();

        for (int r = 0; r < 3; r++) {
            for (int i = 0; i < 200_000; i++) {
                deque.offer(i);
                linkedList.offer(i);
            }
            while (!deque.isEmpty()) {
                sink += deque.poll();
            }
            while (!linkedList.isEmpty()) {
                sink += linkedList.poll();
            }
        }

        long dqMin = Long.MAX_VALUE;
        long llMin = Long.MAX_VALUE;
        for (int r = 0; r < 7; r++) {
            long t0 = System.nanoTime();
            for (int i = 0; i < N; i++) {
                deque.offer(i);
            }
            while (!deque.isEmpty()) {
                sink += deque.poll();
            }
            long t1 = System.nanoTime();

            for (int i = 0; i < N; i++) {
                linkedList.offer(i);
            }
            while (!linkedList.isEmpty()) {
                sink += linkedList.poll();
            }
            long t2 = System.nanoTime();

            dqMin = Math.min(dqMin, t1 - t0);
            llMin = Math.min(llMin, t2 - t1);
        }

        System.out.println();
        System.out.println("=== 补充：当队列用（入队 " + N + " + 全部出队）===");
        System.out.printf("  ArrayDeque  %10.3f ms%n", dqMin / 1e6);
        System.out.printf("  LinkedList  %10.3f ms%n", llMin / 1e6);
        System.out.println("  提醒：这个数字在 10 万量级下两者很接近，别急着下结论。");
        System.out.println("        改成 100 万 + 加 -Xmx128m 重跑，LinkedList 会掉到慢 5 倍 ——");
        System.out.println("        它的代价不在单次 add/remove，而在每个元素多一个 24 字节 Node 和随之而来的 GC。");
    }
}