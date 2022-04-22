package pt.isel.leic.pc.demos.synch;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class ManualResetEventIntrinsicJ {

    private static class Request {
        public boolean isSignaled = false;
    }

    private boolean isSignaled = false;
    private final LinkedList<Request> requests = new LinkedList<>();

    private final Object monitor = new Object();

    private void unblockThreads() {
        while (!requests.isEmpty())
            requests.removeFirst().isSignaled = true;

        monitor.notifyAll();
    }

    public void set() {
        synchronized (monitor) {
            if (!isSignaled) {
                isSignaled = true;
                unblockThreads();
            }
        }
    }

    public void reset() {
        synchronized (monitor) {
            isSignaled = false;
        }
    }

    private boolean waitOne(long timeout, TimeUnit unit) throws InterruptedException {

        synchronized (monitor) {
            if (isSignaled)
                return true;

            long remainingTime = unit.toMillis(timeout);
            Request myRequest = new Request();
            requests.add(myRequest);

            while (true) {
                try {
                    monitor.wait(remainingTime);
                }
                catch (InterruptedException ie) {
                    requests.remove(myRequest);
                    throw ie;
                }

                if (myRequest.isSignaled)
                    return true;

                if (remainingTime <= 0) {
                    requests.remove(myRequest);
                    return false;
                }
            }
        }
    }
}
