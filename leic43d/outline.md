
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
  * Lecture video (in Portuguese) _(coming soon)_

### 24/03/2022 - Creating a user mode multi-threading runtime: uthreads (Script)  
* The _uthreads_ runtime, conclusion
  * Completing scheduling support
  * Thread states: sleep, wait and ready
  * Synchronizer concept, revisited
1. Completing scheduling support implementation
   1. Fixing the existing memory leak: `internal_exit`
   2. Improving the programming model:
      1. No need to explicitly call `ut_exit`
      2. Passing arguments to the functions that define the behaviour of _uthreads_
2. Add support for _uthreads_ to temporarily relinquish the right to execute: `ut_sleep`
   1. Add a global sleep queue
   2. Change `ut_yield` so that termination only occurs after ALL _uthreads_ terminate
3. Implementation of an _uthreads_ synchronizer (i.e. count down latch)
