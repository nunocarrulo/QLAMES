/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OVS;

/**
 *
 * @author nuno
 */
public class CircularArray {

    private final long[] array;
    private int size;
    private int index;
    private long average;
    
    public CircularArray(int size) {
        array = new long[size];
        average = index = 0;
    }

    public void add(long val) {
        array[index] = val;
        index = (index + 1) % size;
    }

    public long calcAvg() {
        for (int i = 0; i < size; i++) {
            average += array[i];
        }

        return average;
    }

    public long[] getArray() {
        return array;
    }

    public int getSize() {
        return size;
    }

    public int getIndex() {
        return index;
    }

    public long getAverage() {
        return average;
    }

}
