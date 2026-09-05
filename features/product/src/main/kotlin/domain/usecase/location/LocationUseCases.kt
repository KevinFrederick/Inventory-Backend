package domain.usecase.location

data class LocationUseCases(
    val getAllLocation: GetAllLocationUseCase,
    val getLocationById: GetLocationByIdUseCase,
    val insertLocation: InsertLocationUseCase,
    val updateLocation: UpdateLocationUseCase,
    val deleteLocation: DeleteLocationUseCase,
)
