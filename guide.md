- runBlocking

코루틴을 만들고 코드 블록이 수행이 끝날 떄까지 runBlocking 다음의 코드를 수행하지 못하게 막는다. runBlocking 안에서 this를 수행하면 코루틴이 수신 객체(Receiver)인 것을 알 수 있다.
BlockingCoroutine은 CoroutineScope의 자식이다. 코틀린 코루틴을 쓰는 모든 곳에는 코루틴 스코프(CoroutineScope)가 있다고 생각하면 된디ㅏ. 코루틴 컨텍스트를 통해 코루틴이 어떻게
동작할 지의 방식을 알 수 있다.

```
fun main() = runBlocking {
    println(coroutineContext)
    println(Thread.currentThread().name)
    println("Hello")
}
```

- launch

런치는 코루틴 빌더이다. 새로운 코루틴을 만들기 때문에 새롱룬 코루틴 스코프를 만들게 된다. 런치는 할 수 있다면 다른 코루틴 코드를 같이 수행 시키는 코루틴 빌더이다.

```aidl
fun main() = runBlocking {
    launch {
        println("launch: ${Thread.currentThread().name}")
        println("World!")
    }

    println("launch: ${Thread.currentThread().name}")
    println("Hello")
}
```

- delay

해당 쓰레드를 해제를 하고 잠시 쉬어간다. 쓰레드를 다른 곳에서 사용 할 수 있게 해줌.

```aidl
fun main() = runBlocking {
    launch {
        println("launch: ${Thread.currentThread().name}")
        delay(100L)
        println("World!")
    }

    println("launch: ${Thread.currentThread().name}")
    delay(500L)
    println("Hello")
}
```

runBlocking을 캔슬 하면 자식인 launch도 취소 된다.

- suspend

중단 가능한 함수이다. 코드의 일부를 함수로 분리할 떄는 함수의 앞에 suspend 키워드를 붙이면 된다.

- coroutineScope

코루틴은 자식들이 끝날때까지 기다린다(런치1,2,3)
만약 코루틴 스코프를 캔슬하면 자식도 캔슬된다. 코루틴을 호출하기 위해 존재

```aidl
suspend fun doOneTwoThree() =  coroutineScope { // this: 코루틴
    launch { // this: 코루틴
        println("launch1: ${Thread.currentThread().name}")
        delay(1000L)
        println("3!")
    }

    launch { // this: 코루틴
        println("launch2: ${Thread.currentThread().name}")
        delay(1000L)
        println("1!")
    }

    launch { // this: 코루틴
        println("launch3: ${Thread.currentThread().name}")
        delay(1000L)
        println("2!")
    }
    println("4!")
}

fun main() = runBlocking {
    doOneTwoThree()
    println("runBlocking: ${Thread.currentThread().name}")
}
```

코루틴 스코프와 runBlocking의 모양은 거의 비슷하다

하지만 runBLocking은 현재 쓰레드를 멈추게 만들고 기다리지만 coroutineSope는 쓰레드를 멈추게 하지 않는다. 호출한 쪽이 suspend 되고 시작하게 되면 다시 활동하게 된다.

- join

조인은 기다리게 하는 것, 첫번째 런치블록이 끝날때 까지 기다린다.

```aidl
suspend fun doOneTwoThree() =  coroutineScope { // this: 코루틴
    val job = launch { // this: 코루틴
        println("launch1: ${Thread.currentThread().name}")
        delay(1000L) // suspension point
        println("3!")
    }
    job.join() // suspension point

    launch { // this: 코루틴
        println("launch2: ${Thread.currentThread().name}")
        delay(1000L)
        println("1!")
    }

    launch { // this: 코루틴
        println("launch3: ${Thread.currentThread().name}")
        delay(1000L)
        println("2!")
    }
    println("4!")
}

fun main() = runBlocking {
    doOneTwoThree()
    println("runBlocking: ${Thread.currentThread().name}")
}
```

```aidl
suspend fun doOneTwoThree() =  coroutineScope { // this: 코루틴
    val job1 = launch { // this: 코루틴
        println("launch1: ${Thread.currentThread().name}")
        delay(1000L) // suspension point
        println("3!")
    }

    val job2 = launch { // this: 코루틴
        println("launch2: ${Thread.currentThread().name}")
        println("1!")
    }

    val job3 = launch { // this: 코루틴
        println("launch3: ${Thread.currentThread().name}")
        delay(500L)
        println("2!")
    }

    delay(800L)
    job1.cancel()
    job2.cancel()
    job3.cancel()
    println("4!")
}

fun main() = runBlocking {
    doOneTwoThree()
    println("runBlocking: ${Thread.currentThread().name}")
    println("5!")
}
```

- cancelAndJoin

```aidl
suspend fun doCount() = coroutineScope {
    val job1 = launch(Dispatchers.Default) {
        var i = 1
        var nextTime = System.currentTimeMillis() + 100L

        while (i <= 10) {
            val currentTime = System.currentTimeMillis()
            if(currentTime >= nextTime) {
                println(i)
                nextTime = currentTime + 100L
                i++
            }
        }
    }
    
    delay(200L)
    job1.cancelAndJoin()
    println("doCount Done!")
}
```

- isActive 해당 코루틴이 여전히 활성화된지 확인할 수 있습니다.

```aidl
suspend fun doCount() = coroutineScope {
    val job1 = launch(Dispatchers.Default) {
        var i = 1
        var nextTime = System.currentTimeMillis() + 100L

        while (i <= 10 && isActive) {
            val currentTime = System.currentTimeMillis()
            if(currentTime >= nextTime) {
                println(i)
                nextTime = currentTime + 100L
                i++
            }
        }
    }

    delay(200L)
    job1.cancelAndJoin()
    println("doCount Done!")
}
```

- finally

launch에서 자원을 할당 한 경우 JobCancellationException을 발생하기 때문에 표준 try catch finally로 대응 가능

```aidl
suspend fun doOneTwoThree() = coroutineScope {
    val job1 = launch {
        try {
            println("launch1: ${Thread.currentThread().name}")
            delay(1000L)
            println("3!")
        } finally {
            println("job1 is finishing!")
            // 파일을 닫아주는 코드
        }
    }

    val job2 = launch {
        try {
            println("launch2: ${Thread.currentThread().name}")
            delay(1000L)
            println("1!")
        } finally {
            println("job2 is finishing!")
            // 소켓을 닫아주는 코드
        }
    }

    val job3 = launch {
        try {
            println("launch3: ${Thread.currentThread().name}")
            delay(1000L)
            println("2!")
        } finally {
            println("job3 is finishing!")
        }
    }

    delay(800L)
    job1.cancel()
    job2.cancel()
    job3.cancel()
    println("4!")
}
```

- withTimeout

일정 시간이 지나면 종료 하고 싶을

```aidl
fun main() = runBlocking {
    withTimeout(500L) {
        doCount()
    }
}
```

- withTimeoutAndNull

```aidl
fun main() = runBlocking {
    withTimeout(500L) {
        doCount()
        true
    } ?: false
    println(result)
}
```

- suspend 함수들의 순차적인 활용

```aidl
suspend fun getRandom1(): Int {
    delay(1000L)
    return Random.nextInt(0, 500)
}

suspend fun getRandom2(): Int {
    delay(1000L)
    return Random.nextInt(0, 500)
}

fun main() = runBlocking {
    val elapsedTime = measureTimeMillis {
        val value1 = getRandom1()
        val value2 = getRandom2()
        println("$value1 + $value2 = ${value1 + value2}")
    }
    println(elapsedTime)
}
```

- async를 이용해 동시에 수행하기

async를 이용하면 동시에 다른 블록을 수행할 수 있다. launch와 비슷해 보이지만 수행 결과를 await 키워드를 통해 받을 수 있다. 수행된 결과를 가져오는게 await. await를 호출하면 잠이 들었다가
처리가 끝나면

```aidl
suspend fun getRandom1(): Int {
    delay(1000L)
    return Random.nextInt(0, 500)
}

suspend fun getRandom2(): Int {
    delay(1000L)
    return Random.nextInt(0, 500)
}

fun main() = runBlocking {
    val elapsedTime = measureTimeMillis {
        val value1 = async { getRandom1() } // this: 코루틴
        val value2 = async { getRandom2() }

        // job.join() + 결과도 가져옴.
        println("${value1.await()} + ${value2.await()} = ${value1.await() + value2.await()}") // suspention point
    }
    println(elapsedTime)
}
```

- async 게으르게 사용하기

async(start = CoroutineStart.LAZY)로 인자를 전달하면 우리가 원하는 순간 수행을 준비하게 할 수 있다. 이후 start 메서드를 이용해 수행을 준비 할 수 있다.

```aidl
suspend fun getRandom1(): Int {
    delay(1000L)
    return Random.nextInt(0, 500)
}

suspend fun getRandom2(): Int {
    delay(1000L)
    return Random.nextInt(0, 500)
}

fun main() = runBlocking {
    val elapsedTime = measureTimeMillis {
        val value1 = async(start = CoroutineStart.LAZY) { getRandom1() } // 레이지이기 때문
        val value2 = async(start = CoroutineStart.LAZY) { getRandom2() }

        value1.start() // 큐에 수행 예약을 한다.
        value2.start()
        // job.join() + 결과도 가져옴.
        println("${value1.await()} + ${value2.await()} = ${value1.await() + value2.await()}") // suspention point
    }
    println(elapsedTime)
}
```

바로 예약하지 않게 만들고 start로 수행을 예약한다. 일반적으로 많이 쓰지는

- 코루틴 디스패처

1. Default는 코어 수에 비례하는 스레드 풀에서 수행
2. IO는 코어 수 보다 훨ㅆ니 많은 스레드를 가지는 스레드 풀, IO 작업은 CPU를 덜 소모
3. Unconfined는 어디에도 속하지 않는다. 지금 시점에서는 부모의 스레드에서 수행 될것, 앞으로 어디서 실행될지 알 수 없다.
4. newSingleThreadContext는 항상 새로운 스레드를 만든다.

async나 어디서든 사용 가능하다.

```aidl
fun main() = runBlocking {
    launch {
        println("부모의 콘텍스트 / ${Thread.currentThread().name}")
    }

    launch(Dispatchers.Default) {
        println("Default / ${Thread.currentThread().name}")
    }

    launch(Dispatchers.IO) {
        println("IO / ${Thread.currentThread().name}")
    }

    launch(Dispatchers.Unconfined) {
        println("Unconfined / ${Thread.currentThread().name}")
    }

    launch(newSingleThreadContext("Fast Campus")) {
        println("newSingleThreadContext / ${Thread.currentThread().name}")
    } // 새로운 쓰레드를 만들어서 써라, 이름 지정 가능
}
```

- Confined

처음에는 부모의 스레드에서 수행된다. 하지만 중단점(suspension point)에 오면 바뀌게 된다. 어느 디스패처에서 수행될지 예측하기 어려워 가능하면 unconfined를 사용하지 말자

- Job을 직접 만들었을 경우

```fun main() = runBlocking { // 증부모
    val job = launch { // 부모
        launch(Job()) {  // 직접 만들었기 때문에 위에 있는 부모의 자식이 아니다
            println(coroutineContext[Job])
            println("launch1: ${Thread.currentThread().name}")
            delay(1000L)
            println("3!")
        }

        launch {
            println(coroutineContext[Job])
            println("launch1: ${Thread.currentThread().name}")
            delay(1000L)
            println("1!")
        }
    }
    delay(500L)
    job.cancelAndJoin()
    delay(1000L)
}
```

- 코루틴 엘리먼트 결함

여러 코루틴 엘리먼트를 한번에 사용할 수 있다. + 연산으로 엘리먼트를 합치면 된다. 합쳐진 엘리먼트들은 coroutineContext[XXX]로 조회할 수 있다.

```aidl
fun main() = runBlocking {
    launch {
        launch(Dispatchers.IO + CoroutineName("launch1")) {
            println("launch1: ${Thread.currentThread().name}")
            println(coroutineContext[CoroutineDispatcher])
            println(coroutineContext[CoroutineName])
        }
    }
}
```

- GlobalScope

어디에도 속하지 않지만 원래부터 존재하는 전역 GlobalScope가 있다. 이걸 사용하면 코루틴을 쉽게 수행 가능

```aidl
fun main() {
    val job = GlobalScope.launch(Dispatchers.IO) {
        launch { printRandom() }
    }
    Thread.sleep(1000L)
}
```

권장되지 않음

- CoroutineScope

CoroutineScope는 인자로 CoroutineContext를 받는데 코루틴 엘리먼트를 하나만 넣어도 좋고 이전에 배웠든 엘리먼트를 합쳐 코루틴 컨텍스트를 만들어도 된다.

- CEH(코루틴 익셉션 핸들러)

```aidl
suspend fun printRandom() {
    delay(1000L)
    println(Random.nextInt(0, 500))
}

suspend fun printRandom2() {
    delay(500L)
    throw ArithmeticException()
}

val ceh = CoroutineExceptionHandler { coroutineContext, exception ->
    println("Something happened: $exception") // 코루틴 컨텍스트, 익셉션을 어디에도 사용 안하면 언더바 쓰자
}

fun main() = runBlocking {
    val scope = CoroutineScope(Dispatchers.IO)
    val job = scope.launch(ceh) {
        launch { printRandom() }
        launch { printRandom2() }
    }
    job.join()
}
```

대부분의 경우는 exception만 사용하고 나머지는 _로 쓴다.

- runBlocking과 CEH

```aidl
suspend fun printRandom() {
    delay(1000L)
    println(Random.nextInt(0, 500))
}

suspend fun printRandom2() {
    delay(500L)
    throw ArithmeticException()
}

val ceh = CoroutineExceptionHandler { coroutineContext, exception ->
    println("Something happened: $exception") // 코루틴 컨텍스트, 익셉션을 어디에도 사용 안하면 언더바 쓰자
}

fun main() = runBlocking { // 1 최상단 코루틴
    val job = launch(ceh) { //2
        val a = async { printRandom() } //3
        val b = async { printRandom2() } //3
        println(a.await())
        println(b.await())
    }
    job.join()
}
```

- SupervisorJob

예외에 의한 취소를 아래족으로 내려가게 한다.

```aidl
fun main() = runBlocking {
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob() + ceh)
    val job1 = scope.launch { printRandom() }
    val job2 = scope.launch { printRandom2() } // job2에서 에러가 터지먼 얘의 자식에만 영향을 준다.
    joinAll(job1, job2) // 두가지 모두 기다려야 하면 한번에 가능
}

```

- SupervisorScope

```aidl
suspend fun printRandom() {
    delay(1000L)
    println(Random.nextInt(0, 500))
}

suspend fun printRandom2() {
    delay(500L)
    throw ArithmeticException()
}

suspend fun supervisoredFunc() = supervisorScope {
    launch { printRandom() }
    launch(ceh) { printRandom2() } // 에러를 낸 곳에 ceh를 붙여야 한다.
}

val ceh = CoroutineExceptionHandler { _, exception ->
    println("Something happened: $exception") // 코루틴 컨텍스트, 익셉션을 어디에도 사용 안하면 언더바 쓰자
}

fun main() = runBlocking {
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob() + ceh)
    val job = scope.launch { 
        supervisoredFunc()
    }
}
```

- 공유 객체 문제

withContext들어갈때 runblocking은 잠들게 되고 withContext가 끝나면 깨운다. runBlocking은 쓰레드를 잡고 있지만 withContext는 밖에 있는 코루틴을 잠들게 한다.

```aidl
suspend fun massiveRun(action: suspend () -> Unit) {
    val n = 100 // 시작할 코루틴의 갯수
    val k = 1000 // 코루틴 내에서 반복할 횟수
    val elapsed = measureTimeMillis {
        coroutineScope { // scope for coroutines
            repeat(n) {
                launch {
                    repeat(k) { action() }
                }
            }
        }
    }
}

var counter = 0

fun main() {
    runBlocking {
        withContext(Dispatchers.Default) {
            massiveRun {
                counter++
            }
        }
    }
    println("Counter = $counter") // 여러 쓰레드간 정보가 다르기 떄문에 값이 10만이 아니고 다르다
}
```

- @VOlatile

```aidl
@Volatile // 어떤 쓰레드에서 변경해도 영향을 준다. 현재 값을 다른쓰레드에서 알 수 있지만 만약 그떄 증가시키면 방영이 안될 수 있다.
var counter = 0

fun main() {
    runBlocking {
        withContext(Dispatchers.Default) {
            massiveRun {
                counter++ // 값을 가져와서 더해주는거기 때문 값을 가져올때 다른쓰레드에서 증가 시킬수 있고 아닐 수 있다.
            }
        }
    }
    println("Counter = $counter") // 여러 쓰레드간 정보가 다르기 떄문에 값이 10만이 아니고 다르다
}
```

- AtomicInteger()

다른 쓰레드에서 동시에 값을 바꿀 수 없다. 간섭 불가능

```aidl
suspend fun massiveRun(action: suspend () -> Unit) {
    val n = 100 // 시작할 코루틴의 갯수
    val k = 1000 // 코루틴 내에서 반복할 횟수
    val elapsed = measureTimeMillis {
        coroutineScope { // scope for coroutines
            repeat(n) {
                launch {
                    repeat(k) { action() }
                }
            }
        }
    }
}

var counter = AtomicInteger()

fun main() {
    runBlocking {
        withContext(Dispatchers.Default) {
            massiveRun {
                counter.incrementAndGet() // 값을 가져와서 더해주는거기 때문 값을 가져올때 다른쓰레드에서 증가 시킬수 있고 아닐 수 있다.
            }
        }
    }
    println("Counter = $counter") // 여러 쓰레드간 정보가 다르기 떄문에 값이 10만이 아니고 다르다
}
```

- 스레드 한정 (newSingleThreadContext)

하나의 코루틴 컨택스트를 만드는데 특정 쓰레드 하나만 만들어서 사용하게 한다. 항상 같은 쓰레드에서 사용하는것이 보장

```aidl
suspend fun massiveRun(action: suspend () -> Unit) {
    val n = 100 // 시작할 코루틴의 갯수
    val k = 1000 // 코루틴 내에서 반복할 횟수
    val elapsed = measureTimeMillis {
        coroutineScope { // scope for coroutines
            repeat(n) {
                launch {
                    repeat(k) { action() }
                }
            }
        }
    }
}

var counter = 0
var counterContext = newSingleThreadContext("CounterContext")

fun main() {
    runBlocking {
        withContext(Dispatchers.IO) {
            massiveRun {
                withContext(counterContext) { // 원하는 곳에 counterContext를 호출하면 된다.
                    counter++ // 값을 가져와서 더해주는거기 때문 값을 가져올때 다른쓰레드에서 증가 시킬수 있고 아닐 수 있다.
                }
            }
        }
    }
    println("Counter = $counter") // 여러 쓰레드간 정보가 다르기 떄문에 값이 10만이 아니고 다르다
}
```

- 뮤텍스 (Mutual exclusion)

공유 상태를 수정할 때 임계 영역을 이용하게 하며, 임계 영역에 동시에 접근하는 것을 허용하지 않는다.

```aidl
suspend fun massiveRun(action: suspend () -> Unit) {
    val n = 100 // 시작할 코루틴의 갯수
    val k = 1000 // 코루틴 내에서 반복할 횟수
    val elapsed = measureTimeMillis {
        coroutineScope { // scope for coroutines
            repeat(n) {
                launch {
                    repeat(k) { action() }
                }
            }
        }
    }
}

val mutex = Mutex()
var counter = 0

fun main() {
    runBlocking {
        withContext(Dispatchers.IO) {
            massiveRun {
                mutex.withLock {
                    counter++
                }
            }
        }
    }
    println("Counter = $counter")
}
```


- 액터

액터가 독점적으로 자료를 가지며 그 자료를 다른 코루틴과 공유하지 않고 액터를 통해서만 접근 가능하게 만든다.

```aidl
sealed class CounterMsg
object IncCounter : CounterMsg()
class GetCounter(val response: CompletableDeferred<Int>) : CounterMsg()

fun CoroutineScope.counterActor() = actor<CounterMsg> {
    var counter = 0 // 액터 안에 상태를 캡슐화해두고 다른 코루틴이 접근하지 못하게 한다.

    for (msg in channel) { // 외부에서 보내는 것은 채널을 통해서만 받을 수 있다.(receive)
        when (msg) {
            is IncCounter -> counter++ // 증가시키는 신호.
            is GetCounter -> msg.response.complete(counter) // 현재 상태를 반환한다.
        }
    }
}

suspend fun massiveRun(action: suspend () -> Unit) {
    val n = 100 // 시작할 코루틴의 갯수
    val k = 1000 // 코루틴 내에서 반복할 횟수
    val elapsed = measureTimeMillis {
        coroutineScope { // scope for coroutines
            repeat(n) {
                launch {
                    repeat(k) { action() }
                }
            }
        }
    }
}

fun main() {
    runBlocking {
        val counter = counterActor()
        withContext(Dispatchers.Default) {
            massiveRun {
                counter.send(IncCounter)
            }
        }

        val response = CompletableDeferred<Int>()
        counter.send(GetCounter(response))
        println("Counter = ${response.await()}")
        counter.close()
    }
```