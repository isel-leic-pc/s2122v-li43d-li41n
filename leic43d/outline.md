
## Week 1
### 08/03/2022 - Course introduction
* Syllabus, teaching methodology and bibliography.
  * Evaluation
  * Resources
* Course information on [Moodle](https://2122moodle.isel.pt/course/view.php?id=5377) (for authenticated users only)

### 10/03/2022 - Threading on the JVM (the basics)
* Threading on the JVM
  * Purpose and motivation
  * Thread creation and execution (`start`)
  * Synchronization with thread termination (`join`)
* Classifying state: 
  * private - in the thread's stack
  * shared - store globally or accessible through a closure
* Concurrent access to shared mutable state: consequences
  * The need for synchronization: introduction
  * Threading hazards: lost updates
    * Example: loss of increments

For reference: 
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=M3sjLOJqC6w&list=PL8XxoCaL3dBiv-3pHZLbFGYsQiJa9X73o) 
  * [Java Concurrency in Practice by Brian Goetz](https://jcip.net/), chapter 1

## Week 2
### 15/03/2022 - Threading on the JVM (the basics, continued)
* The need for synchronization, continued
  * Threading hazards, continued: lost updates
    * Example: loss of list insertions 
  * Compound actions:
    * Read-modify-write
    * Check-and-act 
* Achieving thread safety: immutability, confinement and synchronization
* Synchronizer:
  * Concept and motivation
  * JDK Synchronizers:
    * Thread (Java's thread object is also a synchronizer. Remember `join`?)
    * AtomicInteger (and other Atomic's)
    * ReentrantLock

For reference: 
  * Lecture video not available (the audio was damaged)
  * [Java Concurrency in Practice by Brian Goetz](https://jcip.net/), chapters 2 and 3

### 17/03/2022 - Creating a user mode multi-threading runtime: uthreads
* Operating system constructs that enable program execution
  * Process
  * Thread (revisited)
* The uthreads runtime
  * Purpose and motivation
  * Demo: implementation of a context-switch in the uthreads runtime
* Cooperative and preemptive runtimes (concept)

For reference:
  * [Development environment setup](https://code.visualstudio.com/docs/remote/containers)
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=ddRqJ3HcLOo&list=PL8XxoCaL3dBiv-3pHZLbFGYsQiJa9X73o&index=2)

## Week 3   
### 22/03/2022 - Creating a user mode multi-threading runtime: uthreads
* Fundamental concepts of multi-threading runtimes
  * Ready queue
  * Running thread
  * Scheduling
* The _uthreads_ runtime, continued
  * Adding scheduling support
  * Demo: materialization of the afore mentioned concepts on the _uthreads_ runtime

For reference:
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=Pz0SES1aGts&list=PL8XxoCaL3dBiv-3pHZLbFGYsQiJa9X73o&index=3)

### 24/03/2022 - Creating a user mode multi-threading runtime: uthreads
* Fundamental concepts of multi-threading runtimes
  * Thread states: sleep, wait and ready
  * Synchronizer concept, revisited
* The _uthreads_ runtime, conclusion
  1. Adding support for _uthreads_ to temporarily relinquish the right to execute: `ut_sleep`
  2. Implementation of an _uthreads_ synchronizer (i.e. count down latch)

For reference:
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=f6qmKxZbjpo&list=PL8XxoCaL3dBiv-3pHZLbFGYsQiJa9X73o&index=4)

## Week 4   
### 29/03/2022 - Threading on the JVM: synchronization
* Synchronization on the JVM
  * Data synchronization
  * Control synchronization
* Data synchronization 
  * Using locks
    * Explicit locks (i.e. `ReentrantLock`)
    * Intrinsic locks
  * Thread safe data structures
    * Lock based (`Collections.synchronizedMap(...)` and related)
    * Optimized variants (e.g. `ConcurrentHashMap`)
* Control synchronization: synchronizers (e.g. `CountDownLatch`)
* Approaches to thread-safety, revisited
* Demo: Materialization of the afore mentioned concepts on the ImageViewer application

For reference:
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=wHACN3eRdcc&list=PL8XxoCaL3dBiv-3pHZLbFGYsQiJa9X73o&index=6)
  * [Java Concurrency in Practice by Brian Goetz](https://jcip.net/), chapter 5

### 31/03/2022 - Threading on the JVM: monitors
* Building custom synchronizers using Lampson and Redell monitors
  * Purpose and motivation
  * Lampson and Redell semantics
* Demo: `Latch` with support for timeout and cancelation
* Exercises: `CountDownLatch`; `Future`

For reference:
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=Ge0bFjOz4Ok&list=PL8XxoCaL3dBiv-3pHZLbFGYsQiJa9X73o&index=7)
  * [Java Concurrency in Practice by Brian Goetz](https://jcip.net/), chapter 14
  * [Sincronização com Monitores, por Carlos Martins](../docs/Synchronization_3rd.pdf)

## Week 5   
### 05/04/2022 - Threading on the JVM: monitors
* Building custom synchronizers using Lampson and Redell monitors, continued
  * Lampson and Redell semantics, continued
  * The _Delegated Execution_ pattern (a.k.a. _kernel style_ approach)
* Demos: `ManualResetEventFlawed`, `ManualResetEventKS`
* Examples: `HandlerThread` and `UnboundedBuffer`
* Exercise: `UnboundedQueue` 

For reference:
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=RUkT7WkWgl8&list=PL8XxoCaL3dBiv-3pHZLbFGYsQiJa9X73o&index=8)
  * [Java Concurrency in Practice by Brian Goetz](https://jcip.net/), chapter 14
  * [Sincronização com Monitores, por Carlos Martins](../docs/Synchronization_3rd.pdf)


### 07/04/2022 - Threading on the JVM: monitors
* Part 1: Building custom synchronizers using Lampson and Redell monitors, continued
  * Lampson and Redell semantics, continued
  * The _Specific Notification_ pattern: optimizing solutions based on the _Delegated Execution_ pattern to reduce the number of context-switches   
  * Demo: Solving the `UnboundedQueue` exercise
* Part 2: Practical class dedicated to the [first assignment](../docs/assignments/PC_s2122v_SE1.pdf)

For reference:
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=j-_kOqrOJU8&list=PL8XxoCaL3dBiv-3pHZLbFGYsQiJa9X73o&index=9)
  * [Java Concurrency in Practice by Brian Goetz](https://jcip.net/), chapter 14
  * [Sincronização com Monitores, por Carlos Martins](../docs/Synchronization_3rd.pdf)


## Week 6   
### 12/04/2022 - Laboratory
* Practical class dedicated to the [first assignment](../docs/assignments/PC_s2122v_SE1.pdf)

### 14/04/2022 - Easter holidays (no classes)

## Week 7   
### 19/04/2022 - Threading on the JVM: thread pools
* Thread pools
  * Purpose and motivation
  * Considerations on the dimensioning of thread pools
  * Compute bound and I/O bound work, revisited
* Building custom synchronizers using Lampson and Redell monitors, continued
* Demo: Building a thread pool

For reference:
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=vbzu6G8IA0g&list=PL8XxoCaL3dBiv-3pHZLbFGYsQiJa9X73o&index=9)
  * [Java Concurrency in Practice by Brian Goetz](https://jcip.net/), chapters 8 and 14

### 21/04/2022 - Threading on the JVM: synchronization
* Synchronization on the JVM: exercises
  * Demo: Building a thread pool, continued
  * Demo: Building a scalable custom cache

For reference:
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=vbzu6G8IA0g&list=PL8XxoCaL3dBiv-3pHZLbFGYsQiJa9X73o&index=10)
  * [Java Concurrency in Practice by Brian Goetz](https://jcip.net/), chapters 5, 8 and 14

## Week 8   
### 26/04/2022 - JVM Memory Model (JMM)
* The JVM memory model, introduction
  * Purpose and motivation
  * Sequential consistency and its limitations
  * Consequences of relaxing the memory model guarantees 
* JVM memory model, described
  * Guarantees regarding atomicity and visibility
  * Coherence and causality in memory accesses 
  * The `volatile` qualifier (`@Volatile` in Kotlin) and the consequences of its use
  * The __Happens Before__ order of events

 For reference:
  * Lecture video (in Portuguese) _(coming soon)_
  * [Java Concurrency in Practice by Brian Goetz](https://jcip.net/), chapter 16

### 28/04/2022 - The JVM Memory Model (JMM), continued (Script)
* The JVM memory model, continued
  * Safe publication
  * Demo: Revisit the SessionInfo singleton in the EchoServer demo
    * Step 1: Assert it is thread safe. Assert it is appropriately published.
    * Step 2: Lets build an artesanal SessionInfo singleton.
      * Step 2.1: Define a class using `var` all over the place instead of `val`
      * Step 2.2: Define a singleton instance using var
    * Guarantees for immutable values
      * The merits of immutability, revisited
* Exercising with the JMM
  * Building simple non blocking algorithms
    * Purpose and motivation
    * Anatomy of lock free algorithms
  * Demos:
    * LockFreeCounter
      * Step 1: Implement increment and decrement operations without using the required while
      * Step 2: Build the test that shows the implementation flaws (by reusing the one from ThreadingHazardsTests)
      * Step 3: Fix it
      * Step 4: Change the signatures of increment and decrement so that they return the new value (with more than one get)
      * Step 5: Buuild a test to show that the solution is incorrect
      * Step 6: Fix it by making only one read. Revisit the coherence slide.
    * LockFreeStack
      * Step 1: Start by identifying the [original author, Treiber](https://en.wikipedia.org/wiki/Treiber_stack)
      * Step 2: Define public contract (i.e. push, pop)
      * Step 3: Define Node class and use an AtomicReference for the stack's top.
      * Step 4: Implement the push operation
      * Step 5: Implement the pop operation
      * Step 6: Build test (by reusing the one from ThreadingHazardsTests)
    * Exercise: LockFreeQueue
      * Describe Michael-Scott algorithm

 For reference:
  * Lecture video (in Portuguese) _(coming soon)_
  * [Java Concurrency in Practice by Brian Goetz](https://jcip.net/), chapters 15 and 16
  * [Java Memory Model Unlearning Experience, by Aleksey Shipilëv](https://www.youtube.com/watch?v=TK-7GCCDF_I)
  * [Java Memory Model Pragmatics (Transcript), by Aleksey Shipilëv](https://shipilev.net/blog/2014/jmm-pragmatics/)