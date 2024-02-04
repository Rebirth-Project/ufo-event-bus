/*
 * Copyright (C) 2021/2024 Andrea Paternesi Rebirth project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.rebirthproject.eventbusdemo.core.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import it.rebirthproject.eventbusdemo.core.camera.BasicCamera;
import it.rebirthproject.eventbusdemo.core.listener.LabelListener;
import it.rebirthproject.eventbusdemo.core.eventemitter.EventEmitterRunnable;
import it.rebirthproject.ufoeb.architecture.eventbus.EventBus;

public class MainWindowScreen implements Screen {

    private Stage mainmenuStage;
    private ImageTextButton buttonStop;
    private LabelListener textLabel;
    private Table table;
    private final float WIDTH = 1024f;
    private final float HEIGHT = 768f;
    private final TextureAtlas imageAtlas = new TextureAtlas(Gdx.files.internal("button.atlas"));
    private final EventBus eventBus;
    private final EventEmitterRunnable eventEmitterRunnable;

    public MainWindowScreen(EventBus eventBus, EventEmitterRunnable eventEmitterRunnable) {
        this.eventBus = eventBus;
        this.eventEmitterRunnable = eventEmitterRunnable;
    }

    @Override
    public void show() {
        mainmenuStage = new Stage(new FitViewport(WIDTH, HEIGHT, new BasicCamera(WIDTH, HEIGHT)));
        table = new Table();
        //table.debug();
        BitmapFont font = new BitmapFont();
        font.getData().setScale(2, 2);
        textLabel = new LabelListener("Starting listening events...", new Label.LabelStyle(font, Color.GOLD));

        ImageTextButtonStyle imageButtonStyle = new ImageTextButtonStyle();
        imageButtonStyle.up = new TextureRegionDrawable(imageAtlas.findRegion("normal"));
        imageButtonStyle.down = new TextureRegionDrawable(imageAtlas.findRegion("clicked"));
        imageButtonStyle.over = new TextureRegionDrawable(imageAtlas.findRegion("hover"));
        imageButtonStyle.pressedOffsetX = 1;
        imageButtonStyle.pressedOffsetY = -1;
        imageButtonStyle.font = font;      
        imageButtonStyle.fontColor = Color.RED;

        buttonStop = new ImageTextButton("Stop Emitter",imageButtonStyle);
        table.add(textLabel).size(450F, 200F).padBottom(20F);
        table.row();
        table.add(buttonStop).padBottom(20F);
        //table.debug();
        mainmenuStage.addActor(table);

        Gdx.input.setInputProcessor(mainmenuStage);

        buttonStop.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                eventEmitterRunnable.stop();
            }
        });

        eventBus.register(textLabel);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mainmenuStage.act();
        mainmenuStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        mainmenuStage.getViewport().update(width, height, true);
        table.invalidateHierarchy();
        table.setSize(WIDTH, HEIGHT);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        mainmenuStage.dispose();
    }
}
