package ch.scs.cs.racer;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentHostCallback;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.scs.cs.racer.databinding.ActivityMainBinding;
import ch.scs.cs.racer.models.Car;
import ch.scs.cs.racer.models.Garage;
import ch.scs.cs.racer.ui.home.HomeFragment;
import ch.scs.cs.racer.ui.shop.ShopFragment;

public class MainActivity extends AppCompatActivity {
    private static final int sharedPreferenceKey = 42069;

    private int highScore;
    private int coins;
    private int selectedCarIndex;
    private Garage garage;

    private ActivityMainBinding binding;

    private TextView coinsText;
    private TextView highScoreText;

    private Fragment homeFragment;
    private Fragment shopFragment;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadViews();
        loadSavedInstanceState(savedInstanceState);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_shop)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        this.navController = navController;

        loadSharedPreferences();
        putSelectedCarBundle(navController);
    }

    private void loadSavedInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            ArrayList<Integer> unlockedCarIndexes = savedInstanceState.getIntegerArrayList("unlockedCarIndexes");
            for (int i = 0; i < garage.getCars().size(); i++) {
                if(unlockedCarIndexes.contains(i)) {
                    garage.setAsBought(garage.getCarAtIndex(i).getName());
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList("unlockedCarIndexes", getUnlockedCarIndexes());
    }

    private void loadViews() {
        coinsText = findViewById(R.id.coinsText);
        highScoreText = findViewById(R.id.highscoreText);
    }

    private void putSelectedCarBundle(NavController navController) {
        Bundle selectedCarBundle = new Bundle();
        selectedCarBundle.putInt("selectedCarIndex", selectedCarIndex);

        selectedCarBundle.putIntegerArrayList("unlockedCarIndexes", getUnlockedCarIndexes());
        navController.navigate(R.id.navigation_home, selectedCarBundle);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getGameActivityIntent();
        loadCurrentFragment();
    }

    private void loadCurrentFragment() {
        int id = navController.getCurrentDestination().getId();
        if(id == R.id.navigation_home) {
            homeFragment = getSupportFragmentManager().getFragments().get(0);
        } else shopFragment = getSupportFragmentManager().getFragments().get(0);

    }

    private void loadSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(sharedPreferenceKey), Context.MODE_PRIVATE);
        highScore = sharedPreferences.getInt("highScore", 0);
        coins = sharedPreferences.getInt("coins", 50000);
        selectedCarIndex = sharedPreferences.getInt("selectedCarIndex", 0);
        garage = new Garage();
        Set<String> unlockedCarNames = sharedPreferences.getStringSet("unlockedCarNames", new HashSet<>());
        for (String unlockedCarName : unlockedCarNames) garage.setAsBought(unlockedCarName);
        setCoinsAndHighScoreText();
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("carName", garage.getCars().get(selectedCarIndex).getName());
        startActivity(intent);
    }

    private void getGameActivityIntent() {
        if (!getIntent().hasExtra("score")) return;
        int score = getIntent().getIntExtra("score", highScore);
        highScore = Math.max(highScore, score);
        coins += getIntent().getIntExtra("coins", coins);
        setCoinsAndHighScoreText();
    }

    private void setCoinsAndHighScoreText() {
        coinsText.setText("Coins: " + coins + "$");
        highScoreText.setText("Highscore: " + highScore + "m");
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(sharedPreferenceKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("highScore", highScore);
        editor.putInt("coins", coins);
        editor.putStringSet("unlockedCarNames", garage.getBoughtCarNames());
        editor.putInt("selectedCarIndex", selectedCarIndex);
        editor.apply();
    }

    public void setSelectedCarIndex(int index) {
        selectedCarIndex = index;
    }

    public ArrayList<Integer> getUnlockedCarIndexes() {
        ArrayList<Integer> unlockedCarIndexes = new ArrayList<>();
        for (int i = 0; i < garage.getCars().size(); i++) {
            if(garage.getCarAtIndex(i).isHasBought()) unlockedCarIndexes.add(i);
        }
        return unlockedCarIndexes;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
        setCoinsAndHighScoreText();
    }

    public void setGarage(Garage garage) {
        this.garage = garage;
    }

    public Garage getGarage() {
        return garage;
    }
}