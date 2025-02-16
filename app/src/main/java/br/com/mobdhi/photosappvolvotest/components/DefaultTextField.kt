package br.com.mobdhi.photosappvolvotest.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.text.isDigitsOnly
import br.com.mobdhi.photosappvolvotest.R
import br.com.mobdhi.photosappvolvotest.ui.theme.PhotosAppVolvoTestTheme

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    label: String = "",
    value: String,
    onValueChange: (String) -> Unit,
    maxCharacters: Int = 100,
    onlyDigits: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    suffix: @Composable() (() -> Unit)? = null,
) {
    TextField(
        label = { Text(label) },
        value = value,
        onValueChange = {
            if ((!onlyDigits || it.isDigitsOnly()) && it.length <= maxCharacters) {
                onValueChange(it)
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.small,
        modifier = modifier,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        ),
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        suffix = suffix
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultTextFieldPreview() {
    PhotosAppVolvoTestTheme {
        var text by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_large))
        ) {
            CustomTextField(
                value = text,
                onValueChange = { text = it }
            )
        }
    }
}