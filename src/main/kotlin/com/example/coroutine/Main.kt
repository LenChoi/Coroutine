package com.example.coroutine

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.runBlocking

//fun main() = runBlocking {
//    println(coroutineContext)
//    println(Thread.currentThread().name)
//    println("Hello")
//}

//fun main() = runBlocking {
//    launch {
//        println("launch: ${Thread.currentThread().name}")
//        println("World!")
//    }
//
//    println("launch: ${Thread.currentThread().name}")
//    println("Hello")
//}

//fun main() = runBlocking {
//    launch {
//        println("launch: ${Thread.currentThread().name}")
//        delay(100L)
//        println("World!")
//    }
//
//    println("launch: ${Thread.currentThread().name}")
//    delay(500L)
//    println("Hello")
//}

//suspend fun doOneTwoThree() =  coroutineScope { // this: 코루틴
//    val job1 = launch { // this: 코루틴
//        println("launch1: ${Thread.currentThread().name}")
//        delay(1000L) // suspension point
//        println("3!")
//    }
//
//    val job2 = launch { // this: 코루틴
//        println("launch2: ${Thread.currentThread().name}")
//        println("1!")
//    }
//
//    val job3 = launch { // this: 코루틴
//        println("launch3: ${Thread.currentThread().name}")
//        delay(500L)
//        println("2!")
//    }
//
//    delay(800L)
//    job1.cancel()
//    job2.cancel()
//    job3.cancel()
//    println("4!")
//}
//
//fun main() = runBlocking {
//    doOneTwoThree()
//    println("runBlocking: ${Thread.currentThread().name}")
//    println("5!")
//}

//suspend fun doCount() = coroutineScope {
//    val job1 = launch(Dispatchers.Default) {
//        var i = 1
//        var nextTime = System.currentTimeMillis() + 100L
//
//        while (i <= 10 && isActive) {
//            val currentTime = System.currentTimeMillis()
//            if(currentTime >= nextTime) {
//                println(i)
//                nextTime = currentTime + 100L
//                i++
//            }
//        }
//    }
//
//    delay(200L)
//    job1.cancelAndJoin()
//    println("doCount Done!")
//}

//suspend fun doOneTwoThree() = coroutineScope {
//    val job1 = launch {
//        try {
//            println("launch1: ${Thread.currentThread().name}")
//            delay(1000L)
//            println("3!")
//        } finally {
//            println("job1 is finishing!")
//            // 파일을 닫아주는 코드
//        }
//    }
//
//    val job2 = launch {
//        try {
//            println("launch2: ${Thread.currentThread().name}")
//            delay(1000L)
//            println("1!")
//        } finally {
//            println("job2 is finishing!")
//            // 소켓을 닫아주는 코드
//        }
//    }
//
//    val job3 = launch {
//        try {
//            println("launch3: ${Thread.currentThread().name}")
//            delay(1000L)
//            println("2!")
//        } finally {
//            println("job3 is finishing!")
//        }
//    }
//
//    delay(800L)
//    job1.cancel()
//    job2.cancel()
//    job3.cancel()
//    println("4!")
//}

//suspend fun getRandom1(): Int {
//    try{
//        delay(1000L)
//        return Random.nextInt(0, 500)
//    } finally {
//        println("getRandom1 is cancelled.")
//    }
//}
//
//suspend fun getRandom2(): Int {
//    delay(500L)
//    throw IllegalStateException()
//}
//
//suspend fun doSomething() = coroutineScope { // 부모 코루틴 (3. 캔슬)
//    val value1 = async { getRandom1() } // 자식 코루틴 (2. 문제 발생했으니깐 캔슬하라.)
//    val value2 = async { getRandom2() } // 자식 코루틴 (1. 문제 발생)
//    try  {
//        println("${value1.await()} + ${value2.await()} = ${value1.await() + value2.await()}") // suspention point
//    } finally {
//        println("doSomething is cancelled.")
//    }
//}
//
//fun main() = runBlocking {
//    try {
//        doSomething()
//    } catch (e: IllegalStateException) {
//        println("doSomething failed: $e")
//    }
//}

//fun main() = runBlocking {
//    launch {
//        println("부모의 콘텍스트 / ${Thread.currentThread().name}")
//    }
//
//    launch(Dispatchers.Default) {
//        println("Default / ${Thread.currentThread().name}")
//    }
//
//    launch(Dispatchers.IO) {
//        println("IO / ${Thread.currentThread().name}")
//    }
//
//    launch(Dispatchers.Unconfined) {
//        println("Unconfined / ${Thread.currentThread().name}")
//    }
//
//    launch(newSingleThreadContext("Fast Campus")) {
//        println("newSingleThreadContext / ${Thread.currentThread().name}")
//    }
//}

//fun main() = runBlocking {
//    async (Dispatchers.Unconfined ) {
//        println("newSingleThreadContext / ${Thread.currentThread().name}")
//        delay(1000L) // suspension point
//        println("newSingleThreadContext / ${Thread.currentThread().name}")
//        delay(1000L) // sp
//        println("newSingleThreadContext / ${Thread.currentThread().name}")
//    }
//}

//fun main() = runBlocking { // 증부모
//    val job = launch { // 부모
//        launch(Job()) {  // 직접 만들었기 때문에 위에 있는 부모의 자식이 아니다
//            println(coroutineContext[Job])
//            println("launch1: ${Thread.currentThread().name}")
//            delay(1000L)
//            println("3!")
//        }
//
//        launch {
//            println(coroutineContext[Job])
//            println("launch1: ${Thread.currentThread().name}")
//            delay(1000L)
//            println("1!")
//        }
//    }
//    delay(500L)
//    job.cancelAndJoin()
//    delay(1000L)
//}

//fun main() = runBlocking {
//    launch {
//        launch(Dispatchers.IO + CoroutineName("launch1")) {
//            println("launch1: ${Thread.currentThread().name}")
////            println(coroutineContext[CoroutineDispatcher])
//            println(coroutineContext[CoroutineName])
//        }
//    }
//}

//suspend fun printRandom() {
//    delay(500L)
//    println(Random.nextInt(0, 500))
//}
//
//fun main() {
//    val job = GlobalScope.launch(Dispatchers.IO) {
//        launch { printRandom() }
//    }
//    Thread.sleep(1000L)
//}

//suspend fun printRandom() {
//    delay(1000L)
//    println(Random.nextInt(0, 500))
//}
//
//suspend fun printRandom2() {
//    delay(500L)
//    throw ArithmeticException()
//}
//
//suspend fun supervisoredFunc() = supervisorScope {
//    launch { printRandom() }
//    launch(ceh) { printRandom2() } // 에러를 낸 곳에 ceh를 붙여야 한다.
//}
//
//val ceh = CoroutineExceptionHandler { _, exception ->
//    println("Something happened: $exception") // 코루틴 컨텍스트, 익셉션을 어디에도 사용 안하면 언더바 쓰자
//}
//
//fun main() = runBlocking {
//    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob() + ceh)
//    val job = scope.launch {
//        supervisoredFunc()
//    }
//}

//suspend fun massiveRun(action: suspend () -> Unit) {
//    val n = 100 // 시작할 코루틴의 갯수
//    val k = 1000 // 코루틴 내에서 반복할 횟수
//    val elapsed = measureTimeMillis {
//        coroutineScope { // scope for coroutines
//            repeat(n) {
//                launch {
//                    repeat(k) { action() }
//                }
//            }
//        }
//    }
//}
//
//val mutex = Mutex()
//var counter = 0
//
//fun main() {
//    runBlocking {
//        withContext(Dispatchers.IO) {
//            massiveRun {
//                mutex.withLock {
//                    counter++
//                }
//            }
//        }
//    }
//    println("Counter = $counter")
//}
//
//sealed class CounterMsg
//object IncCounter : CounterMsg()
//class GetCounter(val response: CompletableDeferred<Int>) : CounterMsg()
//
//fun CoroutineScope.counterActor() = actor<CounterMsg> {
//    var counter = 0 // 액터 안에 상태를 캡슐화해두고 다른 코루틴이 접근하지 못하게 한다.
//
//    for (msg in channel) { // 외부에서 보내는 것은 채널을 통해서만 받을 수 있다.(receive)
//        when (msg) {
//            is IncCounter -> counter++ // 증가시키는 신호.
//            is GetCounter -> msg.response.complete(counter) // 현재 상태를 반환한다.
//        }
//    }
//}
//
//suspend fun massiveRun(action: suspend () -> Unit) {
//    val n = 100 // 시작할 코루틴의 갯수
//    val k = 1000 // 코루틴 내에서 반복할 횟수
//    val elapsed = measureTimeMillis {
//        coroutineScope { // scope for coroutines
//            repeat(n) {
//                launch {
//                    repeat(k) { action() }
//                }
//            }
//        }
//    }
//}
//
//fun main() {
//    runBlocking {
//        val counter = counterActor()
//        withContext(Dispatchers.Default) {
//            massiveRun {
//                counter.send(IncCounter)
//            }
//        }
//
//        val response = CompletableDeferred<Int>()
//        counter.send(GetCounter(response))
//        println("Counter = ${response.await()}")
//        counter.close()
//    }
//}

//fun flowSomething(): Flow<Int> = flow {
//    repeat(10) {
//        emit(Random.nextInt(0, 500))
//        delay(10L)
//    }
//}
//
//fun main()  = runBlocking{
//    flowSomething().collect { value ->
//        println(value)
//    }
//}

//fun flowSomething(): Flow<Int> = flow {
//    repeat(10) {
//        emit(Random.nextInt(0, 500))
//        delay(100L)
//    }
//}
//
//fun main() = runBlocking<Unit> {
//    val result = withTimeoutOrNull(500L) { // 500ms지나면 타임아웃
//        flowSomething().collect { value ->
//            println(value)
//        }
//        true
//    } ?: false
//    if (!result) {
//        println("취소되었습니다.")
//    }
//}

//fun main() = runBlocking {
//    flowOf(1, 2, 3, 4, 5).collect { value ->
//        println(value)
//    }
//    flow {
//        emit(1)
//        emit(2)
//        emit(3)
//        emit(4)
//        emit(5)
//    }.collect { println(it) }
//}

//fun main() = runBlocking {
//    listOf(1, 2, 3, 4, 5).asFlow().collect { value ->
//        println(value)
//    }
//    (6..10).asFlow().collect {
//        println(it)
//    }
//    flowOf(1, 2, 3, 4, 5).collect { println(it) }
//}

//fun flowSomething(): Flow<Int> = flow {
//    repeat(10) {
//        emit(Random.nextInt(0, 500))
//        delay(10L)
//    }
//}
//
//fun main() = runBlocking {
//    flowSomething().map {
//        "$it $it"
//    }.collect { value ->
//        println(value)
//    }
//}

//fun main() = runBlocking{
//    (1..20).asFlow().filter {
//        (it % 2) == 0 //술어 predicate
//    }.collect {
//        println(it)
//    }
//}

//fun main() = runBlocking{
//    (1..20).asFlow().filterNot {
//        (it % 2) == 0 //술어 predicate
//    }.collect {
//        println(it)
//    }
//}

//suspend fun someCalc(i: Int): Int {
//    delay(100L)
//    return i * 2
//}
//
//fun main() = runBlocking{
//    (1..20).asFlow().transform {
//        emit(it)
//        emit(someCalc(it))
//    }.collect {
//        println(it)
//    }
//}

//suspend fun someCalc(i: Int): Int {
//    delay(100L)
//    return i * 2
//}
//
//fun main() = runBlocking {
//    (1..20).asFlow().transform {
//        emit(it)
//        emit(someCalc(it))
//    }.take(10)
//        .collect {
//            println(it)
//        }
//}

//suspend fun someCalc(i: Int): Int {
//    delay(100L)
//    return i * 2
//}
//
//fun main() = runBlocking {
//    (1..20).asFlow().transform {
//        emit(it)
//        emit(someCalc(it))
//    }.drop(1)
//        .collect {
//            println(it)
//        }
//}

//suspend fun someCalc(i: Int): Int {
//    delay(100L)
//    return i * 2
//}
//
//fun main() = runBlocking {
//    val value = (1..10)
//        .asFlow() // 1,2,3,4,5,6,7,~ 10
//        .reduce { a, b -> //a = 1, b = 2 -> a = 3, b = 3 -> a = 6, b = 4
//            a + b // 3, 6, 10
//        } //3  최종 55 나옴
//    println(value)
//}

//suspend fun someCalc(i: Int): Int {
//    delay(10L)
//    return i * 2
//}
//
//fun main() = runBlocking {
//    val value = (1..10)
//        .asFlow() // 1,2,3,4,5,6,7,~ 10
//        .fold(10) { a, b -> // a: 10, b: 1
//            a + b
//        } //최종 65 나옴
//    println(value)
//}

fun main() = runBlocking {
    val counter = (1..10)
        .asFlow() // 1,2,3,4,5,6,7,~ 10
        .count {
            (it % 2) == 0
        }
    println(counter)
}