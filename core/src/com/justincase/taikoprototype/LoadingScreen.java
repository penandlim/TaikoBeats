package com.justincase.taikoprototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by John LIm on 5/30/2017.
 */
public class LoadingScreen implements Screen {

    private final TaikoPrototype app;
    private ShapeRenderer shapeRenderer;
    Sound yooSound;
    Image img;
    private Stage stage;

    public LoadingScreen(final TaikoPrototype app) {
        this.app = app;
        this.stage = new Stage(new FitViewport(app.V_WIDTH, app.V_HEIGHT, app.camera));
        this.shapeRenderer = new ShapeRenderer();
        yooSound = Gdx.audio.newSound(Gdx.files.internal("yoo.wav"));
        Texture loadingTexture = new Texture("Opening Screen.png");
        // loadingTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        img = new Image(loadingTexture);
        img.setColor(1,1,1,0);
        img.addAction(Actions.fadeIn(2.0f, Interpolation.exp5));
        img.setTouchable(Touchable.enabled);
        final boolean[] hasTransitioned = {false};
        img.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                System.out.println("touched");

                if (!hasTransitioned[0]) {
                    hasTransitioned[0] = true;
                    yooSound.play(0.4f);
                    img.setOrigin(img.getWidth() / 2, img.getHeight() / 2);
                    img.addAction(sequence(Actions.fadeOut(5.0f, Interpolation.exp10), run(new Runnable() {
                        @Override
                        public void run() {
                            app.setScreen(new PlayMode(app));
                        }
                    })));

                    return true;
                }
                return false;
            }
        });



        stage.addActor(img);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        System.out.println("LOADING");
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // update(delta);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        app.batch.begin();
        app.batch.end();

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width,height, false);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
