@Composable
fun YandexMapView(){
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    DisposableEffect(Unit) {
        mapView.onStart()
        MapKitFactory.getInstance().onStart()

        onDispose {
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }
    val locationPoint = Point(53.908775, 27.586246)
    mapView.mapWindow.map.move(CameraPosition(
        locationPoint,
        15.0f,
        0.0f,
        0.0f
    ))
    val imageProvider = ImageProvider.fromResource(context, R.drawable.ic_marker)
    mapView.mapWindow.map.mapObjects.addPlacemark().apply{
        geometry = locationPoint
        setIcon(imageProvider)
    }
    AndroidView(
        factory = { mapView },
        modifier = Modifier.fillMaxWidth()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
    )
}

@Composable
fun Images(onClick: () -> Unit){
    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        modifier = Modifier
            .height(220.dp)
            .padding(top = 8.dp, start = 16.dp, end = 16.dp)) {
        items(imagesList){
            img->
            ImageItem(image = img, onClick = {onClick})
        }
    }
}
