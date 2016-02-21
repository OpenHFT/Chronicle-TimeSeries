# Chronicle-TimeSeries
Multi-Threaded Time Series library

# Purpose
This library has two efficiency objectives

- efficient storage on long sequences of data in a column based database.
- multi-threaded processing where possible
- integration with engine for lookup and management of the TimeSeries.
- perform calculation on time series where the timings are in micro-seconds and each time series has it's own timestamps i.e. they don't have to be in sync or vectorized.

# Enterprise edition
The enterprise version

- has more multi-threaded implementations.
- peristsed timeseries (via memory mapped files)
- remote access to time series (no need to have the time series locally)
- distributed times series data where data is processed locally. e.g. if you have N servers, each server can process 1/N of the work.

# Sample program
This program creates two series for the mid of an instrumentent and attempts to see if there is any correlation.

Note: the two time series have different times.

```java
    long size = 600_000_000;

    // generate series 1
    TimeSeries ts = new InMemoryTimeSeries(null);
    ts.setLength(size);

    LongColumn time = ts.getTimestamp();
    time.setAll(Random::new, (c, i, r) -> c.set(i, 9 + (int) Math.pow(1e6, sqr(r.nextFloat()))));
    long sum = time.integrate(); // sum all the intervals

    System.out.printf("%.1f days%n", sum/86400e6);

    DoubleColumn mid = ts.acquireDoubleColumn("mid", BytesDoubleLookups.INT16_4);
    mid.generateBrownian(1, 2, 0.0005);

    // generate series 2
    TimeSeries ts2 = new InMemoryTimeSeries(null);
    ts2.setLength(size);

    LongColumn time2 = ts.getTimestamp();
    time2.setAll(Random::new, (c, i, r) -> c.set(i, 9 + (int) Math.pow(1e6, sqr(r.nextFloat())))); //
    long sum2 = time2.integrate(); // sum all the intervals

    System.out.printf("%.1f days%n", sum2/86400e6);

    DoubleColumn mid2 = ts2.acquireDoubleColumn("mid", BytesDoubleLookups.INT16_4);
    mid2.generateBrownian(1, 2, 0.0005);

    
    // compare the correlation
    CorralationStatistic stats = PearsonsCorrelation.calcCorrelation(mid, mid2, Mode.AFTER_BOTH_CHANGE);
```

This takes around 30 seconds on a 16 core machine for two sets of 600 million data points generated and compared (Notionally >250 business days, ie a year)

When comparing correlations there is many different ways you might do this when the spacing between events varies. 

You can look to see when either changes by a minimum amounts, or when one changes, or when both have changed. 
You might also prefer to sub-sample the data before performing correlation to reduce noise.

