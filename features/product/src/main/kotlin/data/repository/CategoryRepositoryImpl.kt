package data.repository

import DatabaseFactory.dbQuery
import data.local.table.CategoryTable
import data.mapper.toCategory
import domain.model.Category
import domain.model.CategoryId
import domain.repository.CategoryRepository
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

class CategoryRepositoryImpl: CategoryRepository {
    override suspend fun getAllCategory(): List<Category> = dbQuery {
        val categoriesRows = CategoryTable
            .selectAll()
            .toList()

        categoriesRows.map { row ->
            row.toCategory()
        }
    }

    override suspend fun getCategoryById(categoryId: CategoryId): Category? = dbQuery {
        val categoryRow = CategoryTable
            .selectAll()
            .where { CategoryTable.categoryId eq categoryId.value }
            .singleOrNull()

        if (categoryRow == null) return@dbQuery null

        categoryRow.toCategory()
    }

    override suspend fun insertCategory(category: Category): Boolean = dbQuery {
        try {
            val insertStatement = CategoryTable.insert {
                it[categoryId] = category.categoryId.value
                it[name] = category.name
                it[description] = category.description
                it[createdAt] = category.createdAt
                it[lastUpdated] = category.lastUpdated
            }

            insertStatement.insertedCount > 0
        } catch (_: ExposedSQLException) {
            false
        }
    }

    override suspend fun updateCategory(category: Category): Boolean = dbQuery {
        val updatedRowsCount = CategoryTable.update ({ CategoryTable.categoryId eq category.categoryId.value }) {
            it[name] = category.name
            it[description] = category.description
            it[lastUpdated] = category.lastUpdated
        }

        updatedRowsCount > 0
    }

    override suspend fun deleteCategory(categoryId: CategoryId): Boolean = dbQuery {
        val deletedRowsCount = CategoryTable.deleteWhere { CategoryTable.categoryId eq categoryId.value }

        deletedRowsCount > 0
    }
}