package sort;

import sort.BaseSort;
import util.RandomArrayUtil;
import util.StopWatch;

import java.util.Arrays;

/**
 * @className: sort.BubbleSort
 * @description: 冒泡排序
 *
 *             描述：
 *                  冒泡排序是一种简单的排序算法。
 *                  它重复地走访过要排序的数列，一次比较两个元素，如果它们的顺序错误就把它们交换过来。
 *                  走访数列的工作是重复地进行直到没有再需要交换，也就是说该数列已经排序完成。
 *                  这个算法的名字由来是因为越小的元素会经由交换慢慢“浮”到数列的顶端。
 *
 *             算法：
 *                  1. 比较相邻的元素。如果第一个比第二个大，就交换它们两个；
 *                  2. 对每一对相邻元素作同样的工作，从开始第一对到结尾的最后一对，
 *                     这样在最后的元素应该会是最大的数；
 *                  3. 针对所有的元素重复以上的步骤，除了最后一个；
 *                  4. 重复步骤1~3，直到排序完成。
 *
 *             算法复杂度分析：
 *                  时间复杂度：
 *                          平均 O(n^2)   最好 O(n)     最坏O(n^2)
 *                  空间复杂度：
 *                          O(1)
 *                  稳定性：
 *                          稳定
 *
 * @author: ZSZ
 * @date: 2020/3/28 18:13
 */
public class BubbleSort extends BaseSort implements SortedCompared{


    public static <K extends Comparable<K>>void sort(K[] nums){
        if(nums==null)return;
        int length = nums.length;

        //标准冒泡排序，从前往后比，大的元素后移
        for (int i = length-1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if(less(nums[j+1],nums[j])){
                    swap(nums,j,j+1);
                }
            }
        }
    }

    /**
     * 情况一：处理在排序过程中数组整体已经有序的情况
     * 思路：增加一个flag用于判断本轮循环有没有元素进行交换，如果没有则说明已经排好序
     * @param nums
     * @return
     */
    public static <K extends Comparable<K>> void sortWithFlag(K[] nums){
        if(nums==null)return;
        int length = nums.length;

        //标准冒泡排序，从前往后比，大的元素后移
        for (int i = length-1; i > 0; i--) {
            boolean flag = true;
            for (int j = 0; j < i; j++) {
                if(less(nums[j+1],nums[j])){
                    swap(nums,j,j+1);
                    flag = false;
                }
            }
            if(flag)break;
        }
    }

    /**
     * 情况二：处理在排序过程中数组局部已经有序的情况 + 情况一
     * @param nums
     * @return
     */
    public static <K extends Comparable<K>> void sortWithCheckBound(K[] nums){
        if(nums==null)return;
        int length = nums.length;
        //上一次交换元素的下标
        int last_swap_index = length-1;
        //标准冒泡排序，从前往后比，大的元素后移
        for (int i = length-1; i > 0; i--) {
            boolean flag = true;
            for (int j = 0; j < i; j++) {
                if(less(nums[j+1],nums[j])){
                    swap(nums,j,j+1);
                    last_swap_index = j+1;
                    flag = false;
                }
            }
            if(flag)break;
            i=last_swap_index;
        }
    }

    /**
     * 情况三：
     *      同时找到最大，最小值，排序 + 情况二 + 情况一
     * @param nums
     * @return
     */
    public static <K extends Comparable<K>> void sortBilaterally(K[] nums){
        if(nums==null)return ;
        int length = nums.length;

        boolean flag = true;
        int l = 0;
        int r = length-1;

        //上一次交换元素的下标
        int l_last_swap_index = l;
        int r_last_swap_index = r;

        //从前往后比，大的元素后移，小的元素往前移
        while(l<r && flag){
            flag = false;
            for (int j = l; j < r; j++) {
                //大的元素后移
                if(less(nums[j+1],nums[j])){
                    swap(nums,j,j+1);
                    r_last_swap_index = j+1;
                    flag = true;
                }
                //小的元素往前移
                if(less(nums[r+l-j],nums[r+l-j-1])){
                    swap(nums,r+l-j,r+l-j-1);
                    l_last_swap_index = r+l-j;
                    flag = true;
                }
            }
            l = l_last_swap_index;
            r = r_last_swap_index;
        }
    }


    /**
     * 测试数据规模： 50000
     * 结果：
     *      sort method[random]:(12.04 seconds)
     *      sort method[random+duplicate]:(20.40 seconds)
     *
     *      sortWithFlag method[random]:(11.42 seconds)
     *      sortWithFlag method[random+duplicate]:(19.76 seconds)
     *
     *      sortWithCheckBound method[random]:(11.58 seconds)
     *      sortWithCheckBound method[random+duplicate]:(20.30 seconds)
     *
     *      sortBilaterally method[random]:(10.16 seconds)
     *      sortBilaterally method[random+duplicate]:(17.20 seconds)
     */
    @Override
    public void sortingComparison() {
        // 正常随机数组
        Integer[] a11 = RandomArrayUtil.getRandomBoxedIntArray(0, 1000000, 50000);
        Integer[] a12 = Arrays.copyOf(a11, a11.length);
        Integer[] a13 = Arrays.copyOf(a11, a11.length);
        Integer[] a14 = Arrays.copyOf(a11, a11.length);
        // 大量重复数组
        Integer[] a21 = RandomArrayUtil.getRandomBoxedIntArray(0, 100, 50000);
        Integer[] a22 = Arrays.copyOf(a21, a21.length);
        Integer[] a23 = Arrays.copyOf(a21, a21.length);
        Integer[] a24 = Arrays.copyOf(a21, a21.length);

        System.out.println("Array created!");

        StopWatch stopWatch = new StopWatch();
        sort(a11);
        if(isSorted(a11)) System.out.println(String.format(formatStringWithRandom, "sort", stopWatch.elapsedTime()));
        sort(a21);
        if(isSorted(a21)) System.out.println(String.format(formatStringWithDuplicate, "sort", stopWatch.elapsedTime()));
        System.out.println();

        stopWatch = new StopWatch();
        sortWithFlag(a12);
        if(isSorted(a12))System.out.println(String.format(formatStringWithRandom, "sortWithFlag", stopWatch.elapsedTime()));
        sortWithFlag(a22);
        if(isSorted(a22))System.out.println(String.format(formatStringWithDuplicate, "sortWithFlag", stopWatch.elapsedTime()));
        System.out.println();

        stopWatch = new StopWatch();
        sortWithCheckBound(a13);
        if(isSorted(a13))System.out.println(String.format(formatStringWithRandom, "sortWithCheckBound", stopWatch.elapsedTime()));
        sortWithCheckBound(a23);
        if(isSorted(a23))System.out.println(String.format(formatStringWithDuplicate, "sortWithCheckBound", stopWatch.elapsedTime()));
        System.out.println();

        stopWatch = new StopWatch();
        sortBilaterally(a14);
        if(isSorted(a14)) System.out.println(String.format(formatStringWithRandom, "sortBilaterally", stopWatch.elapsedTime()));
        sortBilaterally(a24);
        if(isSorted(a14)) System.out.println(String.format(formatStringWithDuplicate, "sortBilaterally", stopWatch.elapsedTime()));

    }
}
