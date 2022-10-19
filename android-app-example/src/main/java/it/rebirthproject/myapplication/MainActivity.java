/*
 * Copyright (C) 2021/2022 Andrea Paternesi Rebirth project
 * Copyright (C) 2021/2022 Matteo Veroni Rebirth project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.* See the License for the specific language governing permissions and* limitations under the License.
 */

package it.rebirthproject.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import it.rebirthproject.myapplication.databinding.ActivityMainBinding;
import it.rebirthproject.myapplication.events.EventMessage;
import it.rebirthproject.myapplication.services.BackgroundService;
import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;
import it.rebirthproject.ufoeb.architecture.eventbus.EventBusBuilder;
import it.rebirthproject.ufoeb.architecture.eventbus.GlobalEventBus;
import it.rebirthproject.ufoeb.eventannotation.Listen;
import it.rebirthproject.ufoeb.exceptions.EventBusException;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    static {
        try {
            GlobalEventBus.setup(new EventBusBuilder().setNumberOfWorkers(1));
        } catch (EventBusException ex) {
            Log.e("ERROR", "EventBus setup exception");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder().build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        startService(new Intent(getBaseContext(), BackgroundService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            EventBus eventBus = GlobalEventBus.getInstance();
            if(!eventBus.isRegistered(this).get()) {
                eventBus.register(this);
            }
            Log.i("INFO", getClass().getSimpleName() + " - Thread (" + Thread.currentThread().getName() + ") - " + "registered to eventbus");
        } catch (Exception ex) {
            Log.e("ERROR", getClass().getSimpleName() + " - Thread (" + Thread.currentThread().getName() + ") - " + " error on eventbus registration");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_start_emitter) {
            Log.i("INFO", getClass().getSimpleName() + " - Thread (" + Thread.currentThread().getName() + ") - " + " START EMITTER CLICKED!");

            try {
                final UUID uuid = UUID.randomUUID();
                Toast.makeText(this, "Posting message " + uuid, Toast.LENGTH_SHORT).show();
                GlobalEventBus.getInstance().post(new EventMessage(uuid.toString()));
            } catch (EventBusException exception) {
                Log.e("ERROR", exception.getMessage());
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Listen
    public void onEvent(EventMessage eventMessage) {
        Log.i("INFO", getClass().getSimpleName() + " - Thread (" + Thread.currentThread().getName() + ") - " + "Event arrived " + eventMessage.getMessage());
        runOnUiThread(() -> {
            Toast.makeText(this, "Activity received: " + eventMessage.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}