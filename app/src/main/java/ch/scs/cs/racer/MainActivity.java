package ch.scs.cs.racer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ch.scs.cs.racer.databinding.ActivityMainBinding;
import ch.scs.cs.racer.models.Garage;

/**
 * Main activity, here you can start the game and access the shop
 *
 * @author Carlo Schmid
 * @version 18.01.2021
 */
public class MainActivity extends AppCompatActivity {
    private static final int sharedPreferenceKey = 42069;

    // players all time highscore
    private int highScore;
    // players coin amount
    private int coins;
    // currently selected car's index
    private int selectedCarIndex;
    // Players garage
    private Garage garage;

    private ActivityMainBinding binding;

    // Coins display text
    private TextView coinsText;
    // Highscore display text
    private TextView highScoreText;

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

    /**
     * Loads unlocked cars on save instance state
     */
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

    /**
     * Persists unlocked cars on save instance state
     */
    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList("unlockedCarIndexes", getUnlockedCarIndexes());
    }

    /**
     * Loads the two text views
     */
    private void loadViews() {
        coinsText = findViewById(R.id.coinsText);
        highScoreText = findViewById(R.id.highscoreText);
    }

    /**
     * Adds the selected car index and unlocked car index to the nav controller bundle, for the home controller to retreive it
     * @param navController
     */
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
    }

    /**
     * Loads the persisted game data
     */
    private void loadSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(sharedPreferenceKey), Context.MODE_PRIVATE);
        highScore = sharedPreferences.getInt("highScore", 0);
        coins = sharedPreferences.getInt("coins", 4500);
        selectedCarIndex = sharedPreferences.getInt("selectedCarIndex", 0);
        garage = new Garage();
        Set<String> unlockedCarNames = sharedPreferences.getStringSet("unlockedCarNames", new HashSet<>());
        for (String unlockedCarName : unlockedCarNames) garage.setAsBought(unlockedCarName);
        setCoinsAndHighScoreText();
    }

    /**
     * Starts the game
     */
    public void startGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("carName", garage.getCars().get(selectedCarIndex).getName());
        startActivity(intent);
    }

    /**
     * Retrieves played game's results
     */
    private void getGameActivityIntent() {
        if (!getIntent().hasExtra("score")) return;
        int score = getIntent().getIntExtra("score", highScore);
        highScore = Math.max(highScore, score);
        coins += getIntent().getIntExtra("coins", coins);
        setCoinsAndHighScoreText();
    }

    /**
     *  Sets the coins and highscore text
     */
    private void setCoinsAndHighScoreText() {
        coinsText.setText(getString(R.string.coins) + coins + "$");
        highScoreText.setText(getString(R.string.high_score) + highScore + "m");
    }

    @Override
    /**
     * Persists the data on a close of the app
     */
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(sharedPreferenceKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        /**editor.putInt("highScore", highScore);
        editor.putInt("coins", coins);
        editor.putStringSet("unlockedCarNames", garage.getBoughtCarNames());
        editor.putInt("selectedCarIndex", selectedCarIndex);**/
        editor.clear();
        editor.apply();
    }

    /**
     * Sets the currently selected car, called by shop fragment
     * @param index
     */
    public void setSelectedCarIndex(int index) {
        selectedCarIndex = index;
    }

    /**
     * Returns each index of the garage, where the player has unlocked the car
     * @return all unlocked car indexes
     */
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