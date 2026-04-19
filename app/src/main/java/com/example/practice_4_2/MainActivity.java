package com.example.practice_4_2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ImageView ivDog;
    private DogApi dogApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSequential = findViewById(R.id.btnSequential);
        Button btnParallel = findViewById(R.id.btnParallel);
        Button btnLoadDog = findViewById(R.id.btnLoadDog);
        ivDog = findViewById(R.id.ivDog);

        // Инициализация Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://random.dog/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        dogApi = retrofit.create(DogApi.class);

        // 1) Последовательный запуск 3 задач
        btnSequential.setOnClickListener(v -> {
            OneTimeWorkRequest task1 = createWorkRequest("Task 1");
            OneTimeWorkRequest task2 = createWorkRequest("Task 2");
            OneTimeWorkRequest task3 = createWorkRequest("Task 3");

            WorkManager.getInstance(this)
                    .beginWith(task1)
                    .then(task2)
                    .then(task3)
                    .enqueue();
            
            Toast.makeText(this, "Последовательные задачи запущены", Toast.LENGTH_SHORT).show();
        });

        // 2) Параллельный запуск 2 задач
        btnParallel.setOnClickListener(v -> {
            OneTimeWorkRequest taskA = createWorkRequest("Task A (Parallel)");
            OneTimeWorkRequest taskB = createWorkRequest("Task B (Parallel)");

            WorkManager.getInstance(this)
                    .enqueue(Arrays.asList(taskA, taskB));

            Toast.makeText(this, "Параллельные задачи запущены", Toast.LENGTH_SHORT).show();
        });

        // 3) Загрузка изображения собаки
        btnLoadDog.setOnClickListener(v -> loadRandomDog());
    }

    private OneTimeWorkRequest createWorkRequest(String name) {
        Data data = new Data.Builder()
                .putString("task_name", name)
                .build();
        return new OneTimeWorkRequest.Builder(SimpleWorker.class)
                .setInputData(data)
                .build();
    }

    private void loadRandomDog() {
        dogApi.getRandomDog().enqueue(new Callback<DogResponse>() {
            @Override
            public void onResponse(Call<DogResponse> call, Response<DogResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String url = response.body().getUrl();
                    if (url.endsWith(".mp4") || url.endsWith(".webm")) {
                        // Если это видео, попробуем еще раз
                        loadRandomDog();
                    } else {
                        Glide.with(MainActivity.this)
                                .load(url)
                                .into(ivDog);
                    }
                }
            }

            @Override
            public void onFailure(Call<DogResponse> call, Throwable t) {
                Log.e("MainActivity", "Error loading dog", t);
                Toast.makeText(MainActivity.this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
