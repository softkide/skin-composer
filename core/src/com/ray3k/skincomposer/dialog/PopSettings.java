package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.RootTable;
import com.ray3k.skincomposer.utils.Utils;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.Spinner;
import com.ray3k.stripe.Spinner.Orientation;

import static com.ray3k.skincomposer.Main.*;

public class PopSettings extends PopTable {
    private Integer maxUndos;
    private boolean resourcesRelative;
    private boolean allowingWelcome;
    private boolean exportWarnings;
    private boolean allowingUpdates;
    
    public PopSettings() {
        super(skin, "dialog");
    
        setKeepCenteredInWindow(true);
        setModal(true);
        setHideOnUnfocus(true);
        
        maxUndos = projectData.getMaxUndos();
        resourcesRelative = projectData.areResourcesRelative();
        allowingWelcome = projectData.isAllowingWelcome();
        exportWarnings = projectData.isShowingExportWarnings();
        allowingUpdates = projectData.isCheckingForUpdates();
        
        populate();
    }
    
    @Override
    public void show(Stage stage, Action action) {
        fire(new DialogEvent(DialogEvent.Type.OPEN));
        super.show(stage, action);
    }
    
    @Override
    public boolean remove() {
        fire(new DialogEvent(DialogEvent.Type.CLOSE));
        return super.remove();
    }
    
    public void populate() {
        pad(10);
        
        defaults().space(15);
        var label = new Label("Settings", skin, "title");
        add(label);
        
        row();
        var table = new Table();
        add(table);
        
        table.defaults().growX().space(5);
        var textButton = new TextButton("Open temp/log directory", skin);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                try {
                    Utils.openFileExplorer(Main.appFolder.child("temp/"));
                } catch (Exception e) {
                    Gdx.app.error(getClass().getName(), "Error opening temp folder", e);
                    dialogFactory.showDialogError("Folder Error...", "Error opening temp folder.\n\nOpen log?");
                }
            }
        });
        textButton.addListener(handListener);
        table.add(textButton);
        
        table.row();
        textButton = new TextButton("Open preferences directory", skin);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                try {
                    Utils.openFileExplorer(Gdx.files.external(".prefs/"));
                } catch (Exception e) {
                    Gdx.app.error(getClass().getName(), "Error opening preferences folder", e);
                    dialogFactory.showDialogError("Folder Error...", "Error opening preferences folder.\n\nOpen log?");
                }
            }
        });
        textButton.addListener(handListener);
        table.add(textButton);
        
        if (projectData.areChangesSaved() && projectData.getSaveFile().exists()) {
            table.row();
            textButton = new TextButton("Open project/import directory", skin);
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event,
                                    Actor actor) {
                    try {
                        Utils.openFileExplorer(projectData.getSaveFile().sibling(projectData.getSaveFile().nameWithoutExtension() + "_data"));
                    } catch (Exception e) {
                        Gdx.app.error(getClass().getName(), "Error opening project folder", e);
                        dialogFactory.showDialogError("Folder Error...", "Error opening project folder\n\nOpen log?");
                    }
                }
            });
            textButton.addListener(handListener);
            table.add(textButton);
        }
        
        table.row();
        textButton = new TextButton("Open texture packer settings file for export", skin);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                try {
                    Utils.openFileExplorer(Main.appFolder.child("texturepacker/atlas-export-settings.json"));
                } catch (Exception e) {
                    Gdx.app.error(getClass().getName(), "Error opening atlas-export-settings.json", e);
                    dialogFactory.showDialogError("File Error...", "Error opening atlas-export-settings.json\n\nOpen log?");
                }
            }
        });
        textButton.addListener(handListener);
        table.add(textButton);
        
        table.row();
        textButton = new TextButton("Open texture packer settings file for preview", skin);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                try {
                    Utils.openFileExplorer(Main.appFolder.child("texturepacker/atlas-internal-settings.json"));
                } catch (Exception e) {
                    Gdx.app.error(getClass().getName(), "Error opening atlas-internal-settings.json", e);
                    dialogFactory.showDialogError("File Error...", "Error opening atlas-internal-settings.json\n\nOpen log?");
                }
            }
        });
        textButton.addListener(handListener);
        table.add(textButton);
        
        row();
        table = new Table();
        add(table);
        
        table.defaults().space(5);
        label = new Label("Max Number of Undos: ", skin);
        table.add(label);
        
        var spinner = new Spinner(projectData.getMaxUndos(), 1.0, true, Orientation.HORIZONTAL, getSkin());
        spinner.setMinimum(1.0);
        spinner.setMaximum(100.0);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                maxUndos = (int) spinner.getValue();
            }
        });
        spinner.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event,
                                             Actor actor, boolean focused) {
                maxUndos = (int) spinner.getValue();
            }
            
        });
        spinner.getTextField().addListener(ibeamListener);
        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        table.add(spinner).minWidth(100.0f);
        
        row();
        table = new Table();
        add(table);
        
        table.defaults().expandX().left().space(5);
        var relativeCheckBox = new ImageTextButton("Keep resources relative?", getSkin(), "checkbox");
        relativeCheckBox.setChecked(resourcesRelative);
        relativeCheckBox.addListener(handListener);
        relativeCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                resourcesRelative = relativeCheckBox.isChecked();
            }
        });
        table.add(relativeCheckBox);
        
        table.row();
        var welcomeCheckBox = new ImageTextButton("Show welcome screen?", getSkin(), "checkbox");
        welcomeCheckBox.setChecked(allowingWelcome);
        welcomeCheckBox.addListener(handListener);
        welcomeCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                allowingWelcome = welcomeCheckBox.isChecked();
            }
        });
        table.add(welcomeCheckBox);
        
        table.row();
        var exportWarningsCheckBox = new ImageTextButton("Show export warnings?", getSkin(), "checkbox");
        exportWarningsCheckBox.setChecked(exportWarnings);
        exportWarningsCheckBox.addListener(handListener);
        exportWarningsCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                exportWarnings = exportWarningsCheckBox.isChecked();
            }
        });
        table.add(exportWarningsCheckBox);
        
        table.row();
        var updatesCheckBox = new ImageTextButton("Check for updates?", getSkin(), "checkbox");
        updatesCheckBox.setChecked(allowingUpdates);
        updatesCheckBox.addListener(handListener);
        updatesCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                allowingUpdates = updatesCheckBox.isChecked();
            }
        });
        table.add(updatesCheckBox);
        
        row();
        var buttonTable = new Table();
        buttonTable.pad(5);
        add(buttonTable);
        
        buttonTable.defaults().minWidth(75).space(5);
        textButton = new TextButton("OK", getSkin());
        textButton.addListener(handListener);
        buttonTable.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                projectData.setChangesSaved(false);
                projectData.setMaxUndos(maxUndos);
                projectData.setResourcesRelative(resourcesRelative);
                projectData.setAllowingWelcome(allowingWelcome);
                projectData.setShowingExportWarnings(exportWarnings);
                projectData.setCheckingForUpdates(allowingUpdates);
                undoableManager.clearUndoables();
    
                if (allowingUpdates) {
                    Main.checkForUpdates(main);
                } else {
                    Main.newVersion = Main.VERSION;
                    rootTable.fire(new RootTable.RootTableEvent(RootTable.RootTableEnum.CHECK_FOR_UPDATES_COMPLETE));
                }
                
                hide();
            }
        });
        
        textButton = new TextButton("CANCEL", getSkin());
        textButton.addListener(handListener);
        buttonTable.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide();
            }
        });
    
        key(Keys.ESCAPE,() -> {
            hide();
        });
    }
}