
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
  * shared - stored globally or accessible through a closure   

For reference: 
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=7THFvoKf7jk&list=PL8XxoCaL3dBgPaKjgP87uSmKZ1MsIZ4rr)

## Week 2
### 15/03/2022 - Threading on the JVM (the basics, continued)
* Concurrent access to shared mutable state: consequences
  * The need for synchronization
  * Threading hazards: lost updates
    * Examples: loss of increments and loss of list insertions 
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
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=nEjw2HzKQ3I&list=PL8XxoCaL3dBgPaKjgP87uSmKZ1MsIZ4rr&index=3)

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
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=xDNrqbCHvis&list=PL8XxoCaL3dBgPaKjgP87uSmKZ1MsIZ4rr&index=4)

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
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=TfM1yRf56-Q&list=PL8XxoCaL3dBgPaKjgP87uSmKZ1MsIZ4rr&index=4)

### 24/03/2022 - Creating a user mode multi-threading runtime: uthreads
* Fundamental concepts of multi-threading runtimes
  * Thread states: sleep, wait and ready
  * Synchronizer concept, revisited
* The _uthreads_ runtime, conclusion
  1. Adding support for _uthreads_ to temporarily relinquish the right to execute: `ut_sleep`
  2. Implementation of an _uthreads_ synchronizer (i.e. count down latch)

For reference:
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=Hn51QKdZoRU&list=PL8XxoCaL3dBgPaKjgP87uSmKZ1MsIZ4rr&index=5)

## Week 4   
### 29/03/2022 - Threading on the JVM: synchronization
* Synchronization on the JVM
  * Data synchronization
  * Control synchronization
* Data syncronization 
  * Using locks
    * Explicit locks (i.e. `ReentrantLock`)
    * Intrinsic locks
  * Thread safe data structures
    * Lock based (`Collections.synchronizedMap(...)` and related)
    * Optimized variants (e.g. `ConcurrentHashMap`)
* Control synchronization: synchronizers (e.g. `CountDownLatch`)
* Approaches to thread-safety, revisited
* Demo: Materialization of the afore mentioned concepts on the ImageViewer application
* Building custom synchronizers using Lampson and Redell monitors
  * Purpose and motivation
  * Lampson and Redell semantics
* Demo: `Latch` with support for timeout and cancelation
* Exercises: `CountDownLatch`

For reference:
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=mIH_vGd7klY&list=PL8XxoCaL3dBgPaKjgP87uSmKZ1MsIZ4rr&index=6)

### 31/03/2022 - Threading on the JVM: monitors
* Building custom synchronizers using Lampson and Redell monitors, continued
  * Purpose and motivation, revisited
  * Lampson and Redell semantics, revisited
* Demo: `CountDownLatch`
* Exercises: `Future`

For reference:
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=O6sSa0asEQI&list=PL8XxoCaL3dBgPaKjgP87uSmKZ1MsIZ4rr&index=7)

## Week 5   
### 05/04/2022 - Threading on the JVM: monitors (Script)
* Building custom synchronizers using Lampson and Redell monitors, continued
* Quick overview of the provided examples: `HandlerThread` and `UnboundedBuffer`
* Demo: Implementing a `ManualResetEvent` using the techniques studied so far
  * Discuss the flaws of the solution
* Present the "delegated execution" pattern (a.k.a. kernel style approach) as a means to address the solution's flaws
* Demo: Implementing the `ManualResetEventKS` using the kernel style approach
  * Discuss the solution properties
* Exercise: `UnboundedQueue` 

### 07/04/2022 - Threading on the JVM: monitors (Script)
* Building custom synchronizers using Lampson and Redell monitors, continued

_(To Be Defined)_
