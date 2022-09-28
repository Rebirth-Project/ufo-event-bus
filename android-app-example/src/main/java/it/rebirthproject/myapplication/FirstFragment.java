/*
 * Copyright (C) 2021 Andrea Paternesi Rebirth project
 * Copyright (C) 2021 Matteo Veroni Rebirth project
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

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import it.rebirthproject.myapplication.databinding.FragmentFirstBinding;
import it.rebirthproject.myapplication.events.EventMessage;
import it.rebirthproject.ufoeb.architecture.eventbus.GlobalEventBus;
import it.rebirthproject.ufoeb.eventannotation.Listen;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        try {
            if (!GlobalEventBus.getInstance().isRegistered(this).get())
                GlobalEventBus.getInstance().register(this);
        } catch (Exception ex) {
            Log.e("ERROR", ex.getMessage());
        }
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Listen
    public void onEvent(EventMessage eventMessage) {
        Log.i("INFO", getClass().getSimpleName() + " - Thread (" + Thread.currentThread().getName() + ") - " + "Event arrived " + eventMessage.getMessage());
        if(isAdded()) {
            getActivity().runOnUiThread(() -> {
                TextView viewById = getActivity().findViewById(R.id.textview_first);
                if (viewById != null) {
                    ((TextView) viewById.findViewById(R.id.textview_first)).setText(String.format("Fragment received: %s", eventMessage.getMessage()));
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}