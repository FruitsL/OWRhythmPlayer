package com.fruitcoding.owrhythmplayer.controller.component;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.regex.Pattern;

public class NumericTextField extends TextField {
    public NumericTextField() {
        super();

        this.setTextFormatter(new TextFormatter<>(change -> Pattern.matches("\\D+", change.getText()) ? null : change));
        this.textProperty().addListener((_, _, newVal) -> this.setText(newVal.replaceFirst("^0+(?!$)", "")));
    }
}
