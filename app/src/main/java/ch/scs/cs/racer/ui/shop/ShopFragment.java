package ch.scs.cs.racer.ui.shop;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ch.scs.cs.racer.MainActivity;
import ch.scs.cs.racer.R;
import ch.scs.cs.racer.databinding.FragmentShopBinding;
import ch.scs.cs.racer.models.Car;
import ch.scs.cs.racer.models.Garage;

public class ShopFragment extends Fragment {

    private ShopViewModel shopViewModel;
    private FragmentShopBinding binding;
    private Garage garage = new Garage();
    private MainActivity mainActivity;
    private final int surfaceViewWidth = 220;
    private final int surfaceViewHeight = 130;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shopViewModel =
                new ViewModelProvider(this).get(ShopViewModel.class);

        binding = FragmentShopBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mainActivity = (MainActivity) getActivity();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();
    }

    private void initialize() {
        for (Integer unlockedCarIndex : mainActivity.getUnlockedCarIndexes()) {
            garage.setAsBought(garage.getCarAtIndex(unlockedCarIndex).getName());
        }
        render();
    }

    private void render() {
        LinearLayout carLayout = getView().findViewById(R.id.carLayout);
        for (int i = 0; i < garage.getCars().size(); i++) {
            Car car = garage.getCarAtIndex(i);
            TextView textView = new TextView(getContext());
            textView.setPadding(0, 50, 0, 0);
            textView.setGravity(Gravity.CENTER);
            textView.setText(car.getName());
            carLayout.addView(textView);
            TextView textView1 = new TextView(getContext());
            textView.setPadding(0, 20, 0, 0);
            textView1.setGravity(Gravity.CENTER);
            textView1.setText(car.getSpeed() + "km/h");
            carLayout.addView(textView1);
            Button button = new Button(getContext());
            button.setText(car.isHasBought() ? "Bought" : car.getPrice() + "$");
            button.setTextColor(Color.WHITE);
            button.setPadding(0, 30, 0, 0);
            button.setGravity(Gravity.CENTER);
            button.setBackgroundColor(car.isHasBought() ? getResources().getColor(R.color.purple_200, null) : getResources().getColor(R.color.purple_500, null));
            int finalI = i;
            button.setOnClickListener(v -> buyCar(finalI, button));
            carLayout.addView(button);
        }
    }

    private void buyCar(int index, Button button) {
        int coins = mainActivity.getCoins();
        if(garage.getCars().get(index).isHasBought()) {
            Toast.makeText(getContext(), "Already bought car", Toast.LENGTH_SHORT).show();
            return;
        }
        if (garage.buyCar(coins, index)) {
            button.setText("Bought");
            mainActivity.setGarage(garage);
            mainActivity.setCoins(coins - garage.getCarAtIndex(index).getPrice());
        } else Toast.makeText(getContext(), "Insufficient balance", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}