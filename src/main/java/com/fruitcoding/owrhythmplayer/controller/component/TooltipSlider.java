package com.fruitcoding.owrhythmplayer.controller.component;

import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import lombok.Getter;

public class TooltipSlider extends Slider {
    private final Tooltip tooltip;

    public TooltipSlider() {
        tooltip = new Tooltip();
        tooltip.setAutoHide(false);

        this.valueProperty().addListener(((observableValue, number, t1) -> {
            updateTooltipPosition();
            tooltip.setText(String.valueOf(t1.intValue()));
        }));

        this.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> updateTooltipPosition());
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> updateTooltipPosition());
        this.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> tooltip.hide());
    }

    private void updateTooltipPosition() {
        javafx.scene.Node thumb = this.lookup(".thumb");
        if (thumb != null) {
            double x = thumb.localToScreen(thumb.getBoundsInLocal()).getMinX();
            double y = thumb.localToScreen(thumb.getBoundsInLocal()).getMinY() - 30;
            tooltip.show(thumb, x, y);
        }
    }
}
