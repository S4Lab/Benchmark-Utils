# Benchmark-Utils
Code generation for benchmarking analysis tools <br>
To generate a C code use _tb.java_, it generates a _.c_ file and a _.vld_ file for validation. For validation use the validator class as:
```java
   validator vl = new validator("rg300.vld");
   vl.validate("dotfile");
```
Typically, you might want to compile the resulting _.c_ files and feed it to binary analysis tools or feed the source to source analysis tools. The two parameters in _tb.java_ are _N_, _p_. _N_ is the number of functions, and _p_ is a measure of density in the graph (the number of edges would be around pN<sup>2</sup>), so for small _p_ the graph would be sparse (_p_=0 means no function calls another: no edge in callgraph) and for _p_=1 the graph would be a complete graph.
