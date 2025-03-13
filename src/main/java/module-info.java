module com.example.gallerymanagement {
    requires javafx.fxml;
    requires jpro.webapi;
    requires javafx.controls;
    requires javafx.graphics;


    opens com.example.gallerymanagement to javafx.fxml;
    exports com.example.gallerymanagement;
}