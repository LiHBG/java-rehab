package rehab;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @Author:li_honBin
 * @Version:1.0.0
 * @Date:2026/10/28
 * @Description: ArrayList & LinkedList 耗时检测
 */
public class Bench {
    static long start = -1L;
    static long end = -1L;

    public static void main(String[] args) {


        ArrayList<Integer> list = new ArrayList<>();

        LinkedList<Integer> linkedList = new LinkedList<>();

        // 存入 10w 数据
        for (int i = 0; i < 100000; i++) {
            list.add(i);
            linkedList.add(i);
        }

        // 全部三轮之后开始计时

        // 1. 随机 get(size/2) 各 10 万次
        //listGet(list, linkedList);

        // 2. 头部插入 10 万次
        // ArrayList
        // 预热
        //head(list, linkedList);

        // 3.for-each 遍历 10 万次。

        // ArrayList
        //forEachL(list, linkedList);

        // 4.下标随机 10万次
        listGetAboutRandomIndex(list, linkedList);


    }

private static void listGet(ArrayList<Integer> list, LinkedList<Integer> linkedList) {
        for (int j = 0; j < 10; j++) {
            listFor(list,-1);
            listFor(linkedList,-1);
        }

        System.out.println("预热完成。");

        System.out.println("list 开始时间：" + (start =  System.nanoTime()));
        listFor(list,-1);
        System.out.println("list 结束时间：" + (end = System.nanoTime()));

        System.out.print("差值: " + (end - start) + " ns ≈ " + String.format("%.3f", (end - start) / 1e9) + " s");


        System.out.println("linkedList 开始时间：" + (start =  System.nanoTime()));
        listFor(linkedList,-1);
        System.out.println("linkedList 结束时间：" + (end = System.nanoTime()));

        System.out.print("差值: " + (end - start) + " ns ≈ " + String.format("%.3f", (end - start) / 1e9) + " s");
    }

private static void forEachL(ArrayList<Integer> list, LinkedList<Integer> linkedList) {

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
    }

private static void head(ArrayList<Integer> list, LinkedList<Integer> linkedList) {
        for (int i = 0; i < 10; i++) {
            arrayListAddHead(new ArrayList<>(list));
            linkedListAddHead(new LinkedList<>(linkedList));
        }
        System.out.println("预热完成。");



        System.out.println("list 开始时间：" + (start =  System.nanoTime()));
        arrayListAddHead(list);
        System.out.println("list 结束时间：" + (end = System.nanoTime()));
        System.out.print("差值: " + (end - start) + " ns ≈ " + String.format("%.3f", (end - start) / 1e9) + " s");

        // LinkedList

        System.out.println("linkedList 开始时间：" + (start =  System.nanoTime()));
        linkedListAddHead(linkedList);
        System.out.println("linkedList 结束时间：" + (end = System.nanoTime()));
        System.out.print("差值: " + (end - start) + " ns ≈ " + String.format("%.3f", (end - start) / 1e9) + " s");
    }

private static void listGetAboutRandomIndex(ArrayList<Integer> list, LinkedList<Integer> linkedList){

    Random r = new Random();
    for (int j = 0; j < 10; j++) {
        int index = r.nextInt(100000);
        listFor(list,index);
        //listFor(linkedList,index);
    }

    System.out.println("预热完成。");


    System.out.println("list 开始时间：" + (start =  System.nanoTime()));
    for (int i = 0; i < 100000; i++) {
        int index = r.nextInt(100000);
        list.get(index);
    }
    System.out.println("list 结束时间：" + (end = System.nanoTime()));

    System.out.print("差值: " + (end - start) + " ns ≈ " + String.format("%.3f", (end - start) / 1e9) + " s");


//    System.out.println("linkedList 开始时间：" + (start =  System.nanoTime()));
//    listFor(linkedList,index);
//    System.out.println("linkedList 结束时间：" + (end = System.nanoTime()));
//
//    System.out.print("差值: " + (end - start) + " ns ≈ " + String.format("%.3f", (end - start) / 1e9) + " s");


}


private static void listFor(List<Integer> list,int index) {
        if(index == -1) {
            index = list.size() >> 1;
        }
        for (int i = 0; i < 100000; i++) {
           list.get(index);
        }
    }

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

private static void arrayListTraverse(ArrayList<Integer> list) {
        for (Integer number : list) {}
}

private static void linkedListTraverse(LinkedList<Integer> list) {
        for (Integer number : list) {}
}


private static void start() {
    System.out.println("开始时间：" + (start =  System.nanoTime()));
}

private static void end() {
    System.out.println("结束时间：" + (end = System.nanoTime()));
}

private static void timeDiff() {
    System.out.println("差值: " + (end - start) + " ns ≈ " + String.format("%.3f", (end - start) / 1e9) + " s");
}



}
