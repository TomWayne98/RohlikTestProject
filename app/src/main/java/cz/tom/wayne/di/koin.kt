package cz.tom.wayne.di

import androidx.room.Room
import cz.tom.wayne.R
import cz.tom.wayne.core.apis.DogApi
import cz.tom.wayne.core.navigation.Navigator
import cz.tom.wayne.core.repositories.DogRepo
import cz.tom.wayne.db.RoomDb
import cz.tom.wayne.homescreen.model.DogRepoImpl
import cz.tom.wayne.homescreen.viewmodel.HomeScreenViewModel
import cz.tom.wayne.navigator.NavigatorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule: Module = module {
    single<DogApi> {
        val retroClient = Retrofit.Builder()
            .baseUrl(androidContext().getString(R.string.movie_db_api_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retroClient.create(DogApi::class.java)
    }
}


val dbModule: Module = module {
    single {
        Room.databaseBuilder(
            androidContext(), RoomDb::class.java,
            "room-db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<RoomDb>().dogDAO() }
}

val navigationModule: Module = module {
    single<Navigator> { NavigatorImpl() }
}

val homeScreenModule: Module = module {
    viewModel { HomeScreenViewModel(get()) }
    single<DogRepo> { DogRepoImpl(get(), get()) }
}


