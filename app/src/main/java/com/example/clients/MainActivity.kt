package com.example.clients

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.TextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.clients.data.ClientOrganisation
import com.example.clients.data.Data
import com.example.clients.ui.theme.ClientsTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClientsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ClientsTable()
                }
            }
        }
    }
}

@Composable
fun ClientsTable(
    viewModel: ClientsViewModel = hiltViewModel()
) {

    viewModel.getClientsList()
    val clientsList: State<List<Data>> =
        viewModel.filteredClientList.collectAsState(initial = emptyList())
    var expandedItem by remember { mutableStateOf<Data?>(null) }

    LazyColumn(
        modifier = Modifier
            .wrapContentWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        item {
            Row {
                Header(
                    title = stringResource(R.string.app_name),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
                SortByProperty(
                    viewModel.properties,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { selectedProperty: String ->
                    viewModel.sortByProperty(selectedProperty)
                }
            }
        }
        item {
            Search { searchText: String ->
                viewModel.searchByProperty(searchText)
            }
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }

        items(items = clientsList.value,
            key = { client -> client.id }) { client ->
            ClientRow(client,
                expanded = expandedItem == client,
                onClick = {
                    expandedItem = if (expandedItem == client) null else client
                })
        }
    }
}

@Composable
private fun Search(function: (String) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }

    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = text,
        maxLines = 1,
        onValueChange = {
            text = it
            function(text)
        },
        textStyle = TextStyle(color = LocalContentColor.current),
        label = { Text(stringResource(id = R.string.search)) }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SortByProperty(
    properties: List<String>,
    modifier: Modifier,
    function: (String) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            readOnly = true,
            value = selectedOptionText,
            onValueChange = {},
            label = { Text(stringResource(id = R.string.sort_by)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            properties.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                        function.invoke(selectedOptionText)
                    }
                ) {
                    Text(text = selectionOption)
                }
            }
        }
    }
}

@Composable
private fun Header(
    title: String, modifier: Modifier
) {
    Text(
        text = title,
        modifier = modifier.semantics { heading() },
        style = androidx.compose.material.MaterialTheme.typography.h6
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientRow(data: Data, expanded: Boolean, onClick: () -> Unit) {
    var rotationState by remember { mutableStateOf(0f) }

    TopicRowSpacer(expanded)
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = {
            rotationState = if (expanded) 0f else 90f
            onClick.invoke()
        },
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.firstName + " " + data.lastName,
                    fontSize = 14.sp
                )
                Image(
                    painterResource(R.drawable.ic_arrow_right),
                    contentDescription = "",
                    modifier = Modifier.rotate(rotationState)
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = data.id,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 14.sp
                )
                Text(
                    text = data.clientOrganisation.name,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 14.sp
                )
                Text(
                    text = data.status,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 14.sp
                )
                Text(
                    text = data.fixedLinePhone,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 14.sp
                )
                data.mobilePhone?.let {
                    Text(
                        text = it,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = data.created.toString(),
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 14.sp
                )
            }
        }
    }
    TopicRowSpacer(expanded)
}

@Composable
fun TopicRowSpacer(visible: Boolean) {
    AnimatedVisibility(visible = visible) {
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview
@Composable
fun HorizontalScrollLazyColumnPreview() {
    ClientRow(
        Data(
            Date(),
            "em",
            "fn",
            "phone",
            "id",
            "ln",
            "mobile",
            "status",
            ClientOrganisation("id", "company")
        ), true
    ) {}
}