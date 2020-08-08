package sort;

/**
 * @className: SortedCompared
 * @description: 排序即其优化后的性能比较
 * @author: ZSZ
 * @date: 2020/4/5 20:03
 */

public interface SortedCompared {

    String formatStringWithRandom = "%s method[random]:(%.2f seconds)";

    String formatStringWithDuplicate = "%s method[random+duplicate]:(%.2f seconds)";

    void sortingComparison();

}
