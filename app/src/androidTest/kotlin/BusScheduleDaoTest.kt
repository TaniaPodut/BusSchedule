import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.busschedule.data.AppDatabase
import com.example.busschedule.data.BusSchedule
import com.example.busschedule.data.BusScheduleDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class BusScheduleDaoTest {
    private lateinit var busScheduleDao: BusScheduleDao
    private lateinit var appDatabase: AppDatabase
    private val busSchedule1 = BusSchedule(1, "Main Street", 1617202800)
    private val busSchedule2 = BusSchedule(2, "Park Street", 1617203520)
    private val busSchedule11 = BusSchedule(11, "Main Street", 1617210000)
    private val busSchedulesByStopName = listOf(
        BusSchedule(1, "Main Street", 1617202800),
        BusSchedule(11, "Main Street", 1617210000)
    )
    private val updatedSchedule = BusSchedule(1, "Oak Drive", 1617202800)
    private val listOfSchedules = listOf(
    BusSchedule(1, "Main Street", 1617202800),
    BusSchedule(2, "Park Street", 1617203520)
)

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        busScheduleDao = appDatabase.busScheduleDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        appDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsScheduleIntoDB() = runBlocking {
        addOneScheduleToDb()
        val allSchedules = busScheduleDao.getAll().first()
        assertEquals(allSchedules[0], busSchedule1)
    }

    @Test
    @Throws(Exception::class)
    fun daoDelete_deletesScheduleFromDB() = runBlocking {
    addTwoBusSchedulesToDb()
    removeOneScheduleFromDb()
    val allSchedules = busScheduleDao.getAll().first()
    assertEquals(allSchedules[0], busSchedule2)
}

    @Test
    @Throws(Exception::class)
    fun daoUpdate_updatesScheduleInDB() = runBlocking {
        addOneScheduleToDb()
        updateOneScheduleInDb()
        val allSchedules = busScheduleDao.getAll().first()
        assertEquals(allSchedules[0], updatedSchedule)
    }

    @Test
    @Throws(Exception::class)
    fun daoDelete_deletesAllSchedulesFromDB() = runBlocking {
        addTwoBusSchedulesToDb()
        removeAllSchedulesFromDb()
        val allSchedules = busScheduleDao.getAll().first()
        assertTrue(allSchedules.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun daoGet_getsAllSchedulesFromDB() = runBlocking {
        addTwoBusSchedulesToDb()
        val allSchedules = busScheduleDao.getAll().first()
        assertEquals(allSchedules, listOfSchedules)
    }

    @Test
    @Throws(Exception::class)
    fun daoGet_getsAllSchedulesFromDbByStopName() = runBlocking {
    addBusSchedulesToDb()
    val allSchedulesByStopName = busScheduleDao.getByStopName("Main Street").first()
    assertEquals(allSchedulesByStopName, busSchedulesByStopName)
    }

    private suspend fun addOneScheduleToDb() {
        busScheduleDao.insert(busSchedule1)
    }
    private suspend fun addTwoBusSchedulesToDb() {
        busScheduleDao.insert(busSchedule1)
        busScheduleDao.insert(busSchedule2)
    }
    private suspend fun removeOneScheduleFromDb() {
        busScheduleDao.delete(busSchedule1)
    }
    private suspend fun updateOneScheduleInDb() {
        busScheduleDao.update(updatedSchedule)
    }
    private suspend fun removeAllSchedulesFromDb() {
        busScheduleDao.deleteAllSchedules()
    }
    private suspend fun addBusSchedulesToDb() {
        busScheduleDao.insert(busSchedule1)
        busScheduleDao.insert(busSchedule11)
    }
}