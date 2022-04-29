package pt.isel.leic.pc.demos.lockfree

import java.util.concurrent.atomic.AtomicReference

class LockFreeStack<T> {

    private class Node<T>(val item: T, var next: Node<T>? = null)

    private val top = AtomicReference<Node<T>?>(null)

    fun push(item: T) {
        val newTop = Node(item)
        while(true) {
            val observedTop = top.get()
            newTop.next = observedTop
            if (top.compareAndSet(observedTop, newTop))
                return
        }
    }

    fun pop(): T? {
        while (true) {
            val observedTop = top.get() ?: return null
            if (top.compareAndSet(observedTop, observedTop.next))
                return observedTop.item
        }
    }

}
