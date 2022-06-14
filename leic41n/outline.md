
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
  * [Java Concurrency in Practice by Brian Goetz](https://jcip.net/), chapter 1


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
  * [Java Concurrency in Practice by Brian Goetz](https://jcip.net/), chapter 5    

### 31/03/2022 - Threading on the JVM: monitors
* Building custom synchronizers using Lampson and Redell monitors, continued
  * Purpose and motivation, revisited
  * Lampson and Redell semantics, revisited
* Demo: `CountDownLatch`
* Exercise: `Future`

For reference:
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=O6sSa0asEQI&list=PL8XxoCaL3dBgPaKjgP87uSmKZ1MsIZ4rr&index=7)
  * [Java Concurrency in Practice by Brian Goetz](https://jcip.net/), chapter 14
  * [Sincronização com Monitores, por Carlos Martins](../docs/Synchronization_3rd.pdf)

## Week 5   
### 05/04/2022 - Threading on the JVM: monitors
* Building custom synchronizers using Lampson and Redell monitors, continued
  * Lampson and Redell semantics, continued
  * The _Delegated Execution_ pattern (a.k.a. _kernel style_ approach)
  * The _Specific Notification_ pattern: optimizing solutions based on the _Delegated Execution_ pattern to reduce the number of context-switches
* Demos: `ManualResetEventFlawed`, `ManualResetEventKS`
* Examples: `HandlerThread` and `UnboundedBuffer`
* Exercise: `UnboundedQueue` (solved in class using the _specific notification_ pattern)

For reference:
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=-SMpgXR3lwI&list=PL8XxoCaL3dBgPaKjgP87uSmKZ1MsIZ4rr&index=8)
  * [Java Concurrency in Practice by Brian Goetz](https://jcip.net/), chapter 14
  * [Sincronização com Monitores, por Carlos Martins](../docs/Synchronization_3rd.pdf)

### 07/04/2022 - Laboratory
* Practical class dedicated to the [first assignment](../docs/assignments/PC_s2122v_SE1.pdf)

## Week 6   
### 12/04/2022 - Laboratory
* Practical class dedicated to the [first assignment](../docs/assignments/PC_s2122v_SE1.pdf)

### 14/04/2022 - Easter holidays (no classes)

## Week 7   
### 19/04/2022 - Threading on the JVM: thread pools
* Thread pools
  * Purpose and motivation
  * Considerations on the dimensioning of thread pools
  * Compute bound and I/O bound work
* Building custom synchronizers using Lampson and Redell monitors, continued
* Demo: Building a thread pool

For reference:
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=fdCSLpsNKMU&list=PL8XxoCaL3dBgPaKjgP87uSmKZ1MsIZ4rr&index=9)
  * [Java Concurrency in Practice by Brian Goetz](https://jcip.net/), chapters 8 and 14

### 21/04/2022 - Threading on the JVM: synchronization
* Synchronization on the JVM: exercises
  * Demo: Building a scalable custom cache

For reference:
  * [Lecture video (in Portuguese)](youtube.com/watch?v=fVBnMuSvg3M&list=PL8XxoCaL3dBgPaKjgP87uSmKZ1MsIZ4rr&index=10)
  * [Java Concurrency in Practice by Brian Goetz](https://jcip.net/), chapter 5

## Week 8   
### 26/04/2022 - The JVM Memory Model (JMM)
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
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=X5vUWYqApQU&list=PL8XxoCaL3dBgPaKjgP87uSmKZ1MsIZ4rr&index=11)
  * [Java Concurrency in Practice by Brian Goetz](https://jcip.net/), chapter 16

### 28/04/2022 - The JVM Memory Model (JMM), continued
* The JVM memory model, continued
  * Safe publication and initialization safety
  * Guarantees for immutable values
    * The merits of immutability, revisited
* Non blocking synchronization
  * Purpose and motivation
  * Anatomy of lock free algorithms
  * Support for CAS operations in the JVM
* Demos: 
  * Implementation of a _lock free_ counter
  * Implementation of a _lock free_ Stack (i.e. the [Treiber algorithm](https://en.wikipedia.org/wiki/Treiber_stack))
* Brief description of the [Michael-Scott algorithm](https://www.cs.rochester.edu/~scott/papers/1996_PODC_queues.pdf)

 For reference:
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=61TSLyXJeTU&list=PL8XxoCaL3dBgPaKjgP87uSmKZ1MsIZ4rr&index=12)
  * [Java Concurrency in Practice by Brian Goetz](https://jcip.net/), chapters 15 and 16
  * [Java Memory Model Unlearning Experience, by Aleksey Shipilëv](https://www.youtube.com/watch?v=TK-7GCCDF_I)
  * [Close Encounters of The Java Memory Model Kind, by Aleksey Shipilëv](https://shipilev.net/blog/2016/close-encounters-of-jmm-kind/)

## Week 9   
### 03/05/2022 - Laboratory
* Practical class dedicated to the [second assignment](../docs/assignments/PC_s2122v_SE2.pdf)

### 05/05/2022 - Asynchronous I/O
* Asynchronous I/O
  * Purpose and motivation
  * Revisiting Node.js threading model: merits, guarantees and shortcomings
* Asynchronous I/O on the JVM - [NIO2](https://docs.oracle.com/javase/8/docs/technotes/guides/io/index.html)
  * Threading model
  * Consequences of the _callback_ passing style (a.k.a Continuation Passing Style)

 For reference:
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=mu06udbPid8&list=PL8XxoCaL3dBgPaKjgP87uSmKZ1MsIZ4rr&index=14)

## Week 10   
### 10/05/2022 - Asynchronous I/O, continued
* Asynchronous I/O on the JVM
  * Consequences of the _callback_ passing style (a.k.a Continuation Passing Style), continued
  * API design based on __futures__ and __promises__
  * Promises in Javascript, revisited
    * Purpose and motivation
    * Threading model
  * Promises on the JVM (a.k.a `java.util.concurrent.CompletableFuture`)
    * Purpose and motivation
    * Threading model

 For reference:
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=GH6n9_yoeM0&list=PL8XxoCaL3dBgPaKjgP87uSmKZ1MsIZ4rr&index=14)

### 12/05/2022 - No class

## Week 11   
### 17/05/2022 - Asynchronous I/O, continued, and Kotlin coroutines
* Asynchronous I/O on the JVM - [NIO2](https://docs.oracle.com/javase/8/docs/technotes/guides/io/index.html)
  * Building a single-threaded echo server (Node's threading model in NIO2)
  * The need for throttling, revisited
  * Orchestrating execution in asynchronous environments ("asynchronizers")
* [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-basics.html)
  * Purpose and motivation
  * Coroutines lifecycle
    * Creation, execution and completion
    * Scheduling
  * [Suspending functions](https://kotlinlang.org/docs/composing-suspending-functions.html)
    * Purpose and motivation
    * Remember: *Suspend execution without blocking the underlying thread*
  * Elements of the programming model
    * Coroutine builders: `runBlocking`, `launch` and `async`

 For reference:
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=Aj1JSERBy28&list=PL8XxoCaL3dBgPaKjgP87uSmKZ1MsIZ4rr&index=15)
  * ["Blocking Threads, suspending coroutines", by Roman Elizarov](https://elizarov.medium.com/blocking-threads-suspending-coroutines-d33e11bf4761)
  * ["An Introduction to Koltin Coroutines", by Pedro Félix](https://labs.pedrofelix.org/guides/kotlin/coroutines/)

### 19/05/2022 - Kotlin coroutines, continued
* [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-basics.html), continued
    * Remember: *Suspend execution without blocking the underlying thread*
  * Elements of the programming model, continued
    * [Coroutine context](https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html)
    * Dispatching coroutines for execution
      * Coroutine dispatchers accessible through `Dispatchers`
      * Custom dispatchers, introduction
    * Design goal: Structured concurrency
      * Purpose and motivation
      * [Coroutine Scope](https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html#coroutine-scope)
    * Adapting asynchronous APIs to coroutines
      * The building block `suspendCoroutine`

 For reference:
  * ["Coroutine Context and Scope", by Roman Elizarov](https://elizarov.medium.com/coroutine-context-and-scope-c8b255d59055)

## Week 12   
### 24/05/2022 - Laboratory
* Practical class dedicated to the [third assignment](../docs/assignments/PC_s2122v_SE3.pdf)

### 26/05/2022 - Kotlin coroutines, continued
* Kotlin Coroutines, continued
  * Coding in the real world: [Cancellation and timeouts](https://kotlinlang.org/docs/cancellation-and-timeouts.html)
    * Purpose and motivation
    * Remember: *Cancelation must be cooperative*
  * Building blocks `suspendCancellableCoroutine` and `withContext`

 For reference:
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=Xjk5pf8CHFM&list=PL8XxoCaL3dBgPaKjgP87uSmKZ1MsIZ4rr&index=17)

## Week 13   
### 31/05/2022 - Laboratory
* Practical class dedicated to the [third assignment](../docs/assignments/PC_s2122v_SE3.pdf)

### 02/06/2022 - Kotlin coroutines, continued
* Kotlin Coroutines, continued
  * Coding in the real world: [Cancellation and timeouts](https://kotlinlang.org/docs/cancellation-and-timeouts.html)
  * Building blocks `suspendCancellableCoroutine` and `withContext`
* Considerations on the structure of software solutions using coroutines
* Coroutines' orchestration using the _consumer-producer_ pattern
  * [The `Channel` building block](https://kotlinlang.org/docs/channels.html)

 For reference:
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=7t0B0K1OdrQ&list=PL8XxoCaL3dBgPaKjgP87uSmKZ1MsIZ4rr&index=18)
  * [Channel API](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/-channel/index.html)

## Week 14   
### 07/06/2022 - Laboratory
* Practical class dedicated to the [third assignment](../docs/assignments/PC_s2122v_SE3.pdf)

### 09/06/2022 - Kotlin coroutines, continued
* Kotlin Coroutines, continued
* Considerations on the structure of software solutions using coroutines, continued
  * Coroutines' orchestration using the _consumer-producer_ pattern
    * [The `Channel` building block](https://kotlinlang.org/docs/channels.html)

 For reference:
  * Lecture video (in Portuguese) _(coming soon)_


## Week 15   
### 14/06/2022 - Wrapping up and bonus content (preview)
* Retrospective on the courses' objectives
* Bonus content
  * Kotlin Flows, introduction
    * Purpose and motivation
    * Basic use cases

 For reference:
  * Lecture video (in Portuguese) _(coming soon)_
  * [Cold flows, hot channels](https://elizarov.medium.com/cold-flows-hot-channels-d74769805f9)
  * [Simple design of Kotlin Flow](https://elizarov.medium.com/simple-design-of-kotlin-flow-4725e7398c4c)

### 16/06/2022 - Holliday (no classes)

