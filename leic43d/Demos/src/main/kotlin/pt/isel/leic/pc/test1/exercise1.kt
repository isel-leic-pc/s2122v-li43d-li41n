package pt.isel.leic.pc.test1

import java.util.concurrent.Executor

/**
 * Implement the following function:
 * <code> fun <A,B,C> run(f0: ()->A, f1: ()->B, f2: (A,B)->C, executor: Executor): C </code>
 * The function returns the value of the expression f2(f0(), f1()), with the following requirements:
 * 1) all functions must be executed by the executor, and not on the thread that called run;
 * 2) the potential parallelism existing in the expression should be explored, assuming that the relative order
 * of evaluation of f0() and f1() is not relevant.
 * For the sake of simplification, assume that none of the functions f0, f1, and f2 throw exceptions.
 * Keep in mind that for satisfying the synchronization requirements, solutions can only make use of explicit monitors
 * (i.e. Lock and Condition implementations).
 */
@Suppress("UNCHECKED_CAST")
fun <A, B, C> run(f0: () -> A, f1: () -> B, f2: (A, B) -> C, executor: Executor): C {
    TODO()
}