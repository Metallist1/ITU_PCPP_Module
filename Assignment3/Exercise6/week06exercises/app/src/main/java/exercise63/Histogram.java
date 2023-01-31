package exercise63;

// first version by Kasper modified by jst@itu.dk 24-09-2021
// raup@itu.dk * 05/10/2022

interface Histogram {
    public void increment(int bin);

    public int getCount(int bin);

    public float getPercentage(int bin);

    public int getSpan();

    public int getTotal();
}
