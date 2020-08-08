package sort;

import java.util.Arrays;

/**
 * @className: BaseSort
 * @description: 排序类的基类
 *   主要实现：
 *      1. less()       比较两元素大小
 *      2. swap()       交换元素
 *      3. show()       输出
 *      4. isSorted()   是否已排序
 *      5. sort()       排序
 *
 * @author: ZSZ
 * @date: 2020/4/3 20:58
 */
public abstract class  BaseSort {

    /**
     * 具体排序算法由子类实现
     */
    private static void sort(){}

    /**
     * 比较v和w, 返回v是否比w小
     * @param v 比较值v
     * @param w 比较值w
     * @return v < w返回true, v >= w返回false
     */
    public static <K extends Comparable<K>> boolean less(K v,K w){
        return v.compareTo(w) < 0;
    }

    /**
     * 在数组a中交换索引i, j对应元素
     * @param array 数组array
     * @param i 索引i
     * @param j 索引j
     */
    public static <K extends Comparable<K>> void swap(K[] array,int i,int j){
        K tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }

    public static <K extends Comparable<K>> void show(K[] array){
        Arrays.stream(array).forEach(x -> System.out.print(x + " "));
        System.out.println();
    }

    public static void show(int[] array){
        Arrays.stream(array).forEach(x -> System.out.print(x + " "));
        System.out.println();
    }

    public static void show(double[] array){
        Arrays.stream(array).forEach(x -> System.out.print(x + " "));
        System.out.println();
    }

    /**
     * 判断数组a[l...r]区间是否有序,
     */
    public static <K extends Comparable<K>> boolean isSorted(K[] a,int l, int r){
        for(int i=l+1;i<=r;i++){
            if(less(a[i],a[i-1])) {
                System.out.println("i-1:"+a[i-1]+",i:"+a[i]);
                return false;
            }
        }
        return true;
    }

    /**
     * 判断数组a[l...r]区间是否有序,
     */
    public static boolean isSorted(int[] a,int l, int r){
        for(int i=l+1;i<=r;i++){
            if(a[i]<a[i-1])return false;
        }
        return true;
    }


    /**
     * 判断数组是否增序
     */
    public static <K extends Comparable<K>> boolean isSorted(K[] a){
        return isSorted(a,0,a.length-1);
    }

    /**
     * 判断数组是否增序
     */
    public static boolean isSorted(int[] a){
        return isSorted(a,0,a.length-1);
    }



    public static <K extends Comparable<K>> void pretreatment(K[] a){
        if(a==null)return ;
        int length = a.length;
        if(length==0)return;

        //进行预处理
        for(int i=1; i<length ;i++){
            if(less(a[i],a[i-1]))swap(a,i,i-1);
        }
    }



}
