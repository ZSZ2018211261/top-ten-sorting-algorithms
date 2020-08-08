package sort;

import util.RandomArrayUtil;
import util.StopWatch;

import java.util.Arrays;

/**
 * @className: HeapSort
 * @description: 堆排序
 *
 *              堆排序（Heapsort）是指利用堆这种数据结构所设计的一种排序算法。
 *              堆积是一个近似完全二叉树的结构，并同时满足堆积的性质：
 *              即子结点的键值或索引总是小于（或者大于）它的父节点。
 *
 *              算法描述
 *                  1. 将初始待排序关键字序列(R1,R2….Rn)构建成大顶堆，此堆为初始的无序区；
 *                  2. 将堆顶元素R[1]与最后一个元素R[n]交换，此时得到新的无序区(R1,R2,……Rn-1)和
 *                     新的有序区(Rn),且满足R[1,2…n-1]<=R[n]；
 *                  3. 由于交换后新的堆顶R[1]可能违反堆的性质，因此需要对当前无序区(R1,R2,……Rn-1)
 *                     调整为新堆，然后再次将R[1]与无序区最后一个元素交换，得到新的无序区(R1,R2….Rn-2)
 *                     和新的有序区(Rn-1,Rn)。
 *                     不断重复此过程直到有序区的元素个数为n-1，则整个排序过程完成。
 *
 *              算法复杂度分析：
 *                  时间:
 *                          平均：O(N * log(n))    最好：O(N * log(n))     最坏：O(N * log(n))
 *                  空间: O(1)
 *                  堆排序的空间复杂度是O(1), 这在嵌入式等内存要求严格的场景下很有用!!!
 *
 *              算法的稳定性：不稳定
 *
 * @author: ZSZ
 * @date: 2020/4/23 15:28
 */
public class HeapSort extends BaseSort implements SortedCompared{


    /**
     * 堆排序入口函数
     * @param a 待排数组
     * @param <K> 泛型类型
     */
    public static <K extends Comparable<K>> void heapSort(K[] a){
        if(a==null)return;
        int length = a.length;
        if(a.length<2)return;

        //构建最大堆
        buildMaxHeap(a);
        for(int i=length-1;i>=0;i--){
            //交换第一个和最后一个元素，也就是把最大值，放到数组末尾
            swap(a,0,i);
            //缩小边界
            length--;
            //调整堆
            heapify(a,0,length);
        }

    }

    /**
     * 构建最大堆
     * @param a 待排数组
     * @param <K> 泛型类型
     */
    public static <K extends Comparable<K>> void buildMaxHeap(K[] a){
        for (int len=a.length,i= len >>1; i>=0; i--){
            heapify(a,i,len);
        }
    }

    /**
     * 调整堆，针对第i个元素重建堆
     * @param a
     * @param i
     * @param bound
     * @param <K>
     */
    public static <K extends Comparable<K>> void heapify(K[] a,int i,int bound){
        int lagest = i;
        int left = i*2+1;                       //左孩子
        int right = i*2+2;                      //右孩子

        if(left<bound && less(a[lagest],a[left])) lagest = left;
        if(right<bound && less(a[lagest],a[right])) lagest = right;
        if(lagest!=i){
            swap(a,lagest,i);
            heapify(a,lagest,bound);
        }
    }


    /**
     * 测试规模：5000000 五百万
     *
     * 堆排序在实际中，都慢于快排和归并，但是堆排序不论好坏都可以把时间复杂度维持在 O(N * log(n))
     *
     * 测试结果：
     * heapSort method[random]:(8.33 seconds)
     * heapSort method[random+duplicate]:(10.61 seconds)
     *
     */
    @Override
    public void sortingComparison() {
        // 正常随机数组
        Integer[] a11 = RandomArrayUtil.getRandomBoxedIntArray(0, 1000000, 5000000);

        // 大量重复数组
        Integer[] a21 = RandomArrayUtil.getRandomBoxedIntArray(0, 100, 5000000);

        System.out.println("Array created!");

        StopWatch stopWatch = new StopWatch();
        heapSort(a11);
        if(isSorted(a11))System.out.println(String.format(formatStringWithRandom, "heapSort", stopWatch.elapsedTime()));
        heapSort(a21);
        if(isSorted(a21))System.out.println(String.format(formatStringWithDuplicate, "heapSort", stopWatch.elapsedTime()));
        System.out.println();
    }
}
