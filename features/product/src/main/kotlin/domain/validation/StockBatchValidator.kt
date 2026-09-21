package domain.validation

import domain.model.StockBatch
import domain.model.sync.SyncStockBatch
import result.DomainResult

class StockBatchValidator (
    private val validationRules: ValidationRules,
    private val locationValidator: LocationValidator,
) {
    fun validateStockBatch(batch: StockBatch): DomainResult<Unit> {
        val errors = mutableListOf<String>()

        errors.addAll(
            validateFields(
                item = batch,
                getId = { it.batchId.value },
                getSupplier = { it.supplier },
                getExpirationDate = { it.expirationDate },
                getCreatedAt = { it.createdAt },
                getLastUpdated = { it.lastUpdated }
            )
        )

        when (
            val locationResult = locationValidator.validateLocation(batch.location)
        ) {
            is DomainResult.Error -> errors.add(locationResult.message)
            is DomainResult.Success -> {}
        }

        return ValidationResult.formatResult(errors)
    }

    fun validateSyncStockBatch(batch: SyncStockBatch): DomainResult<Unit> {
        val errors = validateFields(
            item = batch,
            getId = { it.batchId.value },
            getSupplier = { it.supplier },
            getExpirationDate = { it.expirationDate },
            getCreatedAt = { it.createdAt },
            getLastUpdated = { it.lastUpdated }
        )

        return ValidationResult.formatResult(errors)
    }

    private fun <T> validateFields(
        item: T,
        getId: (T) -> String,
        getSupplier: (T) -> String?,
        getExpirationDate: (T) -> Long?,
        getCreatedAt: (T) -> Long,
        getLastUpdated: (T) -> Long,
    ): List<String> {
        val errors = mutableListOf<String>()

        errors.addAll(
            validationRules.validateExpirationDate(
                item = item,
                getId = getId,
                getCreatedAt = getCreatedAt,
                getExpirationDate = getExpirationDate
            )
        )
        
        errors.addAll(
            validationRules.validateStringLengthAndFormat(
                item = item,
                getId = getId,
                getFieldName = { "Supplier" },
                getValue = getSupplier,
                maxLength = 128,
                allowNewLines = false,
                allowSpace = true
            )
        )

        errors.addAll(
            validationRules.validateTimestamps(
                item = item,
                getId = getId,
                getCreatedAt = getCreatedAt,
                getLastUpdated = getLastUpdated
            )
        )

        return errors
    }
}