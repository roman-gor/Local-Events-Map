package com.gorman.detailsevent.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.gorman.detailsevent.R
import com.gorman.detailsevent.components.BottomBlock
import com.gorman.detailsevent.components.MapEventInfoRow
import com.gorman.detailsevent.components.TopBlock
import com.gorman.detailsevent.contextUtils.openBrowser
import com.gorman.detailsevent.contextUtils.openMap
import com.gorman.detailsevent.contextUtils.shareContent
import com.gorman.detailsevent.states.DetailsActions
import com.gorman.detailsevent.states.DetailsScreenState
import com.gorman.detailsevent.states.DetailsScreenUiEvent
import com.gorman.detailsevent.viewmodels.DetailsViewModel
import com.gorman.ui.components.ErrorDataScreen
import com.gorman.ui.components.LoadingStub
import com.gorman.ui.states.MapUiEvent
import com.gorman.ui.theme.LocalEventsMapTheme

@Composable
fun DetailsEventScreenEntry(
    mapUiEvent: MapUiEvent,
    modifier: Modifier = Modifier,
    detailsViewModel: DetailsViewModel = hiltViewModel()
) {
    val uiState by detailsViewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is DetailsScreenState.Error.NoNetwork -> ErrorDataScreen(stringResource(R.string.noNetwork))
        is DetailsScreenState.Error.NotFound -> ErrorDataScreen(stringResource(R.string.eventNotFound))
        is DetailsScreenState.Error.Unknown -> ErrorDataScreen(stringResource(R.string.errorDataLoading))
        DetailsScreenState.Loading -> LoadingStub()
        is DetailsScreenState.Success -> {
            Log.d("State", "${state.event}")
            DetailsEventScreen(
                mapUiEvent = mapUiEvent,
                onUiEvent = detailsViewModel::onUiEvent,
                modifier = modifier
            )
        }
    }
}

@Composable
fun DetailsEventScreen(
    mapUiEvent: MapUiEvent,
    onUiEvent: (DetailsScreenUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val painter = rememberAsyncImagePainter(mapUiEvent.photoUrl)
    val imageState by painter.state.collectAsStateWithLifecycle()
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            TopBlock(
                name = mapUiEvent.name,
                isFavourite = mapUiEvent.isFavourite,
                imageState = imageState,
                detailsActions = DetailsActions(
                    onFavouriteClick = { onUiEvent(DetailsScreenUiEvent.OnFavouriteClick(mapUiEvent.id)) },
                    onLocationClick = { openMap(context, mapUiEvent.coordinates) },
                    onShareClick = { shareContent(context, mapUiEvent.link) },
                    onLinkClick = { openBrowser(context, mapUiEvent.link) }
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = mapUiEvent.description ?: "",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = LocalEventsMapTheme.dimens.paddingLarge)
                    .weight(1f)
                    .verticalScroll(state = rememberScrollState())
            )
            MapEventInfoRow(
                address = mapUiEvent.address,
                timestamp = mapUiEvent.date,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LocalEventsMapTheme.dimens.paddingLarge)
            )
            Spacer(modifier = Modifier.height(4.dp))
            BottomBlock(
                cityName = mapUiEvent.cityName?.lowercase() ?: "",
                category = mapUiEvent.category?.lowercase() ?: "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = LocalEventsMapTheme.dimens.paddingExtraLarge)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
