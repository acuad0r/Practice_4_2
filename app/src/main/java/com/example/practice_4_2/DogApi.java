package com.example.practice_4_2;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DogApi {
    @GET("woof.json")
    Call<DogResponse> getRandomDog();
}
