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

10 万元素，每项预热后计时，JDK 21.0.7 / Windows / `System.nanoTime()`。
完整代码与原始输出见 [`docs/bench-arraylist-vs-linkedlist.md`](docs/bench-arraylist-vs-linkedlist.md)。

| 场景 | ArrayList | LinkedList | 差距 |
|---|---|---|---|
| `get(size/2)` × 10 万次 | 0.019 s | 14.340 s | LinkedList 慢约 750 倍 |
| 头部插入 10 万次 | 2.028 s | 0.002 s | LinkedList 快约 1000 倍 |
| for-each 遍历 | 0.5906 ms | 0.6531 ms | LinkedList 慢 1.1 倍 |
| 随机下标 `get` × 10 万次 | 0.097 s | 7.440 s | LinkedList 慢约 77 倍 |

> 场景一 ArrayList 的 0.019 s 里含首次 `println` 的类加载与 JIT 开销（单独实测约 15 ms），
> 纯访问耗时为微秒级。该坑的完整验证过程记在 docs 里。

### 三条结论

**1. 按 index 访问，LinkedList 是 O(n)，而且是从两端往中间找的 O(n/2)**

`node(int)` 先比 `index < (size >> 1)`，决定从 `first` 往后走还是从 `last` 往前走。
`get(size/2)` 正好是最坏情况。LinkedList 只持有头尾两个引用，中间节点必须一步步走。

**2. 「LinkedList 增删快」只在已经拿到节点引用时成立**

`add(int, E)` 自己也要先 `node(index)` 定位，所以按下标插入依然是 O(n)。
真正 O(1) 的只有 `addFirst` / `addLast` / 拿到 `ListIterator` 之后的增删。

**3. 栈和队列用 ArrayDeque，不用 LinkedList**

`ArrayDeque` 直接 `extends AbstractCollection`，是环形数组，没有 List 那套包袱；
`LinkedList` 每个元素多一个 Node（12 字节对象头 + 3 个引用），内存放大 3 倍以上，
节点在堆上散落，缓存极不友好。`Deque` 接口的 javadoc 原文：
「This interface should be used in preference to the legacy Stack class」，
`ArrayDeque` 自己的 javadoc：「likely to be faster than LinkedList when used as a queue」。

补充实测（连续头插 10 万次，从空表开始）：

| | 耗时 |
|---|---|
| ArrayList | 698.71 ms |
| LinkedList | 8.10 ms |
| ArrayDeque | 2.79 ms |

## 踩过的坑

写 benchmark 时踩到四个坑，都不是「知道就行」而是「不踩不知道」的：

1. **预热和计时必须是同一份数据。** 预热用副本、计时用原表，等于没预热。
2. **`println` 不能放在计时区间里。** JVM 第一次输出要初始化字符编码器、加载类、
   JIT 编译字符串拼接，实测 7~15 ms，比被测代码本身大三个数量级。
3. **固定下标会被 JIT 提到循环外。** `for (i...) list.get(50000)` 里的数组读取是循环不变量，
   JIT 直接提出去，循环体只剩加法。实测 0.22 ns/次 vs 变化下标 14.65 ns/次，差 66 倍。
4. **一个随机下标 ≠ 随机访问。** `int index = r.nextInt(...)` 写在循环外，JIT 眼里它就是常量。

## 环境

- JDK 21.0.7
- Maven 3.9.9
- JUnit 5.11.4
- IntelliJ IDEA 2025.1
