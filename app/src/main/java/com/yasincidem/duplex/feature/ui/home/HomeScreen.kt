package com.yasincidem.duplex.feature.ui.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.yasincidem.duplex.R
import com.yasincidem.duplex.common.constant.Countries
import com.yasincidem.duplex.common.constant.Country
import com.yasincidem.duplex.common.constant.DefaultCountry
import com.yasincidem.duplex.ui.theme.MainDarkBlue
import com.yasincidem.duplex.ui.theme.MainLightOrange
import com.yasincidem.duplex.ui.theme.MainOrange
import com.yasincidem.duplex.ui.theme.Translucent

@Composable
fun HomeScreen() {

    val systemUiController = rememberSystemUiController()
    val isDarkMode = isSystemInDarkTheme()
    val barColor = MaterialTheme.colors.background
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    val phoneState = rememberSaveable { mutableStateOf("") }
    val dropdownState = remember { mutableStateOf(false) }
    val countryState = remember { mutableStateOf(DefaultCountry) }

    val privacyPolicy = stringResource(id = R.string.privacy_policy_title)
    val termsAndConditions = stringResource(id = R.string.terms_and_conditions_title)
    val LOGIN_WARNING_TEXT = buildAnnotatedString {
        append("By continuing I agree the ")
        pushStringAnnotation(tag = privacyPolicy, annotation = privacyPolicy)
        withStyle(SpanStyle(color = MainOrange)) {
            append(privacyPolicy)
        }
        pop()
        append(" and ")
        pushStringAnnotation(tag = termsAndConditions, annotation = termsAndConditions)
        withStyle(SpanStyle(color = MainOrange)) {
            append(termsAndConditions)
        }
        pop()
    }

    val BRANDING_TEXT = buildAnnotatedString {
        append("Speed up your ")
        withStyle(
            SpanStyle(
                textDecoration = TextDecoration.Underline,
                fontStyle = FontStyle.Italic,
                color = MainOrange
            )
        ) {
            append("communication")
        }
        append(" with ")
        withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = MainOrange)) {
            append("duplex")
        }
    }

    SideEffect {
        systemUiController.apply {
            setSystemBarsColor(
                color = Translucent,
                darkIcons = isDarkMode.not()
            )
            setNavigationBarColor(
                color = barColor,
                darkIcons = isDarkMode.not()
            )
        }
    }

    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsWithImePadding()
    ) {

        Column(
            modifier = Modifier.verticalScroll(scrollState),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Image(
                    painter = painterResource(id = R.drawable.login_illustration),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
                Text(text = BRANDING_TEXT)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
                    value = phoneState.value,
                    onValueChange = { input ->
                        if (input.isEmpty()) {
                            phoneState.value = input
                        } else {
                            phoneState.value = when (input.toLongOrNull()) {
                                null -> phoneState.value
                                else -> input
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    label = {
                        Text(text = "Username")
                    },
                    textStyle = MaterialTheme.typography.body1.copy(color = MainOrange),
                    singleLine = true,
                    shape = CircleShape
                )

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                        .height(intrinsicSize = IntrinsicSize.Max),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        16.dp,
                        Alignment.CenterHorizontally
                    )
                ) {

                    OutlinedButton(
                        modifier = Modifier.fillMaxHeight(),
                        onClick = { dropdownState.value = true },
                        shape = CircleShape
                    ) {

                        CountryCode(country = countryState.value)

                        DropdownMenu(
                            expanded = dropdownState.value,
                            onDismissRequest = { dropdownState.value = false },
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            Countries.forEach { country ->
                                DropdownMenuItem(
                                    onClick = {
                                        countryState.value = country
                                        dropdownState.value = false
                                    }
                                ) {
                                    CountryCode(country = country)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = phoneState.value,
                        onValueChange = { input ->
                            if (input.isEmpty()) {
                                phoneState.value = input
                            } else {
                                phoneState.value = when (input.toLongOrNull()) {
                                    null -> phoneState.value
                                    else -> input
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        label = {
                            Text(text = "Phone Number")
                        },
                        textStyle = MaterialTheme.typography.body1.copy(color = MainOrange),
                        singleLine = true,
                        shape = CircleShape
                    )
                }

                ClickableText(
                    text = LOGIN_WARNING_TEXT,
                    style = MaterialTheme.typography.subtitle2.copy(
                        color = Color.LightGray,
                        fontSize = 11.sp,
                    ),
                    onClick = { offset ->
                        LOGIN_WARNING_TEXT.getStringAnnotations(privacyPolicy, offset, offset)
                            .firstOrNull()?.let {
                            }

                        LOGIN_WARNING_TEXT.getStringAnnotations(termsAndConditions, offset, offset)
                            .firstOrNull()?.let {
                            }
                    }
                )

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    onClick = {
                        Toast.makeText(
                            context,
                            "${countryState.value.countryCode}${phoneState.value}",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    shape = CircleShape
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 8.dp),
                        text = "Continue with your number",
                        style = MaterialTheme.typography.body2.copy(
                            color = if (isDarkMode) MainDarkBlue else MainLightOrange,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun CountryCode(country: Country) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painterResource(id = country.flagDrawableRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(text = country.getCountryCodeString())
    }
}