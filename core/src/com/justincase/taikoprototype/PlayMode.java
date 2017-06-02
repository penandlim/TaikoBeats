package com.justincase.taikoprototype;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import javax.xml.soap.Text;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by John LIm on 5/30/2017.
 */
public class PlayMode implements Screen {

    private Sound soundILW, soundOLW, soundIRW, soundORW, soundWoodblock;
    private Image cursorImage;
    private Image beatExtendedImage;
    private Image blackImage;
    private Group propertyBPMButtons;
    private Group propertyBeatButtons;
    private Group propertyHPMButtons;
    private Timer playTimer;

    public enum NoteType {
        NONE, DONR, DONL, KAR, KAL
    }

    ArrayList<Integer> notes = new ArrayList<Integer>();

    private final TaikoPrototype app;
    private final Image settingsGearImage;
    private final Image settingsImage;
    private final Image infoIcon;
    private final Image saveIcon;
    private final Image saveWindowImage;
    private final Image cancelIndicator;
    private final Image confirmIndicator;
    private final TextField textfield;
    private final Image beatPropertyImage;
    private final Image beatListImage1;
    private final Image beatListImage2;
    private boolean isSettingsOpen = false;
    private ShapeRenderer shapeRenderer;
    Image img,  inLeftImage, inLeftImage2, inRightImage, outLeftImage, outRightImage, bottomPanelImage, bottomPanelArrow;
    Group noteGroup;
    Group propertyButtons;
    int cursor = 0;
    int configSpeedMilli = 200;
    int configHPM = 4;
    boolean isPlayMode = true;
    private Stage stage;
    private boolean isBeatPropertyOpen = false;
    int countdown = 0;
    boolean isMusicPlaying = false;

    Color grayNoteColor = new Color(129f/255f, 129f/255f, 129f/255f, 1f);

    public PlayMode(final TaikoPrototype app) {
        shapeRenderer = new ShapeRenderer();

        this.app = app;
        this.stage = new Stage(new FitViewport(app.V_WIDTH, app.V_HEIGHT, app.camera));
        Texture playModeTxt = new Texture("Group 201.png");
        //playModeTxt.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        img = new Image(playModeTxt);
        img.setPosition(-53, -50);

        noteGroup = new Group();
        cursorImage = new Image(new Texture("cursor.png"));
        cursorImage.setPosition(326, 550 + 172 - 750);
        cursorImage.setColor(Color.CLEAR);
        for (int i = 0; i < 20; i++) {
            notes.add(NoteType.NONE.ordinal());
            final Image note = new Image(new Texture("blanknote.png"));
            note.setName(Integer.toString(i + 1));
            note.setPosition(326 + i * note.getWidth(), -200);

            note.addListener(new ActorGestureListener() {
                public void touchDown(InputEvent event, float x, float y,
                                         int pointer, int button) {
                    System.out.println("touched note #" + note.getName() + "at " + x + " " + y);
                    cursorImage.clearActions();
                    cursorImage.addAction(Actions.color(Color.WHITE, 0.1f, Interpolation.exp10));
                    cursorImage.addAction(moveTo(note.getX(), cursorImage.getY(), 0.1f, Interpolation.exp10));
                    cursor = Integer.parseInt(note.getName());
                }

                public boolean longPress (Actor actor, float x, float y) {
                    recordAt(Integer.parseInt(note.getName()), NoteType.NONE.ordinal());
                    return true;
                }

            });

            noteGroup.addActor(note);
        }

        //// MATSURI /////
        notes.set(0, NoteType.DONR.ordinal());
        noteGroup.findActor("1").setColor(Color.RED);
        notes.set(4, NoteType.DONL.ordinal());
        noteGroup.findActor("5").setColor(Color.RED);
        notes.set(8, NoteType.DONR.ordinal());
        noteGroup.findActor("9").setColor(Color.RED);
        notes.set(10, NoteType.KAR.ordinal());
        noteGroup.findActor("11").setColor(Color.BLUE);
        notes.set(11, NoteType.KAL.ordinal());
        noteGroup.findActor("12").setColor(Color.BLUE);
        notes.set(12, NoteType.KAR.ordinal());
        noteGroup.findActor("13").setColor(Color.BLUE);
        notes.set(14, NoteType.KAL.ordinal());
        noteGroup.findActor("15").setColor(Color.BLUE);
        //////////////////////////////////////



        soundILW = Gdx.audio.newSound(Gdx.files.internal("ILW.wav"));
        // Sound soundILS = Gdx.audio.newSound(Gdx.files.internal("ILS.wav"));
        soundIRW = Gdx.audio.newSound(Gdx.files.internal("IRW.wav"));
        // Sound soundIRS = Gdx.audio.newSound(Gdx.files.internal("IRS.wav"));
        soundOLW = Gdx.audio.newSound(Gdx.files.internal("OLW.wav"));
        // Sound soundOLS = Gdx.audio.newSound(Gdx.files.internal("OLS.wav"));
        soundORW = Gdx.audio.newSound(Gdx.files.internal("ORW.wav"));
        // Sound soundORS = Gdx.audio.newSound(Gdx.files.internal("ORS.wav"));
        soundWoodblock = Gdx.audio.newSound(Gdx.files.internal("Woodblock.wav"));


        final Group drums = new Group();

        Texture inLeftTxt = new Texture("inleft-play.png");
        //inLeftTxt.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        inLeftImage = new Image(inLeftTxt);
        drums.addActor(inLeftImage);

        Texture inLeftTxt2 = new Texture("inleft-create.png");
        //inLeftTxt.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        inLeftImage2 = new Image(inLeftTxt2);
        inLeftImage2.setColor(Color.CLEAR);
        drums.addActor(inLeftImage2);

        Texture inRightTxt = new Texture("inright.png");
        //inRightTxt.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        inRightImage = new Image(inRightTxt);
        drums.addActor(inRightImage);


        Texture outLeftTxt = new Texture("outleft.png");
        //outLeftTxt.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        outLeftImage = new Image(outLeftTxt);
        drums.addActor(outLeftImage);

        Texture outRightTxt = new Texture("outright.png");
        //outRightTxt.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        outRightImage = new Image(outRightTxt);
        drums.addActor(outRightImage);
        drums.setPosition(10, 588);

        outRightImage.setTouchable(Touchable.enabled);
        outRightImage.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                if ( Math.pow(x - 742,2) + Math.pow(y - 746,2) <  Math.pow(370,2) && x < 742 ) {
                    System.out.println("touched in left" + x + " " + y);
                    soundILW.play();
                    // Gdx.input.vibrate(150);
                    if (inLeftImage2.getColor().a == 0) {
                        inLeftImage.clearActions();
                        inLeftImage.setColor(Color.RED);
                        inLeftImage.addAction(Actions.color(Color.WHITE, 0.5f, Interpolation.exp5Out));
                    }
                    else {
                        inLeftImage2.clearActions();
                        inLeftImage2.setColor(Color.RED);
                        inLeftImage2.addAction(Actions.color(Color.WHITE, 0.5f, Interpolation.exp5Out));
                    }
                    if (cursor != 0 && !isPlayMode)
                        recordAt(cursor, NoteType.DONL.ordinal());
                    return true;
                }
                else if ( Math.pow(x - 742,2) + Math.pow(y - 746,2) >  Math.pow(370,2) && x < 742 && Math.pow(x - 742,2) + Math.pow(y - 746,2) <  Math.pow(735,2)) {
                    System.out.println("touched out left" + x + " " + y);
                    soundOLW.play();
                    // Gdx.input.vibrate(75);
                    outLeftImage.clearActions();
                    outLeftImage.setColor(Color.BLUE);
                    outLeftImage.addAction(Actions.color(Color.WHITE, 0.5f, Interpolation.exp5Out));
                    if (cursor != 0 && !isPlayMode)
                        recordAt(cursor, NoteType.KAL.ordinal());

                    return true;
                }
                else if ( Math.pow(x - 742,2) + Math.pow(y - 746,2) <  Math.pow(370,2) && x > 742) {
                    System.out.println("touched in right" + x + " " + y);
                    soundIRW.play();
                    // Gdx.input.vibrate(600);
                    inRightImage.clearActions();
                    inRightImage.setColor(Color.RED);
                    inRightImage.addAction(Actions.color(Color.WHITE, 0.5f, Interpolation.exp5Out));
                    if (cursor != 0 && !isPlayMode)
                        recordAt(cursor, NoteType.DONR.ordinal());
                    return true;
                }
                else if ( Math.pow(x - 742,2) + Math.pow(y - 746,2) >  Math.pow(370,2) && x > 742 && Math.pow(x - 742,2) + Math.pow(y - 746,2) <  Math.pow(735,2)) {
                    System.out.println("touched out right" + x + " " + y);
                    soundORW.play();
                    // Gdx.input.vibrate(200);
                    outRightImage.clearActions();
                    outRightImage.setColor(Color.BLUE);
                    outRightImage.addAction(Actions.color(Color.WHITE, 0.5f, Interpolation.exp5Out));
                    if (cursor != 0 && !isPlayMode)
                        recordAt(cursor, NoteType.KAR.ordinal());
                    return true;
                }

                return false;
            }
        });

        bottomPanelImage = new Image(new Texture("Group 198.png")); // 198, 224
        bottomPanelImage.setPosition(0, -1000);
        bottomPanelArrow = new Image(new Texture("arrow_dropdown.png"));
        bottomPanelArrow.setPosition(app.V_WIDTH / 2, 100, Align.center);
        bottomPanelArrow.setOrigin(bottomPanelArrow.getWidth()/2, bottomPanelArrow.getHeight()/2);

        final Group bottomPanel = new Group();
        bottomPanel.addActor(bottomPanelImage);
        bottomPanel.addActor(bottomPanelArrow);
        bottomPanel.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                System.out.println("touched " + x + " " + y);

                // Open and close bottom panel
                if (y > bottomPanelArrow.getImageY() + 50 && y < bottomPanelArrow.getImageY() + 180) {
                    if (isPlayMode) {
                        noteGroup.addAction(Actions.moveBy(0, 750, 1.0f, Interpolation.pow4));
                        cursorImage.addAction(Actions.moveBy(0, 750, 1.0f, Interpolation.pow4));
                        bottomPanel.addAction(Actions.moveBy(0, 750, 1.0f, Interpolation.pow4));
                        drums.addAction(Actions.moveBy(0, 350, 1.0f, Interpolation.pow4));
                        bottomPanelArrow.addAction(Actions.rotateTo(180, 1.0f, Interpolation.pow4));
                        inLeftImage2.addAction(Actions.color(Color.WHITE, 1.0f, Interpolation.pow4));
                        saveIcon.addAction(Actions.color(Color.WHITE, 1.0f, Interpolation.pow4));
                        isPlayMode = false;
                    } else {
                        noteGroup.addAction(Actions.moveBy(0, -750, 1.0f, Interpolation.pow4));
                        cursorImage.addAction(Actions.moveBy(0, -750, 1.0f, Interpolation.pow4));
                        bottomPanel.addAction(Actions.moveBy(0, -750, 1.0f, Interpolation.pow4));
                        drums.addAction(Actions.moveBy(0, -350, 1.0f, Interpolation.pow4));
                        bottomPanelArrow.addAction(Actions.rotateTo(0, 1.0f, Interpolation.pow4));
                        inLeftImage2.addAction(Actions.color(Color.CLEAR, 1.0f, Interpolation.pow4));
                        saveIcon.addAction(Actions.color(Color.CLEAR, 1.0f, Interpolation.pow4));
                        isPlayMode = true;
                    }
                    return true;

                // Open Beat Property page
                } else if (!isPlayMode && x > 1288 && y < -506) {
                    if (!isBeatPropertyOpen) {
                        beatPropertyImage.addAction(Actions.moveTo(0, 0, 0.7f, Interpolation.exp5Out));
                        propertyButtons.addAction(Actions.moveBy(1500, 0, 0.7f, Interpolation.exp5Out));
                        isBeatPropertyOpen = true;
                        return true;
                    }
                }

                // play music!
                else if (x > 620 && x < 860 && y > -720 && y < -520 && !isPlayMode) {
                    if (isMusicPlaying) {
                        stopNotes();
                    } else {
                        playNotes();
                    }
                    return true;
                }

                // Open Beat List image
                else if (x > 400 && x < 600 && y > -720 && y < -520 && !isPlayMode) {
                    beatListImage1.addAction(Actions.moveBy(1500, 0, 0.7f, Interpolation.exp5Out));
                    return true;
                }

                // Open a more detailed beat list image
                else if (x > 72 && x < 200 && y < -541 && y > -680 && !isPlayMode) {
                    System.out.println("expand");
                    beatExtendedImage.addAction(Actions.moveBy(1500, 0, 0.7f, Interpolation.exp5Out));
                    return true;
                } else {
                    cursor = 0;
                    cursorImage.addAction(Actions.color(Color.CLEAR, 0.1f, Interpolation.exp10));
                    return true;
                }

                return false;
            }
        });

        settingsImage = new Image(new Texture("SettingsPage.png"));
        settingsImage.setPosition(-1500, 0);
        settingsImage.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                if (isSettingsOpen && y > 2300) {
                    settingsImage.addAction(Actions.moveBy(-1500, 0, 0.7f, Interpolation.exp5Out));
                    isSettingsOpen = false;
                    return true;
                }
                return false;
            }
        });

        settingsGearImage = new Image(new Texture("settings.png"));
        settingsGearImage.setPosition(20, 2470);
        settingsGearImage.setOrigin(settingsGearImage.getWidth() / 2, settingsGearImage.getHeight() / 2);
        settingsGearImage.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                if (!isSettingsOpen) {
                    settingsGearImage.addAction(Actions.rotateBy(360, 0.5f, Interpolation.exp5Out));
                    settingsImage.addAction(Actions.moveTo(0, 0, 0.5f, Interpolation.exp5Out));
                    isSettingsOpen = true;
                    return true;
                }
                return false;
            }
        });

        infoIcon = new Image(new Texture("info.png"));
        infoIcon.setPosition(220, 2470);
        infoIcon.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                openTaikoSource();
                return true;
            }
        });

        saveWindowImage = new Image(new Texture("save window.png"));
        saveWindowImage.setPosition(app.V_WIDTH / 2, app.V_HEIGHT / 2, Align.center);
        saveWindowImage.setTouchable(Touchable.disabled);
        saveWindowImage.setColor(Color.CLEAR);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 90;
        BitmapFont newfont = generator.generateFont(parameter);
        generator.dispose();

        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.fontColor = Color.BLACK;
        style.font = newfont;
        textfield = new TextField("My new beat", style);
        textfield.setTouchable(Touchable.disabled);
        textfield.setSize(900, 300);
        textfield.setPosition(200, 1500);
        textfield.setColor(Color.CLEAR);

        textfield.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                Input.TextInputListener textListener = new Input.TextInputListener()
                {
                    @Override
                    public void input(String input)
                    {
                        System.out.println(input);
                        textfield.setText(input);
                    }

                    @Override
                    public void canceled()
                    {
                        System.out.println("Aborted");
                    }
                };

                Gdx.input.getTextInput(textListener, "Name of new beat ", "My new beat", "");
                return true;
            }
        });

        cancelIndicator = new Image(new Texture("cancel.png"));
        cancelIndicator.setPosition(300, 1250);
        cancelIndicator.setTouchable(Touchable.disabled);
        cancelIndicator.setColor(Color.CLEAR);
        cancelIndicator.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                saveWindowImage.addAction(Actions.color(Color.CLEAR, 0.5f, Interpolation.exp5));
                cancelIndicator.addAction(Actions.color(Color.CLEAR, 0.5f, Interpolation.exp5));
                confirmIndicator.addAction(Actions.color(Color.CLEAR, 0.5f, Interpolation.exp5));
                textfield.setTouchable(Touchable.disabled);
                textfield.addAction(Actions.color(Color.CLEAR, 0.5f, Interpolation.exp5));
                saveWindowImage.setTouchable(Touchable.disabled);
                cancelIndicator.setTouchable(Touchable.disabled);
                confirmIndicator.setTouchable(Touchable.disabled);
                return true;
            }
        });

        confirmIndicator = new Image(new Texture("Confirm.png"));
        confirmIndicator.setPosition(920, 1250);
        confirmIndicator.setTouchable(Touchable.disabled);
        confirmIndicator.setColor(Color.CLEAR);
        confirmIndicator.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                System.out.println("Clicked confirm");
                saveWindowImage.addAction(Actions.color(Color.CLEAR, 0.5f, Interpolation.exp5));
                cancelIndicator.addAction(Actions.color(Color.CLEAR, 0.5f, Interpolation.exp5));
                confirmIndicator.addAction(Actions.color(Color.CLEAR, 0.5f, Interpolation.exp5));
                textfield.setTouchable(Touchable.disabled);
                textfield.addAction(sequence(Actions.color(Color.CLEAR, 0.5f, Interpolation.exp5), run(new Runnable() {
                    public void run () {
                        textfield.setText("My new Beat");
                    }
                })));
                saveWindowImage.setTouchable(Touchable.disabled);
                cancelIndicator.setTouchable(Touchable.disabled);
                confirmIndicator.setTouchable(Touchable.disabled);
                showSavedSongs();
                return true;
            }
        });

        saveIcon = new Image(new Texture("save.png"));
        saveIcon.setPosition(1300, 2470);
        saveIcon.setColor(Color.CLEAR);
        saveIcon.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                System.out.println("Clicked save");
                saveWindowImage.addAction(Actions.color(Color.WHITE, 0.5f, Interpolation.exp5));
                cancelIndicator.addAction(Actions.color(Color.WHITE, 0.5f, Interpolation.exp5));
                confirmIndicator.addAction(Actions.color(Color.WHITE, 0.5f, Interpolation.exp5));
                textfield.addAction(Actions.color(Color.WHITE, 0.5f, Interpolation.exp5));
                saveWindowImage.setTouchable(Touchable.enabled);
                cancelIndicator.setTouchable(Touchable.enabled);
                confirmIndicator.setTouchable(Touchable.enabled);
                textfield.setTouchable(Touchable.enabled);

                return true;
            }
        });

        // beatPropertyImage = new Image(new Texture("Group 208.png"));
        beatPropertyImage = new Image(new Texture("Group 208.png"));
        beatPropertyImage.setPosition(-1500, 0);
        beatPropertyImage.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                if (isBeatPropertyOpen && y > 2400) {
                    beatPropertyImage.addAction(Actions.moveTo(-1500, 0, 0.7f, Interpolation.exp5Out));
                    propertyButtons.addAction(Actions.moveBy(-1500, 0, 0.7f, Interpolation.exp5Out));
                    isBeatPropertyOpen = false;
                    return true;
                }
                return false;
            }
        });

        beatExtendedImage = new Image(new Texture("Expanded Beats.png"));
        beatExtendedImage.setPosition(-1500, 0);
        beatExtendedImage.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                if ((y > 2250 && y < 2450 )|| (x > 72 && x < 200 && y < 200 && y > 0)) {
                    beatExtendedImage.addAction(Actions.moveTo(-1500, 0, 0.7f, Interpolation.exp5Out));
                    return true;
                }
                return false;
            }
        });

        // beatPropertyImage = new Image(new Texture("Group 208.png"));
        beatListImage1 = new Image(new Texture("Rbeats1.png"));
        beatListImage1.setPosition(-1500, 2668 , Align.topLeft);
        beatListImage1.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                System.out.println("touched in left" + x + " " + y);
                if (y > 3400) {
                    beatListImage1.addAction(Actions.moveBy(-1500, 0, 0.7f, Interpolation.exp5Out));
                    return true;
                } else if (y < 3400 && y > 2850) {
                    beatListImage2.setColor(Color.CLEAR);
                    beatListImage2.addAction(sequence(Actions.moveBy(1500, 0), Actions.color(Color.WHITE, 0.7f, Interpolation.exp10)));
                    return true;
                }
                return false;
            }
        });

        beatListImage2 = new Image(new Texture("Rbeats2.png"));
        beatListImage2.setPosition(-1500, 2668, Align.topLeft);
        beatListImage2.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                System.out.println("touched in left" + x + " " + y);
                if (y > 3800) {
                    beatListImage1.addAction(Actions.moveBy(-1500, 0, 0.5f, Interpolation.exp5Out));
                    beatListImage2.addAction(Actions.moveBy(-1500, 0, 0.5f, Interpolation.exp5Out));
                    return true;
                } else if (y > 2800) {
                    beatListImage2.addAction(sequence(Actions.fadeOut(0.5f, Interpolation.exp5Out), Actions.moveBy(-1500,0)));
                    return true;
                }
                return false;
            }
        });

//        for (int i = 0; i < 20 ; i++) {
//            Actor actor =
//            (326 + i * 75 + x_offset) * Gdx.graphics.getWidth() / app.V_WIDTH,
//                    (550 + y_offset) * Gdx.graphics.getWidth() / app.V_WIDTH,
//                    67 * Gdx.graphics.getWidth() / app.V_WIDTH,
//                    170 * Gdx.graphics.getHeight() /app.V_HEIGHT);
//        }


        blackImage = new Image(new Texture("black.png"));
        blackImage.setScale(1500,2688);
        blackImage.setPosition(0,0, Align.bottomLeft);
        blackImage.addAction(sequence(Actions.color(Color.CLEAR, 2.0f, Interpolation.exp10), run(new Runnable() {
            @Override
            public void run() {
                blackImage.remove();
            }
        })));


        Image bpm1 = new Image(new Texture("buttons/Group 209.png"));
        bpm1.setPosition(220, 2668-805);

        Image bpm2 = new Image(new Texture("buttons/Group 210.png"));
        bpm2.setPosition(602, 2668-805);
        Image bpm3 = new Image(new Texture("buttons/Group 211.png"));
        bpm3.setPosition(984, 2668-805);
        Image bpm4 = new Image(new Texture("buttons/Group 212.png"));
        bpm4.setPosition(220, 2668-1140);
        Image bpm5 = new Image(new Texture("buttons/Group 213.png"));
        bpm5.setPosition(602, 2668-1140);
        Image bpm6 = new Image(new Texture("buttons/Group 214.png"));
        bpm6.setPosition(984, 2668-1140);
        Image swing = new Image(new Texture("buttons/Group 216.png"));
        swing.setPosition(410, 2668-1614);
        Image straight = new Image(new Texture("buttons/Group 215.png"));
        // straight.setColor(Color.GRAY);
        straight.setPosition(790, 2668-1614);
        Image hpm4 = new Image(new Texture("buttons/Group 217.png"));
        hpm4.setPosition(236, 2688 - 2042);
        // hpm4.setColor(Color.GRAY);
        Image hpm5 = new Image(new Texture("buttons/Group 218.png"));
        hpm5.setPosition(503, 2688 - 2042);
        Image hpm6 = new Image(new Texture("buttons/Group 220.png"));
        hpm6.setPosition(772, 2688 - 2042);
        Image hpm7 = new Image(new Texture("buttons/Group 221.png"));
        hpm7.setPosition(1045, 2688 - 2042);

        propertyButtons = new Group();
        propertyBPMButtons = new Group();
        propertyBeatButtons = new Group();
        propertyHPMButtons = new Group();

        propertyBPMButtons.addActor(bpm1);
        propertyBPMButtons.addActor(bpm2);
        propertyBPMButtons.addActor(bpm3);
        propertyBPMButtons.addActor(bpm4);
        propertyBPMButtons.addActor(bpm5);
        propertyBPMButtons.addActor(bpm6);
        propertyBeatButtons.addActor(straight);
        propertyBeatButtons.addActor(swing);
        propertyHPMButtons.addActor(hpm4);
        propertyHPMButtons.addActor(hpm5);
        propertyHPMButtons.addActor(hpm6);
        propertyHPMButtons.addActor(hpm7);

        propertyButtons.addActor(propertyBPMButtons);
        propertyButtons.addActor(propertyBeatButtons);
        propertyButtons.addActor(propertyHPMButtons);

        propertyButtons.moveBy(-1500,0);

        for (final Actor a : propertyBPMButtons.getChildren()) {
            a.setColor(Color.GRAY);
            a.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y,
                                         int pointer, int button) {
                    System.out.println("touched in left" + x + " " + y);
                    for (final Actor b : propertyBPMButtons.getChildren()) {
                        b.setColor(Color.GRAY);
                    }
                    a.setColor(Color.WHITE);
                    return true;
                }
            });
        }

        for (final Actor a : propertyBeatButtons.getChildren()) {
            a.setColor(Color.GRAY);
            a.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y,
                                         int pointer, int button) {
                    System.out.println("touched in left" + x + " " + y);
                    for (final Actor b : propertyBeatButtons.getChildren()) {
                        b.setColor(Color.GRAY);
                    }
                    a.setColor(Color.WHITE);
                    return true;
                }
            });
        }

        for (final Actor a : propertyHPMButtons.getChildren()) {
            a.setColor(Color.GRAY);
            a.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y,
                                         int pointer, int button) {
                    System.out.println("touched in left" + x + " " + y);
                    for (final Actor b : propertyHPMButtons.getChildren()) {
                        b.setColor(Color.GRAY);
                    }
                    a.setColor(Color.WHITE);
                    return true;
                }
            });
        }

        bpm1.setColor(Color.WHITE);
        straight.setColor(Color.WHITE);
        hpm4.setColor(Color.WHITE);

        stage.addActor(img);
        stage.addActor(settingsGearImage);
        stage.addActor(infoIcon);
        stage.addActor(saveIcon);
        stage.addActor(drums);
        stage.addActor(bottomPanel);
        stage.addActor(noteGroup);
        stage.addActor(cursorImage);

        stage.addActor(saveWindowImage);
        stage.addActor(textfield);
        stage.addActor(cancelIndicator);
        stage.addActor(confirmIndicator);
        stage.addActor(settingsImage);
        stage.addActor(beatExtendedImage);
        stage.addActor(beatPropertyImage);
        stage.addActor(propertyButtons);
        stage.addActor(beatListImage1);
        stage.addActor(beatListImage2);
        stage.addActor(blackImage);

        Gdx.input.setInputProcessor(stage);
    }

    public void openTaikoSource() {
        Gdx.net.openURI("http://taikosource.com/song-database/");
    }

    void showSavedSongs() {
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
//        shapeRenderer.setProjectionMatrix(app.batch.getProjectionMatrix());
//        shapeRenderer.setTransformMatrix(app.batch.getTransformMatrix());
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        for (int i = 0; i < notes.size(); i++) {
//            if (i == cursor)
//                shapeRenderer.setColor(Color.WHITE);
//            else if (notes.get(i) == NoteType.NONE.ordinal()) {
//                shapeRenderer.setColor(Color.GRAY);
//            }
//            shapeRenderer.rect(
//                    (326 + i * 75 + x_offset) * Gdx.graphics.getWidth() / app.V_WIDTH,
//                    (550 + y_offset) * Gdx.graphics.getWidth() / app.V_WIDTH,
//                    67 * Gdx.graphics.getWidth() / app.V_WIDTH,
//                    170 * Gdx.graphics.getHeight() /app.V_HEIGHT);
//        }
//        shapeRenderer.end();

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

    public void recordAt(int n, int type) {
        Image note = noteGroup.findActor(Integer.toString(n));
        notes.set(n - 1, type);
        switch(type) {
            case 0 :
                note.setColor(Color.WHITE);
                break; // optional

            case 1 :
                note.setColor(Color.RED);
                // Statements
                break; // optional

            case 2 :
                note.setColor(Color.RED);
                // Statements
                break; // optional

            case 3 :
                note.setColor(Color.BLUE);
                // Statements
                break; // optional

            case 4 :
                note.setColor(Color.BLUE);
                // Statements
                break; // optional

            default : // Optional
                // Statements
        }
    }

    public void loadSong() {

    }

    public void playNotes()  {
        isMusicPlaying = true;
        if (cursor < 1) {
            cursor = 1;
            cursorImage.addAction(Actions.color(Color.WHITE, 0.1f, Interpolation.exp10));
            cursorImage.addAction(moveTo(326, cursorImage.getY()));
        }

        countdown = 0;
        System.out.println("lets go!");

        playTimer = new Timer();
        playTimer.scheduleAtFixedRate(new CountDown(), 0, configSpeedMilli);
    }

    public void stopNotes() {
        isMusicPlaying = false;
        playTimer.cancel();
        playTimer.purge();
    }

    class CountDown extends TimerTask {
        boolean firstRun = true;

        public void run() {

            if (countdown < configHPM) {
                soundWoodblock.play();
                countdown++;
            } else {
                switch (notes.get(cursor - 1)) {
                    case 0 :
                        break; // optional

                    case 1 :
                        soundIRW.play();
                        // Statements
                        break; // optional

                    case 2 :
                        soundILW.play();
                        // Statements
                        break; // optional

                    case 3 :
                        soundORW.play();
                        // Statements
                        break; // optional

                    case 4 :
                        soundOLW.play();
                        // Statements
                        break; // optional
                }
                cursor++;
                if(!firstRun) {
                    cursorImage.addAction(Actions.moveBy(72, 0, 0.1f, Interpolation.exp10));
                }
                firstRun = false;
            }

            if (cursor >= notes.size()) {
                stopNotes();
            }
        }
    }




}
//