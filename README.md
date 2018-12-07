# Benchmark-Utils
Code generation for benchmarking analysis tools <br>
To generate a C code use _tb.java_, it generates a _.c_ file and a _.vld_ file for validation. For validation use the validator class as:
```java
   validator vl = new validator("rg300.vld");
   vl.validate("dotfile");
```
