import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        when (this) {
            is LibraryExtension -> {
                buildFeatures {
                    compose = true
                }
            }
            is ApplicationExtension -> {
                buildFeatures {
                    compose = true
                }
            }
        }

        dependencies {
            val bom = libs.findLibrary("androidx.compose.bom").get()
            add("implementation", platform(bom))
            add("androidTestImplementation", platform(bom))
            add("implementation", libs.findBundle("compose").get())
            add("debugImplementation", libs.findLibrary("androidx.compose.ui.tooling").get())
            add("implementation", libs.findLibrary("androidx.compose.ui.tooling.preview").get())
            add("androidTestImplementation", libs.findLibrary("androidx.compose.ui.test.junit4").get())
        }
    }
}
