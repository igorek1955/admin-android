package com.jarlingwar.adminapp.ui.screens.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.jarlingwar.adminapp.R
import com.jarlingwar.adminapp.ui.common.AnimatedImage
import com.jarlingwar.adminapp.ui.common.LoadingDialog
import com.jarlingwar.adminapp.ui.common.MyInputField
import com.jarlingwar.adminapp.ui.common.MyPasswordField
import com.jarlingwar.adminapp.ui.common.MySnack
import com.jarlingwar.adminapp.ui.common.PagerIndicator
import com.jarlingwar.adminapp.ui.common.PrimaryButton
import com.jarlingwar.adminapp.ui.common.TopBar
import com.jarlingwar.adminapp.ui.theme.AdminAppTheme
import com.jarlingwar.adminapp.ui.theme.Type
import com.jarlingwar.adminapp.ui.theme.adminColors
import com.jarlingwar.adminapp.ui.theme.paddingPrimaryStartEnd
import com.jarlingwar.adminapp.ui.theme.adminDimens
import com.jarlingwar.adminapp.ui.view_models.AuthViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    navigateToMain: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackState = remember { SnackbarHostState() }
    val pagerState = rememberPagerState()
    val ctx = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.setupGoogleAuth(ctx)
    }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { viewModel.processGoogleSignInResult(it) }
            } else {
                val message = "${ctx.getString(R.string.auth_failed)} code: ${result.resultCode}"
                coroutineScope.launch { snackState.showSnackbar(message) }
            }
        }

    val titles =
        listOf(stringResource(id = R.string.login), stringResource(id = R.string.registration))
    val title = if (viewModel.screenType == AuthViewModel.Screen.AUTH) {
        if (pagerState.currentPage == 0) titles[0] else titles[1]
    } else ""
    Scaffold(
        snackbarHost = { MySnack(snackState) },
        topBar = { TopBar(title = title) }
    ) {
        when (viewModel.screenType) {
            AuthViewModel.Screen.LOGIN_SUCCESS -> { navigateToMain() }
            AuthViewModel.Screen.RESET_SUCCESS -> { ResetSuccess(navigateToMain) }
            AuthViewModel.Screen.REGISTRATION_SUCCESS -> { RegistrationSuccess(navigateToMain) }
            AuthViewModel.Screen.AUTH -> { Auth(launcher, viewModel, titles, pagerState) }
            AuthViewModel.Screen.MISSING_PERMISSIONS -> { ActivationRequired { viewModel.retryLogin() } }
        }
    }

    if (viewModel.isLoading) { LoadingDialog() }

    if (viewModel.error != null) {
        val error = viewModel.error!!
        val message = if (error.resId > 0) stringResource(id = error.resId)
        else error.message ?: ""
        LaunchedEffect(error) {
            snackState.showSnackbar(message)
        }
    }
}

@Composable
private fun ResetSuccess(navigateAction: () -> Unit) {
    PlaceholderScreen(
        text = stringResource(id = R.string.reset_instruction),
        stringResource(R.string.go_to_main),
        animResId = R.raw.anim_success,
        navigateAction
    )
}

@Composable
private fun RegistrationSuccess(navigateAction: () -> Unit) {
    PlaceholderScreen(
        text = stringResource(id = R.string.activation_instruction),
        stringResource(R.string.go_to_main),
        animResId = R.raw.anim_success,
        navigateAction
    )
}


@Composable
private fun ActivationRequired(retryLogin: () -> Unit) {
    PlaceholderScreen(
        text = stringResource(id = R.string.activation_required),
        stringResource(R.string.retry_login),
        animResId = R.raw.anim_loading,
        retryLogin
    )
}


@Composable
private fun PlaceholderScreen(
    text: String,
    buttonText: String,
    animResId: Int,
    action: (() -> Unit)? = null
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier
                .align(Alignment.Center)
                .offset(y = (-100).dp)
                .paddingPrimaryStartEnd(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedImage(resId = animResId, size = 80.dp)
            Spacer(Modifier.height(10.dp))
            Text(text, style = Type.Subtitle2, textAlign = TextAlign.Center)
            Spacer(Modifier.height(10.dp))
            action?.let {
                PrimaryButton(buttonText) { it() }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun Auth(
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    viewModel: AuthViewModel,
    titles: List<String>,
    pagerState: PagerState
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(10.dp))
        Text(
            modifier = Modifier.paddingPrimaryStartEnd(),
            text = stringResource(R.string.auth_required_to_continue),
            textAlign = TextAlign.Center,
            style = Type.Subtitle1
        )
        Spacer(Modifier.height(10.dp))
        PagerIndicator(currentPage = pagerState.currentPage, titles = titles) {
            coroutineScope.launch { pagerState.scrollToPage(it) }
        }
        Spacer(Modifier.height(10.dp))
        HorizontalPager(
            count = titles.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = true
        ) { currentPage ->
            when (currentPage) {
                0 -> LoginTab(launcher, viewModel)
                else -> RegistrationTab(launcher, viewModel)
            }
        }
    }
}

@Composable
private fun LoginTab(
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>?,
    viewModel: AuthViewModel?
) {
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val fieldError = viewModel?.fieldError
    Column(
        modifier = Modifier
            .fillMaxSize()
            .paddingPrimaryStartEnd()
            .padding(top = 5.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GoogleButton(launcher = launcher, viewModel = viewModel)
        Spacer(Modifier.height(10.dp))
        MyInputField(
            textVal = email,
            label = stringResource(R.string.email),
            errorText = if (fieldError == AuthViewModel.FieldError.INCORRECT_EMAIL)
                stringResource(fieldError.resId) else "",
            placeholder = stringResource(R.string.input_email),
            startIcon = R.drawable.ic_mail
        )
        MyPasswordField(
            textVal = password,
            label = stringResource(id = R.string.password),
            errorText = if (fieldError == AuthViewModel.FieldError.PASSWORD)
                stringResource(fieldError.resId) else "",
            placeholder = stringResource(R.string.input_password),
        )
        Spacer(Modifier.height(10.dp))
        PrimaryButton(
            text = stringResource(id = R.string.authorize),
            isEnabled = email.value.isNotEmpty() && password.value.isNotEmpty()
        ) { viewModel?.verifyUserLoginInfo(email.value, password.value) }
        TextButton(onClick = {
            focusManager.clearFocus()
            viewModel?.resetPassword(email.value) })
        {
            Text(
                text = stringResource(id = R.string.password_reset),
                style = Type.Body1,
                color = MaterialTheme.adminColors.textSecondary
            )
        }
    }
}

@Composable
private fun GoogleButton(
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>?,
    viewModel: AuthViewModel?
) {
    Button(modifier = Modifier
        .fillMaxWidth()
        .height(MaterialTheme.adminDimens.buttonHeight),
        onClick = {
            viewModel?.getSignInIntent()?.let { launcher?.launch(it) }
        }) {
        Image(
            painter = painterResource(id = R.drawable.ic_google),
            modifier = Modifier
                .size(24.dp)
                .padding(end = 5.dp),
            contentDescription = "google"
        )
        Text(
            text = stringResource(R.string.google_sign_in),
            style = Type.Subtitle2
        )
    }
}

@Composable
private fun RegistrationTab(
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>?,
    viewModel: AuthViewModel?
) {
    val name = rememberSaveable { mutableStateOf("") }
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val password2 = rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val fieldError = viewModel?.fieldError
    Column(
        modifier = Modifier
            .fillMaxSize()
            .paddingPrimaryStartEnd()
            .padding(top = 5.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GoogleButton(launcher = launcher, viewModel = viewModel)
        Spacer(Modifier.height(10.dp))
        MyInputField(
            textVal = name,
            label = stringResource(R.string.name),
            errorText = if (fieldError == AuthViewModel.FieldError.NAME)
                stringResource(fieldError.resId) else "",
            placeholder = stringResource(R.string.name_placeholder),
            startIcon = R.drawable.ic_mail
        )
        MyInputField(
            textVal = email,
            label = stringResource(R.string.email),
            errorText = if (fieldError == AuthViewModel.FieldError.INCORRECT_EMAIL)
                stringResource(fieldError.resId) else "",
            placeholder = stringResource(R.string.input_email),
            startIcon = R.drawable.ic_mail
        )
        MyPasswordField(
            textVal = password,
            label = stringResource(id = R.string.password),
            errorText = if (fieldError == AuthViewModel.FieldError.PASSWORD)
                stringResource(fieldError.resId) else "",
            placeholder = stringResource(R.string.input_password),
        )
        MyPasswordField(
            textVal = password2,
            label = stringResource(id = R.string.password_repeat),
            errorText = if (fieldError == AuthViewModel.FieldError.PASSWORD_MATCH)
                stringResource(fieldError.resId) else "",
            placeholder = stringResource(R.string.input_password_2),
        )
        Spacer(Modifier.height(10.dp))
        PrimaryButton(
            text = stringResource(id = R.string.create_account),
            isEnabled = email.value.isNotEmpty() && password.value.isNotEmpty()
        ) {
            focusManager.clearFocus()
            viewModel?.registerNewUser(
                email.value, password.value, password2.value, name.value
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    AdminAppTheme {
        LoginTab(launcher = null, viewModel = null)
    }
}

@Preview(showBackground = true)
@Composable
private fun RegistrationPreview() {
    AdminAppTheme {
        RegistrationTab(launcher = null, viewModel = null)
    }
}

@Preview(showBackground = true)
@Composable
private fun SuccessPreview() {
    AdminAppTheme {
        RegistrationSuccess() { }
    }
}