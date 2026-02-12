package com.gorman.feature.events.impl.navigation

import com.gorman.feature.details.api.DetailsScreenNavKey
import com.gorman.navigation.navigator.Navigator

class EventsNavDelegate(val navigator: Navigator) {
    fun navigateToDetails(id: String) { navigator.navigateTo(DetailsScreenNavKey(id)) }
}
