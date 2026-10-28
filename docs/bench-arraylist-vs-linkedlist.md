# ArrayList&LinkedList

## 场景一：随机 get(size/2) 各 10 万次

```Java
for (int j = 0; j < 10; j++) {
    listFor(list);
    listFor(linkedList);
}

System.out.println("预热完成。");

System.out.printf("list 开始时间：" + (start =  System.nanoTime()));
listFor(list);
System.out.printf("list 结束时间：" + (end = System.nanoTime()));

System.out.print("差值: " + (end - start) + " ns ≈ " + String.format("%.3f", (end - start) / 1e9) + " s");


System.out.printf("linkedList 开始时间：" + (start =  System.nanoTime()));
listFor(linkedList);
System.out.printf("linkedList 结束时间：" + (end = System.nanoTime()));

System.out.print("差值: " + (end - start) + " ns ≈ " + String.format("%.3f", (end - start) / 1e9) + " s");


private static void listFor(List<Integer> list) {
     int getIndex = list.size() >> 1;

    for (int i = 0; i < 100000; i++) {
       list.get(getIndex);
    }
}

//list 开始时间：248838468811700
//list 结束时间：248838487607000
//差值: 18795300 ns ≈ 0.019 s
//linkedList 开始时间：248838555358900
//linkedList 结束时间：248852895602100
//差值: 14340243200 ns ≈ 14.340 s
```

### 结论：

- **ArrayList的时间明显短于LinkedList的时间。**

### 原因：

- 在底层代码中，ArrayList除去检测索引是否在合理范围，**直接就返回了指定索引位置上的元素** ，按照时间复杂度的说法是**O(1)**。而在LinkedList底层代码中，除去检测索引是否在合理范围，因为链表**只持有头尾两个引用，中间节点要一步步走**，所以会使用**node(int)**方法去找，因为指定的位置就是n/2，所以直接就是最坏的情况，node中的循环体会循环n/2次，从时间复杂度上来说就是**O(n/2)**，简化常数就变成了**O(n)，大 O 忽略常数，但实际差 2 倍**。

## 场景二：头部插入 10 万次

```Java
for (int i = 0; i < 10; i++) {
    arrayListAddHead(new ArrayList<>(list));
    linkedListAddHead(new LinkedList<>(linkedList));
}
System.out.println("预热完成。");



System.out.printf("list 开始时间：" + (start =  System.nanoTime()));
arrayListAddHead(list);
System.out.printf("list 结束时间：" + (end = System.nanoTime()));
System.out.print("差值: " + (end - start) + " ns ≈ " + String.format("%.3f", (end - start) / 1e9) + " s");

// LinkedList

System.out.printf("linkedList 开始时间：" + (start =  System.nanoTime()));
linkedListAddHead(linkedList);
System.out.printf("linkedList 结束时间：" + (end = System.nanoTime()));
System.out.print("差值: " + (end - start) + " ns ≈ " + String.format("%.3f", (end - start) / 1e9) + " s");

private static void arrayListAddHead(ArrayList<Integer> list) {
    for (int i = 0; i < 100000; i++) {
        list.add(0,i);
    }
}

private static void linkedListAddHead(LinkedList<Integer> list) {
    for (int i = 0; i < 100000; i++) {
        list.add(0,i);
    }
}

//list 开始时间：248935072612500
//list 结束时间：248937101058900
//差值: 2028446400 ns ≈ 2.028 s
//linkedList 开始时间：248937163579700
//linkedList 结束时间：248937165277100
//差值: 1697400 ns ≈ 0.002 s
```

### 结论：

- **ArrayList的时间要大于LinkedList的时间。**

### 原因：

- ArrayList在进行头部添加的时候会进行搬格子的操作，也就是**在头部新追加一个元素的时候，会将之前的元素全部向后搬动一格**，**按照循环的理解，就是每次都要循环之前所有的元素，如果设这个元素为n，那么时间复杂度就是O(n)**。LinkedList并不会进行搬格子的操作，**它只需要修改几个参数的赋值就行了。**
- LinkedList 每次头插要做的事：`new Node<>(e, null, first)` 分配一个 24 字节的对象，然后改两个引用。**10 万次 = 10 万个对象 = 2.4MB。**ArrayList 每次头插要做的事：把当前所有元素整体后移一格。插第 k 个时搬 k-1 个引用。插 10 万个，总共搬5×10^9 次，每个引用 4 字节（压缩 oop），总共 **20 GB 的内存流量**。

## 场景三：for-each 遍历 10 万次

```Java
for (int i = 0; i < 10; i++) {
    arrayListTraverse(list);
    linkedListTraverse(linkedList);
}
System.out.println("预热完成");  

long t0 = System.nanoTime();
arrayListTraverse(list);
long t1 = System.nanoTime();

long t2 = System.nanoTime();
linkedListTraverse(linkedList);
long t3 = System.nanoTime();

System.out.printf("ArrayList  %.4f ms%n", (t1 - t0) / 1e6);
System.out.printf("LinkedList %.4f ms%n", (t3 - t2) / 1e6);

//ArrayList  0.5906 ms
//LinkedList 0.6531 ms
```

### 结论：

- ArrayList的时间小于LinkedList的时间。

### 原因：

- 都是走的迭代器，但是因为**ArrayList中的元素在内存上是连续的**，**LinkedList中的元素在内存上并不是连续的**，因为缓存命中的问题，LinkedList的时间就要高于ArrayList。

## 场景一变种：随机下标取10万次

```Java
Random r = new Random();
int index = r.nextInt(100001);
for (int j = 0; j < 10; j++) {
listFor(list,index);
listFor(linkedList,index);
}

System.out.println("预热完成。");


System.out.printf("list 开始时间：" + (start =  System.nanoTime()));
listFor(list,index);
System.out.printf("list 结束时间：" + (end = System.nanoTime()));

System.out.print("差值: " + (end - start) + " ns ≈ " + String.format("%.3f", (end - start) / 1e9) + " s");


System.out.printf("linkedList 开始时间：" + (start =  System.nanoTime()));
listFor(linkedList,index);
System.out.printf("linkedList 结束时间：" + (end = System.nanoTime()));

System.out.print("差值: " + (end - start) + " ns ≈ " + String.format("%.3f", (end - start) / 1e9) + " s");

private static void listFor(List<Integer> list,int index) {
        if(index == -1) {
            index = list.size() >> 1;
        }
        for (int i = 0; i < 100000; i++) {
           list.get(index);
        }
}
//list 开始时间：249822400533200
//list 结束时间：249822497646700
//差值: 97113500 ns ≈ 0.097 s
//linkedList 开始时间：249822625790800
//linkedList 结束时间：249830065656200
//差值: 7439865400 ns ≈ 7.440 s

```

### 备注：只测试了一个随机，测试多个随机耗时太长，平均结果不会改变。

---

## 修正版实测（把上面四个坑都避开之后）

同一台机器、JDK 21.0.7、10 万元素、`System.nanoTime()`、每项取 5~10 轮最小值。
完整可运行代码：`src/main/java/rehab/BenchFixed.java`。

| 场景 | ArrayList | LinkedList | 倍数 |
|---|---|---|---|
| `get(size/2)` × 10 万次 | **0.494 ms** | **16838 ms** | LinkedList 慢 **34099×** |
| 真随机下标 `get` × 10 万次 | **5.670 ms** | **10344 ms** | LinkedList 慢 **1824×** |
| 头部插入 10 万次 | **4506 ms** | **2.084 ms** | LinkedList 快 **2162×** |
| for-each 遍历 10 万个元素 | **1.4423 ms** | **1.6743 ms** | LinkedList 慢 1.16× |
| 队列用法（10 万入队+出队） | ArrayDeque **2.123 ms** | **3.137 ms** | ArrayDeque 快 1.5× |

和原表的差别：

- 场景一 ArrayList 从 0.019 s 降到 **0.0005 s** —— 去掉那行 `println` 的 16.5 ms，
  剩下的才是真代价。
- 场景一 LinkedList 从 14.34 s 涨到 **16.84 s** —— 原表里 `listFor` 的 `getIndex` 是循环不变量，
  C2 把 `node(index)` 的定位提到了循环外；改成循环体内每次重新取值后，每次都要真走 5 万步。
- 变种场景从 0.097 s / 7.44 s 变成 **0.0057 s / 10.34 s** —— 原来那个「随机」其实是一个常量下标。
- 场景二 ArrayList 从 2.03 s 涨到 **4.51 s** —— 原表的预热只做了 10 次、且预热完就紧接着计时，
  没有给 JIT 足够时间编译 `add(0, i)` 这条热路径。

**结论没变，但量级全变了。** 这正是 benchmark 要命的地方：
四个坑没有一个会让程序报错，只会让你拿着错的数字去下对的结论。

### 一个反直觉的发现：ArrayDeque 不是在所有情况下都赢

「队列用 ArrayDeque 不要用 LinkedList」是 JDK 文档的原话，但直接测会发现：
**内存充裕、数据量小时，LinkedList 的单次 offer/poll 反而更快**（10 万量级差 1.5 倍）。

ArrayDeque 赢在别处，要这样才测得出来：

| 条件 | ArrayDeque | LinkedList |
|---|---|---|
| 100 万入队+出队，`-Xmx4g` | 18.50 ms | **11.88 ms**（LinkedList 快 1.6×） |
| 100 万入队+出队，`-Xmx128m` | **18.93 ms** | 100.42 ms（ArrayDeque 快 5.3×） |

同一份代码、同一个 JVM，只把堆从 4G 改成 128M，胜负就翻了。原因是每元素的内存占用：

| | 每元素净增堆 |
|---|---|
| ArrayList（预分配） | **4.00 B** |
| ArrayDeque（预分配） | **4.00 B** |
| LinkedList | **24.63 B** |

（EpsilonGC + 2M 元素 + 共享 Integer 池实测，排除装箱干扰。）

LinkedList 每个元素一个 24 字节 Node，堆压力一大 GC 就开始收租。
所以「为什么用 ArrayDeque」的正确答案不是「它每次操作更快」，
而是「它在同样内存里能装 6 倍的数据，且不用为每个元素付对象头」。


---

## 踩坑记录

写这份 benchmark 时踩到的四个坑，每一个都真实影响过上面的数字。

### 坑一：预热和计时不是同一份数据

第一版 `head()` 里预热用的是 `new ArrayList<>(list)` 副本，计时用的是原表：

```java
for (int i = 0; i < 10; i++) {
    arrayListAddHead(new ArrayList<>(list));   // 预热：副本
    linkedListAddHead(new LinkedList<>(linkedList));
}
arrayListAddHead(list);                        // 计时：原表
```

副本预热的是**副本的扩容路径**，原表的 `elementData` 一个字节都没热。等于没预热。

### 坑二：`println` 不能放进计时区间

```java
System.out.println("list 开始时间：" + (start = System.nanoTime()));
listFor(list);
System.out.println("list 结束时间：" + (end = System.nanoTime()));
```

看起来只测了 `listFor`，但 `end - start` 里塞进了中间那行 `println` 的全部开销。
实测这行在预热后第一次执行要 **16.5 ms**（要初始化字符编码器、加载类、JIT 编译字符串拼接），
第二次起降到 0.26 ms。

场景一的 ArrayList 结果是 0.019 s ≈ 19 ms，其中 16.5 ms 是这行 `println`。
**真实纯访问耗时只有 2~3 ms**。单独复现验证：

```
>>> 这行 println 耗时 16.5486 ms
>>> 第二次同样一行 0.2634 ms
>>> 第三次同样一行 0.2850 ms
```

正确写法：`long t0 = System.nanoTime(); listFor(list); long t1 = System.nanoTime();`
把打印放到计时之后。

### 坑三：固定下标会被 JIT 提到循环外

```java
int getIndex = list.size() >> 1;              // 循环外算一次
for (int i = 0; i < 100000; i++) list.get(getIndex);
```

`getIndex` 是循环不变量，`list.get(getIndex)` 在 ArrayList 里就是一次
`elementData[getIndex]` 数组读取——JIT 认定它每次结果相同，直接**提到循环外**，
循环体只剩一个没人用的空转。

20 轮取最小值的对照（`REPS = 20 万`，JDK 21.0.7）：

| 写法 | 每次耗时 |
|---|---|
| 固定下标 `get(50000)` | **0.0365 ns** |
| 变化下标 `get(i % N)` | **2.136 ns** |
| 倍数 | **58.5×** |

对照组：同一段代码只开 C1（`-XX:TieredStopAtLevel=1`，不做 C2 那套激进优化）——
固定 1.9095 ns / 变化 3.3930 ns，只差 1.8 倍。**58 倍的差距是 C2 优化出来的，不是数组访问变快了。**
0.0365 ns 已经小于一个时钟周期，只可能是循环体被整个消掉了。

### 坑四：一个随机下标 ≠ 随机访问

```java
int index = r.nextInt(100001);                // 循环外
for (int j = 0; j < 10; j++) listFor(list, index);
```

名字叫「随机下标」，但 `index` 只生成一次，对 JIT 来说它就是常量，
和坑三完全一样。要真的测随机访问，`nextInt` 必须写在循环体内：

```java
for (int i = 0; i < 100000; i++) {
    sink += list.get(r.nextInt(list.size()));
}
```

这也是为什么场景一变种里 LinkedList 只有 7.44 s（固定下标，JIT 已经优化过一部分），
而场景一的 15.96 s 才是 `node(size/2)` 的真实代价。

顺带一个反向的观察：LinkedList 上「固定下标」根本提不出去，因为 `node(idx)` 每次都要重新走链表。
实测单次 `get` 的平均耗时（10 万次平均）：

| 下标 | 平均耗时 |
|---|---|
| `get(0)` | 12.5 ns |
| `get(1000)` | 2.5 µs |
| `get(25000)` | 73.9 µs |
| `get(50000)` | **162.4 µs** |
| `get(75000)` | 79.2 µs |
| `get(99000)` | 3.5 µs |
| `get(99999)` | 8.6 ns |

对称的两端、中间最贵，和 `node(int)` 里 `index < (size >> 1)` 的折半完全对上。

