package ch.scs.cs.racer.ui.home;

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

import java.util.stream.Collectors;

import ch.scs.cs.racer.MainActivity;
import ch.scs.cs.racer.R;
import ch.scs.cs.racer.databinding.FragmentHomeBinding;
import ch.scs.cs.racer.models.Car;
import ch.scs.cs.racer.models.Garage;

/**
 * Home fragment, located in main activity
 *
 * @author Carlo Schmid
 * @version 18.01.2021
 */
public class HomeFragment extends Fragment implements SurfaceHolder.Callback {
    private FragmentHomeBinding binding;
    // Surface view where the selected car is displayed
    private SurfaceView carSelectionSurfaceView;
    private int surfaceViewWidth;
    private int surfaceViewHeight;
    private SurfaceHolder surfaceHolder;
    private Canvas canvas;

    private Garage garage = new Garage();
    private int selectedCarIndex;

    // Buttons to change selected car
    private Button leftButton;
    private Button rightButton;

    // Main activity reference
    private MainActivity mainActivity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
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
        if (garage != null)
            garage.setCars(garage.getCars().stream().filter(Car::isHasBought).collect(Collectors.toList()));
        if (getArguments() != null) {
            selectedCarIndex = getArguments().getInt("selectedCarIndex");
        } else {
            selectedCarIndex = 0;
        }

    }

    /**
     * Adds button event listeners
     */
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

    /**
     * Updates the selected car index in the main activity
     */
    private void updateParentSelectedCarIndex() {
        try {
            ((MainActivity) getActivity()).setSelectedCarIndex(selectedCarIndex);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Renders car to surface view
     */
    private void render() {
        canvas = surfaceHolder.lockCanvas();
        if (canvas != null) {
            clearCanvas();
            renderInterface();
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * Clears surface view
     */
    private void clearCanvas() {
        canvas.drawColor(Color.WHITE);
    }

    /**
     * Renders car and car name
     */
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