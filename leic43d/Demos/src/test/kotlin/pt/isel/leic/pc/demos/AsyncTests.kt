package pt.isel.leic.pc.demos

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

val logger: Logger = LoggerFactory.getLogger("AsyncTests")

@Suppress("SameParameterValue")
class AsyncTests {

    private fun asyncOperation(input: String): CompletableFuture<String> {

        val operation = CompletableFuture<String>()
        val timer = Timer()

        timer.schedule(object : TimerTask() {
                override fun run() {
                    logger.info("asyncOperation completed")
                    operation.complete(input.uppercase())
                }
            },
            2000
        )

        return operation
    }

    @Test
    fun `asyncOperation call`() {
        logger.info("Test asyncOperation call starts")
        val opr = asyncOperation("SLB")
        opr.thenAccept {
            logger.info("Inside thenAccept")
        }
        opr.get()
        logger.info("Test asyncOperation call ends")
    }

    @Test
    fun `makes async http request and processes result`() {
        val done = CountDownLatch(1)
        logger.info("Test starts")
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
            .GET()
            .uri(URI("https://httpbin.orgs/delay/2000"))
            .build()

        logger.info("Before client.sendAsync")
        val asyncRequest = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        logger.info("After client.sendAsync")
        val chain = asyncRequest.thenApply {
            logger.info("Inside thenApply")
            it.body().uppercase()
        }
        .whenComplete { _, _ ->
            logger.info("Inside whenComplete.")
            done.countDown()
        }
        logger.info("After chain setup")

        done.await(30, TimeUnit.SECONDS)
        logger.info("Test ends")
    }

    @Test
    fun `makes async http request followed by async operation`() {
        val done = CountDownLatch(1)
        logger.info("Test starts")
        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
            .GET()
            .uri(URI("https://httpbin.org/delay/2000"))
            .build()

        logger.info("Before client.sendAsync")
        val asyncRequest = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        logger.info("After client.sendAsync")
        val chain = asyncRequest.thenCompose {
            logger.info("Inside thenApply")
            asyncOperation(it.body())
        }
        .exceptionally {
            logger.error("Inside exceptionally", it)
            "Error"
        }
        .whenComplete { _, _ ->
            logger.info("Inside whenComplete.")
            done.countDown()
        }
        logger.info("After chain setup")

        done.await(30, TimeUnit.SECONDS)
        logger.info("Test ends")
    }

}