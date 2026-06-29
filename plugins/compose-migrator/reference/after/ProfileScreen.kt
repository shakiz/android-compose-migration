package com.example.app.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.app.R
import com.example.app.designsystem.AppButton
import com.example.app.designsystem.AppIcon
import com.example.app.designsystem.AppScaffold
import com.example.app.designsystem.AppTextField
import com.example.app.designsystem.AppTopBar

// Stateless screen: state hoisted to the ViewModel, callbacks out. No findViewById.
@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    ProfileScreen(
        profile = profile,
        onBack = onBack,
        onSave = viewModel::save,
    )
}

@Composable
fun ProfileScreen(
    profile: Profile,
    onBack: () -> Unit,
    onSave: (name: String, email: String, phone: String) -> Unit,
) {
    var name by remember(profile) { mutableStateOf(profile.name) }
    var email by remember(profile) { mutableStateOf(profile.email) }
    var phone by remember(profile) { mutableStateOf(profile.phone) }

    AppScaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.profile_title),
                onNavigationClick = onBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppTextField(
                value = name,
                onValueChange = { name = it },
                hint = stringResource(R.string.profile_name),
                leadingIcon = { AppIcon(painterResource(R.drawable.ic_person)) },
            )
            AppTextField(
                value = email,
                onValueChange = { email = it },
                hint = stringResource(R.string.profile_email),
                leadingIcon = { AppIcon(painterResource(R.drawable.ic_email)) },
            )
            AppTextField(
                value = phone,
                onValueChange = { phone = it },
                hint = stringResource(R.string.profile_phone),
                leadingIcon = { AppIcon(painterResource(R.drawable.ic_phone)) },
            )
            AppButton(
                text = stringResource(R.string.profile_save),
                onClick = { onSave(name, email, phone) },
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}
