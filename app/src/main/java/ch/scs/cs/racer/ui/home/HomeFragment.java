package ch.scs.cs.racer.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ch.scs.cs.racer.MainActivity;
import ch.scs.cs.racer.R;
import ch.scs.cs.racer.databinding.FragmentHomeBinding;
import ch.scs.cs.racer.models.Car;
import ch.scs.cs.racer.models.Garage;

public class HomeFragment extends Fragment implements SurfaceHolder.Callback {
    private static final int sharedPreferenceKey = 42069;

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private SurfaceView carSelectionSurfaceView;
    private Garage garage = new Garage();
    private Canvas canvas;
    private int selectedCarIndex;
    private int surfaceViewWidth;
    private int surfaceViewHeight;
    private SurfaceHolder surfaceHolder;
    private Button leftButton;
    private Button rightButton;

    private MainActivity mainActivity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        leftButton = root.findViewById(R.id.leftButton);
        rightButton = root.findViewById(R.id.rightButton);
        carSelectionSurfaceView = root.findViewById(R.id.carSelectionSurfaceView);
        carSelectionSurfaceView.getHolder().addCallback(this);
        mainActivity = (MainActivity) getActivity();
        initialize();
        return root;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        garage = mainActivity.getGarage();
        if(garage != null) garage.setCars(garage.getCars().stream().filter(Car::isHasBought).collect(Collectors.toList()));
        if (getArguments() != null) {
            selectedCarIndex = getArguments().getInt("selectedCarIndex");
        } else {
            selectedCarIndex = 0;
        }

    }

    private void initialize() {

        leftButton.setOnClickListener(v -> {
            if (selectedCarIndex == 0) return;
            selectedCarIndex--;
            updateParentSelectedCarIndex();
            render();
        });
        rightButton.setOnClickListener(v -> {
            if (selectedCarIndex == garage.getCars().size() - 1) return;
            selectedCarIndex++;
            updateParentSelectedCarIndex();
            render();
        });
    }

    private void updateParentSelectedCarIndex() {
        try{
            ((MainActivity) getActivity()).setSelectedCarIndex(selectedCarIndex);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void render() {
        canvas = surfaceHolder.lockCanvas();
        if (canvas != null) {
            clearCanvas();
            renderInterface();
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void clearCanvas() {
        canvas.drawColor(Color.WHITE);
    }

    private void renderInterface() {
        Car car = garage.getCarAtIndex(selectedCarIndex);
        Rect carRect = new Rect();
        carRect.left = surfaceViewWidth / 2 - car.getWidth() / 2;
        carRect.right = carRect.left + car.getWidth();
        carRect.top = surfaceViewHeight / 2 - car.getHeight() / 2;
        carRect.bottom = carRect.top + car.getHeight();
        Rect tires = new Rect();
        tires.left = carRect.left - 20;
        tires.right = carRect.right + 20;
        tires.top = carRect.top + 10;
        tires.bottom = carRect.top + 40;
        Paint tirePaint = new Paint();
        tirePaint.setTextSize(50);
        tirePaint.setColor(Color.BLACK);
        canvas.drawRect(tires, tirePaint);
        tires.top = carRect.bottom - 40;
        tires.bottom = carRect.bottom - 10;
        canvas.drawRect(tires, tirePaint);
        canvas.drawRect(carRect, car.getPaint());
        canvas.drawText(car.getName(), carRect.left - 60, carRect.top - 40, tirePaint);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        surfaceViewWidth = carSelectionSurfaceView.getWidth();
        surfaceViewHeight = carSelectionSurfaceView.getHeight();
        surfaceHolder = holder;
        render();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }
}