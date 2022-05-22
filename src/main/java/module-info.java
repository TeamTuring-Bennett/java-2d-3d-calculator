module teamturingbennett {
    requires javafx.controls;
    requires javafx.fxml;

    opens teamturingbennett to javafx.fxml;
    exports teamturingbennett;
}
