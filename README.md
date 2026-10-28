# java-rehab

Java + AI Agent 双栈 · 8 周康复训练仓库。

每天一个主题，走同一条链路：**读源码 → 手写一遍 → 跑实测 → 写结论**。
8 周后这个仓库不是简历上的一行字，是一条能点开看的证据链。

## 进度

| 周 | 主题 | 状态 |
|---|---|---|
| W1D1 | 集合框架总览 + ArrayList / LinkedList 源码 | ✅ |
| W1D2 | HashMap 源码深挖：扰动、扩容、树化与线程不安全 | ⬜ |

## 目录

```
.
├── pom.xml                              Maven 配置（Java 21 + JUnit 5.11.4）
├── run-tests.ps1                        跑测试的脚本（自己设 JAVA_HOME）
├── docs/
│   └── bench-arraylist-vs-linkedlist.md W1D1 实测报告 + 踩坑记录
└── src/
    ├── main/java/rehab/
    │   ├── MyArrayList.java             手写 ArrayList：懒加载 + 1.5 倍扩容 + fail-fast
    │   └── Bench.java                   ArrayList / LinkedList 三场景耗时对比
    └── test/java/rehab/
        ├── MyArrayListTest.java         9 个测试
        └── ToolchainSmokeTest.java      环境自检
```

## 跑起来

```powershell
.\run-tests.ps1 test
```

`run-tests.ps1` 会自己设好 `JAVA_HOME`。PATH 上的 `java` 是 Java 8，
直接 `mvn test` 会报 `invalid target release: 21`。

跑 Bench（`main` 里一次只开一个场景，注释切换）：

```powershell
$env:JAVA_HOME = 'D:\develop\JDK\jdk-21'
java -cp target/classes rehab.Bench
```

## W1D1：手写 MyArrayList

`src/main/java/rehab/MyArrayList.java` 对照 JDK 21 的 `java.util.ArrayList` 逐条实现：

| 机制 | JDK 21 原文 | 我的实现 |
|---|---|---|
| 懒加载 | 空构造器指向 `DEFAULTCAPACITY_EMPTY_ELEMENTDATA`，第一次 add 才分配 10 | `ensureCapacity` 里判哨兵 |
| 1.5 倍扩容 | `ArraysSupport.newLength(oldCap, minGrowth, oldCap >> 1)` | `oldCap + max(minGrowth, oldCap >> 1)` |
| 扩容拷贝 | `Arrays.copyOf`，O(n) 隐性成本 | 同 |
| 元素搬移 | `System.arraycopy`（同数组重叠安全，memmove 语义） | 同 |
| fail-fast | `modCount` vs `expectedModCount` | 内部类 `Itr` |

9 个测试覆盖：懒加载容量、第 11 个元素后容量 15、越界异常、迭代中 `list.remove` 抛 CME、
`iterator.remove` 不抛、`remove` 后左移与 size、被删槽位置 null（GC 可见）。

## W1D1：ArrayList vs LinkedList 实测

10 万元素，JDK 21.0.7 / Windows / `System.nanoTime()` / 每项取 5~10 轮最小值。
完整代码：[`BenchFixed.java`](src/main/java/rehab/BenchFixed.java)；
原始输出与踩坑过程：[`docs/bench-arraylist-vs-linkedlist.md`](docs/bench-arraylist-vs-linkedlist.md)。

| 场景 | ArrayList | LinkedList | 差距 |
|---|---|---|---|
| `get(size/2)` × 10 万次 | 0.494 ms | 16838 ms | LinkedList 慢 **34099×** |
| 真随机下标 `get` × 10 万次 | 5.67 ms | 10344 ms | LinkedList 慢 **1824×** |
| 头部插入 10 万次 | 4506 ms | 2.084 ms | LinkedList 快 **2162×** |
| for-each 遍历 | 1.4423 ms | 1.6743 ms | LinkedList 慢 1.16× |

### 三条结论

**1. 按 index 访问，LinkedList 是 O(n)，而且是从两端往中间找的 O(n/2)**

`node(int)` 先比 `index < (size >> 1)`，决定从 `first` 往后走还是从 `last` 往前走。
`get(size/2)` 正好是最坏情况。实测单次 `get` 的平均耗时：

| 下标 | 0 | 1000 | 25000 | 50000 | 75000 | 99000 | 99999 |
|---|---|---|---|---|---|---|---|
| ns | 12.5 | 2541 | 73891 | **162427** | 79232 | 3490 | 8.6 |

对称的两端、中间最贵，和 `node(int)` 里的折半完全对上。

**2. 「LinkedList 增删快」只在已经拿到节点引用时成立**

`add(int, E)` 自己也要先 `node(index)` 定位，所以按下标插入依然是 O(n)。
真正 O(1) 的只有 `addFirst` / `addLast` / 拿到 `ListIterator` 之后的增删。

**3. 栈和队列用 ArrayDeque —— 但它赢的不是「快」，是「省」**

内存充裕时 LinkedList 的单次 offer/poll 反而更快（10 万量级快 1.5 倍）。
把堆压到 `-Xmx128m` 再跑 100 万入队出队，胜负立刻反转：

| 条件 | ArrayDeque | LinkedList |
|---|---|---|
| 100 万，`-Xmx4g` | 18.50 ms | **11.88 ms** |
| 100 万，`-Xmx128m` | **18.93 ms** | 100.42 ms |

每元素净增堆占用实测：ArrayList 4.00 B / ArrayDeque 4.00 B / **LinkedList 24.63 B**。
LinkedList 每个元素一个 24 字节 Node，堆一紧 GC 就开始收租。
所以正确的说法是：**它在同样内存里能装 6 倍数据，不用为每个元素付对象头。**

## 踩过的坑

写 benchmark 时踩到四个坑，都不是「知道就行」而是「不踩不知道」的：

1. **预热和计时必须是同一份数据。** 预热用副本、计时用原表，等于没预热。
2. **`println` 不能放在计时区间里。** 那行「开始时间」在预热后第一次执行要 **16.5 ms**，
   第二次起 0.26 ms。场景一原本 19 ms 里有 16.5 ms 是它。
3. **固定下标会被 JIT 提到循环外。** 20 轮取最小值：固定 `get(50000)` 0.0365 ns/次，
   变化下标 2.136 ns/次，差 **58.5×**；关掉 C2（`-XX:TieredStopAtLevel=1`）只差 1.8×。
   0.0365 ns 小于一个时钟周期，只可能是循环体被整个消掉了。
4. **一个随机下标 ≠ 随机访问。** `int index = r.nextInt(...)` 写在循环外，JIT 眼里它就是常量。
   要测随机访问，`nextInt` 必须写在循环体内。

## 环境

- JDK 21.0.7
- Maven 3.9.9
- JUnit 5.11.4
- IntelliJ IDEA 2025.1
