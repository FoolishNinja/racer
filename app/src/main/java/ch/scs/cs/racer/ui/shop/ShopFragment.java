package ch.scs.cs.racer.ui.shop;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ch.scs.cs.racer.MainActivity;
import ch.scs.cs.racer.R;
import ch.scs.cs.racer.databinding.FragmentShopBinding;
import ch.scs.cs.racer.models.Car;
import ch.scs.cs.racer.models.Garage;

/**
 * Shop fragment, located in main activity
 *
 * @author Carlo Schmid
 * @version 18.01.2021
 */
public class ShopFragment extends Fragment {

    private FragmentShopBinding binding;
    private Garage garage = new Garage();

    // Main activity reference
    private MainActivity mainActivity;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

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

    /**
     * Loads garage from main activity
     */
    private void initialize() {
        for (Integer unlockedCarIndex : mainActivity.getUnlockedCarIndexes()) {
            garage.setAsBought(garage.getCarAtIndex(unlockedCarIndex).getName());
        }
        render();
    }

    /**
     * Renders all the cars with their buy button
     */
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
            button.setText(car.isHasBought() ? getString(R.string.bought) : (car.getPrice() + "$"));
            button.setTextColor(Color.WHITE);
            button.setPadding(0, 30, 0, 0);
            button.setGravity(Gravity.CENTER);
            button.setBackgroundColor(car.isHasBought() ? getResources().getColor(R.color.purple_200, null) : getResources().getColor(R.color.purple_500, null));
            int finalI = i;
            button.setOnClickListener(v -> buyCar(finalI, button));
            carLayout.addView(button);
        }
    }

    /**
     * Tries to buy car at given index
     *
     * @param index  Index of car to buy
     * @param button The button which was pressed, to change styles and text accordingly
     */
    private void buyCar(int index, Button button) {
        int coins = mainActivity.getCoins();
        if (garage.getCars().get(index).isHasBought()) {
            Toast.makeText(getContext(), getString(R.string.already_bought), Toast.LENGTH_SHORT).show();
            return;
        }
        if (garage.buyCar(coins, index)) {
            button.setText(getString(R.string.bought));
            button.setBackgroundColor(getResources().getColor(R.color.purple_200, null));
            mainActivity.setGarage(garage);
            mainActivity.setCoins(coins - garage.getCarAtIndex(index).getPrice());
        } else
            Toast.makeText(getContext(), getString(R.string.insufficient_balance), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}