package com.example.practice_4_2;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SimpleWorker extends Worker {
    public SimpleWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String taskName = getInputData().getString("task_name");
        Log.d("SimpleWorker", "Starting: " + taskName);
        try {
            Thread.sleep(2000); // Имитация работы
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Result.failure();
        }
        Log.d("SimpleWorker", "Finished: " + taskName);
        return Result.success();
    }
}
