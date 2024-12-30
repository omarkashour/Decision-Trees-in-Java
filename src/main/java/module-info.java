module com.example.mlproj1 {
    requires javafx.fxml;
    requires atlantafx.base;


    opens com.example.mlproj1 to javafx.fxml;
    exports com.example.mlproj1;
}