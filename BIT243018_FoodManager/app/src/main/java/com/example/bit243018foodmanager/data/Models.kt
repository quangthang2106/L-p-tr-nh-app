package com.example.bit243018foodmanager.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import com.google.gson.annotations.SerializedName
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// All persisted table and column names carry BIT243018_ as the exam requires.
@Entity(tableName = "BIT243018_DishCategory")
data class CategoryEntity(
    @PrimaryKey @ColumnInfo(name = "BIT243018_Id") val uid: String,
    @ColumnInfo(name = "BIT243018_Name") val name: String,
    @ColumnInfo(name = "BIT243018_Description") val description: String,
    @ColumnInfo(name = "BIT243018_RemoteId") val remoteId: String? = null,
    @ColumnInfo(name = "BIT243018_Pending") val pending: String = "CREATE",
    @ColumnInfo(name = "BIT243018_Deleted") val deleted: Boolean = false
)

@Entity(tableName = "BIT243018_Dish")
data class DishEntity(
    @PrimaryKey @ColumnInfo(name = "BIT243018_Id") val uid: String,
    @ColumnInfo(name = "BIT243018_Name") val name: String,
    @ColumnInfo(name = "BIT243018_Price") val price: Double,
    @ColumnInfo(name = "BIT243018_PreparationTime") val preparationTime: Int,
    @ColumnInfo(name = "BIT243018_IsAvailable") val isAvailable: Boolean,
    @ColumnInfo(name = "BIT243018_Description") val description: String,
    @ColumnInfo(name = "BIT243018_DishCategoryId") val categoryId: String,
    @ColumnInfo(name = "BIT243018_DishImage") val imagesJson: String,
    @ColumnInfo(name = "BIT243018_RemoteId") val remoteId: String? = null,
    @ColumnInfo(name = "BIT243018_Pending") val pending: String = "CREATE",
    @ColumnInfo(name = "BIT243018_Deleted") val deleted: Boolean = false
) {
    fun images(): List<DishImage> = try {
        Gson().fromJson(imagesJson, object : TypeToken<List<DishImage>>() {}.type) ?: emptyList()
    } catch (_: Exception) { emptyList() }
    fun thumbnail(): String? = images().firstOrNull { it.isThumbnail }?.imageUrl ?: images().firstOrNull()?.imageUrl
}

data class DishImage(
    @SerializedName("BIT243018_ImageUrl") val imageUrl: String,
    @SerializedName("BIT243018_IsThumbnail") val isThumbnail: Boolean
)
fun imagesToJson(images: List<DishImage>): String = Gson().toJson(images)

@Dao
interface FoodDao {
    @Query("SELECT * FROM BIT243018_DishCategory") suspend fun allCategoriesRaw(): List<CategoryEntity>
    @Query("SELECT * FROM BIT243018_Dish") suspend fun allDishesRaw(): List<DishEntity>
    @Query("SELECT * FROM BIT243018_DishCategory WHERE BIT243018_Id = :uid LIMIT 1") suspend fun category(uid: String): CategoryEntity?
    @Query("SELECT * FROM BIT243018_Dish WHERE BIT243018_Id = :uid LIMIT 1") suspend fun dish(uid: String): DishEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun saveCategory(item: CategoryEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun saveDish(item: DishEntity)
    @Query("DELETE FROM BIT243018_DishCategory WHERE BIT243018_Id = :uid") suspend fun removeCategory(uid: String)
    @Query("DELETE FROM BIT243018_Dish WHERE BIT243018_Id = :uid") suspend fun removeDish(uid: String)
}

@Database(entities = [CategoryEntity::class, DishEntity::class], version = 1, exportSchema = false)
abstract class FoodDatabase : RoomDatabase() { abstract fun foodDao(): FoodDao }
