module teamturingbennett {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    opens teamturingbennett to javafx.fxml;
    exports teamturingbennett;
}
