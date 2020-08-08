package util;

import sort.BaseSort;

/**
 * @className: BinarySearch
 * @description: 二分查找工具类
 * @author: ZSZ
 * @date: 2020/4/14 11:00
 */
public class BinarySearch {

    /**
     * 寻找target的边界
     * @param a 数组
     * @param l 左边界
     * @param r 右边界
     * @param target 目标值
     * @param <K> 数组类型
     * @return
     */
    public static <K extends Comparable<K>> int findRightBound(K[] a,int l,int r,K target){
        while(l<=r){
            int mid = l + ((r-l)>>1);
            if(BaseSort.less(target,a[mid])) r = mid-1;
            else l = mid + 1;
        }
        return r;
    }

}
