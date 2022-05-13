package pt.isel.leic.pc.demos.async

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertTrue

val logger: Logger = LoggerFactory.getLogger("Async IO Tests")

class AsyncIOTests {

    fun someAsyncOperation(input: String): CompletableFuture<String> {
        val operation = CompletableFuture<String>()
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                logger.info("Before complete")
                operation.complete(input.uppercase())
                logger.info("After complete")
            }
        }, 2000)
        return operation
    }

    @Test
    fun `call async operation`() {
        val done = CountDownLatch(1)
        logger.info("Test starts")
        val opr = someAsyncOperation("SLB")
        opr.thenAcceptAsync {
            logger.info("Inside then with $it")
            done.countDown()
        }
        done.await()
        logger.info("Test ends")
    }

    @Test
    fun `make async http request followed by sync processing`() {
        val done = CountDownLatch(1)
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
            .GET()
            .uri(URI("https://httpbin.org/delay/2000"))
            .build()

        logger.info("Before building the chain")
        client
            .sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply {
                logger.info("Inside thenApply")
                it.body().uppercase()
            }
            .whenComplete { _, _ -> done.countDown() }
        logger.info("After building the chain")
        assertTrue(done.await(20, TimeUnit.SECONDS))
    }

    @Test
    fun `make async http request followed by async processing`() {
        val done = CountDownLatch(1)
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
            .GET()
            .uri(URI("https://httpbin.org/delay/2000"))
            .build()

        logger.info("Before building the chain")
        client
            .sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenCompose {
                logger.info("Inside thenCompose")
                someAsyncOperation(it.body())
            }
            .exceptionally {
                logger.error("Something went wrong", it)
                "ERROR"
            }
            .whenComplete { _, _ -> done.countDown() }
        logger.info("After building the chain")
        assertTrue(done.await(20, TimeUnit.SECONDS))
    }

}