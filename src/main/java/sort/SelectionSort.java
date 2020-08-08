package sort;

import util.RandomArrayUtil;
import util.StopWatch;

import java.util.Arrays;

/**
 * @className: SelectionSort
 * @description: 选择排序
 *               选择排序(Selection-sort)是一种简单直观的排序算法。
 *
 *              它的工作原理：
 *                  首先在未排序序列中找到最小（大）元素，存放到排序序列的起始位置，
 *                  然后，再从剩余未排序元素中继续寻找最小（大）元素，然后放到已排序序列的末尾。
 *                  以此类推，直到所有元素均排序完毕。
 *
 *              算法描述：
 *                  n个记录的直接选择排序可经过n-1趟直接选择排序得到有序结果。具体算法描述如下：
 *
 *                  1. 初始状态：无序区为R[1..n]，有序区为空；
 *                  2. 第i趟排序(i=1,2,3…n-1)开始时，当前有序区和无序区分别为R[1..i-1]和R(i..n）。
 *                     该趟排序从当前无序区中-选出关键字最小的记录 R[k]，将它与无序区的第1个记录R交换，
 *                     使R[1..i]和R[i+1..n)分别变为记录个数增加1个的新有序区和记录个数减少1个的新无序区；
 *                  3. n-1趟结束，数组有序化了。
 *
 *             算法复杂度分析：
 *                  时间复杂度：
 *                          平均 O(n^2)   最好 O(n^2)     最坏O(n^2)
 *                  空间复杂度：
 *                          O(1)
 *                  稳定性：
 *                          不稳定
 *
 *            无论什么数据进去都是O(n2)的时间复杂度，所以用到它的时候，数据规模越小越好。
 *            唯一的好处可能就是不占用额外的内存空间了吧。
 *
 * @author: ZSZ
 * @date: 2020/4/5 21:36
 */
public class SelectionSort extends BaseSort implements SortedCompared {

    /**
     * 无优化的经典排序   是一个不稳定的算法
     *
     * 不稳定性举例：
     *
     *         2(第一个2),3,2(第二个2),1,8,5
     *   下标 ：0         1 2         3 4 5
     *        第一轮：
     *             第一次交换： 最小元素为1，所以下标为0的元素和下标为3元素交换
     *                        1,3,2(第二个2),2(第一个2),8,5
     *                        两个2的顺序出现颠倒，所以是不稳定排序
     *                      ......
     *
     * @param a
     * @param <K>
     */
    public static <K extends Comparable<K>> void sort(K[] a){
        if(a==null)return ;
        int length = a.length;
        if(length==0)return;

        for(int i=0;i<length-1;i++){
            //最小元素的下标
            int min_index = i;
            for(int j=i+1;j<length;j++){
                if(less(a[j],a[min_index])) min_index = j;
            }
            swap(a,i,min_index);
        }
    }

    /**
     * 优化:同时找出最大值和最小值
     * @param a
     * @param <K>
     */
    public static <K extends Comparable<K>> void sortWithBothEnds(K[] a){
        if(a==null)return ;
        int length = a.length;
        if(length==0)return;

        int l = 0;
        int r = length-1;

        while(l < r){
            //最小元素的下标
            int min_index = l;
            //最大元素的下标
            int max_index = r;

            for(int j=l+1;j<=r;j++){
                if(less(a[j],a[min_index])) min_index = j;
                if(less(a[max_index],a[j-1])) max_index = j-1;
            }

            //如果左边界为最大值，并且右边界为最小值，则交换
            if(l==max_index && r==min_index) {
                swap(a, l, r);
            }else if(l==max_index){ //左边界为最大值,先把最大值交换到右边界
                swap(a,r,max_index);
                swap(a,l,min_index);
            }else {                 //右边界为最小值,先把最小值交换到左边界 或者 最大最小值不在边界上
                swap(a,l,min_index);
                swap(a,r,max_index);
            }

            l++;
            r--;

        }

    }

    /**
     * 选择排序交冒泡排序有很大的提升（虽然他们的时间复杂度一样），
     * 原因可能是：数组元素的交换开销大，选择排序一轮只进行一次元素交换。
     *
     * 测试数据规模： 50000
     *
     * 结果：
     *      selectionSort method[random]:(4.13 seconds)
     *      selectionSort method[random+duplicate]:(6.03 seconds)
     *
     *      sortWithBothEnds method[random]:(3.08 seconds)
     *      sortWithBothEnds method[random+duplicate]:(4.95 seconds)
     */
    @Override
    public void sortingComparison() {
        // 正常随机数组
        Integer[] a11 = RandomArrayUtil.getRandomBoxedIntArray(0, 1000000, 50000);
        Integer[] a12 = Arrays.copyOf(a11, a11.length);
        // 大量重复数组
        Integer[] a21 = RandomArrayUtil.getRandomBoxedIntArray(0, 100, 50000);
        Integer[] a22 = Arrays.copyOf(a21, a21.length);

        System.out.println("Array created!");

        StopWatch stopWatch = new StopWatch();
        sort(a11);
        if(isSorted(a11))System.out.println(String.format(formatStringWithRandom, "selectionSort", stopWatch.elapsedTime()));
        sort(a21);
        if(isSorted(a21))System.out.println(String.format(formatStringWithDuplicate, "selectionSort", stopWatch.elapsedTime()));
        System.out.println();

        stopWatch = new StopWatch();
        sortWithBothEnds(a12);
        if(isSorted(a12))System.out.println(String.format(formatStringWithRandom, "sortWithBothEnds", stopWatch.elapsedTime()));
        sortWithBothEnds(a22);
        if(isSorted(a22))System.out.println(String.format(formatStringWithDuplicate, "sortWithBothEnds", stopWatch.elapsedTime()));
        System.out.println();
    }
}
