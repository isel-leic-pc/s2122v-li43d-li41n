
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
  * Lecture video (in Portuguese) __(coming soon)__ 

### 17/03/2022 - Creating a user mode multi-threading runtime: uthreads - Script
* Development environment setup
  * https://code.visualstudio.com/docs/remote/containers
  * Demo: Describe starting project and execute it
* Operating system constructs that enable program execution
  * Process
  * Thread (revisited)
* Demo: simultaneous executions of the same program
  * Display variable addresses in both processes
  * Refer [ASLR](https://en.wikipedia.org/wiki/Address_space_layout_randomization)
  * Disable it with `setarch -R ./bin/main`, and there you go: virtual memory FTW
* UThreads - Let us begin =)
  * uthread and context switch concepts
  * Demo: Lets show these concepts through code
    * Step 1 - start by implementing the demo orchestration code
    * Step 2 - define uthread and signature for prepare_thread and context_switch
    * Step 3 - implement context_switch
    * Step 4 - implement prepare_thread and try it out =)
  * cooperative and preemptive runtimes (concept)
  * scheduling concept