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