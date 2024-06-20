module com.fruitcoding.owrhythmplayer {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires static lombok;
    requires jave;
    requires org.apache.logging.log4j;

    opens com.fruitcoding.owrhythmplayer to javafx.fxml;
    exports com.fruitcoding.owrhythmplayer;
    exports com.fruitcoding.owrhythmplayer.audio;
}