package io.github.keibai.runnable;


import android.content.Context;
import android.widget.Toast;

public class RunnableToast implements Runnable {

    private final Context context;
    private final String text;
    private final int duration;

    public RunnableToast(Context context, String text) {
        this(context, text, Toast.LENGTH_SHORT);
    }

    public RunnableToast(Context context, String text, int duration) {
        this.context = context;
        this.text = text;
        this.duration = duration;
    }

    @Override
    public void run() {
        Toast.makeText(context, text, duration).show();
    }
}
