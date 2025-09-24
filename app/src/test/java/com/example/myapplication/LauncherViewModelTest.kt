package com.example.myapplication

import android.app.Application
import android.content.Context
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import com.example.myapplication.Utils.toEntity
import com.example.myapplication.data.local.entity.AppInfo
import com.example.myapplication.data.local.entity.LauncherItem
import com.example.myapplication.data.local.entity.LauncherItemEntity
import com.example.myapplication.data.local.entity.LauncherItemType
import com.example.myapplication.data.repo.LauncherRepository
import com.example.myapplication.ui.main.LauncherViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals


@OptIn(ExperimentalCoroutinesApi::class)
class LauncherViewModelTest {

    private lateinit var viewModel: LauncherViewModel
    private val repo: LauncherRepository = mockk(relaxed = true)
    private val app: Application = mockk(relaxed = true)

    @Before
    fun setup() {
        viewModel = LauncherViewModel(repo, app)
    }

    @Test
    fun `loadInstalledApps updates installedApps state`() = runTest {
        // Mock LauncherApps
        val launcherApps: LauncherApps = mockk()
        val activityInfoList = listOf<LauncherActivityInfo>(
            mockk {
                every { label } returns "Gmail"
                every { applicationInfo.packageName } returns "com.gmail"
                every { getIcon(any()) } returns mockk()
                every { user } returns mockk()
            },
            mockk {
                every { label } returns "Maps"
                every { applicationInfo.packageName } returns "com.maps"
                every { getIcon(any()) } returns mockk()
                every { user } returns mockk()
            }
        )

        every { app.getSystemService(Context.LAUNCHER_APPS_SERVICE) } returns launcherApps
        every { launcherApps.getActivityList(null, any()) } returns activityInfoList

        viewModel.loadInstalledApps()

        advanceUntilIdle() // Wait for coroutine

        val installedApps = viewModel.installedApps.value
        assertEquals(2, installedApps.size)
        assertEquals("Gmail", installedApps[0].label)
        assertEquals("Maps", installedApps[1].label)
    }

    @Test
    fun `insertItem calls repo insert`() = runTest {
        val item = LauncherItemEntity(
            id = 1,
            label = "Test",
            type = LauncherItemType.APP,
            apps = listOf()
        )

        viewModel.insertItem(item)
        advanceUntilIdle() // Wait for coroutine inside viewModelScope

        coVerify { repo.insertItem(item) }
    }

    @Test
    fun `deleteItem calls repo delete`() = runTest {
        val item = LauncherItemEntity(
            id = 1,
            label = "Test",
            apps = emptyList(),
            type = LauncherItemType.APP
        )
        viewModel.deleteItem(item)
        advanceUntilIdle()
        coVerify { repo.deleteItem(item) }
    }

    @Test
    fun `updateFolderName calls repo updateFolderName`() = runTest {
        viewModel.updateFolderName(1, "New Name")
        advanceUntilIdle()
        coVerify { repo.updateFolderName(1, "New Name") }
    }

    @Test
    fun `handleDrop merges apps into new folder`() = runTest {
        val app1 = LauncherItem.App(AppInfo("App1", mockk(), mockk(), mockk()))
        val app2 = LauncherItem.App(AppInfo("App2", mockk(), mockk(), mockk()))

        viewModel.handleDrop(app1, app2)
        advanceUntilIdle()

        // verify delete & insert called
        coVerify { repo.deleteItem(app1.toEntity()) }
        coVerify { repo.deleteItem(app2.toEntity()) }
        coVerify { repo.insertItem(match { it.label == "New Folder" }) }
    }
}
