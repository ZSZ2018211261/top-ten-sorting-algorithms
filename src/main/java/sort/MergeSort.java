package sort;

import util.RandomArrayUtil;
import util.StopWatch;

import java.util.Arrays;

/**
 * @className: MergeSort
 * @description: 归并排序
 *
 *              归并排序是建立在归并操作上的一种有效的排序算法。
 *              该算法是采用分治法（Divide and Conquer）的一个非常典型的应用。
 *              将已有序的子序列合并，得到完全有序的序列；即先使每个子序列有序，再使子序列段间有序。
 *              若将两个有序表合并成一个有序表，称为2-路归并。
 *
 *              算法描述：
 *                  1. 把长度为n的输入序列分成两个长度为n/2的子序列；
 *                  2. 对这两个子序列分别采用归并排序；
 *                  3. 将两个排序好的子序列合并成一个最终的排序序列。
 *
 *              算法复杂度分析：
 *                  时间复杂度：
 *                          平均 O(n*log(n))   最好 O(n*log(n))     最坏O(n*log(n)))
 *                          比较操作的次数介于 n(log n)/2 和 n(log n)-n+1。 赋值操作的次数是 2n(log n)。
 *                  空间复杂度：
 *                          O(n)
 *                  稳定性：
 *                          稳定
 *
 *               归并排序是一种稳定的排序方法。和选择排序一样，归并排序的性能不受输入数据的影响，
 *               但表现比选择排序好的多，因为始终都是O(nlogn）的时间复杂度。代价是需要额外的内存空间。
 * @author: ZSZ
 * @date: 2020/4/16 11:09
 */
public class MergeSort extends BaseSort implements SortedCompared{

    private static Comparable[] tmp;

    //阀值 如果待排数组元素 <= 7，则使用适合小数据的插入排序
    private static final int THRESHOLD = 7;

//---------------------------------归并排序 递归实现------------------------------------------------

    /**
     * 归并排序 递归实现 自顶向下
     * @param a
     * @param <K>
     */
    public static <K extends Comparable<K>> void mergeSortByRecursive(K[] a){
        if(a == null) return ;
        int length = a.length;
        if(length == 0)return;

        tmp = new Comparable[length];
        mergeSortByRecursive(a,0,length-1);
    }

    /**
     * 归并排序区间在[l,r] 递归实现 自顶向下
     * @param a
     * @param l
     * @param r
     * @param <K>
     */
    public static <K extends Comparable<K>> void mergeSortByRecursive(K[] a,int l,int r){
        if(r <= l)return;
        int mid = l + ((r - l) >> 1);
        mergeSortByRecursive(a,l,mid);
        mergeSortByRecursive(a,mid+1,r);
        merge(a,l,mid,r);
    }

//-------------------------------归并排序 通过迭代循环实现-------------------------------------

    /**
     * 归并排序 通过迭代循环实现 自底向上
     * @param a
     * @param <K>
     */
    public static <K extends Comparable<K>> void mergeSortByIterate(K[] a){
        if(a == null) return ;
        int length = a.length;
        if(length == 0)return;

        tmp = new Comparable[a.length];

        for(int sz = 2;sz<length*2; sz*=2){ //size 为2,4,8, ... 序列
            for(int i=0; i < length ; i+=sz){
                //防止右边界溢出
                int r = i+sz-1 < length ? i+sz-1:length-1;
                merge(a,i,i+sz/2-1,r);
            }
        }
    }

//--------------------------------------归并数组------------------------------------------------

    /**
     * 归并数组，区间在[l,r]
     * @param a
     * @param l
     * @param mid
     * @param r
     * @param <K>
     */
    public static <K extends Comparable<K>> void merge(K[] a, int l, int mid, int r){
        //只有一个元素，return
        if(r-l<=0) return;

        int i = l, j = mid+1;

        for(int k = l; k<=r;k++){
            //左边耗尽
            if(i>mid) tmp[k] = a[j++];
            //右边耗尽
            else if(j > r) tmp[k] = a[i++];
            //左边大于右边
            else if(less(a[j],a[i])) tmp[k] = a[j++];
            else tmp[k] = a[i++];
        }

        //复制元素
        System.arraycopy(tmp,l,a,l,r-l+1);
    }


//------------------------------------综合优化-----------------------------------------------

    /**
     * 综合优化 插入+归并
     * @param a 待排数组
     * @param <K> 类型
     */
    public static <K extends Comparable<K>> void advancedSort(K[] a){

        K[] cur = (K[]) new Comparable[a.length];
        System.arraycopy(a,0,cur,0,a.length);
        advancedSort(cur,a,0,a.length-1);
    }

    /**
     * 综合优化 插入+归并 排序区间[l,r]
     * 1.对小规模子数组采用插入排序:
     *      - 递归对于小规模的数组将产生过多的小数组甚至是空数组调用栈
     * 2.测试数组是否已经有序:
     *      - 若a[mid]<=a[mid+1], 认为数组已经有序, 可以跳过merge()方法
     * 3.不将元素复制到辅助数组tmp
     *      - 调用两种排序方法: 一种将数据从输入数组排序到辅助数组, 另一个方法反之;
     *
     * @param src 待排数组
     * @param dst 排序后的结果
     * @param l 左界限
     * @param r 右界限
     * @param <K> 类型
     */
    public static <K extends Comparable<K>> void advancedSort(K[] src,K[] dst,int l, int r){
        if(r-l < THRESHOLD){
            insertSort(dst,l,r);
            return;
        }

        int mid = l + ((r-l)>>1);
        advancedSort(dst,src,l,mid);
        advancedSort(dst,src,mid+1,r);

        //2. 如果已经有序，则跳过合并
        if(!less(src[mid+1],src[mid])){
            System.arraycopy(src,l,dst,l,r-l+1);
            return;
        }

        merge(src,dst,l,mid,r);

    }

    /**
     * 归并数组 归并区间[l,mid] [mid+1,r]
     * @param src 归并数组
     * @param dst 归并后的结果
     * @param l 左界限
     * @param mid 中间值
     * @param r 右界限
     * @param <K> 类型
     */
    public static <K extends Comparable<K>> void merge(K[] src,K[] dst,int l,int mid,int r){
        if(r-l<=0)return;

        int i = l;
        int j = mid+1;
        for(int k=l;k<=r;k++){
            if(j>r) dst[k]=src[i++];
            else if(i>mid) dst[k]=src[j++];
            else if(less(src[i],src[j])) dst[k] = src[i++];
            else dst[k] = src[j++];
        }
    }

    /**
     * 插入排序
     * 对 r-l < 7 使用插入排序
     * @param a
     * @param l
     * @param r
     * @param <K>
     */
    public static <K extends Comparable<K>> void insertSort(K[] a,int l,int r){
        for(int i = l+1; i<=r; i++){
            K tmp = a[i];
            int j =i;
            for(;j>l; j--){
                if(less(tmp,a[j-1]))a[j] = a[j-1];
                else break;
            }
            a[j] = tmp;
        }
    }

    /**
     * 测试规模：10000000
     *
     * 测试结果：
     * Array created!
     * mergeSortByRecursive method[random]:(6.54 seconds)
     * mergeSortByRecursive method[random+duplicate]:(10.11 seconds)
     *
     * mergeSortByIterate method[random]:(7.17 seconds)
     * mergeSortByIterate method[random+duplicate]:(10.76 seconds)
     *
     * advancedSort method[random]:(5.55 seconds)
     * advancedSort method[random+duplicate]:(8.47 seconds)
     */
    @Override
    public void sortingComparison() {
        // 正常随机数组
        Integer[] a11 = RandomArrayUtil.getRandomBoxedIntArray(0, 10000000, 10000000);
        Integer[] a12 = Arrays.copyOf(a11, a11.length);
        Integer[] a13 = Arrays.copyOf(a11, a11.length);


        // 大量重复数组
        Integer[] a21 = RandomArrayUtil.getRandomBoxedIntArray(0, 100, 10000000);
        Integer[] a22 = Arrays.copyOf(a21, a21.length);
        Integer[] a23 = Arrays.copyOf(a21, a21.length);

        System.out.println("Array created!");

        // 递归实现 mergeSortByRecursive
        StopWatch stopWatch = new StopWatch();
        mergeSortByRecursive(a11);
        if(isSorted(a11))System.out.println(String.format(formatStringWithRandom, "mergeSortByRecursive", stopWatch.elapsedTime()));
        mergeSortByRecursive(a21);
        if(isSorted(a21))System.out.println(String.format(formatStringWithDuplicate, "mergeSortByRecursive", stopWatch.elapsedTime()));
        System.out.println();

        // 递归实现 mergeSortByIterate
        stopWatch = new StopWatch();
        mergeSortByIterate(a12);
        if(isSorted(a12))System.out.println(String.format(formatStringWithRandom, "mergeSortByIterate", stopWatch.elapsedTime()));
        mergeSortByIterate(a22);
        if(isSorted(a22))System.out.println(String.format(formatStringWithDuplicate, "mergeSortByIterate", stopWatch.elapsedTime()));
        System.out.println();

        // 优化 advancedSort
        stopWatch = new StopWatch();
        advancedSort(a13);
        if(isSorted(a13))System.out.println(String.format(formatStringWithRandom, "advancedSort", stopWatch.elapsedTime()));
        advancedSort(a23);
        if(isSorted(a23))System.out.println(String.format(formatStringWithDuplicate, "advancedSort", stopWatch.elapsedTime()));
        System.out.println();
    }
}
