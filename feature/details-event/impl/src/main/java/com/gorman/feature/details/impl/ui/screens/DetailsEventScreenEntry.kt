package com.gorman.feature.details.impl.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gorman.feature.details.impl.R
import com.gorman.feature.details.impl.ui.components.AboutEventSection
import com.gorman.feature.details.impl.ui.components.BlurImage
import com.gorman.feature.details.impl.ui.components.BottomSection
import com.gorman.feature.details.impl.ui.components.DateTimeSection
import com.gorman.feature.details.impl.ui.components.HeaderSection
import com.gorman.feature.details.impl.ui.components.TitleSection
import com.gorman.feature.details.impl.ui.contextUtils.openBrowser
import com.gorman.feature.details.impl.ui.contextUtils.openCalendar
import com.gorman.feature.details.impl.ui.contextUtils.openMap
import com.gorman.feature.details.impl.ui.contextUtils.shareContent
import com.gorman.feature.details.impl.ui.states.DetailsActions
import com.gorman.feature.details.impl.ui.states.DetailsScreenState
import com.gorman.feature.details.impl.ui.states.DetailsScreenUiEvent
import com.gorman.feature.details.impl.ui.viewmodels.DetailsViewModel
import com.gorman.navigation.navigator.LocalNavigator
import com.gorman.ui.components.ErrorDataScreen
import com.gorman.ui.components.LoadingStub
import com.gorman.ui.states.MapUiEvent
import com.gorman.ui.theme.LocalEventsMapTheme

@Composable
fun DetailsEventScreenEntry(
    modifier: Modifier = Modifier,
    detailsViewModel: DetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val navigator = LocalNavigator.current
    val uiState by detailsViewModel.uiState.collectAsStateWithLifecycle()
    when (val state = uiState) {
        is DetailsScreenState.Error.NoNetwork -> ErrorDataScreen(
            text = stringResource(R.string.noNetwork),
            onRetryClick = {}
        )
        is DetailsScreenState.Error.NotFound -> ErrorDataScreen(
            text = stringResource(R.string.eventNotFound),
            onRetryClick = {}
        )
        is DetailsScreenState.Error.Unknown -> ErrorDataScreen(
            text = stringResource(R.string.errorDataLoading),
            onRetryClick = {}
        )
        DetailsScreenState.Loading -> LoadingStub()
        is DetailsScreenState.Success -> {
            Log.d("State", "${state.event}")
            DetailsEventScreen(
                mapUiEvent = state.event,
                onUiEvent = detailsViewModel::onUiEvent,
                detailsActions = DetailsActions(
                    onCalendarClick = {
                        openCalendar(
                            context = context,
                            dateTime = state.event.date,
                            title = state.event.name,
                            description = state.event.description
                        )
                    },
                    onLocationClick = { openMap(context, state.event.coordinates) },
                    onShareClick = { shareContent(context, state.event.link) },
                    onLinkClick = { openBrowser(context, state.event.link) },
                    onNavigateBack = { navigator.goBack() }
                ),
                modifier = modifier
            )
        }
    }
}

@Composable
fun DetailsEventScreen(
    mapUiEvent: MapUiEvent,
    onUiEvent: (DetailsScreenUiEvent) -> Unit,
    detailsActions: DetailsActions,
    modifier: Modifier = Modifier
) {
    val backgroundColorValue = MaterialTheme.colorScheme.background.value

    Box(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 100.dp)
        ) {
            BlurImage(
                photoUrl = mapUiEvent.photoUrl,
                backgroundColorValue = backgroundColorValue,
                modifier = Modifier.fillMaxWidth().height(400.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .systemBarsPadding()
                    .padding(horizontal = LocalEventsMapTheme.dimens.paddingLarge)
            ) {
                TitleSection(
                    mapUiEvent = mapUiEvent,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                DateTimeSection(
                    mapUiEvent = mapUiEvent,
                    onCalendarClick = { detailsActions.onCalendarClick() },
                    onLocationClick = { detailsActions.onLocationClick() },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                AboutEventSection(
                    description = mapUiEvent.description ?: "",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        HeaderSection(
            name = mapUiEvent.name,
            onNavigateToBack = { detailsActions.onNavigateBack() },
            onShareClick = { detailsActions.onShareClick() },
            modifier = Modifier.fillMaxWidth()
                .padding(
                    horizontal = LocalEventsMapTheme.dimens.paddingExtraLarge,
                    vertical = LocalEventsMapTheme.dimens.paddingMedium
                )
                .systemBarsPadding().align(Alignment.TopCenter)
        )
        BottomSection(
            isLike = mapUiEvent.isFavourite ?: false,
            onLikeClick = { onUiEvent(DetailsScreenUiEvent.OnFavouriteClick(mapUiEvent.id)) },
            onLinkClick = { detailsActions.onLinkClick() },
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding()
                .padding(LocalEventsMapTheme.dimens.paddingExtraLarge)
                .height(58.dp)
                .align(Alignment.BottomCenter)
        )
    }
}
