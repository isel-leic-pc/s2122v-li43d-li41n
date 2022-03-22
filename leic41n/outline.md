
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
### 22/03/2022 - Creating a user mode multi-threading runtime: uthreads (Script)  
* The _uthreads_ runtime, continued
  * Adding scheduling support
0. Separate _uthreads_' code from application's code
1. _uthreads_ shouldn't have to know about other _uthreads_
   1. Add dynamic allocation of _uthreads_ (`ut_create`)
   2. Ready queue and running thread
   3. Revisit special case of `main_uthread` and its lifecycle: `ut_init`, `ut_run` and `ut_end`
   4. New concurrency model for _uthreads_: `ut_yield` and `ut_exit`
   5. Revisit code separation between _uthreads_ library and application
2. Fixing the existing memory leak: `internal_exit`
3. Improving the programming model:
   1. No need to explicitly call `ut_exit`
   2. Passing arguments to the functions that define the behaviour of _uthreads_

### 24/03/2022 - Creating a user mode multi-threading runtime: uthreads (Script)  
* The _uthreads_ runtime, conclusion
  * Thread states: sleep, wait and ready
    
(to be continued)
