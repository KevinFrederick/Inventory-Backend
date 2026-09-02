package data.repository

import DatabaseFactory.dbQuery
import data.local.table.CategoryTable
import data.mapper.toCategory
import domain.model.Category
import domain.model.CategoryId
import domain.repository.CategoryRepository
import domain.result.DomainResult
import domain.result.ErrorType
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

class CategoryRepositoryImpl: CategoryRepository {
    override suspend fun getAllCategory(): DomainResult<List<Category>> = dbQuery {
        try {
            val categoriesRows = CategoryTable
                .selectAll()
                .toList()

            val categories = categoriesRows.map { row ->
                row.toCategory()
            }

            DomainResult.Success(categories)
        } catch (e: Exception) {
            DomainResult.Error(e.message ?: "Unknown error", ErrorType.UNKNOWN)
        }
    }

    override suspend fun getCategoryById(categoryId: CategoryId): DomainResult<Category?> = dbQuery {
        try {
            val categoryRow = CategoryTable
                .selectAll()
                .where { CategoryTable.categoryId eq categoryId.value }
                .singleOrNull()

            if (categoryRow == null) return@dbQuery DomainResult.Success(null)

            val category = categoryRow.toCategory()

            DomainResult.Success(category)
        } catch (e: Exception) {
            DomainResult.Error(e.message ?: "Unknown error", ErrorType.UNKNOWN)
        }
    }

    override suspend fun insertCategory(category: Category): DomainResult<Unit> = dbQuery {
        try {
            CategoryTable.insert {
                it[categoryId] = category.categoryId.value
                it[name] = category.name
                it[description] = category.description
                it[createdAt] = category.createdAt
                it[lastUpdated] = category.lastUpdated
            }

            DomainResult.Success(Unit)
        } catch (e: ExposedSQLException) {
            val errorMessage = e.message ?: ""

            when{
                errorMessage.contains("category_name_key") ->
                    DomainResult.Error("Category already exists", ErrorType.CONFLICT)
                else ->
                    DomainResult.Error("Failed to saved category", ErrorType.UNKNOWN)
            }
        }
    }

    override suspend fun updateCategory(category: Category): DomainResult<Unit> = dbQuery {
        try {
            val updatedRowsCount = CategoryTable.update ({ CategoryTable.categoryId eq category.categoryId.value }) {
                it[name] = category.name
                it[description] = category.description
                it[lastUpdated] = category.lastUpdated
            }

            if (updatedRowsCount == 0) return@dbQuery DomainResult.Error("Category Not Found", ErrorType.NOT_FOUND)

            DomainResult.Success(Unit)
        } catch (e: ExposedSQLException) {
            val errorMessage = e.message ?: ""

            when{
                errorMessage.contains("category_name_key") ->
                    DomainResult.Error("Category already exists", ErrorType.CONFLICT)
                else ->
                    DomainResult.Error("Failed to saved category", ErrorType.UNKNOWN)
            }
        }
    }

    override suspend fun deleteCategory(categoryId: CategoryId): DomainResult<Unit> = dbQuery {
        try {
            val deletedRowsCount = CategoryTable.deleteWhere { CategoryTable.categoryId eq categoryId.value }

            if (deletedRowsCount == 0) return@dbQuery DomainResult.Error("Category Not Found", ErrorType.NOT_FOUND)

            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Error(e.message ?: "Unknown error", ErrorType.UNKNOWN)
        }
    }
}