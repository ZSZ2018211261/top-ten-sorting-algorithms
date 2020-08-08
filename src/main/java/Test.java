import sort.*;
import util.RandomArrayUtil;
import util.StopWatch;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @className:
 * @description:
 * @author: ZSZ
 * @date: 2020/3/28 18:14
 */
public class Test {

    public static void main(String[] args) {
//        Integer[] nums = new Integer[]{4,2,2,2,2,3,7,2,8,0,9,34,21};
//        int[] nums = new int[]{0,2,2,2,2,2,3,4,7,8,9,21,34,34};
//        Integer[] nums = new Integer[]{4,0,2,21,9};
//        Integer[] nums = RandomArrayUtil.getRandomBoxedIntArray(0, 1000000, 1000000);
        //冒泡排序
//        BubbleSort bubbleSort = new BubbleSort();
//        bubbleSort.sortBilaterally(nums);
//        bubbleSort.show(nums);
//        //50000 五万
//        bubbleSort.sortingComparison();


        //选择排序
//        SelectionSort selectionSort = new SelectionSort();
//        selectionSort.sortWithBothEnds(nums);
//        selectionSort.show(nums);
//        //50000 五万
//        selectionSort.sortingComparison();

        //插入排序
//        InsertionSort insertionSort = new InsertionSort();
//        insertionSort.twoPathInsertSort(nums);
//        insertionSort.show(nums);
//        //50000 五万
//        insertionSort.sortingComparison();

        //希尔排序
//        ShellSort shellSort = new ShellSort();
//        shellSort.shellSort(nums,3);
//        System.out.println(BaseSort.isSorted(nums));
//        shellSort.show(nums);
//        //1000000 一百万
//        shellSort.sortingComparison();

        //归并排序
//        MergeSort mergeSort = new MergeSort();
//        mergeSort.advancedSort(nums);
//        mergeSort.show(nums);
//        //10000000 一千万
//        mergeSort.sortingComparison();

        //快速排序
//        QuickSort quickSort = new QuickSort();
//        quickSort.threeWaySort(nums);
//        quickSort.show(nums);
//        System.out.println(quickSort.isSorted(nums));
//        //5000000 五百万
//        quickSort.sortingComparison();

        //堆排序
//        HeapSort heapSort = new HeapSort();
//        heapSort.heapSort(nums);
//        heapSort.show(nums);
//        //5000000 五百万
//        heapSort.sortingComparison();

        //计数排序
//        CountSort countSort = new CountSort();
//        countSort.countSort(nums);
//        countSort.show(nums);
//      //100000000 一亿
//        countSort.sortingComparison();

        //桶排序
//        BucketSort bucketSort = new BucketSort();
//        double[] a = RandomArrayUtil.getRandomDoubleArray(0, 100, 100);
//        bucketSort.sort(a, 101);
//        BucketSort.show(a);

//        RadixSort radixSort = new RadixSort();
//        int[] a = RandomArrayUtil.getRandomIntArray(0,1000000,1000000);
//        radixSort.radixSort2(a);
//        System.out.println(radixSort.isSorted(a));
//      //100000000 一亿
//        radixSort.sortingComparison();

        //编译器默认不开启断言，使用时要添加参数虚拟机启动参数-ea
//        assert 1==0;

//        MyTimSort.sort(nums,0,nums.length,(v1,v2)->v1-v2);
//        assert BaseSort.isSorted(nums);
//        Arrays.stream(nums).forEach(x-> System.out.print(x+" "));

        MyTimSort.sortingComparison();

    }

}
