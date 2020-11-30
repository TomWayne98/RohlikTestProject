package cz.tom.wayne.di

import cz.tom.wayne.R
import cz.tom.wayne.core.apis.DogApi
import org.koin.android.ext.koin.androidContext
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
