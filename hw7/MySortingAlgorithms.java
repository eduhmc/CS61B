import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Class containing all the sorting algorithms from 61B to date.
 *
 * You may add any number instance variables and instance methods
 * to your Sorting Algorithm classes.
 *
 * You may also override the empty no-argument constructor, but please
 * only use the no-argument constructor for each of the Sorting
 * Algorithms, as that is what will be used for testing.
 *
 * Feel free to use any resources out there to write each sort,
 * including existing implementations on the web or from DSIJ.
 *
 * All implementations except Distribution Sort adopted from Algorithms,
 * a textbook by Kevin Wayne and Bob Sedgewick. Their code does not
 * obey our style conventions.
 */
public class MySortingAlgorithms {

    /**
     * Java's Sorting Algorithm. Java uses Quicksort for ints.
     */
    public static class JavaSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            Arrays.sort(array, 0, k);
        }

        @Override
        public String toString() {
            return "Built-In Sort (uses quicksort for ints)";
        }
    }

    /** Insertion sorts the provided data. */
    public static class InsertionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
            k = Math.min(array.length, k);
            for (int a = 1; a < k; a += 1) {
                int temporal = array[a]; int x = a - 1;
                while (x >= 0 && array[x] > temporal) {
                    array[x + 1] = array[x];
                    x -= 1;
                }
                array[x + 1] = temporal;
            }
        }

        @Override
        public String toString() {
            return "Insertion Sort";
        }
    }

    /**
     * Selection Sort for small K should be more efficient
     * than for larger K. You do not need to use a heap,
     * though if you want an extra challenge, feel free to
     * implement a heap based selection sort (i.e. heapsort).
     */
    public static class SelectionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
            k = Math.min(array.length, k);
            for (int a = 0; a < k - 1; a++) {
                int minimo = a;
                for (int b = a + 1; b < k; b++) {
                    if (array[b] < array[minimo]) {
                        minimo = b;
                    }
                }
                int temporal = array[minimo];
                array[minimo] = array[a];
                array[a] = temporal;
            }

        }

        @Override
        public String toString() {
            return "Selection Sort";
        }
    }

    /** Your mergesort implementation. An iterative merge
      * method is easier to write than a recursive merge method.
      * Note: I'm only talking about the merge operation here,
      * not the entire algorithm, which is easier to do recursively.
      */
    public static class MergeSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
            if (k < 2) {
                return;
            }
            int medio = k / 2;
            int[] lino = new int[medio ];
            int[] ready = new int[k - medio ];

            for (int a = 0; a < medio ; a++) {
                lino[a] = array[a];
            }
            for (int i = medio ; i < k; i += 1) {
                ready[i - medio ] = array[i];
            }
            sort(lino, medio );
            sort(ready, k - medio );
            merge(array, lino, ready, medio , k - medio );
        }
        public void merge(int[] array, int[] l, int[] r, int left, int right) {
            int i, j, k;
            i = 0; j = 0; k = 0;
            while (i < left && j < right) {
                if (l[i] < r[j]) {
                    array[k++] = l[i++];
                } else {
                    array[k++] = r[j++];
                }
            }
            while (i < left) {
                array[k++] = l[i++];
            } while (j < right) {
                array[k++] = r[j++];
            }
        }


        // may want to add additional methods

        @Override
        public String toString() {
            return "Merge Sort";
        }
    }

    /**
     * Your Distribution Sort implementation.
     * You should create a count array that is the
     * same size as the value of the max digit in the array.
     */
    public static class DistributionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME: to be implemented
            k = Math.min(array.length, k);
            int maximo = array[0];
            for (int i = 1; i < array.length; i++) {
                if (array[i] > maximo) {
                    maximo = array[i];
                }
            }
            int[] lima = new int[maximo + 1];
            for(int a = 0; a < k; a++) {
                lima[array[a]]++;
            }
            int indice = 0;
            for(int elem = 0; elem < maximo + 1; elem++){
                int airpods = lima[elem] + indice;
                if(airpods != indice)
                    Arrays.fill(array, indice, airpods, elem);
                indice = airpods;
            }
        }

        // may want to add additional methods

        @Override
        public String toString() {
            return "Distribution Sort";
        }
    }

    /** Your Heapsort implementation.
     */
    public static class HeapSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
            int n = Math.min(array.length, k);
            for (int x = n / 2 - 1; x >= 0; x--)
                heapify(array, n, x);
            for (int x = n - 1; x >= 0; x--) {
                int temp = array[0];
                array[0] = array[x];
                array[x] = temp;
                heapify(array, x, 0);
            }
        }
        void heapify(int arr[], int n, int i)
        {
            int macara = i; int nico = 2 * i + 1; int fabi = 2 * i + 2;
            if (nico < n && arr[nico] > arr[macara])
                macara = nico;
            if (fabi < n && arr[fabi] > arr[macara])
                macara = fabi;
            if (macara != i) {
                int temp = arr[i];
                arr[i] = arr[macara];
                arr[macara] = temp;
                heapify(arr, n, macara);
            }


        }

        @Override
        public String toString() {
            return "Heap Sort";
        }
    }

    /** Your Quicksort implementation.
     */
    public static class QuickSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            int n = Math.min(k, array.length);
            sort(array,0, n - 1);
        }
        public void sort(int[] array, int low, int high) {
            if (low < high) {
                int pivot = partition(array, low, high);
                sort(array, low, pivot - 1);
                sort(array, pivot + 1, high);
            }
        }
        private int partition(int[] array, int low, int high) {
            int pivot = array[high];
            int i = low - 1;
            for (int j = low; j < high; j += 1) {
                if (array[j] <= pivot) {
                    i += 1;
                    int temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }
            int temp = array[i + 1];
            array[i + 1] = array[high];
            array[high] = temp;
            return i + 1;
        }

        @Override
        public String toString() {
            return "Quicksort";
        }
    }

    /* For radix sorts, treat the integers as strings of x-bit numbers.  For
     * example, if you take x to be 2, then the least significant digit of
     * 25 (= 11001 in binary) would be 1 (01), the next least would be 2 (10)
     * and the third least would be 1.  The rest would be 0.  You can even take
     * x to be 1 and sort one bit at a time.  It might be interesting to see
     * how the times compare for various values of x. */

    /**
     * LSD Sort implementation.
     */
    public static class LSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            // FIXME
            k = Math.min(k, a.length);
            int[] temporal = new int[k];
            for (int i = 0; i < k; i += 1) {
                temporal[i] = a[i];
            }
            lsd(temporal);
            for (int i = 0; i < k; i += 1) {
                a[i] = temporal[i];
            }
        }
        public static void lsd(int[] arr) {
            Queue<Integer>[] cueue = new Queue[10];
            for (int i = 0; i < 10; i++) {
                cueue[i] = new LinkedList<>();
            }
            boolean sorted = false; int exp = 1;
            while (!sorted) {
                sorted = true;
                for (int item : arr) {
                    int bucket = (item / exp) % 10;
                    if (bucket > 0) sorted = false;
                    cueue[bucket].add(item);
                }
                exp *= 10; int index = 0;
                for (Queue<Integer> bucket : cueue) {
                    while (!bucket.isEmpty()) {
                        arr[index++] = bucket.remove();
                    }
                }
            }
        }


        @Override
        public String toString() {
            return "LSD Sort";
        }
    }

    /**
     * MSD Sort implementation.
     */
    public static class MSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            // FIXME
            k = Math.min(k, a.length);

            int[] temp = new int[k];
            msd(a, 0, k - 1, 0, temp);

        }
        public void msd(int[] a, int start, int end, int k, int[] temp) {
            int derecha = 1 << 8;
            int mierda = derecha - 1;
            int[] moños = new int[derecha + 1];
            int shift = 32 - 8 * k - 8;
            for (int i = start; i <= end; i++) {
                int c = (a[i] >> shift) & mierda;
                moños[c + 1]++;
            }
            for (int r = 0; r < derecha; r++) {
                moños[r + 1] += moños[r];
            }
            for (int i = start; i <= end; i++) {
                int c = (a[i] >> shift) & mierda;
                temp[moños[c]++] = a[i];
            }
            for (int i = start; i <= end; i++) {
                a[i] = temp[i - start];
            }
            if (k == 4) {
                return;
            }
            if (moños[0] > 0) {
                msd(a, start, start + moños[0] - 1, k + 1, temp);
            }
            for (int r = 0; r < derecha; r++) {
                if (moños[r + 1] > moños[r]) {
                    msd(a, start + moños[r], start + moños[r + 1] - 1, k + 1, temp);
                }
            }
        }

        @Override
        public String toString() {
            return "MSD Sort";
        }
    }

    /** Exchange A[I] and A[J]. */
    private static void swap(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

}
