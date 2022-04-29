package pt.isel.leic.pc.demos.lockfree

import java.util.concurrent.atomic.AtomicReference
import javax.swing.text.html.HTML.Attribute.N

class LockFreeStack<T>() {

    private class Node<T>(val item: T, var next: Node<T>? = null)

    private val top = AtomicReference<Node<T>?>(null)

    fun push(item: T) {
        val newNode = Node(item)
        while (true) {
            val observedTop = top.get()
            newNode.next = observedTop
            if (top.compareAndSet(observedTop, newNode))
                return
        }
    }

    fun pop(): T? {
        while (true) {
            val observedTop = top.get() ?: return null
            if (top.compareAndSet(observedTop, observedTop.next)) {
                return observedTop.item
            }
        }
    }
}
