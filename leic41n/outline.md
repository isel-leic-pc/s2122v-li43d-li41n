
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
* Classifiyng state: 
  * private - in the thread's stack
  * shared - stored globally or accessible through a closure   

For reference: 
  * [Lecture video (in Portuguese)](https://www.youtube.com/watch?v=7THFvoKf7jk&list=PL8XxoCaL3dBgPaKjgP87uSmKZ1MsIZ4rr)

## Week 2
### 15/03/2022 - Threading on the JVM (the basics, continued) - Script
* Demo: Add multi-threading to the ImageViewer application
* Concurrent access to shared mutable state: consequences
  * The need for synchronization: introduction
  * Threading hazards: lost updates
    * Example: loss of increments
  * Demo: Adding multi-threaded support to the echo server. Underline the private state and the shared state. Use synchronization to safely update the shared state.
* Achieving thread safety: immutability, confinement, synchronization
* Threading hazards: lost updates, continued
  * Example: loss of list insertions 
  * Demo: stack implementation
* JDK Synchronizers presented so far:
  * Thread (Java's thread object is also a synchronizer. Remember `join`?)
  * AtomicInteger
  * ReentrantLock