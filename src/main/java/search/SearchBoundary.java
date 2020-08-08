package search;

import java.util.Comparator;

/**
 * @className: SearchBoundary
 * @description: 查找边界
 * @author: ZSZ
 * @date: 2020/5/13 10:28
 */
public class SearchBoundary<T> {

    private final T[] a;

    private final Comparator<? super T> c;

    public SearchBoundary(T[] a, Comparator<? super T> c){
        this.a = a;
        this.c = c;
    }


    public <T> int searchRightBoundary(T key, T[] a, int lo, int hi, Comparator<? super T> c){
        while(lo < hi){
            int m = lo + ((hi - lo) >>> 1);

            if(c.compare(key,a[m])<0){        //  key<a[m]
                hi = m ;
            }else {                           //  a[m]<=key
                lo = m + 1;
            }
        }
        assert lo == hi;                      // so a[m]<= key <a[m+1]
        return a[lo-1]==key ? lo-1:-1;
    }

    /**
     * 查找范围在[lo,hi]
     * @param key
     * @param a
     * @param lo
     * @param hi
     * @param c
     * @param <T>
     * @return
     */
    public <T> int searchLeftBoundary(T key, T[] a, int lo, int hi, Comparator<? super T> c){
        while(lo < hi){
            int m = lo + ((hi - lo) >>> 1);

            if(c.compare(key,a[m])>0){        //  key<a[m]
                lo = m +1;
            }else {                           //  a[m]<=key
                hi = m;
            }
        }
        assert lo == hi;                      // so a[m]<= key <a[m+1]
        return a[lo]==key ? lo:-1;
    }
}
