import kotlinx.coroutines.*
import org.scibot.Interfaces
import java.util.concurrent.TimeUnit

class SimpleScheduler : Interfaces.Scheduler {
    companion object {
        val jobList = mutableListOf<Job>()
    }
    override suspend fun schedule(
        operation: Interfaces.Scheduler.() -> Unit,
        interval: Long,
        unit: TimeUnit,
        dispatcher: MainCoroutineDispatcher?
    ) {
        val job = CoroutineScope(dispatcher?.plus(SupervisorJob()) ?: (Dispatchers.Main + SupervisorJob())).launch {
            while (isActive) {
                operation()
                delay(unit.toMillis(interval))
            }
        }
        jobList.add(job)
    }
    override fun close() {
        jobList.forEach{it.cancel()}
        jobList.clear()
    }
}
