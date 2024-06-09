package io.aoriani.recipe.ui.screens.assistant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Assistant(uiState: AssistantUiState, onNavigateUp: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Assistant") }, navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        })
    }
    ) { innerPadding ->

        when (uiState) {
            is AssistantUiState.Loading -> AssistantLoading(Modifier.padding(innerPadding))
            is AssistantUiState.Loaded -> AssistantReady(uiState)
        }
    }
}


@Composable
fun AssistantLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(modifier = Modifier.size(100.dp))
        Text(
            "Loading Assistant...",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AssistantReady(uiState: AssistantUiState.Loaded) {
    val pagerState = rememberPagerState(pageCount = { uiState.stepsUrls.size + 1 })
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = false
    ) { page ->
        //Page Zero is for the ingredients instructions
        //Steps start after
        if (page == 0) {
            Text("Ingredients")
        } else {
            AsyncImage(
                model = uiState.stepsUrls[page - 1],
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )
        }
    }


    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { currentPage ->
                delay(3000)
                if (currentPage < pagerState.pageCount -1) {
                    pagerState.scrollToPage(currentPage + 1)
                }
            }
    }

}