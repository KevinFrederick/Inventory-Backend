package domain.validation

import domain.model.Location
import result.DomainResult
import validation.ValidationResult
import validation.ValidationRules

class LocationValidator {
    fun validateLocation(location: Location): DomainResult<Unit> {
        val errors = validateFields(
            item = location,
            getId = { it.locationId.value },
            getName = { it.name },
            getDescription = { it.description },
            getBarcode = { it.locationBarcode },
            getCreatedAt = { it.createdAt },
            getLastUpdated = { it.lastUpdated }
        )

        return ValidationResult.formatResult(errors)
    }

    private fun <T> validateFields(
        item: T,
        getId: (T) -> String,
        getName: (T) -> String,
        getDescription: (T) -> String?,
        getBarcode: (T) -> String?,
        getCreatedAt: (T) -> Long,
        getLastUpdated: (T) -> Long,
    ): List<String> {
        val errors = mutableListOf<String>()

        errors.addAll(
            ValidationRules.validateStringLengthAndFormat(
                item = item,
                getId = getId,
                getFieldName = { "Name" },
                getValue = getName,
                maxLength = 255,
                allowNewLines = false,
                allowSpace = true
            )
        )

        errors.addAll(
            ValidationRules.validateStringLengthAndFormat(
                item = item,
                getId = getId,
                getFieldName = { "Description" },
                getValue = getDescription,
                maxLength = 500,
                allowNewLines = true,
                allowSpace = true
            )
        )

        errors.addAll(
            ValidationRules.validateStringLengthAndFormat(
                item = item,
                getId = getId,
                getFieldName = { "Location Barcode" },
                getValue = getBarcode,
                maxLength = 64,
                allowNewLines = false,
                allowSpace = false
            )
        )

        errors.addAll(
            ValidationRulesProduct.validateBarcodeAsciiNoSpaces(
                item = item,
                getId = getId,
                getFieldName = { "Location Barcode" },
                getValue = getBarcode,
            )
        )

        errors.addAll(
            ValidationRules.validateTimestamps(
                item = item,
                getId = getId,
                getCreatedAt = getCreatedAt,
                getLastUpdated = getLastUpdated
            )
        )

        return errors
    }
}