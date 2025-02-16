package br.com.mobdhi.photosappvolvotest.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.mobdhi.photosappvolvotest.R

@Composable
fun Header(
    name: String,
    onNameChange: (String) -> Unit,
    age: String,
    onAgeChange: (String) -> Unit,
    date: String
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.name),
            value = name,
            onValueChange = onNameChange
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CustomTextField(
                label = stringResource(R.string.age),
                value = age,
                maxCharacters = 3,
                modifier = Modifier.weight(1f),
                onlyDigits = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                onValueChange = onAgeChange
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(R.string.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }

}

@Preview
@Composable
fun HeaderPreview() {
    val name = remember { mutableStateOf("Dhiessica") }
    val age = remember { mutableStateOf("24") }
    val date = remember { mutableStateOf("16/02/2025") }

    Header(
        name = name.value,
        age = age.value,
        date = date.value,
        onNameChange = {
            name.value = it
        },
        onAgeChange = {
            age.value = it
        }
    )
}