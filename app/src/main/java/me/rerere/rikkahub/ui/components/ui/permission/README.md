# Permission library usage

A complete Android runtime permission library that automatically shows a dialog explaining why a permission is requested.

## Features

- 🎯 **Declarative API**: Compose-style declarative permission handling
- 🔄 **State management**: tracks permission state changes automatically
- 💬 **Smart dialog**: shows the reason for a permission request automatically
- 🎨 **Material Design 3**: follows the latest design guidelines
- 🔧 **Flexible configuration**: required and optional permissions
- 🔄 **Lifecycle aware**: refreshes permission states when the app returns to the foreground
- ⚡ **Permanent denial handling**: handles permanently denied permissions and guides the user to the settings page

## Basic usage

### 1. Single permission

```kotlin
@Composable
fun CameraScreen() {
    val cameraPermission = rememberPermissionState(
        permission = Manifest.permission.CAMERA,
        usage = {
            Text(
                text = "Camera permission is needed to take photos and record videos",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        required = true
    )

    PermissionManager(permissionState = cameraPermission) {
        PermissionCheck(
            permissionState = cameraPermission,
            onGranted = {
                // Permission granted: show the camera UI
                CameraContent()
            },
            onDenied = { state ->
                // Permission denied: show custom content
                Button(onClick = { state.requestPermissions() }) {
                    Text("Request camera permission")
                }
            }
        )
    }
}
```

### 2. Multiple permissions

```kotlin
@Composable
fun MediaScreen() {
    val mediaPermissions = rememberPermissionState(
        permissions = setOf(
            PermissionInfo(
                permission = Manifest.permission.CAMERA,
                usage = {
                    Text("Camera permission is needed to take photos")
                },
                required = true
            ),
            PermissionInfo(
                permission = Manifest.permission.RECORD_AUDIO,
                usage = {
                    Text("Microphone permission is needed to record videos")
                },
                required = false
            ),
            PermissionInfo(
                permission = Manifest.permission.READ_EXTERNAL_STORAGE,
                usage = {
                    Text("Storage permission is needed to save media files")
                },
                required = true
            )
        )
    )

    PermissionManager(permissionState = mediaPermissions) {
        when {
            mediaPermissions.allRequiredPermissionsGranted -> {
                // All required permissions granted
                MediaContent(hasAudioPermission = mediaPermissions.permissionStates[Manifest.permission.RECORD_AUDIO] == PermissionStatus.Granted)
            }
            else -> {
                // Show the permission request UI
                Column {
                    Text("Permissions are required to continue")
                    Button(onClick = { mediaPermissions.requestPermissions() }) {
                        Text("Request permissions")
                    }
                }
            }
        }
    }
}
```

### 3. Checking permission state

```kotlin
@Composable
fun PermissionStatusExample() {
    val permissionState = rememberPermissionState(...)

    // Check all permissions
    if (permissionState.allPermissionsGranted) {
        Text("All permissions granted")
    }

    // Check required permissions
    if (permissionState.allRequiredPermissionsGranted) {
        Text("Required permissions granted")
    }

    // Check a single permission
    when (permissionState.permissionStates[Manifest.permission.CAMERA]) {
        PermissionStatus.Granted -> Text("Camera permission granted")
        PermissionStatus.Denied -> Text("Camera permission denied")
        PermissionStatus.DeniedPermanently -> Text("Camera permission permanently denied")
        PermissionStatus.NotRequested -> Text("Camera permission not requested")
        null -> Text("Permission state unknown")
    }
}
```

### 4. Manual permission handling

```kotlin
@Composable
fun ManualPermissionExample() {
    val permissionState = rememberPermissionState(...)

    Column {
        Button(onClick = {
            // Request all permissions
            permissionState.requestPermissions()
        }) {
            Text("Request all permissions")
        }

        Button(onClick = {
            // Request a specific permission
            permissionState.requestPermission(Manifest.permission.CAMERA)
        }) {
            Text("Request camera permission")
        }

        Button(onClick = {
            // Open the app settings
            permissionState.openAppSettings()
        }) {
            Text("Open settings")
        }

        Button(onClick = {
            // Update permission states
            permissionState.updatePermissionStates()
        }) {
            Text("Refresh permission states")
        }
    }
}
```

## API reference

### PermissionInfo

```kotlin
data class PermissionInfo(
    val permission: String,              // Android permission string
    val usage: @Composable () -> Unit,   // explanation of why the permission is used
    val required: Boolean = false        // whether the permission is required
)
```

### PermissionState

Main properties:
- `permissionStates: Map<String, PermissionStatus>` - permission state map
- `allPermissionsGranted: Boolean` - whether every permission is granted
- `allRequiredPermissionsGranted: Boolean` - whether every required permission is granted
- `deniedPermissions: List<PermissionInfo>` - list of denied permissions

Main methods:
- `requestPermissions()` - request every permission not yet granted
- `requestPermission(permission: String)` - request a specific permission
- `updatePermissionStates()` - update permission states
- `refreshPermissionStates()` - force a refresh of permission states (used on lifecycle changes)
- `openAppSettings()` - open the app settings page

### PermissionStatus

```kotlin
enum class PermissionStatus {
    NotRequested,      // not requested yet
    Granted,           // granted
    Denied,            // denied, but can be requested again
    DeniedPermanently  // denied and the user chose "Don't ask again"
}
```

## Notes

1. **Activity requirement**: `rememberPermissionState` must be used inside a `ComponentActivity`
2. **Permission declaration**: make sure the permissions are declared in `AndroidManifest.xml`
3. **Lifecycle aware**: permission states refresh automatically when the app returns to the foreground
4. **Dialog**: the explanation dialog is shown automatically when needed
5. **Settings shortcut**: permanently denied permissions offer a shortcut to the settings page
6. **State sync**: after the user changes a permission in the settings, the state updates as soon as they return to the app

## Best practices

1. **Request on demand**: only request a permission when the feature needs it
2. **Explain clearly**: describe the purpose of the permission clearly in `usage`
3. **Classify**: set the `required` flag to separate required and optional permissions
4. **Graceful fallback**: provide a reduced experience when a permission is denied
5. **Persistent state**: permission states survive configuration changes